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

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


/**
 * The Class Role.
 * 
 * @author Dan 2009-7-7
 */
@Entity
@Table(name = "role")
public class Role extends BusinessEntity {
   
   private static final long serialVersionUID = 3075074829017736369L;

   /** The Constant ROLE_MODELER. */
   public static final String ROLE_MODELER = "ROLE_MODELER";
   
   /** The Constant ROLE_DESIGNER. */
   public static final String ROLE_DESIGNER = "ROLE_DESIGNER";
   
   public static final String ROLE_ADMIN = "ROLE_ADMIN";

   /** The name. */
   private String name;
   
   /** The users. */
   private List<User> users;

   /**
    * Gets the name.
    * 
    * @return the name
    */
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
    * Gets the users.
    * 
    * @return the users
    */
   @ManyToMany(mappedBy = "roles")
   public List<User> getUsers() {
      return users;
   }

   /**
    * Sets the users.
    * 
    * @param users the new users
    */
   public void setUsers(List<User> users) {
      this.users = users;
   }
   
   
}
