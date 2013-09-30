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
package org.openremote.controller.statuscache;

import org.openremote.controller.model.event.Range;


/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class RangeFacade extends SingleValueEventFacade<RangeFacade.RangeAdapter, Range>
{

  @Override protected Range createDefaultEvent(int sourceID, String sourceName)
  {
    return new Range(sourceID, sourceName, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  @Override protected RangeAdapter createAdapter(Range event)
  {
    return new RangeAdapter(event);
  }




  public class RangeAdapter
  {

    private Range range;

    private RangeAdapter(Range event)
    {
      this.range = event;
    }

    public void value(int value)
    {
      if (value < range.getMinValue())
      {
        value = range.getMinValue();
      }

      else if (value > range.getMaxValue())
      {
        value = range.getMaxValue();
      }

      Range newRangeEvent = range.clone(value);


System.out.println("\n\n\n============ DISPATCHING " + newRangeEvent);
System.out.println("Incoming value was " + value);
      
      dispatchEvent(newRangeEvent);
    }
  }

}

