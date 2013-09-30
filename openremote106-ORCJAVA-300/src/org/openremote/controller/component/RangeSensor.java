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

import java.util.Map;
import java.util.HashMap;

import org.openremote.controller.protocol.EventProducer;
import org.openremote.controller.protocol.Event;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.event.Range;
import org.openremote.controller.statuscache.StatusCache;

/**
 * TODO : ORCJAVA-105
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class RangeSensor extends Sensor
{

  private static Map<String, String> createDefaultProperties(boolean setDefaultProps, int min, int max)
  {
    if (!setDefaultProps)
    {
      return new HashMap<String, String>(0);
    }
    
    HashMap<String, String> props = new HashMap<String, String>(2);

    // TODO : these are legacy

    props.put(Sensor.RANGE_MIN_STATE, Integer.toString(min));
    props.put(Sensor.RANGE_MAX_STATE, Integer.toString(max));

    return props;
  }



  private int min;
  private int max;

  

  public RangeSensor(String name, int sensorID, StatusCache cache, EventProducer eventProducer, int min, int max)
  {
    this(name, sensorID, EnumSensorType.RANGE, eventProducer, cache, min, max, true);

  }

  protected RangeSensor(String name, int sensorID, EnumSensorType type,
                        EventProducer producer, StatusCache cache,
                        int min, int max, boolean setDefaultProps)
  {
    super(name, sensorID, cache, producer, createDefaultProperties(setDefaultProps, min, max), type);

    if (min > max)
      throw new IllegalArgumentException("min " + min + " is larger than max " + max);

    this.min = min;
    this.max = max;
  }


  /**
   * TODO
   *
   * @return range max state value
   */
  public int getMaxValue()
  {
    return max;
  }


  /**
   * TODO
   *
   * @return range min state value
   */
  public int getMinValue()
  {
    return min;
  }


  // Sensor Implementation ------------------------------------------------------------------------

  @Override protected Event processEvent(String value)
  {
    try
    {
      return new Range(getSensorID(), getName(), new Integer(value.trim()), getMinValue(), getMaxValue());
    }

    catch (NumberFormatException exception)
    {
      if (!isUnknownSensorValue(value))
      {
        log.warn(
            "Sensor ''{0}'' (ID = {1}) is RANGE type but produced a value that is not " +
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
        "Range Sensor (Name = '" + getName() + "', ID = '" + getSensorID() +
        "', Min: " + getMinValue() + ", Max: " + getMaxValue() +")";
  }


}

