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
package org.openremote.modeler.client.event;

import org.openremote.modeler.client.utils.PropertyEditable;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EventType;

/**
 * The Event is fire to edit property
 * @author Javen
 */
public class PropertyEditEvent extends BaseEvent {
   
   public static final EventType PropertyEditEvent = new EventType();
   
   public PropertyEditable propertyEditable = null;
   
   public PropertyEditEvent(PropertyEditable propertyEditable) {
      super(PropertyEditEvent);
      this.propertyEditable = propertyEditable;
   }

   public PropertyEditable getPropertyEditable() {
      return propertyEditable;
   }
}
