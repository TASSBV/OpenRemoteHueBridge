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
 * A generic KNX API connection exception which indicates errors in the underlying KNX IP
 * connection -- connection errors, acknowledgement errors, connection closed, etc.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ConnectionException extends Exception
{

  // TODO : prevent exception type leakage
  //
  // By allowing the Throwable type as root cause opens the door to type leakage where the
  // underlying exception type may be from the library implementing the KNX IP wire protocol.
  //
  // This could be prevented by not allowing the root cause to be set to impl. specific type
  // in the ORC KNX layers, or enforcing such convention (currently not being enforced).
  // Downside is the extra work to transfer the exception information or loss of that information.
  //
  // Alternatively this exception implementation could replace specific library implementation
  // exception types with generic exception types allowing less stringent convention on the
  // API use. Nevertheless the original exception information should be transferred from the
  // underlying types.
  //
  // Since all approaches are extra work, this item is left as a "to-do" for now.
  
  public ConnectionException()
  {
    super();
  }

  public ConnectionException(String message)
  {
    super(message);
  }

  public ConnectionException(String message, Throwable rootCause)
  {
    super(message, rootCause);
  }

  public ConnectionException(Throwable rootCause)
  {
    super(rootCause);
  }
  
}