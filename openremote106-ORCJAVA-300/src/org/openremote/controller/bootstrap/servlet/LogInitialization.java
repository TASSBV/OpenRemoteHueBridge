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

import java.util.logging.Logger;
import java.util.logging.LogManager;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;

import org.openremote.controller.bootstrap.Startup;
import org.openremote.controller.Constants;

/**
 * Part of a Controller's web application bootstrap for a servlet container.  <p>
 *
 * This is a servlet application context listener implementation which sets up java.util.logging
 * redirect to log4j implementation. It is separated out from the rest of the web app bootstrap
 * so that it can initiated early and therefore logging is redirected early, before other
 * components are initiated and start logging.   <p>
 *
 * This implementation is hooked to servlet lifecycle in the web.xml configuration file under
 * webapp's WEB-INF directory. It delegates to a generic
 * {@link org.openremote.controller.bootstrap.Startup} class which can be shared across all
 * different bootstrap implementations.
 *
 * @see org.openremote.controller.bootstrap.Startup
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class LogInitialization implements ServletContextListener
{

  /**
   * Initialize java.util.Logging redirector to Log4J implementation. Delegates to
   * {@link org.openremote.controller.bootstrap.Startup#redirectJULtoLog4j()} implementation
   * which is generic without relying on any specific bootstrap or container sevices.
   *
   * @see org.openremote.controller.bootstrap.Startup#redirectJULtoLog4j()
   * 
   * @param event   servlet context event provided by the container with access to the web
   *                application's environment
   */
  @Override public void contextInitialized(ServletContextEvent event)
  {
    // Strictly speaking Log4j is probably not necessary (JUL does the job just fine) but
    // currently a lot of the implementation and most likely many of the third party libraries
    // default to Log4j as the logging framework. Therefore default to redirect all the JUL
    // logging to Log4j for now.
    //
    // In general, org.openremote implementations should have compile-time dependency either
    // to java.util.logging API or org.openremote.util.Logger API (where latter is the preferred
    // option). This minimizes the third party API dependencies which helps in portability,
    // especially where it is prudent to try to minimize the runtime size (therefore using
    // JUL logging in class libraries instead of external Log4j implementation).
    //
    // For a standard servlet implementation (which this initialization is part of), the system
    // resource requirements are such that external Log4j library is probably manageable.
    // Eventually this dependency might be removed altogether, making this redirect unnecessary.

    Startup.redirectJULtoLog4j();


    // Programmatically disable log recording on parent log categories and their handlers. This
    // currently cannot be done through the Apache Tomcat's conf/logging.properties file (due
    // to how it is implemented) so doing it here -- since this can be somewhat specific to
    // Tomcat, currently not setting this in the generic Startup.redirectJULtoLog4j implementation.

    doNotDelegateControllerLogsToParentHandlers();
  }

  /**
   * Empty implementation
   *
   * @param event   servlet context event provided by the container with access to the web
   *                application's environment
   */
  @Override public void contextDestroyed(ServletContextEvent event)
  {
    // does nothing
  }




  // Private Instance Methods ---------------------------------------------------------------------

  /**
   * The Apache implementation defaults to setting 'use parent handlers' as false to all JUL
   * handlers that are explicitly declared in logging.properties file. This is the behavior
   * we want for the controller log root category as well. However, we are setting the handler
   * for controller root log category programmatically (to log to log4j, see
   * {@link org.openremote.controller.bootstrap.Startup#redirectJULtoLog4j()) which
   * Apache's LogManager implementation is not aware of. Therefore we must explicitly set
   * the '.useParentHandlers' property to false.
   *
   * Ideally this could be done declaratively in the logging.properties file. However, the
   * Apache implementation ignores an explicit false setting to this property with the
   * (incorrect) assumption that it is not necessary to explicitly set, since all handlers
   * default to false value (which is only true if they're configured through the
   * logging.properties file).
   *
   * Therefore we set the property here programmatically, rather than declaratively in the
   * config file.
   *
   * Alternative fixes would be to replace the Apache JUL LogManager implementation completely
   * which may be relevant at some point (where log4j is no longer desired) but at the moment is
   * too much of a distraction. The other option would be to configure the log4j redirector
   * declaratively through Tomcat's conf/logging.properties (rather than programmatically) but
   * this is likely to require the handler implementation to be extracted from the web archive
   * and deployed as part of Tomcat's server library since the logging is initialized far
   * earlier than loading web archive bundles.
   *
   * http://svn.apache.org/viewvc/tomcat/trunk/java/org/apache/juli/ClassLoaderLogManager.java?revision=1060586&view=markup
   * 
   */
  private void doNotDelegateControllerLogsToParentHandlers()
  {
    try
    {
      final Logger controllerRootLogger = Logger.getLogger(Constants.CONTROLLER_ROOT_LOG_CATEGORY);

      // ---- BEGIN PRIVILEGED CODE BLOCK ---------------------------------------------------------

      AccessController.doPrivilegedWithCombiner(new PrivilegedAction<Void>()
      {
        public Void run()
        {
          controllerRootLogger.setUseParentHandlers(false);

          return null;
        }
      });

      // ---- END PRIVILEGED CODE BLOCK -----------------------------------------------------------

      controllerRootLogger.info(
          "Programmatically set 'useParentHandlers=false' in '" +
          Constants.CONTROLLER_ROOT_LOG_CATEGORY + "' log category."
      );
    }

    catch (SecurityException exception)
    {
      LogManager.getLogManager().getLogger("").warning(
          "Can't disable parent handler delegation on " + Constants.CONTROLLER_ROOT_LOG_CATEGORY +
          " log category due to security restrictions: " + exception.getMessage()
      );
    }

  }

}

