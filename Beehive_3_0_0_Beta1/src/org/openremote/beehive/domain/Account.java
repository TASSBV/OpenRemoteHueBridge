/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.beehive.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The Class Account.
 * 
 * @author Dan 2009-7-7
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "account")
public class Account extends BusinessEntity {

   private List<User> users;
   
   private List<Template> templates;

   public Account() {
      templates = new ArrayList<Template>();
   }

   @OneToMany(mappedBy = "account")
   public List<User> getUsers() {
      return users;
   }

   public void setUsers(List<User> users) {
      this.users = users;
   }

   @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
   public List<Template> getTemplates() {
      return templates;
   }


   public void setTemplates(List<Template> templates) {
      this.templates = templates;
   }
   
   public void addTemplate(Template t) {
      if (templates != null) {
         templates.add(t);
      }
   }

   
}
