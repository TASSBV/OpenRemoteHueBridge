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
package org.openremote.controller.component.onlysensory;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.component.Component;
import org.openremote.controller.component.ComponentBuilder;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.utils.Logger;

/**
 * TODO :
 *
 *  * relevant tasks :
 *   - ORCJAVA-153 : http://jira.openremote.org/browse/ORCJAVA-153
 *   - ORCJAVA-167 : http://jira.openremote.org/browse/ORCJAVA-167
 * 
 * @author Handy
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ImageBuilder extends ComponentBuilder
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * Common log category for all XML parsing related issues.
   */
  private final static Logger log = Logger.getLogger(Constants.XML_PARSER_LOG_CATEGORY);


  // Instance Fields ------------------------------------------------------------------------------

  private Deployer deployer;



  // Implements ComponentBuilder ------------------------------------------------------------------

  @Override public Component build(Element componentElement, String commandParam)
  {
    Image image = new Image();

    if (!image.isValidActionWith(commandParam))
    {
      return image;
    }

    List<Element> operationElements = componentElement.getChildren();

    for (Element operationElement : operationElements)
    {
      if (hasIncludeSensorElement(operationElement))
      {
        try
        {
          Sensor sensor = deployer.getSensorFromComponentInclude(operationElement);
          
          image.setSensor(sensor);
        }
        catch (InitializationException e)
        {
          log.error(
              "Unable to initialize a sensor for an image component. Some image components on " +
              "panels will not update correctly. Error message: {0}", e.getMessage()
          );
        }
      }
    }

    return image;
  }



  // Service Dependencies -------------------------------------------------------------------------

  /**
   * TODO : this dependency can/will be satisfied by ObjectBuilder implementation (see ORCJAVA-153)
   *
   * @param deployer
   */
  public void setDeployer(Deployer deployer)
  {
    this.deployer = deployer;
  }
  
}
