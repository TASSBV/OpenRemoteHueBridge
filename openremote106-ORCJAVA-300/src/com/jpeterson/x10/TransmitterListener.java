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

import java.util.EventListener;

/**
 * An extendion to the <CODE>GatewayListener</CODE> interface for receiving
 * notification of events associated with a <CODE>Transmitter</CODE>.
 * <CODE>TransmitterListener</CODE> objects are attached to and removed from
 * a <CODE>Transmitter</CODE> by calling the <CODE>addGatewayListener</CODE>
 * and <CODE>removeGatewayListener</CODE> methods (which
 * <CODE>Transmitter</CODE> inherits from the <CODE>Gateway</CODE> interface.
 * <P>
 * The source for all <CODE>TransmitterEvents</CODE> provided to a
 * <CODE>TransmitterListener</CODE> is the <CODE>Transmitter</CODE>.
 * <P>
 * The <CODE>TransmitterAdapter</CODE> class provides a trivial implementation
 * of this interface.
 *
 * @see TransmitterListener
 * @see Tranmitter
 * @see Gateway
 * @see addGatewayListener
 * @see removeGatewayListener
 *
 * @author Jesse Peterson <6/29/99>
 * @version 1.0
 */
public abstract interface TransmitterListener extends EventListener
{
    /**
     * A <CODE>QUEUE_EMPTIED</CODE> event has occurred indicating that the
     * output queue of the <CODE>Transmitter</CODE> has emptied. The
     * <CODE>Transmitter</CODE> is in the <CODE>QUEUE_EMPTY</CODE> state.
     *
     * @see QUEUE_EMPTIED
     * @see QUEUE_EMPTY
     */
    public void queueEmptied(TransmitterEvent e);

    /**
     * A <CODE>QUEUE_UPDATED</CODE> event has occurred indicating that the
     * output queue has changed. The
     * <CODE>Transmitter</CODE> is in the <CODE>QUEUE_NOT_EMPTY</CODE> state.
     *
     * @see QUEUE_UPDATED
     * @see QUEUE_NOT_EMPTY
     */
    public void queueUpdated(TransmitterEvent e);
}
