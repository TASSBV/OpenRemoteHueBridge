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
package org.openremote.modeler.client.widget.buildingmodeler;

import org.openremote.modeler.client.event.SubmitEvent;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;

/**
 * The window creates a deviceCommand, but not save into server.
 */
public class DeviceCommandWizardWindow extends DeviceCommandWindow {

   public DeviceCommandWizardWindow() {
     super(null, null); // No need for this here, provide dummy info
      form.removeAllListeners();
      onSubmit();
   }

   private void onSubmit() {
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
           populateDeviceCommandFromFields();
           fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(deviceCommand));
           if (hideWindow) {
              hide();
           } else {
              hideWindow = true;
              unmask();
              info.setText("Command '" + deviceCommand.getName() + "' is created.");
              info.show();              
              deviceCommand = deviceCommand.cloneFields();
           }
         }
      });
   }

}
