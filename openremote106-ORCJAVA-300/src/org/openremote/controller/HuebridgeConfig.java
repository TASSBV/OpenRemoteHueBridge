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
package org.openremote.controller;

import org.openremote.controller.service.ServiceContext;

/*
* @author <a href="mailto:eric@openremote.org>Eric Bariaux</a>
*/
public class HuebridgeConfig extends Configuration {

   public final static String HUEBRIDGE_ADDRESS = "huebridge.address";
   public final static String HUEBRIDGE_KEY = "huebridge.key";


  // Class Members --------------------------------------------------------------------------------

  public static HuebridgeConfig readXML()
  {
     HuebridgeConfig config = ServiceContext.getHuebridgeConfiguration();

    return (HuebridgeConfig)Configuration.updateWithControllerXMLConfiguration(config);
  }


  // Instance Fields ------------------------------------------------------------------------------

   private String address;
   private String key;


  // Public Instance Methods ----------------------------------------------------------------------
  

   public String getAddress()
  {
      return preferAttrCustomValue(HUEBRIDGE_ADDRESS, address);
   }

   public void setAddress(String address)
  {
      this.address = address;
   }

   public String getKey()
  {
      return preferAttrCustomValue(HUEBRIDGE_KEY, key);
   }

   public void setKey(String key)
  {
      this.key = key;
   }

}