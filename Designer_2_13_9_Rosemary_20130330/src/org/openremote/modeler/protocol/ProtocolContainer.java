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

package org.openremote.modeler.protocol;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The Class is used for containing <b>ProtocolDefinition</b> of different protocol types.</br>
 * The container defined as a hash map, it use protocol displayname as the key, protocolDefinition 
 * as the value.</br></br>
 * 
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
public class ProtocolContainer implements Serializable {
   
   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = -5478194408714473866L;
   
   /** The protocols. */
   private static Map<String, ProtocolDefinition> protocols = new HashMap<String, ProtocolDefinition>();

   /**
    * Gets the protocols.
    * 
    * @return the protocols
    */
   public  Map<String, ProtocolDefinition> getProtocols() {
      return protocols;
   }

   /**
    * Sets the protocols.
    * 
    * @param protocols the protocols
    */
   public  void setProtocols(Map<String, ProtocolDefinition> protocols) {
      ProtocolContainer.protocols = protocols;
   }


   /** The instance. */
   private static ProtocolContainer instance = new ProtocolContainer();
   
   /**
    * Instantiates a new protocol container.
    */
   private ProtocolContainer() {
     
   }

   /**
    * Gets the single instance of ProtocolContainer.
    * 
    * @return single instance of ProtocolContainer
    */
   public static synchronized ProtocolContainer getInstance() {
      return instance;
   }


   /**
    * Read protocol definitions.
    */
   public static void readProtocolDefinitions() {
      
   }

   /**
    * Gets the.
    * 
    * @param name the name
    * 
    * @return the protocol definition
    */
   public ProtocolDefinition get(String name) {
      if (protocols.containsKey(name)) {
         return protocols.get(name);
      }
      return null;
   }
   
   /**
    * Find tag name.
    * 
    * @param protocolDisplayName the protocol display name
    * 
    * @return the string
    */
   public static String findTagName(String protocolDisplayName) {
      ProtocolDefinition protocolDefinition = protocols.get(protocolDisplayName);
      return protocolDefinition == null ? "" : protocolDefinition.getTagName();
   }
}
