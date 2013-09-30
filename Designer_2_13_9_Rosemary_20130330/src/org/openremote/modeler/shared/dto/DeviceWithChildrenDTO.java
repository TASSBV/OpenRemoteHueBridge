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

public class DeviceWithChildrenDTO extends DeviceDTO {

  private static final long serialVersionUID = 1L;

  private ArrayList<DeviceCommandDTO> deviceCommands;
  private ArrayList<SensorDTO> sensors;
  private ArrayList<SwitchDTO> switches;
  private ArrayList<SliderDTO> sliders;
  
  public ArrayList<DeviceCommandDTO> getDeviceCommands() {
    return deviceCommands;
  }

  public void setDeviceCommands(ArrayList<DeviceCommandDTO> deviceCommands) {
    this.deviceCommands = deviceCommands;
  }

  public ArrayList<SensorDTO> getSensors() {
    return sensors;
  }

  public void setSensors(ArrayList<SensorDTO> sensors) {
    this.sensors = sensors;
  }

  public ArrayList<SwitchDTO> getSwitches() {
    return switches;
  }

  public void setSwitches(ArrayList<SwitchDTO> switches) {
    this.switches = switches;
  }

  public ArrayList<SliderDTO> getSliders() {
    return sliders;
  }

  public void setSliders(ArrayList<SliderDTO> sliders) {
    this.sliders = sliders;
  }

  public DeviceWithChildrenDTO() {
    super();
  }

  public DeviceWithChildrenDTO(long oid, String displayName) {
    super(oid, displayName);
  }

}
