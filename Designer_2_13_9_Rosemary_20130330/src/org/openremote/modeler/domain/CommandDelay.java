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
import javax.persistence.Transient;

/**
 * It define a delay second for sending a delay command.
 */
@Entity
@DiscriminatorValue("CMD_DELAY")
public class CommandDelay extends DeviceMacroItem {

   private static final long serialVersionUID = -6381795078683635947L;
   
   /** The delay millisecond. */
   private String delaySecond;
   
   /**
    * Instantiates a new command delay, this constructor is for gwt rpc serial.
    */
   public CommandDelay() {
   }
   
   /**
    * Instantiates a new command delay.
    * 
    * @param delaySecond the delay second
    */
   public CommandDelay(String delaySecond) {
      super();
      this.delaySecond = delaySecond;
   }

   /**
    * Gets the delay second.
    * 
    * @return the delay second
    */
   public String getDelaySecond() {
      return delaySecond;
   }

   /**
    * Sets the delay second.
    * 
    * @param delaySecond the new delay second
    */
   public void setDelaySecond(String delaySecond) {
      this.delaySecond = delaySecond;
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.domain.BusinessEntity#getDisplayName()
    */
   @Override
   @Transient
   public String getDisplayName() {
      return "Delay (" + getDelaySecond() + "ms)";
   }
   
}
