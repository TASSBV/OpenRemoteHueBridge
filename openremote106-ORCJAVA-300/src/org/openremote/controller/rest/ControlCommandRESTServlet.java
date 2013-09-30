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

import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.controller.Constants;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.exception.ControlCommandException;
import org.openremote.controller.exception.InvalidCommandTypeException;
import org.openremote.controller.service.ControlCommandService;
import org.openremote.controller.service.ServiceContext;

/**
 * TODO
 *
 * @author Handy.Wang
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author <a href="mailto:marcus@openremote.org">Marcus Redeker</a>
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
@SuppressWarnings("serial")
public class ControlCommandRESTServlet extends RESTAPI {
   
  // Class Members --------------------------------------------------------------------------------

  private final static Logger logger = Logger.getLogger(Constants.REST_COMPONENT_ACTION_LOG_CATEGORY);

  private final static ControlCommandService componentControlService =
    ServiceContext.getComponentControlService();


  // Implement REST API ---------------------------------------------------------------------------

  @Override
  protected void handleRequest(HttpServletRequest request, HttpServletResponse response)
  {
    String pathinfo = request.getPathInfo();
    String componentID = null;
    String commandParam = null;
    StringTokenizer st = new StringTokenizer(pathinfo, "/");

    if (st.hasMoreTokens())
    {
      componentID = st.nextToken();
    }

    if (st.hasMoreTokens())
    {
      commandParam = st.nextToken();
    }

    try
    {
      if (isNotEmpty(componentID) && isNotEmpty(commandParam))
      {
        componentControlService.trigger(componentID, commandParam);

        sendResponse(request, response, 200, "SUCCESS");
      }

      else
      {
        throw new InvalidCommandTypeException(commandParam);
      }
    }

    catch (ControlCommandException e)
    {
      logger.error("Error executing command ''{0}'' : {1}", e, pathinfo, e.getMessage());
      sendResponse(request, response, e.getErrorCode(), e.getMessage());
    }
  }


  /**
   * Checks if String parameter is not empty.
   *
   * @param param the param
   *
   * @return true, if parameter is not empty
   */
  private boolean isNotEmpty(String param)
  {
    return (param != null && !"".equals(param)) ? true : false;
  }
}
