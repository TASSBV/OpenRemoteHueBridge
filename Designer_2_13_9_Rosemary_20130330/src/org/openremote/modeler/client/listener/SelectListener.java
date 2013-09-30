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
package org.openremote.modeler.client.listener;

import org.openremote.modeler.client.event.SelectEvent;

import com.extjs.gxt.ui.client.event.Listener;

/**
 * The listener interface for receiving select events.
 * The class that is interested in processing a select
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addSelectListener</code> method. When
 * the select event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see SelectEvent
 */
public abstract class SelectListener implements Listener<SelectEvent> {

   public void handleEvent(SelectEvent be) {
      if (be.getType() == SelectEvent.SELECT) {
         afterSelect(be);
      }
   }
   
   public abstract void afterSelect(SelectEvent be);
}
