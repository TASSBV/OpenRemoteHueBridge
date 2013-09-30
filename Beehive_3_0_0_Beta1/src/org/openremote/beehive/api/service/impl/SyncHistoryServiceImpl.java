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
package org.openremote.beehive.api.service.impl;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openremote.beehive.api.service.SyncHistoryService;
import org.openremote.beehive.domain.SyncHistory;

/**
 * The Class SyncHistoryServiceImpl.
 * 
 * @author Tomsky
 */
public class SyncHistoryServiceImpl extends BaseAbstractService<SyncHistory> implements SyncHistoryService {

   /**
    * {@inheritDoc}
    */
   public SyncHistory getLatest() {
      return genericDAO.getByMaxId(SyncHistory.class);
   }

   /**
    * {@inheritDoc}
    */
   public void save(SyncHistory syncHistory) {
      genericDAO.saveOrUpdate(syncHistory);
   }

   /**
    * {@inheritDoc}
    */
   public void update(String status, Date endTime) {
      SyncHistory dbSyncHistory = getLatest();
      dbSyncHistory.setStatus(status);
      dbSyncHistory.setEndTime(endTime);
      genericDAO.merge(dbSyncHistory);
   }
   
   /**
    * {@inheritDoc}
    */
   public SyncHistory getLatestByType(String type) {
      DetachedCriteria criteria = DetachedCriteria.forClass(SyncHistory.class);
      criteria.addOrder(Order.desc("oid"));
      criteria.add(Restrictions.eq("type", type));
      return genericDAO.findOneByDetachedCriteria(criteria);
   }

   /**
    * {@inheritDoc}
    */
   public SyncHistory getLastSyncByType(String type) {
      DetachedCriteria criteria = DetachedCriteria.forClass(SyncHistory.class);
      criteria.addOrder(Order.desc("oid"));
      criteria.add(Restrictions.eq("type", type));
      criteria.add(Restrictions.ne("status", "running"));
      return genericDAO.findOneByDetachedCriteria(criteria);
   }
   
   
}
