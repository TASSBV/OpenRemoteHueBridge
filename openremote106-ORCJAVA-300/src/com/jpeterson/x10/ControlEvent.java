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

import java.util.EventObject;

/**
 * The root event class for all control events. All events from an x10 engine
 * (transmitter or receiver) are synchronized with the AWT event queue. This
 * allows an application to mix control and AWT events without being concerned
 * with multi-threading problesm.
 * <P>
 * <STRONG>Note to Gateway Developers</STRONG>
 * <P>
 * The AWT event queue is obtained through the AWT Toolkit:
 * <BLOCKQUOTE>
 * <PRE>
 * import java.awt.*;
 * ...
 * EventQueue q = Toolkit.getDefaultToolkit().getSystemEventQueue();
 * </PRE>
 * </BLOCKQUOTE>
 * A gateway should create a sub-class of AWTEvent that can be placed on the
 * AWT event queue. The gateway also needs to create a non-visual AWT
 * <CODE>Component</CODE> to receive the gateway's <CODE>AWTEvent</CODE>. When
 * the AWT event is notified to the gateway's component, the gateway should issue
 * the appropriate control event. The control event can be issued either from
 * the AWT event thread or from a sparate thread created by the gateway.
 * (Note that <CODE>ControlEvent</CODE> is not a sub-class of
 * <CODE>AWTEvent</CODE> so control events can not be placed directly on the
 * AWT event queue.)
 *
 * @author Jesse Peterson <6/29/99>
 * @version 1.0
 */
public class ControlEvent extends EventObject
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Event identifier. Id values are defined for each sub-class of
     * <CODE>ControlEvent</CODE>.
     *
     * @see getId
     */
    protected int id;

    /**
     * Constructs a <CODE>ControlEvent</CODE> with a specified source. The
     * source must be non-null.
     *
     * @author Jesse Peterson <6/29/99>
     */
    protected ControlEvent(Object source)
    {
        super(source);
    }

    /**
     * Constructs a <CODE>ControlEvent</CODE>
     *
     * @param source the object that issued the event
     * @param id the identifier for the event type
     *
     * @author Jesse Peterson <6/29/99>
     */
    protected ControlEvent(Object source, int id)
    {
        super(source);
        this.id = id;
    }

    /**
     * Return the event identifier. Id values are defined for each sub-class
     * of <CODE>ControlEvent</CODE>.
     *
     * @author Jesse Peterson <6/29/99>
     */
    public int getId()
    {
        return(id);
    }

    /**
     * Returns a parameter string identifying this event. This method is
     * useful for event-logging and for debugging.
     *
     * @return a string identifying the event
     *
     * @author Jesse Peterson <6/29/99>
     */
    public String paramString()
    {
        return("Control Event - Source = [" + getSource() + "] ID = [" + getId() + "]");
    }

    /**
     * Return a printable String. Useful for event-logging and debugging.
     *
     * @author Jesse Peterson <6/29/99>
     */
    public String toString()
    {
        return(paramString());
    }
}
