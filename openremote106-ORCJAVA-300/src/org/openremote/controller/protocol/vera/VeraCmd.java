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
package org.openremote.controller.protocol.vera;

/**
 * These are all allowed commands for the Vera protocol
 */
public enum VeraCmd {
   
   GET_COMMENT,
   GET_BATTERY_LEVEL,
   ON,
   OFF,
   GET_STATUS,
   SET_LEVEL,
   SET_HEAT_SETPOINT,
   GET_HEAT_SETPOINT,
   GET_LEVEL,
   GET_TEMPERATURE,
   GET_HUMIDITY,
   GET_WATTS,
   GET_CONSUMPTION,
   GET_ARMED,
   GET_TRIPPED,
   GENERIC_ACTION,
   GENERIC_STATUS;

   /**
    * All: getComment, getBatteryLevel
    * Switch: on, off, getStatus
    * Dimmer: on, off, getStatus, setLevel, getLevel
    * Temp.Sensor: getTemperature
    * Humidity.Sensor: getHumidity
    * Thermostat: 
    * PowerMeter: getWatts, getConsumption
    * 
    * 
    */

   private VeraCmd() {
      this.value = null;
   }

   private VeraCmd(String value) {
      this.value = value;
   }

   private VeraCmd(VeraCmd otherKey) {
      this(otherKey.getValue());
   }

   private String value;

   public String getValue() {
      if (value == null) {
         return this.name();
      }
      return value;
   }
   

}