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
package org.openremote.beehive;

import java.io.File;

public class Configuration {

   private String workDir;

   private String iconsDir;

   private String svnDir;

   private String lircCrawRegex;
   
   public String getWorkDir() {
      return workDir;
   }

   public String getSyncHistoryDir() {
      return workDir + File.separator + Constant.SYNC_HISTORY;
   }

   public void setWorkDir(String workDir) {
      this.workDir = workDir;
   }

   public String getWorkCopyDir() {
      return workDir + File.separator + Constant.WORK_COPY;
   }

   public String getSvnDir() {
      if (workDir.startsWith("/")) {
         svnDir = "file://" + workDir + File.separator + "svn-repos/lirc/trunk";
      } else {
         svnDir = "file:///" + workDir + File.separator + "svn-repos/lirc/trunk";
      }
      return svnDir;
   }

   public String getIconsDir() {
      return iconsDir;
   }

   public void setIconsDir(String iconsDir) {
      this.iconsDir = iconsDir;
   }

   public String getLircCrawRegex() {
      return lircCrawRegex;
   }

   public void setLircCrawRegex(String lircCrawRegex) {
      this.lircCrawRegex = lircCrawRegex;
   }

   public String getModelerResourcesDir() {
      return workDir + File.separator + "resources";
   }

   public String getTemplateResourcesDir() {
      return workDir + File.separator + "templates";
   }

   
   
}
