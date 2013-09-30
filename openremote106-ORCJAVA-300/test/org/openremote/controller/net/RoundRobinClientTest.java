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
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.RoundRobinConfiguration;
import org.openremote.controller.exception.roundrobin.TCPClientEstablishException;
import org.openremote.controller.exception.roundrobin.UDPServerStartFailException;

/**
 * All usecase test for RoundRobin Client. <br /><br />
 * 
 * This RoundRobinClient is responsible for <b>START UP TCP SERVER</b> firstly,<br /> 
 * and then <b>SEND UDP MULTICAST REQUEST</b> to discover groupmember.
 * 
 * @author Handy.Wang 2009-12-22
 */
public class RoundRobinClientTest {

   private ControllerConfiguration configuration = ControllerConfiguration.readXML();
   private RoundRobinConfiguration roundRobinConfig = RoundRobinConfiguration.readXML();
   private Logger logger = Logger.getLogger(this.getClass().getName());
   private List<MulticastSocket> udpMulticastServerSockets = new ArrayList<MulticastSocket>();
   
   private static final String CONTROLLER_URL_PROTOCOL_HEADER = "http://";
   private static final String CONTROLLER_URL_SEPARATOR = "/";
   
   @After
   public void tearDown() {
      shutdownSimulateUDPServer();
   }
   
   @Test
   public void testStartupUDPServer() throws Exception {
      simulateUDPServer("A");
      simulateUDPServer("A");
      simulateUDPServer("A");
      simulateUDPServer("B");
      simulateUDPServer("B");
      nap(2000);
   }
   
   @Test
   public void testConcurrentRoundRobinClient() {
      new Thread(){
         @Override
         public void run() {
            RoundRobinClient rrc = new RoundRobinClient();
            for(String s : rrc.getGroupMemberURLsSet()) {
               logger.info("####################" +s);
            }
         }
      }.start();
      nap(100);
      new Thread(){
         @Override
         public void run() {
            RoundRobinClient rrc = new RoundRobinClient();
            for(String s : rrc.getGroupMemberURLsSet()) {
               logger.info("####################" +s);
            }
         }
      }.start();
      
      nap(10000);
   }

   private void nap(long time) {
      try {
         Thread.sleep(time);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

   //######################        Simulations      ######################
   /** Simulate UDP Server */
   private void simulateUDPServer(String groupName) {
    new Thread(new UDPServerThread(groupName)).start();
   }
   
   /** shut down udpMulticastServerSocket */
   private void shutdownSimulateUDPServer() {
      for(MulticastSocket multicastSocket: udpMulticastServerSockets) {
         multicastSocket.close();
      }
      logger.info("UDP Multicast Server sockets are shutdown by JUnit method teardown.");
   }
   
   /**
    * Simulte thread of UDP server which receive RoundRobin MultiCase request.
    * 
    * @author Handy.Wang 2009-12-22
    */
   class UDPServerThread implements Runnable {
      
      private String groupName;
      
      public UDPServerThread(String groupName) {
         this.groupName = groupName;
      }
      
      @Override
      public void run() {
         try {
            MulticastSocket udpMulticastServerSocket = null;
            try {
               udpMulticastServerSocket = new MulticastSocket(roundRobinConfig.getRoundRobinMulticastPort());
               udpMulticastServerSocket.joinGroup(InetAddress.getByName(roundRobinConfig.getRoundRobinMulticastAddress()));
               udpMulticastServerSockets.add(udpMulticastServerSocket);
            } catch (Exception e) {
               TestCase.fail("Simulate UDP Server : Startup roundRobin UDP multicast serversocket fail.");
               throw new UDPServerStartFailException("Simulate UDP Server : Startup roundRobin UDP multicast serversocket fail.", e);
            }
            byte[] buffer = new byte[1024];
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            logger.info("UDP server is on and waiting multicast request...");
            while(true) {
               try {
                  udpMulticastServerSocket.receive(datagramPacket);
               } catch (SocketException e) {
                  logger.info("UDPMulticastServerSocket can't receive data anymore for it is shut down by JUnit method teardown.");
                  break;
               }
               Thread dealThread = new Thread(new UDPServerDealThread(groupName, datagramPacket));
               dealThread.start();
               nap(100);
            }
         } catch (IOException e) {
            TestCase.fail("Simulate UDP Server : Startup roundRobin UDP multicast serversocket fail.");
            throw new UDPServerStartFailException("Simulate UDP Server : Startup roundRobin UDP multicast serversocket fail.", e);
         }
      }
      
   }
   
   /**
    * Simulate thread of UDP server deal the datagramPacket from RoundRobin MultiCast request. 
    * 
    * @author Handy.Wang 2009-12-22
    */
   class UDPServerDealThread implements Runnable {
      
      private String groupName;
      private DatagramPacket datagramPacket;
      
      public UDPServerDealThread(String groupName, DatagramPacket datagramPacket) {
         this.groupName = groupName;
         this.datagramPacket = datagramPacket;
      }
      
      @Override
      public void run() {
         String clientControllerGroupName = new String(datagramPacket.getData()).trim();
         String expectedClientControllerGroupName = clientControllerGroupName;
         String controllerURL = null;
         if (groupName.equalsIgnoreCase(clientControllerGroupName)) {
            logger.info("UDP Server : Received the controller multicast request from the same group " + clientControllerGroupName);
            Socket tcpClientSocket = null;
            PrintWriter printWriter = null;
            try {
               tcpClientSocket = new Socket(datagramPacket.getAddress(), roundRobinConfig.getRoundRobinTCPServerSocketPort());
               printWriter = new PrintWriter(tcpClientSocket.getOutputStream(), true);
            } catch (Exception e) {
               TestCase.fail("Established TCP Client socket fail.");
               throw new TCPClientEstablishException("Established TCP Client socket fail.");
            }
            controllerURL = CONTROLLER_URL_PROTOCOL_HEADER + getLocalHostIP() + ":" + configuration.getWebappPort() + CONTROLLER_URL_SEPARATOR + roundRobinConfig.getControllerApplicationName();
            printWriter.println(controllerURL);
            printWriter.close();
            try {
               tcpClientSocket.close();
            } catch (IOException e) {
               logger.error("Close tcpClientSocket exception", e);
            }
         } else {
            logger.info("The client controller groupname " + clientControllerGroupName + " doesn't equals self-groupname " + groupName);
         }
         logger.info("UDP Server : self controller url : " + controllerURL);
         logger.info("Actual clientController GroupName is : " + clientControllerGroupName);
         Assert.assertTrue("Exepcted " + expectedClientControllerGroupName + " but " + clientControllerGroupName,
               expectedClientControllerGroupName.equals(clientControllerGroupName));
      }
      
      private String getLocalHostIP() {
         String A = (int)(Math.random()*255)+".";
         String B = (int)(Math.random()*255)+".";
         String C = (int)(Math.random()*255)+".";
         String D = (int)(Math.random()*255)+"";
         return A+B+C+D;
      }
   }
}
