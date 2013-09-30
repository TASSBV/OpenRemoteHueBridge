/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;

/**
 * ApplicationContext for Spring container
 * 
 * @author Handy.Wang 2009-11-12
 */
public class SpringTestContext {

   private static SpringTestContext m_instance;

   private static String[] contextFiles = new String[] { "spring-context-test.xml" };

   private ApplicationContext ctx;

   public SpringTestContext() {
      ctx = new ClassPathXmlApplicationContext(contextFiles);
   }

   public SpringTestContext(String[] setting) {
      ctx = new ClassPathXmlApplicationContext(setting);
   }

   /**
    * Gets a instance of <code>SpringContext</code>
    * 
    * @return the instance of <code>SpringContext</code>
    */
   public synchronized static SpringTestContext getInstance() {
      if (m_instance == null) {
         m_instance = new SpringTestContext(contextFiles);
      }
      return m_instance;
   }

   /**
    * Gets a bean instance with the given bean identifier
    * 
    * @param beanId
    *           the given bean identifier
    * @return a bean instance
    */
   public Object getBean(String beanId) {
      Object o = ctx.getBean(beanId);
      if (o instanceof TransactionProxyFactoryBean) {
         TransactionProxyFactoryBean factoryBean = (TransactionProxyFactoryBean) o;
         o = factoryBean.getObject();
      }
      return o;
   }

}
