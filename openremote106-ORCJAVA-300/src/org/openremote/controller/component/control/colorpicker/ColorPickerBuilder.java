/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.component.control.colorpicker;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.component.Component;
import org.openremote.controller.component.ComponentBuilder;
import org.openremote.controller.component.control.Control;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.service.Deployer;


public class ColorPickerBuilder extends ComponentBuilder {
   
   // Instance Fields ------------------------------------------------------------------------------
   
   private Deployer deployer;

   // Implements ComponentBuilder ------------------------------------------------------------------

   @SuppressWarnings("unchecked")
   @Override
   public Component build(Element componentElement, String commandParam) throws InitializationException {
      ColorPicker cp = new ColorPicker();
      if (cp.isValidActionWith(commandParam)) {
         List<Element> commandRefElements = componentElement.getChildren();
         for (Element commandRefElement : commandRefElements) {
             String commandID = commandRefElement.getAttributeValue(Control.REF_ATTRIBUTE_NAME);
             Element commandElement = deployer.queryElementById(Integer.parseInt(commandID));
             commandElement.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, commandParam);
             ExecutableCommand command = (ExecutableCommand) commandFactory.getCommand(commandElement);
             cp.addExecutableCommand(command);
         }
      }
      return cp;
   }

   // Service Dependencies -------------------------------------------------------------------------

   /**
    * TODO : this dependency can/will be satisfied by ObjectBuilder implementation (see ORCJAVA-155)
    *
    * @param deployer
    */
   public void setDeployer(Deployer deployer)
   {
     this.deployer = deployer;
   }

}
