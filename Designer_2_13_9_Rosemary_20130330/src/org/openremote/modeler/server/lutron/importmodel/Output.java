package org.openremote.modeler.server.lutron.importmodel;

import org.openremote.modeler.shared.lutron.OutputType;

public class Output {
  
  private String name;
  private OutputType type;
  private String address;
  
  
  public Output(String name, String type, String address) {
    super();
    this.name = name;
    this.type = OutputType.getFromXmlValue(type);
    this.address = address;
  }
  
  public Output(String name, OutputType type, String address) {
    super();
    this.name = name;
    this.type = type;
    this.address = address;
  }

  public String getName() {
    return name;
  }
  
  public OutputType getType() {
    return type;
  }

  public String getAddress() {
    return address;
  }

}
