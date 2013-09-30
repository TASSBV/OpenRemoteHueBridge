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
import javax.persistence.Transient;


/**
 * The Class DeviceMacroRef.
 */
@Entity
@DiscriminatorValue("DEVICE_MACRO_REF")
public class DeviceMacroRef extends DeviceMacroItem {

   private static final long serialVersionUID = 266287402505655375L;
   
   /** The target device macro. */
   private DeviceMacro targetDeviceMacro;
   
   /**
    * Instantiates a new device macro ref.
    */
   public DeviceMacroRef() {
      
   }
   
   /**
    * Instantiates a new device macro ref.
    * 
    * @param targetDeviceMacro the target device macro
    */
   public DeviceMacroRef(DeviceMacro targetDeviceMacro) {
      super();
      this.targetDeviceMacro = targetDeviceMacro;
   }

   /**
    * Gets the target device macro.
    * 
    * @return the target device macro
    */
   @OneToOne
   @JoinColumn(name = "target_device_macro_oid")
   public DeviceMacro getTargetDeviceMacro() {
      return targetDeviceMacro;
   }

   /**
    * Sets the target device macro.
    * 
    * @param targetDeviceMacro the new target device macro
    */
   public void setTargetDeviceMacro(DeviceMacro targetDeviceMacro) {
      this.targetDeviceMacro = targetDeviceMacro;
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.domain.BusinessEntity#getDisplayName()
    */
   @Override
   @Transient
   public String getDisplayName() {
      return getTargetDeviceMacro().getName() + " (Macro)";
   }
   
}
