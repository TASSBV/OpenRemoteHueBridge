/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.beehive.api.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.openremote.beehive.Configuration;
import org.openremote.beehive.Constant;
import org.openremote.beehive.api.service.SVNDelegateService;
import org.openremote.beehive.api.service.SyncHistoryService;
import org.openremote.beehive.domain.SyncHistory;
import org.openremote.beehive.domain.Vendor;
import org.openremote.beehive.exception.SVNException;
import org.openremote.beehive.file.LIRCElement;
import org.openremote.beehive.repo.Actions;
import org.openremote.beehive.repo.ChangeCount;
import org.openremote.beehive.repo.DateFormatter;
import org.openremote.beehive.repo.DiffResult;
import org.openremote.beehive.repo.DifferenceModel;
import org.openremote.beehive.repo.LIRCEntry;
import org.openremote.beehive.repo.LogMessage;
import org.openremote.beehive.repo.SVNClientFactory;
import org.openremote.beehive.repo.SvnCommand;
import org.openremote.beehive.repo.DiffStatus.Element;
import org.openremote.beehive.repo.LogMessage.ChangePath;
import org.openremote.beehive.utils.DateUtil;
import org.openremote.beehive.utils.FileUtil;
import org.openremote.beehive.utils.StringUtil;
import org.openremote.beehive.utils.SvnUtil;
import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.ISVNInfo;
import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.ISVNLogMessageChangePath;
import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * SVNDelegateServiceImpl wrap all svn operations
 * @author Tomsky
 * 
 */
public class SVNDelegateServiceImpl extends BaseAbstractService<Vendor> implements SVNDelegateService {
   private static Logger logger = Logger.getLogger(SVNDelegateServiceImpl.class.getName());
   private Configuration configuration;
   private SyncHistoryService syncHistoryService;
   private ISVNClientAdapter svnClient = SVNClientFactory.getSVNClient();
   private static Map<String, Object> fileLocks = new HashMap<String, Object>();

   public Configuration getConfiguration() {
      return configuration;
   }

   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   public void setSyncHistoryService(SyncHistoryService syncHistoryService) {
      this.syncHistoryService = syncHistoryService;
   }

   /**
    * {@inheritDoc}
    * 
    * @throws SVNException
    */
   public void commit(String[] paths, String message, String username) {
      Date date = new Date();
      SyncHistory syncHistory = new SyncHistory();
      syncHistory.setStartTime(new Date());
      String logPath = DateUtil.addTimestampToFilename(date, Constant.COMMIT_PROGRESS_FILE);
      syncHistory.setLogPath(logPath);
      syncHistory.setType("commit");
      syncHistory.setStatus("running");
      syncHistoryService.save(syncHistory);
      String commitFilePath = configuration.getSyncHistoryDir() + File.separator + logPath;

      svnClient.setUsername(username);
      int totalPath = paths.length;
      FileUtil.deleteFileOnExist(new File(commitFilePath));
      File[] files = new File[totalPath];
      if (totalPath > 0) {
         try {
            for (int i = 0; i < totalPath; i++) {
               String[] arr = paths[i].split("\\|");
               String path = arr[0];
               String status = arr[1];
               files[i] = new File(configuration.getWorkCopyDir() + path);

               if (status != null) {
                  if (status.equals(Actions.UNVERSIONED.toString())) {
                     if (files[i].isDirectory()) {
                        svnClient.addDirectory(files[i], false);
                     } else {
                        svnClient.addFile(files[i]);
                     }
                  }
                  if (status.equals(Actions.MISSING.toString())) {
                     svnClient.update(files[i], SVNRevision.HEAD, true);
                     svnClient.remove(new File[] { files[i] }, true);
                  }

               } else {
                  logger.info("The file of " + files[i] + " is not exist!");
               }
            }
            if (configuration.getWorkCopyDir().startsWith("/")) { // linux
               svnClient.commit(files, message, true);
            } else { // windows
               int mod = 500;
               int commitTimes = totalPath / mod + 1;
               int lastCommitCount = totalPath % mod;
               commitTimes = lastCommitCount > 0 ? commitTimes : commitTimes - 1;
               for (int i = 0; i < commitTimes; i++) {
                  if (i == commitTimes - 1 && lastCommitCount > 0) {
                     File[] subFiles = new File[lastCommitCount];
                     System.arraycopy(files, i * mod, subFiles, 0, lastCommitCount);
                     svnClient.commit(subFiles, message, true);
                  } else {
                     File[] subFiles = new File[mod];
                     System.arraycopy(files, i * mod, subFiles, 0, mod);
                     svnClient.commit(subFiles, message, true);
                  }
               }
            }
            FileUtil.writeLineToFile(commitFilePath, DateFormatter.format(date) + " Completed!");
         } catch (SVNClientException e) {
            logger.error("Commit changes occur exception! maybe you have commit too many changes.", e);
            SVNException ee = new SVNException(
                  "Commit changes occur exception! maybe you have commit too many changes.", e);
            ee.setErrorCode(SVNException.SVN_COMMIT_ERROR);
            throw ee;
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public void copyFromUploadToWC(String srcPath, String destPath) {
      File srcFile = new File(srcPath);
      File destFile = new File(configuration.getWorkCopyDir() + destPath);
      if (!fileLocks.containsKey(destPath)) {
         fileLocks.put(destPath, new Object());
      }
      synchronized (fileLocks.get(destPath)) {
         if (destFile.exists()) {
            destFile.delete();
         }
         FileUtil.copyFile(srcFile, destFile);
         logger.info("Copy file " + srcPath + " to " + destFile.getPath());
      }
   }

   /**
    * {@inheritDoc}
    */
   public DiffResult diff(String path) {
      DiffResult dr = new DiffResult();
      String uuid = UUID.randomUUID().toString();
      File file = new File(configuration.getWorkCopyDir() + path);
      File tempFile = new File(configuration.getWorkCopyDir() + File.separator + uuid);
      if (!file.isDirectory()) {
         try {
            svnClient.diff(file, tempFile, true);
            String strDiff = FileUtils.readFileToString(tempFile, "UTF8");
            tempFile.delete();
            InputStream is = svnClient.getContent(new File(SvnUtil.escapeFileName(file.getAbsolutePath())),
                  SVNRevision.HEAD);
            String left = StringUtil.readInputStreamToStringBuffer(is).toString();
            ISVNStatus[] status = svnClient.getStatus(file, false, true);
            String right = null;
            if (SVNStatusKind.NORMAL.equals(status[0].getTextStatus())) {
               dr.setLeft(null);
               dr.setRight(null);
            } else if (SVNStatusKind.UNVERSIONED.equals(status[0].getTextStatus())) {
               dr.setLeft(null);
               right = FileUtil.readFileToString(file).toString();
               dr.setRight(DifferenceModel.getUntouchedLines(right));
            } else if (SVNStatusKind.DELETED.equals(status[0].getTextStatus())
                  || SVNStatusKind.MISSING.equals(status[0].getTextStatus())) {
               dr.setLeft(DifferenceModel.getUntouchedLines(left));
               dr.setRight(null);
            } else {
               DifferenceModel diff = new DifferenceModel(strDiff);
               right = FileUtil.readFileToString(file).toString();
               dr.setLeft(diff.getLeftLines(left));
               dr.setRight(diff.getRightLines(right));
               ChangeCount changeCount = new ChangeCount(diff.getAddedItemsCount(), diff.getModifiedItemsCount(), diff
                     .getDeletedItemsCount());
               dr.setChangeCount(changeCount);
            }
         } catch (IOException e) {
            logger.error("Read " + tempFile.getName() + " to string occur error!", e);
            SVNException ee = new SVNException("Read " + tempFile.getName() + " to string occur error!", e);
            ee.setErrorCode(SVNException.SVN_IO_ERROR);
            throw ee;
         } catch (SVNClientException e) {
            logger.error("Get difference of " + path + " error!", e);
            SVNException ee = new SVNException("Get difference of " + path + " error!", e);
            ee.setErrorCode(SVNException.SVN_DIFF_ERROR);
            throw ee;
         }
      }
      return dr;

   }

   /**
    * {@inheritDoc}
    */
   public DiffResult diff(String url, long oldRevision, long newRevision) {
      DiffResult dr = new DiffResult();
      String uuid = UUID.randomUUID().toString();
      File tempFile = new File(configuration.getWorkCopyDir() + File.separator + uuid);
      try {
         SVNUrl svnUrl = new SVNUrl(configuration.getSvnDir() + SvnUtil.escapeFileName(url));
         svnClient.diff(new SVNUrl(configuration.getSvnDir() + url), new SVNRevision.Number(oldRevision),
               new SVNRevision.Number(newRevision), tempFile, false);
         String strDiff = FileUtil.readFileToString(tempFile).toString();
         tempFile.delete();
         InputStream leftIS = svnClient.getContent(svnUrl, new SVNRevision.Number(oldRevision));
         String left = StringUtil.readInputStreamToStringBuffer(leftIS).toString();

         InputStream rightIS = svnClient.getContent(svnUrl, new SVNRevision.Number(newRevision));
         String right = StringUtil.readInputStreamToStringBuffer(rightIS).toString();

         DifferenceModel diff = new DifferenceModel(strDiff);
         dr.setLeft(diff.getLeftLines(left));
         dr.setRight(diff.getRightLines(right));
         ChangeCount changeCount = new ChangeCount(diff.getAddedItemsCount(), diff.getModifiedItemsCount(), diff
               .getDeletedItemsCount());
         dr.setChangeCount(changeCount);
      } catch (MalformedURLException e) {
         logger.error("Create svnUrl " + url + " failed!", e);
         SVNException ee = new SVNException("Create svnUrl " + url + " failed!", e);
         ee.setErrorCode(SVNException.SVN_URL_ERROR);
         throw ee;
      } catch (SVNClientException e) {
         logger
               .error("Get difference of " + url + " between version " + oldRevision + " and version " + newRevision, e);
         SVNException ee = new SVNException("Get difference of " + url + " between version " + oldRevision
               + " and version " + newRevision, e);
         ee.setErrorCode(SVNException.SVN_DIFF_ERROR);
         throw ee;
      }
      return dr;
   }

   /**
    * {@inheritDoc}
    */
   public void doExport(String srcUrl, String destPath, int revision, boolean force) {
      try {
         svnClient.doExport(new SVNUrl(configuration.getSvnDir() + SvnUtil.escapeFileName(srcUrl)), new File(destPath),
               new SVNRevision.Number(revision), force);
      } catch (SVNClientException e) {
         logger.error("Error when export form " + srcUrl + " to " + destPath, e);
         SVNException ee = new SVNException("Export " + srcUrl + " occur exception!", e);
         ee.setErrorCode(SVNException.SVN_EXPORT_ERROR);
         throw ee;
      } catch (MalformedURLException e) {
         logger.error("Create svnUrl " + srcUrl + " failed!", e);
         SVNException ee = new SVNException("Create svnUrl " + srcUrl + " failed!", e);
         ee.setErrorCode(SVNException.SVN_URL_ERROR);
         throw ee;
      }
   }

   /**
    * {@inheritDoc}
    */
   public List<LIRCEntry> getList(String url, long revision) {
      List<LIRCEntry> entryList = new ArrayList<LIRCEntry>();

      try {
         long headRevision = getHeadRevision();
         ISVNDirEntry[] list = svnClient.getList(new SVNUrl(configuration.getSvnDir() + SvnUtil.escapeFileName(url)),
               new SVNRevision.Number(revision), false);
         for (ISVNDirEntry dirEntry : list) {
            LIRCEntry entry = new LIRCEntry();
            entry.setPath(dirEntry.getPath());
            entry.setVersion(dirEntry.getLastChangedRevision().getNumber());
            entry.setAuthor(dirEntry.getLastCommitAuthor());
            entry.setDate(dirEntry.getLastChangedDate());
            if (dirEntry.getLastChangedRevision().getNumber() == headRevision) {
               entry.setHeadversion(true);
            }
            entry.setSize(dirEntry.getSize());
            if (dirEntry.getNodeKind().equals(SVNNodeKind.FILE)) {
               entry.setFile(true);
            }
            entryList.add(entry);
         }
      } catch (MalformedURLException e) {
         logger.error("Create svnUrl " + url + " failed!", e);
         SVNException ee = new SVNException("Create svnUrl " + url + " failed!", e);
         ee.setErrorCode(SVNException.SVN_URL_ERROR);
         throw ee;
      } catch (SVNClientException e) {
         logger.error("Get the list files of directory " + url + " occur exception", e);
         SVNException ee = new SVNException("Get the list files of directory " + url + " occur exception", e);
         ee.setErrorCode(SVNException.SVN_GETLIST_ERROR);
         throw ee;
      }
      return entryList;
   }

   /**
    * {@inheritDoc}
    */
   public List<LogMessage> getLogs(String url) {
      List<LogMessage> lms = new ArrayList<LogMessage>();

      try {
         ISVNLogMessage[] logs;
         logs = svnClient.getLogMessages(new SVNUrl(configuration.getSvnDir() + SvnUtil.escapeFileName(url)),
               SVNRevision.HEAD, new SVNRevision.Number(1));
         for (ISVNLogMessage logMessage : logs) {
            LogMessage lm = new LogMessage();
            lm.setRevision(logMessage.getRevision().toString());
            lm.setAuthor(logMessage.getAuthor());
            lm.setDate(logMessage.getDate());
            lm.setComment(logMessage.getMessage());
            for (ISVNLogMessageChangePath change : logMessage.getChangedPaths()) {
               ChangePath cp = lm.new ChangePath(change.getPath(), change.getAction());
               lm.addChangePath(cp);
            }
            lms.add(lm);
         }
      } catch (MalformedURLException e) {
         logger.error("Create svnUrl " + url + " failed!", e);
         SVNException ee = new SVNException("Create svnUrl " + url + " failed!", e);
         ee.setErrorCode(SVNException.SVN_URL_ERROR);
         throw ee;
      } catch (SVNClientException e) {
         logger.error("Get logs of " + url + " occur exception", e);
         SVNException ee = new SVNException("Get logs of " + url + " occur exception", e);
         ee.setErrorCode(SVNException.SVN_GETLOG_ERROR);
         throw ee;
      }
      return lms;
   }

   /**
    * {@inheritDoc}
    */
   public void revert(String path, boolean recurse) {
      File file = new File(configuration.getWorkCopyDir() + path);
      try {
         svnClient.revert(file, recurse);
      } catch (SVNClientException e) {
         logger.error("The file " + path + " can't revert!", e);
         SVNException ee = new SVNException("Revert " + path + " occur exception", e);
         ee.setErrorCode(SVNException.SVN_REVERT_ERROR);
         throw ee;
      }
   }

   /**
    * {@inheritDoc}
    */
   public void rollback(String path, long revision) {
      // svnClient.setUsername(username);
      File file = new File(configuration.getWorkCopyDir() + path);
      try {
         if (file.isFile()) {
            InputStream is = svnClient.getContent(new SVNUrl(configuration.getSvnDir() + SvnUtil.escapeFileName(path)),
                  new SVNRevision.Number(revision));
            FileUtil.createFile(is, file);
         } else {
            revert(path, true);
            deleteFile(file);
            String uuid = UUID.randomUUID().toString();
            File tempFile = new File(configuration.getWorkCopyDir() + File.separator + uuid);

            svnClient.doExport(new SVNUrl(configuration.getSvnDir() + SvnUtil.escapeFileName(path)), tempFile,
                  new SVNRevision.Number(revision), true);
            FileUtils.copyDirectory(tempFile, file);
            FileUtils.deleteDirectory(tempFile);
         }
      } catch (MalformedURLException e) {
         logger.error("Create svnUrl " + path + " failed!", e);
         SVNException ee = new SVNException("Create svnUrl " + path + " failed!", e);
         ee.setErrorCode(SVNException.SVN_URL_ERROR);
         throw ee;
      } catch (SVNClientException e) {
         logger.error("Rollback " + path + " to revision " + revision + " occur svnException", e);
         SVNException ee = new SVNException("Rollback " + path + " to revision " + revision + " occur svnException", e);
         ee.setErrorCode(SVNException.SVN_ROLLBACK_ERROR);
         throw ee;
      } catch (IOException e) {
         logger.error("copy or delete file occur error!", e);
         SVNException ee = new SVNException("copy or delete file occur error!", e);
         ee.setErrorCode(SVNException.SVN_IO_ERROR);
         throw ee;
      }
   }

   /**
    * {@inheritDoc}
    */
   public void deleteFileFromRepo(String filePath, String username) {
      File path = new File(configuration.getWorkCopyDir() + filePath);
      svnClient.setUsername(username);
      try {
         svnClient.revert(path, true);
         if (path.exists()) {
            svnClient.remove(new File[] { path }, true);
         }
      } catch (SVNClientException e) {
         logger.error("Delete file " + filePath + " from svn repository occur exception", e);
         throw new SVNException("Delete file " + filePath + " from svn repository occur exception", e);
      }

   }

   /**
    * {@inheritDoc}
    */
   public void cancelOperation() {
      try {
         svnClient.cancelOperation();
      } catch (SVNClientException e) {
         logger.error("Cancel svn operation fail", e);
         throw new SVNException("Cancel svn operation fail", e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public List<Element> getDiffStatus(String path) {
      return SvnCommand.getStatus(configuration.getWorkCopyDir() + path).getDiffStatus();
   }

   /**
    * This method is used for delete the file or directory in workCopy except the ".svn"
    * 
    */
   private void deleteFile(File path) {
      for (File file : path.listFiles()) {
         if (file.isFile()) {
            file.delete();
         }
         if (file.isDirectory() && !file.getName().equals(".svn")) {
            deleteFile(file);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public boolean isBlankSVN() {
      boolean isBlank = false;
      try {
         SVNUrl svnUrl = new SVNUrl(configuration.getSvnDir());
         if (svnClient.getList(svnUrl, SVNRevision.HEAD, true).length == 0) {
            isBlank = true;
         }
      } catch (MalformedURLException e) {
         logger.error("Create root svnUrl failed!", e);
         SVNException ee = new SVNException("Create root svnUrl failed!", e);
         ee.setErrorCode(SVNException.SVN_URL_ERROR);
         throw ee;
      } catch (SVNClientException e) {
         logger.error("Judge svn repo whether blank, get list error!", e);
         SVNException ee = new SVNException("Judge svn repo whether blank, get list error!", e);
         ee.setErrorCode(SVNException.SVN_GETLIST_ERROR);
         throw ee;

      }
      return isBlank;
   }

   /**
    * {@inheritDoc}
    */
   public long getHeadRevision() {
      ISVNInfo svnInfo = null;
      try {
         svnInfo = svnClient.getInfo(new SVNUrl(configuration.getSvnDir()));
      } catch (MalformedURLException e) {
         logger.error("Create root svnUrl failed!", e);
         SVNException ee = new SVNException("Create root svnUrl failed!", e);
         ee.setErrorCode(SVNException.SVN_URL_ERROR);
         throw ee;
      } catch (SVNClientException e) {
         logger.error("Get head revision failed", e);
         SVNException ee = new SVNException("Get head revision failed", e);
         ee.setErrorCode(SVNException.SVN_GETINFO_ERROR);
         throw ee;
      }
      if (svnInfo != null) {
         return svnInfo.getLastChangedRevision().getNumber();
      }
      return 0;
   }

   /**
    * {@inheritDoc}
    */
   public LogMessage getHeadLog(String path) {
      LogMessage headLog = new LogMessage();
      try {
         ISVNLogMessage[] logs = svnClient.getLogMessages(new SVNUrl(configuration.getSvnDir()
               + SvnUtil.escapeFileName(path)), SVNRevision.HEAD, SVNRevision.HEAD, new SVNRevision.Number(1), true,
               false, 1);
         headLog.setRevision(logs[0].getRevision().toString());
         headLog.setAuthor(logs[0].getAuthor());
         headLog.setDate(logs[0].getDate());
         headLog.setComment(logs[0].getMessage());
      } catch (MalformedURLException e) {
         logger.error("Create svnUrl " + path + " failed!", e);
         SVNException ee = new SVNException("Create svnUrl " + path
               + " failed! Maybe you have not install the svn server.", e);
         ee.setErrorCode(SVNException.SVN_URL_ERROR);
         throw ee;
      } catch (SVNClientException e) {
         logger.error("Get head revision message of " + path, e);
         SVNException ee = new SVNException("Get head revision message of " + path, e);
         ee.setErrorCode(SVNException.SVN_GETLOG_ERROR);
         throw ee;
      }
      return headLog;
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public List<String> getFileContent(String path, long revision) {
      List<String> lines = new ArrayList<String>();
      try {
         SVNRevision svnrevision = SVNRevision.HEAD;
         if (revision != 0) {
            svnrevision = new SVNRevision.Number(revision);
         }
         InputStream is = svnClient.getContent(new File(configuration.getWorkCopyDir() + SvnUtil.escapeFileName(path)),
               svnrevision);
         lines = IOUtils.readLines(is);
      } catch (SVNClientException e) {
         logger.error("Get file content of " + path + " from workCopy failed", e);
         SVNException ee = new SVNException("Get file content of " + path + " from workCopy failed", e);
         ee.setErrorCode(SVNException.SVN_GETCONTENT_ERROR);
         throw ee;
      } catch (IOException e) {
         logger.error("Get content of " + path + " occur error!", e);
         SVNException ee = new SVNException("Get content of " + path + " occur error!", e);
         ee.setErrorCode(SVNException.SVN_IO_ERROR);
         throw ee;
      }
      return lines;
   }

   /**
    * {@inheritDoc}
    */
   public LogMessage getLogByRevision(String path, long revision) {
      LogMessage logMessage = new LogMessage();
      try {
         ISVNLogMessage[] logs = svnClient.getLogMessages(new SVNUrl(configuration.getSvnDir()
               + SvnUtil.escapeFileName(path)), new SVNRevision.Number(revision), new SVNRevision.Number(revision),
               new SVNRevision.Number(1), true, false, 1);
         logMessage.setRevision(logs[0].getRevision().toString());
         logMessage.setAuthor(logs[0].getAuthor());
         logMessage.setDate(logs[0].getDate());
         logMessage.setComment(logs[0].getMessage());
      } catch (MalformedURLException e) {
         logger.error("Create svnUrl " + path + " failed!", e);
         SVNException ee = new SVNException("Create svnUrl " + path + " failed!", e);
         ee.setErrorCode(SVNException.SVN_URL_ERROR);
         throw ee;
      } catch (SVNClientException e) {
         logger.error("Get log message of " + path + " failed", e);
         SVNException ee = new SVNException("Get log message of " + path + " failed", e);
         ee.setErrorCode(SVNException.SVN_GETLOG_ERROR);
         throw ee;
      }
      return logMessage;
   }

   /**
    * {@inheritDoc}
    */
   public Actions compareFileByLastModifiedDate(LIRCElement lirc) {
      Actions action = Actions.NORMAL;
      File workFile = new File(StringUtil.appendFileSeparator(configuration.getWorkCopyDir()) + lirc.getRelativePath());
      if (workFile.exists()) {
         try {
            Date uploadDate = lirc.getUploadDate();
            boolean underSVN = false;
            for (String fileName : workFile.getParentFile().list()) {
               if (".svn".equals(fileName)) {
                  underSVN = true;
                  break;
               }
            }
            if (underSVN) {
               ISVNInfo svnInfo = svnClient.getInfoFromWorkingCopy(new File(SvnUtil.escapeFileName(workFile
                     .getAbsolutePath())));
               if (svnInfo.getLastChangedDate() != null && uploadDate.compareTo(svnInfo.getLastChangedDate()) > 0) {
                  action = Actions.MODIFIED;
               }
            } else if (uploadDate.compareTo(new Date(workFile.lastModified())) > 0) {
               action = Actions.MODIFIED;
            }
         } catch (SVNClientException e) {
            logger.error("Get file " + workFile.getName()
                  + " info error, this may occur by the fileName not case sensitive!", e);
            SVNException ee = new SVNException("Get file " + workFile.getName()
                  + " info error, this may occur by the fileName not case sensitive!", e);
            ee.setErrorCode(SVNException.SVN_GETINFO_ERROR);
            throw ee;
         }
      } else {
         action = Actions.ADDED;
      }
      return action;
   }

   /**
    * {@inheritDoc}
    */
   public String[] getDiffPaths(String path) {
      List<Element> ds = getDiffStatus(path);
      String[] paths = new String[ds.size()];
      Element element;
      for (int i = 0; i < paths.length; i++) {
         element = ds.get(i);
         paths[i] = element.getPath() + "|" + element.getStatus().getValue();
      }
      return paths;
   }
}
