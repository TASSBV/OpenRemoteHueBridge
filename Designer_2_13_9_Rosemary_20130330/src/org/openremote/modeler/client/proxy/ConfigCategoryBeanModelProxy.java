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



import java.util.Set;

import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.ConfigCategory;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The proxy for getting all configuration categories.
 */
public class ConfigCategoryBeanModelProxy {
   public static void getAllCategory(final AsyncCallback<Set<ConfigCategory>> callback){
      AsyncServiceFactory.getConfigCategoryRPCServiceAsync().getCategories(new AsyncSuccessCallback<Set<ConfigCategory>>(){
         @Override
         public void onSuccess(Set<ConfigCategory> result) {
//            BeanModelDataBase.configCategoryTable.insertAll(ConfigCategory.createModels(result));
            callback.onSuccess(result);
         }
         
      });
   }
   
}
