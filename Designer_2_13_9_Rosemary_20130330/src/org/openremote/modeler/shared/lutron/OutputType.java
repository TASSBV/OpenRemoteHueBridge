package org.openremote.modeler.shared.lutron;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum OutputType {
  Dimmer("DIMMER"),
  Switch("SWITCH"),
  Motor("MOTOR"),
  GrafikEyeMainUnit("GRX MAIN UNIT"),
  Fan("FAN"),
  MaintainedOutput("MAINTAINED OUTPUT"),
  PulsedOutput("PULSED OUTPUT"),
  QEDShade("QED SHADE"),
  SivoiaShadeKeypad("SIVOIA SHADE KEYPAD");
  
  private static final Map<String, OutputType> lookup = new HashMap<String, OutputType>();

  static {
    for(OutputType t : EnumSet.allOf(OutputType.class))
       lookup.put(t.getXmlValue(), t);
  }

  private String xmlValue;
  
  public String getXmlValue() {
    return xmlValue;
  }

  private OutputType(String xmlValue) {
    this.xmlValue = xmlValue;
  }
  
  public static OutputType getFromXmlValue(String xmlValue) {
    return lookup.get(xmlValue);
  }
};
