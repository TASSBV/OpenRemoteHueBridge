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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.openremote.beehive.api.dto.ModelDTO;
import org.openremote.beehive.api.service.ModelService;

/**
 * Exports restful service of <code>Model</code> User: allenwei Date: 2009-2-10
 */
@Path("/lirc/{vendor_name}")
public class ModelRESTService extends RESTBaseService {


   /**
    * Shows all models belongs to the vendor which name is {vendor_name} Visits @ url "/lirc/{vendor_name}"
    * 
    * @param vendorName
    * @return ModelListing contain a list of Models
    */
   @GET
   @Produces( { "application/xml", "application/json" })
   public Response getModels(@PathParam("vendor_name") String vendorName) {
      List<ModelDTO> list = getModelService().findModelsByVendorName(vendorName);
      if (list == null) {
          return resourceNotFoundResponse();
      }
      if (list.size() == 0) {
         return buildResponse(null);
      }
      return buildResponse(new ModelListing(list));
   }

   /**
    * Retrieves instance of ModelService from spring IOC
    * 
    * @return ModelService instance
    */
   public ModelService getModelService() {
      return (ModelService) getSpringContextInstance().getBean("modelService");
   }
}
