/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.controller.protocol.socket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;

/**
 * 
 * @author marcus
 *
 */
public class UDPListenerCommand implements EventListener {

   // Class Members --------------------------------------------------------------------------------

   /** The logger. */
   private static Logger logger = Logger.getLogger(UDPListenerCommandBuilder.UDPLISTENER_PROTOCOL_LOG_CATEGORY);

   //** A map keeping track on the listener threads for each port */
   private static Map<String, UDPListenerThread> portListenerMap = new HashMap<String, UDPListenerThread>();
   
   // Instance Fields ------------------------------------------------------------------------------
   
   /** The port the UDPSocket is created for */
   private String port;

   /** The regex which is used to find a match in the received UDP packet */
   private String regex;

   /**
    * UDPListenerCommand is a protocol to listen for UDP packets and to trigger a sensor when the packet includes the given regex
    * @param port
    * @param regex
    */
   public UDPListenerCommand(String port, String regex) {
      this.port = port;
      this.regex = regex;
   }

   @Override
   public void setSensor(Sensor sensor) {
      if (portListenerMap.containsKey(port)) {
         portListenerMap.get(port).add(regex, sensor);
      } else {
         UDPListenerThread thread = new UDPListenerThread(port);
         thread.add(regex, sensor);
         portListenerMap.put(port, thread);
         thread.start();
      }
   }

   @Override
   public void stop(Sensor sensor) {
      portListenerMap.get(port).remove(regex);
   }
   
   private class UDPListenerThread extends Thread {

      private Map<String, Sensor> regexSensorMap = new HashMap<String, Sensor>();
      
      private int port;
      
      public UDPListenerThread(String port) {
         this.port = Integer.parseInt(port);
         setName("UDPListener Port: " + port);
      }

      public void remove(String regex) {
         regexSensorMap.remove(regex);
      }

      @Override
      public void run() {
         try {
            DatagramSocket dsocket = new DatagramSocket(port);
            byte[] buffer = new byte[2048];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // Now loop forever, waiting to receive packets and evaluating them
            while (true) {
              dsocket.receive(packet);
              String msg = new String(buffer, 0, packet.getLength());
              logger.debug("Received UDP packet: " + msg);
              for (Entry<String, Sensor> entry : regexSensorMap.entrySet()) {
                 String regex = entry.getKey();
                 Sensor sensor = entry.getValue();
                 Pattern regexPattern = Pattern.compile(regex);
                 Matcher matcher = regexPattern.matcher(msg);
                 if (matcher.find()) {
                    sensor.update(""+System.currentTimeMillis());
                 }
              }

              // Reset the length of the packet before reusing it.
              packet.setLength(buffer.length);
            }
          } catch (Exception e) {
            logger.error("Error in UDPListenerThread", e);
          }
      }

      public void add(String regex, Sensor sensor) {
         regexSensorMap.put(regex, sensor);
      }

   }

}
