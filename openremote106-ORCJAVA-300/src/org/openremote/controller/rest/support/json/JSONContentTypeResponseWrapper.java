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

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * This is responsible for wraping server's response with caching content of response<br />
 * into ByteArrayOutputStream which saved the content into memory.<br />
 * 
 * This is mainly ready for appending something else into response content in filter.<br />
 * In current situation, this prepared for supporting JSONP, that means appending callback name, "(" and ")" in JSONCallbackFilter.<br />
 * For instance, resonpse content is JSON data with {"person":{"@name":"somename", "@age":"22"}}, <br />
 * this data is wrapped into aaa && aaa({"person":{"@name":"somename", "@age":"22"}}), if the callback name is aaa.
 * 
 * @author handy.wang 2010-06-28
 */
public class JSONContentTypeResponseWrapper extends HttpServletResponseWrapper {
   private ByteArrayOutputStream output;
   private int contentLength;
   private String contentType;

   public JSONContentTypeResponseWrapper(HttpServletResponse response) {
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
