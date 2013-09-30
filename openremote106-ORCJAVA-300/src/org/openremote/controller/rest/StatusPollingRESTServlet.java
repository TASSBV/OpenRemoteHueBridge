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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openremote.controller.Constants;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.exception.ControlCommandException;
import org.openremote.controller.exception.ControllerException;
import org.openremote.controller.exception.NoSuchComponentException;
import org.openremote.controller.service.StatusPollingService;
import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.spring.SpringContext;

/**
 * Status Polling RESTful servlet of sensor.
 * It's responsiable for response corresponding result with the RESTful polling url.
 * 
 * @author Handy.Wang 2009-10-19
 */
public class StatusPollingRESTServlet extends RESTAPI {

  // TODO : add appropriate subcategory to logging
  private final static Logger logger = Logger.getLogger(Constants.HTTP_REST_LOG_CATEGORY);


  private StatusCache deviceStateCache = ServiceContext.getDeviceStateCache();

   /** This service is responsible for observe statuses change and return the changed statuses(xml-formatted). */
   private StatusPollingService statusPollingService =
      (StatusPollingService) SpringContext.getInstance().getBean("statusPollingService");
   
   @Override
   protected void handleRequest(HttpServletRequest request, HttpServletResponse response) {

      String url = request.getRequestURL().toString();
      String regexp = "rest\\/polling\\/(.*?)\\/(.*)";
      Pattern pattern = Pattern.compile(regexp);
      Matcher matcher = pattern.matcher(url);
      String unParsedSensorIDs = null;
      
      if (matcher.find()) {
         String deviceID = matcher.group(1);
         if (deviceID == null || "".equals(deviceID)) {
            sendResponse(request, response, ControlCommandException.INVALID_POLLING_URL, "Device id was null");
         }
         unParsedSensorIDs = matcher.group(2);
         try {
            checkSensorId(unParsedSensorIDs);
            String pollingResults = statusPollingService.queryChangedState(deviceID, unParsedSensorIDs);
            if (pollingResults != null && !"".equals(pollingResults)) {
               if (Constants.SERVER_RESPONSE_TIME_OUT.equalsIgnoreCase(pollingResults)) {
                  sendResponse(request, response, 504, "Time out");
               } else {
                  logger.info("Return the polling status.");
                  sendResponse(request, response, pollingResults);
               }
            } else {
               sendResponse(request, response, 504, "Time out");
            }
            logger.info("Finished polling at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
         } catch (ControllerException e) {
            logger.error("CommandException occurs", e);
            sendResponse(request, response, e.getErrorCode(), e.getMessage());
         }
      } else {
         sendResponse(request, response, ControlCommandException.INVALID_POLLING_URL, "Invalid polling url:"+url);
      }
   }
   
  /**
   * check whether the sensor id is valid.
   *
   * @param unParsedSensorIDs
   */
  private void checkSensorId(String unParsedSensorIDs)
  {
    String[] sensorIDs = (unParsedSensorIDs == null || "".equals(unParsedSensorIDs)) ? new String[] {}
          : unParsedSensorIDs.split(Constants.STATUS_POLLING_SENSOR_IDS_SEPARATOR);

    if (sensorIDs.length == 0)
    {
      throw new NullPointerException("Polling ids were null.");
    }

    String tmpStr = null;

    try
    {
      for (int i = 0; i < sensorIDs.length; i++)
      {
        tmpStr = sensorIDs[i];
        deviceStateCache.queryStatus(Integer.parseInt(tmpStr));
      }
    }

    catch (NumberFormatException e)
    {
       throw new NoSuchComponentException("Wrong sensor id :'"+tmpStr+"' The sensor id can only be digit");
    }
  }

}
