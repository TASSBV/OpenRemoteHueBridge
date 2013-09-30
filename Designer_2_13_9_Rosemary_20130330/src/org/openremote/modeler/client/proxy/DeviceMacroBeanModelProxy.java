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
import java.util.List;

import org.openremote.modeler.client.model.TreeFolderBean;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.shared.dto.DTOHelper;
import org.openremote.modeler.shared.dto.MacroDTO;
import org.openremote.modeler.shared.dto.MacroDetailsDTO;

import com.extjs.gxt.ui.client.data.BeanModel;


/**
 * The proxy is for managing deviceMacro.
 */
public class DeviceMacroBeanModelProxy {

   /**
    * Not be instantiated.
    */
   private DeviceMacroBeanModelProxy() {
   }

   /**
    * Load device maro.
    * 
    * @param deviceMacroBeanModel the device macro bean model
    * @param callback the callback
    */
   public static void loadDeviceMaro(BeanModel deviceMacroBeanModel,
         final AsyncSuccessCallback<List<BeanModel>> callback) {
      if (deviceMacroBeanModel == null || deviceMacroBeanModel.getBean() instanceof TreeFolderBean) {
         AsyncServiceFactory.getDeviceMacroServiceAsync().loadAllDTOs(new AsyncSuccessCallback<ArrayList<MacroDTO>>() {
            public void onSuccess(ArrayList<MacroDTO> result) {
               List<BeanModel> beanModels = DTOHelper.createModels(result);
//               BeanModelDataBase.deviceMacroTable.insertAll(beanModels);
               callback.onSuccess(beanModels);
            }
         });
      } else {
        List<BeanModel> beanModels = DTOHelper.createModels(((MacroDTO) deviceMacroBeanModel.getBean()).getItems());
        callback.onSuccess(beanModels);
      }
   }

   /**
    * Delete device macro.
    * 
    * @param deviceMacroBeanModel the device macro bean model
    * @param callback the callback
    */
   public static void deleteDeviceMacro(final BeanModel deviceMacroBeanModel, final AsyncSuccessCallback<Void> callback) {
      if (deviceMacroBeanModel != null && deviceMacroBeanModel.getBean() instanceof MacroDTO) {
        AsyncServiceFactory.getDeviceMacroServiceAsync().deleteDeviceMacro(((MacroDTO)deviceMacroBeanModel.getBean()).getOid(), callback);
      }
   }
   
   public static void loadMacroDetails(MacroDTO macro, final AsyncSuccessCallback<BeanModel> asyncSuccessCallback) {
     AsyncServiceFactory.getDeviceMacroServiceAsync().loadMacroDetails(macro.getOid(), new AsyncSuccessCallback<MacroDetailsDTO>() {
       public void onSuccess(MacroDetailsDTO result) {
         asyncSuccessCallback.onSuccess(DTOHelper.getBeanModel(result));
       }
     });
   }
   
   public static void saveNewMacro(final MacroDetailsDTO macro, final AsyncSuccessCallback<MacroDTO> callback) {
     AsyncServiceFactory.getDeviceMacroServiceAsync().saveNewMacro(macro, callback);
   }
   public static void updateMacroWithDTO(final MacroDetailsDTO macro, final AsyncSuccessCallback<MacroDTO> callback) {
     AsyncServiceFactory.getDeviceMacroServiceAsync().updateMacroWithDTO(macro, callback);
   }

}
