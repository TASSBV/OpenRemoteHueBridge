/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.protocol.x10;

/**
 * A generic X10 API connection exception which indicates errors in the underlying X10 connection.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Jerome Velociter
 *
 * @see  org.openremote.controller.protocol.knx.ConnectionException
 */
public class ConnectionException extends Exception {

   // TODO : prevent exception type leakage
   // See org.openremote.controller.protocol.knx.ConnectionException

   // [JPL] : Type leakage is less of an issue here as long as the underlying X10 library
   //         is under LGPL or other OSS license that does not introduce additional constraints
   //         to linking (a la GPL)
   //
   //         Nevertheless, this class and the one in KNX package should eventually be brought
   //         together as a single class.

   public ConnectionException() {
      super();
   }

   public ConnectionException(String message) {
      super(message);
   }

   public ConnectionException(String message, Throwable rootCause) {
      super(message, rootCause);
   }

   public ConnectionException(Throwable rootCause) {
      super(rootCause);
   }

}