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
package org.openremote.controller.jetty;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.openremote.controller.suite.AllTests;

/**
 * Starts Jetty embedded servlet test server, using 'web' folder of this project as war, so that we can test our REST
 * API.
 * 
 * @author Dan Cong
 * 
 */
public class JettyStart {

   private static Server server;

   public static void main(String[] args) throws Exception {
      server = new Server();

      SocketConnector connector = new SocketConnector();
      connector.setPort(AllTests.WEBAPP_PORT);
      server.setConnectors(new Connector[] { connector });
      WebAppContext context = new WebAppContext();
      context.setServer(server);
      context.setContextPath("/controller");
      context.setWar(".");
      server.addHandler(context);
      Thread monitor = new MonitorThread();
      monitor.start();
      server.start();
      server.join();
   }

   private static class MonitorThread extends Thread {

      private ServerSocket socket;

      public MonitorThread() {
         setDaemon(true);
         setName("StopMonitor");
         try {
            socket = new ServerSocket(8079, 1, InetAddress.getByName("127.0.0.1"));
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      @Override
      public void run() {
         System.out.println("*** running jetty 'stop monitor' thread");
         Socket accept;
         try {
            accept = socket.accept();
            BufferedReader reader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
            reader.readLine();
            System.out.println("*** stopping jetty embedded server");
            server.stop();
            accept.close();
            socket.close();
         } catch (Exception e) {
            throw new RuntimeException(e);
         } finally {
            System.exit(0);
         }
      }
   }

}
