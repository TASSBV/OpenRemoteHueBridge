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
package org.openremote.controller.protocol.isy99;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;

/**
 * Command builder for the ISY-99 protocol, which communicates with the Universal Devices
 * ISY-99 Insteon-to-HTTP bridge.
 * 
 * @author <a href="mailto:andrew.puch.1@gmail.com">Andrew Puch</a>
 * @author <a href="mailto:aball@osintegrators.com">Andrew D. Ball</a>
 */
public class Isy99CommandBuilder implements CommandBuilder
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * A common log category name intended to be used across all classes related
   * to ISY-99 implementation.
   */
  public final static String ISY99_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "isy99";

  public final static String ISY99_XMLPROPERTY_ADDRESS = "address";

  public final static String ISY99_XMLPROPERTY_COMMAND = "command";

  // Class Members --------------------------------------------------------------------------------

  private final static Logger log = Logger.getLogger(Isy99CommandBuilder.ISY99_LOG_CATEGORY);

  // Instance Fields ------------------------------------------------------------------------------
  
  private String hostname;
  private String username;
  private String password;

  // Constructors ---------------------------------------------------------------------------------

  /**
   * @param hostname hostname or IP address for the ISY-99
   * @param username username for authentication to the ISY-99
   * @param password password for authentication to the ISY-99
   */
  public Isy99CommandBuilder(String hostname, String username, String password)
  {
    this.hostname = hostname;
    this.username = username;
    this.password = password;
  }

  // Implements EventBuilder ----------------------------------------------------------------------

  /**
   * Parses the ISY-99 command XML snippets and builds a corresponding ISY-99 command instance.
   * <p>
   * 
   * The expected XML structure is:
   * 
   * <pre>
   *
   * <command protocol = "isy99" >
   *   <property name = "address" value = "17 54 AE 1"/>
   *   <property name = "command" value = "DON"/>
   * </command>
   *
   * </pre>
   *
   *
   * For reading a device's status, the "command" property can be set to whatever you want -- it will
   * be ignored.
   *
   * The write commands are named the same as they are for the ISY-99 REST API
   * (http://www.universal-devices.com/mwiki/index.php?title=ISY-99i_Series_INSTEON:REST_Interface).
   *
   * For changing the illumination level on a dimmer, DON should be used.
   *
   * For turning on a switch, DON should also be used.
   *
   * For turning off a switch, DOF should be used.
   *
   * Additional properties not listed here are ignored.
   * 
   * @throws NoSuchCommandException
   *             if the ISY-99 command instance cannot be
   *             constructed from the XML snippet for any reason
   * 
   * @return an immutable ISY-99 command instance with known
   *         configured properties set
   */
  @Override
  public Command build(Element element)
  {
    String address = null;
    String command = null;

    @SuppressWarnings("unchecked")
    List<Element> propertyElements = element.getChildren(XML_ELEMENT_PROPERTY,
        element.getNamespace());

    for (Element el : propertyElements)
    {
      String propertyName = el.getAttributeValue(XML_ATTRIBUTENAME_NAME);
      String propertyValue = el.getAttributeValue(XML_ATTRIBUTENAME_VALUE);

      if (ISY99_XMLPROPERTY_ADDRESS.equalsIgnoreCase(propertyName))
      {
        address = propertyValue;
      }
      else if (ISY99_XMLPROPERTY_COMMAND.equalsIgnoreCase(propertyName))
      {
        command = propertyValue;
      }
      else
      {
        log.warn("Unknown ISY-99 property '<" + XML_ELEMENT_PROPERTY + " " +
            XML_ATTRIBUTENAME_NAME + " = \"" + propertyName + "\" " + XML_ATTRIBUTENAME_VALUE +
            " = \"" + propertyValue + "\"/>'.");
      }
    }

    if (command == null || "".equals(command))
    {
      throw new NoSuchCommandException("ISY-99 command must have a '" + ISY99_XMLPROPERTY_COMMAND + "' property.");
    }

    if (address == null || "".equals(address))
    {
      throw new NoSuchCommandException("ISY-99 address must have a '" + ISY99_XMLPROPERTY_ADDRESS + "' property.");
    }

    String commandParam = element.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);

    Isy99Command cmd = null;

    if (commandParam == null || commandParam.equals(""))
    {
      cmd = new Isy99Command(hostname, username, password, address, command);
    }
    else
    {
      cmd = new Isy99Command(hostname, username, password, address, command, commandParam);
    }

    return cmd; 
  }

}
