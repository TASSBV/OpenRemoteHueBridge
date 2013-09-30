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

import java.util.List;
import java.text.MessageFormat;

import org.jdom.Element;
import org.openremote.controller.command.CommandFactory;
import org.openremote.controller.command.Command;
import org.openremote.controller.component.control.Control;
import org.openremote.controller.exception.XMLParsingException;
import org.openremote.controller.exception.ConfigurationException;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.Constants;
import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.protocol.EventProducer;


/**
 * TODO : see ORCJAVA-143 (http://jira.openremote.org/browse/ORCJAVA-143)
 * 
 * @author Handy.Wang 2009-10-15
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
@Deprecated public abstract class ComponentBuilder
{
  protected CommandFactory commandFactory;



  protected boolean hasIncludeSensorElement(Element childElementOfControl) 
  {
    boolean hasIncludeElement =
        Control.INCLUDE_ELEMENT_NAME.equalsIgnoreCase(childElementOfControl.getName());

    boolean isIncludeSensor = Control.INCLUDE_TYPE_SENSOR.equals(
        childElementOfControl.getAttributeValue(Control.INCLUDE_TYPE_ATTRIBUTE_NAME)
    );

    return hasIncludeElement && isIncludeSensor;
  }


  /**
   * TODO : Builds the component.
   *
   * @param componentElement
   * @param commandParam
   *
   * @throws XMLParsingException
   *
   * @return TODO
   */
  public abstract Component build(Element componentElement, String commandParam)
      throws InitializationException;


  public void setCommandFactory(CommandFactory commandFactory)
  {
      this.commandFactory = commandFactory;
  }

}
