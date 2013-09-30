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
package org.openremote.controller.protocol.datetime;

import java.util.HashMap;
import java.util.TimeZone;

import junit.framework.Assert;
import static org.junit.Assert.fail;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.command.Command;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.exception.CommandBuildException;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.model.sensor.StateSensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.model.sensor.StateSensor.DistinctStates;
import org.openremote.controller.protocol.infrared.IRCommand;
import org.openremote.controller.protocol.infrared.IRCommandBuilder;
/**
 * DateTimeCommandBuilder Test
 * 
 * @author Marcus Redeker
 *
 */
public class DateTimeCommandBuilderTest {

   private DateTimeCommandBuilder builder = null;

   @Before
   public void setUp() {
      builder = new DateTimeCommandBuilder();
   }

   @Test
   public void testCommandBuilder() {
      Element ele = new Element("command");
      ele.setAttribute("id", "1");
      ele.setAttribute("protocol", "datetime");
      ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, "++");

      Element nameProp = new Element("property");
      nameProp.setAttribute("name", "command");
      nameProp.setAttribute("value", "date");
      
      Element cmdProp = new Element("property");
      cmdProp.setAttribute("name", "timezone");
      cmdProp.setAttribute("value", "Europe/Berlin");

      ele.addContent(nameProp);
      ele.addContent(cmdProp);
      
      DateTimeCommand cmd = (DateTimeCommand)builder.build(ele);

      Assert.assertEquals("date", cmd.getCommand());
      Assert.assertEquals("Europe/Berlin", cmd.getTimezone().getID());
   }
   
   @Test
   public void testCommandBuilderUnknownTimezone() {
      Element ele = new Element("command");
      ele.setAttribute("id", "1");
      ele.setAttribute("protocol", "datetime");
      ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, "++");

      Element nameProp = new Element("property");
      nameProp.setAttribute("name", "command");
      nameProp.setAttribute("value", "date");
      
      Element cmdProp = new Element("property");
      cmdProp.setAttribute("name", "timezone");
      cmdProp.setAttribute("value", "Europe/Bielefeld");

      ele.addContent(nameProp);
      ele.addContent(cmdProp);
      
      DateTimeCommand cmd = (DateTimeCommand)builder.build(ele);

      Assert.assertEquals("date", cmd.getCommand());
      Assert.assertEquals("GMT", cmd.getTimezone().getID());
   }
   
   @Test
   public void testCommandBuilderSunriseParams() {
      Element ele = new Element("command");
      ele.setAttribute("id", "1");
      ele.setAttribute("protocol", "datetim");
      ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, "++");

      Element prop = new Element("property");
      prop.setAttribute("name", "command");
      prop.setAttribute("value", "sunrise");
      ele.addContent(prop);
      
      prop = new Element("property");
      prop.setAttribute("name", "latitude");
      prop.setAttribute("value", "11.111");
      ele.addContent(prop);
      
      prop = new Element("property");
      prop.setAttribute("name", "longitude");
      prop.setAttribute("value", "11.111");
      ele.addContent(prop);
      
      prop = new Element("property");
      prop.setAttribute("name", "timezone");
      prop.setAttribute("value", "Europe/Berlin");
      ele.addContent(prop);
      
      DateTimeCommand cmd = (DateTimeCommand)builder.build(ele);

      Assert.assertEquals("sunrise", cmd.getCommand());
      Assert.assertEquals("Europe/Berlin", cmd.getTimezone().getID());
   }

   @Test (expected=NoSuchCommandException.class)
   public void testCommandBuilderSunriseMissingParam() {
      Element ele = new Element("command");
      ele.setAttribute("id", "1");
      ele.setAttribute("protocol", "datetim");
      ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, "++");

      Element prop = new Element("property");
      prop.setAttribute("name", "command");
      prop.setAttribute("value", "sunrise");
      ele.addContent(prop);
      
      prop = new Element("property");
      prop.setAttribute("name", "latitude");
      prop.setAttribute("value", "11.111");
      ele.addContent(prop);
      
      prop = new Element("property");
      prop.setAttribute("name", "timezone");
      prop.setAttribute("value", "Europe/Berlin");
      ele.addContent(prop);
      
      builder.build(ele);
   }
}
