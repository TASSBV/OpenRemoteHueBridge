/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.modeler.shared.lutron;

import java.io.Serializable;

@SuppressWarnings("serial")
public class OutputImportConfig implements Serializable {

  private String outputName;
  private OutputType type;
  private String address;
  private String roomName;
  private String areaName;
  
  public OutputImportConfig() {
  }

  public OutputImportConfig(String outputName, OutputType type, String address) {
    this();
    this.outputName = outputName;
    this.type = type;
    this.address = address;
  }

  public OutputImportConfig(String outputName, OutputType type, String address, String roomName, String areaName) {
    this(outputName, type, address);
    this.roomName = roomName;
    this.areaName = areaName;
  }

  public String getOutputName() {
    return outputName;
  }

  public void setOutputName(String outputName) {
    this.outputName = outputName;
  }

  public OutputType getType() {
    return type;
  }

  public void setType(OutputType type) {
    this.type = type;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getRoomName() {
    return roomName;
  }

  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }

  public String getAreaName() {
    return areaName;
  }

  public void setAreaName(String areaName) {
    this.areaName = areaName;
  }

}
