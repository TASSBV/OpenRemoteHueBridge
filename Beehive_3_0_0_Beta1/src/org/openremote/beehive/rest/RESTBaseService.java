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

import java.lang.reflect.Method;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.openremote.beehive.api.service.AccountService;
import org.openremote.beehive.spring.ISpringContext;
import org.openremote.beehive.spring.SpringContext;


/**
 * REST Base Service for changing SpringContext.
 * 
 * @author Dan Cong
 */
public class RESTBaseService {
   
   private static Logger log = Logger.getLogger(RESTBaseService.class);
   
   protected Class<? extends ISpringContext> getSpringContextClass() {
      return SpringContext.class;
   }
   
   public ISpringContext getSpringContextInstance() {
      Method m = null;
      try {
         m = getSpringContextClass().getMethod("getInstance", new Class[] {});
      } catch (NoSuchMethodException e) {
         log.error(e);
      }
      try {
         return (ISpringContext) m.invoke(this, new Object[] {});
      } catch (Exception e) {
         log.error(e);
      }
      return null;
   }

   protected Response buildResponse(Object entity) {
      if (entity != null) {
         return Response.status(Response.Status.OK).entity(entity).build();
      }
      return Response.status(Response.Status.NO_CONTENT).build();
   }
   
   protected Response resourceNotFoundResponse() {
      return Response.status(Response.Status.NOT_FOUND).build();
   }
   
   protected Response unAuthorizedResponse() {
      return Response.status(Response.Status.UNAUTHORIZED).build();
   }
   
   /*
    * If the user was not validated, fail with a
    * 401 status code (UNAUTHORIZED) and
    * pass back a WWW-Authenticate header for
    * this servlet.
    *  
    */
   protected boolean authorize(long accountId, String credentials) {
      if (!getAccountService().isHTTPBasicAuthorized(accountId, credentials)) {
         return false;
      }
      return true;
   }
   
   protected boolean authorize(String credentials) {
      if (!getAccountService().isHTTPBasicAuthorized(credentials)) {
         return false;
      }
      return true;
   }
   
   protected AccountService getAccountService() {
      return (AccountService) getSpringContextInstance().getBean("accountService");
   }
   
   
}
