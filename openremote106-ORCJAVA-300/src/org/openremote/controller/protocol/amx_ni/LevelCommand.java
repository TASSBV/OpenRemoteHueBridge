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

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.component.LevelSensor;
import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.amx_ni.model.AMXNIDevice;
import org.openremote.controller.protocol.amx_ni.model.AMXNIDeviceLevels;
import org.openremote.controller.utils.Logger;

/**
 * Command to be sent to a level to actuate or query status.
 * This includes : SEND_LEVEL, LEVEL_READ
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class LevelCommand extends AMXNICommand implements ExecutableCommand, EventListener {

   /**
    * AMX NI logger. Uses a common category for all AMX NI related logging.
    */
   private final static Logger log = Logger.getLogger(AMXNICommandBuilder.AMX_NI_LOG_CATEGORY);

   public static LevelCommand createCommand(String name, AMXNIGateway gateway, Integer deviceIndex, Integer channel, Integer level, String value, Integer pulseTime, String statusFilter, Integer statusFilterGroup) {
      Integer lValue = null;
      
      // Check for mandatory attributes
      if (deviceIndex == null) {
        throw new NoSuchCommandException("DeviceIndex is required for any AMX command");
      }
      if (level == null) {
         throw new NoSuchCommandException("Level is required for any AMX command working with levels");
      }
      if ("SEND_LEVEL".equals(name)) {
         if (value == null) {
            throw new NoSuchCommandException("Level value is required for SEND_LEVEL command");
         }
         try {
            lValue = Integer.parseInt(value);
         } catch (NumberFormatException e) {
            throw new NoSuchCommandException("Level value must be numeric for SEND_LEVEL command");
         }
      }

      return new LevelCommand(name, gateway, deviceIndex, level, lValue);
    }

    // Private Instance Fields ----------------------------------------------------------------------

    /**
     * Level this command will act on.
     */
    private Integer level;

    /**
     * Value to set this level to.
     */
    private Integer levelValue;
    
    // Constructors ---------------------------------------------------------------------------------

    public LevelCommand(String name, AMXNIGateway gateway, Integer deviceIndex, Integer level, Integer levelValue) {
      super(name, gateway, deviceIndex);
      this.level = level;
      this.levelValue = levelValue;
    }

    // Implements ExecutableCommand -----------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void send() {
      try {
        AMXNIDeviceLevels levelDevice = (AMXNIDeviceLevels) gateway.getAMXNIDevice(deviceIndex, AMXNIDeviceLevels.class);
        if ("SEND_LEVEL".equals(name)) {
           levelDevice.sendLevel(level, levelValue);
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
              AMXNIDeviceLevels levelDevice = (AMXNIDeviceLevels) gateway.getAMXNIDevice(deviceIndex, AMXNIDeviceLevels.class);
              if (levelDevice == null) {
                // This should never happen as above command is supposed to create device
                log.warn("Gateway could not create a device we're receiving feedback for (" + deviceIndex + ")");
              }

              // Register ourself with the device so it can propagate update when received
              levelDevice.addCommand(this);
              addSensor(sensor);

              // Trigger a query to get the initial value
              levelDevice.queryLevelStatus(level);
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
             AMXNIDeviceLevels levelDevice = (AMXNIDeviceLevels) gateway.getAMXNIDevice(deviceIndex, AMXNIDeviceLevels.class);
             if (levelDevice == null) {
               // This should never happen as above command is supposed to create device
                log.warn("Gateway could not create a device we're receiving feedback for (" + deviceIndex + ")");
             }

             levelDevice.removeCommand(this);
          } catch (AMXNIDeviceException e) {
             log.error("Impossible to get device", e);
          }
       }
    }

    @Override
    protected void updateSensor(AMXNIDevice device, Sensor sensor) {
       AMXNIDeviceLevels levelDevice = (AMXNIDeviceLevels)device;
       Integer levelValue = levelDevice.getLevelValue(level);
       if (levelValue != null) {
          if (sensor instanceof RangeSensor) {
             sensor.update(Integer.toString(levelValue));
           } else if (sensor instanceof LevelSensor) {
              // We don't know the actual range of the sensor, so we just "clip it" to 0-100 range before passing to sensor
              sensor.update(Integer.toString(Math.min(100, Math.max(0, levelValue))));
           } else {
             log.warn("Query level value for incompatible sensor type (" + sensor + ")");
           }
       }
    }
}
