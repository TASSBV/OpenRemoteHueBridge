/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2013, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.vera.model;

import java.util.HashMap;
import java.util.Map;

import org.jdom.Element;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.vera.VeraClient;
import org.openremote.controller.protocol.vera.VeraCmd;
import org.openremote.controller.protocol.vera.VeraCommandBuilder;
import org.openremote.controller.utils.Logger;

public abstract class VeraDevice {

   // Class Members --------------------------------------------------------------------------------
   private final static Logger log = Logger.getLogger(VeraCommandBuilder.VERA_PROTOCOL_LOG_CATEGORY);
   
   // Instance Fields ------------------------------------------------------------------------------
   protected VeraClient client;
   private String name;
   protected int id;
   private VeraCategory category;
   private String comment;
   private Integer batteryLevel;
      
   protected Map<VeraCmd, Sensor> attachedSensors = new HashMap<VeraCmd, Sensor>();
   protected Map<String, Sensor> attachedGenericSensors = new HashMap<String, Sensor>();
   
   // Constructors ----------------------------------------------------------------------------------
   public VeraDevice(VeraCategory category, int id, String name, VeraClient client) {
      this.category = category;
      this.id = id;
      this.name = name;
      this.client = client;
      log.debug("Vera device - " + getClass().getSimpleName() + "- with id: " + id + " and category: " + category  + " created.");
   }

   // Methods ------------------------------------------------------------------------------
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public VeraCategory getCategory() {
      return category;
   }

   public void setCategory(VeraCategory category) {
      this.category = category;
   }

   public String getComment() {
      return comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public int getBatteryLevel() {
      return batteryLevel;
   }

   public void setBatteryLevel(int batteryLevel) {
      this.batteryLevel = batteryLevel;
   }
   
   public void addSensor(VeraCmd cmd, Sensor sensor)
   {
     this.attachedSensors.put(cmd, sensor);
     this.client.requestFullStatus();
   }
   
   public void addGenericSensor(String statusAttribute, Sensor sensor)
   {
     this.attachedGenericSensors.put(statusAttribute, sensor);
     this.client.requestFullStatus();
   }
   

   public void removeSensor(Sensor sensor)
   {
     for (Map.Entry<VeraCmd, Sensor> mapEntry : this.attachedSensors.entrySet()) {
        if (mapEntry.getValue().equals(sensor)) {
           this.attachedSensors.remove(mapEntry.getKey());
        }
     }
     for (Map.Entry<String, Sensor> mapEntry : this.attachedGenericSensors.entrySet()) {
        if (mapEntry.getValue().equals(sensor)) {
           this.attachedGenericSensors.remove(mapEntry.getKey());
        }
     }
   }
   
   protected VeraClient getClient() {
      return this.client;
   }
   
   private void updateSensors() {
      if ((attachedSensors.get(VeraCmd.GET_BATTERY_LEVEL) != null) && (batteryLevel != null)) {
         attachedSensors.get(VeraCmd.GET_BATTERY_LEVEL).update(batteryLevel.toString());
      }
      updateDeviceSpecificSensors();
   }
   
   protected abstract void updateDeviceSpecificSensors();
   protected abstract void updateDeviceSpecificStatus(Element element);

   public void updateStatus(Element element) {
      if (element.getAttributeValue("comment") != null) {
         this.comment = element.getAttributeValue("comment");
      }
      if (element.getAttributeValue("batterylevel") != null) {
         this.batteryLevel = Integer.parseInt(element.getAttributeValue("batterylevel"));
      }
      
      for (String statusAttributeName : attachedGenericSensors.keySet()) {
         if (element.getAttributeValue(statusAttributeName) != null) {
            String value = element.getAttributeValue(statusAttributeName);
            log.debug("Updated generic sensor field '" + statusAttributeName + "' with value: " + value);
            attachedGenericSensors.get(statusAttributeName).update(value);
         } 
      }
      
      updateDeviceSpecificStatus(element);
      updateSensors();
   }

   public void executeGenericAction(String serviceId, String action, String variable, String paramValue) {
      StringBuffer cmdUrl = new StringBuffer();
      cmdUrl.append("http://");
      cmdUrl.append(client.getAddress());
      cmdUrl.append(":3480/data_request?id=lu_action&output_format=xml&DeviceNum=");
      cmdUrl.append(id);
      cmdUrl.append("&serviceId=" + serviceId + "&action=" + action);
      if ((variable != null) && (paramValue !=null)) {
         cmdUrl.append("&" + variable + "=" + paramValue);
      }
      getClient().sendCommand(cmdUrl.toString());
   }
   
}
