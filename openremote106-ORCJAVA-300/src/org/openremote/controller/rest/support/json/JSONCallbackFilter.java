/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.rest.support.json;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.openremote.controller.Constants;


/**
 * This is responsible for wrapping customized request and response for JSONP support.
 * 
 * @author handy.wang 2010-06-28
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
      String callbackName = servletRequest.getParameter(Constants.CALLBACK_PARAM_NAME);
      if (callbackName != null  && !"".equals(callbackName)) {
         servletResponse.setCharacterEncoding("UTF-8");
         PrintWriter printWriter = servletResponse.getWriter();
         printWriter.print(callbackName + " && " + callbackName + "(");
         JSONAcceptTypeRequestWrapper requestWrapper = new JSONAcceptTypeRequestWrapper((HttpServletRequest) servletRequest, filterConfig);
         filterChain.doFilter(requestWrapper, servletResponse);
         printWriter.print(");");
         printWriter.flush();
         printWriter.close();
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
