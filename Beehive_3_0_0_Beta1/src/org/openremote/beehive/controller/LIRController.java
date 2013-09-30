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

import org.openremote.beehive.api.service.SyncHistoryService;
import org.openremote.beehive.domain.SyncHistory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

// TODO: Auto-generated Javadoc
/**
 * The Class LIRController.
 * 
 * @author Tomsky
 */
public class LIRController extends MultiActionController {
   
   /** The sync history service. */
   protected SyncHistoryService syncHistoryService;
   
   /**
    * Sets the sync history service.
    * 
    * @param syncHistoryService the new sync history service
    */
   public void setSyncHistoryService(SyncHistoryService syncHistoryService) {
      this.syncHistoryService = syncHistoryService;
   }

   /**
    * Adds the status of operate ,such as commit or update.
    * 
    * @param mav the mav
    */
   public void addStatus(ModelAndView mav) {
      SyncHistory syncHistory = syncHistoryService.getLatest();
      if(syncHistory != null){
         mav.addObject(syncHistory.getType(), syncHistory.getStatus());
      }
   }

}
