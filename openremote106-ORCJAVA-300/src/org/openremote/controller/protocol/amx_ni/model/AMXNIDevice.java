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
package org.openremote.controller.protocol.amx_ni.model;

import java.util.ArrayList;
import java.util.List;

import org.openremote.controller.protocol.amx_ni.AMXNICommand;
import org.openremote.controller.protocol.amx_ni.AMXNIGateway;

/**
 * Abstract parent class of all "devices".
 * An AMXNIDevice is a proxy to an device on the AMX side, for one specific aspect supported: channel, level, string or command.
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public abstract class AMXNIDevice {

   // Instance Fields
   // ----------------------------------------------------------------------

   /**
    * Gateway we're associated with. This is the gateway we'll use to send the commands.
    */
   protected AMXNIGateway gateway;
   
   /**
    * Index of the device in devices array of AMX module.
    */
   protected Integer deviceIndex;
   
   /**
    * Commands we should update when our status changes
    */
   private List<AMXNICommand> commands;

   // Constructors ---------------------------------------------------------------------------------

   public AMXNIDevice(AMXNIGateway gateway, Integer deviceIndex) {
      this.gateway = gateway;
      this.deviceIndex = deviceIndex;
      this.commands = new ArrayList<AMXNICommand>();
   }

   // Public methods -------------------------------------------------------------------------------
   
   /**
    * Called when a feedback information is received from the AMX NI in order for this device to update its status.
    * This is implemented by each specific device to process the feedback received as appropriate for it.
    * Subclasses must then call this implementation to make sure value change is propagated to registered commands.
    * 
    * @param parameter1 First string received from the AMX NI after the device index
    * @param parameter2 Second string received from the AMX NI after the device index (comma separated from 1st string)
    */
   public void processUpdate(String parameter1, String parameter2) {
      updateCommands();
   }
   
   /**
    * Add a command to update on value change.
    * 
    * @param command AMXNICommand to add
    */
   public void addCommand(AMXNICommand command) {
      commands.add(command);
   }
   
   /**
    * Remove a command to update on value change.
    * 
    * @param command AMXNICommand to remove
    */
   public void removeCommand(AMXNICommand command) {
      commands.remove(command);
   }

   /**
    * Update all registered commands (because of a status change received from bus)
    */
   protected void updateCommands() {
      for (AMXNICommand command : commands) {
         command.updateSensors(this);
      }
   }
}
