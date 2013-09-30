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
package org.openremote.controller.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.openremote.controller.Constants;

/**
 * This servlet implements the REST API '/rest/logout' functionality which invalidates the
 * user's session.
 * 
 * See <a href = "http://www.openremote.org/display/docs/Controller+2.0+HTTP-REST-XML">
 * Controller 2.0 REST XML API<a> and
 * <a href = "http://openremote.org/display/docs/Controller+2.0+HTTP-REST-JSONP">Controller 2.0
 * REST JSONP API</a> for more details.
  *
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
@SuppressWarnings("serial")
public class LogoutServlet extends RESTAPI
{

  /*
   *  IMPLEMENTATION NOTES:
   *    - TODO: Need to specify which REST API this will be included in.
   *                                                                                      [RAT]
   *    
   *    - This adheres to the current 2.0 version of the HTTP/REST/XML and HTTP/REST/JSON APIs.
   *      There's currently no packaging or REST URL distinction for supported API versions.
   *      Later versions of the Controller may support multiple revisions of the API depending
   *      on client request. Appropriate implementation changes should be made then.
   *                                                                                      [JPL]
   */


  // Class Members --------------------------------------------------------------------------------

  /**
   * Log category for HTTP REST API Logout requests.
   */
  private final static Logger logger = Logger.getLogger(Constants.REST_LOGOUT_CATEGORY);

  // Implement REST API ---------------------------------------------------------------------------

  @Override protected void handleRequest(HttpServletRequest request, HttpServletResponse response)
  {
    try
    {
       // Destroy the session and return 401 page
       request.getSession().invalidate();
       response.sendError(401);
    }

    catch (IOException e)
    {
       logger.error("Failed to logout: " + e.getMessage());
       sendResponse(request, response, 999, e.getMessage());
    }      
  }
}
