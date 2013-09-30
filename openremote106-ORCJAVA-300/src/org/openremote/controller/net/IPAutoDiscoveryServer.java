/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.apache.log4j.Logger;
import org.openremote.controller.ControllerConfiguration;

/**
 * The Class IP Auto Discovery Server.
 * This server will listen to all the clients on a multicast address.
 * After a client sends a UDP packet to this server with its IP,it will create a TCP server to receive the response.
 * Then this server will respond its HTTP URL to the client via TCP connection.
 * 
 * @author Dan 2009-5-18
 */
public class IPAutoDiscoveryServer implements Runnable {
   
   /** The logger. */
   private static Logger logger = Logger.getLogger(IPAutoDiscoveryServer.class.getName());
   
   /** The configuration. */
   private ControllerConfiguration configuration = ControllerConfiguration.readXML();
   

   /**
    * {@inheritDoc}
    */
   public void run() {
      final int MULTICAST_PORT = configuration.getMulticastPort();
      final String MULTICAST_ADDRESS = configuration.getMulticastAddress();
      String multicastLocation = MULTICAST_ADDRESS + ":" + MULTICAST_PORT;
      MulticastSocket socket = null;
      InetAddress address = null;
      try {
         socket = new MulticastSocket(MULTICAST_PORT);
         address = InetAddress.getByName(MULTICAST_ADDRESS);
         logger.info("Created IP discover multicast server !");
      } catch (IOException e) {
         logger.error("Can't create multicast socket on " + multicastLocation, e);
      }
      try {
         socket.joinGroup(address);
         logger.info("Joined a group : "+multicastLocation);
      } catch (IOException e) {
         logger.error("Can't join group of " + multicastLocation, e);
      }
      byte[] buf = new byte[512];
      DatagramPacket packet = new DatagramPacket(buf, buf.length);
      while (true) {
         try {
            logger.info("Listening on  " + multicastLocation);
            socket.receive(packet);
            logger.info("Received an IP auto-discovery request from " + packet.getAddress().getHostAddress());
         } catch (IOException e) {
            logger.error("Can't receive packet on " + MULTICAST_ADDRESS + ":" + MULTICAST_PORT, e);
         }
         sendLocalIPBack(packet); 
      }
   }



   /**
    * Send local ip back.
    * 
    * @param packet the packet
    */
   private void sendLocalIPBack(DatagramPacket packet) {
      new Thread(new IPResponseTCPClient(packet.getAddress())).start();
   }



   /**
    * Sets the configuration.
    * 
    * @param configuration the new configuration
    */
   public void setConfiguration(ControllerConfiguration configuration) {
      this.configuration = configuration;
   }
   

}
