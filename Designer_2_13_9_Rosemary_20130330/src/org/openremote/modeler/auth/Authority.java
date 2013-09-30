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
package org.openremote.modeler.auth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * The Class defines a user's authority, which generate from security context's authentication.
 * It includes a username and a list of roles.
 * 
 */
@SuppressWarnings("serial")
public class Authority implements Serializable {
   
   /** The username. */
   private String username;
   
   /** The roles. */
   private List<String> roles;
   
   /**
    * Instantiates a new authority.
    */
   public Authority() {
      roles = new ArrayList<String>();
   }
   
   /**
    * Gets the username.
    * 
    * @return the username
    */
   public String getUsername() {
      return username;
   }
   
   /**
    * Gets the roles.
    * 
    * @return the roles
    */
   public List<String> getRoles() {
      return roles;
   }
   
   /**
    * Sets the username.
    * 
    * @param username the new username
    */
   public void setUsername(String username) {
      this.username = username;
   }

   /**
    * Adds the role.
    * 
    * @param role the role
    */
   public void addRole(String role) {
      roles.add(role);
   }
   
   
   
}
