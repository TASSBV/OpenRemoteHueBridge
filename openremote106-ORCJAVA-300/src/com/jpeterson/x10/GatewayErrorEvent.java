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
 * <CODE>GatewayErrorEvent</CODE> is an asynchronous notification of an
 * internal error in the gateway which prevents normal behavior of that
 * gateway. The event encapsulates a <CODE>Throwable</CODE> object that
 * provides details about the rror.
 *
 * @see gatewayError
 * @see SerializedForm
 *
 * @author Jesse Peterson <6/29/99>
 * @version 1.0
 */
public class GatewayErrorEvent extends GatewayEvent
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Identifier for event issued when gateway error occurs.
     *
     * @see gatewayError
     */
    public static final int GATEWAY_ERROR = 7;

    /**
     * Throwable object (<CODE>Exception</CODE> or <CODE>Error</CODE>) that
     * describes the detected gateway problem.
     *
     * @see getGatewayError
     */
    protected Throwable problem;

    /**
     * Constructs a <CODE>GatewayErrorEvent</CODE> with an event identifier,
     * throwable, old gateway state and new gateway state. The old and new states
     * are zero if the gateway states are unknown or undefined.
     *
     * @param source the object that issued the event
     * @param id the identifier for the event type
     * @param throwable description of the detected error
     * @param oldGatewayState gateway state prior to this event
     * @param newGatewayState gateway state following this event
     * @see getEngineState
     *
     * @author Jesse Peterson <6/29/99>
     */
    public GatewayErrorEvent(Gateway source, int id, Throwable throwable,
			     long oldGatewayState, long newGatewayState)
    {
	super(source, id, oldGatewayState, newGatewayState);
	problem = throwable;
    }

    /**
     * Return the <CODE>Throwable</CODE> object (<CODE>Exception</CODE> or
     * <CODE>Error</CODE>) that describes the gateway problem.
     *
     * @author Jesse Peterson <6/29/99>
     */
    public Throwable getGatewayError()
    {
	return(problem);
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
	case GATEWAY_ERROR:
	    eventDescription = "Gateway Error";
	    break;

	default:
	    eventDescription = "Gateway Error Event Unknown";
	    break;
	}

	// create string
	buffer.append("Gateway Error Event - Source = [");
	buffer.append(getSource());
	buffer.append("] ID = [");
	buffer.append(eventDescription);
	buffer.append(" (").append(eventId);
	buffer.append(")]");

	return(buffer.toString());
    }
}
