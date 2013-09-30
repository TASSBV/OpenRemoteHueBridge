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

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;

/**
 * @author Greg Rapp
 * 
 */
public class DSCIT100CommandBuilderTest
{

  // Test Setup
  // -----------------------------------------------------------------------------------

  private DSCIT100CommandBuilder builder = null;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception
  {
    builder = new DSCIT100CommandBuilder();
  }

  // Tests
  // ----------------------------------------------------------------------------------------

  /**
   * Test DSCIT100 command parsing with "ARM_AWAY" as the command string and
   * 1.1.1.1:5000 address, partition 1 and security code 1234.
   */
  @Test
  public void testDSCIT100ArmAway()
  {
    Command cmd = getCommand("ARM_AWAY", "1.1.1.1:5000", "1", "");

    assertTrue(cmd instanceof ExecuteCommand);
    assertTrue(((ExecuteCommand) cmd).getPacket() instanceof Packet);
    assertEquals("030", ((ExecuteCommand) cmd).getPacket().getCommand());
    assertEquals("1", ((ExecuteCommand) cmd).getPacket().getData());
  }

  /**
   * Test DSCIT100 command parsing with "ARM_STAY" as the command string and
   * 1.1.1.1:5000 address, partition 1 and security code 1234.
   */
  @Test
  public void testDSCIT100ArmStay()
  {
    Command cmd = getCommand("ARM_STAY", "1.1.1.1:5000", "1", "1234");

    assertTrue(cmd instanceof ExecuteCommand);
    assertTrue(((ExecuteCommand) cmd).getPacket() instanceof Packet);
    assertEquals("031", ((ExecuteCommand) cmd).getPacket().getCommand());
    assertEquals("1", ((ExecuteCommand) cmd).getPacket().getData());
  }

  /**
   * Test DSCIT100 command parsing with "ARM_NO_ENTRY_DELAY" as the command
   * string and 1.1.1.1:5000 address, partition 1 and security code 1234.
   */
  @Test
  public void testDSCIT100ArmNoEntryDelay()
  {
    Command cmd = getCommand("ARM_NO_ENTRY_DELAY", "1.1.1.1:5000", "1", "1234");

    assertTrue(cmd instanceof ExecuteCommand);
    assertTrue(((ExecuteCommand) cmd).getPacket() instanceof Packet);
    assertEquals("032", ((ExecuteCommand) cmd).getPacket().getCommand());
    assertEquals("1", ((ExecuteCommand) cmd).getPacket().getData());
  }

  /**
   * Test DSCIT100 command parsing with "ARM" as the command string and
   * 1.1.1.1:5000 address, partition 1 and security code 1234.
   */
  @Test
  public void testDSCIT100Arm()
  {
    Command cmd = getCommand("ARM", "1.1.1.1:5000", "1", "1234");

    assertTrue(cmd instanceof ExecuteCommand);
    assertTrue(((ExecuteCommand) cmd).getPacket() instanceof Packet);
    assertEquals("033", ((ExecuteCommand) cmd).getPacket().getCommand());
    assertEquals("1123400", ((ExecuteCommand) cmd).getPacket().getData());
  }

  /**
   * Test DSCIT100 command parsing with "DISARM" as the command string and
   * 1.1.1.1:5000 address, partition 1 and security code 1234.
   */
  @Test
  public void testDSCIT100Disarm()
  {
    Command cmd = getCommand("DISARM", "1.1.1.1:5000", "1", "1234");

    assertTrue(cmd instanceof ExecuteCommand);
    assertTrue(((ExecuteCommand) cmd).getPacket() instanceof Packet);
    assertEquals("040", ((ExecuteCommand) cmd).getPacket().getCommand());
    assertEquals("1123400", ((ExecuteCommand) cmd).getPacket().getData());
  }

  /**
   * Test DSCIT100 command parsing with "PANIC_FIRE" as the command string and
   * 1.1.1.1:5000 address.
   */
  @Test
  public void testDSCIT100PanicFire()
  {
    Command cmd = getCommand("PANIC_FIRE", "1.1.1.1:5000", "", "");

    assertTrue(cmd instanceof ExecuteCommand);
    assertTrue(((ExecuteCommand) cmd).getPacket() instanceof Packet);
    assertEquals("060", ((ExecuteCommand) cmd).getPacket().getCommand());
    assertEquals("1", ((ExecuteCommand) cmd).getPacket().getData());
  }

  /**
   * Test DSCIT100 command parsing with "PANIC_AMBULANCE" as the command string
   * and 1.1.1.1:5000 address.
   */
  @Test
  public void testDSCIT100PanicAmbulance()
  {
    Command cmd = getCommand("PANIC_AMBULANCE", "1.1.1.1:5000", "", "");

    assertTrue(cmd instanceof ExecuteCommand);
    assertTrue(((ExecuteCommand) cmd).getPacket() instanceof Packet);
    assertEquals("060", ((ExecuteCommand) cmd).getPacket().getCommand());
    assertEquals("2", ((ExecuteCommand) cmd).getPacket().getData());
  }

  /**
   * Test DSCIT100 command parsing with "PANIC_POLICE" as the command string and
   * 1.1.1.1:5000 address.
   */
  @Test
  public void testDSCIT100PanicPolice()
  {
    Command cmd = getCommand("PANIC_POLICE", "1.1.1.1:5000", "", "");

    assertTrue(cmd instanceof ExecuteCommand);
    assertTrue(((ExecuteCommand) cmd).getPacket() instanceof Packet);
    assertEquals("060", ((ExecuteCommand) cmd).getPacket().getCommand());
    assertEquals("3", ((ExecuteCommand) cmd).getPacket().getData());
  }

  /**
   * Test DSCIT100 command parsing with "PARTITION_STATE" as the command string
   * and 1.1.1.1:5000 address and partition 1.
   */
  @Test
  public void testDSCIT100PartitionState()
  {
    Command cmd = getCommand("PARTITION_STATE", "1.1.1.1:5000", "1", "");

    assertTrue(cmd instanceof ReadCommand);
  }

  /**
   * Test DSCIT100 command parsing with invalid command name.
   */
  @Test(expected = NoSuchCommandException.class)
  public void testNoSuchCommand()
  {
    getCommand(" ", "1.1.1.1:5000", "", "");
  }

  /**
   * Test DSCIT100 command parsing with XML snippets containing superfluous
   * properties.
   */
  @Test
  public void testDSCIT100SuperfluousProperties()
  {
    Command cmd = getCommandWithExtraProperties("ARM", "1.1.1.1:5000");
    assertTrue(cmd instanceof ExecuteCommand);
  }

  /**
   * Test DSCIT100 command parsing with a missing mandatory command property.
   */
  @Test(expected = NoSuchCommandException.class)
  public void testDSCIT100WithMissingCommandProperty()
  {
    getCommandMissingCommandProperty("1.1.1.1:5000");
  }

  /**
   * Test DSCIT100 command parsing with a missing mandatory address property.
   */
  @Test(expected = NoSuchCommandException.class)
  public void testDSCIT100WithMissingAddressProperty()
  {
    getCommandMissingAddressProperty("ARM");
  }

  // Helpers
  // --------------------------------------------------------------------------------------

  private Command getCommand(String cmd, String address, String target,
      String code)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "dscit100");

    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
        DSCIT100CommandBuilder.DSCIT100_XMLPROPERTY_ADDRESS);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, address);

    ele.addContent(propAddr);

    Element propAddr2 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
        DSCIT100CommandBuilder.DSCIT100_XMLPROPERTY_COMMAND);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, cmd);

    ele.addContent(propAddr2);

    Element propAddr3 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr3.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
        DSCIT100CommandBuilder.DSCIT100_XMLPROPERTY_CODE);
    propAddr3.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, code);

    ele.addContent(propAddr3);

    Element propAddr4 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr4.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
        DSCIT100CommandBuilder.DSCIT100_XMLPROPERTY_TARGET);
    propAddr4.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, target);

    ele.addContent(propAddr4);

    return builder.build(ele);
  }

  private Command getCommandMissingAddressProperty(String cmd)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "dscit100");

    Element propAddr2 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
        DSCIT100CommandBuilder.DSCIT100_XMLPROPERTY_COMMAND);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, cmd);

    ele.addContent(propAddr2);

    return builder.build(ele);
  }

  private Command getCommandMissingCommandProperty(String address)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "dscit100");

    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
        DSCIT100CommandBuilder.DSCIT100_XMLPROPERTY_ADDRESS);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, address);

    ele.addContent(propAddr);

    return builder.build(ele);
  }

  private Command getCommandWithExtraProperties(String cmd, String address)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "dscit100");

    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
        DSCIT100CommandBuilder.DSCIT100_XMLPROPERTY_ADDRESS);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, address);

    ele.addContent(propAddr);

    Element propAddr2 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
        DSCIT100CommandBuilder.DSCIT100_XMLPROPERTY_COMMAND);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, cmd);

    ele.addContent(propAddr2);

    // empty properties..

    Element propAddr3 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr3.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, "");
    propAddr3.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, "");

    ele.addContent(propAddr3);

    // unknown properties

    Element propAddr4 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr4.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, "test");
    propAddr4.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, "unknown");

    ele.addContent(propAddr4);
    return builder.build(ele);
  }

}
