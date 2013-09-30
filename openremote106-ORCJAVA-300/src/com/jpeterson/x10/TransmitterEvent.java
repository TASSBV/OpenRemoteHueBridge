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

/**
 * Event issued by <CODE>Transmitter</CODE> to indicate a change in state or
 * other activity. A <CODE>TransmitterEvent</CODE> is issued to each
 * <CODE>TransmitterListener</CODE> attached to a <CODE>Transmitter</CODE>
 * using the <CODE>addGatewayListener</CODE> method it inherits from the
 * <CODE>Gateway</CODE> interface. 
 * <P>
 * The <CODE>TransmitterEvent</CODE> class extends the
 * <CODE>GatewayEvent</CODE> class. Similarly, the
 * <CODE>TransmitterListener</CODE> interface extends the
 * <CODE>GatewayListener</CODE> interface. 
 * <P>
 * <CODE>TransmitterEvent</CODE> extends <CODE>GatewayEvent</CODE> with
 * several events that are specialized for tranmitting. It also
 * inherits several event types from <CODE>GatewayEvent</CODE>:
 * <CODE>GATEWAY_ALLOCATED</CODE>, <CODE>GATEWAY_DEALLOCATED</CODE>,
 * <CODE>GATEWAY_ALLOCATING_RESOURCES</CODE>,
 * <CODE>GATEWAY_DEALLOCATING_RESOURCES</CODE>, <CODE>GATEWAY_PAUSED</CODE>,
 * <CODE>GATEWAY_RESUMED</CODE>.
 *
 * @see Transmitter
 * @see TransmitterListener
 * @see addGatewayListener
 * @see GatewayEvent
 * @see Serialized Form
 *
 * @author Jesse Peterson <6/29/99>
 * @version 1.0
 */
public class TransmitterEvent extends ControlEvent
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The transmit queue of the <CODE>Transmitter</CODE> has emptied and
     * the <CODE>Transmitter</CODE> has changed to the
     * <CODE>QUEUE_EMPTY</CODE> state. The queue may become empty because
     * output of all items in the queue is completed.
     *
     * @see queueEmptied
     * @see QUEUE_EMPTY
     */
    public static final int QUEUE_EMPTIED = 19;

    /**
     * The output queue has changed. This event may indicate a change
     * in state of the <CODE>Transmitter</CODE> from <CODE>QUEUE_EMPTY</CODE>
     * to <CODE>QUEUE_NOT_EMPTY</CODE>. The event may also occur in the
     * <CODE>QUEUE_NOT_EMPTY</CODE> state without changing state.
     * <P>
     * The output queue changes when (a) a new item is placed on the
     * queue with a call to one of the <CODE>speak</CODE> methods or (b) when
     * output of the top item of
     * the queue is completed (again, without leaving an empty queue). 
     * <P>
     * The <CODE>topOfQueueChanged</CODE> boolean parameter is set to true if
     * the top item on the queue has changed. 
     *
     * @see queueUpdated
     * @see getTopOfQueueChanged
     * @see QUEUE_NOT_EMPTY
    */
    public static final int QUEUE_UPDATED = 20;

    /**
     * <CODE>topOfQueueChanged</CODE> is <CODE>true</CODE> for
     * <CODE>QUEUE_UPDATED</CODE> event when the top item in the
     * output queue has changed. 
     */
    protected boolean topOfQueueChanged;

    /**
     * Construct a <CODE>TransmitterEvent</CODE> with a specified event id
     * and <CODE>topOfQueueChanged</CODE> flag.
     *
     * @param source the <CODE>Transmitter</CODE> that issued the event
     * @param id the identifier for the event type
     * @param topOfQueueChanged true if top item on output queue changed
     * @param oldGatewayState gateway state prior to this event
     * @param newGatewayState gateway state following this event
     *
     * @author Jesse Peterson <6/29/99>
     */
    public TransmitterEvent(Transmitter source, int id,
			    boolean topOfQueueChanged, long oldGatewayState,
			    long newGatewayState)
    {
	super(source, id);

	this.topOfQueueChanged = topOfQueueChanged;
    }

    /**
     * Return the <CODE>topOfQueueChanged</CODE> value. The value is
     * <CODE>true</CODE> for a <CODE>QUEUE_UPDATED</CODE> event when the top
     * item in the output queue has changed.
     *
     * @see topOfQueueChanged
     *
     * @author Jesse Peterson <6/29/99>
     */
    public boolean getTopOfQueueChanged()
    {
	return(topOfQueueChanged);
    }

    /**
     * Returns a parameter string identifying this event. This method is
     * useful for event-logging and for debugging.
     *
     * @return a string identifying the event
     *
     * @author Jesse Peterson <6/29/99>
     */
    public String paramString()
    {
	StringBuffer buffer = new StringBuffer();

	int eventId = getId();
	String eventDescription;

	// decode event ID to human readable meaning
	switch (eventId)
	{
	case QUEUE_EMPTIED:
	    eventDescription = "Queue Emptied";
	    break;

	case QUEUE_UPDATED:
	    eventDescription = "Queue Updated";

	default:
	    return("(TransmitterEvent) " + super.paramString());
	}

	// create string
	buffer.append("(TransmitterEvent) Engine Event - Source = [");
	buffer.append(getSource());
	buffer.append("] ID = [");
	buffer.append(eventDescription);
	buffer.append(" (").append(eventId);
	buffer.append(")]");

	return(buffer.toString());
    }
}
