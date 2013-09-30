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
package org.openremote.modeler.logging;

import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.MDC;

/**
 * This is a log facade for log4j with additional convenience methods such as
 * message parameterization and helpers for thread local contexts.  <p>
 *
 * Designer classes should use this API for maximum portability. <p>
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class LogFacade
{

  // TODO Tasks:
  //
  //   - context loggers
  //   - implementation is currently based on log4j but should be modified to support GWT
  //     client side logging


  // Enums ----------------------------------------------------------------------------------------

  /**
   * Typesafe log categories. These mainly exists to aid tooling implementations to map
   * string based canonical log category names to something more understandable to system
   * operators (while enabling type safe log constants instead of strings in code).
   */
  public enum Category implements Hierarchy
  {
    /**
     * Root log category. All logging categories must be subcategories of this root category. <p>
     *
     * The canonical log category name is defined in {@link LogFacade#ROOT_LOG_CATEGORY}.
     */
    ROOT(ROOT_LOG_CATEGORY, "Designer Root Log Category"),

    /**
     * User account related logging. New account creation, removal, user logins/logouts, etc. <p>
     *
     * The canonical log category name is defined in {@link LogFacade#USER_LOG_CATEGORY}.
     */
    USER(USER_LOG_CATEGORY, "User Log Category"),


    // Object Model Related Log Categories --------------------------------------------------------

    // TODO : currently unused
    DOMAIN_MODEL(DOMAIN_MODEL_LOG_CATEGORY, "Object Model Log Category"),

    PANEL_MODEL(PANEL_MODEL_LOG_CATEGORY, "Panel Model Log Category"),



    // Resource Cache Related Log Categories ------------------------------------------------------

    /**
     * Cache runtime operation logging. <p>
     *
     * The canonical log category name is defined in {@link LogFacade#RESOURCE_CACHE_LOG_CATEGORY}.
     */
    CACHE(RESOURCE_CACHE_LOG_CATEGORY, "Resource Cache Log Category"),



    // Designer State (Save/Restore) Related Log Categories ---------------------------------------

    /**
     * Generic log category for designer state modifications (save/restore). <p>
     *
     * The canonical log category name is defined in {@link LogFacade#STATE_LOG_CATEGORY}.
     */
    STATE(STATE_LOG_CATEGORY, "Designer State Log Category"),

    /**
     * Specific Designer 'save' operation subcategory of {@link #STATE}. Resource save related
     * logging only. <p>
     *
     * The canonical log category name is defined in {@link LogFacade#STATE_SAVE_LOG_CATEGORY}.
     */
    STATE_SAVE(STATE_SAVE_LOG_CATEGORY, "Designer Save Log Category"),

    /**
     * Specific Designer 'restore' operation subcategory of {@link #STATE}. Resource restore
     * related logging only. <p>
     *
     * The canonical log category name is defined in {@link LogFacade#RECOVERY_LOG_CATEGORY}.
     */
    STATE_RECOVERY(RECOVERY_LOG_CATEGORY, "Designer State Recovery Log"),



    // Beehive REST API Related Log Categories ----------------------------------------------------

    /**
     * Generic log category for all Beehive Client REST API related logging. <p>
     *
     * The canonical log category name is defined in {@link LogFacade#BEEHIVE_SERVICE_LOG_CATEGORY}.
     */
    BEEHIVE(BEEHIVE_SERVICE_LOG_CATEGORY, "Beehive Client Service Log Category"),

    // TODO
    BEEHIVE_NETWORK_PERFORMANCE(BEEHIVE_NETWORK_PERF_LOG_CATEGORY, "Beehive Network Performance"),

    // TODO
    BEEHIVE_DOWNLOAD_PERFORMANCE(BEEHIVE_DOWNLOAD_PERF_LOG_CATEGORY, "Beehive Download Performace"),



    // Service Facade Implementation Log Categories -----------------------------------------------

    /**
     * Generic log category for all Designer server-side service implementations
     * (org.openremote.modeler.service.* package). <p>
     *
     * Specific service implementations should use this category as their parent logging category.
     * <p>
     * The canonical log category name is defined in {@link LogFacade#SERVICE_LOG_CATEGORY}.
     */
    SERVICE(SERVICE_LOG_CATEGORY, "Designer Server-Side Service Implementations Log Category"),

    /**
     * Log category for designer resource service
     * ({@link org.openremote.modeler.service.ResourceService). <p>
     *
     * This is a child category of {@link #SERVICE}.
     *
     * The canonical log category name is defined {@link LogFacade#RESOURCE_SERVICE_LOG_CATEGORY}.
     */
    RESOURCE_SERVICE(RESOURCE_SERVICE_LOG_CATEGORY, "Designer Resource Service Log Category"),


    // Persistence and Database Access Log Categories ---------------------------------------------


    /**
     * Log category for designer database access. <p>
     *
     * The canonical log category name is defined {@link LogFacade#PERSISTENCE_LOG_CATEGORY}.
     */
    PERSISTENCE(PERSISTENCE_LOG_CATEGORY, "Designer Database Access Log Category"),


    // TODO
    EXPORT(EXPORT_LOG_CATEGORY, "Resource Export Log Category"),

    ;


    // Instance Fields ----------------------------------------------------------------------------

    /**
     * Stores canonical log hierarchy name with a string dot-notation as defined for Java util
     * logging (JUL) and Log4j frameworks.
     */
    private String canonicalLogCategoryName;


    // Constructors -------------------------------------------------------------------------------

    private Category(String canonicalLogCategoryName, String displayName)
    {
      this.canonicalLogCategoryName = canonicalLogCategoryName;
    }


    // Implements Type ----------------------------------------------------------------------------

    @Override public String getCanonicalLogCategoryName()
    {
      return canonicalLogCategoryName;
    }
  }




  // Constants ------------------------------------------------------------------------------------


  /**
   * Canonical log hierarchy name for Designer. All other log categories must be sub-categories
   * of this root name.
   *
   * @see LogFacade.Category#ROOT
   */
  public final static String ROOT_LOG_CATEGORY = "OpenRemote.Designer";


  /**
   * Canonical log hierarchy for user related logging. New user account creation, login/logout, etc.
   * <p>
   *
   * @see LogFacade.Category#USER
   */
  public final static String USER_LOG_CATEGORY = ROOT_LOG_CATEGORY + ".User";

  
  /**
   * Canonical log hierarchy name for all object model related logging. Specific domain object
   * logging should be subcategories of this root hierarchy.
   *
   * @see LogFacade.Category#DOMAIN_MODEL
   */
  public final static String DOMAIN_MODEL_LOG_CATEGORY = ROOT_LOG_CATEGORY + ".Model";

  /**
   * Canonical log hierarchy name for for panel domain objects.  <p>
   *
   * This is a child category of {@link #DOMAIN_MODEL_LOG_CATEGORY}.
   *
   * @see LogFacade.Category#DOMAIN_MODEL
   * @see LogFacade.Category#PANEL_MODEL
   */
  public final static String PANEL_MODEL_LOG_CATEGORY = DOMAIN_MODEL_LOG_CATEGORY + ".Panel";


  /**
   * Canonical log hierarchy name for resource cache implementations.
   *
   * @see LogFacade.Category#CACHE
   * @see org.openremote.modeler.cache.ResourceCache
   */
  public final static String RESOURCE_CACHE_LOG_CATEGORY = ROOT_LOG_CATEGORY + ".Cache";


  /**
   * Canonical log hierarchy name for designer UI state management (save/restore) service.
   * This is the generic root hierarchy with operation-specific sub-categories defined in
   * {@link LogFacade.Category#STATE_RECOVERY} and {@link LogFacade.Category#STATE_SAVE}.
   *
   * @see LogFacade.Category#STATE
   * @see LogFacade.Category#STATE_SAVE
   * @see LogFacade.Category#STATE_RECOVERY
   */
  public final static String STATE_LOG_CATEGORY = ROOT_LOG_CATEGORY + ".State";

  /**
   * Canonical log hierarchy name for designer state save operations.  <p>
   *
   * This is a child of {@link LogFacade.Category#STATE}.
   *
   * @see LogFacade.Category#STATE_SAVE
   */
  public final static String STATE_SAVE_LOG_CATEGORY = STATE_LOG_CATEGORY + ".Save";

  
  /**
   * Canonical log hierarchy name for designer state restore operations. <p>
   *
   * This is a child category of {@link LogFacade.Category#STATE}
   *
   * @see LogFacade.Category#STATE_RECOVERY
   */
  public final static String RECOVERY_LOG_CATEGORY = STATE_LOG_CATEGORY + ".Restore";



  /**
   * Canonical log hierarchy name for Beehive REST client API service.
   *
   * @see LogFacade.Category#BEEHIVE
   */
  public final static String BEEHIVE_SERVICE_LOG_CATEGORY = ROOT_LOG_CATEGORY + ".BeehiveService";


  /**
   * Specialized subcategory of {@link LogFacade.Category#BEEHIVE} for recording network
   * performance statistics between this Beehive client and the Beehive server.
   *
   * @see LogFacade.Category#BEEHIVE_NETWORK_PERFORMANCE
   */
  public final static String BEEHIVE_NETWORK_PERF_LOG_CATEGORY =
      BEEHIVE_SERVICE_LOG_CATEGORY + ".Performance.Network";

  /**
   * Specialized subcategory of {@link #BEEHIVE_NETWORK_PERF_LOG_CATEGORY} for recording
   * only the download performance between this Beehive client and the Beehive server
   *
   * @see LogFacade.Category#BEEHIVE_DOWNLOAD_PERFORMANCE
   */
  public final static String BEEHIVE_DOWNLOAD_PERF_LOG_CATEGORY =
      BEEHIVE_NETWORK_PERF_LOG_CATEGORY + ".Download";


  /**
   * Canonical log hierarchy name for generic service logging category
   * (org.openremote.modeler.service.* implementations). Specific service implementations should
   * use this category as their parent log category.
   *
   * @see LogFacade.Category#SERVICE
   */
  public final static String SERVICE_LOG_CATEGORY = ROOT_LOG_CATEGORY + ".Service";

  /**
   * Canonical log hierarchy name for resource service implementation in
   * {@link org.openremote.modeler.service.impl.ResourceServiceImpl}. <p>
   *
   * This is a child category of {@link LogFacade.Category#SERVICE}.
   *
   * @see LogFacade.Category#RESOURCE_SERVICE
   */
  public final static String RESOURCE_SERVICE_LOG_CATEGORY =
      SERVICE_LOG_CATEGORY + ".ResourceService";


  /**
   * Canonical log hierarchy name for persistence and database access logging categories.
   * More specific persistence logging categories should use this category as their parent
   * log category.
   *
   * @see LogFacade.Category#PERSISTENCE
   */
  public final static String PERSISTENCE_LOG_CATEGORY = ROOT_LOG_CATEGORY + ".Persistence";


  // TODO
  public final static String EXPORT_LOG_CATEGORY = ROOT_LOG_CATEGORY + ".Export";


  /**
   * A string identifier used for storing username as a thread local logging context.
   *
   * @see #addUserName(String)
   */
  public final static String USER_THREADCONTEXT = "User";

  /**
   * A string identifier used for storing account ID as a thread local logging context.
   *
   * @see #addAccountID(Long)
   */
  public final static String ACCOUNTID_THREADCONTEXT = "Account";



  // Class Members --------------------------------------------------------------------------------


  /**
   * Returns a new log facade instance which delegates to log4j implementation.  <p>
   *
   * @see LogFacade.Category
   * @see AdministratorAlert.Type
   *
   * @param   hierarchy  log hierarchy that provides a canonical log category name
   *
   * @return  Log facade with additional convenience API for logging.
   */
  public static LogFacade getInstance(Hierarchy hierarchy)
  {
    Logger logger = Logger.getLogger(hierarchy.getCanonicalLogCategoryName());

    return new LogFacade(logger);
  }

  /**
   * Add user name to this thread's log context.
   *
   * @param username  username string
   */
  public static void addUserName(String username)
  {
    MDC.put(USER_THREADCONTEXT, username);
  }

  /**
   * Removes username from this thread's log context.
   */
  public static void removeUserName()
  {
    MDC.remove(USER_THREADCONTEXT);
  }

  /**
   * Add account ID to this thread's log context.
   *
   * @param id    account ID, see {@link org.openremote.modeler.domain.Account#getOid}
   */
  public static void addAccountID(Long id)
  {
    MDC.put(ACCOUNTID_THREADCONTEXT, id);
  }

  /**
   * Removes account ID from this threa's log context.
   */
  public static void removeAccountID()
  {
    MDC.remove(ACCOUNTID_THREADCONTEXT);
  }


  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Log4j delegate.
   */
  private Logger logger;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Private constructor accessed from {@link #getInstance}.
   *
   * @param logger    log4j delegate
   */
  private LogFacade(Logger logger)
  {
    this.logger = logger;
  }


  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * This is synonymous to using log4j error().
   *
   * @param msg   log message
   */
  public void error(String msg)
  {
    if (logger.isEnabledFor(Level.ERROR))
    {
      logger.log(Level.ERROR, msg);
    }
  }

  /**
   * Same as {@link #error} but allows parameterized log messages.
   *
   * @param msg     log message
   * @param params  log message parameters -- message parameterization must be compatible with
   *                {@link java.text.MessageFormat} API
   *
   * @see java.text.MessageFormat
   */
  public void error(String msg, Object... params)
  {
    // check level is enabled first so we don't waste time formatting for nothing...

    if (logger.isEnabledFor(Level.ERROR))
    {
      error(format(msg, params));
    }
  }

  /**
   * Same as {@link #error} with an additional exception stack trace added to the logging record.
   *
   * @param msg         log message
   * @param throwable   exception or error associated with the log message
   */
  public void error(String msg, Throwable throwable)
  {
    if (logger.isEnabledFor(Level.ERROR))
    {
      logger.log(Level.ERROR, msg, throwable);
    }
  }

  /**
   * Same as {@link #error} with an additional exception stack trace and message parameterization
   * added to the logging record.
   *
   * @param msg         log message
   * @param throwable   exception or error associated with the log message
   * @param params      log message parameters -- message parameterization must be compatible with
   *                    {@link java.text.MessageFormat} API
   *
   * @see java.text.MessageFormat
   */
  public void error(String msg, Throwable throwable, Object... params)
  {
    // check level is enabled first so we don't waste time formatting for nothing...

    if (logger.isEnabledFor(Level.ERROR))
    {
      error(format(msg, params), throwable);
    }
  }


  // Warning Logging ------------------------------------------------------------------------------

  /**
   * Synonymous to using log4j warn().
   *
   * @param msg   log message
   */
  public void warn(String msg)
  {
    if (logger.isEnabledFor(Level.WARN))
    {
      logger.log(Level.WARN, msg);
    }
  }


  /**
   * Same as {@link #warn} but allows parameterized log messages.
   *
   * @param msg     log message
   * @param params  log message parameters -- message parameterization must be compatible with
   *                {@link java.text.MessageFormat} API
   *
   * @see java.text.MessageFormat
   */
  public void warn(String msg, Object... params)
  {
    // check level is enabled first so we don't waste time formatting for nothing...

    if (logger.isEnabledFor(Level.WARN))
    {
      warn(format(msg, params));
    }
  }


  /**
   * Same as {@link #warn} with an additional exception stack trace added to the logging record.
   *
   * @param msg         log message
   * @param throwable   exception or error associated with the log message
   */
  public void warn(String msg, Throwable throwable)
  {
    if (logger.isEnabledFor(Level.WARN))
    {
      logger.log(Level.WARN, msg, throwable);
    }
  }


  /**
   * Same as {@link #warn} with an additional exception stack trace and message parameterization
   * added to the logging record.
   *
   * @param msg         log message
   * @param throwable   exception or error associated with the log message
   * @param params      log message parameters -- message parameterization must be compatible with
   *                    {@link java.text.MessageFormat} API
   *
   * @see java.text.MessageFormat
   */
  public void warn(String msg, Throwable throwable, Object... params)
  {
    // check level is enabled first so we don't waste time formatting for nothing...

    if (logger.isEnabledFor(Level.WARN))
    {
      warn(format(msg, params), throwable);
    }
  }


  // Info Logging ---------------------------------------------------------------------------------

  /**
   * Synonymous to using log4j info().
   *
   * @param msg   log message
   */
  public void info(String msg)
  {
    if (logger.isEnabledFor(Level.INFO))
    {
      logger.log(Level.INFO, msg);
    }
  }

  /**
   * Same as {@link #info} but allows parameterized log messages.
   *
   * @param msg     log message
   * @param params  log message parameters -- message parameterization must be compatible with
   *                {@link java.text.MessageFormat} API
   *
   * @see java.text.MessageFormat
   */
  public void info(String msg, Object... params)
  {
    // check level is enabled first so we don't waste time formatting for nothing...

    if (logger.isEnabledFor(Level.INFO))
    {
      info(format(msg, params));
    }
  }

  /**
   * Same as {@link #info} with an additional exception stack trace added to the logging record.
   *
   * @param msg         log message
   * @param throwable   exception or error associated with the log message
   */
  public void info(String msg, Throwable throwable)
  {
    if (logger.isEnabledFor(Level.INFO))
    {
      logger.log(Level.INFO, msg, throwable);
    }
  }

  /**
   * Same as {@link #info} with an additional exception stack trace and message parameterization
   * added to the logging record.
   *
   * @param msg         log message
   * @param throwable   exception or error associated with the log message
   * @param params      log message parameters -- message parameterization must be compatible with
   *                    {@link java.text.MessageFormat} API
   *
   * @see java.text.MessageFormat
   */
  public void info(String msg, Throwable throwable, Object... params)
  {
    // check level is enabled first so we don't waste time formatting for nothing...

    if (logger.isEnabledFor(Level.INFO))
    {
      info(format(msg, params), throwable);
    }
  }


  // Debug Logging --------------------------------------------------------------------------------

  /**
   * Synonymous to using log4j debug().
   *
   * @param msg   log message
   */
  public void debug(String msg)
  {
    if (logger.isEnabledFor(Level.DEBUG))
    {
      logger.debug(msg);
    }
  }

  /**
   * Same as {@link #debug} but allows parameterized log messages.
   *
   * @param msg     log message
   * @param params  log message parameters -- message parameterization must be compatible with
   *                {@link java.text.MessageFormat} API
   *
   * @see java.text.MessageFormat
   */
  public void debug(String msg, Object... params)
  {
    // check level is enabled first so we don't waste time formatting for nothing...

    if (logger.isEnabledFor(Level.DEBUG))
    {
      debug(format(msg, params));
    }
  }

  /**
   * Same as {@link #debug} with an additional exception stack trace added to the logging record.
   *
   * @param msg         log message
   * @param throwable   exception or error associated with the log message
   */
  public void debug(String msg, Throwable throwable)
  {
    if (logger.isEnabledFor(Level.DEBUG))
    {
      logger.log(Level.DEBUG, msg, throwable);
    }
  }

  /**
   * Same as {@link #debug} with an additional exception stack trace and message parameterization
   * added to the logging record.
   *
   * @param msg         log message
   * @param throwable   exception or error associated with the log message
   * @param params      log message parameters -- message parameterization must be compatible with
   *                    {@link java.text.MessageFormat} API
   *
   * @see java.text.MessageFormat
   */
  public void debug(String msg, Throwable throwable, Object... params)
  {
    // check level is enabled first so we don't waste time formatting for nothing...

    if (logger.isEnabledFor(Level.DEBUG))
    {
      debug(format(msg, params), throwable);
    }
  }


  // Trace Logging --------------------------------------------------------------------------------

  /**
   * Synonymous to using log4j trace()
   *
   * @param msg   log message
   */
  public void trace(String msg)
  {
    if (logger.isEnabledFor(Level.TRACE))
    {
      logger.log(Level.TRACE, msg);
    }
  }

  /**
   * Same as {@link #trace} but allows parameterized log messages.
   *
   * @param msg     log message
   * @param params  log message parameters -- message parameterization must be compatible with
   *                {@link java.text.MessageFormat} API
   *
   * @see java.text.MessageFormat
   */
  public void trace(String msg, Object... params)
  {
    // check level is enabled first so we don't waste time formatting for nothing...

    if (logger.isEnabledFor(Level.TRACE))
    {
      trace(format(msg, params));
    }
  }

  /**
   * Same as {@link #trace} with an additional exception stack trace added to the logging record.
   *
   * @param msg         log message
   * @param throwable   exception or error associated with the log message
   */
  public void trace(String msg, Throwable throwable)
  {
    if (logger.isEnabledFor(Level.TRACE))
    {
      logger.log(Level.TRACE, msg, throwable);
    }
  }

  /**
   * Same as {@link #trace} with an additional exception stack trace and message parameterization
   * added to the logging record.
   *
   * @param msg         log message
   * @param throwable   exception or error associated with the log message
   * @param params      log message parameters -- message parameterization must be compatible with
   *                    {@link java.text.MessageFormat} API
   *
   * @see java.text.MessageFormat
   */
  public void trace(String msg, Throwable throwable, Object... params)
  {
    // check level is enabled first so we don't waste time formatting for nothing...

    if (logger.isEnabledFor(Level.TRACE))
    {
      trace(format(msg, params), throwable);
    }
  }



  // Private Instance Methods ---------------------------------------------------------------------

  /**
   * Formats the message as per the {@link MessageFormat} API.
   *
   * @param msg     message to format
   * @param params  message parameters
   *
   * @return    formatted message
   */
  private String format(String msg, Object... params)
  {
    try
    {
      return MessageFormat.format(msg, params);
    }

    catch (Throwable t)
    {
      return msg + "  [EXCEPTION MESSAGE FORMATTING ERROR: " + t.getMessage().toUpperCase() + "]";
    }
  }



  // Nested Interfaces ----------------------------------------------------------------------------


  /**
   * Defines a hierarchy of log categories. An object must implement and return a canonical
   * dot-separated hierarchy string (the form used by Java Util Logging and Log4j frameworks).
   */
  public static interface Hierarchy
  {
    /**
     * Returns a canonical dot-separated hierarchy string.
     *
     * @return    log hierarchy as dot-separated string (see Java Util Logging and/or Log4j
     *            documentation for details).
     */
    String getCanonicalLogCategoryName();
  }
}

