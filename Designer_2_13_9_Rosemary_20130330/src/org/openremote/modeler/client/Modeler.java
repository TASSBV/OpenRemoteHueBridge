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
package org.openremote.modeler.client;

import org.openremote.modeler.client.view.ApplicationView;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The EntryPoint of GWT application. all the code must put into {@link #onModuleLoad()} method.
 */
public class Modeler implements EntryPoint {

   /**
    * This is the entry point method.
    */
   public void onModuleLoad() {
      init();
   }

   /**
    * Inits the application, hide the loading image and message.
    * Display the application view.
    */
   private void init() {
      DOM.setStyleAttribute(RootPanel.get("loading-cont").getElement(), "display", "none");
      
      EventBus eventBus = new SimpleEventBus();
      ApplicationView appView = new ApplicationView(eventBus);
      appView.initialize();
   }

}
