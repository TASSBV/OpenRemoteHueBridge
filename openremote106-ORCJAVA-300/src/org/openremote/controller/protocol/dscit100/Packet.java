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

import org.apache.log4j.Logger;

public class Packet
{

  // Class Members
  // --------------------------------------------------------------------------------

  /**
   * DSCIT100 logger. Uses a common category for all KNX related logging.
   */
  private final static Logger log = Logger
      .getLogger(DSCIT100CommandBuilder.DSCIT100_LOG_CATEGORY);

  private String command;
  private String data;
  private String checksum;
  private PacketCallback callback;

  // End of Packet CRLF
  public static final String EOP = "\r\n";

  public Packet(String command, String data)
  {
    this.command = command;
    this.data = data;
    this.generateChecksum();
    log.debug("New packet created " + toString());
  }

  public Packet(String command, String data, PacketCallback callback)
  {
    this(command, data);
    this.callback = callback;
  }

  /**
   * Parse raw data from host into an instance of Packet
   * 
   * @param raw
   *          Raw data from host
   * @throws IOException
   *           Error parsing raw data
   */
  public Packet(String raw) throws IOException
  {
    try
    {
      this.command = raw.substring(0, 3);
      this.checksum = raw.substring(raw.length() - 2, raw.length());
      this.data = raw.substring(3, raw.length() - 2);

      String calcChecksum = checksum();
      if (!this.checksum.equalsIgnoreCase(calcChecksum))
        log.error("Received packet with invalid checksum [packet=" + checksum
            + ",calculated=" + calcChecksum + "]");

    }
    catch (StringIndexOutOfBoundsException e)
    {
      log.error("Received bad packet [raw packet=" + raw + "]", e);
    }
  }

  public String getCommand()
  {
    return command;
  }

  public String getData()
  {
    return data;
  }

  public String getChecksum()
  {
    return checksum;
  }

  public PacketCallback getCallback()
  {
    return callback;
  }

  public void generateChecksum()
  {
    checksum = checksum();
  }

  public String toString()
  {
    return "[command=" + command + ", data=" + data + ",checksum=" + checksum
        + "]";
  }

  /**
   * Return the data to be sent to the IT-100
   * 
   * @return String Packet data
   */
  public String toPacket()
  {
    return command + data + checksum + EOP;
  }

  /**
   * Calculate the packet's checksum
   * 
   * @return String Packet checksum
   */
  private String checksum()
  {
    int i;
    int iChecksum = 0;

    for (i = 0; i < command.length(); i++)
    {
      iChecksum += command.charAt(i);
    }

    for (i = 0; i < data.length(); i++)
    {
      iChecksum += data.charAt(i);
    }

    String sChecksum = Integer.toHexString(iChecksum).toUpperCase();
    sChecksum = sChecksum.substring(sChecksum.length() - 2);

    return sChecksum;
  }

  public interface PacketCallback
  {
    public void receive(DSCIT100Connection connection, Packet packet);
  }
}
