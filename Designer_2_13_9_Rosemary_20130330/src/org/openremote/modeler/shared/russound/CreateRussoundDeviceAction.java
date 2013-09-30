package org.openremote.modeler.shared.russound;

import net.customware.gwt.dispatch.shared.Action;

public class CreateRussoundDeviceAction implements Action<CreateRussoundDeviceResult> {
  
  private String deviceName;
  private String model;
  private int controllerCount;

  public CreateRussoundDeviceAction() {
    super();
  }
  
  public CreateRussoundDeviceAction(String deviceName, String model, int controllerCount) {
    super();
    this.deviceName = deviceName;
    this.model = model;
    this.controllerCount = controllerCount;
  }

  public String getDeviceName() {
    return deviceName;
  }
  
  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }
  
  public String getModel() {
    return model;
  }
  
  public void setModel(String model) {
    this.model = model;
  }
  
  public int getControllerCount() {
    return controllerCount;
  }
  
  public void setControllerCount(int controllerCount) {
    this.controllerCount = controllerCount;
  }
  
}
