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
package org.openremote.controller.protocol.upnp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.UPnP;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.command.Command;
import org.openremote.controller.exception.NoSuchCommandException;
import org.jdom.Element;

/**
 * UPnPCommandBuilder is responsible for parsing the XML model from controller.xml and create
 * appropriate Command objects for it. <p>
 *
 * The structure of a UPnP command XML snippet from controller.xml is shown below:
 *
 * <pre>{@code
 * <command protocol = "upnp" >
 *   <property name = "device" value = "xxx"/>
 *   <property name = "action" value = "yyy"/>
 *   <property name = "argname1" value = "..."/>
 *   <property name = "argname2" value = "..."/>
 *   <property name = "argnameX" value = "..."/>
 * </command>
 * }</pre>
 *
 * Properties 'device' and 'action' are mandatory. Any other property name is considered
 * an UPnP Event argument.
 *
 * @author Mathieu Gallissot
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class UPnPCommandBuilder implements CommandBuilder
{
  /*
   * NOTE:
   *
   *   UPnP implementation has not been tested completely.
   *                                                          [JPL]
   */

  // Constants ------------------------------------------------------------------------------------


  /**
   * String constant for parsing UPnP protocol XML entries from controller.xml file.
   *
   * This constant is the expected property name value for UPnP device
   * (<code>{@value}</code>):
   *
   * <pre>{@code
   * <command protocol = "upnp" >
   *   <property name = "device" value = "..."/>
   *   <property name = "action" value = "ON"/>
   * </command>
   * }</pre>
   */
  public final static String UPNP_XMLPROPERTY_DEVICE = "device";

  /**
   * String constant for parsing UPnP protocol XML entries from controller.xml file.
   *
   * This constant is the expected property name value for UPnP action
   * (<code>{@value}</code>):
   *
   * <pre>{@code
   * <command protocol = "upnp" >
   *   <property name = "device" value = "..."/>
   *   <property name = "action" value = "ON"/>
   * </command>
   * }</pre>
   */
  public final static String UPNP_XMLPROPERTY_ACTION = "action";


  // Instance Fields ------------------------------------------------------------------------------

	private ControlPoint controlPoint;


  // Constructors ---------------------------------------------------------------------------------

	/**
	 * Constructor of the UPnP Event. It initialize the UPnP stack by :
   *
	 * <ul>
	 * <li>Initializing a proper parser (UPnPParser.class) and setting this
	 * parser for the stack</li>
	 * <li>Initializing the UPnP control point and starting it. It will start
	 * the discovery of compatible devices on the network</li>
	 * </ul>
   *
	 * No other configuration parameters are handled here.
	 */
	public UPnPCommandBuilder()
  {
		UPnP.setXMLParser(new UPnPParser());
		this.controlPoint = new ControlPoint();
		this.controlPoint.start();
	}


  // Implements CommandBuilder --------------------------------------------------------------------

  /**
   * Parses the UPnP command XML snippets and builds a corresponding UPnP command instance.  <p>
   *
   * The expected XML structure is:
   *
   * <pre>{@code
   * <command protocol = "upnp" >
   *   <property name = "device" value = "xxx"/>
   *   <property name = "action" value = "yyy"/>
   *   <property name = "argname1" value = "..."/>
   *   <property name = "argname2" value = "..."/>
   *   <property name = "argnameX" value = "..."/>
   * </command>
   * }</pre>
   *
   * Properties other than 'device' and 'action' are passed on as UPnP event arguments.
   *
   * @see UPnPCommand
   * @see org.openremote.controller.command.CommandBuilder#build(org.jdom.Element)
   *
   * @throws org.openremote.controller.exception.NoSuchCommandException
   *            if the UPnP command instance cannot be constructed from the XML snippet
   *            for any reason
   *
   * @return an immutable UPnP command instance with configured properties set
   */
	public Command build(Element element)
  {
    String upnpDevice = null;
    String upnpAction = null;
    Map<String, String> upnpEventArguments = new HashMap<String, String>(3);


    // grab all the properties which are child elements...

    List<Element> propertyElements = element.getChildren(XML_ELEMENT_PROPERTY, element.getNamespace());

    for (Element el : propertyElements)
    {
      String upnpPropertyName = el.getAttributeValue(XML_ATTRIBUTENAME_NAME);
      String upnpPropertyValue = el.getAttributeValue(XML_ATTRIBUTENAME_VALUE);

      if (UPNP_XMLPROPERTY_DEVICE.equalsIgnoreCase(upnpPropertyName))
      {
        upnpDevice = upnpPropertyValue;
      }
      else if (UPNP_XMLPROPERTY_ACTION.equalsIgnoreCase(upnpPropertyName))
      {
        upnpAction = upnpPropertyValue;
      }
      else
      {
        // anything that's not a device or action property will be taken as an event argument...

        upnpEventArguments.put(upnpPropertyName, upnpPropertyValue);
      }
    }


    // sanity checks...

    if (upnpDevice == null || "".equals(upnpDevice))
    {
      throw new NoSuchCommandException(
          "UPnP command must have a '" + UPNP_XMLPROPERTY_DEVICE + "' property."
      );
    }

    if (upnpAction == null || "".equals(upnpAction))
    {
      throw new NoSuchCommandException(
          "UPnP command must have a '" + UPNP_XMLPROPERTY_ACTION + "' property."
      );
    }

    // done!
    
		return new UPnPCommand(this.controlPoint, upnpDevice, upnpAction, upnpEventArguments);
	}

}
