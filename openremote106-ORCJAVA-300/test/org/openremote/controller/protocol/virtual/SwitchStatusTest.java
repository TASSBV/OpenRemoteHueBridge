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
package org.openremote.controller.protocol.virtual;

import java.util.HashMap;

import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.component.EnumSensorType;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

/**
 * Test 'switch' sensor state updates on OpenRemote virtual room/device protocol.
 *
 * @see org.openremote.controller.protocol.virtual.VirtualCommand
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class SwitchStatusTest
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
   * Tests protocol read command behavior for 'switch' sensor type when no explict command to
   * set state has been sent yet. Expecting a switch to return 'off' in such a case.
   */
  @Test public void testStatusDefaultValue()
  {
    StatusCommand cmd = getReadCommand("test status default value");

    String value = cmd.read(EnumSensorType.SWITCH, new HashMap<String, String>());

    Assert.assertTrue(value.equals("off"));
  }


  /**
   * Tests 'switch' sensor read/write behavior.
   */
  @Test public void testOnOffState()
  {
    final String ADDRESS = "test on off state";

    // Read command in uninitialized state, should return 'off'...

    StatusCommand readCmd = getReadCommand(ADDRESS);

    String value = readCmd.read(EnumSensorType.SWITCH, new HashMap<String, String>());

    Assert.assertTrue(value.equalsIgnoreCase("off"));


    // Send write command 'on' to the same address...

    ExecutableCommand writeON = getWriteCommand(ADDRESS, "on");

    writeON.send();


    // Read state, should return 'on'...

    value = readCmd.read(EnumSensorType.SWITCH, new HashMap<String, String>());

    Assert.assertTrue(value.equals("on"));


    // Send write command 'off' to the same address...

    ExecutableCommand writeOFF = getWriteCommand(ADDRESS, "off");

    writeOFF.send();



    // Read state, should return 'off'...

    value = readCmd.read(EnumSensorType.SWITCH, new HashMap<String, String>());

    Assert.assertTrue(value.equals("off"));
  }



  // Helpers --------------------------------------------------------------------------------------

  private StatusCommand getReadCommand(String address)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");

    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "virtual");
    //ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, Integer.toString(value));

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


  private ExecutableCommand getWriteCommand(String address, String cmd)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");

    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "virtual");
    //ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, Integer.toString(value));

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

