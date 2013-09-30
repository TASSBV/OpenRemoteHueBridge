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
package org.openremote.controller.protocol.denonavrserial;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openremote.controller.DenonAVRSerialConfiguration;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.spring.SpringContext;

/**
 * Implementation of serial support for Denon AV Receivers.
 * 
 * The following models are supported: 2106, 2803, 2805, 2807*, 3803, 3805, 3806, 4306, 4802, 4806,
 * 5803, 5805
 * 
 * Verified models are indicated with *. Reports on successful use of this protocol on other models
 * are appreciated.
 * 
 * Not all models support all commands. Which commands work should be self-explaining and clear from
 * the specification of the equipment.
 * 
 * @author Torbjörn Österdahl, toesterdahl@ultra-marine.org
 */
public class DenonAVRSerialCommand implements ExecutableCommand, StatusCommand
{

  /** The logger. */
  private static Logger logger = Logger.getLogger(DenonAVRSerialCommand.class.getName());

  enum ConnectionType
  {
    RS232, UDP
  };

  /** The command to perform the http get request on */
  private String command;

  private Byte[] bytes;

  public DenonAVRSerialCommand()
  {
    init();
  }

  private void init()
  {

    Enumeration<?> e = CommPortIdentifier.getPortIdentifiers();

    logger.info("CommPortIdentifiers :" + e);

    while (e.hasMoreElements())
    {
      logger.info("CommPortIdentfier: " + e.nextElement());
    }
  }

  /**
   * Gets the command
   * 
   * @return the command
   */
  public String getCommand()
  {
    return command;
  }

  /**
   * Sets the command
   * 
   * @param command the new command
   */
  public void setCommand(String command)
  {
    this.command = command;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void send()
  {
    sendCommand();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String read(EnumSensorType sensoryType, Map<String, String> stateMap)
  {
    logger.info("Read: Not implemented, return an empty message");
    // Not implemented, return an empty message
    return "";
  }

  private void sendCommand()
  {
    try
    {
      DenonAVRSerialConfiguration conf = ServiceContext.getDenonAVRSerialConfiguration();

      ConnectionType connectionType = ConnectionType.valueOf(conf.getConnectionType());

      byte[] dataBytes = getCommandAsByteArray();

      logger.info("Command: " + getCommand() + " DataBytes: " + Arrays.toString(dataBytes));

      if (ConnectionType.UDP == connectionType)
      {
        InetAddress udpAddr = InetAddress.getByName(conf.getUdpIp());
        int udpPort = conf.getUdpPort();

        sendCommandUDP(dataBytes, udpAddr, udpPort);
      } else
      {
        String comPort = conf.getComPort();

        sendCommandComPort(dataBytes, comPort);
      }
    } catch (Exception e)
    {
      logger.error("Could not send command: " + getCommand(), e);
    }
  }

  private void sendCommandComPort(byte[] dataBytes, String comPort)
  {
    try
    {
      CommPortIdentifier id = CommPortIdentifier.getPortIdentifier(comPort);
      SerialPort serialPort = (SerialPort) id.open("ORBController", 2000);
      serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
          SerialPort.PARITY_NONE);
      OutputStream outputStream = serialPort.getOutputStream();
      outputStream.write(dataBytes);
      outputStream.close();
      serialPort.close();
    } catch (Exception e)
    {
      logger.error("Error sending serial command Port: " + comPort, e);
    }
  }

  private void sendCommandUDP(byte[] dataBytes, InetAddress udpAddr, int udpPort)
      throws SocketException, IOException
  {
    DatagramSocket clientSocket = null;
    try
    {
      clientSocket = new DatagramSocket();
      DatagramPacket sendPacket = new DatagramPacket(dataBytes, dataBytes.length, udpAddr, udpPort);
      clientSocket.send(sendPacket);
    } catch (Exception e)
    {
      logger.error("Error sending serial command over UDP", e);
    } finally
    {
      if (clientSocket != null)
      {
        clientSocket.close();
      }
    }
  }

  private byte[] getCommandAsByteArray()
  {
    Properties props = (Properties) SpringContext.getInstance().getBean("denonAVRSerialCommands");
    String commandString = props.getProperty(getCommand());
    byte[] dataBytes = javaStringToAsciiByteArray(commandString);
    return dataBytes;
  }

  private final String USASCII = "US-ASCII";

  private byte[] javaStringToAsciiByteArray(String s)
  {
    try
    {
      String message = s + "\r";
      return message.getBytes(USASCII);
    } catch (UnsupportedEncodingException e)
    {
      logger.error("Unsupported Encoding for converting String to ASCII byte array: " + USASCII);
      throw new RuntimeException("Unsupported Encoding for converting String to ASCII byte array");
    }
  }
}
