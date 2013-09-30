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

import org.openremote.controller.model.sensor.Sensor;

/**
* Lagarto network class
*/
public class LagartoNetwork
{
  /**
  * Name of the lagarto network (server)
  */
  private String networkName;

  /**
  * HTTP address/port of the lagarto server
  */
  private String serverHttpAddr = null;

  /**
  * Map containing all endpoints managed by the lagarto client
  */
  public Map<String, Sensor> sensorMap = new HashMap<String, Sensor>();

  /**
   * Class constructor
   *
   * @param networkName Name of the lagarto network
   * @param serverAddr HTTP address/port of the lagarto server
   */
  public LagartoNetwork(String networkName, String serverAddr)
  {
     this.networkName = networkName;
     this.serverHttpAddr = serverAddr;
  }

  /**
   * Class constructor
   *
   * @param networkName Name of the lagarto network
   */
  public LagartoNetwork(String networkName)
  {
     this.networkName = networkName;
  }

  /**
   * Get endpoint from map
   *
   * @param id endpoint ID
   */
   public Sensor getEndpoint(String id)
   {
     if (sensorMap.containsKey(id))
       return sensorMap.get(id);

     return null;
   }

  /**
   * Add endpoint to the map
   *
   * @param id endpoint ID
   * @param sensor object
   */
   public void addEndpoint(String id, Sensor sensor)
   {
     sensor.update("N/A");
     if (sensorMap.containsKey(id) == false)
       sensorMap.put(id, sensor);
   }

  /**
   * Remove endpoint from map
   *
   * @param id endpoint ID
   */
   public void removeEndpoint(String id)
   {
     if (sensorMap.containsKey(id))
       sensorMap.remove(id);
   }

   /**
    * Get network name
    */
   public String getName()
   {
     return this.networkName;
   }

   /**
    * Get HTTP address:port
    */
   public String getHttpAddr()
   {
     return this.serverHttpAddr;
   }

   /**
    * Set HTTP address:port
    *
    * @param httAddr new HTTP address:port
    */
   public void setHttpAddr(String httpAddr)
   {
     this.serverHttpAddr = httpAddr;
   }
}
