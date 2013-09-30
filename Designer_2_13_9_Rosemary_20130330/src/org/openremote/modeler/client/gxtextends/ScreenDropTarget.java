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
package org.openremote.modeler.client.gxtextends;

import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.widget.Component;

/**
 * The Class is for creating grid cell as a dropTarget.
 */
public class ScreenDropTarget extends DropTarget {

   /**
    * Instantiates a new screen drop target.
    * 
    * @param target the target
    */
   public ScreenDropTarget(Component target) {
      super(target);
      // TODO Auto-generated constructor stub
   }

   @Override
   protected void onDragCancelled(DNDEvent event) {
      // TODO Auto-generated method stub
      super.onDragCancelled(event);
   }

   @Override
   protected void onDragDrop(DNDEvent event) {
      super.onDragDrop(event);
   }

   @Override
   protected void onDragEnter(DNDEvent event) {
      // TODO Auto-generated method stub
      super.onDragEnter(event);
   }

   @Override
   protected void onDragLeave(DNDEvent event) {
      // TODO Auto-generated method stub
      super.onDragLeave(event);
   }

   @Override
   protected void onDragMove(DNDEvent event) {
      // TODO Auto-generated method stub
      super.onDragMove(event);
   }
   
   

}
