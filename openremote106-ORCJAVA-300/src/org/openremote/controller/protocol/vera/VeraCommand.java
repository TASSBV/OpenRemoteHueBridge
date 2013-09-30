/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.controller.protocol.vera;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.vera.model.Dimmer;
import org.openremote.controller.protocol.vera.model.Switch;
import org.openremote.controller.protocol.vera.model.Thermostat;
import org.openremote.controller.protocol.vera.model.VeraDevice;
import org.openremote.controller.utils.Logger;

/**
 * 
 * @author Marcus
 */
public class VeraCommand implements EventListener, ExecutableCommand {

   // Class Members --------------------------------------------------------------------------------
   private final static Logger logger = Logger.getLogger(VeraCommandBuilder.VERA_PROTOCOL_LOG_CATEGORY);

   // Instance Fields ------------------------------------------------------------------------------
   private int deviceId;
   private VeraCmd command;
   private String paramValue;
   private VeraClient client;
   private String serviceId = null;
   private String action = null;
   private String variable = null;
   private String statusAttribute = null;

   // Constructor ---------------------------------------------------------------------
   public VeraCommand(int deviceId, VeraCmd command, String paramValue, VeraClient client, String serviceId, String action, String variable, String statusAttribute) {
      this.client = client;
      this.deviceId = deviceId;
      this.command = command;
      this.paramValue = paramValue;
      this.serviceId = serviceId;
      this.action = action;
      this.variable = variable;
      this.statusAttribute = statusAttribute;
   }

   @Override
   public void send() {
      switch (command) {
         case ON:
            ((Switch)client.getDevice(deviceId)).turnOn();
            break;
         case OFF:
            ((Switch)client.getDevice(deviceId)).turnOff();
            break;
         case SET_LEVEL:
            ((Dimmer)client.getDevice(deviceId)).setLevel(paramValue);
            break;
         case SET_HEAT_SETPOINT:
            ((Thermostat)client.getDevice(deviceId)).setHeatSetpoint(paramValue);
            break;
         case GENERIC_ACTION:
            client.getDevice(deviceId).executeGenericAction(serviceId, action, variable, paramValue);
            break;
      }
   }

   @Override
   public void setSensor(Sensor sensor) {
      logger.debug("*** setSensor called as part of EventListener init *** sensor is: " + sensor);
      VeraDevice device = client.getDevice(deviceId);
      if ((command == VeraCmd.GENERIC_STATUS) && (statusAttribute != null)) {
         device.addGenericSensor(statusAttribute, sensor);
      } else {
         device.addSensor(command, sensor);
      }
   }

   @Override
   public void stop(Sensor sensor) {
      VeraDevice device = client.getDevice(deviceId);
      device.removeSensor(sensor);
   }

}
