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
import org.openremote.modeler.shared.dto.SwitchDTO;
import org.openremote.modeler.shared.dto.SwitchDetailsDTO;
import org.openremote.modeler.shared.dto.SwitchWithInfoDTO;

import com.extjs.gxt.ui.client.data.BeanModel;

/**
 * The proxy is for managing switch.
 */
public class SwitchBeanModelProxy {
   private SwitchBeanModelProxy() {
   }
   
   public static void delete(final BeanModel beanModel, final AsyncSuccessCallback<Void> callback) {
      if (beanModel != null && beanModel.getBean() instanceof SwitchDTO) {
         AsyncServiceFactory.getSwitchRPCServiceAsync().delete(((SwitchDTO) (beanModel.getBean())).getOid(), callback);
      }
   }
   
   public static void loadSwitchDetails(final BeanModel beanModel, final AsyncSuccessCallback<BeanModel> asyncSuccessCallback) {
     AsyncServiceFactory.getSwitchRPCServiceAsync().loadSwitchDetails(((SwitchDTO)beanModel.getBean()).getOid(), new AsyncSuccessCallback<SwitchDetailsDTO>() {
       public void onSuccess(SwitchDetailsDTO result) {
         asyncSuccessCallback.onSuccess(DTOHelper.getBeanModel(result));
       }
     });
   }
   
   public static void loadAllSwitchWithInfosDTO(AsyncSuccessCallback<ArrayList<SwitchWithInfoDTO>> callback) {
     AsyncServiceFactory.getSwitchRPCServiceAsync().loadAllSwitchWithInfosDTO(callback);
   }
   
   public static void updateSwitchWithDTO(final SwitchDetailsDTO switchDTO, AsyncSuccessCallback<Void> callback) {
     AsyncServiceFactory.getSwitchRPCServiceAsync().updateSwitchWithDTO(switchDTO, callback);
   }

   public static void saveNewSwitch(final SwitchDetailsDTO switchDTO, final long deviceId, AsyncSuccessCallback<Void> callback) {
     AsyncServiceFactory.getSwitchRPCServiceAsync().saveNewSwitch(switchDTO, deviceId, callback);
   }
}