/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.protocol.amx_ni;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.utils.Logger;

/**
 * Builder for AMXNICommand, based on XML snippet.
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class AMXNICommandBuilder implements CommandBuilder {

   // Constants ------------------------------------------------------------------------------------

   /**
    * A common log category name intended to be used across all classes related to AMX NI implementation.
    */
   public final static String AMX_NI_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "AMX_NI";

   /**
    * String constant for parsing AMX NI protocol XML entries from controller.xml file.
    */
   public final static String AMX_NI_XMLPROPERTY_NAME = "name";

   public final static String AMX_NI_XMLPROPERTY_COMMAND = "command";

   public final static String AMX_NI_XMLPROPERTY_DEVICE_INDEX = "deviceIndex";

   public final static String AMX_NI_XMLPROPERTY_CHANNEL = "channel";

   public final static String AMX_NI_XMLPROPERTY_LEVEL = "level";

   public final static String AMX_NI_XMLPROPERTY_VALUE = "value";
   
   public final static String AMX_NI_XMLPROPERTY_PULSE_TIME = "pulseTime";
   
   public final static String AMX_NI_XMLPROPERTY_STATUS_FILTER = "statusFilter";

   public final static String AMX_NI_XMLPROPERTY_STATUS_FILTER_GROUP = "statusFilterGroup";

   // Class Members --------------------------------------------------------------------------------

   /**
    * AMX NI logger. Uses a common category for all AMX NI related logging.
    */
   private final static Logger log = Logger.getLogger(AMXNICommandBuilder.AMX_NI_LOG_CATEGORY);

   // Instance Fields ------------------------------------------------------------------------------

   private AMXNIGateway gateway;

   // Constructors ---------------------------------------------------------------------------------

   public AMXNICommandBuilder() {

   }

   /**
    * Parses the AMX NI command XML snippets and builds a corresponding AMX NI command instance.
    * <p>
    * 
    * The expected XML structure is:
    * 
    * <pre>
    * @code
    * <command protocol = "amx_ni" >
    *   <property name = "command" value = ""/>
    *   <property name = "deviceIndex" value = ""/>
    *   <property name = "channel" value = ""/>
    *   <property name = "level" value = ""/>
    *   <property name = "value" value = ""/>
    *   <property name = "statusFilter" value = ""/>
    *   <property name = "statusFilterGroup" value = ""/>
    * </command>
    * }
    * </pre>
    * 
    * Additional properties not listed here are ignored.
    * 
    * @throws NoSuchCommandException
    *            if the AMX NI command instance cannot be constructed from the XML snippet for any reason
    * 
    * @return an immutable AMX NI command instance with known configured properties set
    */
   @Override
   public Command build(Element element) {
      
      String commandAsString = null;
      String deviceIndexAsString = null;
      String channelAsString = null;
      String levelAsString = null;
      String valueAsString = null;
      String pulseTimeAsString = null;
      String statusFilterAsString = null;
      String statusFilterGroupAsString = null;

      Integer deviceIndex = null;
      Integer channel = null;
      Integer level = null;
      Integer pulseTime = null;
      Integer statusFilterGroup = null;

      // Get the list of properties from XML...

      @SuppressWarnings("unchecked")
      List<Element> propertyElements = element.getChildren(XML_ELEMENT_PROPERTY, element.getNamespace());

      for (Element el : propertyElements) {
         String propertyName = el.getAttributeValue(XML_ATTRIBUTENAME_NAME);
         String propertyValue = el.getAttributeValue(XML_ATTRIBUTENAME_VALUE);

         if (AMX_NI_XMLPROPERTY_COMMAND.equalsIgnoreCase(propertyName)) {
            commandAsString = propertyValue;
         }

         else if (AMX_NI_XMLPROPERTY_DEVICE_INDEX.equalsIgnoreCase(propertyName)) {
            deviceIndexAsString = propertyValue;
         }

         else if (AMX_NI_XMLPROPERTY_CHANNEL.equalsIgnoreCase(propertyName)) {
            channelAsString = propertyValue;
         }

         else if (AMX_NI_XMLPROPERTY_LEVEL.equalsIgnoreCase(propertyName)) {
            levelAsString = propertyValue;
         }
         
         else if (AMX_NI_XMLPROPERTY_VALUE.equalsIgnoreCase(propertyName)) {
            valueAsString = propertyValue;
         }

         else if (AMX_NI_XMLPROPERTY_PULSE_TIME.equalsIgnoreCase(propertyName)) {
            pulseTimeAsString = propertyValue;
         }
         
         else if (AMX_NI_XMLPROPERTY_STATUS_FILTER.equalsIgnoreCase(propertyName)) {
            statusFilterAsString = propertyValue;
         }

         else if (AMX_NI_XMLPROPERTY_STATUS_FILTER_GROUP.equalsIgnoreCase(propertyName)) {
            statusFilterGroupAsString = propertyValue;
         }

         else if (!AMX_NI_XMLPROPERTY_NAME.equalsIgnoreCase(propertyName)) { // name property is allowed but we're not interested in its value
            log.warn("Unknown AMX NI property '<" + XML_ELEMENT_PROPERTY + " " + XML_ATTRIBUTENAME_NAME + " = \"" + propertyName + "\" " + XML_ATTRIBUTENAME_VALUE + " = \"" + propertyValue + "\"/>'.");
         }
      }
      
      // Sanity check on mandatory property 'command'

      if (commandAsString == null || "".equals(commandAsString)) {
         throw new NoSuchCommandException("AMX NI command must have a '" + AMX_NI_XMLPROPERTY_COMMAND + "' property.");
      }
      
      // Sanity check on mandatory property 'deviceIndex'

      if (deviceIndexAsString == null || "".equals(deviceIndexAsString)) {
         throw new NoSuchCommandException("AMX NI command must have a '" + AMX_NI_XMLPROPERTY_DEVICE_INDEX + "' property.");
      }

      // Convert deviceIndex to integer
      try {
         deviceIndex = Integer.parseInt(deviceIndexAsString);
      } catch (NumberFormatException e) {
         log.error("Invalid deviceIndex", e);
         throw new NoSuchCommandException(e.getMessage(), e);
      }
      
      // If a channel was provided, attempt to convert to integer
      if (channelAsString != null && !"".equals(channelAsString)) {
         try {
            channel = Integer.parseInt(channelAsString);
         } catch (NumberFormatException e) {
           log.error("Invalid channel number", e);
            throw new NoSuchCommandException(e.getMessage(), e);
         }
      }

      // If a level was provided, attempt to convert to integer
      if (levelAsString != null && !"".equals(levelAsString)) {
         try {
            level = Integer.parseInt(levelAsString);
         } catch (NumberFormatException e) {
           log.error("Invalid level number", e);
            throw new NoSuchCommandException(e.getMessage(), e);
         }
      }

      // If a pulseTime was provided, attempt to convert to integer
      if (pulseTimeAsString != null && !"".equals(pulseTimeAsString)) {
         try {
            pulseTime = Integer.parseInt(pulseTimeAsString);
         } catch (NumberFormatException e) {
           log.error("Invalid pulse time", e);
            throw new NoSuchCommandException(e.getMessage(), e);
         }
      }

      // If a statusFilterGroup was provided, attempt to convert to integer
      if (statusFilterGroupAsString != null && !"".equals(statusFilterGroupAsString)) {
         try {
            statusFilterGroup = Integer.parseInt(statusFilterGroupAsString);
         } catch (NumberFormatException e) {
           log.error("Invalid status filter group", e);
            throw new NoSuchCommandException(e.getMessage(), e);
         }
      }

      if (valueAsString == null) {
         // No specific level provided, use parameter (passed in from Slider)
         // We don't do any type checking here, it'll be handled by the command class
         valueAsString = element.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);
      }
      
      // Translate the command string to a type safe AMX NI Command types...
      Command cmd = AMXNICommand.createCommand(commandAsString, gateway, deviceIndex, channel, level, valueAsString, pulseTime, statusFilterAsString, statusFilterGroup);

      log.info("Created AMX NI Command " + cmd);

      return cmd;
   }

   // Getters / Setters ----------------------------------------------------------------------------

   public AMXNIGateway getGateway() {
      return gateway;
   }

   public void setGateway(AMXNIGateway gateway) {
      this.gateway = gateway;
   }

}