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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import com.jpeterson.x10.event.FunctionEvent;

/**
 * A macro, when triggered by being initiated by a <CODE>TimerInitiator</CODE>
 * or <CODE>MacroInitiator</CODE>, will issue X10 events. A macro's execution
 * can be delayed up to 4 hours once it is initiaited. A macro contains one or
 * more macro element.  Each macro element is an X10 function. When the macro
 * executes, all of the macro's MacroElement function's will be executed.
 *
 * @author Jesse Peterson <jesse@jpeterson.com>
 */
public class Macro implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** Size of macro header. */
    private static final int HEADER_SIZE = 2;

    /** Maximum number of macro elements. */
    private static final int MAX_ELEMENTS = 255;

    /**
     * Offset in minutes before the macro's functions are executed.
     */
    private int timerOffset;

    /**
     * Contains all of the MacroElements for this macro. The MacroElements are
     * keyed in the hashtable based on their FunctionEvent.
     */
    private Hashtable<FunctionEvent, MacroElement> elements;

    /**
     * Create a new macro with no timer offset.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public Macro()
    {
        this(0);
    }

    /**
     * Create a new macro. Each macro can multiple macro elements performing
     * multiple functions.
     *
     * @param timerOffset Offset in minutes from the time at which the macro is
     *        called to the time when the macro actually takes effect. 0 to
     *        240 minutes (4 hours).
     * @exception IllegalArgumentException Thrown if timer offset is less than
     *            0 or greater than 240.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public Macro(int timerOffset)
    {
        if ((timerOffset < 0) || (timerOffset > 240))
        {
            throw new IllegalArgumentException("Macro timer offset minutes must be between 0 and 240");
        }
        elements = new Hashtable<FunctionEvent, MacroElement>();
        this.timerOffset = timerOffset;
    }

    /**
     * Retrieve the macro's timer offset value.
     *
     * @return The macro's timer offset in minutes
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public int getTimerOffset()
    {
        return(timerOffset);
    }

    /**
     * Add a command for this macro to execute.  When the macro is executed,
     * this command will be carried out. The house code for the address and
     * function must both be the same. The maximum number of commands is 255.
     * Commands added after 255 are silently ignored.
     *
     *
     * @param device Device number.  Must be between 1 and 16, inclusive.
     * @param function A FunctionEvent for a command that the macro will execute.
     *        For proper use in this case for a macro, the source of the X10Event
     *        will be ignored.
     * @exception IllegalArgumentException Thrown if the device number is less
     *            than 1 or greater than 16.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public synchronized void addCommand(int device, FunctionEvent function)
        throws IllegalArgumentException
    {
        addCommand(device, function, false);
    }

    /**
     * Add a command for this macro to execute.  When the macro is executed,
     * this command will be carried out. The house code for the address and
     * function must both be the same. The maximum number of commands is 255.
     * Commands added after 255 are silently ignored.
     *
     *
     * @param device Device number.  Must be between 1 and 16, inclusive.
     * @param function A FunctionEvent for a command that the macro will execute.
     *        For proper use in this case for a macro, the source of the X10Event
     *        will be ignored.
     * @param brightenFirst If true and Functin Event is a dim or bright command,
     *        the device will be brought to maximum brightness before applying the
     *        dim amount. This functionality is global per dim/bright command per
     *        macro. It is updated each time a device is added.
     * @exception IllegalArgumentException Thrown if the device number is less
     *            than 1 or greater than 16.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public synchronized void addCommand(int device, FunctionEvent function,
                                        boolean brightenFirst)
        throws IllegalArgumentException
    {
        MacroElement element;

        if (((element = (MacroElement)elements.get(function)) == null) &&
            (elements.size() < MAX_ELEMENTS))
        {
            // new element
            element = new MacroElement(function);
            elements.put(function, element);
        }

        element.addDevice(device);
        element.setBrightenFirst(brightenFirst);
    }

    /**
     * Add a command for this macro to execute. When the macro is executed,
     * this command will be carried out. The maximum number of commands is 255.
     * Commands added after 255 are silently ignored.
     *
     * @param function A FunctionEvent command that the macro will execute.
     *        For proper use in this case for a macro, the source of the X10Event
     *        will be ignored.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public synchronized void addCommand(FunctionEvent function)
    {
        MacroElement element;

        if (((element = (MacroElement)elements.get(function)) == null) &&
            (elements.size() < MAX_ELEMENTS))
        {
            // new element
            element = new MacroElement(function);
            elements.put(function, element);
        }
    }

    /**
     * This method provides a way to remove a macro element from the macro.
     * You can retrieve all of the macro elements in this macro from the
     * <CODE>elements</CODE> method.
     *
     * @param element the macro element to remove.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public synchronized void removeElement(MacroElement element)
    {
        elements.remove(element);
    }

    /**
     * Returns an enumeration of the MacroElements for this macro.
     *
     * @return An enumeration of the MacroElements in this macro.
     */
    public synchronized Enumeration<MacroElement> elements()
    {
        return(elements.elements());
    }

    /**
     * Determine if this Macro is equal to the target object.
     *
     * @param object Target object to determine if it is equal to this Macro.
     * @return True if the target object equals this one, false otherwise.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public boolean equals(Object object)
    {
        Macro target;

        if ((object == null) ||
            (!(object instanceof Macro)))
        {
            return(false);
        }

        target = (Macro)object;

        if (timerOffset == target.getTimerOffset())
        {
            MacroElement element, targetElement;
            FunctionEvent function;
            Hashtable<FunctionEvent, MacroElement> targetElements = new Hashtable<FunctionEvent, MacroElement>();

            // make a hashtable of the target elements.
            for (Enumeration<MacroElement> e = target.elements(); e.hasMoreElements(); )
            {
                targetElement = (MacroElement)e.nextElement();
                targetElements.put(targetElement.getFunction(), targetElement);
            }

            // check to see that the target contains all of this macro's elements
            for (Enumeration<FunctionEvent> e = elements.keys(); e.hasMoreElements(); )
            {
                function = (FunctionEvent)e.nextElement();
                targetElement = (MacroElement)targetElements.get(function);
                if (targetElement == null)
                {
                    return(false);
                }
                element = (MacroElement)elements.get(function);
                if (!(element.equals(targetElement)))
                {
                    return(false);
                }
            }

            // check to see that this macro contains all of the target macro's
            // elements
            for (Enumeration<FunctionEvent> e = targetElements.keys(); e.hasMoreElements(); )
            {
                function = (FunctionEvent)e.nextElement();
                element = (MacroElement)elements.get(function);
                if (element == null)
                {
                    return(false);
                }
                targetElement = (MacroElement)targetElements.get(function);
                if (!(element.equals(targetElement)))
                {
                    return(false);
                }
            }

            // if we got this far, they are equal
            return(true);
        }

        return(false);
    }

    /**
     * Get the byte array representing this macro and all of its macro
     * elements.
     *
     * @return Byte array containing the macro and all of its elements.
     * @exception ArrayIndexOutOfBoundsException if copying would cause access
     *            of data outside array bounds.
     * @exception ArrayStoreException if an element in the <CODE>src</CODE>
     *            array could not be stored into the <CODE>dest</CODE> array
     *            because of a type mismatch.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public synchronized byte[] getBytes()
    {
        byte[] component, element;
        MacroElement macroElement;
        int macroSize = 0, offset = 0;
        Vector<byte[]> byteArrays;

        byteArrays = new Vector<byte[]>();  // temporary storage of macro elements
                                    // byte arrays.

        // get all of the macro element byte arrays and determine size
        // of array necessary to hold all macro element byte arrays.
        for (Enumeration<MacroElement> e = elements.elements(); e.hasMoreElements(); )
        {
            macroElement = (MacroElement)e.nextElement();
            component = macroElement.getBytes();
            macroSize += component.length;

            byteArrays.addElement(component);
        }

        // allocate array to store entire macro
        element = new byte[HEADER_SIZE + macroSize];

        // encode delay
        element[offset++] = (byte)timerOffset;

        // encode number of macro elements
        element[offset++] = (byte)byteArrays.size();

        // copy macro element bytes to macro array
        for (Enumeration<byte[]> e = byteArrays.elements(); e.hasMoreElements(); )
        {
            component = (byte[])e.nextElement();
            System.arraycopy(component, 0, element, offset, component.length);
            offset += component.length;
        }

        return(element);
    }
}
