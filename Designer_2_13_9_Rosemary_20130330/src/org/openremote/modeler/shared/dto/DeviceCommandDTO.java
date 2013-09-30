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

public class DeviceCommandDTO implements DTO, UICommandDTO {

  private static final long serialVersionUID = 1L;
  
  private String displayName;
  private String protocolType;
  private Long oid;

  public DeviceCommandDTO() {
    super();
  }
  
  public DeviceCommandDTO(Long oid, String displayName, String protocolType) {
    super();
    this.oid = oid;
    this.displayName = displayName;
    this.protocolType = protocolType;
  }

  public String getDisplayName() {
    return displayName;
  }
  
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
  
  public Long getOid() {
    return oid;
  }
  
  public void setOid(Long oid) {
    this.oid = oid;
  }

  public String getProtocolType() {
    return protocolType;
  }

  public void setProtocolType(String protocolType) {
    this.protocolType = protocolType;
  }

}
