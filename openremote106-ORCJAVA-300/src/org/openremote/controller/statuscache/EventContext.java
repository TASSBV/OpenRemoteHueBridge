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

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import org.openremote.controller.protocol.Event;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class EventContext
{


  // Instance Fields ------------------------------------------------------------------------------

  private StatusCache cache;
  private Event event;
  private CommandFacade commandFacade;
  private boolean terminated = false;


  
  // Constructors ---------------------------------------------------------------------------------

  protected EventContext(StatusCache cache, Event evt, CommandFacade commandFacade)
  {
    this.cache = cache;
    this.event = evt;
    this.commandFacade = commandFacade;
  }


  // Public Instance Methods ----------------------------------------------------------------------


  public void terminateEvent()
  {
System.out.println(this + " TERMINATED.");

    terminated = true;
  }

  public boolean hasTerminated()
  {
    return terminated;
  }


  public void replace(Event evt)
  {
    this.event = evt;
  }
  


  public StatusCache getDeviceStateCache()
  {
    return cache;
  }
  
  public CommandFacade getCommandFacade()
  {
    return  commandFacade;
  }

  public Event getEvent()
  {
    return event;
  }

  public Collection getEvents()
  {
    Set<Event> events = new HashSet<Event>();

    Iterator<Event> it = cache.getStateSnapshot();

    while (it.hasNext())
    {
      Event evt = it.next();

      if (evt.getSource().equals(getEvent().getSource()) &&
          evt.getSourceID().equals(getEvent().getSourceID()))
      {
        continue;
      }

      events.add(evt);
    }

    events.add(getEvent());

    return events;
  }



  @Override public String toString()
  {
    return "Event Context for " + event;
  }

}

