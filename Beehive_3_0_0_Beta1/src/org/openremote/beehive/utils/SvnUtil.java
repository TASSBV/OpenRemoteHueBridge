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
package org.openremote.beehive.utils;



public class SvnUtil {

   /**
    * Returns either the (SVN) tagged version or a temporary version number. When the files are tagged by SVN then the
    * '$HeadUrl: $' and '$Revision: $' keywords will be substituted by SVN. In that case this version number should end
    * up in the project.
    * 
    * @param headUrl
    *           The string which was replaced by SVN server within keyword: $HeadURL: $. The keyword's whole body was
    *           included. It should be in one of the following format:
    *           <ol>
    *           <li>https://svn.hostname/repository/path/trunk/src/web/include/version.jsp</li>
    *           <li>https://svn.hostname/repository/path/branches/v2_1/src/web/include/version.jsp</li>
    *           <li>https://svn.hostname/repository/path/tags/v2_1/src/web/include/version.jsp</li>
    *           </ol>
    * @param revision
    *           The string which was replaced by SVN server for keyword: $Revision: $. The tag's whole body was
    *           included.
    * @return If headUrl is a tagged url, it should return tag's name; If headUrl is a branches' url, it should return
    *         branch's name with "Branch: " prefix; otherwise, it should return revision number with "Untagged: " prefix
    */
   public static String getVersionLabel(String headUrl, String revision) {
      String result = "Untagged";
      if (headUrl == null) {
         return result;
      }
      String verStr = "";
      int tagStart = -1;
      if ((tagStart = headUrl.indexOf("tags")) >= 0) {
         int tagsEnd = headUrl.indexOf("/", tagStart + 5);
         if (tagsEnd >= 0) {
            verStr = headUrl.substring(tagStart + 5, tagsEnd);
         }
      } else if ((tagStart = headUrl.indexOf("branches")) >= 0) {
         int tagsEnd = headUrl.indexOf("/", tagStart + 9);
         if (tagsEnd >= 0) {
            verStr = headUrl.substring(tagStart + 9, tagsEnd);
            verStr = "Branch: " + verStr;
         }
      } else if (revision != null) {
         tagStart = revision.indexOf("$Revision:");
         int tagsEnd = revision.indexOf("$", tagStart + 10);
         if (tagsEnd >= 0) {
            verStr = " r" + revision.substring(tagStart + 10, tagsEnd).trim();
         }
      }
      if (verStr.length() != 0) {
         result = verStr.replace('_', '.');
      }
      return result;

   }

   public static String escapeFileName(String filePath) {
      return filePath.contains("@") ? filePath.concat("@") : filePath;
   }

}
