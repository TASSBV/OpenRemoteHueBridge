/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.component;

/**
 * 
 * Sensor Type is used to define the sensor data type comes back with 'rest/status' or 'rest/polling' API.
 * 
 * @author Handy.Wang 2010-03-19
 *
 */
public enum EnumSensorType {
   
   SWITCH,  //sensor data is either 'on' or 'off' as a string.
   LEVEL,   //sensor data is a percent number, it expects an integer value as a string in the range of [0-100]
   RANGE,   //sensor data is a float number between min value and max value, it expects a float value as a string in the range of [min-max]
   COLOR,   //sensor data is color string.
   CUSTOM;  //sensor data is custom enumeration string.
   
   @Override
   public String toString() {
      return super.toString().toLowerCase();
   }
   
//   public static EnumSensorType enumValueOf(String typePropertyValueOfSensor) {
//      return Enum.valueOf(EnumSensorType.class, typePropertyValueOfSensor.toUpperCase());
//   }
}
