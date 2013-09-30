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


/**
 * Specialized log facade for alerts that should be sent to system administrators. These should
 * indicate errors that require immediate attention (such as configuration and/or deployment
 * errors) and errors that may cause incorrect behavior of the Designer application.
 *
 * @see LogFacade
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class AdministratorAlert
{

  // Enums ----------------------------------------------------------------------------------------

  /**
   * Typesafe alert categories. These are specific log categories that normally generate
   * alerts rather than merely log record entries.
   */
  public enum Type implements LogFacade.Hierarchy
  {

    /**
     * Administrator alerts from designer state (save/restore) implementation.
     *
     * The canonical log category name is defined in
     * {@link AdministratorAlert#STATE_ADMIN_ALERT_LOG_CATEGORY}
     *
     * @see org.openremote.modeler.service.impl.DesignerState
     */
    DESIGNER_STATE(STATE_ADMIN_ALERT_LOG_CATEGORY, "Designer State Administrator Alerts"),

    /**
     * Administrator alerts from resource cache implementations.
     *
     * The canonical log category name is defined in
     * {@link AdministratorAlert#RESOURCE_CACHE_ADMIN_ALERT_LOG_CATEGORY}
     *
     * @see org.openremote.modeler.cache.ResourceCache
     */
    RESOURCE_CACHE(RESOURCE_CACHE_ADMIN_ALERT_LOG_CATEGORY, "Resource Cache Administrator Alerts"),

      /**
     * Administrator alerts raised by database operations (integrity issues, etc.).
     *
     * The canonical log category name is defined in
     * {@link AdministratorAlert#DATABASE_ADMIN_ALERT_LOG_CATEGORY}
     *
     */
    DATABASE(DATABASE_ADMIN_ALERT_LOG_CATEGORY, "Database Administrator Alerts"),

      ;



    // Instance Fields ----------------------------------------------------------------------------

    /**
     * Stores canonical log hierarchy name with a string dot-notation as defined for Java util
     * logging (JUL) and Log4j frameworks.
     */
    private String canonicalLogCategoryName;
    private String displayName;


    // Constructors -------------------------------------------------------------------------------

    private Type(String canonicalLogCategoryName, String displayName)
    {
      this.canonicalLogCategoryName = canonicalLogCategoryName;
      this.displayName = displayName;
    }


    // Implements LogFacade.Hierarchy -------------------------------------------------------------

    @Override public String getCanonicalLogCategoryName()
    {
      return canonicalLogCategoryName;
    }
  }



  // Constants ------------------------------------------------------------------------------------


  /**
   * Common log subcategory name for alerts.
   */
  public final static String ALERT_LOG_CATEGORY = ".AdminAlert";


  /**
   * Canonical log hierarchy name for alerts in Designer resource cache. <p>
   *
   * This is a child category of {@link LogFacade.Category#CACHE}.
   *
   * @see AdministratorAlert.Type#RESOURCE_CACHE
   */
  public final static String RESOURCE_CACHE_ADMIN_ALERT_LOG_CATEGORY =
      LogFacade.RESOURCE_CACHE_LOG_CATEGORY + ALERT_LOG_CATEGORY;

  /**
   * Canonical log hierarchy name for alerts in Designer state service (Save/Restore). <p>
   *
   * This is a child category of {@link LogFacade.Category#STATE}.
   *
   * @see AdministratorAlert.Type#DESIGNER_STATE
   */
  public final static String STATE_ADMIN_ALERT_LOG_CATEGORY =
      LogFacade.STATE_LOG_CATEGORY + ALERT_LOG_CATEGORY;


  /**
   * Canonical log hierarchy name for database integrity related alerts. <p>
   *
   * This is a child category of {@link LogFacade.Category#PERSISTENCE}.
   *
   * @see AdministratorAlert.Type#DATABASE
   */
  public final static String DATABASE_ADMIN_ALERT_LOG_CATEGORY =
          LogFacade.PERSISTENCE_LOG_CATEGORY + ALERT_LOG_CATEGORY;


    // Class Members --------------------------------------------------------------------------------


  /**
   * Returns a new alert facade instance. <p>
   *
   * This implementation delegates to {@link LogFacade}. The actual alert functionality should
   * be configured in the logging subsystem.
   *
   * @see AdministratorAlert.Type
   *
   * @param   type  alert type that provides a canonical log category name
   *
   * @return  Alert API.
   */
  public static AdministratorAlert getInstance(Type type)
  {
    return new AdministratorAlert(LogFacade.getInstance(type));
  }



  // Instance Fields ------------------------------------------------------------------------------

  /**
   * The logging API delegate.
   */
  private LogFacade logger;



  // Constructors ---------------------------------------------------------------------------------

  /**
   * Private constructor used by {@link #getInstance}.
   *
   * @param logger    the logging API delegate
   */
  private AdministratorAlert(LogFacade logger)
  {
    this.logger = logger;
  }



  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Creates a new alert with a given message. Delegates the alert as an {@link LogFacade#error}
   * message to the log subsystem. Actual alert delivery semantics should be configured in the
   * logging system.
   *
   * @param msg   alert message
   */
  public void alert(String msg)
  {
    logger.error(msg);
  }

  /**
   * Creates a new alert with a given message and message parameters. Delegates the alert as
   * an {@link LogFacade#error} message to the log subsystem. Actual alert delivery semantics
   * should be configured in the logging system.
   *
   * @param msg     alert message
   * @param params  message parameters
   */
  public void alert(String msg, Object... params)
  {
    logger.error(msg, params);
  }

  /**
   * Creates a new alert with a given message and exception instance. Delegates the alert as
   * an {@link LogFacade#error} message to the log subsystem. Actual alert delivery semantics
   * should be configured in the logging system.
   *
   * @param msg     alert message
   * @param t       exception
   */
  public void alert(String msg, Throwable t)
  {
    logger.error(msg, t);
  }

  /**
   * Creates a new alert with a given message, message parameters and exception instance.
   * Delegates the alert as an {@link LogFacade#error} message to the log subsystem. Actual
   * alert delivery semantics should be configured in the logging system.
   *
   * @param msg     alert message
   * @param t       exception
   * @param params  message parameters
   */
  public void alert(String msg, Throwable t, Object... params)
  {
    logger.error(msg, t, params);
  }

}

