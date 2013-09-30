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
package org.openremote.modeler.beehive;

import java.io.InputStream;

import org.openremote.modeler.domain.User;
import org.openremote.modeler.exception.NetworkException;
import org.openremote.modeler.exception.ConfigurationException;
import org.openremote.modeler.cache.ResourceCache;
import org.openremote.modeler.cache.CacheOperationException;

/**
 * This interface abstracts the <b>client</b> side of the Beehive REST API. <p>
 *
 * Interface is introduced to allow multiple implementations, both in order to
 * evolve with the Beehive API (if the interface methods can be sufficiently
 * supported with new API) and to allow for network service mocking for unit
 * tests.  <p>
 *
 * The goal is to migrate all HTTP related communication to Beehive service
 * through this interface implementation(s). The interface implementations can
 * therefore act as centralized points to outbound communication to Beehive.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public interface BeehiveService
{

  /**
   * Downloads an archive containing user account artifacts stored in Beehive.
   *
   * @see org.openremote.modeler.cache.ResourceCache#openWriteStream()
   *
   * @param   user      a reference to user whose account is accessed
   * @param   cache     reference to designer's local cache where the downloaded resources
   *                    will be stored
   *
   *
   * @throws NetworkException
   *                  If (possibly recoverable) network errors occured during the service
   *                  operation. Network errors may be recoverable in which case this operation
   *                  could be re-attempted. See {@link NetworkException.Severity} for an
   *                  indication of the network error type.
   *
   * @throws ConfigurationException
   *                  If designer configuration error prevents the service from executing
   *                  normally. Often a fatal error type that should be logged/notified to
   *                  admins, configuration corrected and application re-deployed.
   *
   * @throws BeehiveServiceException
   *                  If service implementation specific errors occur. This is a generic exception
   *                  type defined for the interface. Concrete implementations can (and often
   *                  should) implement more specific exception types to indicate issues specific
   *                  to their implementation details.
   *
   * @throws CacheOperationException
   *                  If any errors occur as part of the interaction with the resource cache.
   *
   */
  void downloadResources(User user, ResourceCache cache)
      throws NetworkException, BeehiveServiceException, ConfigurationException, CacheOperationException;


  /**
   * Uploads resources to user's account in Beehive.
   *
   * @param   input      Input stream used for reading the resource data. The concrete
   *                     implementations of this method must specify the requirements
   *                     for the data stream.
   *
   * @param currentUser  The user to authenticate in Beehive.
   *
   *
   * @throws ConfigurationException
   *                  If designer configuration error prevents the service from executing
   *                  normally. Often a fatal error type that should be logged/notified to
   *                  admins, configuration corrected and application re-deployed.
   *
   * @throws NetworkException
   *                  If (possibly recoverable) network errors occured during the service
   *                  operation. Network errors may be recoverable in which case this operation
   *                  could be re-attempted. See {@link NetworkException.Severity} for an
   *                  indication of the network error type.
   */
  void uploadResources(InputStream input, User currentUser)
      throws ConfigurationException, NetworkException;


  // Nested Classes -------------------------------------------------------------------------------


  /**
   * Exception type to indicate potential issues with the Beehive server being
   * connected to that manifest themselves through the client API (such as unexpected
   * or erroneous responses on the REST API).
   */
  public static class ServerException extends BeehiveServiceException
  {
    /**
     * Constructs a new exception with a given message
     *
     * @param msg   exception message
     */
    ServerException(String msg)
    {
      super(msg);
    }

    /**
     * Constructs a new exception with a parameterized message.
     *
     * @param msg       exception message
     * @param params    message parameters
     */
    ServerException(String msg, Object... params)
    {
      super(msg, params);
    }
  }

}
