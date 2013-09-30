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
package org.openremote.controller.protocol.knx;

/**
 * Exception raised by incorrectly formatted KNX group address when attempting to convert
 * a string representation into an instance of {@link GroupAddress}
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
class InvalidGroupAddressException extends Exception
{

  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Keep the original invalid string format around, useful for error reporting and logging.
   */
  private String invalidAddress;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new exception instance with a given message and the string representation
   * of a KNX group address that could not be parsed.
   *
   * @param message           exception message
   * @param invalidAddress    original group address string that did not convert into
   *                          {@link GroupAddress} instance
   */
  InvalidGroupAddressException(String message, String invalidAddress)
  {
    super(message);

    this.invalidAddress = invalidAddress;
  }

  /**
   * Constructs a new exception instance with a given message, string representation of a KNX
   *  group address that could not be parsed and a root exception cause.
   *
   * @param message           exception message
   * @param invalidAddress    original group address string that did not convert into
   *                          {@link GroupAddress} instance
   * @param rootCause         root cause of this exception
   */
  InvalidGroupAddressException(String message, String invalidAddress, Exception rootCause)
  {
    super(message, rootCause);

    this.invalidAddress = invalidAddress;
  }


  // Instance Methods -----------------------------------------------------------------------------


  /**
   * Returns the string that was attempted to parse into a {@link GroupAddress} instance.
   *
   * @return  the original group address string that failed to parse correctly.
   */
  String invalidGroupAddress()
  {
    return invalidAddress;
  }
}
