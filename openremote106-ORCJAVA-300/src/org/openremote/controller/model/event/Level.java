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
package org.openremote.controller.model.event;

import org.openremote.controller.utils.Logger;
import org.openremote.controller.Constants;

/**
 * Level events are integer based events where values have been limited to a range of
 * [0..100] <p>
 *
 * Level events are associated with sensors of 'level' type in controller.xml model. <p>
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Level extends Range
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * Log events into sensor log category.
   */
  private final static Logger log = Logger.getLogger(Constants.RUNTIME_SENSORS_LOG_CATEGORY);


  /**
   * Validate range value to [0..100].
   *
   * @param sensorID    originating sensor ID
   * @param sensorName  originating sensor name
   * @param value       event value
   *
   * @return  event value unless it is exceeding LEVEL limits in which case it has been limited
   *          to range [0..100]
   */
  private static int validate(int sensorID, String sensorName, int value)
  {
    if (value > 100)
    {
      value = 100;

      log.warn(
          "A LEVEL event was created with an invalid value : {0} (Source Sensor = {1} - {2}). " +
          "The event value has been limited to maximum value of 100.", value, sensorID, sensorName
      );
    }

    if (value < 0)
    {
      value = 0;

      log.warn(
          "A LEVEL event was create with an invalid value : {0} (Source Sensor = {1} - {2}). " +
          "The event value has been limited to minimum value of 0.", value, sensorID, sensorName
      );
    }

    return value;
  }



  // Constructors ---------------------------------------------------------------------------------


  /**
   * Constructs a new LEVEL event with a given originating sensor ID, originating sensor name
   * and event value.  <p>
   *
   * Level events are associated with sensors of 'level' type in controller.xml model. <p>
   *
   * Level event values must be restricted to a range of [0..100].
   *
   * @param sourceSensorID      originating sensor's ID
   * @param sourceSensorName    originating sensor's name
   * @param value               level event value
   */
  public Level(int sourceSensorID, String sourceSensorName, int value)
  {
    super(sourceSensorID, sourceSensorName, validate(sourceSensorID, sourceSensorName, value), 0, 100);
  }



  // Range Overrides ------------------------------------------------------------------------------

  public Level clone(int newValue)
  {
    return new Level(getSourceID(), getSource(), newValue);
  }

  @Override public boolean isEqual(Object o)
  {
    if (o == null)
    {
      return false;
    }

    if (o == this)
    {
      return true;
    }

    if (o.getClass() != this.getClass())
    {
      return false;
    }

    Level l = (Level)o;

    return l.getSourceID().equals(this.getSourceID())
        && l.getSource().equals(this.getSource())
        && l.getValue().equals(this.getValue());
  }

}

