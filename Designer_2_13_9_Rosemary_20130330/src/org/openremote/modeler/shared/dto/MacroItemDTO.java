package org.openremote.modeler.shared.dto;

public class MacroItemDTO implements DTO {

  private static final long serialVersionUID = 1L;

  private String displayName;
  private MacroItemType type;
  
  public MacroItemDTO() {
    super();
  }
  
  public MacroItemDTO(String displayName, MacroItemType type) {
    super();
    this.displayName = displayName;
    this.type = type;
  }

  public String getDisplayName() {
    return displayName;
  }
  
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
  
  public MacroItemType getType() {
    return type;
  }
  
  public void setType(MacroItemType type) {
    this.type = type;
  }
  
}