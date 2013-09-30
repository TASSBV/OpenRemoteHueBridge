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

import com.extjs.gxt.ui.client.data.BeanModelTag;

/**
 * The GroupRef is linked the group and panel.
 */
public class GroupRef extends BusinessEntity implements BeanModelTag {

   private static final long serialVersionUID = -8454086925745873969L;
   
   private Group group;
   private Panel panel;
   
   public GroupRef() {
   }
   public GroupRef(Group group) {
      super();
      group.ref();
      this.group = group;
   }
   public Group getGroup() {
      return group;
   }
   public Panel getPanel() {
      return panel;
   }
   
   /**
    * Sets the group, make the old group release reference, and make the new group increase reference.
    * 
    * @param group the new group
    */
   public void setGroup(Group group) {
      if (this.group != null) {
         this.group.releaseRef();
      }
      group.ref();
      this.group = group;
   }
   public void setPanel(Panel panel) {
      this.panel = panel;
   }
   
   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.domain.BusinessEntity#getDisplayName()
    */
   @Override
   @Transient
   public String getDisplayName() {
      return group.getName();
   }
   
   @Transient
   public long getGroupId() {
      return group.getOid();
   }
}
