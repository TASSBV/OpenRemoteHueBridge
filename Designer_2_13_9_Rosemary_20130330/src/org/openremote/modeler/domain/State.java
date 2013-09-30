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
package org.openremote.modeler.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The Class State.
 */
@Entity
@Table(name = "state")
public class State extends BusinessEntity {
   private static final long serialVersionUID = -4878125106767971531L;
   
   private String name = "state1";
   private String value = "";
   
   private CustomSensor sensor;
   
   public State() {
    super();
  }

  public State(long oid) {
    super(oid);
  }

  public State(String name, String value) {
    super();
    this.name = name;
    this.value = value;
  }

  public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(nullable = false)
   public CustomSensor getSensor() {
      return sensor;
   }

   public void setSensor(CustomSensor sensor) {
      this.sensor = sensor;
   }
   
   @Transient
   public String getDisplayName() {
      return getName();
   }
}
