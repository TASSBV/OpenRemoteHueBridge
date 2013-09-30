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

/**
 * Data transfer object to move a switch component state between two separate processes
 * (mainly used between client and the server side component).
 *
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class SwitchDetailsDTO implements DTO
{

  private static final long serialVersionUID = 1L;
  
  private Long oid;
  private String name;
  private DTOReference sensor;
  private DTOReference onCommand;
  private String onCommandDisplayName;
  private DTOReference offCommand;
  private String offCommandDisplayName;

  public SwitchDetailsDTO()
  {
    super();
  }

  public SwitchDetailsDTO(Long oid, String name, DTOReference sensor, DTOReference onCommand, String onCommandDisplayName, DTOReference offCommand, String offCommandDisplayName) {
    super();
    this.oid = oid;
    this.name = name;
    this.sensor = sensor;
    this.onCommand = onCommand;
    this.onCommandDisplayName = onCommandDisplayName;
    this.offCommand = offCommand;
    this.offCommandDisplayName = offCommandDisplayName;
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

  public DTOReference getOnCommand() {
    return onCommand;
  }

  public void setOnCommand(DTOReference onCommand) {
    this.onCommand = onCommand;
  }

  public DTOReference getOffCommand() {
    return offCommand;
  }

  public void setOffCommand(DTOReference offCommand) {
    this.offCommand = offCommand;
  }

  public String getOnCommandDisplayName() {
    return onCommandDisplayName;
  }

  public void setOnCommandDisplayName(String onCommandDisplayName) {
    this.onCommandDisplayName = onCommandDisplayName;
  }

  public String getOffCommandDisplayName() {
    return offCommandDisplayName;
  }

  public void setOffCommandDisplayName(String offCommandDisplayName) {
    this.offCommandDisplayName = offCommandDisplayName;
  }

}
