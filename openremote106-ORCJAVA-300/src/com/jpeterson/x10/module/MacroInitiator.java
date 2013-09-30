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
import com.jpeterson.x10.X10Util;
import com.jpeterson.x10.event.FunctionEvent;
import com.jpeterson.x10.event.OffEvent;
import com.jpeterson.x10.event.OnEvent;

public class MacroInitiator implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final int SIZEOF_MACRO_INITIATOR = 3;

    /**
     * Function that triggers the macro. Will be an OnEvent or an OffEvent.
     */
    private FunctionEvent function;

    /**
     * Device that triggers the macro. Will be from 1 through 16 inclusive.
     */
    private int device;

    /**
     * Macro initiated by this MacroInitiator.
     */
    private Macro macro;

    /**
     * Empty constructor for serialization.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public MacroInitiator()
    {
    }

    /**
     * Create a new MacroInitiator that is initiated by an OnEvent recieved for
     * the specified device for the specified house code.
     *
     * @param device Device number.  Must be between 1 and 16, inclusive.
     * @param function An OnEvent that designates that the macro will execute.
     *        The OnEvent contains the house code that this MacroInitiator
     *        is assigned to as well. For proper use with a macro, the source
     *        of the OnEvent will be ignored.
     * @exception IllegalArgumentException Thrown if the device number is less
     *            than 1 or greater than 16.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public MacroInitiator(int device, OnEvent function)
        throws IllegalArgumentException
    {
        this.device = device;
        this.function = function;
    }

    /**
     * Create a new MacroInitiator that is initiated by an OffEvent recieved for
     * the specified device for the specified house code.
     *
     * @param device Device number.  Must be between 1 and 16, inclusive.
     * @param function An OffEvent that designates that the macro will execute.
     *        The OffEvent contains the house code that this MacroInitiator
     *        is assigned to as well. For proper use with a macro, the source
     *        of the OffEvent will be ignored.
     * @exception IllegalArgumentException Thrown if the device number is less
     *            than 1 or greater than 16.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public MacroInitiator(int device, OffEvent function)
        throws IllegalArgumentException
    {
        this.device = device;
        this.function = function;
    }

    /**
     * Return the device number assigned to this MacroInitiator.
     *
     * @return The assigned device number.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public int getDevice()
    {
        return(device);
    }

    /**
     * Return the function assigned to this MacroInitiator. Will be an
     * OnEvent or an OffEvent.
     *
     * @return Assigned function.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public FunctionEvent getFunction()
    {
        return(function);
    }

    /**
     * Assign a macro to be initiated by this MacroInitiator.
     *
     * @param macro Macro to be initiated by this MacroInitiator.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void setMacro(Macro macro)
    {
        this.macro = macro;
    }

    /**
     * Retrieve the macro assigned to this MacroInitiator.
     *
     * @return Macro to be initiated by this MacroInitiator.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public Macro getMacro()
    {
        return(macro);
    }

    /**
     * Get the byte array representing this macro initiator.
     *
     * @param macroOffset Integer offset to macro to initiate.
     * @return Byte array containing the macro initiator.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public byte[] getBytes(int macroOffset)
    {
        byte houseCode, deviceCode;
        byte[] element, offset;

        houseCode = function.getHiNibble();
        deviceCode = X10Util.deviceCode2byte(device);

        element = new byte[3];
        element[0] = (byte)((houseCode << 4) | deviceCode);
        offset = CM11A.toBytes(macroOffset, 2, true);
        element[1] = offset[0];
        element[2] = offset[1];
        if (function instanceof OnEvent)
        {
            element[1] |= 0x80;
        }

        return(element);
    }
}
