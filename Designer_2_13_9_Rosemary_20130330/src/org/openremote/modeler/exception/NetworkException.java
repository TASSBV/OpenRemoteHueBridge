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
package org.openremote.modeler.exception;

/**
 * A generic exception type to indicate network I/O errors. In general this is used to indicate
 * network related issues when communicating with other services. Network issues may be
 * intermittent and at times recoverable with retry attempts. To this effect this exception
 * type supports a {@link NetworkException.Severity severity} level that can be set to indicate
 * the likelyhood that the error is recoverable (and the operation may be worth retrying).
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class NetworkException extends OpenRemoteException
{

  // Enums ----------------------------------------------------------------------------------------

  /**
   * Defines the severity of the network error. This can be used to determine how likely it is
   * that the exception can be recovered from and how aggressively the operation causing the
   * exception should be retried.
   */
  public enum Severity
  {
    // Implementation note:
    //
    //   - Only adding severity levels that are currently in use in the codebase. As need arises,
    //     additional levels can be added. Obvious candidates would be TRIVIAL (usually
    //     recoverable, operation can be re-attempted quite aggressively), and FATAL (no chance
    //     of recovery, don't even bother.

    /**
     * Medium severity network error. Has a good chance of being recoverable with a somewhat
     * lazy re-attempts of the operation that caused it.
     */
    MEDIUM,

    /**
     * Severe network error. May be recoverable under some circumstances but don't keep your
     * hopes up. May be worth retrying for a while but be prepared to bail out.
     */
    SEVERE
  }


  // Instance Fields ------------------------------------------------------------------------------


  /**
   * Network error severity level.
   */
  private Severity severity = Severity.MEDIUM;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new exception with a given message and {@link NetworkException.Severity#MEDIUM}
   * severity.
   *
   * @param msg  human-readable error message
   */
  public NetworkException(String msg)
  {
    super(msg);
  }


  /**
   * Constructs a new exception with a given message and severity level.
   *
   * @param severity    exception severity level, see {@link NetworkException.Severity}
   * @param msg         human-readable error message
   */
  public NetworkException(Severity severity, String msg)
  {
    this(msg);

    this.severity = severity;
  }


  /**
   * Constructs a new exception with a parameterized message and
   * {@link NetworkException.Severity#MEDIUM} severity level.
   *
   * @param msg     human-readable error message
   * @param params  exception message parameters -- message parameterization must be
   *                compatible with {@link java.text.MessageFormat} API
   *
   * @see java.text.MessageFormat
   */
  public NetworkException(String msg, Object... params)
  {
    super(format(msg, params));
  }


  /**
   * Constructs a new exception with a parameterized message and a given severity level.
   *
   * @param severity  exception severity level, see {@link NetworkException.Severity}
   * @param msg       human-readable error message
   * @param params    exception message parameters -- message parameterization must be
   *                  compatible with {@link java.text.MessageFormat} API
   *
   * @see java.text.MessageFormat
   */
  public NetworkException(Severity severity, String msg, Object... params)
  {
    this(msg, params);

    this.severity = severity;
  }


  /**
   * Constructs a new exception with a given message, root cause and a
   * {@link NetworkException.Severity#MEDIUM} severity level.
   *
   * @param msg     human-readable error message
   * @param cause   root exception cause
   */
  public NetworkException(String msg, Throwable cause)
  {
    super(msg, cause);
  }


  /**
   * Constructs a new exception with a parameterized message, root cause and a
   * {@link NetworkException.Severity#MEDIUM} severity level.
   *
   * @param msg     human-readable error message
   * @param cause   root exception cause
   * @param params  exception message parameters -- message parameterization must be
   *                compatible with {@link java.text.MessageFormat} API
   *
   * @see java.text.MessageFormat
   */
  public NetworkException(String msg, Throwable cause, Object... params)
  {
    super(format(msg, params), cause);
  }


  /**
   * Constructs a new exception with a given severity, message, and root cause.
   *
   * @param severity  exception severity level, see {@link NetworkException.Severity}
   * @param msg       human-readable error message
   * @param cause     root exception cause
   */
  public NetworkException(Severity severity, String msg, Throwable cause)
  {
    this(msg, cause);

    this.severity = severity;
  }


  /**
   * Constructs a new exception with a given severity, a parameterized message, and root cause.
   *
   * @param severity  exception severity level, see {@link NetworkException.Severity}
   * @param msg       human-readable error message
   * @param cause     root exception cause
   * @param params    exception message parameters -- message parameterization must be
   *                  compatible with {@link java.text.MessageFormat} API
   *
   * @see java.text.MessageFormat
   */
  public NetworkException(Severity severity, String msg, Throwable cause, Object... params)
  {
    this(msg, cause, params);

    this.severity = severity;
  }


  // Instance Methods -----------------------------------------------------------------------------

  /**
   * Returns the severity indicated by this exception. See {@link NetworkException.Severity}
   * for level descriptions.
   *
   * @return  network exception severity level
   */
  public Severity getSeverity()
  {
    return severity;
  }

}

