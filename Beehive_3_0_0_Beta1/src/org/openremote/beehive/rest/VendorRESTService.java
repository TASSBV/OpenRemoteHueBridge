/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.beehive.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.openremote.beehive.api.dto.VendorDTO;
import org.openremote.beehive.api.service.VendorService;

/**
 * Exports restful service of <code>Vendor</code> User: allenwei Date: 2009-2-9
 */
@Path("/lirc")
public class VendorRESTService extends RESTBaseService {

   /**
    * Shows all vendors Visits @ url "/lirc"
    * 
    * @return VendorListing contains a list of Vendors
    */
   @GET
   @Produces( { "application/xml", "application/json" })
   public Response getAllVendors() {
      List<VendorDTO> list = getVendorService().loadAllVendors();
      if (list.size() > 0) {
         return buildResponse(new VendorListing(list));
      }
      return resourceNotFoundResponse();
   }

   /**
    * Retrieves instance of VendorDAO from spring IOC
    * 
    * @return VendorService instance
    */
   protected VendorService getVendorService() {
         return (VendorService) getSpringContextInstance().getBean("vendorService");
   }
   
}
