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

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Created by IntelliJ IDEA. User: finalist Date: Mar 5, 2009 Time: 2:10:10 PM To change this template use File |
 * Settings | File Templates.
 */

/**
 * @author allen.wei
 */
public class GenericResponseWrapper extends HttpServletResponseWrapper {
   private ByteArrayOutputStream output;
   private int contentLength;
   private String contentType;

   public GenericResponseWrapper(HttpServletResponse response) {
      super(response);
      output = new ByteArrayOutputStream();
   }

   public byte[] getData() {
      return output.toByteArray();
   }

   public ServletOutputStream getOutputStream() {
      return new FilterServletOutputStream(output);
   }

   public PrintWriter getWriter() {
      return new PrintWriter(getOutputStream(), true);
   }

   public void setContentLength(int length) {
      this.contentLength = length;
      super.setContentLength(length);
   }

   public int getContentLength() {
      return contentLength;
   }

   public void setContentType(String type) {
      this.contentType = type;
      super.setContentType(type);
   }

   public String getContentType() {
      return contentType;
   }
}
