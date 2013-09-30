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

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import flexjson.JSON;

/**
 * The super class of <b>SliderSensorRef</b> and <b>SwitchSensorRef</b>.
 * It include a sensor.
 */
@Entity
@Table(name = "sensor_ref_item")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@DiscriminatorValue("SENSOR_REF_ITEM")
public class SensorRefItem extends BusinessEntity {

   private static final long serialVersionUID = 6345154064811943808L;
   
   private Sensor sensor;
   
   @OneToOne
   @JoinColumn(name = "target_sensor_oid")
   @JSON(include = false)
   public Sensor getSensor() {
      return sensor;
   }
   public void setSensor(Sensor sensor) {
      this.sensor = sensor;
   }
   
   @Override
   @Transient
   public String getDisplayName() {
      return sensor.getDisplayName();
   }
   
   /**
    * Equals without compare oid.
    * Used for rebuilding from template.
    * 
    * @param other the other
    * 
    * @return true, if successful
    */
   public boolean equalsWithoutCompareOid(SensorRefItem other) {
      return sensor.equalsWithoutCompareOid(other.getSensor());
   }
   
   public boolean equals(Object obj) {
      if (obj == null) return false;
      if (obj.getClass() != this.getClass()) return false;
      SensorRefItem other = (SensorRefItem) obj;
      if (other.getOid() != this.getOid()) return false;
      if (sensor == null) {
         if(other.sensor != null) return false;
      } else if (! sensor.equals(other.sensor)) return false;
      return true;
   }
}
