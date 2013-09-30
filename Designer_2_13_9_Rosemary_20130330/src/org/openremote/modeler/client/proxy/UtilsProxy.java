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

import java.util.Collection;
import java.util.List;

import org.openremote.modeler.client.model.AutoSaveResponse;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.utils.PanelsAndMaxOid;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.component.UISlider;
import org.openremote.modeler.shared.GraphicalAssetDTO;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The proxy supports utility methods to export file(openremote.zip), save uiDesignerLayout, restore uiDesignerLayout, 
 * get some url path, download image and rotate image.
 * 
 * @author handy.wang
 */
public class UtilsProxy {
   
   /**
    * Not be instantiated.
    */
   private UtilsProxy() {
   }

   /**
    * Load device.
    * 
    * @param callback the callback
    * @param maxId the max id
    * @param activityList the activity list
    */
   public static void exportFiles(long maxId, List<Panel> panelList, final AsyncSuccessCallback<String> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().exportFiles(maxId, panelList,callback);
   }

   /**
    * Auto save ui designer layout json.
    * 
    * @param groups the activities
    * @param callback the callback
    */
   public static void autoSaveUiDesignerLayout(Collection<Panel> panels, long maxID, final AsyncSuccessCallback<AutoSaveResponse> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().autoSaveUiDesignerLayout(panels,  maxID, callback);
   }
   
   public static void saveUiDesignerLayout(Collection<Panel> panels, long maxID, final AsyncSuccessCallback<AutoSaveResponse> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().saveUiDesignerLayout(panels,  maxID, callback);
   }
   
   public static void restore(final AsyncCallback<PanelsAndMaxOid> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().restore(callback);
   }
   
   public static void canRestore(final AsyncCallback<Boolean> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().canRestore(callback);
   }
   
   /**
    * Gets the beehive rest icon url.
    * 
    * @param callback the callback
    * 
    */
   public static void getBeehiveRestIconUrl(final AsyncSuccessCallback<String> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().beehiveRestIconUrl(callback);
   }
   
   public static void getIrServiceRestRootUrl(final AsyncSuccessCallback<String> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().getIrServiceRestRootURL(callback);
   }
   
   public static void loadPanelsFromSession(final AsyncSuccessCallback<Collection<Panel>> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().loadPanelsFromSession(callback);
   }
   
   public static void loadGroupsFromSession(final AsyncSuccessCallback<List<Group>> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().loadGroupsFromSession(callback);
   }
   
   public static void loadScreensFromSession(final AsyncSuccessCallback<List<Screen>> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().loadScreensFromSession(callback);
   }
   
   /**
    * Load layout component's max id from session.
    * 
    */
   public static void loadMaxID(final AsyncSuccessCallback<Long> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().loadMaxID(callback);
   }
   
   public static void downLoadImage(String url, final AsyncCallback<String> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().downLoadImage(url, callback);
   }
   
   public static void getTemplatesListRestUrl(final AsyncCallback <String> callback){
      AsyncServiceFactory.getConfigurationRPCServiceAsync().getTemplatesListRestUrl(callback);
   }
   
   public static void getAllPublicTemplateRestURL(final AsyncCallback <String> callback){
      AsyncServiceFactory.getConfigurationRPCServiceAsync().getAllPublicTemplateRestUrl(callback);
   }
   
   public static void roteImages(final UISlider uiSlider, final AsyncCallback <UISlider> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().rotateImage(uiSlider, callback);
   }

   public static void getAccountRelativePath(final AsyncCallback <String> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().getAccountPath(callback);
   }
   
   public static void getOnTestLineURL(final AsyncCallback<String> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().getOnLineTestURL(callback);
   }
   
   public static boolean isPanelNameAvailable(String panelName) {
      List<BeanModel> panelModels = BeanModelDataBase.panelTable.loadAll();
      for (BeanModel panelModel : panelModels) {
         if (panelName.equals(panelModel.get("name").toString())) {
            return false;
         }
      }
      return true;
   }
   
   public static void getUserImagesURLs(AsyncCallback<List<GraphicalAssetDTO>> callback) {
     AsyncServiceFactory.getUtilsRPCServiceAsync().getUserImagesURLs(callback);
   }
   
   public static void deleteImage(String imageName, AsyncCallback<Void> callback) {
     AsyncServiceFactory.getUtilsRPCServiceAsync().deleteImage(imageName, callback);
   }

}
