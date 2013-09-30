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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.openremote.controller.protocol.dscit100.Packet.PacketCallback;

/**
 * @author Greg Rapp
 * 
 */
public class PacketTest
{

  /**
   * Test method for
   * {@link org.openremote.controller.protocol.dscit100.Packet#Packet(java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testPacketStringString()
  {
    Packet packet = new Packet("001", "1234");

    assertTrue(packet instanceof Packet);
    assertEquals("001", packet.getCommand());
    assertEquals("1234", packet.getData());
  }

  /**
   * Test method for
   * {@link org.openremote.controller.protocol.dscit100.Packet#Packet(java.lang.String, java.lang.String, org.openremote.controller.protocol.dscit100.Packet.PacketCallback)}
   * .
   */
  @Test
  public void testPacketStringStringPacketCallback()
  {
    Packet packet = new Packet("001", "1234", new PacketCallback()
    {
      @Override
      public void receive(DSCIT100Connection connection, Packet packet)
      {
      }
    });

    assertTrue(packet instanceof Packet);
    assertEquals("001", packet.getCommand());
    assertEquals("1234", packet.getData());
    assertTrue(packet.getCallback() instanceof PacketCallback);
  }

  /**
   * Test method for
   * {@link org.openremote.controller.protocol.dscit100.Packet#Packet(java.lang.String)}
   * .
   */
  @Test
  public void testPacketString()
  {
    Packet packet = null;
    try
    {
      packet = new Packet("50000126");
    }
    catch (IOException e)
    {
    }

    assertTrue(packet instanceof Packet);
    assertEquals("500", packet.getCommand());
    assertEquals("001", packet.getData());
  }

}
