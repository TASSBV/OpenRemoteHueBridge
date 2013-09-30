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
package org.openremote.beehive.file;

import java.util.HashMap;

import org.json.JSONObject;

/**
 * The Class Progress.
 * 
 * @author Tomsky
 */
public class Progress {
   
   /** The progress. */
   private String message;
   
   /** The status. */
   private String status;
   
   /** The percent. */
   private double percent;
   
   public Progress() {
      status = "";
      message = "";
   }
   /**
    * Gets the progress.
    * 
    * @return the progress
    */
   public String getMessage() {
      return message;
   }
   
   /**
    * Gets the status.
    * 
    * @return the status
    */
   public String getStatus() {
      return status;
   }
   
   /**
    * Gets the percent.
    * 
    * @return the percent
    */
   public String getPercentString() {
      percent = percent > 1 ? 1 : percent;
      return String.valueOf(percent * 100).replaceAll("\\.\\d+", "") + "%";
   }
   
   /**
    * Sets the progress.
    * 
    * @param progress the new progress
    */
   public void setMessage(String message) {
      this.message = message;
   }
   
   /**
    * Sets the status.
    * 
    * @param status the new status
    */
   public void setStatus(String status) {
      this.status = status;
   }
   
   /**
    * Sets the percent.
    * 
    * @param percent the new percent
    */
   public void setPercent(double percent) {
      this.percent = percent;
   }
   
   public JSONObject toJSON(){
      HashMap<String, String> map = new HashMap<String, String>();
      map.put("status", status);
      map.put("data", message);
      map.put("percent", this.getPercentString());
      JSONObject json = new JSONObject(map);
      return json;
   }
}
