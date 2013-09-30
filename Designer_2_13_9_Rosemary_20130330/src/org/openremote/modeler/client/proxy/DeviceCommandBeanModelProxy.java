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
package org.openremote.modeler.client.proxy;

import java.util.ArrayList;

import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.DeviceCommandDetailsDTO;

import com.extjs.gxt.ui.client.data.BeanModel;

/**
 * The is for managing deviceCommand.
 */
public class DeviceCommandBeanModelProxy {
   
   /**
    * Not be instantiated.
    */
   private DeviceCommandBeanModelProxy() {
   }
   
   public static void updateDeviceCommandWithDTO(final DeviceCommandDetailsDTO deviceCommand, AsyncSuccessCallback<Void> callback) {
     AsyncServiceFactory.getDeviceCommandServiceAsync().updateDeviceCommandWithDTO(deviceCommand, callback);
   }
   
   public static void saveNewDeviceCommand(final DeviceCommandDetailsDTO deviceCommand, final long deviceId, AsyncSuccessCallback<Void> callback) {
     AsyncServiceFactory.getDeviceCommandServiceAsync().saveNewDeviceCommand(deviceCommand, deviceId, callback);
   }
   
   /**
    * Delete device command.
    * 
    * @param deviceCommnadModel the device commnad model
    * @param callback the callback
    */
   public static void deleteDeviceCommand(BeanModel deviceCommnadModel, final AsyncSuccessCallback<Boolean> callback) {
     if (deviceCommnadModel.getBean() != null && deviceCommnadModel.getBean() instanceof DeviceCommandDTO) {
       AsyncServiceFactory.getDeviceCommandServiceAsync().deleteCommand(((DeviceCommandDTO)deviceCommnadModel.getBean()).getOid(), callback);
     }
   }
   
   public static void loadDeviceCommandsDTOFromDeviceId(final Long deviceId,final AsyncSuccessCallback<ArrayList<DeviceCommandDTO>>callback) {
     AsyncServiceFactory.getDeviceCommandServiceAsync().loadCommandsDTOByDevice(deviceId, callback);
  }
   
}
