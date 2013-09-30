/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.spring;

import java.util.Properties;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;
import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.command.CommandFactory;

/**
 * TODO :
 *
 *   ORCJAVA-195 : remove direct API references to this subclass of service context
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Dan 2009-2-16
 */
public class SpringContext extends ServiceContext
{


  // Class Members --------------------------------------------------------------------------------

  private static String[] contextFiles = new String[] { "spring-context.xml" };


  /**
   * TODO : should be removed, see ORCJAVA-195
   */
  @Deprecated public synchronized static SpringContext getInstance()
  {
    return (SpringContext)ServiceContext.getInstance();
  }



  // Instance Fields ------------------------------------------------------------------------------

  private ApplicationContext ctx;


  // Constructors ---------------------------------------------------------------------------------

  public SpringContext() throws InstantiationException
  {
    this(contextFiles);
  }

  private SpringContext(String[] setting) throws InstantiationException
  {
    ctx = new ClassPathXmlApplicationContext(setting);

    ServiceContext.registerServiceContext(this);
  }


  /**
   * Gets a bean instance with the given bean identifier
   *
   * @param beanId
   *           the given bean identifier
   * @return a bean instance
   */
  public Object getBean(String beanId)
  {
    Object o = ctx.getBean(beanId);

    if (o instanceof TransactionProxyFactoryBean)
    {
       TransactionProxyFactoryBean factoryBean = (TransactionProxyFactoryBean) o;
       o = factoryBean.getObject();
    }

    return o;
  }


  // Implements ServiceContext --------------------------------------------------------------------


  /**
   * TODO
   */
  @Override protected void initializeController()
  {
    getDeployer().startController();
  }

  /**
   * Implements service lookup using Spring API.
   *
   * @param   name    service name
   *
   * @return  bean reference
   */
  @Override protected Object getService(ServiceName name)
  {
    return getBean(name.getSpringBeanName());
  }




}
