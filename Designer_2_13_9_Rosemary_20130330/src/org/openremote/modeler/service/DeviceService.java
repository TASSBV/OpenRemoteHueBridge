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

import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Device;

/**
 * The Interface DeviceService.
 */
public interface DeviceService {
   
   /**
    * Save device.
    * 
    * @param device the device
    * 
    * @return the device
    */
   Device saveDevice(Device device);
   
   /**
    * Update device.
    * 
    * @param device the device
    */
   void updateDevice(Device device);
   
   /**
    * Delete device.
    * 
    * @param id the id
    */
   void deleteDevice(long id);
   
   /**
    * Load by id.
    * 
    * @param id the id
    * 
    * @return the device
    */
   Device loadById(long id);
   

   /**
    * Load all.
    * 
    * @return the list< device>
    */
   List<Device> loadAll();
   
   /**
    * Load all.
    * 
    * @param account the account
    * 
    * @return the list< device>
    */
   List<Device> loadAll(Account account);
   
   List<Device> loadSameDevices(Device device);
}
