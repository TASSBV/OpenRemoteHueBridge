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

package com.jpeterson.util;

import java.io.Serializable;

public class Condition extends Object implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private boolean isTrue;

    /**
     * Create a new condition variable in a known state.
     * <P>
     * Derived from Java World article
     *
     * @param isTrue initial state of condition variable
     *
     * @author Allen I. Holub
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public Condition(boolean isTrue)
    {
	this.isTrue = isTrue;
    }

    /**
     * See if the condition variable is true (without releasing).
     *
     * @return state of the condition variable
     *
     * @author Allen I. Holub
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public synchronized boolean isTrue()
    {
	return(isTrue);
    }

    /**
     * Set the condition to false. Waiting threads are not affected.
     *
     * @author Allen I. Holub
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public synchronized void setFalse()
    {
	isTrue = false;
    }

    /**
     * Set the condition to true. Waiting threads are not affected.
     *
     * @author Allen I. Holub
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public synchronized void setTrue()
    {
	isTrue = true;
	notifyAll();
    }

    /**
     * Release all waiting threads without setting the condition true.
     *
     * @author Allen I. Holub
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public synchronized void releaseAll()
    {
	notifyAll();
    }

    /**
     * Release one waiting thread without setting the condition true.
     *
     * @author Allen I. Holub
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public synchronized void releaseOne()
    {
	notify();
    }

    /**
     * Wait for the condition to become true.
     *
     * @param timeout Timeout in milliseconds
     *
     * @author Allen I. Holub
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public synchronized void waitForTrue(long timeout)
	throws InterruptedException
    {
	if (!isTrue) {
	    wait(timeout);
	}
    }

    /**
     * Wait (potentially forever) for the condition to become true.
     *
     * @param timeout Timeout in milliseconds
     *
     * @author Allen I. Holub
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public synchronized void waitForTrue() throws InterruptedException
    {
	if (!isTrue) {
	    wait();
	}
    }
}
