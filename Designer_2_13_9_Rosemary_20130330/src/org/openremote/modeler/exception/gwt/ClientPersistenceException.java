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
package org.openremote.modeler.exception.gwt;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is a GWT client-side equivalent of
 * {@link org.openremote.modeler.exception.PersistenceException}. <p>
 *
 * The inheritance hierarchy for this exception type is broken due to it being propagated back
 * to the GWT client (Javascript) and the generic OpenRemote exception class' import dependency
 * to java.text package for exception message formatting which is not supported by the GWT
 * simulated JRE environment as of version 2.4, see
 * https://developers.google.com/web-toolkit/doc/latest/RefJreEmulation <p>
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ClientPersistenceException extends RuntimeException implements IsSerializable
{
  /*
   *  TODO : Implementation Notes
   *
   *    - As per class-level Javadoc, this exception type exists purely due to lack of
   *      "simulated" JRE environment on GWT (Javascript) client-side for java.text package.
   *      This is currently used for convenience on message formatting on the base
   *      OpenRemoteException type.
   *
   *      The message formatting on the original superclass could be replaced with an alternative
   *      implementation but leaving it as-is for now, perhaps the java.text package support will
   *      emerge for GWT simulated JRE environment eventually.
   *
   *      Possible alternative implementations can be found here:
   *      http://stackoverflow.com/questions/3126232/string-formatter-in-gwt
   *
   *    - This exception type is left as unchecked exception type for now to cause least amount
   *      of API modifications where used, but should be considered as a checked exception later on.
   */


  // Constructors ---------------------------------------------------------------------------------

  /**
   * This constructor exists to satisfy GWT serialization requirements.
   */
  private ClientPersistenceException() {}

  
  /**
   * Constructs a new exception with a given message.
   *
   * @param msg  human-readable error message
   */
  public ClientPersistenceException(String msg)
  {
    super(msg);
  }


  /**
   * Constructs a new exception with a given message and root cause.
   *
   * @param msg     human-readable error message
   * @param cause   root exception cause
   */
  public ClientPersistenceException(String msg, Throwable cause)
  {
    super(msg, cause);
  }

}

