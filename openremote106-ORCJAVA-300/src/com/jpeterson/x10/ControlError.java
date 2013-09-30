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
 * Signals that an error has occurred.
 *
 * @see ControlException
 * @see Serialized Form
 *
 * @author Jesse Peterson <6/29/99>
 * @version 1.0
 */
public class ControlError extends Error
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Empty constructor for <CODE>ControlException</CODE> with no detail
     * message.
     *
     * @author Jesse Peterson <6/29/99>
     */
    public ControlError()
    {
	super();
    }

    /**
     * Constructs a <CODE>ControlException</CODE> with a detail message.
     *
     * @param s the detail message
     *
     * @author Jesse Peterson <6/29/99>
     */
    public ControlError(String s)
    {
	super(s);
    }
}
