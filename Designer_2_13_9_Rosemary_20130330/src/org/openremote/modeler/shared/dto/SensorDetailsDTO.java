/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.modeler.shared.dto;

import java.util.HashMap;

import org.openremote.modeler.domain.SensorType;

public class SensorDetailsDTO implements DTO {

  private static final long serialVersionUID = 1L;

  private String name;
  private SensorType type;
  private String commandName;
  private Integer minValue;
  private Integer maxValue;
  private HashMap<String, String> states;
  private Long oid;
  private DTOReference command;
  
  public SensorDetailsDTO() {
    super();
  }

  public SensorDetailsDTO(Long oid, String name, SensorType type, String commandName, Integer minValue, Integer maxValue, HashMap<String, String> states) {
    super();
    this.oid = oid;
    this.name = name;
    this.type = type;
    this.commandName = commandName;
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.states = states;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SensorType getType() {
    return type;
  }

  public void setType(SensorType type) {
    this.type = type;
  }

  public String getCommandName() {
    return commandName;
  }

  public void setCommandName(String commandName) {
    this.commandName = commandName;
  }

  public Integer getMinValue() {
    return minValue;
  }

  public void setMinValue(Integer minValue) {
    this.minValue = minValue;
  }

  public Integer getMaxValue() {
    return maxValue;
  }

  public void setMaxValue(Integer maxValue) {
    this.maxValue = maxValue;
  }

  public HashMap<String, String> getStates() {
    return states;
  }

  public void setStates(HashMap<String, String> states) {
    this.states = states;
  }

  public Long getOid() {
    return oid;
  }

  public void setOid(Long oid) {
    this.oid = oid;
  }

  public DTOReference getCommand() {
    return command;
  }

  public void setCommand(DTOReference command) {
    this.command = command;
  }
  
  public Long getCommandId() {
    return (command != null)?command.getId():null;
  }

}
