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

import java.util.ArrayList;

import org.openremote.modeler.domain.SensorType;

public class SensorWithInfoDTO implements DTO {

  private static final long serialVersionUID = 1L;

  private String displayName;
  private SensorType type;
  private String commandName;
  private String minValue;
  private String maxValue;
  private ArrayList<String> stateNames;
  private Long oid;
  
  public SensorWithInfoDTO() {
    super();
  }

  public SensorWithInfoDTO(Long oid, String displayName, SensorType type, String commandName, String minValue, String maxValue, ArrayList<String> stateNames) {
    super();
    this.oid = oid;
    this.displayName = displayName;
    this.type = type;
    this.commandName = commandName;
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.stateNames = stateNames;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
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

  public String getMinValue() {
    return minValue;
  }

  public void setMinValue(String minValue) {
    this.minValue = minValue;
  }

  public String getMaxValue() {
    return maxValue;
  }

  public void setMaxValue(String maxValue) {
    this.maxValue = maxValue;
  }

  public ArrayList<String> getStateNames() {
    return stateNames;
  }

  public void setStateNames(ArrayList<String> stateNames) {
    this.stateNames = stateNames;
  }

  public Long getOid() {
    return oid;
  }

  public void setOid(Long oid) {
    this.oid = oid;
  }

  public String getStatesInfo() {
    if (stateNames == null) {
      return null;
    }
    StringBuffer states = new StringBuffer("");
    for (String stateName : stateNames) {
       states.append(stateName);
       states.append(". ");
    }
    return states.toString();
  }
}
