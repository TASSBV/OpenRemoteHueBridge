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
package org.openremote.modeler.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.context.SecurityContext;
/**
 * A filter to check whether user's session is out of date. 
 * If the session has timeout, http code 401 will be sent to client. 
 * @author Javen
 *
 */
public class UserTimeoutFilter implements Filter {

   @Override
   public void destroy() {
   }

   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
         ServletException {
      HttpSession session = ((HttpServletRequest)request).getSession(false);
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      if (session == null) {
         handleTimeout(httpResponse);
         return;
      }
      SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
      if (securityContext == null || !securityContext.getAuthentication().isAuthenticated()) {
         handleTimeout(httpResponse);
         return ;
      }
      chain.doFilter(request, response);
   }

   @Override
   public void init(FilterConfig filterConfig) throws ServletException {
   }

   private void handleTimeout(HttpServletResponse response) throws IOException {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
   }
}
