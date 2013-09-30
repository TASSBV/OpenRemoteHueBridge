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

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.DeviceCommandDetailsDTO;

/**
 * The Interface DeviceCommandService.
 */
public interface DeviceCommandService {
   
   /**
    * Save all.
    * 
    * @param deviceCommands the device commands
    * 
    * @return the list< device command>
    */
   List<DeviceCommand> saveAll(List<DeviceCommand> deviceCommands);
   
   /**
    * Save.
    * 
    * @param deviceCommand the device command
    * 
    * @return the device command
    */
   DeviceCommand save(DeviceCommand deviceCommand);
   
   void updateDeviceCommandWithDTO(DeviceCommandDetailsDTO dto);
   
   /**
    * Load by id.
    * By default, do not load the device eagerly.
    * 
    * @param id the id
    * 
    * @return the device command
    */
   DeviceCommand loadById(long id);
   
   /**
    * Load by id.
    * 
    * @param id the id
    * @param loadDeviceEagerly indicates that the device associated with this command and all its relationships need to be eagerly loaded with the command.
    * 
    * @return the device command
    */
   DeviceCommand loadById(long id, boolean loadDeviceEagerly);

   /**
    * Delete command.
    * 
    * @param id the id
    */
   Boolean deleteCommand(long id);
   
   /**
    * Load by device.
    * 
    * @param id the id
    * 
    * @return the list< device command>
    */
   List<DeviceCommand> loadByDevice(long id);
   
   List<DeviceCommand> loadSameCommands(DeviceCommand deviceCommand);
   
   ArrayList<DeviceCommandDTO> loadCommandsDTOByDevice(long id);
   
}