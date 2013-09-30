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

package com.jpeterson.x10.module.event;

import com.jpeterson.x10.ControlEvent;
import com.jpeterson.x10.module.CM11A;

/**
 * @author Jesse Peterson <jesse@jpeterson.com>
 */
public class CM11AEvent extends ControlEvent
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The CM11A received a power failure event.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public static final int POWER_FAILURE = 21;

    /**
     * The CM11A received a macro initiated event.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public static final int MACRO_INITIATED = 22;

    /**
     * Create a new CM11A Event.
     *
     * @param source CM11A object that is sending the event.
     * @param id Type of event.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public CM11AEvent(CM11A source, int id)
    {
        super(source, id);
    }

    /**
     * Returns a parameter string identifying the event. This method is
     * useful for event-logging and for debugging.
     *
     * @return a string identifying the event
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public String paramString()
    {
        return("CM11AEvent - source = [" + getSource() + "] ID = [" + getId() + "]");
    }
}




