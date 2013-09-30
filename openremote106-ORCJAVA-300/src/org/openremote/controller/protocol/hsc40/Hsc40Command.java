/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2011, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.hsc40;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.utils.Logger;

/**
 *
 * @author Marcus
 */
public class Hsc40Command implements EventListener, ExecutableCommand {

   // Class Members --------------------------------------------------------------------------------

   private final static Logger logger = Logger.getLogger(Hsc40CommandBuilder.HSC40_PROTOCOL_LOG_CATEGORY);

   // Instance Fields ------------------------------------------------------------------------------

   private String deviceId;
   private String deviceType;
   private String portId;
   private String command;
   private String paramValue;
   private Hsc40IpClient ipClient;


   // Implements StatusCommand ---------------------------------------------------------------------

   public Hsc40Command(String command, String paramValue, String deviceId, String deviceType, String portId, Hsc40IpClient ipClient) {
      this.command = command;
      this.paramValue = paramValue;
      
      this.ipClient = ipClient;
      if (deviceId.startsWith("0x")) {
         deviceId = deviceId.substring(2);
      }
      if (portId.startsWith("0x")) {
         portId = portId.substring(2);
      }
      this.deviceId = deviceId;
      this.portId = portId;
      this.deviceType = deviceType;
   }

   

   @Override
   public void send() {
      String data = "";
      if (command.equalsIgnoreCase("on")) {
         if (deviceType.equalsIgnoreCase("dimmer")) {
            data = "63";   //Hex-Value for dimmer value '99'
         } else {
            data = "ff";   //Hex-Value for light 'on'
         }
      } else if (command.equalsIgnoreCase("off")) {
         data = "00";  //Hex-Value for light/dimmer 'off'
      } else if (command.equalsIgnoreCase("dim")) {
         data = Integer.toHexString(Integer.parseInt(paramValue));
      } else if (command.equalsIgnoreCase("dim_up")) {
         data = "80";   //Hex-Value for '128' -> dim_up
      } else if (command.equalsIgnoreCase("dim_down")) {
         data = "81";   //Hex-Value for '129' -> dim_down
      } else if (command.equalsIgnoreCase("dim_stop")) {
         data = "82";   //Hex-Value for '130' -> dim_stop
      } else if (command.equalsIgnoreCase("status")) {
         throw new RuntimeException("should not be called");
      }
      ipClient.sendBasicSetCommand(data, deviceId, portId);
   }


   @Override
   public void setSensor(Sensor sensor) {
      logger.debug("*** setSensor called as part of EventListener init *** sensor is: " + sensor);
      ZWaveDevice device = new ZWaveDevice(deviceId, portId, deviceType, sensor);
      ipClient.addStatusDevice(device);
   }



   @Override
   public void stop(Sensor sensor) {
      //TODO
   }

}
