/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.protocol.russound;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.utils.Strings;

/**
 *
 * @author Marcus
 */
public class RussoundCommandBuilder implements CommandBuilder {

   // Constants ------------------------------------------------------------------------------------
   public final static String RUSSOUND_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "russound";

   private final static String STR_ATTRIBUTE_NAME_CONTROLLER = "controller";
   private final static String STR_ATTRIBUTE_NAME_ZONE = "zone";
   private final static String STR_ATTRIBUTE_NAME_COMMAND = "command";

   // Class Members --------------------------------------------------------------------------------
   private final static Logger logger = Logger.getLogger(RUSSOUND_PROTOCOL_LOG_CATEGORY);

   // Instance Fields ------------------------------------------------------------------------------
   private RussoundClient commClient;
   private String russoundIp;
   private int russoundPort;
   private int pollingInterval = -1;
   private String keypadId = "70";
   private String serialDevice = null;
   
   
   // Constructors ---------------------------------------------------------------------------------

   public RussoundCommandBuilder(String russoundIp, int russoundPort, String keypadId, String serialDevice, String pollingInterval) {
      this.russoundIp = russoundIp;
      this.russoundPort = russoundPort;
      
      if ((keypadId != null) && (keypadId.trim().length() != 0)) {
         this.keypadId = keypadId;
      }
      if ((serialDevice != null) && (serialDevice.trim().length() != 0)) {
         this.serialDevice = serialDevice;
      }
      this.pollingInterval = Strings.convertPollingIntervalString(pollingInterval);
    }

   // Implements CommandBuilder --------------------------------------------------------------------

   @SuppressWarnings("unchecked")
   @Override
   public Command build(Element element) {
      if (commClient == null) {
         try {
            commClient = new RussoundClient(russoundIp, russoundPort, keypadId, serialDevice, pollingInterval);
         } catch (Exception e) {
            throw new NoSuchCommandException("Could not start Russound ip connection", e);
         }
      }
      logger.debug("Building Russound command");
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());

      String controller = null;
      String zone = null;
      String commandValue = null;

      // read values from config xml

      for (Element ele : propertyEles) {
         String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
         String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

         if (STR_ATTRIBUTE_NAME_COMMAND.equals(elementName)) {
            commandValue = elementValue;
            logger.debug("Russound command: command = " + commandValue);
         }
         else if (STR_ATTRIBUTE_NAME_CONTROLLER.equals(elementName)) {
            controller = elementValue;
            logger.debug("Russound command: controller = " + controller);
         }
         else if (STR_ATTRIBUTE_NAME_ZONE.equals(elementName)) {
            zone = elementValue;
            logger.debug("Russound command: zone = " + zone);
         }
      }

      if (null == commandValue || null == controller || null == zone ) {
         throw new NoSuchCommandException("Unable to create Russound command, missing configuration parameter(s)");
      }

      String paramValue = element.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);
      try {
         RussCmdEnum command = RussCmdEnum.valueOf(commandValue.trim().toUpperCase());
         logger.debug("Russound command created successfully");
         return new RussoundCommand(controller, zone, command, paramValue, commClient);
      } catch (Exception e) {
         throw new NoSuchCommandException("Invlid commad: " + commandValue);
      }
   }

}
