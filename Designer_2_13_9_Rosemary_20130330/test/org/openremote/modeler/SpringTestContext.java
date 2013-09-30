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

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;

/**
 * ApplicationContext for Spring container.
 * 
 * @author Dan 2009-2-16
 */
public class SpringTestContext {

   /** The m_instance. */
   private static SpringTestContext instance;

   /** The context files. */
   private static String[] contextFiles = new String[] {"applicationContext.xml", "spring-service-hibernate-impl.xml",
         "datasource-test.xml", "annomvc-servlet.xml" };

   /** The ctx. */
   private ApplicationContext ctx;

   /**
    * Instantiates a new spring context.
    */
   public SpringTestContext() {
      ctx = new ClassPathXmlApplicationContext(contextFiles);
   }

   /**
    * Instantiates a new spring context.
    * 
    * @param setting
    *           the setting
    */
   public SpringTestContext(String[] setting) {
      ctx = new ClassPathXmlApplicationContext(setting);
   }

   /**
    * Gets a instance of <code>SpringContext</code>.
    * 
    * @return the instance of <code>SpringContext</code>
    */
   public static synchronized SpringTestContext getInstance() {
      if (instance == null) {
         instance = new SpringTestContext(contextFiles);
      }
      return instance;
   }

   /**
    * Gets a bean instance with the given bean identifier.
    * 
    * @param beanId
    *           the given bean identifier
    * 
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
