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
 * Indicates that a transmission was interrupted by an unsolicted event.
 * The exception contains the event code that identifies what type of
 * event interrupted the transmission.
 *
 * @author Jesse Peterson <jesse@jpeterson.com>
 */
public class InterruptedTransmissionException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The event code is the byte that identifies the event that
     * interrupted the transmission.
     */
    private byte eventCode;

    /**
     * Create a new exception.
     *
     * @param eventCode Byte that identifies the event that interrupted
     *        the transmission.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public InterruptedTransmissionException(byte eventCode)
    {
	super();
	this.eventCode = eventCode;
    }

    /**
     * Retrieve the event code of the event that interrupted the
     * transmission.
     *
     * @return Byte that identifies the event that interrupted
     *         the transmission.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public byte getEventCode()
    {
	return(eventCode);
    }
}
