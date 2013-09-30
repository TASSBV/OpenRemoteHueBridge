package org.openremote.modeler.shared.dto;

import java.util.ArrayList;

public class MacroDetailsDTO implements DTO {

  private static final long serialVersionUID = 1L;

  private Long oid;
  private String name;
  private ArrayList<MacroItemDetailsDTO> items;
  
  public MacroDetailsDTO() {
    super();
  }

  public MacroDetailsDTO(Long oid, String name, ArrayList<MacroItemDetailsDTO> items) {
    super();
    this.oid = oid;
    this.name = name;
    this.items = items;
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

  public ArrayList<MacroItemDetailsDTO> getItems() {
    return items;
  }

  public void setItems(ArrayList<MacroItemDetailsDTO> items) {
    this.items = items;
  }
  
}
