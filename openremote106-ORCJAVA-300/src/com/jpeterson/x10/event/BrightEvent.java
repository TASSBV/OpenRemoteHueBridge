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

/**
 * Brighten the previously addressed units on the same house code by the
 * specified amount.
 *
 * @version 1.0
 * @author Jesse Peterson <jesse@jpeterson.com>
 */
public class BrightEvent extends FunctionEvent
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a new 'Bright' event.
     *
     * @param source The event source.
     * @param houseCode the house code of the event. Valid codes are 'A'
     *        through 'P', uppercase.
     * @param dims The number of dims.
     * @param dimMax The maximum number of dims for the event.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public BrightEvent(Object source, char houseCode, int dims, int dimMax)
    {
	super(source, dims, houseCode, X10Util.X10_FUNCTION_BRIGHT, dimMax);
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
	StringBuffer buffer = new StringBuffer();

	buffer.append("X10 Bright Event: house code<");
	buffer.append(getHouseCode());
	buffer.append("> function code<");
	buffer.append(getFunction());
	buffer.append("> dims<");
	buffer.append(getDims());
	buffer.append("> dimMax<");
	buffer.append(getDimMax());
	buffer.append(">");

	return(buffer.toString());
    }
}
