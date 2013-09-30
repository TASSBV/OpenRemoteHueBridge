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

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Au allen.wei
 */
public class JSONCallbackFilter implements Filter {
   private FilterConfig filterConfig;

   public void init(FilterConfig filterConfig) throws ServletException {
      this.filterConfig = filterConfig;
   }

   public void destroy() {
      this.filterConfig = null;
   }

   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
         throws IOException, ServletException {
      if (servletRequest.getParameter("callback") != null) {
         String callback = servletRequest.getParameter("callback").toString();
         JSONContentTypeRequestWrapper requestWrapper = new JSONContentTypeRequestWrapper(
               (HttpServletRequest) servletRequest, filterConfig);
         OutputStream out = servletResponse.getOutputStream();
         out.write((callback + " && " + callback + "(").getBytes());
         GenericResponseWrapper responseWrapper = new GenericResponseWrapper((HttpServletResponse) servletResponse);
         filterChain.doFilter(requestWrapper, responseWrapper);
         out.write(responseWrapper.getData());
         out.write(")".getBytes());
         out.close();
      } else {
         filterChain.doFilter(servletRequest, servletResponse);
      }

   }

   public FilterConfig getFilterConfig() {
      return this.filterConfig;
   }

   public void setFilterConfig(FilterConfig filterConfig) {
      this.filterConfig = filterConfig;
   }

}
