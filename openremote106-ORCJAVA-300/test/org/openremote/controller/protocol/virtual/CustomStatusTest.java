/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.controller.protocol.virtual;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.component.EnumSensorType;
import org.jdom.Element;

/**
 * Test 'custom' sensor state reads and writes on OpenRemote virtual room/device protocol.
 *
 * @see org.openremote.controller.protocol.virtual.VirtualCommand
 *
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class CustomStatusTest
{


  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Reference to the command builder we can use to build command instances.
   */
  private VirtualCommandBuilder builder = null;



  // Test Setup -----------------------------------------------------------------------------------

  @Before public void setUp()
  {
    builder = new VirtualCommandBuilder();
  }



  // Tests ----------------------------------------------------------------------------------------

  /**
   * Tests protocol read command behavior for 'custom' sensor type when no explict command to
   * set state has been sent yet. Expecting a 'custom' sensor to return an empty string in such
   * case.
   */
  @Test public void testStatusDefaultValue()
  {
    StatusCommand cmd = getReadCommand("test range default value");

    String value = cmd.read(EnumSensorType.CUSTOM, new HashMap<String, String>());

    Assert.assertTrue(value.equals(""));
  }



  /**
   * Tests 'custom' sensor read/write behavior. Note that we are testing directly on the
   * 'raw' protocol implementation, therefore custom state mapping such as provided by
   * the sensor implementation does not apply. More or less we're just testing that arbitrary
   * values are returned correctly.
   */
  @Test public void testCustomState()
  {
    final String ADDRESS = "custom read/write tests";

    // Read command in uninitialized state, should return an empty string...

    StatusCommand readCmd = getReadCommand(ADDRESS);

    String value = readCmd.read(EnumSensorType.CUSTOM, new HashMap<String, String>());

    Assert.assertTrue(value.equalsIgnoreCase(""));


    // Send write command 'foo' to the same address...

    ExecutableCommand writeLevel1 = getWriteCommand(ADDRESS, "foo");

    writeLevel1.send();


    // Read state, should return 'foo'...

    value = readCmd.read(EnumSensorType.CUSTOM, new HashMap<String, String>());

    Assert.assertTrue(value.equals("foo"));


    // Send write command 'STATUS' to the same address...

    ExecutableCommand writeLevel10001 = getWriteCommand(ADDRESS, "STATUS");

    writeLevel10001.send();



    // Read state, should return 'STATUS'...

    value = readCmd.read(EnumSensorType.CUSTOM, new HashMap<String, String>());

    Assert.assertTrue("Was expecting 'STATUS', got '" + value + "'.", value.equals("STATUS"));
  }




  // Helpers --------------------------------------------------------------------------------------

  /**
   * Returns a read ('status') command.
   *
   * @param address   arbitrary string address
   *
   * @return  status command instance for the given address
   */
  private StatusCommand getReadCommand(String address)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");

    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "virtual");

    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, "address");
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, address);

    ele.addContent(propAddr);


    Element propAddr2 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, "command");
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, "status");

    ele.addContent(propAddr2);


    Command cmd = builder.build(ele);

    if (!(cmd instanceof StatusCommand))
    {
      Assert.fail("Was expecting a read command (StatusCommand) type, got " + cmd.getClass());

      return null;
    }
    else
    {
      return (StatusCommand)cmd;
    }
  }


  /**
   * Creates a write command with given command value hacked into an XML element attribute.
   *
   * @param address   arbitrary address string
   * @param cmd       arbitrary command name
   *
   * @return  write command instance with given parameters
   */
  private ExecutableCommand getWriteCommand(String address, String cmd)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");

    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "virtual");

    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, "address");
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, address);

    ele.addContent(propAddr);


    Element propAddr2 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, "command");
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, cmd);

    ele.addContent(propAddr2);



    Command command = builder.build(ele);

    if (!(command instanceof ExecutableCommand))
    {
      Assert.fail("Was expecting a write command (ExecutableCommand) type, got " + command.getClass());

      return null;
    }
    else
    {
      return (ExecutableCommand)command;
    }
  }
  

}

