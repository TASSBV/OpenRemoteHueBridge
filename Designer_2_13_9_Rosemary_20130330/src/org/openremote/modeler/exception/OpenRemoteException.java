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

import java.text.MessageFormat;

/**
 * A common (checked) exception super-class for shared functionality.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class OpenRemoteException extends Exception
{

  // TODO Tasks
  //
  //  - This is a copy/paste job from controller code base. Should come from a common
  //    set of shared classes between controller, designer, beehive.
  //
  //    See MODELER-274

  
  // Class Members --------------------------------------------------------------------------------

  /**
   * Handles the message formatting (and possible errors originating from it).
   *
   * @param msg     exception message to format
   * @param params  exception message parameters -- message parameterization must be
   *                compatible with {@link java.text.MessageFormat} API
   *
   * @see java.text.MessageFormat
   *
   * @return  message formatted with given parameters
   */
  protected static String format(String msg, Object... params)
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


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new exception with a given message.
   *
   * @param msg  human-readable error message
   */
  public OpenRemoteException(String msg)
  {
    super(msg);
  }

  /**
   * Constructs a new exception with a parameterized message.
   *
   * @param msg     human-readable error message
   * @param params  exception message parameters -- message parameterization must be
   *                compatible with {@link java.text.MessageFormat} API
   *
   * @see java.text.MessageFormat
   */
  public OpenRemoteException(String msg, Object... params)
  {
    super(format(msg, params));
  }

  /**
   * Constructs a new exception with a given message and root cause.
   *
   * @param msg     human-readable error message
   * @param cause   root exception cause
   */
  public OpenRemoteException(String msg, Throwable cause)
  {
    super(msg, cause);
  }

  /**
   * Constructs a new exception with a parameterized message and root cause.
   *
   * @param msg     human-readable error message
   * @param cause   root exception cause
   * @param params  exception message parameters -- message parameterization must be
   *                compatible with {@link java.text.MessageFormat} API
   *
   * @see java.text.MessageFormat
   */
  public OpenRemoteException(String msg, Throwable cause, Object... params)
  {
    super(format(msg, params), cause);
  }

}

