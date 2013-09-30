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

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Business entity class for all JPA entities with the common property oid.
 * 
 * @author Dan 2009-2-6
 * 
 */
@MappedSuperclass
public abstract class BusinessEntity implements Serializable {

   private static final long serialVersionUID = -3871334485197341321L;
   private long oid;

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   public long getOid() {
      return oid;
   }

   public void setOid(long oid) {
      this.oid = oid;
   }
}
