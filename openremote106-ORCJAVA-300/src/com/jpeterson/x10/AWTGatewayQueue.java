/*
 * Copyright (C) 2000  Jesse E. Peterson
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 *
 */

package com.jpeterson.x10;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;

/**
 * The AWTGatewayQueue uses the AWT event queue to synchronize control
 * events with all other AWT events.  This should make a Swing GUI thread
 * safe.<p>
 * The method <code>post</code> is used by the class to submit ControlEvents
 * to the AWTEvent queue.  An AWTEventSync object is used to receive the
 * event from the AWTEvent.  The actual ControlEvent is contained within
 * the AWTEngineEvent received by AWTEventSync.  Therefore, AWTEventSync
 * "cracks" the AWTGatewayEvent, pulling out the ControlEvent and then
 * calls ControlEventDispather's <code>dispatch</code> method, passing in the
 * ControlEvent.
 */
public class AWTGatewayQueue extends GatewayQueue
{
    private AWTEventSync awtEventSync;

    /**
     * Create a new gateway queue for processing control messages.
     *
     * @param dispatcher ControlEventDispatcher that will dispatch the
     *        control event.
     * @author Jesse Peterson (<a href="mailto:jesse@jpeterson.com">jesse@jpeterson.com</a>)
     */
    public AWTGatewayQueue(ControlEventDispatcher dispatcher)
    {
        super(dispatcher);
        awtEventSync = new AWTEventSync();
    }

    /**
     * Put an event on the AWT event queue for subsequent processing by the
     * dispatcher.
     * 
     * @param event ControlEvent to be placed on the queue for subsequent
     *        processing.
     * @author Jesse Peterson (<a href="mailto:jesse@jpeterson.com">jesse@jpeterson.com</a>)
     */
    public void post(ControlEvent event)
    {
        AWTControlEvent awtControlEvent = new AWTControlEvent(awtEventSync, event, getDispatcher());
        EventQueue q = Toolkit.getDefaultToolkit().getSystemEventQueue();
        q.postEvent(awtControlEvent);
    }

    protected static final int CONTROL_EVENT_ID = AWTEvent.RESERVED_ID_MAX + 2;

    /**
     * Encapsulate a control  event within an AWT event.
     *
     * @author Jesse Peterson <6/29/99>
     */
    protected class AWTControlEvent extends AWTEvent
    {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        private ControlEvent controlEvent;
        private ControlEventDispatcher handler;

        /**
         * Create a new AWTControlEvent to encapsulate the ControlEvent e.
         *
         * @param e the control event
         * @param handler GatewayImpl class with the dispatchControlEvent to
         *        call
         *
         * @author Jesse Peterson <6/29/99>
         */
        protected AWTControlEvent(Object source, ControlEvent e,
                				  ControlEventDispatcher handler)
        {
            super(source, CONTROL_EVENT_ID);

            controlEvent = e;
            this.handler = handler;
        }

        /**
         * Retrieve the encapsulated control event.
         *
         * @return the encapsulated control event.
         *
         * @author Jesse Peterson <6/29/99>
         */
        public ControlEvent getControlEvent()
        {
            return(controlEvent);
        }

        /**
         * Retrieve the handler for the event.
         *
         * @return Gateway implementation to handle the AWTControlEvent.
         *
         * @author Jesse Peterson <6/29/99>
         */
        public ControlEventDispatcher getHandler()
        {
            return(handler);
        }
    }

    /**
     * Component used to synch ControlEvents with the AWT event queue.
     *
     * @author Jesse Peterson <6/29/99>
     */
    protected class AWTEventSync extends Component
    {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Constructor just calls the parent constructor.
         *
         * @author Jesse Peterson <6/29/99>
         */
        protected AWTEventSync()
        {
            super();

            // enable user defined events
            enableEvents(0);
        }

        /**
         * Processes events occurring on this component.  Handles the
         * CONTROL_EVENT_ID events.  Other events are sent on to the
         * superclass.
         *
         * @param e the event
         *
         * @author Jesse Peterson <6/29/99>
         */
        protected void processEvent(AWTEvent e)
        {
            ControlEvent event;

            if ((e.getID() == CONTROL_EVENT_ID) &&
                (e instanceof AWTControlEvent))
            {
                event = ((AWTControlEvent)e).getControlEvent();
                ((ControlEventDispatcher)(((AWTControlEvent)e).getHandler())).dispatchControlEvent(event);
            }
            else
            {
                super.processEvent(e);
            }
        }
    }
}
