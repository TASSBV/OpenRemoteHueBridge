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

package org.openremote.modeler.service;

import java.util.List;

import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.domain.DeviceMacroRef;

/**
 * Provides functions to operate {@link DeviceMacroItem}.
 * 
 * @author allen.wei
 */
public interface DeviceMacroItemService {
   
   /**
    * Delete by device command.
    * 
    * @param deviceCommand the device command
    */
   void deleteByDeviceCommand(DeviceCommand deviceCommand);

   /**
    * Delete by device macro.
    * 
    * @param targetDeviceMacro the target device macro
    */
   void deleteByDeviceMacro(DeviceMacro targetDeviceMacro);

   /**
    * Load by device command id.
    * 
    * @param id the id
    * 
    * @return the list< device command ref>
    */
   List<DeviceCommandRef> loadByDeviceCommandId(long id);

   /**
    * Load by device macro id.
    * 
    * @param id the id
    * 
    * @return the list< device macro ref>
    */
   List<DeviceMacroRef> loadByDeviceMacroId(long id);
   
   /**
    * Load by device macro item oid.
    * 
    * @param deviceMacroItemOid the device macro item oid
    * 
    * @return the list< device macro item>
    */
   DeviceMacroItem loadByDeviceMacroItemOid(long deviceMacroItemOid);
}
