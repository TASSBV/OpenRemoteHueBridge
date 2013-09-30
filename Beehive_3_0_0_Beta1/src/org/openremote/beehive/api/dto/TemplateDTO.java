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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * UI Template in UI Designer.
 * 
 * @author Dan 2010-1-29
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "template")
public class TemplateDTO extends BusinessEntityDTO {

   private String name;
   private String content;
   private String keywords;
   private boolean shared = false;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public String getKeywords() {
      return keywords;
   }

   public void setKeywords(String keywords) {
      this.keywords = keywords;
   }

   public boolean isShared() {
      return shared;
   }

   public void setShared(boolean shared) {
      this.shared = shared;
   }
   
}
