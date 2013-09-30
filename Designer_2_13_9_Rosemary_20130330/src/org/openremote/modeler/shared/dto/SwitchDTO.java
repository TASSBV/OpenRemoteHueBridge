package org.openremote.modeler.shared.dto;

public class SwitchDTO implements DTO {

  private static final long serialVersionUID = 1L;

  private String displayName;
  private long oid;
  private DeviceCommandDTO onCommand;
  private DeviceCommandDTO offCommand;

  public SwitchDTO() {
    super();
  }
  
  public SwitchDTO(long oid, String displayName) {
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

  public DeviceCommandDTO getOnCommand() {
    return onCommand;
  }

  public void setOnCommand(DeviceCommandDTO onCommand) {
    this.onCommand = onCommand;
  }

  public DeviceCommandDTO getOffCommand() {
    return offCommand;
  }

  public void setOffCommand(DeviceCommandDTO offCommand) {
    this.offCommand = offCommand;
  }

}
