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
package org.openremote.modeler.domain.component;

import org.openremote.modeler.domain.BusinessEntity;

import flexjson.JSON;

/**
 * This domain class represents a action to navigate to.
 * It can navigate to screen, group or some logical targets.
 */
public class Navigate extends BusinessEntity {

   private static final long serialVersionUID = 4180916727141357903L;
   
   private long toScreen = -1L;
   private long toGroup = -1L;
   
   /** Navigate to logical type, which includes to setting, next screen, previous screen, 
    *  back, login and logout. 
    */
   private ToLogicalType toLogical = null;

   public long getToScreen() {
      return toScreen;
   }
   public long getToGroup() {
      return toGroup;
   }
   public void setToScreen(long toScreen) {
      this.toScreen = toScreen;
   }
   public void setToGroup(long toGroup) {
      this.toGroup = toGroup;
   }

   public ToLogicalType getToLogical() {
      return toLogical;
   }
   public void setToLogical(ToLogicalType toLogical) {
      this.toLogical = toLogical;
   }
   public void clearToLogical() {
      this.toLogical = null;
   }
   @JSON(include=false)
   public boolean isSet() {
      if (toGroup != -1) {
         return true;
      } else if (toLogical != null) {
         return true;
      }
      return false;
   }
   
   public static enum ToLogicalType {
      setting, back, login, logout, nextScreen, previousScreen;
   }
   
   public boolean isToLogic() {
      if (toLogical != null) {
         return true;
      }
      return false;
   }
   
   public boolean toGroup() {
      if (toGroup != -1) {
         return true;
      }
      return false;
   }
   
   /**
    * Set navigate to nothing.
    */
   public void clear() {
      this.toLogical = null;
      this.toGroup = -1;
      this.toScreen = -1;
   }
}
