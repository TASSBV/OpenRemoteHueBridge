package org.openremote.modeler.shared.lutron;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum DeviceType {
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
