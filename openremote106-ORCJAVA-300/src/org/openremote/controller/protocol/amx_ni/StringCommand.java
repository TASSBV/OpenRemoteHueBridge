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
import org.openremote.controller.protocol.amx_ni.model.AMXNIDeviceString;
import org.openremote.controller.utils.Logger;

/**
 * 
 * Command to be sent to an AMX device to send a string : SEND_STRING
 * Also bundles the logic to understand STRING_READ replies and update sensors accordingly.
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class StringCommand extends AMXNICommand implements ExecutableCommand, EventListener {

   /**
    * AMX NI logger. Uses a common category for all AMX NI related logging.
    */
   private final static Logger log = Logger.getLogger(AMXNICommandBuilder.AMX_NI_LOG_CATEGORY);

   public static StringCommand createCommand(String name, AMXNIGateway gateway, Integer deviceIndex, Integer channel, Integer level, String value, Integer pulseTime, String statusFilter, Integer statusFilterGroup) {
      // Check for mandatory attributes
      if (deviceIndex == null) {
        throw new NoSuchCommandException("DeviceIndex is required for any AMX command");
      }
      if ("SEND_STRING".equals(name) && (value == null)) {
         throw new NoSuchCommandException("A value is required for SEND_STRING command");
      }
      Pattern statusFilterPattern = null;
      if (statusFilter != null && !"".equals(statusFilter)) {
         if ("STRING_READ".equals(name)) {
            try {
               statusFilterPattern = Pattern.compile(statusFilter, Pattern.CANON_EQ | Pattern.UNICODE_CASE);
            } catch (PatternSyntaxException e) {
               throw new NoSuchCommandException("Invalid regular expression (" + statusFilter + ") for STRING_READ command");
            }
         } else {
            log.warn("A regular expression is only used for COMAMND_READ commands, ignoring it");
         }
      }

      return new StringCommand(name, gateway, deviceIndex, value, statusFilterPattern, statusFilterGroup);
    }

    // Private Instance Fields ----------------------------------------------------------------------

    /**
     * Value this command will send.
     */
    private String value;

    private Pattern statusFilter;
    
    private Integer statusFilterGroup;

    // Constructors ---------------------------------------------------------------------------------

    public StringCommand(String name, AMXNIGateway gateway, Integer deviceIndex, String value, Pattern statusFilter, Integer statusFilterGroup) {
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
         AMXNIDeviceString stringOrCommandDevice = (AMXNIDeviceString) gateway.getAMXNIDevice(deviceIndex, AMXNIDeviceString.class);
        if ("SEND_STRING".equals(name)) {
           stringOrCommandDevice.sendString(value);
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
              AMXNIDeviceString stringDevice = (AMXNIDeviceString) gateway.getAMXNIDevice(deviceIndex, AMXNIDeviceString.class);
              if (stringDevice == null) {
                // This should never happen as above command is supposed to create device
                log.warn("Gateway could not create a device we're receiving feedback for (" + deviceIndex + ")");
              }

              // Register ourself with the device so it can propagate update when received
              stringDevice.addCommand(this);
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
             AMXNIDeviceString stringDevice = (AMXNIDeviceString) gateway.getAMXNIDevice(deviceIndex, AMXNIDeviceString.class);
             if (stringDevice == null) {
               // This should never happen as above command is supposed to create device
                log.warn("Gateway could not create a device we're receiving feedback for (" + deviceIndex + ")");
             }

             stringDevice.removeCommand(this);
          } catch (AMXNIDeviceException e) {
             log.error("Impossible to get device", e);
          }
       }
    }

    @Override
    protected void updateSensor(AMXNIDevice device, Sensor sensor) {
       AMXNIDeviceString stringDevice = (AMXNIDeviceString)device;
       
       String processedValue = null;
       if (statusFilter != null) {
          Matcher m = statusFilter.matcher(stringDevice.getLastReadString());
          if (m.matches()) {
             if (statusFilterGroup != null) {
                String matchedGroup = m.group(statusFilterGroup);
                if (matchedGroup != null) {
                   processedValue = matchedGroup;
                }                
             } else {
                processedValue = stringDevice.getLastReadString();
             }
          } 
       } else {
          processedValue = stringDevice.getLastReadString();
       }
       if (processedValue != null) {
          updateSensorWithValue(sensor, processedValue);
       }
    }

}
