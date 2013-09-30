package org.openremote.modeler.shared.dto;

public class SliderDTO implements DTO {

  private static final long serialVersionUID = 1L;

  private String displayName;
  private long oid;
  private DeviceCommandDTO command;

  public SliderDTO() {
    super();
  }
  
  public SliderDTO(long oid, String displayName) {
    super();
    this.oid = oid;
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
  
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
  
  public long getOid() {
    return oid;
  }
  
  public void setOid(long oid) {
    this.oid = oid;
  }

  public DeviceCommandDTO getCommand() {
    return command;
  }

  public void setCommand(DeviceCommandDTO command) {
    this.command = command;
  }

}
