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
 * Producers fire events to AddressListeners and FunctionListeners. An
 * example of a producer may be a software widget that represents a light
 * switch.  When the switch is turned on, the registered AddressListeners
 * and FunctionListeners will be notified.  For actual transmission of an
 * X10Event through a gateway, it may be desirable to use the Transmitter
 * interface.
 *
 * @author Jesse Peterson <jesse@jpeterson.com>
 */
public interface Producer
{
    /**
     * Add an AddressListener.  This Producer will send all registered
     * AddressListeners AddressEvents as they are produced by the Producer.
     *
     * @param listener Listener to add.
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void addAddressListener(AddressListener listener);

    /**
     * Remove the first instance of the specified listener from the register
     * list of AddressListeners.
     *
     * @param listener Listner to remove.
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void removeAddressListener(AddressListener listener);

    /**
     * Add a FunctionListener.  This Producer will send all registered
     * FunctionListeners FunctionEvents as they are produced by the Producer.
     *
     * @param listener Listener to add.
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void addFunctionListener(FunctionListener listener);

    /**
     * Remove the first instance of the specified listener from the register
     * list of FunctionListeners.
     *
     * @param listener Listner to remove.
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void removeFunctionListener(FunctionListener listener);
}
