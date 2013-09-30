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
 * The subclass of <b>SensorRefItem</b>, which define a relation between a sensor and a switch.
 * A switch can have a SwitchSensorRef.
 */
@Entity
@DiscriminatorValue("SWITCH_SENSOR_REF")
public class SwitchSensorRef extends SensorRefItem {

   private static final long serialVersionUID = -4636324663189338051L;
   
   private Switch switchToggle;

   @OneToOne
   @JoinColumn(name = "switch_oid")
   public Switch getSwitchToggle() {
      return switchToggle;
   }

   public void setSwitchToggle(Switch switchToggle) {
      this.switchToggle = switchToggle;
   }

   public SwitchSensorRef(Switch switchToggle) {
      this.switchToggle = switchToggle;
   }

   public SwitchSensorRef() {
   }
}
