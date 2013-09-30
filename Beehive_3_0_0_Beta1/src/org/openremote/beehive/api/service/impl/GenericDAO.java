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
package org.openremote.beehive.api.service.impl;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * The general DAO for all the DAO. It provides basic CRUD operations using Hibernate.
 * 
 * @author Dan 2009-2-6
 * 
 */
public class GenericDAO extends HibernateDaoSupport {

   /**
    * Persist the given transient instance.
    * 
    * @param o
    *           the transient instance to persist
    * @return the generated identifier
    */
   public Serializable save(Object o) {
      return getHibernateTemplate().save(o);
   }

   public void saveOrUpdate(Object o) {
      getHibernateTemplate().saveOrUpdate(o);
   }

   /**
    * Copy the state of the given object onto the persistent object with the same identifier. Follows JSR-220 semantics.
    * Similar to saveOrUpdate, but never associates the given object with the current Hibernate Session. In case of a
    * new entity, the state will be copied over as well.
    * 
    * Note that merge will not update the identifiers in the passed-in object graph (in contrast to TopLink)! Consider
    * registering Spring's IdTransferringMergeEventListener if you would like to have newly assigned ids transferred to
    * the original object graph too.
    * 
    * @param o
    *           the object to merge with the corresponding persistence instance
    * @return the updated, registered persistent instance
    */
   public Object merge(Object o) {
      return getHibernateTemplate().merge(o);
   }

   /**
    * Return the persistent instance of the given entity class with the given identifier, throwing an exception if not
    * found. This method is a thin wrapper around HibernateTemplate.load(Class, java.io.Serializable) for convenience.
    * For an explanation of the exact semantics of this method, please do refer to the Hibernate API documentation in
    * the first instance.
    * 
    * @param <T>
    *           a persistent generics
    * @param clazz
    *           a persistent class
    * @param id
    *           the identifier of the persistent instance
    * @return the persistent instance
    */
   @SuppressWarnings("unchecked")
   public <T> T loadById(Class<T> clazz, Serializable id) {
      return (T) getHibernateTemplate().load(clazz, id);
   }

   /**
    * Return the persistent instance of the given entity class with the given identifier, or null if not found. This
    * method is a thin wrapper around HibernateTemplate.get(Class, java.io.Serializable) for convenience. For an
    * explanation of the exact semantics of this method, please do refer to the Hibernate API documentation in the first
    * instance.
    * 
    * @param <T>
    *           a persistent generics
    * @param clazz
    *           a persistent class
    * @param id
    *           the identifier of the persistent instance
    * @return the persistent instance, or null if not found
    */
   @SuppressWarnings("unchecked")
   public <T> T getById(Class<T> clazz, Serializable id) {
      return (T) getHibernateTemplate().get(clazz, id);
   }

   /**
    * Return the persistent instance of the given entity class with the given non-identifier field, or null if not
    * found. This method is a thin wrapper around HibernateTemplate.findByCriteria(DetachedCriteria criteria, int
    * firstResult, int maxResults)convenience. For an explanation of the exact semantics of this method, please do refer
    * to the Hibernate API documentation in the first instance.
    * 
    * @param <T>
    *           a persistent generics
    * @param clazz
    *           a persistent class
    * @param fieldName
    *           field name
    * @param fieldValue
    *           field value
    * @return the persistent instance, or null if not found
    */
   @SuppressWarnings("unchecked")
   public <T> T getByNonIdField(Class<T> clazz, String fieldName, String fieldValue) {
      List retList = new ArrayList();
      DetachedCriteria criteria = DetachedCriteria.forClass(clazz);
      criteria.add(Restrictions.eq(fieldName, fieldValue));
      retList = getHibernateTemplate().findByCriteria(criteria, 0, 1);
      if (retList != null && retList.size() > 0) {
         return (T) retList.get(0);
      } else {
         return null;
      }
   }
   
   /**
    * Gets the by max id.
    * 
    * @param clazz the clazz
    * 
    * @return the by max id
    */
   @SuppressWarnings("unchecked")
   public <T> T getByMaxId(Class<T> clazz) {
      List retList = new ArrayList();
      DetachedCriteria criteria = DetachedCriteria.forClass(clazz);
      criteria.addOrder(Order.desc("oid"));
      retList = getHibernateTemplate().findByCriteria(criteria, 0, 1);
      if (retList != null && retList.size() > 0) {
         return (T) retList.get(0);
      } else {
         return null;
      }
   }

   /**
    * Delete the given persistent instance.
    * 
    * @param o
    *           the persistent instance to delete
    */
   public void delete(Object o) {
      getHibernateTemplate().delete(o);
   }

   /**
    * Delete all given persistent instances. This can be combined with any of the find methods to delete by query in two
    * lines of code.
    * 
    * @param <T>
    *           a persistent generics
    * @param entities
    *           the persistent instances to delete
    */
   public <T> void deleteAll(List<T> entities) {
      getHibernateTemplate().deleteAll(entities);
   }

   /**
    * Return all persistent instances of the given entity class. Note: Use queries or criteria for retrieving a specific
    * subset.
    * 
    * @param <T>
    *           a persistent generics
    * @param clazz
    *           a persistent class
    * @return a List containing 0 or more persistent instances
    */
   @SuppressWarnings("unchecked")
   public <T> List<T> loadAll(Class<T> clazz) {
      return (List<T>) getHibernateTemplate().loadAll(clazz);
   }

   /**
    * Inserts a batch of given persistent instances.
    * 
    * @param <T>
    *           a persistent generics
    * @param insertList
    *           persistent instances to insert.
    */
   public <T> void batchInsert(final List<T> insertList) {
      StatelessSession statelessSession = getSessionFactory().openStatelessSession();
      try {
         for (T o : insertList) {
            statelessSession.insert(o);
         }
      } catch (HibernateException ex) {
         throw convertHibernateAccessException(ex);
      } catch (RuntimeException ex) {
         throw ex;
      } finally {
         closeStatelessSession(statelessSession);
      }
   }

   /**
    * Finds by DetachedCriteria
    * 
    * @param <T>
    *           a persistent generics
    * @param detachedCriteria
    *           DetachedCriteria
    * @return the objects (of the target class) to find
    */
   @SuppressWarnings("unchecked")
   public <T> List<T> findByDetachedCriteria(final DetachedCriteria detachedCriteria) {
      return (List<T>) getHibernateTemplate().findByCriteria(detachedCriteria);
   }
   
   @SuppressWarnings("unchecked")
   public <T> T findOneByDetachedCriteria(final DetachedCriteria detachedCriteria) {
      List retList = getHibernateTemplate().findByCriteria(detachedCriteria, 0, 1);
      if (retList != null && retList.size() > 0) {
         return (T) retList.get(0);
      } else {
         return null;
      }
   }

   /**
    * Closes the StatelessSession
    * 
    * @param session
    *           the session to close
    */
   private void closeStatelessSession(StatelessSession session) {
      if (session != null) {
         logger.debug("Closing Hibernate StatelessSession");
         try {
            session.close();
         } catch (HibernateException ex) {
            logger.warn("Could not close Hibernate StatelessSession", ex);
         } catch (Throwable ex) {
            logger.warn("Unexpected exception on closing Hibernate StatelessSession", ex);
         }
      }
   }

   /**
    * Counts all the record according to one field
    * 
    * @param clazz
    *           the class you want to count
    * @param fieldName
    * @param fieldValue
    * @return count number
    */
   @SuppressWarnings("unchecked")
   public int countByField(final Class clazz, final String fieldName, final Object fieldValue) {
      if (fieldValue != null && !"".equals(fieldValue.toString().trim())) {
         return Integer.valueOf(getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
               Criteria criteria = session.createCriteria(clazz.getName());
               return criteria.add(Restrictions.eq(fieldName, fieldValue)).setProjection(Projections.count(fieldName))
                     .uniqueResult();
            }
         }, true).toString());
      } else {
         return 0;
      }
   }

   /**
    * Re-read the state of the given persistent instance.
    * 
    * @param o
    *           the persistent instance to re-read
    */
   public void refresh(Object o) {
      getHibernateTemplate().refresh(o);
   }

   /**
    * Find persistent instances by DetachedCriteria in pagination.
    * 
    * @param <T>
    *           a persistent generics
    * @param detachedCriteria
    *           DetachedCriteria
    * @param pageSize
    *           page size
    * @param startPos
    *           start position
    * @return persistent instances to find
    */
   @SuppressWarnings("unchecked")
   public <T> List<T> findPagedDateByDetachedCriteria(final DetachedCriteria detachedCriteria, final int pageSize,
         final int startPos) {
      return (List<T>) getHibernateTemplate().execute(new HibernateCallback() {
         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            Criteria criteria = detachedCriteria.getExecutableCriteria(session);
            criteria.setMaxResults(pageSize).setFirstResult(startPos);
            return criteria.list();
         }
      }, true);
   }

   /**
    * Counts By DetachedCriteria
    * 
    * @param detachedCriteria
    *           must have Projections.count
    * @return the count
    */
   public int countByDetachedCriteria(final DetachedCriteria detachedCriteria) {
      return Integer.valueOf(getHibernateTemplate().execute(new HibernateCallback() {
         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            Criteria criteria = detachedCriteria.getExecutableCriteria(session);
            return criteria.uniqueResult();
         }
      }, true).toString());
   }
   
   /**
    * Flush.
    */
   public void flush(){
      getHibernateTemplate().flush();
   }
  
   
}
