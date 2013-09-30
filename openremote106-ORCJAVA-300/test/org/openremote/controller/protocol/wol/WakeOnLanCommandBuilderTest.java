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
package org.openremote.controller.protocol.wol;

import junit.framework.Assert;
import static org.junit.Assert.fail;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.command.Command;
import org.openremote.controller.exception.CommandBuildException;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.protocol.infrared.IRCommand;
import org.openremote.controller.protocol.infrared.IRCommandBuilder;
/**
 * WakeOnLanCommandBuilder Test
 * 
 * @author Marcus Redeker
 *
 */
public class WakeOnLanCommandBuilderTest {

   private WakeOnLanCommandBuilder builder = null;

   @Before
   public void setUp() {
      builder = new WakeOnLanCommandBuilder();
   }

   @Test
   public void testCommandBuilder() {
      WakeOnLanCommand cmd = getCommand("11:22:33:44:55:66", "192.168.11.255");

      Assert.assertEquals("11:22:33:44:55:66", cmd.getMacAddress());
      Assert.assertEquals("192.168.11.255", cmd.getBroadcastIp());
   }

   @Test (expected = NoSuchCommandException.class)
   public void testCommandBuilderMissingParam1() {
      getCommand("11:22:33:44:55:66", "");
   }
   

   @Test (expected = NoSuchCommandException.class)
   public void testCommandBuilderMissingParam2() {
      getCommand("", "122.111.111.255");
   }

   private WakeOnLanCommand getCommand(String macAddress, String broadcastIp) {
      Element ele = new Element("command");
      ele.setAttribute("id", "1");
      ele.setAttribute("protocol", "wol");
      ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, "++");

      Element nameProp = new Element("property");
      nameProp.setAttribute("name", "macAddress");
      nameProp.setAttribute("value", macAddress);
      
      Element cmdProp = new Element("property");
      cmdProp.setAttribute("name", "broadcastIp");
      cmdProp.setAttribute("value", broadcastIp);

      ele.addContent(nameProp);
      ele.addContent(cmdProp);
      return (WakeOnLanCommand) builder.build(ele);
   }
}
