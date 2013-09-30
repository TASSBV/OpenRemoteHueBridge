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
package org.openremote.modeler.client.utils;

import org.openremote.modeler.client.widget.propertyform.PropertyForm;
/**
 * The interface indicate a client object can be edit and have properties form.
 * @author Javen
 *
 */
public interface PropertyEditable {
   /**
    * Get a property form to edit a component such as panel, group, screen... 
    * @return A property form. 
    */
   public PropertyForm getPropertiesForm();
   /**
    * Get a title for a property form. 
    * @return
    */
   public String getTitle();
}
