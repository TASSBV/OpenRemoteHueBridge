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
package org.openremote.controller.protocol.rrdgraphurl;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.utils.Logger;

public class RrdGraphUrlCommandBuilder implements CommandBuilder {

   // Constants ------------------------------------------------------------------------------------

   /**
    * A common log category name intended to be used across all classes related to
    * OpenRemote rrdGraphUrl protocol implementation.
    */
   public final static String LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "rrdGraphUrl";

   /**
    * String constant for parsing virtual protocol XML entries from controller.xml file.
    *
    * This constant is the expected property name value for rrdGraphUrl protocol graphname
    * (<code>{@value}</code>):
    *
    * <pre>{@code
    * <command protocol = "rrdGraphUrl" >
    *   <property name = "graphname" value = "graph1"/>
    *   <property name = "width" value = "800"/>
    *   <property name = "height" value = "600"/>
    *   <property name = "start" value = "20120210-10-00"/>
    *   <property name = "end" value = "20120211-10-00"/>
    *   <property name = "command" value = "getUrl"/>
    *   <property name = "ip" value = "localhost"/>
    *   <property name = "port" value = "8080"/>
    * </command>
    * }</pre>
    * 
    * It is also possible to say "-10m" for start and "+10m" for end. 
    * This is the offset in minutes (h=hour is also possible) relative to the time when the graph is generated.<br>
    * Once this is changed with the modifications commands like "startPlus1Hour" the new calculated times will be used
    * and not the relative offset anymore.
    *  
    */
   public final static String XML_GRAPHNAME = "graphname";
   public final static String XML_WIDTH = "width";
   public final static String XML_HEIGHT = "height";
   public final static String XML_COMMAND = "command";
   public final static String XML_START = "start";
   public final static String XML_END = "end";
   public final static String XML_IP = "ip";
   public final static String XML_PORT = "port";


   // Class Members --------------------------------------------------------------------------------

   /**
    * Logging. Use common log category for all related classes.
    */
   private static Logger log = Logger.getLogger(LOG_CATEGORY);



   // Implements CommandBuilder --------------------------------------------------------------------

   /**
    * Parses the OpenRemote rrdGraphUrl command XML snippets and builds a
    * corresponding command instance.  <p>
    *
    * @throws org.openremote.controller.exception.NoSuchCommandException
    *            if the rrdGraphUrl command instance cannot be constructed from the XML snippet
    *            for any reason
    *
    * @return a rrdGraphUrl command instance with known configured properties set
    */
   public Command build(Element element)
   {
     String graphName = null;
     String width = null;
     String height = null;
     String command = null;
     String start = null;
     String end = null;
     String ip = null;
     String port = null;

     // Properties come in as child elements...

     List<Element> propertyElements = getCommandProperties(element);


     // Parse 'address' and 'command' properties...

     for (Element el : propertyElements)
     {
       String propertyName = el.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
       String propertyValue = el.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

       if (XML_GRAPHNAME.equalsIgnoreCase(propertyName))
       {
         graphName = propertyValue;
       }
       else if (XML_WIDTH.equalsIgnoreCase(propertyName))
       {
         width = propertyValue;
       }
       else if (XML_HEIGHT.equalsIgnoreCase(propertyName))
       {
         height = propertyValue;
       }
       else if (XML_COMMAND.equalsIgnoreCase(propertyName))
       {
         command = propertyValue;
       }
       else if (XML_START.equalsIgnoreCase(propertyName))
       {
         start = propertyValue;
       }
       else if (XML_END.equalsIgnoreCase(propertyName))
       {
         end = propertyValue;
       }
       else if (XML_IP.equalsIgnoreCase(propertyName))
       {
         ip = propertyValue;
       }
       else if (XML_PORT.equalsIgnoreCase(propertyName))
       {
         port = propertyValue;
       }
       else
       {
         log.warn(
             "Unknown rrdGraphUrl protocol property '<" + XML_ELEMENT_PROPERTY + " " +
             XML_ATTRIBUTENAME_NAME + " = \"" + propertyName + "\" " +
             XML_ATTRIBUTENAME_VALUE + " = \"" + propertyValue + "\"/>'."
         );
       }
     }

     // sanity checks...

     if (command == null || ("").equals(command))
     {
       throw new NoSuchCommandException(
          "OpenRemote rrdGraphUrl protocol command is missing '" + XML_COMMAND + "' property"
       );
     }

     if (graphName == null || ("").equals(graphName))
     {
       throw new NoSuchCommandException(
          "OpenRemote rrdGraphUrl protocol command is missing '" + XML_GRAPHNAME + "' property"
       );
     }


     Command cmd = new RrdGraphUrlCommand(graphName, command, width, height, start, end, ip, port);
     return cmd;
   }


   // Private Instance Methods ---------------------------------------------------------------------

   /**
    * Isolated here to limit the scope of the unchecked warnings to this one method (JDOM does not
    * use Java generics).
    *
    * @param   element   the element corresponding to {@code <command} in controller.xml
    *
    * @return    child elements of {@code <command} i.e. a list of {@code <property}) elements
    */
   @SuppressWarnings("unchecked")
   private List<Element> getCommandProperties(Element element)
   {
     return element.getChildren(
         CommandBuilder.XML_ELEMENT_PROPERTY,
         element.getNamespace()
     );
   }

}
