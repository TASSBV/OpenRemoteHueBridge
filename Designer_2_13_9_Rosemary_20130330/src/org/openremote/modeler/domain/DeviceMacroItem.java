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

import com.extjs.gxt.ui.client.data.BeanModel;

import flexjson.JSON;


/**
 * The Class Device Macro Item.
 * 
 * @author Dan 2009-7-6
 */
@Entity
@Table(name = "device_macro_item")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@DiscriminatorValue("DEVICE_MACRO_ITEM")
public class DeviceMacroItem extends UICommand {

   private static final long serialVersionUID = 493702462609922642L;
   
   /** The parent device macro. */
   private DeviceMacro parentDeviceMacro;

   /**
    * Gets the parent device macro.
    * 
    * @return the parent device macro
    */
   @ManyToOne
   @JoinColumn(name = "parent_device_macro_oid")
   @JSON(include = false)
   public DeviceMacro getParentDeviceMacro() {
      return parentDeviceMacro;
   }

   /**
    * Sets the parent device macro.
    * 
    * @param parentDeviceMacro the new parent device macro
    */
   public void setParentDeviceMacro(DeviceMacro parentDeviceMacro) {
      this.parentDeviceMacro = parentDeviceMacro;
   }
   
   /**
    * Gets the label.
    * 
    * @return the label
    */
   @Transient
   @JSON(include = false)
   public String getTreeNodeLabel() {
      if (this instanceof DeviceMacroRef) {
         DeviceMacroRef deviceMacroRef = (DeviceMacroRef) this;
         return deviceMacroRef.getTargetDeviceMacro().getName();
      } else if (this instanceof DeviceCommandRef) {
         DeviceCommandRef commandRef = (DeviceCommandRef) this;
         return commandRef.getDeviceCommand().getName();
      } else if (this instanceof CommandDelay) {
         CommandDelay commandDelay = (CommandDelay) this;
         return "Delay(" + commandDelay.getDelaySecond() + "s)";
      } else {
         return "";
      }
   }
   
   /**
    * Gets the target bean model.
    * 
    * @return the target bean model
    */
   @Transient
   @JSON(include = false)
   public BeanModel getTargetBeanModel() {
      if (this instanceof DeviceMacroRef) {
         return ((DeviceMacroRef) this).getTargetDeviceMacro().getBeanModel();
      } else if (this instanceof DeviceCommandRef) {
         return ((DeviceCommandRef) this).getDeviceCommand().getBeanModel();
      } else if (this instanceof CommandDelay) {
         return this.getBeanModel();
      }
      return null;
   }
   
   public boolean equalsWithoutCompareOid(DeviceMacroItem macroItemB) {
      DeviceMacroItem macroItemA = this;
      if (macroItemA != null && macroItemB != null) {
         if (macroItemA instanceof DeviceMacroRef && macroItemB instanceof DeviceMacroRef) {
            DeviceMacroRef macroRefA = (DeviceMacroRef) macroItemA;
            DeviceMacroRef macroRefB = (DeviceMacroRef) macroItemB;
            if (macroRefA.getTargetDeviceMacro().equalsWitoutCompareOid(macroRefB.getTargetDeviceMacro())) {
               return true;
            }
         } else if (macroItemA instanceof DeviceCommandRef && macroItemB instanceof DeviceCommandRef) {
            DeviceCommandRef cmdRefA = (DeviceCommandRef) macroItemA;
            DeviceCommandRef cmdRefB = (DeviceCommandRef) macroItemB;
            if (cmdRefA.getDeviceCommand().equalsWithoutCompareOid(cmdRefB.getDeviceCommand())) {
               return true;
            }
         } else if (macroItemA instanceof CommandDelay && macroItemB instanceof CommandDelay) {
            CommandDelay delayA = (CommandDelay) macroItemA;
            CommandDelay delayB = (CommandDelay) macroItemB;
            if (delayA.getDelaySecond().equals(delayB.getDelaySecond())) {
               return true;
            }
         }
         return false;
      } else {
         return macroItemA == null && macroItemB == null;
      }
    }

}
