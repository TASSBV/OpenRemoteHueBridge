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
package org.openremote.controller.statuscache;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * TODO:
 *
 *  - See relevant task ORCJAVA-208 (http://jira.openremote.org/browse/ORCJAVA-208)
 *    This should be part of internal implementation of StatusCache
 *
 *  - See relevant task ORCJAVA-165 (http://jira.openremote.org/browse/ORCJAVA-165)
 *
 *
 *
 * A changed status record.
 *
 * This Record is used to record the skipped changed statuses and waited changed statuses .
 * 
 * @author Handy.Wang 2009-10-23
 */
public class ChangedStatusRecord
{
   
  /** A logical identity of panel+sensorIDs */
  private String key;

  /** The ids a polling request contains */
  private Set<Integer> pollingSensorIDs = new TreeSet<Integer>();

  /** The ids whose status had changed in the statusChangedSensorIDs */
  private Set<Integer> statusChangedSensorIDs = new HashSet<Integer>(3);



  // Constructors ---------------------------------------------------------------------------------



  public ChangedStatusRecord(String key, Set<Integer> pollingSensorIDs)
  {
    this.key = key;
    this.pollingSensorIDs = pollingSensorIDs;
  }




  public String getRecordKey()
  {
    return key;
  }

  public Set<Integer> getPollingSensorIDs()
  {
   return pollingSensorIDs;
  }


  public Set<Integer> getStatusChangedSensorIDs()
  {
     return statusChangedSensorIDs;
  }

  public void setStatusChangedSensorIDs(Set<Integer> statusChangedSensorIDs)
  {
     this.statusChangedSensorIDs = statusChangedSensorIDs;
  }



  @Override public boolean equals(Object obj)
  {
    if (obj == null || !(obj instanceof ChangedStatusRecord))
    {
       return false;
    }

    ChangedStatusRecord timeoutRecord = (ChangedStatusRecord) obj;

    if (timeoutRecord.getRecordKey().equals(getRecordKey())) {
       return true;
    } else {
       return false;
    }
  }

  @Override
   public int hashCode() {
      return key.hashCode();
   }

  @Override public String toString()
  {
     StringBuffer sb = new StringBuffer();

     sb.append("ChangedStatusRecord:" + key);
     sb.append(" sensorID:" + this.pollingSensorIDs.toString());
     sb.append(" statusChangedSensorID:" + this.statusChangedSensorIDs);
     return sb.toString();
  }



}
