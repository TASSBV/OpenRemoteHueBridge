package org.openremote.modeler.lutron.importmodel;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Device {

  enum DeviceType {
    Keypad("KEYPAD"),
    CCO("CCO"),
    CCI("CCI"),
    OnBoardCCI("OBOARD CCI"),
    Tel9("TEL9"),
    GrafikEyeMainUnit("GRX MAIN UNIT"),
    Dimmer_Switch("DIMMER/SWITCH"),
    SivoiaShadeKeypad("SIVOIA SHADE KEYPAD"),
    HomeworksVareoDimmer_Switch("HOMEWORKS VAREO DIMMER/SWITCH"),
    VCTX("VCTX"),
    HHP("HHP"),
    RemoteDimmer_Switch("REMOTE DIMMER/SWITCH"),
    QEDKeypad("QED Keypad"),
    NonSystemDevice("NON-SYSTEM DEVICE");
    
    private static final Map<String, DeviceType> lookup = new HashMap<String, DeviceType>();

    static {
      for(DeviceType t : EnumSet.allOf(DeviceType.class))
         lookup.put(t.getXmlValue(), t);
    }

    private String xmlValue;
    
    public String getXmlValue() {
      return xmlValue;
    }

    private DeviceType(String xmlValue) {
      this.xmlValue = xmlValue;
    }
    
    public static DeviceType getFromXmlValue(String xmlValue) {
      return lookup.get(xmlValue);
    }
  };
  
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
