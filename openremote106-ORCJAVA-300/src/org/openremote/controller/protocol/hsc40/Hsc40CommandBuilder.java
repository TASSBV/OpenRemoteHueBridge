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
package org.openremote.controller.protocol.hsc40;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.utils.Logger;

/**
 * 
 * @author Marcus
 */
public class Hsc40CommandBuilder implements CommandBuilder {

   // Constants ------------------------------------------------------------------------------------
   public final static String HSC40_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "hsc40";

   private final static String STR_ATTRIBUTE_NAME_DEVICE_ID = "deviceId";
   private final static String STR_ATTRIBUTE_NAME_PORT_ID = "portId";
   private final static String STR_ATTRIBUTE_NAME_COMMAND = "command";
   private final static String STR_ATTRIBUTE_NAME_DEVICE_TYPE = "deviceType";

   // Class Members --------------------------------------------------------------------------------

   private final static Logger logger = Logger.getLogger(HSC40_PROTOCOL_LOG_CATEGORY);

   // Instance Fields ------------------------------------------------------------------------------
   
   private Hsc40IpClient ipClient;
   private String hsc40Ip;
   private int hsc40Port;
   
   
   // Constructors ---------------------------------------------------------------------------------

   public Hsc40CommandBuilder(String hsc40Ip, int hsc40Port) {
      this.hsc40Ip = hsc40Ip;
      this.hsc40Port = hsc40Port;
   }

   // Implements CommandBuilder --------------------------------------------------------------------

   @SuppressWarnings("unchecked")
   @Override
   public Command build(Element element) {
      if (ipClient == null) {
         try {
            ipClient = new Hsc40IpClient(hsc40Ip, hsc40Port);
         } catch (Exception e) {
            throw new NoSuchCommandException("Could not start HSC-40 ip connection", e);
         }
      }
      logger.info("Building HSC-40 command");
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());

      String deviceId = null;
      String deviceType = null;
      String portId = null;
      String command = null;

      // read values from config xml

      for (Element ele : propertyEles) {
         String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
         String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

         if (STR_ATTRIBUTE_NAME_COMMAND.equals(elementName)) {
            command = elementValue;
            logger.debug("HSC-40 command: command = " + command);
         }
         else if (STR_ATTRIBUTE_NAME_DEVICE_ID.equals(elementName)) {
            deviceId = elementValue;
            logger.debug("HSC-40 command: deviceId = " + deviceId);
         }
         else if (STR_ATTRIBUTE_NAME_PORT_ID.equals(elementName)) {
            portId = elementValue;
            logger.debug("HSC-40 command: portId = " + portId);
         }
         else if (STR_ATTRIBUTE_NAME_DEVICE_TYPE.equals(elementName)) {
            deviceType = elementValue;
            logger.debug("HSC-40 command: deviceType = " + deviceType);
         }
      }

      if (null == command || null == deviceId || null == portId || null == deviceType) {
         throw new NoSuchCommandException("Unable to create HSC-40 command, missing configuration parameter(s)");
      }

      String paramValue = element.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);
      
      logger.debug("HSC-40 command created successfully");
      return new Hsc40Command(command, paramValue, deviceId, deviceType, portId, ipClient);
   }

}
