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

import org.openremote.beehive.api.dto.IconDTO;
import org.openremote.beehive.api.service.IconService;

/**
 * Exports restful service of <code>Icon</code>
 * 
 * @author Tomsky 2009-4-21
 *
 */

@Path("/icons")
public class IconRESTService extends RESTBaseService {
   
   /**
    * Shows icons identified by iconName Visits @ url "/icons/{icon_name}"
    * 
    * @param iconName
    * @return IconListing contain a list of Icons
    */
   @GET
   @Produces( { "application/xml", "application/json" })
   @Path("{icon_name}")
   public Response getIcons(@PathParam("icon_name") String iconName) {
      List<IconDTO> list = getIconService().findIconsByName(iconName);
      if (list == null) {
         return resourceNotFoundResponse();
      }
      if (list.size() == 0) {
         return buildResponse(null);
      }
      return buildResponse(new IconListing(list));
   }

   /**
    * Shows all icons Visits @ url "/icons"
    * 
    * @return IconListing contain a list of Icons
    */
   @GET
   @Produces( { "application/xml", "application/json" })
   public Response getAllIcons() {
      List<IconDTO> list = getIconService().loadAllIcons();
      if (list == null) {
         return resourceNotFoundResponse();
      }
      if (list.size() == 0) {
         return buildResponse(null);
      }
      return buildResponse(new IconListing(list));
   }

   public IconService getIconService() {
      return (IconService) getSpringContextInstance().getBean("iconService");
   }
}
