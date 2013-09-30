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
package org.openremote.beehive;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;


public abstract class TestBase extends TestCase {
   private SessionFactory sessionFactory;

   protected void setUp() throws Exception {
      super.setUp();
      sessionFactory = (SessionFactory) SpringTestContext.getInstance().getBean("sessionFactory");

      Session s = sessionFactory.openSession();
      TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(s));

   }

   protected void tearDown() throws Exception {
      super.tearDown();
      SessionHolder holder = (SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
      Session s = holder.getSession();
      try {
         s.flush();
      } catch (Throwable e) {
         e.printStackTrace();
      }

      TransactionSynchronizationManager.unbindResource(sessionFactory);
      SessionFactoryUtils.closeSession(s);
   }

}
