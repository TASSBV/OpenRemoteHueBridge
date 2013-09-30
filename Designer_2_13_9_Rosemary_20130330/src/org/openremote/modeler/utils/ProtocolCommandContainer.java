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
package org.openremote.modeler.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openremote.modeler.client.model.Command;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.protocol.ProtocolContainer;
import org.openremote.modeler.service.ProtocolParser;

/**
 * This class is used to contain <b>UIButtonEvent</b> of  different kinds of protocol types'.<br />
 * The <b>UIButtonEvent</b> don't mean the event of GWT or some UIEvent,<br />  
 * it's just a <b>POJO</b> class that <b>a java class of xml element(eg:&lt;irEvent name="" command="" /&gt;)</b> of <b>'controller.xml'</b> file.<br />
 * So, iterating the property <b>protocolEvents</b> can generate the all avaliable events xml segment of different kinds of protocol types'.<br /><br />
 * The generated xml segments' structure of events is similar following:<br />
 * &lt;events&gt;<br />
    &lt;x10Events&gt;<br />
      &lt;x10Event id="" address="" command="" /&gt;<br />
    &lt;/x10Events&gt;<br />
    &lt;knxEvents&gt;<br />
      &lt;knxEvent id="" command="" groupAddress="" /&gt;<br />
    &lt;/knxEvents&gt;<br />
    &lt;httpEvents&gt;<br />
      &lt;httpEvent id="" url="" /&gt;<br />
    &lt;/httpEvents&gt;<br />
    &lt;irEvents&gt;<br />
    &lt;/irEvents&gt;<br />
    &lt;tcpipEvents&gt;<br />
      &lt;tcpipEvent id="" port="" command="" ipAddress="" /&gt;<br />
    &lt;/tcpipEvents&gt;<br />
    &lt;telnetEvents&gt;<br />
      &lt;telnetEvent id="" port="" command="" ipAddress="" /&gt;<br />
    &lt;/telnetEvents&gt;<br />
    &lt;/events&gt;<br />
    
 * @author handy.wang
 */
public class ProtocolCommandContainer {
   
   /** 
    * This Map instance is used to contain UIButtonEvents of  different kinds of protocol types.<br />
    * The <b>String type parameter</b> means protocol type.<br />
    * The <b>List<UIButtonEvent></b> means all UIButtonEvents
    */
   private Map<String, List<Command>> protocolEvents = new HashMap<String, List<Command>>();
   
   /** 
    * Store all deviceCommands from database.
    */
   private List<DeviceCommand> allDBDeviceCommands = new ArrayList<DeviceCommand>();
      
   
   /**
    * Constructor<br />
    * Initialize the property <b>protocolEvents</b> Using the <b>ProtocolContainer</b> instance,<br /> 
    * so generating the events xml segment is protocol independence,<br />
    * that's means when extending a new protocol, the <b>protocolContainer</b> will find the new protocol,<br />
    * and then iterating the property <b>protocolEvents</b> will also generate the event xml segment for the new protocol.
    */
   public ProtocolCommandContainer() {
      if (ProtocolContainer.getInstance().getProtocols().size() == 0) {
         ProtocolParser parser = new ProtocolParser();
         ProtocolContainer.getInstance().setProtocols(parser.parseXmls());
      }      
      Set<String> protocolDisplayNames = ProtocolContainer.getInstance().getProtocols().keySet();
      for (String protocolDisplayName : protocolDisplayNames) {
         protocolEvents.put(protocolDisplayName, new ArrayList<Command>());
      }
   }
   
   /**
    * Adds a UIButtonEvent to property <b>protocolEvents</b> depend on the uiButtonEvent protocolDisplayName.
    * 
    * @param uiButtonEvent uiButtonEvent
    */
   public void addUIButtonEvent(Command uiButtonEvent) {
      Set<String> protocolDisplayNames = protocolEvents.keySet();
      for (String protocolDisplayName : protocolDisplayNames) {
         if (protocolDisplayName.equals(uiButtonEvent.getProtocolDisplayName())) {
            List<Command> uiButtonEvents = protocolEvents.get(protocolDisplayName);
            for (Command uiBtnEvent : uiButtonEvents) {
              // MODELER-327: The way comparison was done was not working + should take label into account also
               if (uiBtnEvent.getProtocolAttrs().equals(uiButtonEvent.getProtocolAttrs()) && uiBtnEvent.getLabel().equals(uiButtonEvent.getLabel())) {                
                  uiButtonEvent.setId(uiBtnEvent.getId());
                  return;
               }               
            }
            protocolEvents.get(protocolDisplayName).add(uiButtonEvent);
         }
      }
   }
   
   /**
    * Gets the protocol events.
    * 
    * @return the protocol events
    */
   public Map<String, List<Command>> getProtocolEvents() {
      return protocolEvents;
   }
   
   /**
    * Gets the uI button events.
    * 
    * @param protocolDisplayName the protocol type
    * 
    * @return the uI button events
    */
   public List<Command> getUIButtonEvents(String protocolDisplayName) {
      if (protocolEvents.containsKey(protocolDisplayName)) {
         return protocolEvents.get(protocolDisplayName);
      } else {
         return new ArrayList<Command>();
      }
   }
   
   /**
    * Iterating the property <b>protocolEvents</b> Generate events xml segment of all different protocols.
    * 
    * @return the xml string
    */
   public String generateUIButtonEventsXml() {
      StringBuffer uiButtonEventXml = new StringBuffer();
      Set<String> protocolDisplayNames = protocolEvents.keySet();
      uiButtonEventXml.append("  <commands>\n");
      for (String protocolDisplayName : protocolDisplayNames) {
         String protocolTagName = ProtocolContainer.findTagName(protocolDisplayName);
         for (Command uiButtonEvent : protocolEvents.get(protocolDisplayName)) {
            uiButtonEventXml.append("    <command id=\"" + uiButtonEvent.getId() + "\" protocol=\"" + protocolTagName + "\"");
            Set<String> protocolAttrKeySet = uiButtonEvent.getProtocolAttrs().keySet();
            String command = uiButtonEvent.getProtocolAttrs().get("command");
            if (command == null) {
               command = "";
            }
            uiButtonEventXml.append(" value=\""+command+"\">");
            for (String attrKey : protocolAttrKeySet) {
               if (!"command".equals(attrKey)) {
                  uiButtonEventXml.append("<property name=\"" + attrKey + "\" value=\"" + uiButtonEvent.getProtocolAttrs().get(attrKey) + "\" />");
               }
            }
            uiButtonEventXml.append("</command>\n");
         }
//         String eventsTagName = protocolTagName + "Events";
//         uiButtonEventXml.append("    <" + eventsTagName + ">\n");
//         for (UIButtonEvent uiButtonEvent : protocolEvents.get(protocolDisplayName)) {
//            String eventTagName = protocolTagName + "Event";
//            uiButtonEventXml.append("      <" + eventTagName + " id=\"" + uiButtonEvent.getId() + "\"");
//            // All other protocol events required "label" attribute except Infrared protocol event.
//            if (!Constants.INFRARED_TYPE.equals(protocolDisplayName)) {
//               uiButtonEventXml.append(" label=\"" + uiButtonEvent.getLabel() + "\"");
//            }
//            Set<String> protocolAttrKeySet = uiButtonEvent.getProtocolAttrs().keySet();
//            for (String attrKey : protocolAttrKeySet) {
//               uiButtonEventXml.append(" " + attrKey + "=\"" + uiButtonEvent.getProtocolAttrs().get(attrKey) + "\"");
//            }
//            uiButtonEventXml.append(" />\n");
//         }
//         uiButtonEventXml.append("    </" + eventsTagName + ">\n");
      }
      uiButtonEventXml.append("  </commands>\n");
      return uiButtonEventXml.toString();
   }
   
   public List<DeviceCommand> getAllDBDeviceCommands() {
     return allDBDeviceCommands;
   }
   
   public void setAllDBDeviceCommands(List<DeviceCommand> allDBDeviceCommands) {
     this.allDBDeviceCommands = allDBDeviceCommands;
   }
   
   public void removeDeviceCommand(DeviceCommand deviceCommand) {
     this.allDBDeviceCommands.remove(deviceCommand);
   }
   
   /**
    * Clear.
    */
   public void clear() {
      protocolEvents.clear();
   }
}
