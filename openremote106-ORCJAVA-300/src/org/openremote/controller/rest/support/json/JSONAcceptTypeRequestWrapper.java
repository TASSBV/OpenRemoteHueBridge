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

import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.openremote.controller.Constants;

/**
 * This is responsible for wrapping a request whose accept value in header is "application/json" <br />
 * and others are same as HttpServletRequest.
 * 
 * @author handy.wang 2010-06-28
 */
public class JSONAcceptTypeRequestWrapper extends HttpServletRequestWrapper {

   FilterConfig myFilterConfig;

   public JSONAcceptTypeRequestWrapper(HttpServletRequest request, FilterConfig filterConfig) {
      super(request);
      myFilterConfig = filterConfig;
   }

   @Override
   public String getHeader(String name) {
      if (Constants.HTTP_ACCEPT_HEADER.toLowerCase().equals(name.toLowerCase())) {
         return Constants.MIME_TEXT_JAVASCRIPT;
      } else {
         return super.getHeader(name);
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public Enumeration getHeaders(String s) {
      if (Constants.HTTP_ACCEPT_HEADER.toLowerCase().equals(s.toLowerCase())) {
         Vector<String> headers = new Vector();
         headers.add(Constants.MIME_TEXT_JAVASCRIPT);
         return headers.elements();
      } else {
         return super.getHeaders(s);
      }

   }
}
