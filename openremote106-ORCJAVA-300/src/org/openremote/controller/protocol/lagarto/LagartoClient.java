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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.zeromq.ZMQ;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import org.openremote.controller.utils.Logger;
import org.openremote.controller.model.sensor.Sensor;

/**
 * Lagarto client thread
 */
public class LagartoClient extends Thread
{
  /**
   * Logger
   */
  private final static Logger logger = Logger.getLogger(LagartoCommandBuilder.LAGARTO_PROTOCOL_LOG_CATEGORY);

  /**
   * Map containing all endpoints managed by the lagarto client
   */
  private Map<String, LagartoNetwork> networkMap = new HashMap<String, LagartoNetwork>();

  /**
   * ZeroMQ subscribing address
   */
  private String broadcastAddr = "tcp://127.0.0.1:5001";

  /**
   * Class constructor
   */
  public LagartoClient()
  {
    broadcastAddr = LagartoCommandBuilder.controllerConfig.getLagartoBroadcastAddr();
  }

  /**
   * Parse lagarto packet
   *
   * @param packet Lagarto message to be parsed
   */
  private void parseLagartoMsg(String packet) throws LagartoException
  {
    try
    {
      JSONObject json = new JSONObject(packet);
      json = json.getJSONObject("lagarto");

      String networkName = json.getString("procname");
      String serverHttpAddr = json.getString("httpserver");

      // Add lagarto network
      LagartoNetwork network = addNetwork(networkName, serverHttpAddr);

      if (json.has("status"))
      {
        JSONArray array = json.getJSONArray("status");

        // Iterate endpoints in lagarto message
        for(int i = 0 ; i < array.length() ; i++)
        {
          JSONObject jsEndp = array.getJSONObject(i);
          String epId = jsEndp.getString("id");

          // Iterate sensors in current map
          for (Entry<String, Sensor> entry : network.sensorMap.entrySet())
          {            
            // Locate endpoint id
            if (entry.getKey().equals(epId))
            {
              // Update value
              String strVal = "";

              if (jsEndp.has("value"))
                strVal = jsEndp.getString("value");

              Sensor sensor = entry.getValue();
              sensor.update(strVal);
            }
          }
        }
      }
    } catch (JSONException ex)
    {
      throw new LagartoException("Unable to parse Lagarto message");
    }
  }

  /**
   * Run thread
   */
  @Override
  public void run()
  {
    try
    {
      //  Prepare our context and publisher
      ZMQ.Context context = ZMQ.context(1);

      // Subscribe to broadcast address
      ZMQ.Socket subscriber = context.socket(ZMQ.PULL);
      subscriber.connect(this.broadcastAddr);

      logger.info("ZMQ PULL socket conected to " + this.broadcastAddr);

      // Endless loop
      while(true)
      {
        try
        {
          // Listen to updates from Lagarto server
          String msg = new String(subscriber.recv(0));
          parseLagartoMsg(msg);
        }
        catch (LagartoException ex)
        {
          ex.logInfo();
        }
      }
    }
    catch (Exception ex)
    {
      logger.error("Unable to connect to ZeroMQ socket on " + this.broadcastAddr, ex);
    }
  }

  /**
   * Get lagarto network
   *
   * @param networkName Network name
   *
   * @return lagarto network object
   */
  public LagartoNetwork getNetwork(String networkName)
  {
    if (networkMap.containsKey(networkName))
      return networkMap.get(networkName);

    return null;
  }

  /**
   * Add lagarto network to the map
   *
   * @param networkName Network name
   * @param serverHttpAddr HTTP address:port of the lagarto server
   *
   * @return lagarto network object
   */
  public LagartoNetwork addNetwork(String networkName, String serverHttpAddr)
  {
    LagartoNetwork network = getNetwork(networkName);

    if (network == null)
    {
      network = new LagartoNetwork(networkName, serverHttpAddr);
      networkMap.put(networkName, network);
    }
    else if (network.getHttpAddr() == null)
      network.setHttpAddr(serverHttpAddr);

    return network;
  }

  /**
   * Remove lagarto network
   *
   * @param networkName Network name
   */
  public void removeNetwork(String networkName)
  {
    if (networkMap.containsKey(networkName))
      networkMap.remove(networkName);
  }
}
