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
package org.openremote.modeler.client.utils;

import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.rpc.TouchPanelRPCService;
import org.openremote.modeler.client.rpc.TouchPanelRPCServiceAsync;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The Class Panels, get all touch panel definitions from xml files.
 */
public class TouchPanels {
   
   /** The instance map. */
   private static Map<String, List<TouchPanelDefinition>> instanceMap;
   
   /** The Constant panelService. */
   private static final TouchPanelRPCServiceAsync panelService = (TouchPanelRPCServiceAsync) GWT.create(TouchPanelRPCService.class);
   
   /**
    * Instantiates a new panels.
    */
   private TouchPanels() {
   }
   
   /**
    * Gets the single instance of Panels.
    * 
    * @return single instance of Panels
    */
   public static synchronized Map<String, List<TouchPanelDefinition>> getInstance() {
      if (instanceMap == null) {
         load();
      }
      return instanceMap;
   }

   /**
    * 
    */
   public static void load() {
      panelService.getPanels(new AsyncCallback<Map<String, List<TouchPanelDefinition>>>() {
         public void onFailure(Throwable caught) {
            MessageBox.info("Error", "Can't get panels from xml file!", null);
         }
         public void onSuccess(Map<String, List<TouchPanelDefinition>> panels) {
            instanceMap = panels;
         }
      });
   }
}
