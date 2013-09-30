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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openremote.controller.Constants;
import org.openremote.controller.exception.ControlCommandException;
import org.openremote.controller.service.ProfileService;
import org.openremote.controller.spring.SpringContext;

/**
 * TODO : Get panel.xml by profile (panel name).
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Javen, Dan Cong
 *
 */
public class FindPanelByID extends RESTAPI
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * Common log category for HTTP REST API.
   */
  private final static Logger logger = Logger.getLogger(Constants.REST_GET_PANEL_DEF_LOG_CATEGORY);

  // TODO :
  //  reduce API dependency and lookup service implementation through either an service container
  //  or short term servlet application context

  private static final ProfileService profileService = (ProfileService) SpringContext.getInstance().getBean("profileService");

  private static final long serialVersionUID = 1L;



  // Implement REST API ---------------------------------------------------------------------------

  @Override protected void handleRequest(HttpServletRequest request, HttpServletResponse response)
  {
    String url = request.getRequestURL().toString().trim();
    String regexp = "rest\\/panel\\/(.*)";
    Pattern pattern = Pattern.compile(regexp);
    Matcher matcher = pattern.matcher(url);

    if (matcher.find())
    {
      try
      {
        String panelName = matcher.group(1);
        String decodedPanelName = panelName;
        decodedPanelName = URLDecoder.decode(panelName, "UTF-8");

        String panelXML = profileService.getProfileByPanelName(decodedPanelName);

        sendResponse(request, response, panelXML);
      }

      catch (ControlCommandException e)
      {
        logger.error("failed to extract panel.xml for panel : " + e.getMessage(), e);

        // TODO :
        //   this might well break the JSON client code -- but can't know for sure cause chinese
        //   are too effin dumb to write proper tests
        //
        // response.setStatus(e.getErrorCode());

        sendResponse(request, response, e.getErrorCode(), e.getMessage());
      }

      catch (UnsupportedEncodingException e)
      {
        logger.error(e.getMessage(), e);

        sendResponse(request, response, 400, e.getMessage());    // TODO : check API documentation   
      }
    }

    else
    {
      // TODO :
      //   this might well break the JSON client code -- but can't know for sure cause chinese
      //   are too effin dumb to write proper tests
      //
      //response.setStatus(400);

      sendResponse(request, response, 400, "Bad REST Request, should be /rest/panel/{panelName}");
    }
  }

}
