package org.openremote.modeler.server.lutron.importmodel;

import java.util.HashSet;
import java.util.Set;

import org.openremote.modeler.shared.lutron.DeviceType;

public class Device {

  
  String address;
  DeviceType type;
  boolean webEnabled;
  String webKeypadName;
  Set<Button> buttons;
  
  public Device(String type, String address, boolean webEnabled, String webKeypadName) {
    super();
    this.type = DeviceType.getFromXmlValue(type);
    this.address = address;
    this.webEnabled = webEnabled;
    this.webKeypadName = webKeypadName;
    this.buttons = new HashSet<Button>();
  }
  
  public Device(DeviceType type, String address, boolean webEnabled, String webKeypadName) {
    super();
    this.type = type;
    this.address = address;
    this.webEnabled = webEnabled;
    this.webKeypadName = webKeypadName;
    this.buttons = new HashSet<Button>();
  }

  public String getAddress() {
    return address;
  }
  
  public DeviceType getType() {
    return type;
  }
  
  public boolean isWebEnabled() {
    return webEnabled;
  }
  
  public String getWebKeypadName() {
    return webKeypadName;
  }
  
  public Set<Button> getButtons() {
    return buttons;
  }
  
  public void addButton(Button button) {
    buttons.add(button);
  }
  
}
