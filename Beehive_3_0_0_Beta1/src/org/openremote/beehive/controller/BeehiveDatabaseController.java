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

import org.openremote.beehive.api.dto.RemoteSectionDTO;
import org.openremote.beehive.api.service.CodeService;
import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.api.service.RemoteOptionService;
import org.openremote.beehive.api.service.RemoteSectionService;
import org.openremote.beehive.api.service.VendorService;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * Controller for Beehive database UI Interface User: allenwei Date: 2009-2-11 Time: 17:27:22
 */
public class BeehiveDatabaseController extends MultiActionController {

   private VendorService vendorService;
   private ModelService modelService;
   private RemoteSectionService remoteSectionService;
   private RemoteOptionService remoteOptionService;
   private CodeService codeService;

   public void setVendorService(VendorService vendorService) {
      this.vendorService = vendorService;
   }

   public void setModelService(ModelService modelService) {
      this.modelService = modelService;
   }

   public void setRemoteSectionService(RemoteSectionService remoteSectionService) {
      this.remoteSectionService = remoteSectionService;
   }

   public void setRemoteOptionService(RemoteOptionService remoteOptionService) {
      this.remoteOptionService = remoteOptionService;
   }

   public void setCodeService(CodeService codeService) {
      this.codeService = codeService;
   }

   private String index;
   private String cmpSelectContainer;
   private String cmpLircDetails;

   /**
    * Default method in controller
    * 
    * @param request
    *           HttpServletRequest
    * @param response
    *           HttpServletResponse
    * @return ModelAndView
    */
   public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
      ModelAndView mav = new ModelAndView(index);
      mav.addObject("vendors", vendorService.loadAllVendors());
      return mav;
   }

   /**
    * Show cmpSelectContainer page rendor select element as Model list
    * 
    * @param request
    *           HttpServletRequest
    * @param response
    *           HttpServletResponse
    * @return ModelAndView
    * @throws ServletRequestBindingException
    *            Exception occured when missing vendor id parameter
    */
   public ModelAndView showModelSelect(HttpServletRequest request, HttpServletResponse response)
         throws ServletRequestBindingException {
      ModelAndView mav = new ModelAndView(cmpSelectContainer);
      String generateSelectTcagId = ServletRequestUtils.getRequiredStringParameter(request, "generateSelectTcagId");
      long vendorId = ServletRequestUtils.getRequiredLongParameter(request, "vendorId");
      mav.addObject("items", modelService.findModelsByVendorId(vendorId));
      mav.addObject("generateSelectTcagId", generateSelectTcagId);
      return mav;

   }

   /**
    * Show cmpSelectContainer page rendor select element as remoteSection list
    * 
    * @param request
    *           HttpServletRequest
    * @param response
    *           HttpServletResponse
    * @return ModelAndView
    * @throws ServletRequestBindingException
    *            Exception occured when missing model id parameter
    */
   public ModelAndView showRemoteSelect(HttpServletRequest request, HttpServletResponse response)
         throws ServletRequestBindingException {
      ModelAndView mav = new ModelAndView(cmpSelectContainer);
      String generateSelectTcagId = ServletRequestUtils.getRequiredStringParameter(request, "generateSelectTcagId");
      long modelId = ServletRequestUtils.getRequiredLongParameter(request, "modelId");
      mav.addObject("items", remoteSectionService.findByModelId(modelId));
      mav.addObject("generateSelectTcagId", generateSelectTcagId);
      return mav;
   }

   /**
    * Show LIRC details page according to Model
    * 
    * @param request
    *           HttpServletRequest
    * @param response
    *           HttpServletResponse
    * @return ModelAndView
    * @throws ServletRequestBindingException
    *            Exception occured when missing model id parameter
    */
   public ModelAndView showLircDetailByModel(HttpServletRequest request, HttpServletResponse response)
         throws ServletRequestBindingException {
      ModelAndView mav = new ModelAndView(cmpLircDetails);
      long modelId = ServletRequestUtils.getRequiredLongParameter(request, "modelId");
      RemoteSectionDTO remoteSectionDTO = remoteSectionService.loadFisrtRemoteSectionByModelId(modelId);
      mav.addObject("model", modelService.loadModelById(modelId));
      mav.addObject("section", remoteSectionDTO);
      mav.addObject("options", remoteOptionService.findByRemoteSectionId(remoteSectionDTO.getOid()));
      mav.addObject("codes", codeService.findByRemoteSectionId(remoteSectionDTO.getOid()));
      return mav;
   }

   /**
    * Show LIRC details page according to remoteSection
    * 
    * @param request
    *           HttpServletRequest
    * @param response
    *           HttpServletResponse
    * @return ModelAndView
    * @throws ServletRequestBindingException
    *            Exception occured when missing section id parameter
    */
   public ModelAndView showLircDetailByRemoteSection(HttpServletRequest request, HttpServletResponse response)
         throws ServletRequestBindingException {
      ModelAndView mav = new ModelAndView(cmpLircDetails);
      long sectionId = ServletRequestUtils.getRequiredLongParameter(request, "sectionId");
      mav.addObject("model", remoteSectionService.loadModelById(sectionId));
      mav.addObject("section", remoteSectionService.loadSectionById(sectionId));
      mav.addObject("options", remoteOptionService.findByRemoteSectionId(sectionId));
      mav.addObject("codes", codeService.findByRemoteSectionId(sectionId));
      return mav;
   }

   /**
    * Return if this model has multi-remoteSection
    * 
    * @param request
    *           HttpServletRequest
    * @param response
    *           HttpServletResponse
    * @return ModelAndView
    * @throws IOException
    *            IOException
    * @throws ServletRequestBindingException
    *            Exception occured when missing model id parameter
    */
   public ModelAndView isMutiSection(HttpServletRequest request, HttpServletResponse response) throws IOException,
         ServletRequestBindingException {
      long modelId = ServletRequestUtils.getRequiredLongParameter(request, "modelId");
      response.getWriter().print(remoteSectionService.findByModelId(modelId).size() > 1);
      return null;
   }

   public void setIndex(String index) {
      this.index = index;
   }

   public void setCmpSelectContainer(String cmpSelectContainer) {
      this.cmpSelectContainer = cmpSelectContainer;
   }

   public void setCmpLircDetails(String cmpLircDetails) {
      this.cmpLircDetails = cmpLircDetails;
   }
}
