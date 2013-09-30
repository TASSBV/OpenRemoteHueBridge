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

import javax.persistence.Transient;

import org.openremote.modeler.touchpanel.TouchPanelDefinition;

import com.extjs.gxt.ui.client.data.BeanModelTag;

/**
 * The ScreenRef is linked screenpair and group, it make a screenpair be share in different group.
 */
public class ScreenPairRef extends BusinessEntity implements BeanModelTag {

  private static final long serialVersionUID = 4005175737682443437L;

  private ScreenPair screen;
   
   private Group group;

   /** The touch panel definition is defined as screenpair's canvas. */
   private TouchPanelDefinition touchPanelDefinition;
   
   public ScreenPairRef() {
   }
   
   /**
    * Instantiates a new screen pair ref, increase the screenpair's refCount.
    * 
    * @param screen the screen
    */
   public ScreenPairRef(ScreenPair screen) {
      super();
      screen.ref();
      this.screen = screen;
   }
   
   
   public ScreenPair getScreen() {
      return screen;
   }

   /**
    * Sets the screenpair, decrease the old screenpair's refCount, increase the new one.
    * 
    * @param screen the new screen
    */
   public void setScreen(ScreenPair screen) {
      if (this.screen != null) {
         this.screen.releaseRef();
      }
      screen.ref();
      this.screen = screen;
   }

   
   /**
    * Gets the group.
    * 
    * @return the group
    */
   public Group getGroup() {
      return group;
   }

   /**
    * Sets the group.
    * 
    * @param group the new group
    */
   public void setGroup(Group group) {
      this.group = group;
   }
   
   @Transient
   public TouchPanelDefinition getTouchPanelDefinition() {
      return touchPanelDefinition;
   }
   public void setTouchPanelDefinition(TouchPanelDefinition touchPanelDefinition) {
      this.touchPanelDefinition = touchPanelDefinition;
   }
   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.domain.BusinessEntity#getDisplayName()
    */
   @Override
   @Transient
   public String getDisplayName() {
      return screen.getName();
   }
   
   /**
    * Gets the screenpair id.
    * 
    * @return the screenpair id
    */
   @Transient
   public long getScreenId() {
      return screen.getOid();
   }
}
