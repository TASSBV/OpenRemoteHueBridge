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
package org.openremote.modeler.domain;

import com.extjs.gxt.ui.client.data.BeanModelTag;

/**
 * A domain class for describing the category of controller configurations. 
 * @author javen
 *
 */
@SuppressWarnings("serial")
public class ConfigCategory extends BusinessEntity implements BeanModelTag {
   public static final String XML_NODE_NAME = "category";
   public static final String NAME_XML_ATRIBUTE_NAME = "name";
   public static final String DESCRIBTION_NODE_NAME = "description";
   
   
   private String name = "";
   private String description = "";
   
   public ConfigCategory(){};
   public ConfigCategory(String name,String description){
      this.name = name;
      this.description = description;
   }
   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   public String getDescription() {
      return description;
   }
   public void setDescription(String description) {
      this.description = description;
   }
}
