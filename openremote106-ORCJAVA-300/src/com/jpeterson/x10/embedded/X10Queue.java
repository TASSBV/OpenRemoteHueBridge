/*
 * Copyright (C) 2000  Jesse E. Peterson
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

package com.jpeterson.x10.embedded;

import java.util.Vector;

/**
 * Implements a simple FIFO queue.  If no queue elements when <code>get</code>
 * is called, method will wait till a <code>put</code> is performed.
 *
 * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
 */
public class X10Queue
{
    private Vector<Object> queue;

    /**
     * Create a new queue with an initial capacity of 20.
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    public X10Queue()
    {
        this(20);
    }

    /**
     * Create a new queue with the specified initial capacity.
     *
     * @param initCapactiy Initial number of free slots in the queue.  Will grow
     *        to support more elements as needed.
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    public X10Queue(int initCapacity)
    {
        queue = new Vector<Object>(initCapacity);
    }

    /**
     * Put an element on the end of the queue.
     *
     * @param obj Object to put end of the queue.
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    public synchronized void put(Object obj)
    {
        //System.out.println("Placing object " + obj + " on queue");  // DEBUG
        queue.addElement(obj);

        //System.out.println("Notifying...");  // DEBUG
        notifyAll();
        //System.out.println("Done notifying");  // DEBUG
    }

    /**
     * Get an element from the beginning of the queue.  If there are no available
     * elements, the method will block for an available element.
     *
     * @return Returns the element at the beginning of the queue.
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    public synchronized Object get()
    {
        //System.out.println("Getting object");  // DEBUG
        if (queue.size() <= 0)
        {
            try
            {
                //System.out.println("Waiting for next object.");  // DEBUG
                wait();
                //System.out.println("Got a message to return.");  // DEBUG
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();  // TODO: enhance error handling
            }
        }

        Object obj = queue.firstElement();
        queue.removeElementAt(0);
        return(obj);
    }
}
