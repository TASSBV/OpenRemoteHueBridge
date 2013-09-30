package org.openremote.modeler.shared.dto;

public class MacroItemDetailsDTO implements DTO {

  private static final long serialVersionUID = 1L;

  private Long oid;
  private MacroItemType type;
  private String displayName;
  private Integer delay;
  private DTOReference dto;
  
  public MacroItemDetailsDTO() {
    super();
  }
  
  public MacroItemDetailsDTO(Long oid, Integer delay) {
    super();
    this.oid = oid;
    this.delay = delay;
    this.type = MacroItemType.Delay;
    this.displayName = "Delay " + delay + " ms";
  }

  public MacroItemDetailsDTO(Long oid, MacroItemType type, String displayName, DTOReference dto) {
    super();
    this.oid = oid;
    this.type = type;
    this.displayName = displayName;
    this.dto = dto;
  }

  public Long getOid() {
    return oid;
  }

  public void setOid(Long oid) {
    this.oid = oid;
  }

  public Integer getDelay() {
    return delay;
  }

  public void setDelay(Integer delay) {
    this.delay = delay;
    this.displayName = "Delay " + delay + " ms";
  }

  public DTOReference getDto() {
    return dto;
  }

  public void setDto(DTOReference dto) {
    this.dto = dto;
  }

  public MacroItemType getType() {
    return type;
  }

  public void setType(MacroItemType type) {
    this.type = type;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

}
