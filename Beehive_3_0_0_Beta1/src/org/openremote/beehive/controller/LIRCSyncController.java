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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.beehive.api.service.impl.WebscraperThread;
import org.openremote.beehive.domain.SyncHistory;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Tomsky
 *
 */
public class LIRCSyncController extends LIRController {
   private String indexView;
   
   public void setIndexView(String indexView) {
      this.indexView = indexView;
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
      SyncHistory lastUpdate = syncHistoryService.getLastSyncByType("update");
      if(lastUpdate != null){
         mav.addObject("lastUpdate", lastUpdate);
      }
      return mav;
   }
   
   /**
    * Update all the LIRC configuration files which in workCopy with http://lirc.sourceforge.net/remotes/.
    * 
    * Start the new Thread to release the long connection from ajax call. 
    * 
    * @param request
    * @param response
    * @return
    * @throws Exception 
    */
   public ModelAndView update(HttpServletRequest request, HttpServletResponse response) throws Exception {
      new Thread(new WebscraperThread()).start();
      return null;
   }
}
