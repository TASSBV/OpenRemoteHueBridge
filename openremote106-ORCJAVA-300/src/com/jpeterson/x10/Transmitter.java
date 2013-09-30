/*
 * Copyright (C) 1999  Jesse E. Peterson
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

import java.io.IOException;
import com.jpeterson.x10.event.X10Event;

/**
 * Implementors of this interface are typically some sort of Gateway that
 * bridge a software model with a physical implmentation.  For example,
 * the CM11A object is a gateway between a software representation and
 * the physical X10 powerline interface via a serial connection.
 */
public interface Transmitter
{
    /**
     * Bit of state that is set when the output queue of a
     * <CODE>Transmitter</CODE> is empty. The <CODE>QUEUE_EMPTY</CODE> state
     * is a sub-state of the <CODE>ALLOCATED</CODE> state. An allocated
     * <CODE>Transmitter</CODE> is always in either the
     * <CODE>QUEUE_NO_EMPTY</CODE> or <CODE>QUEUE_EMPTY</CODE> state.
     * <P>
     * A <CODE>Transmitter</CODE> is always allocated in the
     * <CODE>QUEUE_EMPTY</CODE> state. The <CODE>Transmitter</CODE> transitions
     * from the <CODE>QUEUE_EMPTY</CODE> state to the
     * <CODE>QUEUE_NOT_EMPTY</CODE> state when a method places an object on
     * the output queue.
     * A <CODE>QUEUE_UPDATED</CODE> event is issued to indicate this change
     * in state.
     * <P>
     * A <CODE>Transmitter</CODE> returns from the <CODE>QUEUE_NOT_EMPTY</CODE>
     * state to the <CODE>QUEUE_EMPTY</CODE> state once the queue is emptied
     * because of completion of transmitting all objects.
     * <P>
     * The queue status can be tested with the <CODE>waitQueueEmpty</CODE>, 
     * <CODE>getEngineState</CODE> and <CODE>testEngineState</CODE> methods.
     * To block a thread until the queue is empty:
     * <BLOCKQUOTE>
     * <PRE>
     * Transmitter transmitter = ...;
     * transmitter.waitGatewayState(QUEUE_EMPTY);
     * </PRE>
     * </BLOCKQUOTE>
     * 
     * @see <CODE>QUEUE_NOT_EMPTY</CODE>
     * @see <CODE>ALLOCATED</CODE>
     * @see getGatewayState
     * @see waitGatewayState
     * @see testGatewayState
     * @see QUEUE_UPDATED
     */
    public static final long QUEUE_EMPTY = 0x40;

    /**
     * Bit of state that is set when the speech output queue of a
     * <CODE>Transmitter</CODE> is not empty. The <CODE>QUEUE_NOT_EMPTY</CODE>
     * state is a sub-state of the <CODE>ALLOCATED</CODE> state. An allocated
     * <CODE>Transmitter</CODE> is always in either the
     * <CODE>QUEUE_NOT_EMPTY</CODE> or <CODE>QUEUE_EMPTY</CODE> state.
     * <P>
     * A <CODE>Transmitter</CODE> enters the <CODE>QUEUE_NOT_EMPTY</CODE>
     * from the <CODE>QUEUE_EMPTY</CODE> state when the
     * <CODE>x10Transmission</CODE> method is called to place an object on the
     * output queue. A <CODE>QUEUE_UPDATED</CODE> event is issued to mark this
     * change in state.
     * <P>
     * A <CODE>Transmitter</CODE> returns form the <CODE>QUEUE_NOT_EMPTY</CODE>
     * state to the <CODE>QUEUE_EMPTY</CODE> state once the queue is emptied
     * because of completion of transmitting all objects.
     *
     * @see QUEUE_EMPTY
     * @see ALLOCATED
     * @see getGatewayState
     * @see waitGatewayState
     * @see testGetwayState
     * @see QUEUEU_UPDATED
     */
    public static final long QUEUE_NOT_EMPTY = 0x80;

    /**
     * Transmit an event.  This is typically done by an object that
     * implements a gateway function to some physical media.  For example,
     * the CM11A object is a gateway between a software model and the physical
     * X10 network via a serial interface.
     *
     * @param X10Event The event to send.
     */
    public void transmit(X10Event e) throws IOException;
}

