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
package org.openremote.controller.component;

import org.openremote.controller.protocol.EventProducer;
import org.openremote.controller.protocol.Event;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.model.event.Level;

/**
 * TODO : ORCJAVA-104
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class LevelSensor extends RangeSensor
{

  /**
   * TODO
   *
   * @param name
   * @param sensorID
   * @param producer
   */
  public LevelSensor(String name, int sensorID, StatusCache cache, EventProducer producer)
  {
    super(name, sensorID, EnumSensorType.LEVEL, producer, cache, 0, 100, false);
  }


  // RangeSensor Overrides ------------------------------------------------------------------------

  @Override public Event processEvent(String value)
  {
    try
    {
      return new Level(getSensorID(), getName(), new Integer(value.trim()));
    }

    catch (NumberFormatException e)
    {
      if (!isUnknownSensorValue(value))
      {
        log.warn(
            "Sensor ''{0}'' (ID = {1}) is LEVEL type but produced a value that is not " +
            " an integer : ''{2}''", getName(), getSensorID(), value
        );
      }

      return new UnknownEvent(this);
    }
  }

  // Object Overrides -----------------------------------------------------------------------------

  /**
   * String representation of a range sensor. Returns sensor's name, ID, and range minimum and
   * maximum values.
   *
   * @return  this sensor as a string
   */
  @Override public String toString()
  {
    return
        "Level Sensor (Name = '" + getName() + "', ID = '" + getSensorID() +
        "', Min: " + getMinValue() + ", Max: " + getMaxValue() +")";
  }

}

