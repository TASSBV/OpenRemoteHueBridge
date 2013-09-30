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
package org.openremote.controller.command;

import java.util.Map;

import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.protocol.EventProducer;


/**
 * Represents a device read command which is polled by its associated sensor.
 *
 * @deprecated This interface has been deprecated and should no longer be directly implemented.
 *             Instead, extend the {@link org.openremote.controller.protocol.ReadCommand}
 *             class instead which has an updated API to access the associated sensor.
 *             For legacy rea sons and to ease migration, <tt>ReadCommand</tt> still implements
 *             this interface.
 *
 * @author Handy.Wang 2009-10-15
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
@Deprecated public interface StatusCommand extends EventProducer
{

  /**
   *
   * @deprecated This interface has been deprecated altogether.
   */
   @Deprecated public final static String UNKNOWN_STATUS = "N/A";

   /**
    * Read raw status from device and return parsed status. <p>
    *
    * And you also can translate the raw status to readable string according to sensoryType and state map.
    * 
    * @param sensorType
    *           sensor type
    * @param sensorProperties
    *           state map, key is state name, value is the returned raw string related to the state. 
    *           NOTE: if sensor type is RANGE, this map only contains min/max states.
    * @return parsed status string
    *
    * @deprecated This interface has been deprecated altogether.
    */
   @Deprecated public String read(EnumSensorType sensorType, Map<String, String> sensorProperties);
   
    
}
