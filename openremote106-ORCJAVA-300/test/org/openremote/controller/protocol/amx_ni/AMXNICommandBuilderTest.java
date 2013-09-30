/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.controller.protocol.amx_ni;

import org.jdom.Element;
import org.junit.Assert;
import org.junit.Test;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;

/**
 * Basic unit tests for parsing XML elements in
 * {@link org.openremote.controller.protocol.amx.AMXNICommandBuilder} and building commands.
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class AMXNICommandBuilderTest {

   @Test public void testChannelCommands() {
      CommandBuilder cb = new AMXNICommandBuilder();
      Assert.assertTrue(cb.build(getCommandElement("ON", "1", "1", null, null, null, null, null)) instanceof ChannelCommand);
      Assert.assertTrue(cb.build(getCommandElement("OFF", "1", "1", null, null, null, null, null)) instanceof ChannelCommand);
      Assert.assertTrue(cb.build(getCommandElement("PULSE", "1", "1", null, null, null, null, null)) instanceof ChannelCommand);
      Assert.assertTrue(cb.build(getCommandElement("PULSE", "1", "1", null, null, "5", null, null)) instanceof ChannelCommand);
      Assert.assertTrue(cb.build(getCommandElement("CHANNEL_STATUS", "1", "1", null, null, null, null, null)) instanceof ChannelCommand);
    }
    
    @Test(expected = NoSuchCommandException.class)
    public void testChannelCommandInvalidDeviceIndex() {
      new AMXNICommandBuilder().build(getCommandElement("ON", "a", "1", null, null, null, null, null));
    }
    
    @Test(expected = NoSuchCommandException.class)
    public void testChannelCommandMissingDeviceIndex() {
      new AMXNICommandBuilder().build(getCommandElement("ON", null, "1", null, null, null, null, null));
    }

    @Test(expected = NoSuchCommandException.class)
    public void testChannelCommandInvalidChannel() {
      new AMXNICommandBuilder().build(getCommandElement("ON", "1", "a", null, null, null, null, null));
    }
    
    @Test(expected = NoSuchCommandException.class)
    public void testChannelCommandMissingChannel() {
      new AMXNICommandBuilder().build(getCommandElement("ON", "1", null, null, null, null, null, null));
    }

    @Test(expected = NoSuchCommandException.class)
    public void testChannelCommandInvalidPulseTime() {
      new AMXNICommandBuilder().build(getCommandElement("ON", "1", "1", null, null, "a", null, null));
    }
    

    @Test public void testLevelCommands() {
       CommandBuilder cb = new AMXNICommandBuilder();
       Assert.assertTrue(cb.build(getCommandElement("SEND_LEVEL", "1", null, "1", "50", null, null, null)) instanceof LevelCommand);
       Assert.assertTrue(cb.build(getCommandElement("LEVEL_STATUS", "1", null, "1", null, null, null, null)) instanceof LevelCommand);
     }
    
    @Test public void testLevelCommandDynamicElement() {
       CommandBuilder cb = new AMXNICommandBuilder();
       Element ele = getCommandElement("SEND_LEVEL", "1", null, "1", null, null, null, null);
       ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, "50");
       Assert.assertTrue(cb.build(ele) instanceof LevelCommand);
     }

    @Test(expected = NoSuchCommandException.class)
    public void testLevelCommandInvalidDynamicElement() {
       Element ele = getCommandElement("SEND_LEVEL", "1", null, "1", null, null, null, null);
       ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, "a");
       new AMXNICommandBuilder().build(ele);
    }
    
    @Test(expected = NoSuchCommandException.class)
    public void testLevelCommandInvalidDeviceIndex() {
      new AMXNICommandBuilder().build(getCommandElement("SEND_LEVEL", "a", null, "1", "50", null, null, null));
    }

    @Test(expected = NoSuchCommandException.class)
    public void testLevelCommandMissingDeviceIndex() {
      new AMXNICommandBuilder().build(getCommandElement("SEND_LEVEL", null, null, "1", "50", null, null, null));
    }

    @Test(expected = NoSuchCommandException.class)
    public void testLevelCommandInvalidLevel() {
      new AMXNICommandBuilder().build(getCommandElement("SEND_LEVEL", "1", null, "a", "50", null, null, null));
    }

    @Test(expected = NoSuchCommandException.class)
    public void testLevelCommandMissingLevel() {
      new AMXNICommandBuilder().build(getCommandElement("SEND_LEVEL", "1", null, null, "50", null, null, null));
    }

    @Test(expected = NoSuchCommandException.class)
    public void testLevelCommandInvalidValue() {
      new AMXNICommandBuilder().build(getCommandElement("SEND_LEVEL", "1", null, "1", "a", null, null, null));
    }

    @Test(expected = NoSuchCommandException.class)
    public void testLevelCommandMissingValue() {
      new AMXNICommandBuilder().build(getCommandElement("SEND_LEVEL", "1", null, "1", null, null, null, null));
    }
    
    
    @Test public void testCommandCommands() {
       CommandBuilder cb = new AMXNICommandBuilder();
       Assert.assertTrue(cb.build(getCommandElement("SEND_COMMAND", "1", null, null, "a command", null, null, null)) instanceof CommandCommand);
       Assert.assertTrue(cb.build(getCommandElement("COMMAND_READ", "1", null, null, null, null, null, null)) instanceof CommandCommand);
       Assert.assertTrue(cb.build(getCommandElement("COMMAND_READ", "1", null, null, null, null, "command", null)) instanceof CommandCommand);
       Assert.assertTrue(cb.build(getCommandElement("COMMAND_READ", "1", null, null, null, null, "command", "1")) instanceof CommandCommand);
     }

    @Test(expected = NoSuchCommandException.class)
    public void testCommandCommandInvalidDeviceIndex() {
      new AMXNICommandBuilder().build(getCommandElement("SEND_COMMAND", "a", null, null, "a command", null, null, null));
    }

    @Test(expected = NoSuchCommandException.class)
    public void testCommandCommandMissingDeviceIndex() {
      new AMXNICommandBuilder().build(getCommandElement("SEND_COMMAND", null, null, null, "a command", null, null, null));
    }

    @Test(expected = NoSuchCommandException.class)
    public void testCommandCommandMissingCommand() {
      new AMXNICommandBuilder().build(getCommandElement("SEND_COMMAND", "1", null, null, null, null, null, null));
    }
    
    @Test(expected = NoSuchCommandException.class)
    public void testCommandCommandInvalidStatusFilterGroup() {
      new AMXNICommandBuilder().build(getCommandElement("SEND_COMMAND", "1", null, null, "a command", null, null, "a"));
    }

    
    @Test public void testStringCommands() {
       CommandBuilder cb = new AMXNICommandBuilder();
       Assert.assertTrue(cb.build(getCommandElement("SEND_STRING", "1", null, null, "a string", null, null, null)) instanceof StringCommand);
       Assert.assertTrue(cb.build(getCommandElement("STRING_READ", "1", null, null, null, null, null, null)) instanceof StringCommand);
       Assert.assertTrue(cb.build(getCommandElement("STRING_READ", "1", null, null, null, null, "string", null)) instanceof StringCommand);
       Assert.assertTrue(cb.build(getCommandElement("STRING_READ", "1", null, null, null, null, "string", "1")) instanceof StringCommand);
     }

    @Test(expected = NoSuchCommandException.class)
    public void testStringCommandInvalidDeviceIndex() {
      new AMXNICommandBuilder().build(getCommandElement("SEND_STRING", "a", null, null, "a string", null, null, null));
    }

    @Test(expected = NoSuchCommandException.class)
    public void testStringCommandMissingDeviceIndex() {
      new AMXNICommandBuilder().build(getCommandElement("SEND_STRING", null, null, null, "a string", null, null, null));
    }

    @Test(expected = NoSuchCommandException.class)
    public void testCommandStringMissingCommand() {
      new AMXNICommandBuilder().build(getCommandElement("SEND_STRING", "1", null, null, null, null, null, null));
    }
    
    @Test(expected = NoSuchCommandException.class)
    public void testStringCommandInvalidStatusFilterGroup() {
      new AMXNICommandBuilder().build(getCommandElement("SEND_STRING", "1", null, null, "a string", null, null, "a"));
    }
    
    @Test(expected = NoSuchCommandException.class)
    public void testInvalidCommand() {
      new AMXNICommandBuilder().build(getCommandElement("INVALID", null, null, null, null, null, null, null));
    }

    
    private Element getCommandElement(String cmd, String deviceIndex, String channel, String level, String value, String pulseTime, String statusFilter, String statusFilterGroup) {
       Element ele = new Element("command");
       ele.setAttribute("id", "test");
       ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "amxni");

       if (cmd != null) {
         Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
         propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, AMXNICommandBuilder.AMX_NI_XMLPROPERTY_COMMAND);
         propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, cmd);
         ele.addContent(propAddr);
       }

       if (deviceIndex != null) {
         Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
         propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, AMXNICommandBuilder.AMX_NI_XMLPROPERTY_DEVICE_INDEX);
         propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, deviceIndex);
         ele.addContent(propAddr);
       }

       if (channel != null) {
         Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
         propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, AMXNICommandBuilder.AMX_NI_XMLPROPERTY_CHANNEL);
         propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, channel);
         ele.addContent(propAddr);
       }

       if (level != null) {
         Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
         propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, AMXNICommandBuilder.AMX_NI_XMLPROPERTY_LEVEL);
         propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, level);
         ele.addContent(propAddr);
       }

       if (value != null) {
         Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
         propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, AMXNICommandBuilder.AMX_NI_XMLPROPERTY_VALUE);
         propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, value);
         ele.addContent(propAddr);
       }

       if (pulseTime != null) {
          Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
          propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, AMXNICommandBuilder.AMX_NI_XMLPROPERTY_PULSE_TIME);
          propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, pulseTime);
          ele.addContent(propAddr);
       }
       
       if (statusFilter != null) {
          Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
          propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, AMXNICommandBuilder.AMX_NI_XMLPROPERTY_STATUS_FILTER);
          propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, statusFilter);
          ele.addContent(propAddr);
        }

       if (statusFilterGroup != null) {
          Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
          propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, AMXNICommandBuilder.AMX_NI_XMLPROPERTY_STATUS_FILTER_GROUP);
          propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, statusFilterGroup);
          ele.addContent(propAddr);
        }

       return ele;
     }

}
