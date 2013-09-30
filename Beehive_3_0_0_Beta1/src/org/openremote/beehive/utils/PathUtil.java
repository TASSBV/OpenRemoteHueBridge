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

/**
 * The Utility for file system Path.
 * 
 * @author Dan 2009-5-14
 */
public class PathUtil {

   
   /**
    * Append file separator.
    * 
    * @param src the src
    * 
    * @return the string
    */
   public static String addSlashSuffix(String src) {
      return src.endsWith("/") ? src : src + "/";
   }
   
   /**
    * Removes the slash suffix.
    * 
    * @param src the src
    * 
    * @return the string
    */
   public static String removeSlashSuffix(String src) {
      return src.endsWith("/") ? src.substring(0, src.lastIndexOf("/")) : src ;
   }
   
}
