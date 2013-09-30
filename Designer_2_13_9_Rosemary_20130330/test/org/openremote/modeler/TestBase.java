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
package org.openremote.modeler;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * The Class TestBase.
 * 
 * @author Dan 2009-7-31
 */
public abstract class TestBase extends TestCase {
   
   private static final Logger log = Logger.getLogger(TestBase.class);
   
   /** The session factory. */
   private SessionFactory sessionFactory;

   /**
    * {@inheritDoc}
    * @see junit.framework.TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
      sessionFactory = (SessionFactory) SpringTestContext.getInstance().getBean("sessionFactory");

      Session s = sessionFactory.openSession();
      TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(s));

   }

   /**
    * {@inheritDoc}
    * @see junit.framework.TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      super.tearDown();
      SessionHolder holder = (SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
      Session s = holder.getSession();
      try {
         s.flush();
      } catch (Throwable e) {
         log.error("Can not flush session", e);
      }

      TransactionSynchronizationManager.unbindResource(sessionFactory);
      SessionFactoryUtils.closeSession(s);
   }

}
