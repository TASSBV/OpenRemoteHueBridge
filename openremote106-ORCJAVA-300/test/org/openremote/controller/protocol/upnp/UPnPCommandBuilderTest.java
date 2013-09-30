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
package org.openremote.controller.protocol.upnp;


import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.protocol.upnp.UPnPCommandBuilder;
import org.openremote.controller.protocol.upnp.UPnPCommand;
import org.junit.Before;
import org.junit.Test;
import org.jdom.Element;
import static junit.framework.Assert.assertTrue;

/**
 * Basic unit tests for parsing XML elements in
 * {@link org.openremote.controller.protocol.upnp.UPnPCommandBuilder}.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 *
 */
public class UPnPCommandBuilderTest
{

  // Test Setup -----------------------------------------------------------------------------------

  private UPnPCommandBuilder builder = null;

  @Before public void setUp()
  {
    builder = new UPnPCommandBuilder();
  }


  // Tests ----------------------------------------------------------------------------------------

  /**
   * Test UPnP command parsing with arbitrary device and action properties.
   */
  @Test public void testUPnPCommandParsing()
  {
    Command cmd = getCommandDeviceAndAction("ABC", "ON");

    assertTrue(cmd instanceof UPnPCommand);
  }


  /**
   * Test UPnP command parsing with event arguments.
   */
  @Test public void testUPnPEventProperties()
  {
    Command cmd = getCommandWithUPnPEventArguments("on", "1/1/1") ;
    assertTrue(cmd instanceof UPnPCommand);
  }

  /**
   * Test KNX command parsing with a missing mandatory device property.
   */
  @Test(expected = NoSuchCommandException.class)
  public void testUPnPWithMissingCommandProperty()
  {
    getCommandMissingDeviceProperty("ON") ;
  }


  /**
   * Test UPnP command parsing with a missing mandatory action property.
   */
  @Test(expected = NoSuchCommandException.class)
  public void testUPnPWithMissingGroupAddressProperty()
  {
    getCommandMissingActionProperty("ABC") ;
  }


  /**
   * Test UPNP command parsing with arbitrary property order
   */
  @Test public void testUPnPWithArbitraryPropertyOrder()
  {
    Command cmd = getCommandDeviceAndActionArbitraryPropertyOrder("device", "on");
    assertTrue(cmd instanceof UPnPCommand);
  }


  // Helpers --------------------------------------------------------------------------------------

  private Command getCommandDeviceAndAction(String device, String action)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "upnp");

    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                          UPnPCommandBuilder.UPNP_XMLPROPERTY_DEVICE);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                          device);

    ele.addContent(propAddr);

    Element propAddr2 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                           UPnPCommandBuilder.UPNP_XMLPROPERTY_ACTION);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                           action);

    ele.addContent(propAddr2);

    return builder.build(ele);
  }

  private Command getCommandDeviceAndActionArbitraryPropertyOrder(String device, String action)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "upnp");


    Element propAddr2 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                           UPnPCommandBuilder.UPNP_XMLPROPERTY_ACTION);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                           action);

    ele.addContent(propAddr2);


    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                          UPnPCommandBuilder.UPNP_XMLPROPERTY_DEVICE);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                          device);

    ele.addContent(propAddr);

    return builder.build(ele);
  }

  private Command getCommandMissingDeviceProperty(String action)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "upnp");

    Element propAddr2 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                           UPnPCommandBuilder.UPNP_XMLPROPERTY_ACTION);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                           action);

    ele.addContent(propAddr2);

    return builder.build(ele);
  }

  private Command getCommandMissingActionProperty(String device)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "upnp");

    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                          UPnPCommandBuilder.UPNP_XMLPROPERTY_DEVICE);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                          device);

    ele.addContent(propAddr);

    return builder.build(ele);
  }

  private Command getCommandWithUPnPEventArguments(String device, String action)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "upnp");

    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                          UPnPCommandBuilder.UPNP_XMLPROPERTY_ACTION);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                          action);

    ele.addContent(propAddr);

    Element propAddr2 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                           UPnPCommandBuilder.UPNP_XMLPROPERTY_DEVICE);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                           device);

    ele.addContent(propAddr2);

    // random event args...

    Element propAddr3 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr3.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,"EventArgument1");
    propAddr3.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, "Value1");

    ele.addContent(propAddr3);


    Element propAddr4 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr4.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, "EventArgument2");
    propAddr4.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, "unknown");

    ele.addContent(propAddr4);
    return builder.build(ele);
  }

}
