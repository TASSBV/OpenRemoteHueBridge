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

import java.util.List;

import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.utils.ScreenFromTemplate;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.domain.Cell;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.Template;
import org.openremote.modeler.domain.ScreenPair.OrientationType;
import org.openremote.modeler.domain.component.Gesture;
import org.openremote.modeler.domain.component.UIGrid;

import com.google.gwt.user.client.rpc.AsyncCallback;
/**
 * The proxy is for managing template.
 * 
 * @author javen
 *
 */
public class TemplateProxy {
   /**
    * Save a template to the beehive. 
    * Only the screen and name information are necessary.After being saved,the content will be set to the template.
    * @param template The template you want to save. 
    * @param callback 
    */
   public static void saveTemplate(final Template template, final AsyncCallback<Template> callback) {
      AsyncServiceFactory.getTemplateRPCServiceAsync().saveTemplate(template, new AsyncSuccessCallback<Template>() {

         @Override
         public void onSuccess(Template result) {
            callback.onSuccess(result);
         }

         @Override
         public void onFailure(Throwable caught) {
            callback.onFailure(caught);
         }

        
      });
   }
   /**
    * Build a screen from a template. 
    * This method is used to build a screen by a template.
    * @param template
    * @param callback
    */
   public static void buildScreenFromTemplate(final Template template, final AsyncCallback<ScreenFromTemplate> callback) {
      AsyncServiceFactory.getTemplateRPCServiceAsync().buildScreeFromTemplate(template,
            new AsyncSuccessCallback<ScreenFromTemplate>() {

               @Override
               public void onSuccess(ScreenFromTemplate screenFromTemplate) {
                  ScreenPair screen = screenFromTemplate.getScreen();
                  initOid(screen);
                  callback.onSuccess(screenFromTemplate);
               }

               @Override
               public void onFailure(Throwable caught) {
                  callback.onFailure(caught);
               }
               
               
            });
   }
   
   public static void buildScreen(final Template template, final AsyncCallback<ScreenPair> callback) {
      AsyncServiceFactory.getTemplateRPCServiceAsync().buildScreen(template, 
            new AsyncSuccessCallback<ScreenPair>() {

               @Override
               public void onSuccess(ScreenPair screen) {
                  initOid(screen);
                  callback.onSuccess(screen);
               }

               @Override
               public void onFailure(Throwable caught) {
                  callback.onFailure(caught);
               }
               
               
            });
   }
   public static void deleteTemplateById(final long templateId, final AsyncCallback<Boolean> callback){
      AsyncServiceFactory.getTemplateRPCServiceAsync().deleteTemplate(templateId,new AsyncSuccessCallback<Boolean>(){

         @Override
         public void onSuccess(Boolean result) {
           callback.onSuccess(result);
         }

         @Override
         public void onFailure(Throwable caught) {
            callback.onFailure(caught);
         }
         
         
      });
   }
   /**
    * get all templates by shared type. 
    * @param isFromPrivate
    * @param callback
    */
   public static void getTemplates(final boolean isFromPrivate, final AsyncCallback<List<Template>> callback) {
      AsyncServiceFactory.getTemplateRPCServiceAsync().getTemplates(isFromPrivate, new AsyncCallback<List<Template>>() {

         @Override
         public void onFailure(Throwable caught) {
           callback.onFailure(caught);
         }

         @Override
         public void onSuccess(List<Template> result) {
           callback.onSuccess(result);
         }
         
      });
   }
   
   public static void searchTemplates(final String keywords, final int page, final AsyncCallback<List<Template>> callback) {
      AsyncServiceFactory.getTemplateRPCServiceAsync().searchTemplates (keywords, page,new AsyncCallback<List<Template>>() {

         @Override
         public void onFailure(Throwable caught) {
           callback.onFailure(caught);
         }

         @Override
         public void onSuccess(List<Template> result) {
           callback.onSuccess(result);
         }
         
      });
   }
   
   public static void updateTemplate(final Template template, final AsyncCallback<Template> callback) {
      AsyncServiceFactory.getTemplateRPCServiceAsync().updateTemplate(template, new AsyncSuccessCallback<Template>() {

         @Override
         public void onSuccess(Template result) {
            callback.onSuccess(result);
         }

         @Override
         public void onFailure(Throwable caught) {
            callback.onFailure(caught);
         }

        
      });
   }
   /**
    * initialize the oid for every BusinessEntity.
    * @param screen
    */
   private static void initOid(ScreenPair screenPair) {
      if (screenPair != null) {
         screenPair.setOid(IDUtil.nextID());
         if (screenPair.getOrientation().equals(OrientationType.PORTRAIT)) {
            initScreenOid(screenPair.getPortraitScreen());
         } else if (screenPair.getOrientation().equals(OrientationType.LANDSCAPE)) {
            initScreenOid(screenPair.getLandscapeScreen());
         } else if (screenPair.getOrientation().equals(OrientationType.BOTH)) {
            initScreenOid(screenPair.getPortraitScreen());
            initScreenOid(screenPair.getLandscapeScreen());
         }
      }
   }
   
   private static void initScreenOid(Screen screen) {
      if (screen != null) {
         screen.setOid(IDUtil.nextID());
         for (Absolute abs : screen.getAbsolutes()) {
            abs.getUiComponent().setOid(IDUtil.nextID());
         }
         for (UIGrid grid : screen.getGrids()) {
            for (Cell cell : grid.getCells()) {
               cell.getUiComponent().setOid(IDUtil.nextID());
            }
         }
         for (Gesture gesture : screen.getGestures()) {
            gesture.setOid(IDUtil.nextID());
         }
      }
   }
}
