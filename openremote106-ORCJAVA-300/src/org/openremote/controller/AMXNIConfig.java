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
public class AMXNIConfig extends Configuration {

   public final static String AMX_NI_ADDRESS = "amx_ni.address";
   public final static String AMX_NI_PORT = "amx_ni.port";


  // Class Members --------------------------------------------------------------------------------

  public static AMXNIConfig readXML()
  {
     AMXNIConfig config = ServiceContext.getAMXNiConfiguration();

    return (AMXNIConfig)Configuration.updateWithControllerXMLConfiguration(config);
  }


  // Instance Fields ------------------------------------------------------------------------------

   private String address;
   private int port;


  // Public Instance Methods ----------------------------------------------------------------------
  

   public String getAddress()
  {
      return preferAttrCustomValue(AMX_NI_ADDRESS, address);
   }

   public void setAddress(String address)
  {
      this.address = address;
   }

   public int getPort()
  {
      return preferAttrCustomValue(AMX_NI_PORT, port);
   }

   public void setPort(int port)
  {
      this.port = port;
   }

}