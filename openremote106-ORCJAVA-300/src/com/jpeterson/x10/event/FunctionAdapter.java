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

/**
 * Trivial implementation of the <CODE>FunctionListener</CODE> interface that
 * receives a <CODE>FunctionEvent</CODE>. The methods in this class are empty,
 * this class is provided as a convenience for easily creating listeners by
 * extending this class and overriding only the methods of interest.
 *
 * @see FunctionEvent
 *
 * @author Jesse Peterson <6/29/99>
 * @version 1.0
 */
public class FunctionAdapter extends Object implements FunctionListener
{
    /**
     * Constructor does nothing.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public FunctionAdapter()
    {
	// empty
    }

    /**
     * "All Units Off" command.
     *
     * @see <CODE>FUNCTION_ALL_UNITS_OFF</CODE>
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionAllUnitsOff(FunctionEvent e)
    {
	// empty
    }

    /**
     * "All Lights On" command.
     *
     * @see <CODE>FUNCTION_ALL_LIGHTS_ON</CODE>
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionAllLightsOn(FunctionEvent e)
    {
	// empty
    }

    /**
     * "On" command.
     *
     * @see <CODE>FUNCTION_ON</CODE>
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionOn(FunctionEvent e)
    {
	// empty
    }

    /**
     * "Off" command.
     *
     * @see <CODE>FUNCTION_OFF</CODE>
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionOff(FunctionEvent e)
    {
	// empty
    }

    /**
     * "Dim" command.
     *
     * @see <CODE>FUNCTION_DIM</CODE>
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionDim(FunctionEvent e)
    {
	// empty
    }

    /**
     * "Bright" command.
     *
     * @see <CODE>FUNCTION_BRIGHT</CODE>
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionBright(FunctionEvent e)
    {
	// empty
    }

    /**
     * "All Lights Off" command.
     *
     * @see <CODE>FUNCTION_ALL_LIGHTS_OFF</CODE>
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionAllLightsOff(FunctionEvent e)
    {
	// empty
    }

    /**
     * "HailRequest" command.
     *
     * @see <CODE>FUNCTION_HAIL_REQUEST</CODE>
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionHailRequest(FunctionEvent e)
    {
	// empty
    }

    /**
     * "Hail Acknowledge" command.
     *
     * @see <CODE>FUNCTION_HAIL_ACKNOWLEDGE</CODE>
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionHailAcknowledge(FunctionEvent e)
    {
	// empty
    }

    /**
     * "Preset Dim 1" command.
     *
     * @see <CODE>FUNCTION_PRESET_DIM_1</CODE>
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionPresetDim1(FunctionEvent e)
    {
	// empty
    }

    /**
     * "Preset Dim 2" command.
     *
     * @see <CODE>FUNCTION_PRESET_DIM_2</CODE>
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionPresetDim2(FunctionEvent e)
    {
	// empty
    }

    /**
     * "Status On" command.
     *
     * @see <CODE>FUNCTION_STATUS_ON</CODE>
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionStatusOn(FunctionEvent e)
    {
	// empty
    }

    /**
     * "Status Off" command.
     *
     * @see <CODE>FUNCTION_STATUS_OFF</CODE>
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionStatusOff(FunctionEvent e)
    {
	// empty
    }

    /**
     * "Status Request" command.
     *
     * @see <CODE>FUNCTION_STATUS_REQUEST</CODE>
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionStatusRequest(FunctionEvent e)
    {
	// empty
    }
}





