/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.protocol.samsungtv;

import java.util.Hashtable;
import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.utils.Logger;

/**
 * Builds a SamsungTVRemoteCommand which can be used to control Samsung TV's through LAN
 * 
 * @author Marcus Redeker
 * @author Ivan Martinez
 */
public class SamsungTVRemoteCommandBuilder implements CommandBuilder {

   // Constants ------------------------------------------------------------------------------------

   public final static String SAMSUNG_TV_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "SamsungTV";

   private final static String STR_ATTRIBUTE_NAME_KEY_CODE = "keyCode";
   private final static String STR_ATTRIBUTE_NAME_IP_ADDRESS = "ipAddress";
   
   private final static int SAMSUNG_TV_REMOTE_CONTROL_PORT = 55000;
   private final static String SAMSUNG_TV_REMOTE_APPLICATION_NAME = "OpenRemote";

   // Class Members --------------------------------------------------------------------------------

   private final static Logger logger = Logger.getLogger(SAMSUNG_TV_PROTOCOL_LOG_CATEGORY);

   // Instance Fields ------------------------------------------------------------------------------
   
   private String samsungTVIp;
   // cached sessions for reusing session objects for different commands...
   private Hashtable<String, SamsungTVSession> sessions = new Hashtable<String, SamsungTVSession>();
   
   // Constructors ---------------------------------------------------------------------------------
   

   public SamsungTVRemoteCommandBuilder(String samsungTVIp) {
      this.samsungTVIp = samsungTVIp;
   }
   
   
   // Implements CommandBuilder --------------------------------------------------------------------
   
   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public Command build(Element element) {
      logger.debug("Building Samsung TV command");
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());
      String keyCode = null;
      String ip = samsungTVIp;

      // read values from config xml

      for (Element ele : propertyEles) {
         String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
         String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

         if (STR_ATTRIBUTE_NAME_KEY_CODE.equals(elementName)) {
            keyCode = elementValue;
            logger.debug("Samsung TV Command: keyCode = " + keyCode);
         } else if (STR_ATTRIBUTE_NAME_IP_ADDRESS.equals(elementName)) {
            ip = elementValue;
            logger.debug("Samsung TV Command: ipAddress = " + ip);
         }
      }

      if (null == keyCode) {
         throw new NoSuchCommandException("Samsung TV command must have a 'keyCode' property.");
      }

      try {
         Key key = Key.valueOf(keyCode);
         logger.debug("Samsung TV Command created successfully");
         return new SamsungTVCommand(getSession(ip), key);

      } catch (Exception e) {
         throw new NoSuchCommandException("Unable to create SamsungTVCommand keyCode: " + keyCode, e);
      }
      
   }
   
   private SamsungTVSession getSession(String ip) {
      if (!sessions.contains(ip)) {
         sessions.put(ip, new SamsungTVSession(ip, SAMSUNG_TV_REMOTE_CONTROL_PORT, SAMSUNG_TV_REMOTE_APPLICATION_NAME));
      }
      return sessions.get(ip);
   }
   
}
