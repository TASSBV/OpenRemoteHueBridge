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
 * Signals an error caused by an illegal call to a method of a gateway.
 * For example, it is illegal to request a deallocated <CODE>Trasnmitter</CODE>
 * to transmit or to request any deallocated gateway to pause or resume.
 *
 * @see Serialized Form
 *
 * @author Jesse Peterson <6/29/99>
 * @version 1.0
 */
public class GatewayStateError extends ControlError
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a <CODE>GatewayStateError</CODE> with no detail message.
     *
     * @author Jesse Peterson <6/29/99>
     */
    public GatewayStateError()
    {
	super();
    }

    /**
     * Construct a <CODE>GatewayStateError</CODE> with a detail message. A
     * detail message is a <CODE>String</CODE> that describes this particular
     * exception.
     *
     * @author Jesse Peterson <6/29/99>
     */
    public GatewayStateError(String s)
    {
	super(s);
    }
}
