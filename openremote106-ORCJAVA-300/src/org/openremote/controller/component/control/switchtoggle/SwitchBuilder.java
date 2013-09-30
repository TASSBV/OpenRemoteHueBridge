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
package org.openremote.controller.component.control.switchtoggle;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.component.ComponentBuilder;
import org.openremote.controller.component.control.Control;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.utils.Logger;

/**
 * TODO :
 *
 *  - see related tasks
 *     ORCJAVA-145  (http://jira.openremote.org/browse/ORCJAVA-145)
 *     ORCJAVA-146  (http://jira.openremote.org/browse/ORCJAVA-146)
 *     ORCJAVA-147  (http://jira.openremote.org/browse/ORCJAVA-147)
 * 
 * @author Handy.Wang 2009-10-23
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class SwitchBuilder extends ComponentBuilder
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * Common log category for all XML parsing related issues.
   */
  private final static Logger log = Logger.getLogger(Constants.XML_PARSER_LOG_CATEGORY);



  // Instance Fields ------------------------------------------------------------------------------

  private Deployer deployer;


  // Implements ComponentBuilder ------------------------------------------------------------------

  @Override public Control build(Element componentElement, String commandParam)
      throws InitializationException
  {
    Switch switchToggle = new Switch();

    if (!switchToggle.isValidActionWith(commandParam))
    {
       return switchToggle;
    }

    List<Element> operationElements = componentElement.getChildren();

    for (Element operationElement : operationElements)
    {
      if (hasIncludeSensorElement(operationElement))
      {
        try
        {
          Sensor sensor = deployer.getSensorFromComponentInclude(operationElement);
          
          switchToggle.setSensor(sensor);
        }
        catch (InitializationException e)
        {
          log.error(
              "Unable to initialize a sensor for a switch. The switch will not update " +
              "it's state correctly in response to external events. Error message: {0}", e.getMessage()
          );
        }
      }

      if (commandParam.equalsIgnoreCase(operationElement.getName()))
      {
        List<Element> commandRefElements = operationElement.getChildren();

        for (Element commandRefElement : commandRefElements)
        {
          String commandID = commandRefElement.getAttributeValue(Control.REF_ATTRIBUTE_NAME);
          Element commandElement = deployer.queryElementById(Integer.parseInt(commandID));
          Command command = commandFactory.getCommand(commandElement);
          switchToggle.addExecutableCommand((ExecutableCommand) command);
        }
      }
    }

    return switchToggle;
  }



  // Service Dependencies -------------------------------------------------------------------------

  /**
   * TODO : this dependency can/will be satisfied by ObjectBuilder implementation (see ORCJAVA-147)
   *
   * @param deployer
   */
  public void setDeployer(Deployer deployer)
  {
    this.deployer = deployer;
  }
}
