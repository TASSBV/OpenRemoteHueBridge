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
import java.util.Date;

import org.apache.log4j.Logger;
import org.openremote.beehive.Configuration;
import org.openremote.beehive.Constant;
import org.openremote.beehive.api.service.SVNDelegateService;
import org.openremote.beehive.api.service.SyncHistoryService;
import org.openremote.beehive.api.service.WebscraperService;
import org.openremote.beehive.domain.SyncHistory;
import org.openremote.beehive.domain.Vendor;
import org.openremote.beehive.exception.LIRCrawlerException;
import org.openremote.beehive.exception.SVNException;
import org.openremote.beehive.file.LIRCElement;
import org.openremote.beehive.repo.Actions;
import org.openremote.beehive.repo.DateFormatter;
import org.openremote.beehive.utils.DateUtil;
import org.openremote.beehive.utils.FileUtil;
import org.openremote.beehive.utils.LIRCrawler;
import org.openremote.beehive.utils.StringUtil;

/**
 * @author Tomsky
 * 
 */
public class WebscraperServiceImpl extends BaseAbstractService<Vendor> implements WebscraperService {
   private static Logger logger = Logger.getLogger(WebscraperServiceImpl.class.getName());
   private SVNDelegateService svnDelegateService;
   private SyncHistoryService syncHistoryService;
   private Configuration configuration;

   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   public SVNDelegateService getSvnDelegateService() {
      return svnDelegateService;
   }

   public void setSvnDelegateService(SVNDelegateService svnDelegateService) {
      this.svnDelegateService = svnDelegateService;
   }

   public void setSyncHistoryService(SyncHistoryService syncHistoryService) {
      this.syncHistoryService = syncHistoryService;
   }

   public void scrapeFiles() {
      Date date = new Date();
      SyncHistory syncHistory = new SyncHistory();
      syncHistory.setStartTime(date);
      String logPath = DateUtil.addTimestampToFilename(date, Constant.SYNC_PROGRESS_FILE);
      syncHistory.setLogPath(logPath);
      syncHistory.setType("update");
      syncHistory.setStatus("running");
      syncHistoryService.save(syncHistory);

      String syncFilePath = configuration.getSyncHistoryDir() + File.separator + logPath;
      FileUtil.deleteFileOnExist(new File(syncFilePath));
      try {
         logger.info("Update lirc files from lirc website...");
         crawl(Constant.LIRC_ROOT_URL, syncFilePath);
         logger.info("Update lirc files from lirc website success.");
         FileUtil.writeLineToFile(syncFilePath, DateFormatter.format(date) + " Completed!");
         syncHistoryService.update("success", new Date());
      } catch (SVNException e) {
         logger.error("update occur SVNException!");
         FileUtil.writeLineToFile(syncFilePath, e.getMessage());
         syncHistoryService.update("faild", new Date());
      } catch (LIRCrawlerException e) {
         logger.error("update occur LIRCrawlerException!");
         FileUtil.writeLineToFile(syncFilePath, e.getMessage());
         syncHistoryService.update("faild", new Date());
      }
   }

   private void crawl(String lircUrl, String syncFilePath) {
      for (LIRCElement lirc : LIRCrawler.list(lircUrl)) {
         if (lirc.isModel()) {
            Actions action = svnDelegateService.compareFileByLastModifiedDate(lirc);
            FileUtil.writeLineToFile(syncFilePath, " [" + StringUtil.systemTime() + "]  " + action.getValue() + "  "
                  + lirc.getRelativePath());
            if (!action.equals(Actions.NORMAL)) {
               LIRCrawler.writeModel(lirc);
            }
         } else {
            crawl(lirc.getPath(), syncFilePath);
         }
      }
   }
}
