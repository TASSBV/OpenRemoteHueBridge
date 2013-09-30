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

/**
 * Enum of charset.
 * 
 * Created by IntelliJ IDEA. User: AllenWei Date: 2008-6-6 Time: 16:30:08
 * 
 */
public enum EnumCharset {

   UTF_8("UTF-8"), ISO_8859_1("ISO-8859-1"), US_ASCII("US-ASCII"), UTF_16("UTF-16"), UTF_16BE("UTF-16BE"), UTF_16LE(
         "UTF-16LE");

   EnumCharset(String str) {
      this.value = str;
   }

   private String value;

   public String getValue() {
      return value;
   }

   @Override
   public String toString() {
      return value;
   }
   
   
}
