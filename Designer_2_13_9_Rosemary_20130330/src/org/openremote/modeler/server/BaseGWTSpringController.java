/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.modeler.server;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The Class BaseGWTSpringController is a supper class of rpc implement. the subclass can only return common pojo.
 */
public class BaseGWTSpringController extends RemoteServiceServlet implements Controller, ServletContextAware {

   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = 8359963960220818310L;
   
   /** The servlet context. */
   private ServletContext servletContext;

   /**
    * @see org.springframework.web.context.ServletContextAware#setServletContext(javax.servlet.ServletContext)
    * @param servletContext servletContext
    */
   public void setServletContext(ServletContext servletContext) {
      this.servletContext = servletContext;
   }

   /**
    * @see javax.servlet.GenericServlet#getServletContext()
    * @return servletContext
    */
   public ServletContext getServletContext() {
      return servletContext;
   }

   /**
    * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    * @param request request
    * @param response response
    * @throws Exception exception
    * @return null
    */
   public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
      super.doPost(request, response);
      return null;
   }

}
