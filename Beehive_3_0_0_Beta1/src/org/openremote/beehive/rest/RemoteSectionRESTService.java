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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.openremote.beehive.api.dto.ModelDTO;
import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.api.service.RemoteSectionService;

/**
 * Exports restful service of <code>RemoteSection</code> User: allenwei Date: 2009-2-10
 */
@Path("/lirc/{vendor_name}/{model_name}")
public class RemoteSectionRESTService extends RESTBaseService {

   /**
    * Shows remoteSecrtions by {vendor_name} and {model_name} Visits @ url "/lirc/{vendor_name}/{model_name}"
    * 
    * @param vendorName
    * @param modelName
    * @return RemoteSectionListing contain a list of RemoteSections
    */
   @GET
   @Produces( { "application/xml", "application/json" })
   public Response getRemoteSections(@PathParam("vendor_name") String vendorName,
         @PathParam("model_name") String modelName) {
      ModelDTO model = getModelService().loadByVendorNameAndModelName(vendorName, modelName);
      if (model == null) {
         return resourceNotFoundResponse();
      }
      return buildResponse(new RemoteSectionListing(getRemoteSectionService().findByModelId(model.getOid())));
   }

   /**
    * Retrieves instance of ModelService from spring IOC
    * 
    * @return ModelService instance
    */
   public ModelService getModelService() {
      return (ModelService) getSpringContextInstance().getBean("modelService");
   }

   /**
    * Retrieves instance of RemoteSectionService from spring IOC
    * 
    * @return RemoteSectionService instance
    */
   public RemoteSectionService getRemoteSectionService() {
      return (RemoteSectionService) getSpringContextInstance().getBean("remoteSectionService");
   }
}