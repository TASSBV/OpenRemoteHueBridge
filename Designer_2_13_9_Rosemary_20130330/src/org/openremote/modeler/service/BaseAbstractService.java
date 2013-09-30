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

package org.openremote.modeler.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.openremote.modeler.dao.GenericDAO;
import org.openremote.modeler.domain.BusinessEntity;
import org.openremote.modeler.utils.GenericUtil;

/**
 * The common service for all the services.
 * <p/>
 * @param <T> t
 * User: allenwei Date: 2009-2-13 Time: 10:49:20
 */
public abstract class BaseAbstractService<T extends BusinessEntity> {

   /** The generic dao. */
   protected GenericDAO genericDAO;

   /**
    * Sets the generic dao.
    * 
    * @param genericDAO the new generic dao
    */
   public void setGenericDAO(GenericDAO genericDAO) {
      this.genericDAO = genericDAO;
   }

   /**
    * Return the persistent instance of the given entity class with the given identifier.
    * throw exception if not found.
    * 
    * @param id the identifier of the persistent instance
    * 
    * @return the persistent instance
    */
   @SuppressWarnings("unchecked")
   public T loadById(long id) {
      return (T) genericDAO.loadById(GenericUtil.getClassForGenericType(this.getClass()), id);
   }
   
   
   /**
    * Get the persistent instance of the given entity class with the given identifier.
    * return null if not found.
    * 
    * @param id the identifier of the persistent instance
    * 
    * @return the persistent instance
    */
   @SuppressWarnings("unchecked")
   public T getById(long id) {
      return (T) genericDAO.getById(GenericUtil.getClassForGenericType(this.getClass()), id);
   }

   /**
    * Return all persistent instances of the given entity class. Note: Use queries or criteria for retrieving a specific
    * subset.
    * 
    * @return List containing 0 or more persistent instances
    */
   @SuppressWarnings("unchecked")
   public List<T> loadAll() {
      return genericDAO.loadAll(GenericUtil.getClassForGenericType(this.getClass()));
   }

   /**
    * Gets the detached criteria.
    * 
    * @return the detached criteria
    */
   public DetachedCriteria getDetachedCriteria() {
      return DetachedCriteria.forClass(GenericUtil.getClassForGenericType(this.getClass()));
   }

}

