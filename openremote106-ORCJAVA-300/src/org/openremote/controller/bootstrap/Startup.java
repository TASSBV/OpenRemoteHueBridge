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
package org.openremote.controller.bootstrap;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogManager;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;

import org.openremote.controller.Constants;
import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.exception.InitializationException;

/**
 * Generic startup implementation for controller. Depending on deployment environment (servlet,
 * stand-alone, etc.), particular server bootstrap mechanisms can delegate to this implementation.
 *
 * TODO: It currently lacks a <tt>main()</tt> method of its own to provide a stand-alone bootstrap.
 *
 * @see org.openremote.controller.bootstrap.servlet.LogInitialization
 * @see org.openremote.controller.bootstrap.servlet.ServletStartup
 *
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Startup 
{

  /**
   * Instantiate a specific ServiceContext implementation and execute its no-args constructor.
   * The service context implementation is assumed to automatically register itself as the
   * implementation for this controller's service context.
   *
   * @see org.openremote.controller.service.ServiceContext
   *
   * @param   serviceContextClassName   fully qualified class name of the service context
   *                                    implementation
   *
   * @throws InitializationException    if there are errors in instantiating the service context
   */
  public static void loadServiceContext(String serviceContextClassName) throws InitializationException
  {

    if (serviceContextClassName == null || serviceContextClassName.equals(""))
      throw new InitializationException("Service context class name is null or empty.");

    serviceContextClassName = serviceContextClassName.trim();

    try
    {
      // Not executing this in the privileged code segment... the class to load is parameterized
      // and could therefore be used to load any hostile class. Therefore if security manager is
      // present, it must be configured to grant privileges to particular service context class
      // implementation.

      Class clazz = Thread.currentThread().getContextClassLoader()
          .loadClass(serviceContextClassName);

      ServiceContext ctx = (ServiceContext)clazz.newInstance();

      // Make an explicit call to initialize the controller...

      ctx.init();
    }

    catch (SecurityException exception)
    {
      throw new InitializationException(
          "Could not instantiate a service context implementation ('" +
          serviceContextClassName + "') due to security restriction. " +
          "If security manager has been configured, it may deny access to class " +
          "instantion: " + exception.getMessage(), exception
      );
    }

    catch (ExceptionInInitializerError error)
    {
      Throwable cause = error.getCause();

      String causeMsg = "";

      if (cause != null)
        causeMsg = cause.getMessage();

      throw new InitializationException(
          "Unable to instantiate service context implementation '" +
          serviceContextClassName + "', error has occured in " +
          "static initializer block (" + cause.getClass().getSimpleName() +
          " : " + causeMsg + ")", error
      );
    }

    catch (IllegalAccessException exception)
    {
      throw new InitializationException(
          "Cannot instantiate service context class '" + serviceContextClassName +
          "', can't access a public constructor: " + exception.getMessage(), exception
      );
    }

    catch (InstantiationException exception)
    {
      throw new InitializationException(
          "Cannot instantiate service context class '" + serviceContextClassName +
          "': " + exception.getMessage(), exception
      );
    }

    catch (ClassNotFoundException exception)
    {
      throw new InitializationException(
          "The configured service context class '" + serviceContextClassName +
          "' was not found.", exception
      );
    }

  }


  /**
   * Configure all logging categories under {@link Constants#CONTROLLER_ROOT_LOG_CATEGORY} to
   * redirect from java.util.logging to log4j logging.
   */
  public static void redirectJULtoLog4j()
  {
    try
    {
      final Logger controllerRootLogger = Logger.getLogger(Constants.CONTROLLER_ROOT_LOG_CATEGORY);

      // ---- BEGIN PRIVILEGED CODE BLOCK ---------------------------------------------------------

      AccessController.doPrivilegedWithCombiner(new PrivilegedAction<Void>()
      {
        public Void run()
        {
          controllerRootLogger.addHandler(new Log4jRedirect());
          controllerRootLogger.setLevel(Level.ALL);

          return null;
        }
      });

      // ---- END PRIVILEGED CODE BLOCK -----------------------------------------------------------

      controllerRootLogger.info("Initialized JUL to LOG4J Redirector.");
    }

    catch (SecurityException exception)
    {
      LogManager.getLogManager().getLogger("").warning(
          "Can't install Log4j redirect handler due to security restrictions: " +
          exception.getMessage()
      );
    }
  }





  // Nested Classes -------------------------------------------------------------------------------


  /**
   * Java util logging handler implementation to map JUL log records to log4j API and send
   * log messages to log4j. <p>
   */
  private final static class Log4jRedirect extends Handler
  {

    /**
     * Translates and sends JUL log records to log4j logging.
     *
     * @param logRecord   java.util.logging log record
     */
    @Override public void publish(LogRecord logRecord)
    {

      // extract the level, message, log category and exception from log record...

      Level level = logRecord.getLevel();
      String msg  = logRecord.getMessage();
      String category = logRecord.getLoggerName();
      Throwable thrown = logRecord.getThrown();
      Object[] params = logRecord.getParameters();

      // translate to log4j log category (1 to 1 name mapping)...

      org.apache.log4j.Logger log4j;

      try
      {
        log4j = org.apache.log4j.Logger.getLogger(category);
      }

      catch (NullPointerException e)
      {
        // Seeing an occasional NPE from log4j call during shutdown. These may be caused by
        // undefined or incorrect shutdown hook execution order and/or thread shutdown ordering
        // issues -- catching here and attempt to print on the output console (for information
        // purposes)...

        printWhatWeCan(level, category, msg, params, thrown);

        return;
      }

      // mapping from JUL level to log4j levels...

      org.apache.log4j.Level log4jLevel = mapToLog4jLevel(level);

      // and log...

      if (log4j.isEnabledFor(log4jLevel))
      {
        if (params != null)
        {
          try
          {
            msg = MessageFormat.format(msg, params);
          }
          catch (IllegalArgumentException e)
          {
            msg = msg + "  [LOG MESSAGE PARAMETERIZATION ERROR: " +
                  e.getMessage().toUpperCase() + "]";
          }
        }

        if (thrown != null)
        {
          log4j.log(log4jLevel, msg, thrown);
        }

        else
        {
          log4j.log(log4jLevel, msg);
        }
      }

    }

    /**
     * Does nothing -- Log4j API does not provide explicit flushing to appenders.
     */
    @Override public void flush()
    {
      // no-op
    }

    /**
     * Does nothing -- Log4j API does not provide an explicit close operation for appenders.
     */
    @Override public void close()
    {
      // no-op
    }



    private static boolean systemWarningOnUnmappedLevel = false;

    private org.apache.log4j.Level mapToLog4jLevel(Level level)
    {
      if (level == Level.OFF)
      {
        return org.apache.log4j.Level.OFF;
      }

      else if (level == Level.SEVERE)
      {
        return org.apache.log4j.Level.ERROR;
      }

      else if (level == Level.WARNING)
      {
        return org.apache.log4j.Level.WARN;
      }

      else if (level == Level.INFO)
      {
        return org.apache.log4j.Level.INFO;
      }

      else if (level == Level.FINE)
      {
        return org.apache.log4j.Level.DEBUG;
      }

      else if (level == Level.FINER || level == Level.FINEST)
      {
        return org.apache.log4j.Level.TRACE;
      }

      else if (level == Level.ALL)
      {
        return org.apache.log4j.Level.ALL;
      }

      else
      {
        if (!systemWarningOnUnmappedLevel)
        {
          System.err.println(
              "\n\n" +
              "-----------------------------------------------------------------------------" +
              "\n\n" +

              "  System is using custom log level (" + level.getName() + ") which has no \n" +
              "  defined mapping. Defaulting to INFO level." +

              "\n\n" +
              "-----------------------------------------------------------------------------" +
              "\n\n"
          );

          systemWarningOnUnmappedLevel = true;
        }

        return org.apache.log4j.Level.INFO;
      }
    }

    private void printWhatWeCan(Level level, String category, String message, Object[] params,
                                Throwable thrown)
    {
       StringBuffer[] paramStrings = null;
       
       if (params != null) {
          paramStrings = new StringBuffer[params.length];

         int index = 0;
   
         for (Object param : params)
         {
           try
           {
             paramStrings[index] = new StringBuffer().append(param);
           }
   
           catch (NullPointerException e)
           {
             paramStrings[index] = new StringBuffer().append("<null>");
           }
   
           finally
           {
             index++;
           }
         }
       }
   
      System.out.println(
          "Unhandled Log Messages (may occur due to undefined or incorrect shutdown order):\n" +
          "--------------------------------------------------------------------------------\n" +
          level + " [" + category + "]: " + MessageFormat.format(message, (Object[])paramStrings)
      );

      if (thrown != null)
      {
        thrown.printStackTrace();
      }
    }
  }
}

