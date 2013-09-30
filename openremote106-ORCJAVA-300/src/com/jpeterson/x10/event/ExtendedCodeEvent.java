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

package com.jpeterson.x10.event;

import com.jpeterson.x10.X10Util;
import com.jpeterson.util.HexFormat;

/**
 * Extended event
 *
 * @version 1.0
 * @author Jesse Peterson <jesse@jpeterson.com>
 */
public class ExtendedCodeEvent extends FunctionEvent
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private byte data;

    private byte command;

    /**
     * Create a new 'Extended Code' event.
     *
     * @param source The event source.
     * @param houseCode the house code of the event. Valid codes are 'A'
     *        through 'P', uppercase.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public ExtendedCodeEvent(Object source, char houseCode, byte data,
			     byte command)
    {
	super(source, 0, true, houseCode,
	      X10Util.X10_FUNCTION_EXTENDED_CODE, 22);
	this.data = data;
	this.command = command;
    }

    /**
     * Retrieve the bytes that compose the X10 event.
     *
     * @return Array of bytes that constitute the payload of the X10 event.
     *         The size will be 4.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public byte[] getPacket()
    {
	byte[] packet = new byte[4];
	byte[] standard = super.getPacket();

	packet[0] = standard[0];
	packet[1] = standard[1];
	packet[2] = data;
	packet[3] = command;

	return(packet);
    }

    /**
     * Retrieve the data byte.
     *
     * @return Extended code's data byte.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public byte getData()
    {
	return(data);
    }

    /**
     * Retrieve the command byte.
     *
     * @return Extended code's command byte.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public byte getCommand()
    {
	return(command);
    }

    /**
     * Create a string that represents the X10 event.
     *
     * @return string representation of the X10 event
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public String toString()
    {
	HexFormat hexFormat = new HexFormat();
	StringBuffer buffer = new StringBuffer();

	buffer.append("X10 Extended Code Event: house code<");
	buffer.append(getHouseCode());
	buffer.append("> function code<");
	buffer.append(getFunction());
	buffer.append("> data<0x");
	buffer.append(hexFormat.format(getData()));
	buffer.append("> command<0x");
	buffer.append(hexFormat.format(getCommand()));
	buffer.append(">");

	return(buffer.toString());
    }
}
