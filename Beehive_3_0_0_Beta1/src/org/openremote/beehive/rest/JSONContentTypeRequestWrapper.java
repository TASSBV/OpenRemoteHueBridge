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

import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Created by IntelliJ IDEA. User: finalist Date: Mar 5, 2009 Time: 2:17:53 PM To change this template use File |
 * Settings | File Templates.
 */

/**
 * @author allen.wei
 */
public class JSONContentTypeRequestWrapper extends HttpServletRequestWrapper {

   FilterConfig myFilterConfig;

   public JSONContentTypeRequestWrapper(HttpServletRequest request, FilterConfig filterConfig) {
      super(request);
      myFilterConfig = filterConfig;
   }

   @Override
   public String getHeader(String name) {
      if ("Accept".toLowerCase().equals(name.toLowerCase())) {
         return "application/json";
      } else {
         return super.getHeader(name);
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public Enumeration getHeaders(String s) {
      if ("accept".equals(s.toLowerCase())) {
         Vector<String> headers = new Vector();
         headers.add("application/json");
         return headers.elements();
      } else {
         return super.getHeaders(s);
      }

   }
}
