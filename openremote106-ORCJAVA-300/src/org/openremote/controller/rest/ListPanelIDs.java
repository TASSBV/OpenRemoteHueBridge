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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openremote.controller.Constants;
import org.openremote.controller.exception.ControlCommandException;
import org.openremote.controller.service.ProfileService;
import org.openremote.controller.spring.SpringContext;

/**
 * This servlet implements the REST API '/rest/panels' functionality which returns either an
 * XML or JSON document with all the available panel definitions deployed to the controller. <p>
 *
 * Access to each individual panel definition will depend on further REST API calls and access
 * restrictions configured in the controller.
 *
 * See <a href = "http://www.openremote.org/display/docs/Controller+2.0+HTTP-REST-XML">
 * Controller 2.0 REST XML API<a> and
 * <a href = "http://openremote.org/display/docs/Controller+2.0+HTTP-REST-JSONP">Controller 2.0
 * REST JSONP API</a> for more details.
 *
 * @see FindPanelByID
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ListPanelIDs extends RESTAPI
{

  /*
   *  IMPLEMENTATION NOTES:
   *
   *    - This adheres to the current 2.0 version of the HTTP/REST/XML and HTTP/REST/JSON APIs.
   *      There's currently no packaging or REST URL distinction for supported API versions.
   *      Later versions of the Controller may support multiple revisions of the API depending
   *      on client request. Appropriate implementation changes should be made then.
   *                                                                                      [JPL]
   */


  // Class Members --------------------------------------------------------------------------------

  /**
   * Common log category for HTTP REST API.
   */
  private final static Logger logger = Logger.getLogger(Constants.REST_ALL_PANELS_LOG_CATEGORY);


  // TODO :
  //  reduce API dependency and lookup service implementation through either an service container
  //  or short term servlet application context

  private final static ProfileService profileService = (ProfileService) SpringContext.getInstance().getBean(
       "profileService");



  // Implement REST API ---------------------------------------------------------------------------


  @Override protected void handleRequest(HttpServletRequest request, HttpServletResponse response)
  {
    try
    {
      String panelsXML = profileService.getAllPanels();
      sendResponse(request, response, panelsXML);
    }

    catch (ControlCommandException e)
    {
      logger.error("failed to get all the panels", e);

      // TODO :
      //   this might well break the JSON client code -- but can't know for sure cause chinese
      //   are too effin dumb to write proper tests
      //
      // response.setStatus(e.getErrorCode());

      sendResponse(request, response, e.getErrorCode(), e.getMessage());
    }
  }

}
