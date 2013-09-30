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

import org.junit.Before;
import org.junit.Assert;
import org.junit.Test;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.component.EnumSensorType;
import org.jdom.Element;

/**
 * Test 'level' sensor state reads and writes on OpenRemote virtual room/device protocol.
 *
 * @see org.openremote.controller.protocol.virtual.VirtualCommand
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class LevelStatusTest
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
   * Tests protocol read command behavior for 'level' sensor type when no explict command to
   * set state has been sent yet. Expecting a 'level' sensor to return '0' in such a case.
   */
  @Test public void testStatusDefaultValue()
  {
    StatusCommand cmd = getReadCommand("test level default value");

    String value = cmd.read(EnumSensorType.LEVEL, new HashMap<String, String>());

    Assert.assertTrue(value.equals("0"));
  }



  /**
   * Tests 'level' sensor read/write behavior.
   */
  @Test public void testLevelState()
  {
    final String ADDRESS = "level read/write tests";

    // Read command in uninitialized state, should return '0'...

    StatusCommand readCmd = getReadCommand(ADDRESS);

    String value = readCmd.read(EnumSensorType.LEVEL, new HashMap<String, String>());

    Assert.assertTrue(value.equalsIgnoreCase("0"));


    // Send write command '1' to the same address...

    ExecutableCommand writeLevel1 = getWriteCommand(ADDRESS, "any command", 1);

    writeLevel1.send();


    // Read state, should return '1'...

    value = readCmd.read(EnumSensorType.LEVEL, new HashMap<String, String>());

    Assert.assertTrue(value.equals("1"));


    // Send write command '100' to the same address...

    ExecutableCommand writeLevel100 = getWriteCommand(ADDRESS, "any command", 100);

    writeLevel100.send();



    // Read state, should return '100'...

    value = readCmd.read(EnumSensorType.LEVEL, new HashMap<String, String>());

    Assert.assertTrue(value.equals("100"));
  }




  /**
   * Tests 'level' sensor read/write behavior with out of bounds values.
   */
  @Test public void testLevelOutOfBoundsState()
  {
    final String ADDRESS = "level out of bounds tests";

    // Read command in uninitialized state, should return '0'...

    StatusCommand readCmd = getReadCommand(ADDRESS);

    String value = readCmd.read(EnumSensorType.LEVEL, new HashMap<String, String>());

    Assert.assertTrue(value.equalsIgnoreCase("0"));


    // Send write command '-1' to the same address...

    ExecutableCommand writeLevelNeg1 = getWriteCommand(ADDRESS, "any command", -1);

    writeLevelNeg1.send();


    // Read state, should return '0'...

    value = readCmd.read(EnumSensorType.LEVEL, new HashMap<String, String>());

    Assert.assertTrue(value.equals("0"));


    // Send write command '101' to the same address...

    ExecutableCommand writeLevel101 = getWriteCommand(ADDRESS, "any command", 101);

    writeLevel101.send();



    // Read state, should return '100'...

    value = readCmd.read(EnumSensorType.LEVEL, new HashMap<String, String>());

    Assert.assertTrue(value.equals("100"));
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
   * @param value     command value
   *
   * @return  write command instance with given parameters
   */
  private ExecutableCommand getWriteCommand(String address, String cmd, int value)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");

    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "virtual");
    ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, Integer.toString(value));

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

