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
package org.openremote.controller.component.control.slider;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.component.Component;
import org.openremote.controller.component.ComponentBuilder;
import org.openremote.controller.component.control.Control;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.exception.XMLParsingException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.utils.Logger;

/**
 * TODO :
 *
 * relevant tasks :
 *   - ORCJAVA-151 : http://jira.openremote.org/browse/ORCJAVA-151
 *   - ORCJAVA-166 : http://jira.openremote.org/browse/ORCJAVA-166
 *
 * @author Handy.Wang 2009-11-10
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class SliderBuilder extends ComponentBuilder
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
    Slider slider = new Slider();

    if (!slider.isValidActionWith(commandParam))
    {
      return slider;
    }

    List<Element> operationElements = componentElement.getChildren(); 

    for (Element operationElement : operationElements)
    {
      /* sensor Element */
      if (hasIncludeSensorElement(operationElement))
      {
        Sensor sensor = deployer.getSensorFromComponentInclude(operationElement);

        slider.setSensor(sensor);

        continue;
      }

      /* non-sensor Element */
      if (Slider.EXECUTE_CONTENT_ELEMENT_NAME.equalsIgnoreCase(operationElement.getName()))
      {
        Element commandRefElement = (Element) operationElement.getChildren().get(0);
        String commandID = commandRefElement.getAttributeValue(Component.REF_ATTRIBUTE_NAME);
        Element commandElement = deployer.queryElementById(Integer.parseInt(commandID));
        commandElement.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, commandParam);
        Command command = commandFactory.getCommand(commandElement);
        slider.addExecutableCommand((ExecutableCommand) command);
      }

      else
      {
        throw new XMLParsingException(
            "Element <{0}> not supported in slider.", operationElement.getName()
        );
      }
    }

    return slider;
  }


  // Service Dependencies -------------------------------------------------------------------------

  /**
   * TODO : this dependency can/will be satisfied by ObjectBuilder implementation (see ORCJAVA-151)
   *
   * @param deployer
   */
  public void setDeployer(Deployer deployer)
  {
    this.deployer = deployer;
  }
}
