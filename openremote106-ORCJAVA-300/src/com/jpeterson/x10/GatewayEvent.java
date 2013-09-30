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
 * <CODE>GatewayEvent</CODE> notifies changes in state of a gateway that
 * transmits or receives. <CODE>GatewayEvents</CODE> are issued to each
 * <CODE>GatewayListener</CODE> attached to a gateway. The
 * <CODE>TransmissionEvent</CODE> and <CODE>ReceptionEvent</CODE> classes both
 * extend <CODE>GatewayEvent</CODE> to provide specific events for transmitters
 * and receivers.
 *
 * @author Jesse Peterson <6/29/99>
 * @version 1.0
 */
public class GatewayEvent extends ControlEvent
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Identifier for event issued when gateway allocation is complete. The
     * <CODE>ALLOCATED</CODE> flag of the <CODE>newGatewayState</CODE> is set.
     *
     * @see getNewGatewayState
     * @see getId
     * @see allocate
     * @see gatewayAllocated
     */
    public static final int GATEWAY_ALLOCATED = 1;

    /**
     * Identifier for event issued when gateway deallocation is complete. The
     * <CODE>DEALLOCATED</CODE> flag of the <CODE>newGatewayState</CODE> is set.
     *
     * @see getNewGatewayState
     * @see getId
     * @see allocate
     * @see gatewayDeallocated
     */
    public static final int GATEWAY_DEALLOCATED = 2;

    /**
     * Identifier for event issued when gateway allocation has commenced. The
     * <CODE>ALLOCATING_RESOURCES</CODE> flag of the
     * <CODE>newGatewayState</CODE> is set.
     *
     * @see getNewGatewayState
     * @see getId
     * @see allocate
     * @see gatewayAllocatingResources
     */
    public static final int GATEWAY_ALLOCATING_RESOURCES = 3;

    /**
     * Identifier for event issued when gateway deallocation has commenced. The
     * <CODE>DEALLOCATING_RESOURCES</CODE> flag of the
     * <CODE>newGatewayState</CODE> is set.
     *
     * @see getNewGatewayState
     * @see getId
     * @see allocate
     * @see gatewayDeallocatingResources
     */
    public static final int GATEWAY_DEALLOCATING_RESOURCES = 4;

    /**
     * Identifier for event issued when gateway is paused. The
     * <CODE>PAUSED</CODE> flag of the <CODE>newGatewayState</CODE> is set.
     *
     * @see getNewGatewayState
     * @see getId
     * @see pause
     * @see gatewayPaused
     */
    public static final int GATEWAY_PAUSED = 5;

    /**
     * Identifier for event issued when gateway is resumed. The
     * <CODE>RESUMED</CODE> flag of the <CODE>newGatewayState</CODE> is set.
     *
     * @see getNewGatewayState
     * @see getId
     * @see resume
     * @see gatewayResumed
     */
    public static final int GATEWAY_RESUMED = 6;

    /**
     * Gateway state following this event.
     *
     * @see getNewGatewayState
     */
    protected long newGatewayState;

    /**
     * Gateway state prior to this event.
     *
     * @see getOldGatewayState
     */
    protected long oldGatewayState;

    /**
     * Constructs an <CODE>GatewayEvent</CODE> with an event identifier, old
     * gateway state and new gateway state.
     *
     * @param source the object that issued the event
     * @param id the identifier for the event type
     * @param oldGatewayState gateway state prior to this event
     * @param newGatewayState gateway state following this event
     * @see getGatewayState
     *
     * @author Jesse Peterson <6/29/99>
     */
    public GatewayEvent(Gateway source, int id, long oldGatewayState,
		       long newGatewayState)
    {
	super(source, id);
	this.oldGatewayState = oldGatewayState;
	this.newGatewayState = newGatewayState;
    }

    /**
     * Return the state following this <CODE>GatewayEvent</CODE>. The value
     * matches the <CODE>getGatewayState</CODE> method.
     *
     * @see getGatewayState
     *
     * @author Jesse Peterson <6/29/99>
     */
    public long getNewGatewayState()
    {
	return(newGatewayState);
    }

    /**
     * Return the state prior to this <CODE>GatewayEvent</CODE>.
     *
     * @see getGatewayState
     *
     * @author Jesse Peterson <6/29/99>
     */
    public long getOldGatewayState()
    {
	return(oldGatewayState);
    }

    /**
     * Returns a parameter string identifying this event. This method is useful
     * for event-logging and for debugging.
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
	case GATEWAY_ALLOCATED:
	    eventDescription = "Gateway Allocated";
	    break;

	case GATEWAY_DEALLOCATED:
	    eventDescription = "Gateway Deallocated";
	    break;

	case GATEWAY_ALLOCATING_RESOURCES:
	    eventDescription = "Gateway Allocating Resources";
	    break;

	case GATEWAY_DEALLOCATING_RESOURCES:
	    eventDescription = "Gateway Deallocating Resources";
	    break;

	case GATEWAY_PAUSED:
	    eventDescription = "Gateway Paused";
	    break;

	case GATEWAY_RESUMED:
	    eventDescription = "Gateway Resumed";
	    break;

	default:
	    eventDescription = "Gateway Event Unknown";
	    break;
	}

	// create string
	buffer.append("Gateway Event - Source = [");
	buffer.append(getSource());
	buffer.append("] ID = [");
	buffer.append(eventDescription);
	buffer.append(" (").append(eventId);
	buffer.append(")]");

	return(buffer.toString());
    }
}
