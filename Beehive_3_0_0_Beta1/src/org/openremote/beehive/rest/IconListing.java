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
package org.openremote.beehive.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.openremote.beehive.api.dto.IconDTO;

/**
 * In order to let rest service to serialize list of icon
 * 
 * @author Tomsky 2009-4-21
 *
 */
@XmlRootElement(name = "icons")
public class IconListing {
   
   private List<IconDTO> icons = new ArrayList<IconDTO>();
   
   public IconListing() {
   }
   
   public IconListing(List<IconDTO> icons) {
      this.icons = icons;
   }
   
   @XmlElement(name = "icon")
   public List<IconDTO> getIcons() {
      return icons;
   }
   
   public void setIcons(List<IconDTO> icons) {
      this.icons = icons;
   }
}
