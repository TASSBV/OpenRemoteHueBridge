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
package org.openremote.controller.protocol.amx_ni;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.amx_ni.model.AMXNIDevice;
import org.openremote.controller.protocol.amx_ni.model.AMXNIDeviceCommand;
import org.openremote.controller.utils.Logger;

/**
 * Command to be sent to an AMX device to send a command : SEND_COMMAND
 * Also bundles the logic to understand COMMAND_READ replies and update sensors accordingly.
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class CommandCommand extends AMXNICommand implements ExecutableCommand, EventListener {

   /**
    * AMX NI logger. Uses a common category for all AMX NI related logging.
    */
   private final static Logger log = Logger.getLogger(AMXNICommandBuilder.AMX_NI_LOG_CATEGORY);

   public static CommandCommand createCommand(String name, AMXNIGateway gateway, Integer deviceIndex, Integer channel, Integer level, String value, Integer pulseTime, String statusFilter, Integer statusFilterGroup) {
      // Check for mandatory attributes
      if (deviceIndex == null) {
        throw new NoSuchCommandException("DeviceIndex is required for any AMX command");
      }
      if ("SEND_COMMAND".equals(name) && (value == null)) {
         throw new NoSuchCommandException("A value is required for SEND_COMMAND command");
      }
      Pattern statusFilterPattern = null;
      if (statusFilter != null && !"".equals(statusFilter)) {
         if ("COMMAND_READ".equals(name)) {
            try {
               statusFilterPattern = Pattern.compile(statusFilter, Pattern.CANON_EQ | Pattern.UNICODE_CASE);
            } catch (PatternSyntaxException e) {
               throw new NoSuchCommandException("Invalid regular expression (" + statusFilter + ") for COMMAND_READ command");
            }
         } else {
            log.warn("A regular expression is only used for COMAMND_READ commands, ignoring it");
         }
      }

      return new CommandCommand(name, gateway, deviceIndex, value, statusFilterPattern, statusFilterGroup);
    }

    // Private Instance Fields ----------------------------------------------------------------------

    /**
     * Value this command will send.
     */
    private String value;

    private Pattern statusFilter;
    
    private Integer statusFilterGroup;
    
    // Constructors ---------------------------------------------------------------------------------

    public CommandCommand(String name, AMXNIGateway gateway, Integer deviceIndex, String value, Pattern statusFilter, Integer statusFilterGroup) {
      super(name, gateway, deviceIndex);
      this.value = value;
      this.statusFilter = statusFilter;
      this.statusFilterGroup = statusFilterGroup;
    }

    // Implements ExecutableCommand -----------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void send() {
      try {
         AMXNIDeviceCommand commandDevice = (AMXNIDeviceCommand) gateway.getAMXNIDevice(deviceIndex, AMXNIDeviceCommand.class);
        if ("SEND_COMMAND".equals(name)) {
           commandDevice.sendCommand(value);
        }
      } catch (AMXNIDeviceException e) {
        log.error("Impossible to get device", e);
      }
    }

    // Implements EventListener -------------------------------------------------------------------

    @Override
    public void setSensor(Sensor sensor) {
        if (sensors.isEmpty()) {
           // First sensor registered, we also need to register ourself with the device
           try {
              AMXNIDeviceCommand commandDevice = (AMXNIDeviceCommand) gateway.getAMXNIDevice(deviceIndex, AMXNIDeviceCommand.class);
              if (commandDevice == null) {
                // This should never happen as above command is supposed to create device
                log.warn("Gateway could not create a device we're receiving feedback for (" + deviceIndex + ")");
              }

              // Register ourself with the device so it can propagate update when received
              commandDevice.addCommand(this);
              addSensor(sensor);

              // There is no initial value to read
              
              // At this stage, there is no triggering, command and string values are always send from AMX to OR
              // Might add some registration for interest on a per device basis to avoid un-necessary traffic
           } catch (AMXNIDeviceException e) {
              log.error("Impossible to get device", e);
           }
        } else {
           addSensor(sensor);
        }
    }
    
    @Override
    public void stop(Sensor sensor) {
       removeSensor(sensor);
       if (sensors.isEmpty()) {
          // Last sensor removed, we may unregister ourself from device
          try {
             AMXNIDeviceCommand commandDevice = (AMXNIDeviceCommand) gateway.getAMXNIDevice(deviceIndex, AMXNIDeviceCommand.class);
             if (commandDevice == null) {
               // This should never happen as above command is supposed to create device
                log.warn("Gateway could not create a device we're receiving feedback for (" + deviceIndex + ")");
             }

             commandDevice.removeCommand(this);
          } catch (AMXNIDeviceException e) {
             log.error("Impossible to get device", e);
          }
       }
    }

    @Override
    protected void updateSensor(AMXNIDevice device, Sensor sensor) {
       AMXNIDeviceCommand commandDevice = (AMXNIDeviceCommand)device;
       
       String processedValue = null;
       if (statusFilter != null) {
          Matcher m = statusFilter.matcher(commandDevice.getLastReadCommand());
          if (m.matches()) {
             if (statusFilterGroup != null) {
                String matchedGroup = m.group(statusFilterGroup);
                if (matchedGroup != null) {
                   processedValue = matchedGroup;
                }                
             } else {
                processedValue = commandDevice.getLastReadCommand();
             }
          } 
       } else {
          processedValue = commandDevice.getLastReadCommand();
       }
       if (processedValue != null) {
          updateSensorWithValue(sensor, processedValue);
       }
    }

}
