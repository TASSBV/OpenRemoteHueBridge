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
package org.openremote.beehive.api.service;

import java.util.Date;

import org.openremote.beehive.domain.SyncHistory;

/**
 * The Interface SyncHistoryService.
 * 
 * @author Tomsky
 */
public interface SyncHistoryService {
   
   /**
    * Gets the latest.
    * 
    * @return the latest
    */
   SyncHistory getLatest();
   
   /**
    * Save.
    * 
    * @param syncHistory the sync history
    */
   void save(SyncHistory syncHistory);
   
   /**
    * Update.
    * 
    * @param status the status
    * @param endTime the end time
    */
   void update(String status, Date endTime);
   
   /**
    * Gets the latest by type.
    * 
    * @param type the type
    * 
    * @return the latest by type
    */
   SyncHistory getLatestByType(String type);
   
   /**
    * Gets the last sync by type.
    * 
    * @param type the type
    * 
    * @return the last sync by type
    */
   SyncHistory getLastSyncByType(String type);
}
