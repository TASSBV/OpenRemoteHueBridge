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

import java.io.File;
import java.io.FileNotFoundException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openremote.beehive.Constant;
import org.openremote.beehive.api.service.ResourceService;
import org.openremote.beehive.exception.InvalidPanelXMLException;
import org.openremote.beehive.exception.NoSuchAccountException;
import org.openremote.beehive.exception.NoSuchPanelException;
import org.openremote.beehive.exception.PanelXMLNotFoundException;

/**
 * Resource (openremote.zip) restful service. 
 * This openremote.zip is used for <code>OpenRemote Controller</code>.
 * 
 * @author Dan Cong
 */

@Path("/user/{username}")
public class ResourceRESTService extends RESTBaseService{
   
   public static final int TIMEOUT_CODE = 504;
   public static final int TIMEOUT_MILLISECOND = 45000;
   
   
   /**
    * Controller will use this method to download openremote.zip online.
    * 
    * @param username
    *           Beehive username
    * @param credentials
    *           HTTP basic header credentials : "Basic base64(username:md5(password,username))"
    * @return openremote.zip file
    */
   @Path(Constant.ACCOUNT_RESOURCE_ZIP_NAME)
   @GET
//   @Produces( { "application/zip" })
   @Produces( { MediaType.APPLICATION_OCTET_STREAM })
   public Response getResourcesForController(@PathParam("username") String username, 
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      
      if (!authorize(username, credentials)) {
         return unAuthorizedResponse();
      }
      
      File file = getResourceService().getResourceZip(username);
      if (file != null) {
         return buildResponse(file);
      }
      return resourceNotFoundResponse();
   }
   
   
   @Path("rest/panels")
   @GET
   @Produces( { MediaType.APPLICATION_XML })
   public Response getPanels(@PathParam("username") String username,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {

      if (!authorize(username, credentials, false)) {
         return unAuthorizedResponse();
      }
      String panels = null;
      
      try {
         panels = getResourceService().getAllPanelsXMLFromAccount(username);
      } catch (PanelXMLNotFoundException e) {
         throw new WebApplicationException(e, 426);
      } catch (InvalidPanelXMLException e) {
         throw new WebApplicationException(e, 427);
      } catch (NoSuchAccountException e) {
         return unAuthorizedResponse();
      }
      
      if (panels != null) {
         return buildResponse(panels);
      }
      return resourceNotFoundResponse();
   }

   @Path("rest/panel/{panel_id}")
   @GET
   @Produces( { MediaType.APPLICATION_XML })
   public Response getPanelXMLByName(@PathParam("username") String username, @PathParam("panel_id") String panelName,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {

      if (!authorize(username, credentials, false)) {
         return unAuthorizedResponse();
      }
      String panelXML = null;
      
      try {
         panelXML = getResourceService().getPanelXMLByPanelNameFromAccount(username, panelName);
      } catch (PanelXMLNotFoundException e) {
         throw new WebApplicationException(e, 426);
      } catch (InvalidPanelXMLException e) {
         throw new WebApplicationException(e, 427);
      } catch (NoSuchPanelException e) {
         throw new WebApplicationException(e, 428);
      } catch (NoSuchAccountException e) {
         return unAuthorizedResponse();
      }
      
      if (panelXML != null) {
         return buildResponse(panelXML);
      }
      return resourceNotFoundResponse();
   }
   
   @Path("resources/{file_name}")
   @GET
   @Produces( { MediaType.APPLICATION_OCTET_STREAM })
   public Response getResource(@PathParam("username") String username, @PathParam("file_name") String fileName,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {

      if (!authorize(username, credentials, false)) {
         return unAuthorizedResponse();
      }
      
      try {
         File file = getResourceService().getResource(username, fileName);
         if (file != null) {
            return buildResponse(file);
         }
      } catch (FileNotFoundException e) {
         throw new WebApplicationException(e,Response.Status.NOT_FOUND);
      }
      
      return resourceNotFoundResponse();
   }
   
   
   @GET
   public String getServiceAvailable() {
      return "OK";
   }
   

   @Path("rest/servers")
   @GET
   public String getGroupMemberServers() {
      return "<openremote></openremote>";
   }
   
   @Path("rest/status/{component_ids}")
   @GET
   public Response getComponentStatus() {
      try {
         Thread.sleep(TIMEOUT_MILLISECOND);
      } catch (InterruptedException e) {
         throw new WebApplicationException(e);
      }
      return Response.status(TIMEOUT_CODE).build();
   }
   
   @Path("rest/polling/{panel_id}/{component_ids}")
   @GET
   public Response getComponentPolling() {
      try {
         Thread.sleep(TIMEOUT_MILLISECOND);
      } catch (InterruptedException e) {
         throw new WebApplicationException(e);
      }
      return Response.status(TIMEOUT_CODE).build();
   }
   
   
   /*
    * If the user was not validated, fail with a
    * 401 status code (UNAUTHORIZED) and
    * pass back a WWW-Authenticate header for
    * this servlet.
    *  
    */
   protected boolean authorize(String username, String credentials) {
      return authorize(username, credentials, true);
   }
   
   private boolean authorize(String username, String credentials, boolean isPasswordEncoded) {
      if (!getAccountService().isHTTPBasicAuthorized(username, credentials, isPasswordEncoded)) {
         return false;
      }
      return true;
   }
   
   protected ResourceService getResourceService() {
      return (ResourceService) getSpringContextInstance().getBean("resourceService");
   }
}
