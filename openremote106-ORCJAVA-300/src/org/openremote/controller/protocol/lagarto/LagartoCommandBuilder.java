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

package org.openremote.controller.protocol.lagarto;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.utils.CommandUtil;


/**
 * CommandBuilder subclass for Lagarto command
 */
public class LagartoCommandBuilder implements CommandBuilder
{

  public final static String LAGARTO_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "lagarto";
  private final static Logger logger = Logger.getLogger(LAGARTO_PROTOCOL_LOG_CATEGORY);

  public static ControllerConfiguration controllerConfig;

  /**
   * Class constructor
   */
  public LagartoCommandBuilder()
  {
  }

  /**
  * {@inheritDoc}
  */
  @SuppressWarnings("unchecked")
  public Command build(Element element)
  {
    List<Element> propertyEles = element.getChildren("property", element.getNamespace());
    String networkName = null;
    String epId = null;
    String epValue = null;

    // read values from config xml
    for (Element ele : propertyEles)
    {
      String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
      String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);
      if ("network".equals(elementName))
        networkName = elementValue;
      else if ("epid".equals(elementName))
        epId = elementValue;
      else if ("value".equals(elementName))
        epValue = CommandUtil.parseStringWithParam(element, elementValue);
    }

    LagartoCommand cmd = new LagartoCommand(networkName, epId, epValue);

    return cmd;
  }

  /**
   * Get controller configuration
   */
  public ControllerConfiguration getConfiguration()
  {
    return controllerConfig;
  }

  /**
   * Set controller configuration
   */
  public void setConfiguration(ControllerConfiguration configuration)
  {
    controllerConfig = configuration;
  }
}
