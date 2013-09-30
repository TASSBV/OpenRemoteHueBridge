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

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.beehive.Constant;
import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.api.service.SVNDelegateService;
import org.openremote.beehive.exception.SVNException;
import org.openremote.beehive.repo.DiffResult;
import org.openremote.beehive.repo.LogMessage;
import org.openremote.beehive.repo.DiffResult.Line;
import org.openremote.beehive.repo.DiffStatus.Element;
import org.openremote.beehive.utils.HighlightUtil;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Tomsky
 * 
 */
public class LIRCRevisionChangesController extends LIRController {
   private SVNDelegateService svnDelegateService;
   private ModelService modelService;
   private String indexView;
   private String changeView;

   public void setIndexView(String indexView) {
      this.indexView = indexView;
   }

   public void setSvnDelegateService(SVNDelegateService svnDelegateService) {
      this.svnDelegateService = svnDelegateService;
   }

   public void setChangeView(String changeView) {
      this.changeView = changeView;
   }

   public void setModelService(ModelService modelService) {
      this.modelService = modelService;
   }

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
      ModelAndView mav = new ModelAndView(indexView);
      super.addStatus(mav);
      String showAll = request.getParameter("showAll");
      LogMessage headMessage = svnDelegateService.getHeadLog(Constant.ROOT_PATH);
      mav.addObject("headMessage", headMessage);
      request.getSession().setAttribute("headRevision", headMessage.getRevision().toString());
      if (svnDelegateService.isBlankSVN()) {
         request.setAttribute("isBlankSVN", true);
      } else {
         request.setAttribute("isBlankSVN", false);
      }
      List<Element> ds = svnDelegateService.getDiffStatus(Constant.ROOT_PATH);
      int diffSize = ds.size();
      mav.addObject("diffSize", diffSize);

      if ("true".equals(showAll)) {
         mav.addObject("showAll", true);
         mav.addObject("diffStatus", ds);
      } else if (diffSize > 50) {
         mav.addObject("diffStatus", ds.subList(0, 50));
      } else {
         mav.addObject("diffStatus", ds);
      }

      return mav;
   }

   /**
    * Show one file's difference between workCopy with svnrepo
    * 
    * @param request
    *           HttpServletRequest
    * @param response
    *           HttpServletResponse
    * @return ModelAndView
    * @throws ServletRequestBindingException
    *            Exception occured when missing path or action parameter
    */
   public ModelAndView change(HttpServletRequest request, HttpServletResponse response)
         throws ServletRequestBindingException {
      ModelAndView mav = new ModelAndView(changeView);
      String path = ServletRequestUtils.getRequiredStringParameter(request, "path");
      String action = ServletRequestUtils.getRequiredStringParameter(request, "action");
      if (!"UNVERSIONED".equals(action) && !"ADDED".equals(action)) {
         System.out.println(action);
         LogMessage repoMessage = svnDelegateService.getHeadLog(path);
         mav.addObject("repoMessage", repoMessage);
      }
      mav.addObject("breadcrumbPath", path);
      mav.addObject("isFile", modelService.isFile(path));
      mav.addObject("action", action);
      DiffResult dr = svnDelegateService.diff(path);
      List<Line> leftLines = dr.getLeft();
      List<Line> rightLines = dr.getRight();
      HighlightUtil.highlightDiffLines(leftLines);
      HighlightUtil.highlightDiffLines(rightLines);
      mav.addObject("leftLines", leftLines);
      mav.addObject("rightLines", rightLines);
      mav.addObject("changeCount", dr.getChangeCount());
      return mav;
   }

   /**
    * @param request
    *           HttpServletRequest
    * @param response
    *           HttpServletResponse
    * @return ModelAndView
    * @throws SVNException
    *            Exception occured when modelService.update() failed
    */
   public ModelAndView commit(HttpServletRequest request, HttpServletResponse response) {
      String[] items = request.getParameterValues("items");
      String comment = request.getParameter("comment");
      String commitAll = request.getParameter("commitAll");
      if ("true".equals(commitAll)) {
         items = svnDelegateService.getDiffPaths(Constant.ROOT_PATH);
         if (items.length == 0) {
            items = null;
         }
      }
      if (items != null) {
         try {
            svnDelegateService.commit(items, comment, "admin");
         } catch (SVNException e) {
            syncHistoryService.update("faild", new Date());
            throw e;
         }
         syncHistoryService.update("success", new Date());
      }
      return null;
   }
}
