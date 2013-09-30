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
import org.openremote.modeler.shared.dto.SliderDTO;
import org.openremote.modeler.shared.dto.SliderDetailsDTO;
import org.openremote.modeler.shared.dto.SliderWithInfoDTO;

import com.extjs.gxt.ui.client.data.BeanModel;

/**
 * The proxy is for managing slider.
 */
public class SliderBeanModelProxy {
  
   private SliderBeanModelProxy() {
   }
   
   public static void delete(final BeanModel beanModel, final AsyncSuccessCallback<Void> callback) {
      if (beanModel != null && beanModel.getBean() instanceof SliderDTO) {
         AsyncServiceFactory.getSliderRPCServiceAsync().delete(((SliderDTO) (beanModel.getBean())).getOid(), callback);
      }
   }
   
   public static void loadSliderDetails(final BeanModel beanModel, final AsyncSuccessCallback<BeanModel> asyncSuccessCallback) {
     AsyncServiceFactory.getSliderRPCServiceAsync().loadSliderDetails(((SliderDTO)beanModel.getBean()).getOid(), new AsyncSuccessCallback<SliderDetailsDTO>() {
      public void onSuccess(SliderDetailsDTO result) {
        asyncSuccessCallback.onSuccess(DTOHelper.getBeanModel(result));
      }
    });
   }
   
   public static void loadAllSliderWithInfosDTO(AsyncSuccessCallback<ArrayList<SliderWithInfoDTO>> callback) {
     AsyncServiceFactory.getSliderRPCServiceAsync().loadAllSliderWithInfosDTO(callback);
   }
   
   public static void updateSliderWithDTO(final SliderDetailsDTO sensor, AsyncSuccessCallback<Void> callback) {
     AsyncServiceFactory.getSliderRPCServiceAsync().updateSliderWithDTO(sensor, callback);
   }

   public static void saveNewSlider(final SliderDetailsDTO sensor, final long deviceId, AsyncSuccessCallback<Void> callback) {
     AsyncServiceFactory.getSliderRPCServiceAsync().saveNewSlider(sensor, deviceId, callback);
   }

}
