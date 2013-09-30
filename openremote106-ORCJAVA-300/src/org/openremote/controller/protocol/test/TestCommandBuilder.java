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
package org.openremote.controller.protocol.test;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;

public class TestCommandBuilder implements CommandBuilder {
   
   @SuppressWarnings("unchecked")
   @Override
   public Command build(Element element) {
      Element commandPropertyElement = null;
      
      List<Element> commandProperyElements = (List<Element>) element.getChildren("property", element.getNamespace());
      for (Element propertyElement : commandProperyElements) {
         if ("command".equals(propertyElement.getAttributeValue("name"))) {
            commandPropertyElement = propertyElement;
         }
      }
      
      if (commandPropertyElement != null) {       
         String commandStr = commandPropertyElement.getAttributeValue("value");
         
         TestCommandType testCommandType = null;
         
         if (TestCommandType.SWITCH_ON.isEqual(commandStr))
            testCommandType = TestCommandType.SWITCH_ON;
         else if (TestCommandType.SWITCH_OFF.isEqual(commandStr))
            testCommandType = TestCommandType.SWITCH_OFF;
         else if (TestCommandType.STATUS.isEqual(commandStr)) {
            testCommandType = TestCommandType.STATUS;
         } else if (TestCommandType.NUMBER_COMAND.isEqual(commandStr)) {
            String dynamicCommandValueForSlider = element.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);
            commandStr = dynamicCommandValueForSlider;
//            Integer.parseInt(commandStr) >= 0 && Integer.parseInt(commandStr) <= 100
            testCommandType = TestCommandType.NUMBER_COMAND;
         }else {
            throw new NoSuchCommandException("Couldn't find command " + commandStr + " in TestCommandType.");
         }
         
         TestCommand testCommand = null;
         testCommand = new TestCommand(testCommandType);
         testCommand.setCommandValue(commandStr);
         return testCommand;
      }
      return null;
   }

}
