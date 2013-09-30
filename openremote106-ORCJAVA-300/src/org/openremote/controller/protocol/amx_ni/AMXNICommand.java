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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openremote.controller.command.Command;
import org.openremote.controller.component.LevelSensor;
import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.StateSensor;
import org.openremote.controller.protocol.amx_ni.model.AMXNIDevice;
import org.openremote.controller.utils.Logger;

/**
 * Command to the AMX NI processor (through the gateway).
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public abstract class AMXNICommand implements Command {
   
   // Class Members --------------------------------------------------------------------------------

   /**
    * AMX NI logger. Uses a common category for all AMX NI related logging.
    */
   private final static Logger log = Logger.getLogger(AMXNICommandBuilder.AMX_NI_LOG_CATEGORY);

   // Keep a list of all the command strings known to OR AMX protocol and the associated command class to handle that command
   private static HashMap<String, Class<? extends AMXNICommand>> commandClasses = new HashMap<String, Class<? extends AMXNICommand>>();

   static {
      commandClasses.put("ON", ChannelCommand.class);
      commandClasses.put("OFF", ChannelCommand.class);
      commandClasses.put("PULSE", ChannelCommand.class);
      commandClasses.put("CHANNEL_STATUS", ChannelCommand.class);
      commandClasses.put("SEND_LEVEL", LevelCommand.class);
      commandClasses.put("LEVEL_STATUS", LevelCommand.class);
      commandClasses.put("SEND_COMMAND", CommandCommand.class);
      commandClasses.put("COMMAND_READ", CommandCommand.class);
      commandClasses.put("SEND_STRING", StringCommand.class);
      commandClasses.put("STRING_READ", StringCommand.class);
   }

   /**
    * Factory method for creating AMX NI command instances based on a
    * human-readable configuration strings.
    * 
    * @return new AMX NI command instance
    */
   static AMXNICommand createCommand(String name, AMXNIGateway gateway, Integer deviceIndex, Integer channel, Integer level, String value, Integer pulseTime, String statusFilter, Integer statusFilterGroup) {
    log.debug("Received request to build command with name " + name);
    
      name = name.trim().toUpperCase();
      Class<? extends AMXNICommand> commandClass = commandClasses.get(name);

      log.debug("This command maps to the command class " + commandClass);

      if (commandClass == null) {
         throw new NoSuchCommandException("Unknown command '" + name + "'.");
      }
      AMXNICommand cmd = null;
      try {
         Method method = commandClass.getMethod("createCommand", String.class, AMXNIGateway.class, Integer.class, Integer.class, Integer.class, String.class, Integer.class, String.class, Integer.class);
         log.debug("Got the creation method " + method + ", will call it");
         
         cmd = (AMXNICommand) method.invoke(null, name, gateway, deviceIndex, channel, level, value, pulseTime, statusFilter, statusFilterGroup);
         log.debug("Creation successfull, got command " + cmd);
      } catch (SecurityException e) {
         throw new NoSuchCommandException("Impossible to create command '" + name + "'.", e);
      } catch (NoSuchMethodException e) {
         throw new NoSuchCommandException("Impossible to create command '" + name + "'.", e);
      } catch (IllegalArgumentException e) {
         throw new NoSuchCommandException("Impossible to create command '" + name + "'.", e);
      } catch (IllegalAccessException e) {
         throw new NoSuchCommandException("Impossible to create command '" + name + "'.", e);
      } catch (InvocationTargetException e) {
        if (e.getCause() instanceof NoSuchCommandException) {       
          // This means method threw an exception, re-throw it as is
          throw (NoSuchCommandException)e.getCause();
        } else {
          throw new NoSuchCommandException("Impossible to create command '" + name + "'.", e);
        }
      }
      return cmd;
   }
   
   // Instance Fields ------------------------------------------------------------------------------

   /**
    * Gateway to be used to transmit this command.
    */
   protected AMXNIGateway gateway;

   /**
    * Name of the command
    */
   protected String name;
   
   /**
    * Index of device this command is for.
    */
   protected Integer deviceIndex;
   
   /**
    * Sensors we are a command of
    */
   protected List<Sensor> sensors;
   
   // Constructors ---------------------------------------------------------------------------------

   /**
    * Constructs a AMX NI command with a given gateway.
    * 
    * @param gateway AMX NI gateway instance used for transmitting this command
    */
   public AMXNICommand(String name, AMXNIGateway gateway, Integer deviceIndex) {
      this.name = name;
      this.gateway = gateway;
      this.deviceIndex = deviceIndex;
      this.sensors = new ArrayList<Sensor>();
   }
   
   /**
    * Add a sensor to update on value change.
    * 
    * @param sensor Sensor to add
    */
   public void addSensor(Sensor sensor) {
      sensors.add(sensor);
   }
   
   /**
    * Remove a sensor to update on value change.
    * 
    * @param sensor Sensor to remove
    */
   public void removeSensor(Sensor sensor) {
      sensors.remove(sensor);
   }
   
   /**
    * Update all registered sensor (because of value change on given device).
    * 
    * @param device AMXNIDevice that triggers the update
    */
   public void updateSensors(AMXNIDevice device) {
      for (Sensor sensor : sensors) {
         updateSensor(device, sensor);
      }     
   }

   /**
    * Update the specified sensor based on value of given device.
    * 
    * @param device AMXNIDevice that triggers the update
    * @param sensor Sensor to update
    */
   abstract protected void updateSensor(AMXNIDevice device, Sensor sensor);
   
   protected void updateSensorWithValue(Sensor sensor, String value) {
      if (sensor instanceof StateSensor) { // Note this includes SwitchSensor
         sensor.update(value);
      } else if (sensor instanceof RangeSensor) {
         try {
            Integer parsedValue = Integer.parseInt(value);
            if (sensor instanceof LevelSensor) {
               sensor.update(Integer.toString(Math.min(100, Math.max(0, parsedValue))));
            } else {
               sensor.update(Integer.toString(parsedValue));
            }
         } catch (NumberFormatException e){
            log.warn("Received value (" + value + ") invalid, cannot be converted to integer");
         }
      } else{
         log.warn("Query level value for incompatible sensor type (" + sensor + ")");
      }
   }

}
