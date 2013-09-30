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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;



/**
 * The Attribute of Protocol.
 * 
 * @author Dan 2009-7-6
 */
@Entity
@Table(name = "protocol_attr")
public class ProtocolAttr extends BusinessEntity {

   private static final long serialVersionUID = 7659446044086879559L;

   /** The name. */
   private String name;
   
   /** The value. */
   private String value;
   
   /** The protocol. */
   private Protocol protocol;
   
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
    * Gets the value.
    * 
    * @return the value
    */
   @Column(nullable = false)
   @Lob
   public String getValue() {
      return value;
   }

   /**
    * Sets the value.
    * 
    * @param value the new value
    */
   public void setValue(String value) {
      this.value = value;
   }
   
   /**
    * Gets the protocol.
    * 
    * @return the protocol
    */
   @ManyToOne
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

   @Override
   public boolean equals(Object obj) {
      //attention: oid is not needed to be compare. 
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      ProtocolAttr other = (ProtocolAttr) obj;
      if (name == null) {
         if (other.name != null) return false;
      } else if (!name.equals(other.name)) return false;
      if (value == null) {
         if (other.value != null) return false;
      } else if (!value.equals(other.value)) return false;
      return true;
   }
   
}
