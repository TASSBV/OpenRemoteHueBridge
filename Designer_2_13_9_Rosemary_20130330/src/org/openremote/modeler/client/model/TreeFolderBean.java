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
package org.openremote.modeler.client.model;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;

/**
 * Indicates a node on tree, and not the leaf node.
 */
public class TreeFolderBean implements BeanModelTag {

   private static final long serialVersionUID = -8480046408027493475L;

   /**
    * Gets the type.
    * 
    * @return the type
    */
   public String getType() {
      return type;
   }

   /**
    * {@inheritDoc}
    */
   public String getDisplayName() {
      return displayName;
   }

   /**
    * Sets the display name.
    * 
    * @param displayName the new display name
    */
   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }

   /**
    * Sets the type.
    * 
    * @param type the new type
    */
   public void setType(String type) {
      this.type = type;
   }

   /** The display name. */
   private String displayName;
   
   /** The type. */
   private String type;

   public BeanModel getBeanModel() {
      BeanModelFactory beanModelFactory = BeanModelLookup.get().getFactory(getClass());
      return beanModelFactory.createModel(this);
   }
   
}
