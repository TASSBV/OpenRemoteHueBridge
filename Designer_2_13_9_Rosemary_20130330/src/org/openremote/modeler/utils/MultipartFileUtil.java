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
package org.openremote.modeler.utils;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public final class MultipartFileUtil {

   private static final Logger LOGGER = Logger.getLogger(MultipartFileUtil.class);
   
   /**
    * Gets the multipart file from request.
    * Used for uploading file.
    * 
    * @param request the request
    * @param fileFieldName the file field name
    * 
    * @return the multipart file from request
    */
   @SuppressWarnings("unchecked")
   public static MultipartFile getMultipartFileFromRequest(HttpServletRequest request, String fileFieldName) {
      MultipartFile multipartFile = null;
      FileItemFactory factory = new DiskFileItemFactory();
      ServletFileUpload upload = new ServletFileUpload(factory);
      List items = null;
      try {
         items = upload.parseRequest(request);
      } catch (FileUploadException e) {
         LOGGER.error("get InputStream from httpServletRequest error.", e);
      }
      if (items == null) {
         return null;
      }
      Iterator it = items.iterator();
      FileItem fileItem = null;
      while (it.hasNext()) {
         fileItem = (FileItem) it.next();
         if (!fileItem.isFormField() && fileFieldName != null && fileFieldName.equals(fileItem.getFieldName())) {
            break;
         }
      }
      if (fileItem != null) {
         multipartFile = new CommonsMultipartFile(fileItem);
      }
      return multipartFile;
   }
}
