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

import java.util.HashSet;

import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.ConfigCategory;
import org.openremote.modeler.shared.dto.ControllerConfigDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Class is for managing contoller configurations.
 */
public class ControllerConfigBeanModelProxy {
   
   public static void getConfigDTOs(final ConfigCategory category, final AsyncCallback<HashSet<ControllerConfigDTO>> callback) {
     AsyncServiceFactory.getControllerConfigPRCServiceAsync().getConfigDTOsByCategoryForCurrentAccount(
           category.getName(), new AsyncSuccessCallback<HashSet<ControllerConfigDTO>>() {

              @Override
              public void onSuccess(HashSet<ControllerConfigDTO> result) {
                 callback.onSuccess(result);
              }

           });
  }
   
   public static void listAllMissingConfigDTOs(final String categoryName, final AsyncCallback<HashSet<ControllerConfigDTO>> callback) {
        AsyncServiceFactory.getControllerConfigPRCServiceAsync().listAllMissedConfigDTOsByCategoryName(categoryName, new AsyncSuccessCallback<HashSet<ControllerConfigDTO>>() {
          @Override
          public void onSuccess(HashSet<ControllerConfigDTO> result) {
            callback.onSuccess(result);
          }
        });
     }
   
   public static void saveAllConfigDTOs(final HashSet<ControllerConfigDTO> configs, final AsyncCallback<HashSet<ControllerConfigDTO>> callback) {
      AsyncServiceFactory.getControllerConfigPRCServiceAsync().saveAllDTOs(configs, new AsyncSuccessCallback<HashSet<ControllerConfigDTO>>() {
        @Override
        public void onSuccess(HashSet<ControllerConfigDTO> result) {
          callback.onSuccess(result);
        }
      });
   }

}
