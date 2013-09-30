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

import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.api.service.ProgressService;
import org.openremote.beehive.file.Progress;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * Controller for importing the LIRC configuration files from http://lirc.sourceforge.net/remotes/, and exporting a
 * specified LIRC configuration file.
 * 
 * @author Dan 2009-2-6
 */
public class ProgressController extends MultiActionController {
   
   /** The progress service. */
   private ProgressService progressService;
   private ModelService modelService;
   /**
    * Sets the progress service.
    * 
    * @param progressService the new progress service
    */
   public void setProgressService(ProgressService progressService) {
      this.progressService = progressService;
   }
   
   public void setModelService(ModelService modelService) {
      this.modelService = modelService;
   }


   /**
    * Scraps and imports all the LIRC configuration files.
    * 
    * @param request HttpServletRequest
    * @param response HttpServletResponse
    * 
    * @return null
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    */
   public void getProgress(HttpServletRequest request, HttpServletResponse response) throws IOException {
      Long modelCount = 1L;
      String type = request.getParameter("type");
      if("update".equals(type)){
         modelCount =  (Long) request.getSession().getAttribute("modelCount");
         if(modelCount == null){
            modelCount = Long.valueOf(modelService.count());
            request.getSession().setAttribute("modelCount", modelCount);
         }
      }
      Progress progress = progressService.getProgress(type,modelCount);
      response.getWriter().print(progress.toJSON());
   }

}
