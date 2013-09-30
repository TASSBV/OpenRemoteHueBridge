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
package org.openremote.controller.protocol.telnet;

import junit.framework.Assert;

import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.command.Command;
import org.openremote.controller.protocol.telnet.TelnetCommand;
import org.openremote.controller.protocol.telnet.TelnetCommandBuilder;
/**
 * TelnetCommandBuilder Test
 * 
 * @author Javen
 *
 */
public class TelnetCommandBuilderTest {
   private TelnetCommandBuilder builder = null;

   @Before
   public void setUp() {
      builder = new TelnetCommandBuilder();
   }

   @Test
   public void testTelnet() {
      TelnetCommand cmd = getCommand("192.168.1.1", "23", "test");
      Assert.assertEquals("192.168.1.1", cmd.getIp());
      Assert.assertEquals(23, cmd.getPort().intValue());
      Assert.assertEquals("test", cmd.getCommand());
   }
   
   @Test
   public void testTelnetWithParam() {
      TelnetCommand cmd = getCommand("192.168.1.1", "23", "light1_${param}");
      Assert.assertEquals("192.168.1.1", cmd.getIp());
      Assert.assertEquals(23, cmd.getPort().intValue());
      Assert.assertEquals("light1_255", cmd.getCommand());
   }

   private TelnetCommand getCommand(String address, String port, String cmd) {
      Element ele = new Element("command");
      ele.setAttribute("id", "test");
      ele.setAttribute("protocol", "telnet");
      ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, "255");

      Element propName = new Element("property");
      propName.setAttribute("name", "name");
      propName.setAttribute("value", "testName");

      Element propAddr = new Element("property");
      propAddr.setAttribute("name", "ipAddress");
      propAddr.setAttribute("value", address);

      Element propPort = new Element("property");
      propPort.setAttribute("name", "port");
      propPort.setAttribute("value", port);
      
      Element propCommand = new Element("property");
      propCommand.setAttribute("name", "command");
      propCommand.setAttribute("value", cmd);

      ele.addContent(propName);
      ele.addContent(propAddr);
      ele.addContent(propPort);
      ele.addContent(propCommand);

      return (TelnetCommand) builder.build(ele);
   }
}
