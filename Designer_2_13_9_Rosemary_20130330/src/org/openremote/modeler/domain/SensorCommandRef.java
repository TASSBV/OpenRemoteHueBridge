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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * This commandRefItem is used for sensor.
 * It define the relation between command and sensor.
 */
@Entity
@DiscriminatorValue("SENSOR_CMD_REF")
public class SensorCommandRef extends CommandRefItem {

   private static final long serialVersionUID = 2310916896667009631L;
   
   private Sensor sensor;
   
   @OneToOne
   @JoinColumn(name = "sensor_oid")
   public Sensor getSensor() {
      return sensor;
   }

   public void setSensor(Sensor sensor) {
      this.sensor = sensor;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      SensorCommandRef other = (SensorCommandRef) obj;
      if (this.getDeviceCommand() == null) {
         if (other.getDeviceCommand() != null) return false;
      } else if (! this.getDeviceCommand().equals(other.getDeviceCommand())) return false;
      return this.getOid() == other.getOid();
   }

   public boolean equalsWithoutCompareOid(SensorCommandRef other) {
      DeviceCommand cmd = this.getDeviceCommand();
      if (cmd != null) {
         return cmd.equalsWithoutCompareOid(other.getDeviceCommand());
      }
      else return false;
   }
}
