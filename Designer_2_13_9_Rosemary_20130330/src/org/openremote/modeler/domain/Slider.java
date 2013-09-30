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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import flexjson.JSON;

/**
 * The domain class represent a slider entity.
 * It contains a command and a sensor.
 */

@Entity
@Table(name = "slider")
public class Slider extends BusinessEntity {

  private static final long serialVersionUID = 134992115758993597L;
  
  private String name;
   private SliderCommandRef setValueCmd;
   private SliderSensorRef sliderSensorRef;
   private Account account;
   private Device device;
   
   public Slider() {
    super();
  }

  public Slider(long oid) {
    super(oid);
  }

  public Slider(String name, DeviceCommand setCmd, Sensor sensor) {
    super();
    setName(name);
    setDevice(sensor.getDevice());
    SliderCommandRef setValueCmdRef = new SliderCommandRef();
    setValueCmdRef.setDeviceCommand(setCmd);
    setValueCmdRef.setDeviceName(setCmd.getDevice().getName());
    setValueCmdRef.setSlider(this);
    SliderSensorRef sensorRef = new SliderSensorRef(this);
    sensorRef.setSensor(sensor);
    setSetValueCmd(setValueCmdRef);
    setSliderSensorRef(sensorRef);
  }

  public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @OneToOne(mappedBy = "slider", cascade = CascadeType.ALL)
   public SliderCommandRef getSetValueCmd() {
      return setValueCmd;
   }

   public void setSetValueCmd(SliderCommandRef setValueCmd) {
      this.setValueCmd = setValueCmd;
   }

   @ManyToOne
   @JSON(include = false)
   public Account getAccount() {
      return account;
   }

   public void setAccount(Account account) {
      this.account = account;
   }

   @OneToOne(mappedBy = "slider", cascade = CascadeType.ALL)
   public SliderSensorRef getSliderSensorRef() {
      return sliderSensorRef;
   }

   public void setSliderSensorRef(SliderSensorRef sliderSensorRef) {
      this.sliderSensorRef = sliderSensorRef;
   }

   @ManyToOne
   @JSON(include = false)
   public Device getDevice() {
      return device;
   }

   public void setDevice(Device device) {
      this.device = device;
   }

   @Transient
   public String getDisplayName() {
      return getName();
   }

   @Override
   public int hashCode() {
      return (int) getOid();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      Slider other = (Slider) obj;
      if (this.getOid() != other.getOid()) return false;
      if (device == null) {
         if (other.device != null) return false;
      } else if (!device.equals(other.device)) return false;
      if (name == null) {
         if (other.name != null) return false;
      } else if (!name.equals(other.name)) return false;
      if (setValueCmd == null) {
         if (other.setValueCmd != null) return false;
      } else if (!setValueCmd.equals(other.setValueCmd)) return false;
      if (sliderSensorRef == null) {
         if (other.sliderSensorRef != null) return false;
      } else if (!sliderSensorRef.equals(other.sliderSensorRef)) return false;
      return true;
   }
   
   /**
    * Equals without compare oid.
    * Used for rebuilding from template.
    * 
    * @param other the other
    * 
    * @return true, if successful
    */
   public boolean equalsWithoutCompareOid(Slider other) {
      if (other == null) return false;
      if (name == null) {
         if (other.name != null) return false;
      } else if (!name.equals(other.name)) return false;
      if (this.device != null) {
         if (other.device == null || this.device.getOid() != other.device.getOid()) return false;
      } else if (other.device != null) return false;
      if (this.setValueCmd != null) {
         if (other.setValueCmd == null || !this.setValueCmd.equalsWithoutCompareOid(other.getSetValueCmd())) return false;
      } else if (other.setValueCmd != null)  return false;
      if (this.sliderSensorRef != null) {
         if (other.sliderSensorRef == null || !sliderSensorRef.equalsWithoutCompareOid(other.sliderSensorRef)) return false;
      } else if(other.sliderSensorRef != null) return false;
      return true;
   }
}
