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

public class UserDTO implements DTO {

  private static final long serialVersionUID = 1L;
  
  private String userName;
  private String eMail;
  private String role;
  private long oid;
  
  public UserDTO() {
    super();
  }

  public UserDTO(long oid, String userName, String eMail, String role) {
    super();
    this.oid = oid;
    this.userName = userName;
    this.eMail = eMail;
    this.role = role;
  }

  public String getUserName() {
    return userName;
  }
  
  public void setUserName(String userName) {
    this.userName = userName;
  }
  
  public long getOid() {
    return oid;
  }
  
  public void setOid(long oid) {
    this.oid = oid;
  }

  public String geteMail() {
    return eMail;
  }

  public void seteMail(String eMail) {
    this.eMail = eMail;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

}
