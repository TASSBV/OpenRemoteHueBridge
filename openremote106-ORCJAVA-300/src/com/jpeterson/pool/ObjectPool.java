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

package com.jpeterson.pool;

import java.util.Hashtable;
import java.util.Enumeration;

/**
 * Base functionality for implementating a pool of objects. Based on
 * the article
 * <A HREF="http://www.javaworld.com/javaworld/jw-06-1998/jw-06-object-pool.html">
 * <I>Build your own ObjectPool in Java to boost app speed</I></A> from the
 * June 1998 issue of <A HREF="http://www.javaworld.com">JavaWorld</A>.
 *
 * @author Thomas E. Davis
 * @author Jesse Peterson <jesse@jpeterson.com>
 */
public abstract class ObjectPool
{
    /**
     * The amount of time an object can be in the pool.
     */
    private long expiration;

    /**
     * This data element contains the objects that are currently checked
     * out. The actual object is the key of the hashtable with a
     * <CODE>Long</CODE> containing the time when the object was checked
     * out in milliseconds as the value in the hashtable.
     */
    protected Hashtable<Object, Long> locked;

    /**
     * This data element contains the objects are available in the pool.
     * The actual object is the key of the hashtable with a <CODE>Long</CODE>
     * containing the expiration time of the object in milliseconds as the
     * value in the hashtable.
     */
    protected Hashtable<Object, Long> unlocked;

    /**
     * Default expiration is 30 seconds.
     */
    public static final int DEFAULT_EXPIRATION = 1000 * 30;

    /**
     * Create an <CODE>ObjectPool</CODE> with default expiration.
     *
     * @author Thomas E. Davis
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public ObjectPool()
    {
        this(30000);
    }

    /**
     * Create an <CODE>ObjectPool</CODE> with the specified expiration.
     *
     * @param expiration Duration that objects can be in the pool before
     *        expiring. Expressed in milliseconds.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public ObjectPool(long expiration)
    {
        setExpiration(expiration);
        locked = new Hashtable<Object, Long>();
        unlocked = new Hashtable<Object, Long>();
    }

    /**
     * Set the expiration for objects in the pool.
     *
     * @param expiration Duration that objects can be in the pool before
     *        expiring. Expressed in milliseconds.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void setExpiration(long expiration)
    {
        this.expiration = expiration;
    }

    /**
     * Get the expiration for objects in the pool.
     *
     * @return The duration that objects can be in the pool before expiring.
     *         Expressed in milliseconds.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public long getExpiration()
    {
        return(expiration);
    }

    /**
     * Called by <CODE>checkOut</CODE> when a new object is needed to fulfill
     * a request. May return <CODE>null</CODE> if a new object can not be
     * created.
     *
     * @return New object or <CODE>null</CODE> if a new object can not be
     *         created.
     *
     * @author Thomas E. Davis
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    protected abstract Object create();

    /**
     * Called by <CODE>checkOut</CODE> when using an object from the pool.
     * Objects created via <CODE>create</CODE> are assumed to be valid.
     * Object that are valid are assumed to be ready for use.
     *
     * @param o The object to validate.
     * @return Returns <CODE>true<CODE> if the object is "valid",
     *         <CODE>false</CODE> otherwise.
     *
     * @author Thomas E. Davis
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    protected abstract boolean validate(Object o);

    /**
     * Called by <CODE>checkOut</CODE> when an object fails validation.
     * This gives an object a chance to free any of its allocated resources.
     *
     * @param o The object that has just expired.
     *
     * @author Thomas E. Davis
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    protected abstract void expire(Object o);

    /**
     * Retrieve an available object from the pool. If no objects can be
     * provided, <CODE>null</CODE> is returned.
     *
     * @return An object from the available pool.  May be <CODE>null</CODE>
     *         if no objects are available.
     *
     * @author Thomas E. Davis
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    protected synchronized Object checkOut()
    {
        long now = System.currentTimeMillis();
        Object o;
        if (unlocked.size() > 0)
        {
            Enumeration<Object> e = unlocked.keys();
            while (e.hasMoreElements())
            {
                o = e.nextElement();
                if ((now - ((Long)unlocked.get(o)).longValue()) > expiration)
                {
                    // object has expired
                    unlocked.remove(o);
                    expire(o);
                    o = null;
                }
                else
                {
                    if (validate(o))
                    {
                        unlocked.remove(o);
                        locked.put(o, new Long(now));
                        return(o);
                    }
                    else
                    {
                        // object failed validation
                        unlocked.remove(o);
                        expire(o);
                        o = null;
                    }
                }
            }
        }
        // no objects available, create a new one
        o = create();
        if (o != null)
        {
            locked.put(o, new Long(now));
        }
        return(o);
    }

    /**
     * Return an object to the pool.
     *
     * @param o Object originally checked out of the pool.
     *
     * @author Thomas E. Davis
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    protected synchronized void checkIn(Object o)
    {
        locked.remove(o);
        unlocked.put(o, new Long(System.currentTimeMillis()));
    }

    /**
     * Return a broken object to the pool. The method <CODE>expire</CODE>
     * will be called to expire the object.
     *
     * @param o Object originally checked out of the pool.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    protected synchronized void broken(Object o)
    {
        locked.remove(o);
        expire(o);
    }
}
