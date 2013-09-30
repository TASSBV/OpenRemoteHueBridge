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
package org.openremote.controller.deployer;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

import org.jdom.Element;
import org.openremote.controller.model.Command;
import org.openremote.controller.exception.XMLParsingException;
import org.openremote.controller.command.CommandFactory;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Version20CommandBuilder implements DeviceProtocolBuilder
{

  private CommandFactory commandFactory;


  public Version20CommandBuilder(CommandFactory commandFactory)
  {
    this.commandFactory = commandFactory;
  }

  @Override public Command build(Element commandElement) throws XMLParsingException
  {
    try
    {
      int commandID = Integer.parseInt(commandElement.getAttribute("id").getValue());

      String protocolType = commandElement.getAttribute("protocol").getValue();

      List<Element> propertyElements = getPropertyElements(commandElement);

      Map<String, String> properties = getCommandProperties(propertyElements);

      return new Command(commandFactory, commandID, protocolType, properties);
    }

    catch (NumberFormatException e)
    {
      throw new XMLParsingException("Command ids must be integers : {0}", e, e.getMessage());
    }
  }


  protected Map<String, String> getCommandProperties(List<Element> propertyElements)
  {
    Map<String, String> properties = new HashMap<String, String>();

    for (Element property : propertyElements)
    {


      String name = property.getAttribute("name").getValue();
      String value = property.getAttribute("value").getValue();

      properties.put(name, value);
    }

    return properties;
  }

  /**
   * Isolated this one method call to suppress warnings (JDOM API does not use generics).
   *
   * @param rootElement  the root element which child elements are retrieved
   *
   * @return  the child elements of the root element
   */
  @SuppressWarnings("unchecked")
  private List<Element> getPropertyElements(Element rootElement)
  {
    return rootElement.getChildren("property", ModelBuilder.SchemaVersion.OPENREMOTE_NAMESPACE);
  }

}

