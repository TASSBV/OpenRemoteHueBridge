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
import org.openremote.controller.exception.ResourceNotFoundException;
import org.openremote.controller.model.event.Switch;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.OpenRemoteRuntime;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class SwitchFacade extends EventFacade
{

//  private EventContext ctx;
//  private Logger log;
//

//  public void pushEventContext(EventContext ctx)
//  {
//    this.ctx = ctx;
//  }
//
//  public void pushLogger(Logger log)
//  {
//    this.log = log;
//  }


  
  public SwitchAdapter name(String name) throws ResourceNotFoundException
  {
    Event evt = eventContext.getDeviceStateCache().queryStatus(name);

    if (evt instanceof Sensor.UnknownEvent)
    {
      evt = new Switch(
          evt.getSourceID(),
          evt.getSource(),
          Switch.State.OFF.serialize(),
          Switch.State.OFF
      );
    }
    
    if (evt instanceof Switch)
    {
      return new SwitchAdapter((Switch)evt);
    }

    else
    {
      throw new ResourceNotFoundException("Sensor ''{0}'' is not switch type.", name);
    }
  }



  // Inner Classes --------------------------------------------------------------------------------

  public class SwitchAdapter
  {

    // Instance Fields ----------------------------------------------------------------------------

    private Switch switchEvent;


    // Constructors -------------------------------------------------------------------------------

    private SwitchAdapter(Switch switchEvent)
    {
      this.switchEvent = switchEvent;
    }



    // Public Instance Methods --------------------------------------------------------------------

    public void off()
    {
      off(null);
    }

    public void off(String eventValue)
    {
      if (eventValue == null)
      {
        eventValue = Switch.State.OFF.serialize();
      }

      Switch newSwitchEvent = switchEvent.clone(eventValue, Switch.State.OFF);

      dispatchEvent(newSwitchEvent);
    }



    public void on()
    {
      on(null);
    }

    public void on(String eventValue)
    {
      if (eventValue == null)
      {
        eventValue = Switch.State.ON.serialize();
      }

      Switch newSwitchEvent = switchEvent.clone(eventValue, Switch.State.ON);

      dispatchEvent(newSwitchEvent);
    }



//    // Private Instance Methods -------------------------------------------------------------------
//
//    private void dispatchEvent(final Switch switchEvent)
//    {
//      eventContext.terminateEvent();
//
//      Thread t = OpenRemoteRuntime.createThread(
//
//          "Event Processor New Switch Event Dispatcher",
//
//          new Runnable()
//          {
//            public void run()
//            {
//              eventContext.getDeviceStateCache().update(switchEvent);
//            }
//          }
//      );
//
//      t.start();
//    }

  }
  
}

