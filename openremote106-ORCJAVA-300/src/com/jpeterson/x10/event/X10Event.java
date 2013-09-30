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

import java.util.EventObject;
import com.jpeterson.util.HexFormat;

/**
 * An X10 event represents an X10 transmission packet. This is the base
 * class for subclasses that implement the different X10 messages.
 *
 * @version 1.0
 * @author Jesse Peterson <jesse@jpeterson.com>
 */
public class X10Event extends EventObject
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Number of dims.
     */
    private int dims;

    /**
     * Maximum number of dims.
     */
    private int dimMax;

    /**
     * True if the event is a function event.
     */
    private boolean function;

    /**
     * True if the event is an extended event.
     */
    private boolean extended;

    /**
     * High nibble of the Code portion of the X10 event.
     */
    private byte hiNibble;

    /**
     * Low nibble of the Code portion of the X10 event.
     */
    private byte loNibble;

    /**
     * Create an X10 event, specifying all parameters.
     *
     * @param source The event source.
     * @param dims The number of dims.
     * @param funtion True if the event is a function event, false
     *        if the event is an address event.
     * @param extended True if the event is an extended event, false
     *        otherwise
     * @param hiNibble The upper four bits of the 'Code' portion of the
     *        X10 event. This is usually the housecode
     * @param loNibble The lower four bits of the 'Code' portion of the
     *        X10 event. This is typically the device code in an address
     *        event or the function code in a function event.
     * @param dimMax The maximum number of dims for the event.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public X10Event(Object source, int dims, boolean function,
            boolean extended, byte hiNibble, byte loNibble,
            int dimMax)
    {
        // call the parent class constructor
        super(source);

        this.dims = dims;
        this.dimMax = dimMax;
        this.function = function;
        this.extended = extended;
        this.hiNibble = hiNibble;
        this.loNibble = loNibble;
    }

    /**
     * Create an X10 event. This constructor is normally used for
     * standard address and function events that do not have dim values
     * or extended characteristics. It is provided for convenience.
     * The maximum number of dims is 22.
     *
     * @param source The event source.
     * @param funtion True if the event is a function event, false
     *        if the event is an address event.
     * @param hiNibble The upper four bits of the 'Code' portion of the
     *        X10 event. This is usually the housecode
     * @param loNibble The lower four bits of the 'Code' portion of the
     *        X10 event. This is typically the device code in an address
     *        event or the function code in a function event.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public X10Event(Object source, boolean function, byte hiNibble,
            byte loNibble)
    {
        this(source, 0, function, false, hiNibble, loNibble, 22);
    }

    /**
     * Retrieve the bytes that compose the X10 event.
     *
     * @return Array of bytes that constitute the payload of the X10 event.
     *         For standard events, the array will have a size of 2. For
     *         extended events, the size will be 4.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public byte[] getPacket()
    {
        byte[] packet = new byte[2];

        // header
        packet[0] = (byte)(dims << 3);
        packet[0] |= 0x04;
        if (function)
        {
            packet[0] |= 0x02;
        }
        if (extended)
        {
            packet[0] |= 0x01;
        }

        // code
        packet[1] = (byte)((hiNibble << 4) | loNibble);

        return(packet);
    }

    /**
     * Retrieve the checksum of the bytes in the message or the X10
     * transmission.
     *
     * @return the checksum
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public byte getChecksum()
    {
        int sum = 0;
        byte[] packet = getPacket();

        for (int i = 0; i < packet.length; i++)
        {
            sum += packet[i];
        }
        return((byte)sum);
    }

    /**
     * Determine if the event is a function or address event.
     *
     * @return True if a function event, false if an address event.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public boolean isFunction()
    {
        return(function);
    }

    /**
     * Determine if the event is an extended event.
     *
     * @return True if an extended event, false otherwise.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public boolean isExtended()
    {
        return(extended);
    }

    /**
     * Retrieve the number of dims in the event.
     *
     * @return number of dims
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public int getDims()
    {
        return(dims);
    }

    /**
     * Retrieve the maximum number of dims in the event.
     *
     * @return maximum number of dims
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public int getDimMax()
    {
        return(dimMax);
    }

    /**
     * Retrieve the high nibble of the 'code' portion of the packet.
     * This is the 'housecode'.
     *
     * @return the high nibble of the 'code' portion of the packet.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public byte getHiNibble()
    {
        return(hiNibble);
    }

    /**
     * Retrieve the low nibble of the 'code' portion of the packet.
     * This is the 'device code' for address messages or the 'function'
     * for function messages.
     *
     * @return the low nibble of the 'code' portion of the packet.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public byte getLoNibble()
    {
        return(loNibble);
    }

    /**
     * Calculate a hash code for the event. Uses the checksum.
     *
     * @return Hash code value for the event.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public int hashCode()
    {
        return((int)getChecksum());
    }

    /**
     * Determines if two objects are equals.  This tests all X10Event attributes
     * of the objects for equallity.
     *
     * @return True if the target object equals this object, false otherwise.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public boolean equals(Object object)
    {
        X10Event target;
        Object thisSource, targetSource;

        if ((object == null) ||
            (!(object instanceof X10Event)))
        {
            return(false);
        }

        target = (X10Event)object;

        thisSource = getSource();
        targetSource = target.getSource();

        if (thisSource == null)
        {
            if (targetSource != null)
            {
                return(false);
            }
        }
        else
        {
            if (!(thisSource.equals(targetSource)))
            {
                return(false);
            }
        }

        if ((dims == target.getDims()) &&
            (function == target.isFunction()) &&
            (extended == target.isExtended()) &&
            (hiNibble == target.getHiNibble()) &&
            (loNibble == target.getLoNibble()) &&
            (dimMax == target.getDimMax()))
        {
            return(true);
        }

        return(false);
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
        HexFormat hexFormat = new HexFormat();

        buffer.append("X10 Event: dims <");
        buffer.append(dims);
        buffer.append("> dimMax<");
        buffer.append(dimMax);
        buffer.append("> function<");
        buffer.append(function);
        buffer.append("> extended<");
        buffer.append(extended);
        buffer.append("> high nibble<0x");
        buffer.append(hexFormat.format(hiNibble));
        buffer.append("> low nibble<0x");
        buffer.append(hexFormat.format(loNibble));
        buffer.append(">");

        return(buffer.toString());
    }
}


