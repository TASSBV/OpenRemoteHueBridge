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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The Class CommandRefItem is for reference the device command by sensor, switch, slider and other components.
 */
@Entity
@Table(name = "command_ref_item")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@DiscriminatorValue("COMMAND_REF_ITEM")
public class CommandRefItem extends UICommand {

   private static final long serialVersionUID = -7718538501310557635L;

   private DeviceCommand deviceCommand;

   private String deviceName;
   
   @ManyToOne
   @JoinColumn(name = "target_device_command_oid")
   public DeviceCommand getDeviceCommand() {
      return deviceCommand;
   }

   public void setDeviceCommand(DeviceCommand deviceCommand) {
      this.deviceCommand = deviceCommand;
   }
   
   @Transient
   public String getDeviceName() {
      return deviceName;
   }

   public void setDeviceName(String deviceName) {
      this.deviceName = deviceName;
   }
   
   @Override
   @Transient
   public String getDisplayName() {
      this.deviceName = (this.deviceName == null || "".equals(this.deviceName)) ? getDeviceCommand().getDevice().getName() : this.deviceName;
      return getDeviceCommand().getName() + " (" + this.deviceName + ")";
   }
   
   public boolean equals(Object obj) {
      if (obj == null) return false;
      if (obj.getClass() != getClass()) return false;
      CommandRefItem other = (CommandRefItem)obj;
      if (this.getOid() != other.getOid()) return false;
      if (this.deviceCommand == null) {
         if (other.deviceCommand != null) return false;
      } else if (!this.deviceCommand .equals(other.deviceCommand)) return false;
      return true;
   }
}
