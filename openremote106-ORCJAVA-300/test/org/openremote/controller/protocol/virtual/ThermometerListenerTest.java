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

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.command.Command;
import org.jdom.Element;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ThermometerListenerTest
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
   *
   */
  @Test public void testStatusDefaultValue()
  {
    EventListener listener = createThermoListener();


  }
  



  // Helpers --------------------------------------------------------------------------------------

  /**
   * Returns a temperature listener (event listener) through virtual command builder.
   *
   * @return  event listener for temperature sensor
   */
  private EventListener createThermoListener()
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");

    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "virtual");

    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, "address");
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, "test temperature listener");

    ele.addContent(propAddr);


    Element propAddr2 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, "command");
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, "TemperatureSensor");

    ele.addContent(propAddr2);


    Command cmd = builder.build(ele);

    if (!(cmd instanceof EventListener))
    {
      Assert.fail("Was expecting a EventListener type, got " + cmd.getClass());

      return null;
    }
    else
    {
      return (EventListener)cmd;
    }
  }
}

