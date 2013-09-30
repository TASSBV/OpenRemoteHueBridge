package org.openremote.modeler.shared.dto;

import java.util.HashMap;

public class DeviceCommandDetailsDTO implements DTO {

  private static final long serialVersionUID = 1L;
  
  private Long oid;
  private String name;
  private String protocolType;
  private HashMap<String, String> protocolAttributes;
  
  public DeviceCommandDetailsDTO() {
    super();
  }
  
  public DeviceCommandDetailsDTO(Long oid, String name, String protocolType) {
    super();
    this.oid = oid;
    this.name = name;
    this.protocolType = protocolType;
  }

  public Long getOid() {
    return oid;
  }
  
  public void setOid(Long oid) {
    this.oid = oid;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getProtocolType() {
    return protocolType;
  }
  
  public void setProtocolType(String protocolType) {
    this.protocolType = protocolType;
  }
  
  public HashMap<String, String> getProtocolAttributes() {
    return protocolAttributes;
  }
  
  public void setProtocolAttributes(HashMap<String, String> protocolAttributes) {
    this.protocolAttributes = protocolAttributes;
  }

  /**
   * @return New DeviceCommandDetailsDTO instance with all fields equal except oid that is left null.
   */
  public DeviceCommandDetailsDTO cloneFields() {
    DeviceCommandDetailsDTO clone = new DeviceCommandDetailsDTO(null, getName(), getProtocolType());
    clone.setProtocolAttributes(new HashMap<String, String>(getProtocolAttributes()));
    return clone;
  }
  
}
