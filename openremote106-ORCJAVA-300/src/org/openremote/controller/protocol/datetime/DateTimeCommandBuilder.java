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
package org.openremote.controller.protocol.datetime;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.utils.Logger;

/**
 * CommandBuilder implementation to create DateTime commands. DateTime commands can be used to display date or time on
 * the console or use certain DateTime events to trigger rules. To calculate sunrise and sunset the location has to be
 * given as longitude, latidude and timezone. Timezone is the string identifier which is available in Java eg.
 * Europe/Berlin
 * 
 * The following commands are available in the moment: - date (returns a date/time as string depending on the given
 * formatter) - sunrise (returns the sunrise time as string depending on the given formatter) - sunset (returns the
 * sunset time as string depending on the given formatter) - minutesUntilSunrise (returns an integer with the minutes
 * until sunrise) - minutesUntilSunset (returns an integer with the minutes until sunset) - isDay (returns a boolean
 * string) - isNight (returns a boolean string)
 * 
 * @author Marcus
 */
public class DateTimeCommandBuilder implements CommandBuilder {

   // Constants ------------------------------------------------------------------------------------
   public final static String DATETIME_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "datetime";

   private final static String STR_ATTRIBUTE_NAME_LATITUDE = "latitude";
   private final static String STR_ATTRIBUTE_NAME_LONGITUDE = "longitude";
   private final static String STR_ATTRIBUTE_NAME_TIMEZONE = "timezone";
   private final static String STR_ATTRIBUTE_NAME_COMMAND = "command";
   private final static String STR_ATTRIBUTE_NAME_FORMAT = "format";

   // Class Members --------------------------------------------------------------------------------

   private final static Logger logger = Logger.getLogger(DATETIME_PROTOCOL_LOG_CATEGORY);

   // Implements CommandBuilder --------------------------------------------------------------------

   @SuppressWarnings("unchecked")
   @Override
   public Command build(Element element) {
      logger.debug("Building DateTime command");
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());

      String latitude = null;
      String longitude = null;
      String timezone = null;
      String command = null;
      String format = null;

      // read values from config xml

      for (Element ele : propertyEles) {
         String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
         String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

         if (STR_ATTRIBUTE_NAME_LATITUDE.equals(elementName)) {
            latitude = elementValue;
            logger.debug("DateTime command: latitude = " + latitude);
         }

         else if (STR_ATTRIBUTE_NAME_LONGITUDE.equals(elementName)) {
            longitude = elementValue;
            logger.debug("DateTime command: longitude = " + longitude);
         }

         else if (STR_ATTRIBUTE_NAME_TIMEZONE.equals(elementName)) {
            timezone = elementValue;
            logger.debug("DateTime command: timezone = " + timezone);
         }

         else if (STR_ATTRIBUTE_NAME_COMMAND.equals(elementName)) {
            command = elementValue;
            logger.debug("DateTime command: command = " + command);
         }

         else if (STR_ATTRIBUTE_NAME_FORMAT.equals(elementName)) {
            format = elementValue;
            logger.debug("DateTime command: format = " + format);
         }
      }

      if (command.equals("sunrise") || command.equals("sunset") || command.equals("minutesUntilSunrise")
            || command.equals("minutesUntilSunset") || command.equals("isDay") || command.equals("isNight")) {
         if (null == longitude || null == latitude) {
            throw new NoSuchCommandException("Unable to create DateTime command for sunrise/sunset related command, " +
                  "missing configuration parameter(s)");
         }
      }

      logger.debug("DateTime command created successfully");
      return new DateTimeCommand(latitude, longitude, timezone, command, format);
   }

}
