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
import java.net.InetAddress;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.utils.NetworkUtil;

/**
 * The Class TCPClient.
 * 
 * @author Dan 2009-6-1
 */
public class IPResponseTCPClient implements Runnable {
   
   /** The logger. */
   private static Logger logger = Logger.getLogger(IPResponseTCPClient.class.getName());
   
   /** The target ip. */
   private InetAddress targetIP;
   
   /** The TCP port. */
   public final static int TCP_PORT = 2346;
   
   /** The configuration. */
   private ControllerConfiguration configuration = ControllerConfiguration.readXML();
   
   

   /**
    * Instantiates a new tCP client.
    * 
    * @param targetIP the target ip
    */
   public IPResponseTCPClient(InetAddress targetIP) {
      super();
      this.targetIP = targetIP;
   }

   /* (non-Javadoc)
    * @see java.lang.Runnable#run()
    */
   @Override
   public void run() {
      sendTcp();
   }
   
   /**
    * Send tcp.
    */
   public void sendTcp() {
      String targetIPStr = targetIP.getHostAddress();
      String data = "http://" + NetworkUtil.getLocalhostIP() + ":" + configuration.getWebappPort() + "/"
            + configuration.getWebappName();
      logger.info("Sending server IP '" + data + "' to " + targetIPStr);
      Socket skt = null;
      PrintWriter out = null;
      try {
         skt = new Socket(targetIP, TCP_PORT);
         out = new PrintWriter(skt.getOutputStream(), true);
      } catch (IOException e) {
         logger.error("Response failed! Can't create TCP socket on " + targetIPStr, e);
      } finally {
         out.print(data);
         out.close();
         try {
            skt.close();
         } catch (IOException e) {
            logger.error("Can't close socket", e);
         }
      }

   }
}
