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

public enum VeraCategory {
   Unknown(0),
   Interface(1),
   DimmableLight(2),
   Switch(3),
   SecuritySensor(4),
   Thermostat(5),
   Camera(6),
   DoorLock(7),
   Window(8),
   SceneController(14),
   AV(15),
   HumiditySensor(16),
   TemperatureSensor(17),
   PowerMeter(21),
   AlarmPanel(22),
   AlarmPartition(23);
 
   public final int id;
   
   private static final Map<Integer, VeraCategory> intToTypeMap = new HashMap<Integer, VeraCategory>();
   static {
       for (VeraCategory type : VeraCategory.values()) {
           intToTypeMap.put(type.id, type);
       }
   }

   public static VeraCategory fromInt(int i) {
      VeraCategory type = intToTypeMap.get(Integer.valueOf(i));
       if (type == null) 
           return VeraCategory.Unknown;
       return type;
   }
   
   VeraCategory(int id) {
      this.id = id;
    }
}
