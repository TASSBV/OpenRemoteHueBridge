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
package org.openremote.controller.bootstrap.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContext;

import org.openremote.controller.net.IPAutoDiscoveryServer;
import org.openremote.controller.net.RoundRobinTCPServer;
import org.openremote.controller.net.RoundRobinUDPServer;
import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.exception.ControllerException;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.bootstrap.Startup;


/**
 * Controller application initialization point when it is hosted within a Java servlet container. <p>
 *
 * This'll serve us as our application initialization point as long as the controller is a
 * pure servlet web app. If earlier initialization points are required then they need to be
 * hooked into Tomcat's container specific service implementations or Tomcat needs to be
 * wrapped with a service interface and initialized and started programmatically
 * ("embedded tomcat"). <p>
 *
 * As this is part of the standard Java servlet functionality, it will also work with other
 * servlet-based containers (such as Jetty). For Android runtimes without fully-compliant servlet
 * engines, some alternative lifecycle mechanism needs to be used. <p>
 *
 * This implementation is hooked to servlet lifecycle in the web.xml configuration file under
 * webapp's WEB-INF directory.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ServletStartup implements ServletContextListener
{

  /**
   * Defines the context parameter name that must be set in the web applications web.xml file
   * for the service context class initialization. This is a mandatory property that must be
   * present for the application to start.
   *
   * <pre>{@code
   *
   * <context-param>
   *   <param-name>ServiceContextImplementation</param-name>
   *     <param-value>foo.bar.ClassName</param-value>
   *  </context-param>
   *
   * }</pre>
   *
   * In the above, the class 'foo.bar.ClassName' must extend from the abstract base class
   * (@link org.openremote.controller.service.ServiceContext}. A default implementation for
   * Java SE runtime can be found in {@link org.openremote.controller.spring.SpringContext}.
   *
   * @see org.openremote.controller.service.ServiceContext
   * @see org.openremote.controller.spring.SpringContext
   */
  public final static String SERVICE_CONTEXT_IMPL_INIT_PARAM_NAME = "ServiceContextImplementation";



  // Implement ServletContextListener -------------------------------------------------------------


  /**
   * This is invoked by the servlet container after the application (web archive) is loaded but
   * before it starts servicing incoming HTTP requests.  <p>
   *
   * It is the earliest point where we can accomplish application initialization short of hooking
   * directly in the implementation details of particular servlet container implementations.
   *
   * @param event     servlet context event provided by the container with access to the web
   *                  application's environment
   */
  @Override public void contextInitialized(ServletContextEvent event)
  {
    try
    {
      // Initializes the service context for this runtime environment.
      //
      // Purpose of service context is to isolate the core of the implementation from compile-time
      // third-party library dependencies. These service implementations can be switched depending
      // on deployment environment and system resources available. By avoiding direct compile time
      // linking, it is easier to port the controller implementation to other environments. Service
      // dependencies are built at runtime through a service context implementation.
      //
      // The default service context in a servlet runtime is based on Spring library.

      initializeServiceContext(event.getServletContext());
    }

    catch (Throwable t)
    {
      // In case any initialization fails, wrap a clear message to user who is deploying the
      // controller about the error.

      String msg =
          "\n\n=============================================================================\n\n" +

          " Application initialization failed: \n" +
          " " + t.getMessage()  +

          "\n\n=============================================================================\n\n";


      System.err.println(msg);
      t.printStackTrace(System.err);
    }
  }


  /**
   * Empty implementation.
   *
   * @param event     servlet context event provided by the container with access to the web
   *                  application's environment
   */
  @Override public void contextDestroyed(ServletContextEvent event)
  {
    // empty
  }



  // Private Methods ------------------------------------------------------------------------------


  /**
   * Initializes a runtime service context for servlet container based deployments.  <p>
   *
   * The implementation can be configured in web application's web.xml file using the
   * context-param element. See {@link #SERVICE_CONTEXT_IMPL_INIT_PARAM_NAME} for the name
   * of the parameter. <p>
   *
   * This implementation delegates to the generic implementation in
   * {@link Startup#loadServiceContext(String)} but provides servlet container specific
   * functionality.
   *
   * @param ctx   web application context provided by the servlet container
   *
   * @throws InitializationException  if the initialization fails
   */
  private void initializeServiceContext(ServletContext ctx) throws InitializationException
  {

    String serviceContextImplementationClass =
        ctx.getInitParameter(SERVICE_CONTEXT_IMPL_INIT_PARAM_NAME);

    // Check that configuration is present...

    if (serviceContextImplementationClass == null || serviceContextImplementationClass.equals(""))
    {
      throw new InitializationException(
          SERVICE_CONTEXT_IMPL_INIT_PARAM_NAME +
          " initialization parameter in web.xml is missing or empty.\n" +
          " Cannot instantiate controller's service context.\n\n" +

          " Please make sure the following entry is in WEB-INF/web.xml: \n\n" +

          "  <context-param>\n" +
          "    <param-name>ServiceContextImplementation</param-name>\n" +
          "    <param-value>foo.bar.ClassName \n" +
          "       [implements: org.openremote.controller.service.ServiceContext] \n" +
          "       [default: 'org.openremote.controller.service.spring.SpringContext']\n" +
          "    </param-value>\n" +
          "  </context-param>\n\n"
      );
    }

    Startup.loadServiceContext(serviceContextImplementationClass.trim());
  }


}
