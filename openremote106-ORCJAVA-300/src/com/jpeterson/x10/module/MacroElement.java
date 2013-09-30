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

import java.io.Serializable;
import java.util.BitSet;
import com.jpeterson.x10.event.FunctionEvent;
import com.jpeterson.x10.X10Util;

public class MacroElement implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final int MAX_DEVICES = 16 + 1;  // Add one because zero based
    private static final int MAX_DEVICE = 16;
    private static final int MIN_DEVICE = 1;
    private static final byte BRIGHTEN_FIRST = (byte)0x80;
    private FunctionEvent function;
    private int[] devices;
    private boolean brightenFirst;

    /**
     * Provided for proper serialization.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    protected MacroElement()
    {
        devices = new int[MAX_DEVICES];
        brightenFirst = false;
    }

    /**
     * Create a MacroElement for the provided FunctionEvent.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public MacroElement(FunctionEvent function)
    {
        this();
        setFunction(function);
    }

    /**
     * Set the function event for this macro element.
     *
     * @param function The function event for this macro element.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    private void setFunction(FunctionEvent function)
    {
        this.function = function;
    }

    /**
     * Get the function event for this macro element.
     *
     * @return The function event for this macro element.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public FunctionEvent getFunction()
    {
        return(function);
    }

    /**
     * If this is a dim command, should the device be brought to 100% percent
     * first before applying the dim, or just dim. By default this is false.
     *
     * @param brightenFirst Set to true if the devices should be brought up
     *        to 100% before the dim is performed, false if the devices should
     *        be dimmed from their current brightness.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void setBrightenFirst(boolean brightenFirst)
    {
        this.brightenFirst = brightenFirst;
    }

    /**
     * Determine the functions preference for brightening first before dimming.
     *
     * @return True if the macro element will brighten the devices to 100%
     *         before applying the dim, or false if the dim is applied to the
     *         current brightness level.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public boolean getBrightenFirst()
    {
        return(brightenFirst);
    }

    /**
     * Add a device to this macro element.
     *
     * @param device Device number for this macro element.
     * @exception IllegalArgumentException Thrown if the device number is less
     *            than 1 or greater than 16.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void addDevice(int device)
    {
        if ((device < MIN_DEVICE) || (device > MAX_DEVICE))
        {
            throw new IllegalArgumentException("Device number " + device +
                                               " illegal. Device number must be between " +
                                               MIN_DEVICE + " and " + MAX_DEVICE +
                                               " inclusive.");
        }
        devices[device] = 1;
    }

    /**
     * Remove a device from this macro element.
     *
     * @param device Device to be removed from this macro element.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void removeDevice(int device)
    {
        if ((device >= MIN_DEVICE) && (device <= MAX_DEVICE))
        {
            devices[device] = 0;
        }
    }

    /**
     * Retrieve an array of devices for this macro element.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public int[] getDevices()
    {
        int[] result;
        int numDevices = 0;
        int index;

        for (int i = MIN_DEVICE; i <= MAX_DEVICE; i++)
        {
            if (devices[i] == 1)
            {
                ++numDevices;
            }
        }

        result = new int[numDevices];

        index = 0;
        for (int i = MIN_DEVICE; i <= MAX_DEVICE; i++)
        {
            if (devices[i] == 1)
            {
                result[index++] = i;
            }
        }

        return(result);
    }

    /**
     * Get the byte array representing this macro element.
     *
     * @return Byte array containing the macro element.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public byte[] getBytes()
    {
        byte functionCode = 0, houseCode = 0, devicesHi = 0, devicesLo = 0;
        byte[] element = null;
        BitSet deviceBitSet = new BitSet(16);
        
        // encode device bytes
        for (int i = 1; i <= 16; i++)
        {
            if (devices[i] == 1)
            {
                // the macro applies to this device
                deviceBitSet.set(X10Util.deviceCode2byte(i));
            }
        }
        for (int i = 0; i < 8; i++)
        {
            if (deviceBitSet.get(i))
            {
                devicesLo |= 1 << i;
            }
            if (deviceBitSet.get(i + 8))
            {
                devicesHi |= 1 << i;
            }
        }

        functionCode = function.getFunction();
        houseCode = function.getHiNibble();

        if ((functionCode == X10Util.X10_FUNCTION_DIM) ||
            (functionCode == X10Util.X10_FUNCTION_BRIGHT))
        {
            // bright or dim command
            element = new byte[4];
            element[0] = (byte)((houseCode << 4) | functionCode);
            //element[1] = nine2sixteen;
            //element[2] = one2eight;
            element[1] = devicesHi;
            element[2] = devicesLo;
            if (brightenFirst)
            {
                element[3] |= BRIGHTEN_FIRST;
            }
            element[3] |= (byte)function.getDims();
        }
        else if (functionCode == X10Util.X10_FUNCTION_EXTENDED_DATA_TRANSFER)
        {
            // TODO: not yet supported
        }
        else
        {
            // basic command
            element = new byte[3];
            element[0] = (byte)((houseCode << 4) | functionCode);
            //element[1] = nine2sixteen;
            //element[2] = one2eight;
            element[1] = devicesHi;
            element[2] = devicesLo;
        }

        return(element);
    }
}
