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
package org.openremote.controller.component;

import java.util.Map;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.exception.ConfigurationException;
import org.openremote.controller.exception.InitializationException;


/**
 * TODO : A factory for creating Component objects.
 * 
 * @author Handy.Wang 2009-10-15
 */
public class ComponentFactory
{

  private Map<String, ComponentBuilder> componentBuilders;

  public Component getComponent(Element componentElement, String commandParam)
      throws InitializationException
  {
    String componentType = componentElement.getName();

    if(!componentBuilders.keySet().contains(componentType))
    {
      throw new ConfigurationException(
          "Component builder in {0} was not found for component type <{1}>.",
          Constants.BEAN_BINDING_CONFIGURATION_XML, componentElement.getName()
      );
    }

    ComponentBuilder componentBuilder = componentBuilders.get(componentType);
    
    return componentBuilder.build(componentElement, commandParam);
  }

  public void setComponentBuilders(Map<String, ComponentBuilder> componentBuilders)
  {
    this.componentBuilders = componentBuilders;
  }

}
