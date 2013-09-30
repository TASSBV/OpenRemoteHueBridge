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

import org.openremote.controller.protocol.Event;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.exception.ResourceNotFoundException;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public abstract class SingleValueEventFacade<T, U extends Event> extends EventFacade
{

  public T name(String name) throws ResourceNotFoundException
  {
    Event evt = eventContext.getDeviceStateCache().queryStatus(name);

    if (evt instanceof Sensor.UnknownEvent)
    {
      evt = createDefaultEvent(evt.getSourceID(), evt.getSource());
    }

    try
    {
      return createAdapter((U)evt);
    }

    catch (ClassCastException e)
    {
      throw new ResourceNotFoundException("event class mismatch");
    }
  }


  // Protected Instance Methods -------------------------------------------------------------------

  protected abstract U createDefaultEvent(int sourceID, String sourceName);

  protected abstract T createAdapter(U event);
}

