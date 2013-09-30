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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Test;
import org.openremote.controller.ControllerConfiguration;

/**
 * 
 * @author Dan
 *
 */
public class MulticastAutoDiscoveryTest {

   private ControllerConfiguration configuration = ControllerConfiguration.readXML();

   @Test
   public void getMulticastSocketIP() {
      final int MULTICAST_PORT = configuration.getMulticastPort();
      final String MULTICAST_ADDRESS = configuration.getMulticastAddress();
      new Thread(new IPAutoDiscoveryServer()).start();
      new Thread(new TcpServer()).start();
      try {
         DatagramSocket socket = new DatagramSocket();
         byte[] b = new byte[512];
         DatagramPacket dgram;
         dgram = new DatagramPacket(b, b.length, InetAddress.getByName(MULTICAST_ADDRESS), MULTICAST_PORT);
         System.err.println("Sending " + b.length + " bytes to Controller at " + dgram.getAddress() + ':' + dgram.getPort());
         socket.send(dgram);
      } catch (Exception e) {
         e.printStackTrace();
      }
      
      


   }

}

class TcpServer implements Runnable{

   @Override
   public void run() {
      try {
         ServerSocket srvr = new ServerSocket(IPResponseTCPClient.TCP_PORT);
         Socket connectionSocket = srvr.accept();
         System.out.println("Detected Controller!");
         BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
         System.out.println("Received Controller URL: " + inFromClient.readLine());
         connectionSocket.close();
         srvr.close();
      }
      catch(Exception e) {
         System.out.println("Whoops! It didn't work!");
      }
      
   }
   
}
