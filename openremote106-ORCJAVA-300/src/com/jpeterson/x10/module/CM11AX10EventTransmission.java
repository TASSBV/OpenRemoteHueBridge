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

package com.jpeterson.x10.module;

import com.jpeterson.x10.event.X10Event;

/**
 * Create a CM11 transmission event to transmit an X10 event.
 *
 * @author Jesse Peterson <jesse@jpeterson.com>
 */
public class CM11AX10EventTransmission extends CM11AStandardTransmission
{
    private X10Event event;

    /**
     * Create a CM11 transmission event to transmit the specified X10 event.
     *
     * @param event The X10 event to transmit.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public CM11AX10EventTransmission(X10Event event)
    {
	super(event.getPacket());
	this.event = event;
    }

    /**
     * Retrieve the X10 event
     *
     * @return The X10 event that this object was initialized with.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public X10Event getEvent()
    {
	return(event);
    }

    /**
     * Create a string representation of the transmission.
     *
     * @return String representation of the transmission.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public String toString()
    {
	StringBuffer buffer = new StringBuffer();

	buffer.append("CM11AX10EventTransmission - ");
	buffer.append(event.toString());
	return(buffer.toString());
    }
}


