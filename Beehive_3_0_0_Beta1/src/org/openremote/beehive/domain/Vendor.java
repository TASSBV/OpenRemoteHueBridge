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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * This is the top level hierarchy shown in http://lirc.sourceforge.net/remotes/. Such as Sony, Apple, Samsung etc.
 * 
 * @author Dan 2009-2-6
 */
@Entity
@SuppressWarnings("serial")
@Table(name = "vendor")
public class Vendor extends BusinessEntity {

   private String name;
   private List<Model> models;

   public Vendor() {
      models = new ArrayList<Model>();
   }

   @Column(nullable = false)
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @OneToMany(mappedBy = "vendor", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
   public List<Model> getModels() {
      return models;
   }

   public void setModels(List<Model> models) {
      this.models = models;
   }

}
