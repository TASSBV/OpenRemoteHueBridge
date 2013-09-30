/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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

package org.openremote.modeler.utils;

/**
 * @author allen.wei, handy.wang
 */
public class StringUtils {
   
   /**
    * Instantiates a new string utils.
    */
   private StringUtils() {
   }
   
   /**
    * File name remove uuid.
    * 
    * @param fileName the file name
    * 
    * @return the string
    */
   public static String fileNameRemoveUUID(String fileName) {
      return fileName.substring(0, fileName.lastIndexOf("_"));
   }

   /**
    * Gets the file ext.
    * 
    * @param fileName the file name
    * 
    * @return the file ext
    */
   public static String getFileExt(String fileName) {
      String[] parts = fileName.split("\\.");
      if (parts.length == 0) {
         return "";
      }
      return parts[parts.length - 1];
   }
   
   public static String escapeXml(String input) {
      if (input == null) {
         return "";
      }
      return input.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
   }
   
   public static String unescapeXml(String input) {
      if (input == null) {
         return input;
      }
      return input.replace("&quot;", "\"").replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");
   }
   
   public static boolean isEmpty(String str) {
      return str == null || "".equals(str.trim());
   }
}
