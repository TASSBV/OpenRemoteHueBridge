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
package org.openremote.controller.protocol.x10;

import static junit.framework.Assert.assertTrue;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.protocol.x10.X10Command;
import org.openremote.controller.protocol.x10.X10CommandBuilder;

/**
 * Basic unit tests for parsing XML elements in
 * {@link org.openremote.controller.protocol.x10.X10CommandBuilder}.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Dan Cong
 * @author Fekete Kamosh
 *
 */
public class X10CommandBuilderTest
{

  // Test Setup -----------------------------------------------------------------------------------

  private X10CommandBuilder builder = null;

  private char[] housecodes = { 'A', 'B', 'C', 'D', 'E', 'F',
                                'G', 'H', 'I', 'J', 'K', 'L',
                                'M', 'N', 'O', 'P' };


  @Before public void setUp()
  {
    builder = new X10CommandBuilder();
  }


  // Tests ----------------------------------------------------------------------------------------

  /**
   * Test X10 command creation with 'A1' as address and 'ON' as command.
   */
  @Test public void testGetCommandByRightCmdAndAddress()
  {
    Command cmd = getCommand("ON", "A1");

    assertTrue(cmd instanceof X10Command);
  }

  /**
   * Test X10 command creation with 'A1' as address and 'on', 'On' or 'oN' as command.
   */
  @Test public void testGetCommandByRightCmdAndAddressMixedCase()
  {
    Command cmd1 = getCommand("on", "A1");

    assertTrue(cmd1 instanceof X10Command);

    Command cmd2 = getCommand("oN", "A1");

    assertTrue(cmd2 instanceof X10Command);

    Command cmd3 = getCommand("On", "A1");

    assertTrue(cmd3 instanceof X10Command);
  }

  /**
   * Test X10 command creation with 'A1' as address and 'OFF' as command.
   */
  @Test public void testX10BuilderWithOffCmd()
  {
    Command cmd = getCommand("OFF", "A1");

    assertTrue(cmd instanceof X10Command);
  }

  /**
   * Test X10 command creation with 'A1' as address and 'off', 'Off' and 'oFf' as command.
   */
  @Test public void testX10BuilderWithOffCmdMixedCase()
  {
    Command cmd1 = getCommand("off", "A1");

    assertTrue(cmd1 instanceof X10Command);

    Command cmd2 = getCommand("Off", "A1");

    assertTrue(cmd2 instanceof X10Command);

    Command cmd3 = getCommand("oFf", "A1");

    assertTrue(cmd3 instanceof X10Command);
  }

  /**
   * Test X10 command creation with 'A1' as address and 'ALL_OFF' as command.
   */
  @Test public void testX10BuilderWithAllOffCmd()
  {
    Command cmd = getCommand("ALL_OFF", "A1");

    assertTrue(cmd instanceof X10Command);
  }

  /**
   * Test X10 command creation with 'A' as address and 'ALL_OFF' as command.
   */
  @Test public void testX10BuilderWithAllOffCmdWithoutDeviceCode()
  {
    Command cmd = getCommand("ALL_OFF", "A");

    assertTrue(cmd instanceof X10Command);
  }  

  /**
   * Test X10 command creation with 'A1' as address and 'all_off', 'All_Off' and 'All_OFF'
   * as command.
   */
  @Test public void testX10BuilderWithAllOffCmdMixedCase()
  {
    Command cmd1 = getCommand("all_off", "A1");

    assertTrue(cmd1 instanceof X10Command);

    Command cmd2 = getCommand("All_Off", "A1");

    assertTrue(cmd2 instanceof X10Command);

    Command cmd3 = getCommand("All_OFF", "A1");

    assertTrue(cmd3 instanceof X10Command);
  }

  /**
   * Test X10 command creation with 'A1' as address and 'ALL_UNITS_OFF' as command.
   */
  @Test public void testX10BuilderWithAllUnitsOffCmd()
  {
    Command cmd = getCommand("ALL_UNITS_OFF", "A1");

    assertTrue(cmd instanceof X10Command);
  }

  /**
   * Test X10 command creation with 'A1' as address and 'all_units_off', 'All_Units_Off'
   * and 'All_units_OFF' as commands.
   */
  @Test public void testX10BuilderWithAllUnitsOffCmdMixedCase()
  {
    Command cmd1 = getCommand("all_units_off", "A1");

    assertTrue(cmd1 instanceof X10Command);

    Command cmd2 = getCommand("All_Units_Off", "A1");

    assertTrue(cmd2 instanceof X10Command);

    Command cmd3 = getCommand("All_units_OFF", "A1");

    assertTrue(cmd3 instanceof X10Command);
  }


  /**
   * Test X10 command creation with 'A1' as address and 'ALL OFF' as command.
   */
  @Test public void testX10BuilderWithAllOffHumanFormCmd()
  {
    Command cmd = getCommand("ALL OFF", "A1");

    assertTrue(cmd instanceof X10Command);
  }

  /**
   * Test X10 command creation with 'A1' as address and 'all off', 'All Off' and 'All OFF'
   * as command.
   */
  @Test public void testX10BuilderWithAllOffHumanFormCmdMixedCase()
  {
    Command cmd1 = getCommand("all off", "A1");

    assertTrue(cmd1 instanceof X10Command);

    Command cmd2 = getCommand("All Off", "A1");

    assertTrue(cmd2 instanceof X10Command);

    Command cmd3 = getCommand("All OFF", "A1");

    assertTrue(cmd3 instanceof X10Command);
  }

  /**
   * Test X10 command creation with 'A1' as address and 'ALL UNITS OFF' as command.
   */
  @Test public void testX10BuilderWithAllUnitsOffHumanFormCmd()
  {
    Command cmd = getCommand("ALL UNITS OFF", "A1");

    assertTrue(cmd instanceof X10Command);
  }

  /**
   * Test X10 command creation with 'A1' as address and 'all units off', 'All Units Off'
   * and 'All units OFF' as commands.
   */
  @Test public void testX10BuilderWithAllUnitsOffHumanFormCmdMixedCase()
  {
    Command cmd1 = getCommand("all units off", "A1");

    assertTrue(cmd1 instanceof X10Command);

    Command cmd2 = getCommand("All Units Off", "A1");

    assertTrue(cmd2 instanceof X10Command);

    Command cmd3 = getCommand("All units OFF", "A1");

    assertTrue(cmd3 instanceof X10Command);
  }

  /**
   * Test X10 command creation with 'A1' as address and 'DIM' as command.
   */
  @Test public void testX10BuilderWithDimCmd()
  {
    Command cmd = getCommand("DIM", "A1");

    assertTrue(cmd instanceof X10Command);
  }


  /**
   * Test X10 command creation with 'A1' as address and 'dim', 'Dim' and 'dIm' as command.
   */
  @Test public void testX10BuilderWithDimCmdMixedCase()
  {
    Command cmd1 = getCommand("dim", "A1");

    assertTrue(cmd1 instanceof X10Command);

    Command cmd2 = getCommand("Dim", "A1");

    assertTrue(cmd2 instanceof X10Command);

    Command cmd3 = getCommand("dIm", "A1");

    assertTrue(cmd3 instanceof X10Command);
  }

  /**
   * Test X10 command creation with 'A1' as address and 'BRIGHT' as command.
   */
  @Test public void testX10BuilderWithBrightCmd()
  {
    Command cmd = getCommand("BRIGHT", "A1");

    assertTrue(cmd instanceof X10Command);
  }

  /**
   * Test X10 DIM command creation with 'A1' as address and 'bright', 'Bright' and 'brIGht' as command.
   */
  @Test public void testX10BuilderWithBrightCmdMixedCase()
  {
    Command cmd1 = getCommand("bright", "A1");

    assertTrue(cmd1 instanceof X10Command);

    Command cmd2 = getCommand("Bright", "A1");

    assertTrue(cmd2 instanceof X10Command);

    Command cmd3 = getCommand("brIGht", "A1");

    assertTrue(cmd3 instanceof X10Command);
  }


  /**
   * Test X10 command creation with 'A1' as address and 'ALL_LIGHTS_ON' as command
   */
  @Test public void testX10BuilderWithAllLightsOnCmd()
  {
    Command cmd = getCommand("ALL_LIGHTS_ON", "A1");

    assertTrue(cmd instanceof X10Command);
  }

  /**
   * Test X10 command creation with 'A' as address and 'ALL_LIGHTS_ON' as command
   */
  @Test public void testX10BuilderWithAllLightsOnCmdWithoutDeviceCode()
  {
    Command cmd = getCommand("ALL_LIGHTS_ON", "A");

    assertTrue(cmd instanceof X10Command);
  }


  /**
   * Test X10 command creation with 'A1' as address and 'ALL LIGHTS ON', 'LIGHTS ON'
   */
  @Test public void testX10BuilderWithAllLightsOnHumanFormCmd()
  {
    Command cmd1 = getCommand("ALL LIGHTS ON", "A1");

    assertTrue(cmd1 instanceof X10Command);

    Command cmd2 = getCommand("LIGHTS ON", "A1");

    assertTrue(cmd2 instanceof X10Command);
  }

  /**
   * Test X10 command creation with 'A1' as address and 'ALL_liGHtS_ON', 'All lights ON', 'LIGHTS on'
   */
  @Test public void testX10BuilderWithAllLightsOnCmdMixedCase()
  {
    Command cmd1 = getCommand("ALL_liGHtS_ON", "A1");

    assertTrue(cmd1 instanceof X10Command);

    Command cmd2 = getCommand("All lights ON", "A1");

    assertTrue(cmd2 instanceof X10Command);

    Command cmd3 = getCommand("LIGHTS on", "A1");

    assertTrue(cmd3 instanceof X10Command);
  }

  /**
   * Test X10 command creation with 'A' as address and 'ALL LIGHTS ON', 'LIGHTS ON'
   */
  @Test public void testX10BuilderWithAllLightsOnHumanFormCmdWithoutDeviceCode()
  {
    Command cmd = getCommand("ALL LIGHTS ON", "A");

    assertTrue(cmd instanceof X10Command);

    Command cmd2 = getCommand("LIGHTS ON", "A");

    assertTrue(cmd2 instanceof X10Command);
  }


  /**
   * Test X10 command creation with 'A' as address and 'ALL_liGHtS_ON', 'All lights ON', 'LIGHTS on'
   */
  @Test public void testX10BuilderWithAllLightsOnHumanFormCmdWithoutDeviceCodeMixedCase()
  {
    Command cmd1 = getCommand("ALL_liGHtS_ON", "A");

    assertTrue(cmd1 instanceof X10Command);

    Command cmd2 = getCommand("All lights ON", "A");

    assertTrue(cmd2 instanceof X10Command);

    Command cmd3 = getCommand("LIGHTS on", "A");

    assertTrue(cmd3 instanceof X10Command);
  }

  
  /**
   * Tests X10 command creation with an unknown command 'foobar'.
   */
  @Test (expected = NoSuchCommandException.class)
  public void testX10BuilderWithIncorrectCommand()
  {
    getCommand("foobar", "A1");  
  }

  /**
   * Tests X10 command creation with incorrect housecode value 'Z'.
   */
  @Test (expected = NoSuchCommandException.class)
  public void testX10BuilderWithIncorrectHouseCode()
  {
    getCommand("ON", "Z1");
  }

  /**
   * Tests X10 command creation with an incorrect device code value 17.
   */
  @Test (expected = NoSuchCommandException.class)
  public void testX10BuilderWithIncorrectDeviceCode()
  {
    getCommand("ON", "A17");
  }

  /**
   * Tests X10 command creation with an incorrect address 'T256'.
   */
  @Test (expected = NoSuchCommandException.class)
  public void testX10BuilderWithIncorrectAddress()
  {
    getCommand("ON", "T256");
  }


  /**
   * Tests X10 command creation with all valid addresses ranging from [A1..A16] to [P1..P16].
   */
  @Test public void iterateThroughAllX10Addresses()
  {
    for (Character housecode : housecodes)
    {
      for (int deviceCode = 1; deviceCode <= 16; deviceCode++)
      {
        String address = housecode + "" + deviceCode;
        getCommand("ON", address);

        address = housecode.toString().toLowerCase() + deviceCode;
        getCommand("ON", address);
      }
    }
  }
  
  /**
   * Test border cases with X10 addressing from  [A0..P0] and [A17..P17].
   */
  @Test public void testX10AddressBorderCases()
  {
    int expectedFailures = 0;

    for (Character housecode : housecodes)
    {
      try
      {
        String address = housecode + "0";
        getCommand("ON", address);
      }
      catch (NoSuchCommandException e)
      {
        ++expectedFailures;
      }

      try
      {
        String address = housecode + "17";
        getCommand("ON", address);
      }
      catch (NoSuchCommandException e)
      {
        ++expectedFailures;
      }
    }

    assertTrue(expectedFailures == housecodes.length * 2);
  }


  /**
   * Test X10 command parsing with XML snippets containing superfluous properties.
   */
  @Test public void testX10SuperfluousProperties()
  {
    Command cmd = getCommandWithExtraProperties("on", "P16") ;
    assertTrue(cmd instanceof X10Command);
  }

  /**
   * Test X10 command parsing with a missing mandatory command property.
   */
  @Test (expected = NoSuchCommandException.class)
  public void testX10WithMissingCommandProperty()
  {
    getCommandMissingCommandProperty("A16") ;
  }


  /**
   * Test X10 command parsing with a missing mandatory address property.
   */
  @Test (expected = NoSuchCommandException.class)
  public void testX10WithMissingAddressProperty()
  {
    getCommandMissingAddressProperty("on") ;
  }


  /**
   * Test X10 command parsing with arbitrary property order
   */
  @Test public void testX10WithArbitraryPropertyOrder()
  {
    Command cmd = getCommandArbitraryPropertyOrder("on", "H10");
    assertTrue(cmd instanceof X10Command);
  }




  // Helpers --------------------------------------------------------------------------------------

  private Command getCommand(String cmd, String address)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "x10");
    ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, "255");

    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                          X10CommandBuilder.X10_XMLPROPERTY_ADDRESS);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, address);

    Element propCommand = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propCommand.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                             X10CommandBuilder.X10_XMLPROPERTY_COMMAND);
    propCommand.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, cmd);

    ele.addContent(propAddr);
    ele.addContent(propCommand);

    return builder.build(ele);
  }

  private Command getCommandArbitraryPropertyOrder(String cmd, String address)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "x10");
    ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, "255");

    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                          X10CommandBuilder.X10_XMLPROPERTY_ADDRESS);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, address);

    Element propCommand = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propCommand.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                             X10CommandBuilder.X10_XMLPROPERTY_COMMAND);
    propCommand.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, cmd);


    ele.addContent(propCommand);
    ele.addContent(propAddr);

    return builder.build(ele);
  }

  private Command getCommandMissingCommandProperty(String address)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "x10");
    ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, "255");

    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, 
                          X10CommandBuilder.X10_XMLPROPERTY_ADDRESS);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, address);

    ele.addContent(propAddr);

    return builder.build(ele);
  }

  private Command getCommandMissingAddressProperty(String cmd)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "x10");
    ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, "255");

    Element propCommand = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propCommand.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                             X10CommandBuilder.X10_XMLPROPERTY_COMMAND);
    propCommand.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, cmd);

    ele.addContent(propCommand);

    return builder.build(ele);
  }

  private Command getCommandWithExtraProperties(String cmd, String address)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "x10");
    ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, "255");

    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                          X10CommandBuilder.X10_XMLPROPERTY_ADDRESS);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, address);

    Element propCommand = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propCommand.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                             X10CommandBuilder.X10_XMLPROPERTY_COMMAND);
    propCommand.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, cmd);

    ele.addContent(propAddr);
    ele.addContent(propCommand);

    // add some extra properties

    Element propExtra1 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propExtra1.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, "");
    propExtra1.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, "");

    Element propExtra2 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propExtra2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, "to be ignored");
    propExtra2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, "true");

    ele.addContent(propExtra1);
    ele.addContent(propExtra2);

    return builder.build(ele);
  }

}
