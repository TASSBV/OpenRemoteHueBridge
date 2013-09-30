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

package org.openremote.controller.protocol.lagarto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.openremote.controller.utils.Logger;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.command.ExecutableCommand;

/**
  * Lagarto listener class. Designed to receive event-driven information
 */
public class LagartoCommand implements ExecutableCommand, EventListener
{
  /**
   * Logger
   */
  private final static Logger logger = Logger.getLogger(LagartoCommandBuilder.LAGARTO_PROTOCOL_LOG_CATEGORY);

  /**
  * Lagarto client thread
  */
  private static LagartoClient lagartoClient = null;

  /**
  * Name of the lagarto network
  */
  private String networkName;

  /**
  * Endpoint ID
  */
  private String epId;

  /**
  * Endpoint value
  */
  private String epValue = null;

  /**
  * Class constructor
  *
  * @param networkName Network name
  * @param id endpoint ID
  * @param value New endpoint value
  */
  public LagartoCommand(String networkName, String id, String value)
  {
    this.networkName = networkName;
    this.epId = id;
    this.epValue = value;
  }

  @Override
  public void setSensor(Sensor sensor)
  {
    LagartoNetwork network = null;

    // Get lagarto network
    if (lagartoClient != null)
    {
      network = lagartoClient.getNetwork(networkName);
    }
    else
    {
      lagartoClient = new LagartoClient();
      lagartoClient.start();
    }
    
    // Add the network if not found
    if (network == null)
      network = lagartoClient.addNetwork(networkName, null);

    // Add endpoint (sensor) to the network
    network.addEndpoint(epId, sensor);

    logger.info("Endpoint command created for " + epId);
  }

  @Override
  public void stop(Sensor sensor)
  {
  }

  @Override
  public void send()
  {
    try
    {
      httpRequest();
    }
    catch (LagartoException ex)
    {
      ex.logError();
    }
  }

  /**
  * Send HTTP request to lagarto server
  *
  * @return Response string from lagarto server
  */
  private String httpRequest() throws LagartoException
  {
    LagartoNetwork network = lagartoClient.getNetwork(networkName);

    if (network == null)
      throw new LagartoException("Lagarto network not available");

   
    if (network.getHttpAddr() == null)
      return null;

    BufferedReader in = null;

    try
    {
      String strUrl = "http://" + network.getHttpAddr() + "/values/" + this.epId + "/?";

      if (this.epValue == null)
        logger.error("Tried to send http command to input endpoint");
      else
      {
        strUrl += "value=" + this.epValue;
        URL cmdurl = new URL(strUrl);

        in = new BufferedReader(new InputStreamReader(cmdurl.openStream()));
        StringBuffer result = new StringBuffer();
        String str;
        while ((str = in.readLine()) != null)
        {
          result.append(str);
        }

        logger.info("Command sent to endpoint " + this.epId + ". New value = " + this.epValue);
        return result.toString();
      }
    }
    catch (Exception e)
    {
      logger.error("HttpGetCommand could not execute", e);
    }
    finally
    {
      if (in != null)
      {
        try
        {
          in.close();
        }
        catch (IOException e)
        {
          logger.error("BufferedReader could not be closed", e);
        }
      }
    }
    return "";
  }
}
