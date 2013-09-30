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

import org.openremote.modeler.client.event.DeviceUpdatedEvent;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.DeviceBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.CommonForm;
import org.openremote.modeler.client.widget.CommonWindow;
import org.openremote.modeler.selenium.DebugId;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.modeler.shared.dto.DeviceDetailsDTO;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.event.shared.EventBus;

/**
 * The window to edit a device, not used for creation.
 */
public class DeviceWindow extends CommonWindow {

  /** The device form. */
  private CommonForm deviceForm;

  private EventBus eventBus;

  /**
   * Instantiates a new device window.
   * 
   * @param deviceBeanModel the device model
   */
  public DeviceWindow(DeviceDTO device, EventBus eventBus) {
    super();
    this.eventBus = eventBus;
    initial(device);
  }

  /**
    * Initial.
    * 
    * @param deviceBeanModel the device bean model
    */
  protected void initial(final DeviceDTO device) {
    setSize(360, 200);
    setHeading("Loading...");
    // TODO EBR better indicate device is loading

    addListener(SubmitEvent.SUBMIT, new SubmitListener() {
      @Override
      public void afterSubmit(SubmitEvent be) {
        hide();
        BeanModel deviceModel = be.getData();
        DeviceDetailsDTO deviceDetails = deviceModel.getBean();
        device.setDisplayName(deviceDetails.getName()); // Update the DeviceDTO so changes are displayed without the need to reload it
        eventBus.fireEvent(new DeviceUpdatedEvent(device));
        Info.display("Info", "Edit device " + deviceDetails.getName() + " success.");
      }
    });

    DeviceBeanModelProxy.loadDeviceDetails(device, new AsyncSuccessCallback<BeanModel>() {
      public void onSuccess(BeanModel result) {
        setHeading("Edit Device");
        deviceForm = new DeviceInfoForm(DeviceWindow.this, result);
        add(deviceForm);
        setFocusWidget(deviceForm.getFields().get(0));
        layout();
      }
    });

    ensureDebugId(DebugId.NEW_DEVICE_WINDOW);
  }

}
