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
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.beehive.Configuration;
import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.file.LircConfFileScraper;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * Controller for importing the LIRC configuration files from http://lirc.sourceforge.net/remotes/, and exporting a
 * specified LIRC configuration file.
 * 
 * @author Dan 2009-2-6
 * 
 */
public class ModelController extends MultiActionController {

   private ModelService modelService;
   private Configuration configuration;

   /**
    * Scraps and imports all the LIRC configuration files.
    * 
    * @param request
    *           HttpServletRequest
    * @param response
    *           HttpServletResponse
    * @return null
    * @throws IOException
    */
   public ModelAndView scrap(HttpServletRequest request, HttpServletResponse response) throws IOException {
      LircConfFileScraper.scrapDir(configuration.getWorkCopyDir());
      return null;
   }

   /**
    * Views the content text of a LIRC configuration file
    * 
    * @param request
    *           HttpServletRequest
    * @param response
    *           HttpServletResponse
    * @return null
    * @throws IOException
    * @throws ServletRequestBindingException
    */
   public ModelAndView view(HttpServletRequest request, HttpServletResponse response) throws IOException,
         ServletRequestBindingException {
      long id = ServletRequestUtils.getLongParameter(request, "id");
      PrintWriter out = response.getWriter();
      out.print(modelService.exportText(id));
      return null;
   }

   /**
    * Exports a LIRC configuration file without disk writes.
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
      response.setHeader("Content-Disposition", "attachment;filename=\"lircd.conf\"");
      FileCopyUtils.copy(modelService.exportStream(id), response.getOutputStream());
      return null;
   }

   public ModelService getModelService() {
      return modelService;
   }

   public void setModelService(ModelService modelService) {
      this.modelService = modelService;
   }

   public Configuration getConfiguration() {
      return configuration;
   }

   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

}
