package org.openremote.modeler.lutron.importmodel;

import java.util.HashSet;
import java.util.Set;

public class ControlStation {

  private String name;
  private Set<Device> devices;
  
  public ControlStation(String name) {
    super();
    this.name = name;
    this.devices = new HashSet<Device>();
  }

  public String getName() {
    return name;
  }
  
  public Set<Device> getDevices() {
    return devices;
  }
  
  public void addDevice(Device device) {
    devices.add(device);
  }
  
}
