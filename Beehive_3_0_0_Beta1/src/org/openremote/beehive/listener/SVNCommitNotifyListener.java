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

import org.openremote.beehive.Configuration;
import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.api.service.SyncHistoryService;
import org.openremote.beehive.api.service.VendorService;
import org.openremote.beehive.spring.SpringContext;
import org.openremote.beehive.utils.FileUtil;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving SVNCommitNotify events. The class that is interested in processing a
 * SVNCommitNotify event implements this interface, and the object created with that class is registered with a
 * component using the component's <code>addSVNCommitNotifyListener<code> method. When
 * the SVNCommitNotify event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see SVNCommitNotifyEvent
 * @author Dan 2009-5-24
 */
public class SVNCommitNotifyListener implements ISVNNotifyListener {
   
   /** The command. */
   private int command;
   
   /** The vendor service. */
   private static VendorService vendorService = (VendorService) SpringContext.getInstance().getBean("vendorService");;

   /** The model service. */
   private static ModelService modelService = (ModelService) SpringContext.getInstance().getBean("modelService");
   
   /** The sync history service. */
   private static SyncHistoryService syncHistoryService= (SyncHistoryService) SpringContext.getInstance().getBean("syncHistoryService");
   
   /** The configuration. */
   private static Configuration configuration = (Configuration) SpringContext.getInstance().getBean("configuration");
   
   /* (non-Javadoc)
    * @see org.tigris.subversion.svnclientadapter.ISVNNotifyListener#logCommandLine(java.lang.String)
    */
   public void logCommandLine(String msg) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.tigris.subversion.svnclientadapter.ISVNNotifyListener#logCompleted(java.lang.String)
    */
   public void logCompleted(String msg) {
   }

   /* (non-Javadoc)
    * @see org.tigris.subversion.svnclientadapter.ISVNNotifyListener#logError(java.lang.String)
    */
   public void logError(String msg) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.tigris.subversion.svnclientadapter.ISVNNotifyListener#logMessage(java.lang.String)
    */
   public void logMessage(String msg) {
   }

   /* (non-Javadoc)
    * @see org.tigris.subversion.svnclientadapter.ISVNNotifyListener#logRevision(long, java.lang.String)
    */
   public void logRevision(long revision, String msg) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.tigris.subversion.svnclientadapter.ISVNNotifyListener#onNotify(java.io.File, org.tigris.subversion.svnclientadapter.SVNNodeKind)
    */
   public void onNotify(File file, SVNNodeKind kind) {
      String commitFilePath = configuration.getSyncHistoryDir() + File.separator
            + syncHistoryService.getLatestByType("commit").getLogPath();
      if (command == Command.ADD) {
         FileUtil.writeLineToFile(commitFilePath, "Adding        " + FileUtil.relativeWorkcopyPath(file));
      } else if (command == Command.COMMIT) {
         File fixedFile = FileUtil.fixCommitFilePath(file);
         FileUtil.writeLineToFile(commitFilePath, "Committing    " + FileUtil.relativeWorkcopyPath(fixedFile));
         if (fixedFile.isDirectory()) {
            vendorService.syncWith(fixedFile);
         } else if (fixedFile.isFile()) {
            modelService.syncWith(fixedFile);
         }
      }
   }

   /* (non-Javadoc)
    * @see org.tigris.subversion.svnclientadapter.ISVNNotifyListener#setCommand(int)
    */
   public void setCommand(int cmd) {
      this.command = cmd;
   }

   
}
