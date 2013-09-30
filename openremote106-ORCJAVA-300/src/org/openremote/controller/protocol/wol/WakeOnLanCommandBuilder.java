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
package org.openremote.controller.protocol.wol;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.utils.Logger;

/**
 * Builds a WakeOnLandCommand which can be used to send WOL magic packets onto the LAN to wakeup a pc.
 * 
 * @author Marcus Redeker
 */
public class WakeOnLanCommandBuilder implements CommandBuilder {

   // Constants ------------------------------------------------------------------------------------

   public final static String WOL_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "WOL";

   private final static String STR_ATTRIBUTE_NAME_MAC_ADDRESS = "macAddress";
   private final static String STR_ATTRIBUTE_NAME_BROADCAST_IP = "broadcastIp";

   // Class Members --------------------------------------------------------------------------------

   private final static Logger logger = Logger.getLogger(WOL_PROTOCOL_LOG_CATEGORY);

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public Command build(Element element) {
      logger.debug("Building WOL command");
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());

      String macAddress = null;
      String broadcastIp = null;

      // read values from config xml

      for (Element ele : propertyEles) {
         String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
         String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

         if (STR_ATTRIBUTE_NAME_MAC_ADDRESS.equals(elementName)) {
            macAddress = elementValue;
            logger.debug("WOL Command: macAddress = " + macAddress);
         } else if (STR_ATTRIBUTE_NAME_BROADCAST_IP.equals(elementName)) {
            broadcastIp = elementValue;
            logger.debug("WOL Command: broadcastIp = " + broadcastIp);
         }
      }

      if (null == macAddress || null == broadcastIp || macAddress.trim().length() == 0 || broadcastIp.trim().length() == 0) {
         throw new NoSuchCommandException("WOL command must have a both properties 'macAddress' and 'broadcastIp'.");
      }

      logger.debug("WOL Command created successfully");

      return new WakeOnLanCommand(macAddress, broadcastIp);
   }

}
