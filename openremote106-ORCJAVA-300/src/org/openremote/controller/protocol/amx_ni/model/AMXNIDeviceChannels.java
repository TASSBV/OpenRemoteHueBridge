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

import java.util.HashMap;
import java.util.Map;

import org.openremote.controller.protocol.amx_ni.AMXNICommandBuilder;
import org.openremote.controller.protocol.amx_ni.AMXNIGateway;
import org.openremote.controller.utils.Logger;

/**
 * Proxy to all channels on a given AMX device.
 *  
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class AMXNIDeviceChannels extends AMXNIDevice {

   /**
    * AMX NI logger. Uses a common category for all AMX NI related logging.
    */
   private final static Logger log = Logger.getLogger(AMXNICommandBuilder.AMX_NI_LOG_CATEGORY);

   // Private Instance Fields ----------------------------------------------------------------------

   /**
    * Current status of channels, as reported by the system. No entry in the array if we don't have this info.
    */
   private Map<Integer, Boolean> channelsStatuses = new HashMap<Integer, Boolean>();

   // Constructors ---------------------------------------------------------------------------------

   public AMXNIDeviceChannels(AMXNIGateway gateway, Integer deviceIndex) {
      super(gateway, deviceIndex);
   }

   // Command methods ------------------------------------------------------------------------------

   public void on(Integer channel) {
      this.gateway.sendCommand("ON", deviceIndex, Integer.toString(channel));
   }

   public void off(Integer channel) {
      this.gateway.sendCommand("OFF", deviceIndex, Integer.toString(channel));
   }

   public void pulse(Integer channel, Integer pulseTime) {
      if (pulseTime != null) {
         this.gateway.sendCommand("PULSE", deviceIndex, Integer.toString(channel) + "," + Integer.toString(pulseTime));
      } else {
         this.gateway.sendCommand("PULSE", deviceIndex, Integer.toString(channel));
      }
   }

   public void queryChannelStatus(Integer channel) {
      this.gateway.sendCommand("CHANNEL_STATUS", deviceIndex, Integer.toString(channel));
   }

   // Feedback method from AMXNIDevice ---------------------------------------------------------

   @Override
   public void processUpdate(String parameter1, String parameter2) {
      log.info("Will update channel for device (" + deviceIndex + ") with strings " + parameter1 + " , " + parameter2);

      Integer channel = Integer.parseInt(parameter1);
      channelsStatuses.put(channel, "on".equals(parameter2));
      super.processUpdate(parameter1, parameter2);
   }

   public Boolean getChannelStatus(Integer channel) {
      return channelsStatuses.get(channel);
   }
}
