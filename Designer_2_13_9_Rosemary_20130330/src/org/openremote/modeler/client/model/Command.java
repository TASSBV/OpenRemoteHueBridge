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
package org.openremote.modeler.client.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class UIButtonEvent.
 */
public class Command {

   /** The protocol display name. */
   private String protocolDisplayName;

   /** The id. */
   private long id;

   /** The protocol attrs. */
   private Map<String, String> protocolAttrs = new HashMap<String, String>();

   /** The delay is used in a button event. */
   private String delay = "";

   /** The label is used by all event except irEvent. */
   private String label = "";
   
   /**
    * Gets the id.
    * 
    * @return the id
    */
   public long getId() {
      return id;
   }

   /**
    * Sets the id.
    * 
    * @param id the new id
    */
   public void setId(long id) {
      this.id = id;
   }

   /**
    * Gets the protocol display name.
    * 
    * @return the protocol display name
    */
   public String getProtocolDisplayName() {
      return protocolDisplayName;
   }

   /**
    * Sets the protocol display name.
    * 
    * @param protocolDisplayName the new protocol display name
    */
   public void setProtocolDisplayName(String protocolDisplayName) {
      this.protocolDisplayName = protocolDisplayName;
   }

   /**
    * Gets the protocol attrs.
    * 
    * @return the protocol attrs
    */
   public Map<String, String> getProtocolAttrs() {
      return protocolAttrs;
   }

   /**
    * Sets the protocol attrs.
    * 
    * @param protocolAttrs the protocol attrs
    */
   public void setProtocolAttrs(Map<String, String> protocolAttrs) {
      this.protocolAttrs = protocolAttrs;
   }

   /**
    * Gets the delay.
    * 
    * @return the delay
    */
   public String getDelay() {
      return delay;
   }

   /**
    * Sets the delay.
    * 
    * @param delay the new delay
    */
   public void setDelay(String delay) {
      this.delay = delay;
   }

   /**
    * Gets the label.
    * 
    * @return the label
    */
   public String getLabel() {
      return label;
   }

   /**
    * Sets the label.
    * 
    * @param label the new label
    */
   public void setLabel(String label) {
      this.label = label;
   }
   
}
