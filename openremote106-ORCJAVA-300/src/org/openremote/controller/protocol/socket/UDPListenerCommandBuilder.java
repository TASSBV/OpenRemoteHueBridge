/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2012, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.socket;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.utils.Logger;

/**
 * Builds a UDPListenerCommand which can be used to react on incoming UDP packets
 * 
 * @author Marcus Redeker
 */
public class UDPListenerCommandBuilder implements CommandBuilder {

   // Constants ------------------------------------------------------------------------------------

   public final static String UDPLISTENER_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "UDP_LISTENER";

   private final static String STR_ATTRIBUTE_NAME_PORT = "port";
   private final static String STR_ATTRIBUTE_NAME_REGEX = "regex";

   // Class Members --------------------------------------------------------------------------------

   private final static Logger logger = Logger.getLogger(UDPLISTENER_PROTOCOL_LOG_CATEGORY);


   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public Command build(Element element) {
      logger.debug("Building UDPListener command");
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());
      String port = null;
      String regex = null;

      // read values from config xml
      for (Element ele : propertyEles) {
         String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
         String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);
         if (STR_ATTRIBUTE_NAME_PORT.equals(elementName)) {
            port = elementValue;
            logger.debug("UDPListener Command: port = " + port);
         } else if (STR_ATTRIBUTE_NAME_REGEX.equals(elementName)) {
            regex = elementValue;
            logger.debug("UDPListener Command: regex = " + regex);
         }
      }
      
      try
      {
        if (null != port) {
          Integer.valueOf(port);
          logger.debug("UDPListener Command: port is no valid port number");
        }
      } catch (Exception e1)
      {
        throw new NoSuchCommandException("Unable to create UDPListener command: port is no valid port number");
      }

      logger.debug("UDPListenet command created successfully");
      return new UDPListenerCommand(port, regex);
   }

}
