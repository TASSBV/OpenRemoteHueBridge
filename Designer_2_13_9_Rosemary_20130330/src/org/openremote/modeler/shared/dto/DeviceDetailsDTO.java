package org.openremote.modeler.shared.dto;

public class DeviceDetailsDTO implements DTO {

  private static final long serialVersionUID = 1L;
  
  private Long oid;
  private String name;
  private String vendor;
  private String model;
  
  public DeviceDetailsDTO() {
    super();
  }
  
  public DeviceDetailsDTO(Long oid, String name, String vendor, String model) {
    super();
    this.oid = oid;
    this.name = name;
    this.vendor = vendor;
    this.model = model;
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
  
  public String getVendor() {
    return vendor;
  }
  
  public void setVendor(String vendor) {
    this.vendor = vendor;
  }
  
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

}
