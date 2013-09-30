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
package org.openremote.beehive.file;

import java.util.Date;
import java.util.Locale;

import org.openremote.beehive.Constant;
import org.openremote.beehive.utils.DateUtil;

/**
 * The Class LIRCElement.
 * 
 * @author Tomsky
 */
public class LIRCElement {
   private boolean isModel;
   private String path;
   private String uploadDate;
   public LIRCElement() {
      isModel = false;
   }
   public boolean isModel() {
      return isModel;
   }
   public String getPath() {
      return path;
   }

   public void setModel(boolean isModel) {
      this.isModel = isModel;
   }
   public void setPath(String path) {
      this.path = path;
   }
   public void setUploadDate(String uploadDate) {
      this.uploadDate = uploadDate;
   }
   public Date getUploadDate(){
      return DateUtil.String2Date(uploadDate, "dd-MMM-yyyy kk:mm", Locale.ENGLISH);
   }
   public String getRelativePath(){
      return path.replaceAll(Constant.LIRC_ROOT_URL, "");
   }
}
