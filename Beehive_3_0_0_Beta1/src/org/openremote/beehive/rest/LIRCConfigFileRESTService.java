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

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.openremote.beehive.api.service.RemoteSectionService;
import org.openremote.beehive.utils.StringUtil;

/**
 * Exports restful service of LIRC config file export
 * 
 * @author allen.wei 2009-2-15
 */
@Path("/lirc.conf")
public class LIRCConfigFileRESTService extends RESTBaseService {

   /**
    * Shows lirc config file according to vendor name and model name Visits @ url "/{vendor_name}/{model_name}"
    * 
    * @param sectionIds
    * @return content of lirc configuration file
    */
   @GET
   @Produces("text/plain")
   public String getLIRCConfigFile(@QueryParam("ids") String sectionIds) {
      ArrayList<Long> ids = StringUtil.parseStringIds(sectionIds,",");
      if (ids.size() == 0) {
         return "";
      }
      StringBuffer lircStr = new StringBuffer();
      for (long id : ids) {
         lircStr.append(getRemoteSectionService().exportText(id));
         lircStr.append(System.getProperty("line.separator"));
      }
      return lircStr.toString();

   }

   /**
    * Retrieves instance of RemoteSectionService from spring IOC container
    * 
    * @return RemoteSectionService instance
    */
   private RemoteSectionService getRemoteSectionService() {
      return (RemoteSectionService) getSpringContextInstance().getBean("remoteSectionService");
   }
}
