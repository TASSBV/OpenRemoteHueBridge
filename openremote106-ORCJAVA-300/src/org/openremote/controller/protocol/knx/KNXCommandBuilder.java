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
package org.openremote.controller.protocol.knx;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.command.CommandParameter;
import org.openremote.controller.exception.ConversionException;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.protocol.knx.datatype.DataPointType;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.Constants;


/**
 * KNXCommandBuilder is responsible for parsing the XML model from controller.xml and create
 * appropriate Command objects for it. <p>
 *
 * The structure of a KNX command XML snippet from controller.xml is shown below:
 *
 * <pre>{@code
 * <command protocol = "knx" >
 *   <property name = "groupAddress" value = "x/x/x"/>
 *   <property name = "command" value = "ON|OFF|STATUS"/>
 * </command>
 * }</pre>
 *
 * The protocol identifier is "knx" as is shown in the command element's protocol attribute. <p>
 *
 * Nested are a number of properties to be included with the KNX command. Properties named
 * {@link #KNX_XMLPROPERTY_COMMAND} and {@link #KNX_XMLPROPERTY_GROUPADDRESS} are mandatory. <p>
 *
 * KNX group address values should follow the established convention of three level addressing
 * with a forward slash as separator character for the digits. The valid values for command
 * property are implemented in {@link GroupValueRead} and {@link GroupValueWrite} classes --
 * command values can be localized.
 *
 * @see org.openremote.controller.command.CommandBuilder
 * @see KNXCommand
 * @see GroupValueRead
 * @see GroupValueWrite
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class KNXCommandBuilder implements CommandBuilder
{
  /*
   * Implementation Notes:
   *
   *  Open questions:
   *
   *  1) When should KNX discovery be executed in Controller's lifecycle?
   *     - KNX gateway is a fairly static thing, so should be at bootup
   *     - However, should probably be executed asynchronously to avoid extending bootup times
   *        * properly done, discovery is going to rely on certain timeouts
   *        * timeouts should not affect bootup time especially on deployments where knx is not
   *          present
   *        * ultimately you could remove all KNX related bean config if not needed but acting
   *          asynchronously in the background is more user friendly (not strictly necessary for
   *          user to modify component config)
   *     - For development scenarios and to increase controller uptime there should be a mechanism
   *       to re-trigger the discovery
   *
   *  2) Alternative to multicast discovery is direct IP Address:Port configuration
   *     - discovery should fall back to this if available
   *     - changes should be possible at runtime
   *
   *  3) KNX Connection lifecycle
   *     - It seems many KNX IP gateways are limited to low number of concurrent connections
   *     - If I understood the spec correctly, the KNX requires a KNX individual address per
   *      open connection
   *     - This means many IP gateways default to one concurrent connection (single address)
   *     - Multiple KNX individual addresses would need to be commissioned via ETS (possible?)
   *     - Unclear if/how many KNX IP gateways support multiple individual addresses for
   *       multiple connections (?)
   *
   *     Therefore:
   *     - Should assume low connection concurrency
   *     - Simplistic mode: ORB creates and maintains a connection through-out its lifetime
   *       taking up one connection slot in the gateway
   *     - Yielding mode: open/close connection only when KNX event is triggered
   *        * This will potentially have a bad performance degradation if high number of events
   *          are triggered -- especially if within a macro
   *     - Managed mode: keep connection open while there's traffic, close after idle timeout
   *        * Less open/close overhead, still yields after a while if no more traffic
   *        * With sufficiently low timeout almost identical to yield mode (but could be high
   *          enough threshold to maintain a connection over most macro executions)
   *
   *  - Will do Simplistic at first to prototype.
   *
   */


  // Constants ------------------------------------------------------------------------------------


  /**
   * A common log category name intended to be used across all classes related to
   * KNX implementation.
   */
  public final static String KNX_LOG_CATEGORY  = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "knx";

  /**
   * String constant for parsing KNX protocol XML entries from controller.xml file.
   *
   * This constant is the expected property name value for KNX group addresses
   * (<code>{@value}</code>):
   *
   * <pre>{@code
   * <command protocol = "knx" >
   *   <property name = "groupAddress" value = "x/x/x"/>
   *   <property name = "command" value = "ON"/>
   * </command>
   * }</pre>
   */
  public final static String KNX_XMLPROPERTY_GROUPADDRESS  = "groupAddress";

  /**
   * String constant for parsing KNX protocol XML entries from controller.xml file.
   *
   * This constant is the expected property name value for KNX commands ({@value}):
   *
   * <pre>{@code
   * <command protocol = "knx" >
   *   <property name = "groupAddress" value = "x/x/x"/>
   *   <property name = "command" value = "ON"/>
   * </command>
   * }</pre>
   */
  public final static String KNX_XMLPROPERTY_COMMAND       = "command";

  /**
   * String constant for parsing KNX protocol XML entries from controller.xml file.
   *
   * This constant is the expected property name value for KNX commands ({@value}):
   *
   * <pre>{@code
   * <command protocol = "knx" >
   *   <property name = "groupAddress" value = "x/x/x"/>
   *   <property name = "command" value = "ON"/>
   * </command>
   * }</pre>
   */
  public final static String KNX_XMLPROPERTY_DPT       = "DPT";


  // Class Members --------------------------------------------------------------------------------

  /**
   * Logging. Use common KNX log category for all KNX related classes.
   */
  private static Logger log = Logger.getLogger(KNX_LOG_CATEGORY);


  // Instance Fields ------------------------------------------------------------------------------


  // TODO : inject service dependency
  private KNXIpConnectionManager connectionManager = null;

  private String knxIpInterfaceHostname;
  private int knxIpInterfacePort;
  private String physicalBusClazz;

  // Constructors ---------------------------------------------------------------------------------

  /**
   * TODO
   */
  public KNXCommandBuilder(String knxIpInterfaceHostname, int knxIpInterfacePort, String physicalBusClazz)
  {
    //Moved initialization of connectionManger into build() method to only start it when needed
    this.knxIpInterfaceHostname = knxIpInterfaceHostname;
    this.knxIpInterfacePort = knxIpInterfacePort;
    this.physicalBusClazz = physicalBusClazz;
  }


  // Implements EventBuilder ----------------------------------------------------------------------


  /**
   * Parses the KNX command XML snippets and builds a corresponding KNX command instance.  <p>
   *
   * The expected XML structure is:
   *
   * <pre>{@code
   * <command protocol = "knx" >
   *   <property name = "groupAddress" value = "x/x/x"/>
   *   <property name = "DPT" value = "n.mmm"/>
   *   <property name = "command" value = "ON"/>
   * </command>
   * }</pre>
   *
   * Additional properties not listed here are ignored.
   *
   * @see GroupValueWrite
   *
   * @throws NoSuchCommandException
   *            if the KNX command instance cannot be constructed from the XML snippet
   *            for any reason
   *
   * @return an immutable KNX command instance with known configured properties set
   */
  public Command build(Element element)
  {
    //Start the KNX connectionManager only if KNX commands are created
    if (this.connectionManager == null) {
      this.connectionManager = new KNXIpConnectionManager();
      this.connectionManager.setKnxIpInterfaceHostname(this.knxIpInterfaceHostname);
      this.connectionManager.setKnxIpInterfacePort(this.knxIpInterfacePort);
      this.connectionManager.setPhysicalBusClazz(physicalBusClazz);
      this.connectionManager.scheduleConnection();         
    }
    /*
     * TODO : ${param} handling (javadoc)
     *
     * TODO : unit test for parameterized commands, DPT property
     * 
     * TODO : could use JAXB instead JDOM since its included in JDK
     *
     * TODO : NoSuchCommandException should be a checked exception
     */

    String groupAddressString = null;
    String commandAsString = null;
    String dptString = null;


    // Get the list of properties from XML...

    List<Element> propertyElements = element.getChildren(XML_ELEMENT_PROPERTY, element.getNamespace());

    for (Element el : propertyElements)
    {
      String knxPropertyName = el.getAttributeValue(XML_ATTRIBUTENAME_NAME);
      String knxPropertyValue = el.getAttributeValue(XML_ATTRIBUTENAME_VALUE);

      if (KNX_XMLPROPERTY_GROUPADDRESS.equalsIgnoreCase(knxPropertyName))
      {
        groupAddressString = knxPropertyValue;
      }

      else if (KNX_XMLPROPERTY_COMMAND.equalsIgnoreCase(knxPropertyName))
      {
        commandAsString = knxPropertyValue;
      }

      else if (KNX_XMLPROPERTY_DPT.equalsIgnoreCase(knxPropertyName))
      {
        dptString = knxPropertyValue;
      }

      else
      {
        log.warn(
            "Unknown KNX property '<" + XML_ELEMENT_PROPERTY + " " +
            XML_ATTRIBUTENAME_NAME + " = \"" + knxPropertyName + "\" " +
            XML_ATTRIBUTENAME_VALUE + " = \"" + knxPropertyValue + "\"/>'."
        );
      }
    }


    // Sanity check on mandatory properties 'command', 'groupAddress' and 'DPT'...

    if (groupAddressString == null || "".equals(groupAddressString))
    {
      throw new NoSuchCommandException(
          "KNX command must have a '" + KNX_XMLPROPERTY_GROUPADDRESS + "' property."
      );
    }

    if (commandAsString == null || "".equals(commandAsString))
    {
      throw new NoSuchCommandException(
          "KNX command must have a '" + KNX_XMLPROPERTY_COMMAND + "' property."
      );
    }

    if (dptString == null || "".equals(dptString))
    {
      throw new NoSuchCommandException(
          "KNX command must have a '" + KNX_XMLPROPERTY_DPT + "' property."
      );
    }


    // Attempt to build GroupAddress instance...

    GroupAddress groupAddress = null;

    try
    {
      groupAddress = new GroupAddress(groupAddressString.trim());
    }
    catch (InvalidGroupAddressException e)
    {
      throw new NoSuchCommandException(e.getMessage(), e);
    }

    // Translate DPT string into a type safe instance...

    DataPointType dpt = DataPointType.lookup(dptString);

    if (dpt == null)
    {
      throw new NoSuchCommandException("Unrecognized KNX datapoint type '" + dptString + "'.");
    }

    // Check for and create a parameterized command if present, and translate the command string
    // to a type safe KNX Command types...

    String paramValue = element.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);

    CommandParameter parameter = null;

    if (paramValue != null && !paramValue.equals(""))
    {
      try
      {
        parameter = new CommandParameter(paramValue);
      }
      catch (ConversionException exception)
      {
        throw new NoSuchCommandException(
            "Cannot convert '" + paramValue + "' to command parameter : " + exception.getMessage(),
            exception
        );
      }
    }
    
    Command cmd = KNXCommand.createCommand(commandAsString, dpt, connectionManager, groupAddress, parameter);

    log.info("Created KNX Command " + cmd + " for group address '" + groupAddress + "'");

    return cmd;

  }


}
