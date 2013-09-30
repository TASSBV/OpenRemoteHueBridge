package org.openremote.modeler.shared.russound;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Result;

import org.openremote.modeler.shared.dto.DeviceDTO;

public class CreateRussoundDeviceResult implements Result {

  private ArrayList<DeviceDTO> devices;

  public CreateRussoundDeviceResult() {
    super();
  }

  public ArrayList<DeviceDTO> getDevices() {
    return devices;
  }

  public void setDevices(ArrayList<DeviceDTO> devices) {
    this.devices = devices;
  }
  
  // TODO: should hold  potential error message
}
