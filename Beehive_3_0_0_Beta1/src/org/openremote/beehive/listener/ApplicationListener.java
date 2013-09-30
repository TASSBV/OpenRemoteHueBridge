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
package org.openremote.beehive.listener;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openremote.beehive.Configuration;
import org.openremote.beehive.api.service.SyncHistoryService;
import org.openremote.beehive.api.service.impl.SVNDelegateServiceImpl;
import org.openremote.beehive.domain.SyncHistory;
import org.openremote.beehive.repo.SVNClientFactory;
import org.openremote.beehive.spring.SpringContext;
import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * @author Tomsky
 *
 */
public class ApplicationListener implements ServletContextListener {
   private ISVNClientAdapter svnClient = SVNClientFactory.getSVNClient();
   private static Configuration configuration = (Configuration) SpringContext.getInstance().getBean("configuration");
   private static SyncHistoryService syncHistoryService = (SyncHistoryService) SpringContext.getInstance().getBean(
         "syncHistoryService");
   private static Logger logger = Logger.getLogger(SVNDelegateServiceImpl.class.getName());

   /**
    * Create svn repo and work copy if not exist
    */
   @Override
   public void contextInitialized(ServletContextEvent arg0) {
      SyncHistory syncHistory = syncHistoryService.getLatest();
      if (syncHistory != null && "running".equals(syncHistory.getStatus())) {
         syncHistory.setStatus("failed");
         syncHistoryService.save(syncHistory);
      }
      String svnDir = configuration.getSvnDir();
      String fileSvnPath = "file:///";
      if (configuration.getWorkCopyDir().startsWith("/")) {
         fileSvnPath = "file://";
      }
      String svnRepoPath = svnDir.substring(svnDir.indexOf(fileSvnPath) + fileSvnPath.length(), svnDir
            .indexOf("/trunk"));
      File svnRepo = new File(svnRepoPath);
      SVNUrl svnUrl = null;
      File workCopyDir = new File(configuration.getWorkCopyDir());
      svnUrl = checkRepoExists(svnDir, svnRepo, svnUrl, workCopyDir);
      if (svnUrl != null) {
         checkWorkCopyExists(svnUrl, workCopyDir);
      }
   }

   private SVNUrl checkRepoExists(String svnDir, File svnRepo, SVNUrl svnUrl, File workCopyDir) {
      try {
         svnUrl = new SVNUrl(svnDir);
         svnClient.getInfo(svnUrl);
      } catch (MalformedURLException e) {
         logger.error("Create SVNUrl from " + svnDir + " failed!");
      } catch (NullPointerException e) {
         logger.error("Create svn client from " + svnUrl + " failed!");
      } catch (SVNClientException e) {
         try {
            if (svnRepo.exists()) {
               logger.info("Clean svn repository directory " + svnRepo.getPath() + " ......");
               FileUtils.cleanDirectory(svnRepo);
               logger.info("Clean svn repository directory " + svnRepo.getPath() + " success");
            } else {
               svnRepo.mkdirs();
            }
            svnClient.createRepository(svnRepo, ISVNClientAdapter.REPOSITORY_BDB);
            logger.info("Create svn repository " + svnRepo.getPath() + " success.");
            svnClient.mkdir(svnUrl, true, "create /trunk");
            logger.info("Make svn directory " + svnDir + " success!");
            if (workCopyDir.exists()) {
               logger.info("Clean workCopy " + workCopyDir.getPath() + " ......");
               FileUtils.cleanDirectory(workCopyDir);
               logger.info("Clean workCopy " + workCopyDir.getPath() + " success.");
            } else {
               workCopyDir.mkdirs();
               logger.info("Create workCopy directory " + workCopyDir.getPath() + " success.");
            }
            logger.info("Checkout " + svnUrl + " to " + workCopyDir + " ......");
            svnClient.checkout(svnUrl, workCopyDir, SVNRevision.HEAD, true);
            logger.info("Checkout " + svnUrl + " to " + workCopyDir + " success.");
         } catch (SVNClientException e1) {
            logger.error("Unable to create local repository '" + svnDir
                  + "', please install svn server to sync with LIRC.");
            return null;
         } catch (IOException e2) {
            logger.error("Can't clean dir " + svnRepo + " or " + workCopyDir);
         }
      }
      return svnUrl;
   }

   private void checkWorkCopyExists(SVNUrl svnUrl, File workCopyDir) {
      try {
         svnClient.getInfo(workCopyDir);
      } catch (NullPointerException e) {
         logger.error("Create svn client from " + svnUrl + " failed!");
      } catch (SVNClientException e) {
         try {
            if (workCopyDir.exists()) {
               logger.info("Clean workCopy " + workCopyDir.getPath() + " ......");
               FileUtils.cleanDirectory(workCopyDir);
               logger.info("Clean workCopy " + workCopyDir.getPath() + " success.");
            } else {
               workCopyDir.mkdirs();
               logger.info("Create workCopy directory " + workCopyDir.getPath() + " success.");
            }
            logger.info("Checkout " + svnUrl + " to " + workCopyDir + " ......");
            svnClient.checkout(svnUrl, workCopyDir, SVNRevision.HEAD, true);
            logger.info("Checkout " + svnUrl + " to " + workCopyDir + " success.");
         } catch (SVNClientException e1) {
            logger.error("Can't checkout " + svnUrl + " to " + workCopyDir);
         } catch (IOException e1) {
            logger.error("Can't clean dir " + workCopyDir);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void contextDestroyed(ServletContextEvent arg0) {
      // do nothing

   }
}
