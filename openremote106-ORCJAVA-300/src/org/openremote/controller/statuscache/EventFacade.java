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

import org.openremote.controller.utils.Logger;
import org.openremote.controller.model.event.Switch;
import org.openremote.controller.OpenRemoteRuntime;
import org.openremote.controller.protocol.Event;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class EventFacade
{
  protected EventContext eventContext;
  protected Logger log;


  public void pushEventContext(EventContext ctx)
  {
    this.eventContext = ctx;
  }

  public void pushLogger(Logger log)
  {
    this.log = log;
  }


  // Protected Instance Methods -------------------------------------------------------------------
  
  protected void dispatchEvent(final Event event)
  {
    eventContext.terminateEvent();

    Thread t = OpenRemoteRuntime.createThread(

        "New Event Processor Dispatcher",

        new Runnable()
        {
          public void run()
          {
            eventContext.getDeviceStateCache().update(event);
          }
        }
    );

    t.start();
  }

}

