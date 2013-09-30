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

import java.util.EventListener;

/**
 * @author Jesse Peterson <jesse@jpeterson.com>
 */
public interface FunctionListener extends EventListener
{
    /**
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionAllUnitsOff(FunctionEvent e);

    /**
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionAllLightsOn(FunctionEvent e);

    /**
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionOn(FunctionEvent e);

    /**
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionOff(FunctionEvent e);

    /**
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionDim(FunctionEvent e);

    /**
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionBright(FunctionEvent e);

    /**
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionAllLightsOff(FunctionEvent e);

    /**
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionHailRequest(FunctionEvent e);

    /**
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionHailAcknowledge(FunctionEvent e);

    /**
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionPresetDim1(FunctionEvent e);

    /**
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionPresetDim2(FunctionEvent e);

    /**
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionStatusOn(FunctionEvent e);

    /**
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionStatusOff(FunctionEvent e);

    /**
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionStatusRequest(FunctionEvent e);
}
