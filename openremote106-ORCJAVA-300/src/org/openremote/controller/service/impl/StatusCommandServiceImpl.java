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
package org.openremote.controller.service.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openremote.controller.Constants;
import org.openremote.controller.config.ControllerXMLChangedException;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.service.StatusCommandService;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.utils.Logger;

/**
 * TODO
 * 
 * @author Handy.Wang 2009-10-15
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class StatusCommandServiceImpl implements StatusCommandService
{
  // Class Members --------------------------------------------------------------------------------

  private final static Logger log = Logger.getLogger(Constants.XML_PARSER_LOG_CATEGORY);


  // Instance Fields ------------------------------------------------------------------------------

  private StatusCache statusCache;
  private Deployer deployer;
  

  // Constructors ---------------------------------------------------------------------------------

  public StatusCommandServiceImpl(Deployer deployer, StatusCache statusCache)
  {
    this.deployer = deployer;
    this.statusCache = statusCache;
  }


  // Implements StatusCommandService --------------------------------------------------------------

  @Override public String readFromCache(String unParsedSensorIDs)
  {
    if (deployer.isPaused())
    {
      throw new ControllerXMLChangedException("The content of controller.xml had changed.");
    }

    Set<Integer> statusSensorIDs = parseStatusSensorIDsStrToSet(unParsedSensorIDs);
    Map<Integer, String> latestStatuses = statusCache.queryStatus(statusSensorIDs);

    StringBuffer sb = new StringBuffer();
    sb.append(Constants.STATUS_XML_HEADER);
    Set<Integer> sensorIDs = latestStatuses.keySet();

    for (Integer sensorID : sensorIDs)
    {
      sb.append("<")
          .append(Constants.STATUS_XML_STATUS_RESULT_ELEMENT_NAME)
          .append(" ")
          .append(Constants.STATUS_XML_STATUS_RESULT_ELEMENT_SENSOR_IDENTITY)
          .append("=\"")
          .append(sensorID)
          .append("\">");

      sb.append(latestStatuses.get(sensorID));
      sb.append("</");
      sb.append(Constants.STATUS_XML_STATUS_RESULT_ELEMENT_NAME + ">\n");
      sb.append("\n");
    }

    sb.append(Constants.STATUS_XML_TAIL);

    return sb.toString();
  }


  // Private Instance Methods ---------------------------------------------------------------------
  
  private Set<Integer> parseStatusSensorIDsStrToSet(String unParsedSensorIDs)
  {
    String[] parsedSensorIDs = unParsedSensorIDs.split(Constants.STATUS_POLLING_SENSOR_IDS_SEPARATOR);
    Set<Integer> statusSensorIDs = new HashSet<Integer>();

    for (String statusSensorID : parsedSensorIDs)
    {
      try
      {
        statusSensorIDs.add(Integer.parseInt(statusSensorID));
      }

      catch (NumberFormatException e)
      {
        log.warn("Sensor ID = {0} is not a valid integer ID. Skipping...", statusSensorID);
      }
    }

    return statusSensorIDs;
  }

}
