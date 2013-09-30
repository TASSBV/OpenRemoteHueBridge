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

import java.io.Serializable;


/**
 * Defines a object to indicate the uiDesignerLayout if has updated when save the layout.
 * 
 * @author handy.wang
 */
@SuppressWarnings("serial")
public class AutoSaveResponse implements Serializable {

   /** The is updated. */
   private boolean isUpdated;
   
   /**
    * Instantiates a new auto save response.
    */
   public AutoSaveResponse() {
      super();
      this.isUpdated = false;
   }

   /**
    * Checks if is updated.
    * 
    * @return true, if is updated
    */
   public boolean isUpdated() {
      return isUpdated;
   }

   /**
    * Sets the updated.
    * 
    * @param isUpdated the new updated
    */
   public void setUpdated(boolean isUpdated) {
      this.isUpdated = isUpdated;
   }

}
