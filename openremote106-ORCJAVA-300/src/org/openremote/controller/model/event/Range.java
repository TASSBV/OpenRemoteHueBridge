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

import org.openremote.controller.protocol.Event;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Range extends Event<Integer>
{

  private Integer rangeValue;
  private Integer min;
  private Integer max;


  public Range(int sourceID, String sourceName, Integer value, Integer min, Integer max)
  {
    super(sourceID, sourceName);
    this.min = min;
    this.max = max;
    setValue(value);
  }

  public Integer getMinValue()
  {
    return min;
  }

  public Integer getMaxValue()
  {
    return max;
  }

  @Override public Integer getValue()
  {
    return rangeValue;
  }

  @Override public void setValue(Integer value)
  {
    if (value > max)
    {
      this.rangeValue = max;
    }

    else if (value < min)
    {
      this.rangeValue = min;
    }

    else
    {
      this.rangeValue = value;
    }
  }

  @Override public Range clone(Integer newValue)
  {
    if (newValue < getMinValue())
    {
      newValue = getMinValue();
    }
    
    else if (newValue > getMaxValue())
    {
      newValue = getMaxValue();
    }

    return new Range(this.getSourceID(), this.getSource(), newValue, getMinValue(), getMaxValue());
  }

  @Override public String serialize()
  {
    return Integer.toString(rangeValue);
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

    Range r = (Range)o;

    return r.getSourceID().equals(this.getSourceID())
        && r.getSource().equals(this.getSource())
        && r.rangeValue.equals(this.rangeValue);
  }



  @Override public String toString()
  {
    return
        "Range Event (ID = " + getSourceID() + ", Source = '" + getSource() + "', Value = '" +
        getValue() + "', Boundaries = [" + getMinValue() + "..." + getMaxValue() + "])";
  }
}

