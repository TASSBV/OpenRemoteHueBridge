/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.openremote.controller.Constants;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.config.ControllerXMLChangedException;
import org.openremote.controller.exception.NoSuchComponentException;
import org.openremote.controller.service.StatusPollingService;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.statuscache.ChangedStatusRecord;
import org.openremote.controller.statuscache.ChangedStatusTable;
import org.openremote.controller.statuscache.PollingData;
import org.openremote.controller.statuscache.StatusCache;

/**
 * TODO :
 *  - this needs a thorough review and testing, much of the code doesn't make sense when read
 *    (ORCJAVA-165 -- http://jira.openremote.org/browse/ORCJAVA-165)
 * 
 * @author Handy.Wang 2009-10-21
 */
public class StatusPollingServiceImpl implements StatusPollingService {

  // TODO : use one of the controller runtime categories
  private final static Logger logger = Logger.getLogger(Constants.RUNTIME_STATECACHE_LOG_CATEGORY);

   private ChangedStatusTable changedStatusTable;
   private Deployer deployer;
   private StatusCache statusCache;


   /* (non-Javadoc)
    * @see org.openremote.controller.service.ControlStatusPollingService#querySkipState(java.lang.String)
    */
   /**
    * This operation is :<br />
    * 1) Query changed status record from changedStatusTable. if not found insert a new record with polling request, <br /> 
    * if found check whether the statuses of polling ids had changed.
    * 2) Check whether the statuses of polling ids had changed. if changed return the changed statuses, if not wait the changeStatusRecord.
    * 3) Waiting the status change until status changed or timeout. if had waited the chanted status then return changed status and <br />
    * changedSensorIds column of ChangedStatusRecord. if timeout throws 504 exception.
    */
   @Override
   public String queryChangedState(String deviceID, String unParsedSensorIDs) {
      if (deployer.isPaused()) {
         throw new ControllerXMLChangedException("The content of controller.xml had changed.");
      }
      
      logger.info("Querying changed state from ChangedStatus table...");
      String skipState = "";
      String[] sensorIDs = (unParsedSensorIDs == null || "".equals(unParsedSensorIDs)) ? new String[]{} : unParsedSensorIDs.split(Constants.STATUS_POLLING_SENSOR_IDS_SEPARATOR);
      
      Set<Integer> pollingSensorIDs = new TreeSet<Integer>();
      for (String pollingSensorID : sensorIDs) {
         try {
            pollingSensorIDs.add(Integer.parseInt(pollingSensorID));
         } catch (NumberFormatException e) {
            throw new NoSuchComponentException("The sensor id '" + pollingSensorID + "' should be digit", e);
         }
      }
      String orderedSensorIDs = pollingSensorIDs.toString();
      String changedStatusRecordKey = deviceID+"-"+orderedSensorIDs;

      ChangedStatusRecord changedStateRecord = changedStatusTable.query(changedStatusRecordKey);
      String tempInfo = "Found: [device => " + deviceID + ", sensorIDs => " + orderedSensorIDs + "] in ChangedStatus table.";
      logger.info(changedStateRecord == null ? "Not " + tempInfo : tempInfo);
      
      if (changedStateRecord == null) {
         changedStateRecord = new ChangedStatusRecord(changedStatusRecordKey, pollingSensorIDs);
         changedStatusTable.insert(changedStateRecord);
      }
      if (changedStateRecord.getStatusChangedSensorIDs() != null && changedStateRecord.getStatusChangedSensorIDs().size() > 0) {
         logger.info("Got the skipped sensor ids of statuses in " + changedStateRecord);
      }
      
      synchronized (changedStateRecord) {
         boolean willTimeout = false;
         while (changedStateRecord.getStatusChangedSensorIDs() == null || changedStateRecord.getStatusChangedSensorIDs().size() == 0) {
            if (willTimeout) {
               logger.info("Had timeout for waiting status change.");
               return Constants.SERVER_RESPONSE_TIME_OUT;
            }
            try {
               logger.info(changedStateRecord + "Waiting...");
               changedStateRecord.wait(50000);
               
               if (deployer.isPaused()) {
                  throw new ControllerXMLChangedException("The content of controller.xml had changed.");
               }
               
               willTimeout = true;
            } catch (InterruptedException e) {
               e.printStackTrace();
               return Constants.SERVER_RESPONSE_TIME_OUT;
            }
         }
         if (willTimeout) {
            logger.info("Had waited the skipped sensor ids of statuses in " + changedStateRecord);
         }
         skipState = queryChangedStatusesFromCachedStatusTable(changedStateRecord.getStatusChangedSensorIDs());
         changedStatusTable.resetChangedStatusIDs(changedStatusRecordKey);
      }
      
      return skipState;
   }
   
   /**
    * Query the changed statuses from CachedStatusTable with changedSensorIDs of ChangedStatusRecord. 
    */
   private String queryChangedStatusesFromCachedStatusTable(Set<Integer> statusChangedIDs) {
      logger.info("Querying changed data from StatusCache...");
      PollingData pollingData = new PollingData(statusChangedIDs);
      Map<Integer, String> changedStatuses = statusCache.queryStatus(pollingData.getSensorIDs());
      pollingData.setChangedStatuses(changedStatuses);
      logger.info("Have queried changed data from StatusCache.");
      return composePollingResult(pollingData);
   }
   
   /**
    * compose the changed statuses into xml-formatted data.
    */
   private String composePollingResult(PollingData pollingResult) {
      StringBuffer sb = new StringBuffer();
      sb.append(Constants.STATUS_XML_HEADER);
      
      Map<Integer, String> changedStatuses = pollingResult.getChangedStatuses();
      if (changedStatuses == null) {
         return "";
      }
      Set<Integer> sensorIDs = changedStatuses.keySet();
      for (Integer sensorID : sensorIDs) {
          sb.append("<" + Constants.STATUS_XML_STATUS_RESULT_ELEMENT_NAME + " " + Constants.STATUS_XML_STATUS_RESULT_ELEMENT_SENSOR_IDENTITY + "=\"" + sensorID + "\">");
          sb.append(changedStatuses.get(sensorID));
          sb.append("</" + Constants.STATUS_XML_STATUS_RESULT_ELEMENT_NAME + ">\n");
          sb.append("\n");
      }
      sb.append(Constants.STATUS_XML_TAIL);
      return sb.toString();
   }


  // Service Dependencies -------------------------------------------------------------------------

   public void setChangedStatusTable(ChangedStatusTable changedStatusTable) {
      this.changedStatusTable = changedStatusTable;
   }

  public void setDeployer(Deployer deployer)
  {
    this.deployer = deployer;
  }

  public void setStatusCache(StatusCache statusCache) {
     this.statusCache = statusCache;
  }

}
