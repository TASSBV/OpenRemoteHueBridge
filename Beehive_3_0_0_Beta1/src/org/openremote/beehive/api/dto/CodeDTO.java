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
package org.openremote.beehive.api.dto;

import org.apache.commons.lang.StringUtils;

/**
 * Infrared Codes (arbitrary name-value pairs). Typically consists of string names mapped to infrared code hex values.
 * 
 * <pre>
 * For example:
 *    play	0x20
 *    plus	0xD0
 *    ffwd	0xE0
 *    rev	0x10
 *    minus	0xB0
 *    menu	0x40
 * </pre>
 * 
 * @author Dan 2009-2-6
 * @author allen.wei 2009-2-18
 */
@SuppressWarnings("serial")
public class CodeDTO extends BusinessEntityDTO {

   private String name;

   private String remoteName;

   private String value;

   private String comment;

   public CodeDTO() {
      value = "";
      remoteName = "";
      comment = "";
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public String getComment() {
      return comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public boolean isBlankComment() {
      return StringUtils.isBlank(getComment());
   }

   public String getRemoteName() {
      return remoteName;
   }

   public void setRemoteName(String remoteName) {
      this.remoteName = remoteName;
   }
}