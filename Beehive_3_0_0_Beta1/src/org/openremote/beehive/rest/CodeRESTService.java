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

import org.openremote.beehive.api.dto.CodeDTO;
import org.openremote.beehive.api.service.CodeService;

/**
 * Exports restful service of <code>Code</code> User: allenwei Date: 2009-2-10
 */
@Path("/lirc/{vendor_name}/{model_name}/{section_id}/codes")
public class CodeRESTService extends RESTBaseService {

   /**
    * Shows codes by {section_id} Visits @ url "/lirc/{vendor_name}/{model_name}/{section_id}/codes"
    * 
    * @param vendorName
    * @param modelName
    * @param sectionId
    * @return RemoteOptionListing contain a list of Codes
    */
   @GET
   @Produces( { "application/xml", "application/json" })
   public Response getRemoteOptions(@PathParam("vendor_name") String vendorName,
         @PathParam("model_name") String modelName, @PathParam("section_id") long sectionId) {
      List<CodeDTO> list = getCodeService().findByRemoteSectionId(sectionId);
      if (list.size() == 0) {
         return buildResponse(null);
      }
      return buildResponse(new CodeListing(list));
   }

   /**
    * Retrieves instance of CodeService from spring IOC
    * 
    * @return CodeService instance
    */
   public CodeService getCodeService() {
      return (CodeService) getSpringContextInstance().getBean("codeService");
   }
}