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
package org.openremote.modeler.client.rpc;

import java.util.ArrayList;

import org.openremote.modeler.shared.dto.SwitchDetailsDTO;
import org.openremote.modeler.shared.dto.SwitchWithInfoDTO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The Interface defines some methods to manage the switch entity.
 */
@RemoteServiceRelativePath("switch.smvc")
public interface SwitchRPCService extends RemoteService {
   
   /**
    * Delete switch by id.
    * 
    * @param id the id
    */
   void delete(long id);
   
   SwitchDetailsDTO loadSwitchDetails(long id);

   ArrayList<SwitchWithInfoDTO> loadAllSwitchWithInfosDTO();

   void updateSwitchWithDTO(SwitchDetailsDTO switchDTO);

   void saveNewSwitch(SwitchDetailsDTO switchDTO, long deviceId);

}
