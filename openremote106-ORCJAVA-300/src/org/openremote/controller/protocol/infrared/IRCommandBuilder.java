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
package org.openremote.controller.protocol.infrared;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.CommandBuildException;
import org.openremote.controller.utils.CommandUtil;


/**
 * The IREvent Builder which can build a IREvent from a DOM Element in controller.xml.
 * 
 * @author Dan 2009-4-3
 */
public class IRCommandBuilder implements CommandBuilder {

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public Command build(Element element) {
      IRCommand irCommand = new IRCommand();
      String command = "";
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());
      String name = "";
      for(Element ele : propertyEles){
         if("name".equals(ele.getAttributeValue("name"))){
            name = ele.getAttributeValue("value");
         } else if ("command".equals(ele.getAttributeValue("name"))) {
            command = ele.getAttributeValue("value");
         }
      }
      if ("".equals(command.trim()) || "".equals(name.trim())) {
         throw new CommandBuildException("Cannot build a IREvent with empty property : command=" + command + ",name=" + name);
      } else {
         irCommand.setCommand(CommandUtil.parseStringWithParam(element, command));
         irCommand.setName(name);
      }
      return irCommand;
   }

}
