/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.modeler.domain.component;

import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.shared.dto.DTO;
import org.openremote.modeler.shared.dto.SensorWithInfoDTO;
/**
 * This interface is primarily used for get a Sensor just by judging whether the instance has implemented it, without knowing the specific class.
 * This interface is very convenient for export sensors to a xml file by velocity 
 * @author Javen
 *
 */
public interface SensorOwner {
   Sensor getSensor();
   void setSensor(Sensor sensor);
   
   SensorWithInfoDTO getSensorDTO();
   void setSensorDTO(SensorWithInfoDTO sensorDTO);
}
