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
package org.openremote.controller.exception;

/**
 * Exception class to indicate errors during transformation of XML command elements into
 * Java domain classes.  <p>
 *
 * This exception instance uses {@link ControlCommandException#NO_SUCH_COMMAND} as its error code.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Dan 2009-5-23
 */
@SuppressWarnings("serial")
public class NoSuchCommandException extends ControlCommandException 
{

  // Constructors ---------------------------------------------------------------------------------

  /**
   * Instantiates a new exception with no message or root cause.
   */
  public NoSuchCommandException()
  {
    super();
    setErrorCode(ControlCommandException.NO_SUCH_COMMAND);
  }

  /**
   * Instantiates a new exception instance with a given message and root cause.
   *
   * @param message descriptive exception message
   * @param cause an exception that was the root cause of this error
   */
  public NoSuchCommandException(String message, Throwable cause)
  {
    super(message, cause);
    setErrorCode(ControlCommandException.NO_SUCH_COMMAND);
  }

  /**
   * Instantiates a new exception instance with a given message.
   *
   * @param message descriptive exception message
   */
  public NoSuchCommandException(String message)
  {
    super(message);
    setErrorCode(ControlCommandException.NO_SUCH_COMMAND);
  }


}
