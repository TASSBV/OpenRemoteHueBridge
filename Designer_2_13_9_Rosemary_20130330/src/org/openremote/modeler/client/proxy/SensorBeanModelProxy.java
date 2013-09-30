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
import org.openremote.modeler.shared.dto.DTOHelper;
import org.openremote.modeler.shared.dto.SensorDTO;
import org.openremote.modeler.shared.dto.SensorDetailsDTO;
import org.openremote.modeler.shared.dto.SensorWithInfoDTO;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The proxy is for managing sensor.
 */
public class SensorBeanModelProxy {

   private SensorBeanModelProxy() {
   }
   
   public static void deleteSensor(final BeanModel beanModel, final AsyncCallback<Boolean> callback) {
     if (beanModel != null && beanModel.getBean() instanceof SensorDTO) {
      AsyncServiceFactory.getSensorRPCServiceAsync().deleteSensor(((SensorDTO)beanModel.getBean()).getOid(), callback);
     }
   }
   
   public static void loadSensorDTOsByDeviceId(final long id, final AsyncSuccessCallback<ArrayList<SensorDTO>> callback) {
     AsyncServiceFactory.getSensorRPCServiceAsync().loadSensorDTOsByDeviceId(id, callback);
   }

  public static void loadSensorDetails(final BeanModel beanModel, final AsyncSuccessCallback<BeanModel> asyncSuccessCallback) {
    AsyncServiceFactory.getSensorRPCServiceAsync().loadSensorDetails(((SensorDTO)beanModel.getBean()).getOid(), new AsyncSuccessCallback<SensorDetailsDTO>() {
      public void onSuccess(SensorDetailsDTO result) {
        asyncSuccessCallback.onSuccess(DTOHelper.getBeanModel(result));
      }
    });
  }
  
  public static void loadAllSensorWithInfosDTO(AsyncSuccessCallback<ArrayList<SensorWithInfoDTO>> callback) {
    AsyncServiceFactory.getSensorRPCServiceAsync().loadAllSensorWithInfosDTO(callback);
  }
  
  public static void updateSensorWithDTO(final SensorDetailsDTO sensor, AsyncSuccessCallback<Void> callback) {
    AsyncServiceFactory.getSensorRPCServiceAsync().updateSensorWithDTO(sensor, callback);
  }
  
  public static void saveNewSensor(final SensorDetailsDTO sensor, final long deviceId, AsyncSuccessCallback<Void> callback) {
    AsyncServiceFactory.getSensorRPCServiceAsync().saveNewSensor(sensor, deviceId, callback);
  }

}
