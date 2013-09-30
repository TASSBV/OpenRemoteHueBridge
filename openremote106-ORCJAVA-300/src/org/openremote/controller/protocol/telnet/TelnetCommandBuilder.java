/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.protocol.telnet;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.utils.CommandUtil;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.utils.Strings;


/**
 * The Class TelnetCommandBuilder.
 *
 * @author Marcus 2009-4-26
 */
public class TelnetCommandBuilder implements CommandBuilder {

   // Constants ------------------------------------------------------------------------------------

   public final static String TELNET_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "TELNET";

   private final static String STR_ATTRIBUTE_NAME_PORT = "port";
   private final static String STR_ATTRIBUTE_NAME_IP_ADDRESS = "ipAddress";
   private final static String STR_ATTRIBUTE_NAME_COMMAND = "command";
   private final static String STR_ATTRIBUTE_NAME_TIMEOUT = "timeout";
   private final static String STR_ATTRIBUTE_NAME_STATUS_FILTER = "statusFilter";
   private final static String STR_ATTRIBUTE_NAME_STATUS_FILTER_GROUP = "statusFilterGroup";
   private final static String STR_ATTRIBUTE_NAME_STATUS_DEFAULT= "statusDefault";
   private final static String STR_ATTRIBUTE_NAME_POLLINGINTERVAL = "pollingInterval";

   
   // Class Members --------------------------------------------------------------------------------

   private final static Logger logger = Logger.getLogger(TELNET_PROTOCOL_LOG_CATEGORY);

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public Command build(Element element) {
      logger.debug("Building Telnet command");
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());

      String portStr = null;
      Integer port = null;
      String ipAddress = null;
      String command = null;
      String timeoutStr = null;
      Integer timeout = null;
      String statusFilter = null;
      String statusFilterGroupStr = null;
      Integer statusFilterGroup = null;
      String statusDefault = null;
      String interval = null;
      Integer intervalInMillis = null;
      
      // read values from config xml

      for (Element ele : propertyEles) {
         String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
         String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

         if (STR_ATTRIBUTE_NAME_PORT.equals(elementName)) {
            portStr = elementValue;
            logger.debug("Telnet Command: port= " + portStr);
         } else if (STR_ATTRIBUTE_NAME_IP_ADDRESS.equals(elementName)) {
            ipAddress = elementValue;
            logger.debug("Telnet Command: ipAddress = " + ipAddress);
         } else if (STR_ATTRIBUTE_NAME_POLLINGINTERVAL.equals(elementName))
         {
           interval = elementValue;
           logger.debug("Telnet Command: pollingInterval = " + interval);
         } else if (STR_ATTRIBUTE_NAME_COMMAND.equals(elementName))
         {
           command = CommandUtil.parseStringWithParam(element, elementValue);
           logger.debug("Telnet Command: command = " + command);
         } else if (STR_ATTRIBUTE_NAME_TIMEOUT.equals(elementName))
         {
           timeoutStr = elementValue;
           logger.debug("Telnet Command: timeout = " + timeoutStr);
         } else if (STR_ATTRIBUTE_NAME_STATUS_DEFAULT.equals(elementName))
         {
           statusDefault = elementValue;
           logger.debug("Telnet Command: statusDefault = " + statusDefault);
         } else if (STR_ATTRIBUTE_NAME_STATUS_FILTER.equals(elementName))
         {
           statusFilter = elementValue;
           logger.debug("Telnet Command: statusFilter = " + statusFilter);
         } else if (STR_ATTRIBUTE_NAME_STATUS_FILTER_GROUP.equals(elementName))
         {
           statusFilterGroupStr = elementValue;
           logger.debug("Telnet Command: statusFilterGroup = " + statusFilterGroupStr);
         } 
      }

      try
      {
        if (null != interval) {
          intervalInMillis = Integer.valueOf(Strings.convertPollingIntervalString(interval));
          logger.debug("Telnet Command: pollingIntervalInMillis = " + intervalInMillis);
        }
      } catch (Exception e1)
      {
        throw new NoSuchCommandException("Unable to create Telnet command, pollingInterval could not be converted into milliseconds");
      }
      
      try
      {
        if (null != timeoutStr) {
          timeout = Integer.valueOf(timeoutStr.trim());
        }
      } catch (Exception e1)
      {
        throw new NoSuchCommandException("Unable to create Telnet command, timeout could not be converted into integer");
      }
      
      try
      {
        if (null != portStr) {
          port = Integer.valueOf(portStr.trim());
        }
      } catch (Exception e1)
      {
        throw new NoSuchCommandException("Unable to create Telnet command, port could not be converted into integer");
      }

      try
      {
        if (null != statusFilterGroupStr) {
          statusFilterGroup = Integer.valueOf(statusFilterGroupStr.trim());
        }
      } catch (Exception e1)
      {
        throw new NoSuchCommandException("Unable to create Telnet command, statusFilterGroup could not be converted into integer");
      }
      
      logger.debug("Telnet Command created successfully");

      return new TelnetCommand(command, ipAddress, intervalInMillis, port, statusFilter, statusFilterGroup, statusDefault, timeout);
   }
   

}
