/*
 * OpenRemote, the Home of the Digital Home.
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

package org.openremote.modeler.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.dao.DataAccessException;


/**
 * The general DAO for all the DAO. It provides basic CRUD operations using Hibernate.
 * 
 * @author Dan 2009-2-6
 */
public class GenericDAO extends HibernateDaoSupport {

   /**
    * Persist the given transient instance.
    * 
    * @param o the transient instance to persist
    * 
    * @return the generated identifier
    */
   public Serializable save(Object o) {
      return getHibernateTemplate().save(o);
   }

   public Object merge(Object o) {
      return getHibernateTemplate().merge(o);
   }

   /**
    * Save or update.
    * 
    * @param o the o
    */
   public void saveOrUpdate(Object o) {
      getHibernateTemplate().saveOrUpdate(o);
   }
   
   @SuppressWarnings("unchecked")
   public <T> T loadById(Class<T> clazz, Serializable id) {
      return (T) getHibernateTemplate().load(clazz, id);
   }


  public <T> T getById(Class<T> clazz, Serializable id)
  {
    try
    {
      return (T) getHibernateTemplate().get(clazz, id);
    }

    catch (DataAccessException e)
    {
      // TODO :
      //   Convert to a different type of runtime error here to contain third party library
      //   dependencies higher up in the code. Should really be a checked exception
      //   to force everyone to deal with database issues but that will be a bigger change
      //   for later.
      //                                                                            [JPL]

      throw new DatabaseError(e.getMessage());
    }
  }

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
    * Delete the given persistent instance.
    *
    * @param o the persistent instance to delete
    */
   public void delete(Object o) {
      getHibernateTemplate().delete(o);
   }

   /**
    * Delete all given persistent instances. This can be combined with any of the find methods to delete by query in two
    * lines of code.
    * @param <T> t
    * 
    * @param entities the persistent instances to delete
    */
   public <T> void deleteAll(List<T> entities) {
      getHibernateTemplate().deleteAll(entities);
   }

   /**
    * Return all persistent instances of the given entity class. Note: Use queries or criteria for retrieving a specific
    * subset.
    * 
    * @param clazz a persistent class
    * @param <T> t
    * 
    * @return a List containing 0 or more persistent instances
    */
   @SuppressWarnings("unchecked")
   public <T> List<T> loadAll(Class<T> clazz) {
      return (List<T>) getHibernateTemplate().loadAll(clazz);
   }

   /**
    * Inserts a batch of given persistent instances.
    * 
    * @param insertList persistent instances to insert.
    * @param <T> t
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
    * Finds by DetachedCriteria.
    * 
    * @param detachedCriteria DetachedCriteria
    * @param <T> t
    * 
    * @return the objects (of the target class) to find
    */
   @SuppressWarnings("unchecked")
   public <T> List<T> findByDetachedCriteria(final DetachedCriteria detachedCriteria) {
      return (List<T>) getHibernateTemplate().findByCriteria(detachedCriteria);
   }

   /**
    * Closes the StatelessSession.
    * 
    * @param session the session to close
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
    * Counts all the record according to one field.
    * 
    * @param clazz the class you want to count
    * @param fieldName the field name
    * @param fieldValue the field value
    * 
    * @return count number
    */
   @SuppressWarnings("unchecked")
   public int countByField(final Class clazz, final String fieldName, final Object fieldValue) {
      if (fieldValue != null && !"".equals(fieldValue.toString().trim())) {
         return Integer.valueOf(getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws SQLException {
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
    * @param o the persistent instance to re-read
    */
   public void refresh(Object o) {
      getHibernateTemplate().refresh(o);
   }

   /**
    * Find persistent instances by DetachedCriteria in pagination.
    * 
    * @param detachedCriteria DetachedCriteria
    * @param pageSize page size
    * @param startPos start position
    * @param <T> t
    * 
    * @return persistent instances to find
    */
   @SuppressWarnings("unchecked")
   public <T> List<T> findPagedDateByDetachedCriteria(final DetachedCriteria detachedCriteria, final int pageSize,
         final int startPos) {
      return (List<T>) getHibernateTemplate().execute(new HibernateCallback() {
         public Object doInHibernate(Session session) throws SQLException {
            Criteria criteria = detachedCriteria.getExecutableCriteria(session);
            criteria.setMaxResults(pageSize).setFirstResult(startPos);
            return criteria.list();
         }
      }, true);
   }

   /**
    * Counts By DetachedCriteria.
    * 
    * @param detachedCriteria must have Projections.count
    * 
    * @return the count
    */
   public int countByDetachedCriteria(final DetachedCriteria detachedCriteria) {
      return Integer.valueOf(getHibernateTemplate().execute(new HibernateCallback() {
         public Object doInHibernate(Session session) throws SQLException {
            Criteria criteria = detachedCriteria.getExecutableCriteria(session);
            return criteria.uniqueResult();
         }
      }, true).toString());
   }
   
   public void initialize(Object arg0) {
       this.getHibernateTemplate().initialize(arg0);
   }
   
   public void flush() {
      this.getHibernateTemplate().flush();
   }
   
   public void update(Object arg0){
      this.getHibernateTemplate().update(arg0);
   }


  // Nested Classes -------------------------------------------------------------------------------

  /**
   * TODO :
   *
   *   a temporary error type to manage third party library dependencies -- this is a temporary
   *   construct, should really throw checked PersistenceExceptions from this layer to force
   *   everyone to deal with database issues in the higher code layers
   * 
   */
  public static class DatabaseError extends Error
  {
    DatabaseError(String msg)
    {
      super(msg);
    }
  }
}
