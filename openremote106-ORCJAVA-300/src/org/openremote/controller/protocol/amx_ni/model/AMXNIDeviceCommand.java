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

import org.openremote.controller.protocol.amx_ni.AMXNICommandBuilder;
import org.openremote.controller.protocol.amx_ni.AMXNIGateway;
import org.openremote.controller.utils.Logger;

/**
 * Proxy to all commands on a given AMX device.
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class AMXNIDeviceCommand extends AMXNIDevice {

   /**
    * AMX NI logger. Uses a common category for all AMX NI related logging.
    */
   private final static Logger log = Logger.getLogger(AMXNICommandBuilder.AMX_NI_LOG_CATEGORY);

   private String lastReadCommand;
   
   // Constructors ---------------------------------------------------------------------------------

   public AMXNIDeviceCommand(AMXNIGateway gateway, Integer deviceIndex) {
      super(gateway, deviceIndex);
   }

   // Command methods ------------------------------------------------------------------------------

   public void sendCommand(String value) {
      this.gateway.sendCommand("SEND_COMMAND", deviceIndex, value);
   }

   // Feedback method from AMXNIDevice ---------------------------------------------------------

   @Override
   public void processUpdate(String parameter1, String parameter2) {
      log.info("Will update read command for device (" + deviceIndex + ") with strings " + parameter1 + " , " + parameter2);

      lastReadCommand = parameter1;
      
      super.processUpdate(parameter1, parameter2);
   }

   public String getLastReadCommand() {
      return lastReadCommand;
   }

}
