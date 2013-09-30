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

import org.openremote.controller.model.event.Level;
import org.openremote.controller.model.event.Range;
import org.openremote.controller.protocol.Event;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class LevelFacade extends SingleValueEventFacade<LevelFacade.LevelAdapter, Level>
{

  @Override protected Level createDefaultEvent(int sourceID, String sourceName)
  {
    return new Level(sourceID, sourceName, 0);
  }

  @Override protected LevelAdapter createAdapter(Level event)
  {
    return new LevelAdapter(event);
  }

  // Inner Classes --------------------------------------------------------------------------------

  public class LevelAdapter
  {

    private Level level;


    private LevelAdapter(Level level)
    {
      this.level = level;
    }

    public void value(int value)
    {
      if (value < 0)
      {
        value = 0;
      }

      else if (value > 100)
      {
        value = 100;
      }

      Level newLevelEvent = level.clone(value);

      dispatchEvent(newLevelEvent);
    }
  }
}

