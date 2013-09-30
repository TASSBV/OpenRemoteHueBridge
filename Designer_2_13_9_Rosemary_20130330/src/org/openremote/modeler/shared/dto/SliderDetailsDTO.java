package org.openremote.modeler.shared.dto;

public class SliderDetailsDTO implements DTO {

  private static final long serialVersionUID = 1L;

  private Long oid;
  private String name;
  private DTOReference sensor;
  private String commandName;
  private DTOReference command;

  public SliderDetailsDTO() {
    super();
  }

  public SliderDetailsDTO(Long oid, String name, DTOReference sensor, DTOReference command, String commandName) {
    super();
    this.oid = oid;
    this.name = name;
    this.sensor = sensor;
    this.command = command;
    this.commandName = commandName;
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
  
  public DTOReference getSensor() {
    return sensor;
  }

  public void setSensor(DTOReference sensor) {
    this.sensor = sensor;
  }

  public DTOReference getCommand() {
    return command;
  }

  public void setCommand(DTOReference command) {
    this.command = command;
  }

  public String getCommandName() {
    return commandName;
  }

  public void setCommandName(String commandName) {
    this.commandName = commandName;
  }

}