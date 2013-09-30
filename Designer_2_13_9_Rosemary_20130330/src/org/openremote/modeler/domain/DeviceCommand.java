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

import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * DeviceCommand is represent a command that the device have, which can be sent 
 * to the device and control it.
 * 
 * @author Dan 2009-7-6
 */
@Entity
@Table(name = "device_command")
public class DeviceCommand extends BusinessEntity {
   
   private static final long serialVersionUID = -3654650649337382535L;

   /** The device that the command belonged to. */
   private Device device;
   
   /** Represent the command's type(e.g:Infrared, KNX, X10, etc.), it include protocol attributes. */
   private Protocol protocol;
   
   /** The name. */
   private String name;
   
   /** The section id is optional property, and is for the command which import from beehive(Infrared protocol). */
   private String sectionId;

   /**
    * Gets the name.
    * 
    * @return the name
    */
   @Column(nullable = false)
   public String getName() {
      return name;
   }

   /**
    * Sets the name.
    * 
    * @param name the new name
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * Gets the device.
    * 
    * @return the device
    */
   @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.MERGE })
   public Device getDevice() {
      return device;
   }

   /**
    * Sets the device.
    * 
    * @param device the new device
    */
   public void setDevice(Device device) {
      this.device = device;
   }
   
   /**
    * Gets the protocol.
    * 
    * @return the protocol
    */
   @OneToOne(cascade = CascadeType.ALL ,fetch = FetchType.EAGER)
   @JoinColumn(nullable = false)
   public Protocol getProtocol() {
      return protocol;
   }

   /**
    * Sets the protocol.
    * 
    * @param protocol the new protocol
    */
   public void setProtocol(Protocol protocol) {
      this.protocol = protocol;
   }

   /* (non-Javadoc)
    * @see org.openremote.modeler.domain.BusinessEntity#getDisplayName()
    */
   @Override
   @Transient
   public String getDisplayName() {
      return getName();
   }


   /**
    * Sets the section id.
    * 
    * @param sectionId the new section id
    */
   public void setSectionId(String sectionId) {
      this.sectionId = sectionId;
   }

   /**
    * Gets the section id.
    * 
    * @return the section id
    */
   public String getSectionId() {
      return sectionId;
   }
   @Transient
   public DeviceCommandRef getDeviceCommandRef() {
      DeviceCommandRef cmdRef = new DeviceCommandRef(this);
      return cmdRef;
   }

   @Override 
   public int hashCode() {
      return (int) getOid();
   }
   
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      DeviceCommand other = (DeviceCommand) obj;
      if (device == null) {
         if (other.device != null) return false;
      } else if (!device.equals(other.device)) return false;
      if (name == null) {
         if (other.name != null) return false;
      } else if (!name.equals(other.name)) return false;
      if (protocol == null) {
         if (other.protocol != null) return false;
      } else if (!protocol.equals(other.protocol)) return false;
      if (sectionId == null) {
         if (other.sectionId != null) return false;
      } else if (!sectionId.equals(other.sectionId)) return false;
      return this.getOid() == other.getOid();
   }
   /**
    * This method is used for checking whether same device command exists when try to rebuild command for user from template. 
    * @param other
    * @return
    */
   public boolean equalsWithoutCompareOid(DeviceCommand other) {
      if (other == null) return false;
      if (device == null) {
         if (other.device != null) return false;
      } else if (other.device != null && device.getOid() != other.device.getOid()) return false;
      if (name == null) {
         if (other.name != null) return false;
      } else if (!name.equals(other.name)) return false;
      if (protocol == null) {
         if (other.protocol != null) return false;
      } else if (other.protocol != null && !protocol.equalsWithoutCompareOid(other.protocol)) return false;
      if (sectionId == null) {
         if (other.sectionId != null) return false;
      } else if (!sectionId.equals(other.sectionId)) return false;
      return true;
   }
   
   public Protocol createProtocol(String type) {
     Protocol proto = new Protocol();
     proto.setType(type);
     proto.setDeviceCommand(this);
     setProtocol(proto);
     return proto;
   }
   
   public Protocol createProtocolWithAttributes(String type, Map<String, String> attributes) {
     Protocol proto = createProtocol(type); 
     for (Entry<String, String> e : attributes.entrySet()) {
       proto.addProtocolAttribute(e.getKey(), e.getValue());
     }
     return proto;
   }
}
