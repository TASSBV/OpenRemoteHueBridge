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
package org.openremote.controller.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openremote.controller.Constants;
import org.openremote.controller.exception.roundrobin.RoundRobinException;
import org.openremote.controller.rest.support.json.JSONTranslator;
import org.openremote.controller.service.RoundRobinService;
import org.openremote.controller.spring.SpringContext;

/**
 * REST servlet for providing round robin groupmembers, etc.
 * 
 * @author Handy.Wang 2009-12-23
 */
@SuppressWarnings("serial")
public class RoundRobinRESTServlet extends RESTAPI {
   
   private Logger logger = Logger.getLogger(this.getClass().getName());
   
   private RoundRobinService roundRobinService = (RoundRobinService) SpringContext.getInstance().getBean("roundRobinService");

   @Override
   protected void handleRequest(HttpServletRequest request, HttpServletResponse response) {
      logger.info("Start RoundRobin group member REST service. at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

      String url = request.getRequestURL().toString();
      String regexp = "rest\\/servers";
      Pattern pattern = Pattern.compile(regexp);
      Matcher matcher = pattern.matcher(url);
      if (matcher.find()) {
         try {
            Set<String> groupMemberControllerAppURLSet = roundRobinService.discoverGroupMembersAppURL();
            String serversXML = roundRobinService.constructServersXML(groupMemberControllerAppURLSet);
            sendResponse(request, response, serversXML);
            logger.info("Finished RoundRobin group member REST service.  at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
         } catch (RoundRobinException e) {
            logger.error("CommandException occurs", e);
            sendResponse(request, response, e.getErrorCode(), e.getMessage());
         }
      } else {
         sendResponse(request, response, RoundRobinException.INVALID_ROUND_ROBIN_URL, "Invalid round robin rul " + url);
      }
   }
}
