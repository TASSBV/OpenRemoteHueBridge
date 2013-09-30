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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

import org.openremote.controller.AMXNIConfig;
import org.openremote.controller.protocol.MessageQueueWithPriorityAndTTL;
import org.openremote.controller.protocol.MessageQueueWithPriorityAndTTL.Coalescable;
import org.openremote.controller.protocol.amx_ni.model.AMXNIDevice;
import org.openremote.controller.protocol.amx_ni.model.AMXNIDeviceChannels;
import org.openremote.controller.protocol.amx_ni.model.AMXNIDeviceCommand;
import org.openremote.controller.protocol.amx_ni.model.AMXNIDeviceLevels;
import org.openremote.controller.protocol.amx_ni.model.AMXNIDeviceString;
import org.openremote.controller.protocol.lutron.LutronHomeWorksDeviceException;
import org.openremote.controller.utils.Logger;

/**
 * Gateway talking to the AMX NI Controller over socket protocol.
 * 
 * Protocol exchanges command and replies as text.
 * Each line to AMX processor is terminated by <CR><LF><CR><LF>
 * Format of 1 line is <command>,<device index>,<params>
 * 
 * SEND_COMMAND,<device index>,<command> sends a command to AMX device registered at device index (SEND_COMMAND AMX instruction)
 * e.g. SEND_COMMAND,1,LUTRONKEYPADPRESS-1,14
 * 
 * SEND_STRING,<device index>,<string> sends a string to AMX device registered at device index (SEND_STRING AMX instruction)
 * e.g. SEND_STRING,1,POWER=0
 * 
 * OFF,<device index>,<channel> turns the given channel off on the AMX device registered at device index
 * e.g. OFF,1,1
 * ON,<device index>,<channel> turns the given channel on on the AMX device registered at device index
 * ON,1,1
 * PULSE,<device index>,<channel>,[<pulse time>] pulses the given channel on the AMX device registered at device index
 * <pulse time> parameter is optional. If not specified, default as per AMX program is used when command is received
 * PULSE,1,1 or PULSE,1,1,10
 * CHANNEL_STATUS,<device index>,<channel> requests the on/off status of the given channel
 * on the AMX device registered at device index
 * e.g. CHANNEL_STATUS,1,1
 * 
 * SEND_LEVEL,<device index>,<level>,<value> sets the given level to the given value
 * on the AMX device registered at device index
 * e.g. SEND_LEVEL,1,2,89
 * LEVEL_STATUS,<device index>,<level> requests the value of the given channel on the AMX device registered at device index
 * e.g. LEVEL_STATUS,1,1
 *
 *
 * AMX processors sends back following information
 * COMMAND_READ,<device index>,<command> relaying COMMAND events received on AMX device registered at device index
 * 
 * STRING_READ,<device index>,<string> relaying STRING events received on AMX device registered at device index
 * 
 * CHANNEL_STATUS,<device index>,<channel>,<status> either replying to a CHANNEL_STATUS command from OR
 * or when a CHANNEL event is received on AMX device registered at device index
 * <status> is on or off
 * 
 * LEVEL_STATUS,<device index>,<level>,<value> either replying to a LEVEL_STATUS command from OR 
 * or when a LEVEL event is received on AMX device registered at device index
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class AMXNIGateway {

   // Class Members --------------------------------------------------------------------------------

   private static final int TCP_TIMEOUT = 10000;
   private static final int COMMUNICATION_ERROR_RETRY_DELAY = 15000;

   /**
    * AMX NI logger. Uses a common category for all AMX NI related logging.
    */
   private final static Logger log = Logger.getLogger(AMXNICommandBuilder.AMX_NI_LOG_CATEGORY);

   private static HashMap<Integer, HashMap<Class<? extends AMXNIDevice>, AMXNIDevice>> deviceCache = new HashMap<Integer, HashMap<Class<? extends AMXNIDevice>, AMXNIDevice>>();

   // Don't ask this to the config factory when instantiating the bean, this
   // results in infinite recursion
   private AMXNIConfig amxConfig;

   private MessageQueueWithPriorityAndTTL<AMXCommand> queue = new MessageQueueWithPriorityAndTTL<AMXCommand>();

   private AMXConnectionThread connectionThread;

   public synchronized void startGateway() {
      if (amxConfig == null) {
         amxConfig = AMXNIConfig.readXML();
         // Check config, report error if any -> TODO: auto discovery ?
         log.info("Got AMX config");
         log.info("Address >" + amxConfig.getAddress() + "<");
         log.info("Port >" + amxConfig.getPort() + "<");
      }

      if (connectionThread == null) {
         // Starts some thread that has the responsibility to establish connection and keep it alive
         connectionThread = new AMXConnectionThread();
         connectionThread.start();
      }
   }

   public void sendCommand(String command, Integer deviceIndex, String parameter) {
      log.info("Asked to send command " + command);

      // Ask to start gateway, if it's already done, this will do nothing
      startGateway();
      queue.add(new AMXCommand(command, deviceIndex, parameter));
   }

   /**
    * Gets the AMX NI device from the cache, creating it if not already present.
    * 
    * @param deviceIndex
    * @return
    * @throws LutronHomeWorksDeviceException
    */
   public AMXNIDevice getAMXNIDevice(Integer deviceIndex, Class<? extends AMXNIDevice> deviceClass)
         throws AMXNIDeviceException {
      HashMap<Class<? extends AMXNIDevice>, AMXNIDevice> devices = deviceCache.get(deviceIndex);
      if (devices == null) {
         devices = new HashMap<Class<? extends AMXNIDevice>, AMXNIDevice>();
         deviceCache.put(deviceIndex, devices);
      }
      AMXNIDevice device = devices.get(deviceClass);
      if (device == null) {
         // No device yet in the cache, try to create one
         try {
            Constructor<? extends AMXNIDevice> constructor = deviceClass.getConstructor(AMXNIGateway.class,
                  Integer.class);
            device = constructor.newInstance(this, deviceIndex);
         } catch (SecurityException e) {
            throw new AMXNIDeviceException("Impossible to create device instance", deviceIndex, deviceClass, e);
         } catch (NoSuchMethodException e) {
            throw new AMXNIDeviceException("Impossible to create device instance", deviceIndex, deviceClass, e);
         } catch (IllegalArgumentException e) {
            throw new AMXNIDeviceException("Impossible to create device instance", deviceIndex, deviceClass, e);
         } catch (InstantiationException e) {
            throw new AMXNIDeviceException("Impossible to create device instance", deviceIndex, deviceClass, e);
         } catch (IllegalAccessException e) {
            throw new AMXNIDeviceException("Impossible to create device instance", deviceIndex, deviceClass, e);
         } catch (InvocationTargetException e) {
            throw new AMXNIDeviceException("Impossible to create device instance", deviceIndex, deviceClass, e);
         }
      }
      if (!(deviceClass.isInstance(device))) {
         throw new AMXNIDeviceException("Invalid device type found at given address", deviceIndex, deviceClass, null);
      }
      devices.put(deviceClass, device);
      return device;
   }

   // ---

   private class AMXConnectionThread extends Thread {

      private AMXReaderThread readerThread;
      private AMXWriterThread writerThread;

      @Override
      public void run() {
         Socket socket;
         while (!isInterrupted()) {
            try {
               log.info("Trying to connect to " + amxConfig.getAddress() + " on port " + amxConfig.getPort());
               socket = new Socket();
               socket.connect(new InetSocketAddress(amxConfig.getAddress(), amxConfig.getPort()), TCP_TIMEOUT);
               log.info("Socket client connected");
               readerThread = new AMXReaderThread(socket.getInputStream());
               readerThread.start();
               log.info("Reader thread asked to start");
               writerThread = new AMXWriterThread(socket.getOutputStream());
               writerThread.start();
               log.info("Writer thread asked to start");
               // Wait for the read thread to die, this would indicate the connection was dropped
               while (readerThread != null) {
                  readerThread.join(1000);
                  if (!readerThread.isAlive()) {
                     log.info("Reader thread is dead, clean and re-try to connect");
                     socket.close();
                     readerThread = null;
                     writerThread.interrupt();
                     writerThread = null;
                  }
               }
            } catch (SocketException e) {
               log.error("Connection to AMX NI impossible, sleeping and re-trying later", e);
               // We could not connect, sleep for a while before trying again
               try {
                  Thread.sleep(COMMUNICATION_ERROR_RETRY_DELAY);
               } catch (InterruptedException e1) {
                  log.warn("Interrupted during our sleep", e1);
               }
            } catch (IOException e) {
               log.error("Connection to AMX NI impossible, sleeping and re-trying later", e);
               // We could not connect, sleep for a while before trying again
               try {
                  Thread.sleep(COMMUNICATION_ERROR_RETRY_DELAY);
               } catch (InterruptedException e1) {
                  log.warn("Interrupted during our sleep", e1);
               }
            } catch (InterruptedException e) {
               log.warn("Interrupted during our sleep", e);
            }
         }
         // For now do not support discovery, use information defined in config
      }

   }

   // ---

   private class AMXWriterThread extends Thread {

      private OutputStream os;

      public AMXWriterThread(OutputStream os) {
         super();
         this.os = os;
      }

      @Override
      public void run() {

         log.info("Writer thread starting");

         PrintWriter pr = new PrintWriter(new OutputStreamWriter(os));

         while (!isInterrupted()) {
            AMXCommand cmd = null;
            try {
               cmd = queue.blockingPoll();
            } catch (InterruptedException e) {
               break;
            }
            if (cmd != null) {
               log.info("Sending >" + cmd.toString() + "< on print writer " + pr);
               // We use CRLFCRLF as message delimiter, so AMX can reconstruct and detect individual messages correctly.
               pr.print(cmd.toString() + "\r\n\r\n");
               pr.flush();
            }
         }
         log.info("Out of writer thread");
      }
   }

   // ---

   private class AMXReaderThread extends Thread {

      private InputStream is;

      public AMXReaderThread(InputStream is) {
         super();
         this.is = is;
      }

      @Override
      public void run() {
         log.info("Reader thread starting");
         log.info("Socket input stream " + is);
         BufferedReader br = new BufferedReader(new InputStreamReader(is));
         log.info("Buffered reader " + br);

         String line = null;
         try {
            line = br.readLine();
         } catch (IOException e1) {
            log.warn("Could not read from AMX NI", e1);
         }
         do {
            try {
               log.info("Reader thread got line >" + line + "<");
               // Try parsing the line as a feedback / response from the system
               AMXResponse response = parseResponse(line);
               if (response != null) {
                  if ("CHANNEL_STATUS".equals(response.command)) {
                     try {
                        AMXNIDeviceChannels channelDevice = (AMXNIDeviceChannels) getAMXNIDevice(response.deviceIndex, AMXNIDeviceChannels.class);
                        if (channelDevice != null) {
                           channelDevice.processUpdate(response.parameter1, response.parameter2);
                        }
                     } catch (AMXNIDeviceException e) {
                        log.error("Impossible to get device", e);
                     }
                  } else if ("LEVEL_STATUS".equals(response.command)) {
                     try {
                        AMXNIDeviceLevels levelDevice = (AMXNIDeviceLevels) getAMXNIDevice(response.deviceIndex, AMXNIDeviceLevels.class);
                        if (levelDevice != null) {
                           levelDevice.processUpdate(response.parameter1, response.parameter2);
                        }
                     } catch (AMXNIDeviceException e) {
                        log.error("Impossible to get device", e);
                     }
                  } else if ("STRING_READ".equals(response.command)) {
                     try {
                        AMXNIDeviceString stringDevice = (AMXNIDeviceString) getAMXNIDevice(response.deviceIndex, AMXNIDeviceString.class);
                        if (stringDevice != null) {
                           stringDevice.processUpdate(response.parameter1, response.parameter2);
                        }
                     } catch (AMXNIDeviceException e) {
                        log.error("Impossible to get device", e);
                     }
                  } else if ("COMMAND_READ".equals(response.command)) {
                     try {
                        AMXNIDeviceCommand commandDevice = (AMXNIDeviceCommand) getAMXNIDevice(response.deviceIndex, AMXNIDeviceCommand.class);
                        if (commandDevice != null) {
                           commandDevice.processUpdate(response.parameter1, response.parameter2);
                        }
                     } catch (AMXNIDeviceException e) {
                        log.error("Impossible to get device", e);
                     }
                  } else if ("ERROR".equals(response.command)) {
                     log.error("Received error from AMX module : " + response.parameter1);
                  }
               } else {
                  log.info("Received unknown information from AMX NI >" + line + "<");
               }
            } catch (Exception e) {
               log.warn("Exception not specifically handled by code has been thrown, continuing reading thread", e);
            }
            try {
               line = br.readLine();
            } catch (IOException e) {
               log.error("Could not read from AMX NI", e);
               line = null; // This will kill the reader thread and re-start the connection
            }
         } while (line != null && !isInterrupted());
         log.info("Out of reader thread");
      }
   }

   private AMXResponse parseResponse(String responseText) {
      if (responseText == null) {
         return null;
      }

      AMXResponse response = null;
      String[] parts = responseText.split(", ");
      // All the responses we currently understand have at least 3 components, all except STRING_READ, COMMAND_READ and ERROR
      // have 4
      if (parts.length > 2) {
         response = new AMXResponse();
         response.command = parts[0].trim();
         try {
            response.deviceIndex = Integer.parseInt(parts[1].trim());
         } catch (NumberFormatException e) {
            log.warn("Invalid device index received from AMX", e);
         }
         if ("STRING_READ".equals(response.command) || "COMMAND_READ".equals(response.command) || "ERROR".equals(response.command)) {
            StringBuffer temp = new StringBuffer(parts[2]);
            for (int i = 3; i < parts.length; i++) {
               temp.append(", ");
               temp.append(parts[i]);
            }
            response.parameter1 = temp.toString();
         } else {
            if (parts.length != 4) {
               return null;
            }
            response.parameter1 = parts[2].trim();
            response.parameter2 = parts[3].trim();
         }
      }
      return response;
   }

   public class AMXCommand implements Coalescable {

      private String command;
      private Integer deviceIndex;
      private String parameter;

      public AMXCommand(String command, Integer deviceIndex, String parameter) {
         this.command = command;
         this.deviceIndex = deviceIndex;
         this.parameter = parameter;
      }

      public String toString() {
         StringBuffer buf = new StringBuffer(command);
         buf.append(", ");
         buf.append(deviceIndex);
         buf.append(", ");
         buf.append(parameter);
         return buf.toString();
      }

      @Override
      public boolean isCoalesable(Coalescable other) {
         if (!(other instanceof AMXCommand)) {
            return false;
         }
         // AMXCommand otherCommand = (AMXCommand) other;

         return false;

         // TODO: if we want to be coalesable, need to have more fine grained information e.g. must have distinct
         // channel or level value

      }

   }

   private class AMXResponse {

      public String command;
      public Integer deviceIndex;
      public String parameter1;
      public String parameter2;

   }

}
