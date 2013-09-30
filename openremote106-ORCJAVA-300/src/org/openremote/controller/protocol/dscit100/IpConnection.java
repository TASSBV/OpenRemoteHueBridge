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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.openremote.controller.protocol.dscit100.Packet.PacketCallback;
import org.openremote.controller.protocol.dscit100.PanelState.State;

/**
 * Implements a DSCIT100Connection over IP
 *
 */
public class IpConnection implements DSCIT100Connection
{
  // Class Members
  // --------------------------------------------------------------------------------

  /**
   * DSCIT100 logger. Uses a common category for all DSCIT100 related logging.
   */
  private final Logger log = Logger
      .getLogger(DSCIT100CommandBuilder.DSCIT100_LOG_CATEGORY);

  private Socket socket = null;
  private PrintWriter out = null;
  private IpListener listener;
  protected PacketCallback packetCallback;

  /**
   * @param socket An IP socket
   */
  public IpConnection(Socket socket)
  {
    this.socket = socket;

    listener = new IpListener(socket);

    try
    {
      this.out = new PrintWriter(socket.getOutputStream(), true);
    }
    catch (IOException e)
    {
      log.error("Error creating socket output stream for "
          + socket.getInetAddress().getHostAddress(), e);
    }
  }

  @Override
  public void send(ExecuteCommand command)
  {
    sendInternal(command.getPacket());
  }

  @Override
  public void send(Packet packet)
  {
    sendInternal(packet);
  }

  private void sendInternal(Packet packet)
  {
    if (isConnected())
    {
      log.debug("Sending data to address "
          + socket.getInetAddress().getHostAddress() + " : "
          + packet.toPacket());
      if (packet.getCallback() != null)
        this.packetCallback = packet.getCallback();
      out.println(packet.toPacket());
    }
    else
    {
      log.warn("Could not send data to address "
          + socket.getInetAddress().getHostAddress() + " : "
          + packet.toPacket());
    }
  }

  @Override
  public boolean isConnected()
  {
    return socket.isConnected();
  }

  @Override
  public void close()
  {
    log.debug("Closing connection to "
        + socket.getInetAddress().getHostAddress());
    try
    {
      socket.close();
    }
    catch (IOException e)
    {
      log.warn("Error closing connection to "
          + socket.getInetAddress().getHostAddress(), e);
    }
  }

  @Override
  public String getAddress()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(socket.getInetAddress().getHostAddress());
    sb.append(":");
    sb.append(socket.getPort());
    return sb.toString();
  }

  @Override
  public State getState(StateDefinition stateDefinition)
  {
    if (listener != null && listener.state != null)
      return listener.state.getState(stateDefinition);
    else
    {
      log.warn("Unable to get connection listener or listener state database is unavailable for connection to "
          + socket.getInetAddress().getHostAddress());
      return null;
    }
  }

  /**
   * A listener <code>Thread</code> for an <code>IpConnection</code>
   *
   */
  private class IpListener implements Runnable
  {
    private Thread thread;
    private Socket socket;

    public PanelState state;

    public IpListener(Socket socket)
    {
      this.socket = socket;
      this.state = new PanelState();

      thread = new Thread(this);
      thread.start();
    }

    @Override
    public void run()
    {
      log.info("Starting connection listener thread for "
          + socket.getInetAddress().getHostAddress());
      BufferedReader in = null;
      try
      {
        in = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
      }
      catch (IOException e)
      {
        log.error("I/O error creating reader socket for "
            + socket.getInetAddress().getHostAddress(), e);
      }

      log.info("Starting read loop for "
          + socket.getInetAddress().getHostAddress());

      // Send IT100 state discovery packet to get current system state
      sendInternal(new Packet("001", ""));
      // Send IT100 labels request packet to get system labels
      sendInternal(new Packet("002", ""));

      boolean isConnected = true;

      while (isConnected)
      {
        Packet packet = null;
        try
        {
          String rawData = in.readLine();
          log.debug("Received data from "
              + socket.getInetAddress().getHostAddress() + " : " + rawData);
          packet = new Packet(rawData);
        }
        catch (IOException e)
        {
          log.warn("Error parsing packet", e);
          isConnected = false;
          // Connection has failed, close the socket so it can be recreated
          // later
          IpConnection.this.close();
        }

        if (packet != null)
        {
          state.processPacket(packet);

          if (packetCallback != null)
          {
            log.debug("Executing callback method for packet " + packet);
            packetCallback.receive(IpConnection.this, packet);
          }
        }

      }
    }
  }
}
