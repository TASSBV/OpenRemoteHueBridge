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
package org.openremote.controller.protocol.shellexe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.exec.CommandLine;
import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;

public class ShellExeCommand implements ExecutableCommand, EventListener, Runnable {

   /** The logger. */
   private static Logger logger = Logger.getLogger(ShellExeCommandBuilder.SHELLEXE_PROTOCOL_LOG_CATEGORY);

   /** The full path to executable that should be started */
   private String commandPath;

   /** The params that should be attached to the executable */
   private String commandParams;
   
   /** The regex which is used to extract sensor data from received result */
   private String regex;

   /** The polling interval which is used for the sensor update thread */
   private Integer pollingInterval;

   /** The thread that is used to peridically update the sensor */
   private Thread pollingThread;
   
   /** The map of sensors which all use the same command */
   private Map<String,Sensor> sensors;
   
   /** The ordered list of sensors'names that corresponds to regex groups */
   private String sensorNamesList;
   
   /** Boolean to indicate if polling thread should run */
   boolean doPoll = false;
   
   /**
    * ShellExeCommand is a protocol to start shell scripts on the controller
    * 
    * @param commandPath
    * @param commandParams
    */
   public ShellExeCommand(String commandPath, String commandParams, String regex, String sensorNamesList, Integer pollingInterval) {
      this.commandPath = commandPath;
      this.commandParams = commandParams;
      this.regex = regex;
      this.sensorNamesList = sensorNamesList;
      this.pollingInterval = pollingInterval;
      sensors = new HashMap<String,Sensor>();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void send() {
      executeCommand();
   }

   @Override
   public void setSensor(Sensor sensor)
   {
     logger.debug("*** setSensor called as part of EventListener init *** sensor is: " + sensor);
     if (pollingInterval == null) {
       throw new RuntimeException("Could not set sensor because no polling interval was given");
     }
     this.sensors.put(sensor.getName(), sensor);
     if (sensors.size() == 1) {
        this.doPoll = true;
        pollingThread = new Thread(this);
        pollingThread.setName("Polling thread for sensor: " + sensor.getName()+ " " +sensor.getSensorID());
        pollingThread.start();
     }
   }

   @Override
   public void stop(Sensor sensor)
   {
      this.sensors.remove(sensor);
      if (sensors.size() == 0) {
         this.doPoll = false;
      }
   }
   
   private String executeCommand() {
      logger.debug("Will start shell command: " + commandPath + " and use params: " + commandParams);
      String result = "";
      try {
         // Use the commons-exec to parse correctly the arguments, respecting quotes and spaces to separate parameters
         final CommandLine cmdLine = new CommandLine(commandPath);
         if (commandParams != null) {
            cmdLine.addArguments(commandParams);
         }
         final Process proc = Runtime.getRuntime().exec(cmdLine.toStrings());
         final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
         final StringBuffer resultBuffer = new StringBuffer();
         boolean first = true;
         for (String tmp = reader.readLine(); tmp != null; tmp = reader.readLine()) {
            if (!first) resultBuffer.append("\n");
            first = false;
            resultBuffer.append(tmp);
         }
         result = resultBuffer.toString();
      } catch (IOException e) {
         logger.error("Could not execute shell command: " + commandPath, e);
      }
      logger.debug("Shell command: " + commandPath + " returned: " + result);
      return result;
   }
   
   @Override
   public void run() {
      logger.debug("Thread started: " + pollingThread.getName());
      String[] sensorNames = null;
      if (sensorNamesList != null) {
         sensorNames = sensorNamesList.split(";");          // get an array with all sensor'names
      }
      while (doPoll) {
         String readValue = this.executeCommand();
         if ( (regex != null) && (sensorNamesList != null) ) {
           Pattern regexPattern = Pattern.compile(regex);
           Matcher matcher = regexPattern.matcher(readValue);
           if (matcher.find()) {
              for (int i = 0; i < sensorNames.length; i++) {
                 sensors.get(sensorNames[i]).update(matcher.group(i+1));
              }
           } else {
             logger.info("regex evaluation did not find a match");
             for (Sensor sensor : sensors.values()) {
               sensor.update("N/A");
            }
           }
         } else {
            for (Sensor sensor : sensors.values()) {
               sensor.update(readValue);
            }
         }
         try {
            Thread.sleep(pollingInterval); // We wait for the given pollingInterval before requesting URL again
         } catch (InterruptedException e) {
            doPoll = false;
            pollingThread.interrupt();
         }
      }
      logger.debug("*** Out of run method: " + pollingThread.getName());
   }
}
