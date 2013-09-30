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
package org.openremote.controller.protocol.socket;

import junit.framework.Assert;

import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.command.Command;
import org.openremote.controller.protocol.socket.TCPSocketCommand;
import org.openremote.controller.protocol.socket.TCPSocketCommandBuilder;
/**
 * TCPSocketCommandBuilder Test.
 * 
 * @author Javen
 *
 */
public class TCPSocketCommandBuilderTest {
   private TCPSocketCommandBuilder builder = new TCPSocketCommandBuilder();;

   @Before
   public void setUp() {
   }

   @Test
   public void testSocketCommandBuilder() {
      TCPSocketCommand cmd = getCommand("192.168.0.1", "9090", "test");

      Assert.assertEquals(cmd.getIp(), "192.168.0.1");
      Assert.assertEquals(cmd.getPort(), "9090");
      Assert.assertEquals(cmd.getCommand(), "test");
      Assert.assertEquals(cmd.getName(), "testName");
   }
   
   
   /**
    * Test ${param}
    */
   @Test
   public void testSocketCommandWithParam() {
      TCPSocketCommand cmd = getCommand("192.168.0.1", "9090", "light1_${param}");
      
      Assert.assertEquals(cmd.getIp(), "192.168.0.1");
      Assert.assertEquals(cmd.getPort(), "9090");
      Assert.assertEquals(cmd.getCommand(), "light1_255");
      Assert.assertEquals(cmd.getName(), "testName");
   }

   private TCPSocketCommand getCommand(String address, String port, String command) {
      Element ele = new Element("command");
      ele.setAttribute("id", "test");
      ele.setAttribute("protocol", "tcpSocket");
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
      propCommand.setAttribute("value", command);

      ele.addContent(propName);
      ele.addContent(propAddr);
      ele.addContent(propPort);
      ele.addContent(propCommand);

      return (TCPSocketCommand) builder.build(ele);
   }
}
