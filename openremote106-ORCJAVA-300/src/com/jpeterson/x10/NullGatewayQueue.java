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
 * The NullGatewayQueue implements a null queue.  This means that the
 * <code>put</code> method directly calls the dispatcher to dispatch
 * the message.  This type of handling is useful for embedded type of
 * applications that do not require their event notification to be
 * synchronized with other events.
 */
public class NullGatewayQueue extends GatewayQueue
{
    /**
     * Create a new gateway queue for processing control messages.
     *
     * @param dispatcher ControlEventDispatcher that will dispatch the
     *        control event.
     * @author Jesse Peterson (<a href="mailto:jesse@jpeterson.com">jesse@jpeterson.com</a>)
     */
    public NullGatewayQueue(ControlEventDispatcher dispatcher)
    {
        super(dispatcher);
    }

    /**
     * Put an event on the queue for subsequent processing by the dispatcher.
     * This method directly calls dispatchControlEvent() on the dispatcher.
     * Because it directly calls the dispatcher on the current thread,
     * there is no decoupling or synchronization performed.
     *
     * @param event ControlEvent to be placed on the queue for subsequent
     *        processing.
     * @author Jesse Peterson (<a href="mailto:jesse@jpeterson.com">jesse@jpeterson.com</a>)
     */
    public void post(ControlEvent event)
    {
        dispatcher.dispatchControlEvent(event);
    }
}
