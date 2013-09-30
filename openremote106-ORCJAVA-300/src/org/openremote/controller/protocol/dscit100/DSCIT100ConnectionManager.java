/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.protocol.dscit100;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class DSCIT100ConnectionManager
{

  // Class Members
  // --------------------------------------------------------------------------------

  /**
   * DSCIT100 logger. Uses a common category for all DSCIT100 related logging.
   */
  private final Logger log = Logger
      .getLogger(DSCIT100CommandBuilder.DSCIT100_LOG_CATEGORY);

  /**
   * Connection timeout for DSCIT100 IP socket
   */
  private final static int IP_CONNECT_TIMEOUT = 5000;

  /**
   * Default TCP port if none is provided in command definition
   */
  private final static int DEFAULT_TCP_PORT = 5000;

  // Instance Fields
  // ------------------------------------------------------------------------------

  /**
   * Map containing all DSCIT100Connection objects using the connection address
   * as the key
   */
  public Map<String, DSCIT100Connection> connections;

  // Constructors
  // ---------------------------------------------------------------------------------

  public DSCIT100ConnectionManager()
  {
    connections = new ConcurrentHashMap<String, DSCIT100Connection>();

    // add shutdown hook in an attempt to avoid leaving open connections behind
    log.debug("Adding shutdown hook to manage unclosed DSC IT100 connections in case of controller exit.");
    addShutdownHook();
  }

  // Protected Instance Methods
  // -------------------------------------------------------------------

  protected synchronized DSCIT100Connection getConnection(String address)
      throws Exception
  {
    // If connection exists, return it
    if (connections.containsKey(address) && connections.get(address) != null
        && connections.get(address).isConnected())
      return connections.get(address);

    // TODO: Parse the address to determine if it's an IP or a
    // serial port and call the buildXXX method accordingly

    // Couldn't find an exiting connection, so try to build one
    buildIPConnection(address);

    // If connection exists, return it, else return null
    if (connections.containsKey(address) && connections.get(address) != null
        && connections.get(address).isConnected())
      return connections.get(address);
    else
      return null;
  }

  // Private Instance Methods
  // ---------------------------------------------------------------------

  /**
   * Build an IP connection to a DSC IT-100 and adds a connection object to the
   * connections <code>Map</code>
   * 
   * @param address
   *          IT-100 IP address and TCP port (x.x.x.x:xxxx)
   * @throws NumberFormatException
   *           Address or port cannot be parsed
   * @throws UnknownHostException
   *           Host is not found
   * @throws SocketTimeoutException
   *           Timeout connecting to host
   * @throws IOException
   *           General socket I/O error
   */
  private synchronized void buildIPConnection(String address) throws Exception
  {
    String[] arrAddress = address.split(":", 2);
    String host = arrAddress[0];

    int port = DEFAULT_TCP_PORT;
    if (arrAddress.length == 2)
    {
      try
      {
        port = Integer.parseInt(arrAddress[1]);
      }
      catch (NumberFormatException e)
      {
        log.error("Invalid TCP port specified in command definition : "
            + arrAddress);
        throw e;
      }
    }

    Socket socket = null;
    try
    {

      log.debug("Creating new socket for host : " + address);
      socket = new Socket();
      SocketAddress socketAddress = new InetSocketAddress(host, port);

      socket.connect(socketAddress, IP_CONNECT_TIMEOUT);

      this.connections.put(address, new IpConnection(socket));
    }
    catch (UnknownHostException e)
    {
      log.error("Unknown host : " + address, e);
    }
    catch (SocketTimeoutException e)
    {
      log.warn("Timeout connecting to host : " + address, e);
    }
    catch (IOException e)
    {
      log.error("Couldn't get I/O for the connection", e);
    }
  }

  /**
   * Registers a shutdown hook in the JVM to attempt to close any open IT100
   * connections when JVM process is killed.
   * 
   * Adding shutdown hook in a privileged code block -- as long as calling code
   * has sufficient security permissions, we don't require additional
   * permissions for this operation.
   */
  private void addShutdownHook()
  {
    try
    {
      final Thread shutdown = new Thread(new Shutdown());

      // BEGIN PRIVILEGED CODE BLOCK
      // --------------------------------------------------------------
      AccessController.doPrivileged(new PrivilegedAction<Void>()
      {
        public Void run()
        {
          Runtime.getRuntime().addShutdownHook(shutdown);

          return null;
        }
      });
      // END PRIVILEGED CODE BLOCK
      // ----------------------------------------------------------------

    }
    catch (SecurityException exception)
    {
      log.warn(
          "Cannot register shutdown hook. Most likely due to lack of security permissions "
              + "in the JVM security manager. DSC IT100 connection manager service will operate normally "
              + "but may be unable to clean up all the connection resources in case of an unexpected "
              + "shutdown (security exception: " + exception.getMessage() + ")",
          exception);
    }
    catch (IllegalStateException exception)
    {
      log.error(
          "Unable to register shutdown hook due to illegal state exception ("
              + exception.getMessage()
              + "). This may be due to the JVM already starting the "
              + "shutdown process.", exception);
    }
  }

  // Inner Classes
  // --------------------------------------------------------------------------------

  /**
   * Implements shutdown hook for the DSC IT100 connection manager.
   */
  private class Shutdown implements Runnable
  {
    public void run()
    {
      for (String key : connections.keySet())
      {
        try
        {
          log.debug("Executing JVM shutdown hook to close DSC IT100 connections...");

          DSCIT100Connection connection = connections.get(key);
          if (connection != null)
          {
            String connectionName = connection.getAddress();

            StringBuilder builder = new StringBuilder(256)
                .append("Closing connection (name = ").append(connectionName)
                .append(")");

            log.info(builder.toString());

            connection.close();

            log.info("Connection closed.");
          }
        }
        catch (Throwable t)
        {
          log.error("Closing connection failed: " + t.getMessage(), t);
        }
      }
    }
  }

}
