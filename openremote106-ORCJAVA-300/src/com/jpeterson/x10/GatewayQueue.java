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

/**
 * Base abstract queue management class for X10 ControlEvents.
 */
public abstract class GatewayQueue
{
    /** Dispatcher to process the queue messages. */
    protected ControlEventDispatcher dispatcher;

    /**
     * Create a new gateway queue for processing control messages.
     *
     * @param dispatcher ControlEventDispatcher that will dispatch the
     *        control event.
     * @author Jesse Peterson (<a href="mailto:jesse@jpeterson.com">jesse@jpeterson.com</a>)
     */
    public GatewayQueue(ControlEventDispatcher dispatcher)
    {
        setDispatcher(dispatcher);
    }

    /**
     * Assign a dispatcher.
     *
     * @param dispatcher ControlEventDispatcher that will dispatch the
     *        control event.
     * @author Jesse Peterson (<a href="mailto:jesse@jpeterson.com">jesse@jpeterson.com</a>)
     */
    public void setDispatcher(ControlEventDispatcher dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    /**
     * Retrieve the assigned ControlEventDispatcher.
     *
     * @return ControlEventDispatcher that will dispatch the
     *         control event.
     * @author Jesse Peterson (<a href="mailto:jesse@jpeterson.com">jesse@jpeterson.com</a>)
     */
    public ControlEventDispatcher getDispatcher()
    {
        return(dispatcher);
    }

    /**
     * Put an event on the queue.  The assigned dispatcher will process the
     * event.
     *
     * @param event ControlEvent to be placed on the queue for subsequent
     *        processing.
     * @author Jesse Peterson (<a href="mailto:jesse@jpeterson.com">jesse@jpeterson.com</a>)
     */
    public abstract void post(ControlEvent event);
}
