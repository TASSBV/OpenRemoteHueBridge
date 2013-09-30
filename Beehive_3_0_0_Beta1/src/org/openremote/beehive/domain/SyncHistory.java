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
package org.openremote.beehive.domain;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openremote.beehive.utils.DateUtil;

/**
 * @author Tomsky
 *
 */
@Entity
@Table(name = "sync_history")
@SuppressWarnings("serial")
public class SyncHistory extends BusinessEntity {
   private Date startTime;
   private Date endTime;
   private String type;
   private String status;
   private String logPath;
   
   @Column(name = "start_time")
   public Date getStartTime() {
      return startTime;
   }
   @Column(name = "end_time")
   public Date getEndTime() {
      return endTime;
   }
   @Column(nullable = false)
   public String getType() {
      return type;
   }
   @Column(nullable = false)
   public String getStatus() {
      return status;
   }
   @Column(name = "log_path")
   public String getLogPath() {
      return logPath;
   }

   public void setStartTime(Date startTime) {
      this.startTime = startTime;
   }
   
   @Transient
   public String getStartDate() {
      return DateUtil.getDefaultFormat(startTime);
   }
   
   
   public void setEndTime(Date endTime) {
      this.endTime = endTime;
   }
   
   public void setType(String type) {
      this.type = type;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public void setLogPath(String logPath) {
      this.logPath = logPath;
   }

   public SyncHistory() {
      // TODO Auto-generated constructor stub
   }
}
