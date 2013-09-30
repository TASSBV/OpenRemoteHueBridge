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
package org.openremote.beehive.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.beehive.api.service.RemoteSectionService;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * Controller for exporting a section of a LIRC configuration file.
 * 
 * @author Dan 2009-2-6
 * 
 */
public class RemoteSectionController extends MultiActionController {

   private RemoteSectionService remoteSectionService;

   /**
    * Exports a section of a LIRC configuration without disk writes.
    * 
    * @param request
    *           HttpServletRequest
    * @param response
    *           HttpServletResponse
    * @return null
    * @throws IOException
    * @throws ServletRequestBindingException
    */
   public ModelAndView export(HttpServletRequest request, HttpServletResponse response) throws IOException,
         ServletRequestBindingException {
      long id = ServletRequestUtils.getLongParameter(request, "id");
      response.setContentType("APPLICATION/OCTET-STREAM");
      response.setHeader("Content-Disposition", "attachment;   filename=\"lircd.conf\"");
      FileCopyUtils.copy(remoteSectionService.exportStream(id), response.getOutputStream());
      return null;
   }

   public void setRemoteSectionService(RemoteSectionService remoteSectionService) {
      this.remoteSectionService = remoteSectionService;
   }

}
