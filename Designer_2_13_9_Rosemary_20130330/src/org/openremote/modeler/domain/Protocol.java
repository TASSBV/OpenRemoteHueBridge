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
package org.openremote.modeler.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;


/**
 * The Class is represent a device command's specific instruction.
 * 
 * @author Dan 2009-7-6
 */
@Entity
@Table(name = "protocol")
public class Protocol extends BusinessEntity {
   
   private static final long serialVersionUID = 4999701342536209123L;
   
   public static final String INFRARED_TYPE = "Infrared";

   /** The protocol type, include http, x10, knx, infrared, etc. */
   private String type;
   
   /** The attributes. */
   private List<ProtocolAttr> attributes = new ArrayList<ProtocolAttr>();
   
   /** The protocol represented device command. */
   private DeviceCommand deviceCommand;

   /**
    * Gets the attributes.
    * 
    * @return the attributes
    */
   @OneToMany(mappedBy = "protocol", cascade = CascadeType.ALL,fetch=FetchType.EAGER)
   public List<ProtocolAttr> getAttributes() {
      return attributes;
   }

   /**
    * Sets the attributes.
    * 
    * @param attributes the new attributes
    */
   public void setAttributes(List<ProtocolAttr> attributes) {
      this.attributes = attributes;
   }

  /**
   * Returns the value of the given attribute name or null
   * @param attrName
   * @return
   */
  public String getAttributeValue(String attrName) {
    for (ProtocolAttr attr : attributes) {
      if (attr.getName().equalsIgnoreCase(attrName)) {
          return attr.getValue();
      }
    }
    return null;
  }

   /**
    * Gets the type.
    * 
    * @return the type
    */
   @Column(nullable = false)
   public String getType() {
      return type;
   }

   /**
    * Sets the type.
    * 
    * @param type the new type
    */
   public void setType(String type) {
      this.type = type;
   }

   /**
    * Gets the device event.
    * 
    * @return the device event
    */
   @OneToOne(mappedBy = "protocol")
   public DeviceCommand getDeviceCommand() {
      return deviceCommand;
   }

   /**
    * Sets the device event.
    * 
    * @param deviceCommand the new device event
    */
   public void setDeviceCommand(DeviceCommand deviceCommand) {
      this.deviceCommand = deviceCommand;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      Protocol other = (Protocol) obj;
      if (attributes == null) {
         if (other.attributes != null) return false;
      } else if (!attributes.equals(other.attributes)) return false;
      if (type == null) {
         if (other.type != null) return false;
      } else if (!type.equals(other.type)) return false;
      return true;
   }
   
   public boolean equalsWithoutCompareOid(Protocol other) {
      if (type == null) {
         if (other.type != null) return false;
      } else if (!type.equals(other.type)) return false;
      if (attributes == null) {
         if (other.attributes != null) return false;
      } else if (!(attributes.containsAll(other.attributes) && other.attributes.containsAll(attributes))) return false;
      return true;
   }
   
   /**
    * Adds the given key/value pair as an attribute for this protocol
    * 
    * @param key
    * @param value
    */
   public void addProtocolAttribute(String key, String value) {
     ProtocolAttr protocolAttr = new ProtocolAttr();
     protocolAttr.setName(key);
     protocolAttr.setValue(value);
     protocolAttr.setProtocol(this);
     getAttributes().add(protocolAttr);
   }
   
  public void clearProtocolAttributes() {
    getAttributes().clear();
  }
   
}
