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

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * A pool of <CODE>Socket</CODE>'s.
 *
 * @author Jesse Peterson (<A HREF="mailto:jesse@jpeterson.com">jesse@jpeterson.com</A>)
 */
public class SocketObjectPool extends ObjectPool
{
    /**
     * Address of sockets in the pool.
     */
    protected InetAddress address;

    /**
     * Port of sockets in the pool.
     */
    protected int port;

    /**
     * Create an object pool of <CODE>Socket</CODE>'s for the specified
     * hostname and port. Expiration is set to the default value.
     *
     * @param hostname Host name or IP address for sockets in the pool.
     * @param port Port for sockets in the pool.
     *
     * @author Jesse Peterson (<A HREF="mailto:jesse@jpeterson.com">jesse@jpeterson.com</A>)
     */
    public SocketObjectPool(String hostname, int port)
        throws UnknownHostException
    {
        this(InetAddress.getByName(hostname), port, DEFAULT_EXPIRATION);
    }

    /**
     * Create an object pool of <CODE>Socket</CODE>'s for the specified
     * hostname and port with the specified expiration.
     *
     * @param hostname Host name or IP address for sockets in the pool.
     * @param port Port for sockets in the pool.
     * @param expiration Expiration in milliseconds for sockets in the
     *        pool.
     *
     * @author Jesse Peterson (<A HREF="mailto:jesse@jpeterson.com">jesse@jpeterson.com</A>)
     */
    public SocketObjectPool(String hostname, int port, long expiration)
        throws UnknownHostException
    {
        this(InetAddress.getByName(hostname), port, expiration);
    }

    /**
     * Create an object pool of <CODE>Socket</CODE>'s for the specified
     * address and port. Expiration is set to the default value.
     *
     * @param address Address for sockets in the pool.
     * @param port Port for sockets in the pool.
     *
     * @author Jesse Peterson (<A HREF="mailto:jesse@jpeterson.com">jesse@jpeterson.com</A>)
     */
    public SocketObjectPool(InetAddress address, int port)
    {
        this(address, port, DEFAULT_EXPIRATION);
    }

    /**
     * Create an object pool of <CODE>Socket</CODE>'s for the specified
     * address and port with the specified expiration.
     *
     * @param address Address for sockets in the pool.
     * @param port Port for sockets in the pool.
     * @param expiration Expiration in milliseconds for sockets in the
     *        pool.
     *
     * @author Jesse Peterson (<A HREF="mailto:jesse@jpeterson.com">jesse@jpeterson.com</A>)
     */
    public SocketObjectPool(InetAddress address, int port, long expiration)
    {
        super(expiration);
        this.address = address;
        this.port = port;
    }

    /**
     * Create a new instance of a <CODE>Socket</CODE>.
     *
     * @return A new instance of a <CODE>Socket</CODE> or <CODE>null</CODE>
     *         if unable to create one.
     *
     * @author Jesse Peterson (<A HREF="mailto:jesse@jpeterson.com">jesse@jpeterson.com</A>)
     */
    protected Object create()
    {
        Socket socket;

        try
        {
            socket = new Socket(address, port);
        }
        catch (IOException e)
        {
            return(null);
        }

        return(socket);
    }

    /**
     * Always returns <CODE>true</CODE>.
     *
     * @return Always returns <CODE>true</CODE>.
     *
     * @author Jesse Peterson (<A HREF="mailto:jesse@jpeterson.com">jesse@jpeterson.com</A>)
     */
    protected boolean validate(Object o)
    {
        return(true);
    }

    /**
     * Clean up resources on this expired object.
     *
     * @param o Object that has expired.
     *
     * @author Jesse Peterson (<A HREF="mailto:jesse@jpeterson.com">jesse@jpeterson.com</A>)
     */
    protected void expire(Object o)
    {
        Socket socket;

        if (o instanceof Socket)
        {
            socket = (Socket)o;
            try
            {
                socket.close();
            }
            catch (IOException e)
            {
            }
        }
    }

    /**
     * Borrow an object from the pool. You are obliged to replace the
     * borrowed object once you are through with it via a call to either
     * <CODE>returnObject</CODE> or <CODE>returnBrokenObject</CODE>.
     *
     * @return Available object or <CODE>null</CODE> if no objects are
     *         available.
     * @see returnObject
     * @see returnBrokenObject
     *
     * @author Jesse Peterson (<A HREF="mailto:jesse@jpeterson.com">jesse@jpeterson.com</A>)
     */
    public Object borrowObject()
    {
        return(checkOut());
    }

    /**
     * Return a borrowed object borrowed from a call to
     * <CODE>borrowObject</CODE>.
     *
     * @param o Borrowed object being returned.
     * @see borrowObject
     * @see returnBrokenObject
     *
     * @author Jesse Peterson (<A HREF="mailto:jesse@jpeterson.com">jesse@jpeterson.com</A>)
     */
    public void returnObject(Object o)
    {
        checkIn(o);
    }

    /**
     * Return a broken borrowed object borrowed from a call to
     * <CODE>borrowObject</CODE>.
     *
     * @param o Broken borrowed object being returned.
     * @see borrowObject
     * @see returnObject
     *
     * @author Jesse Peterson (<A HREF="mailto:jesse@jpeterson.com">jesse@jpeterson.com</A>)
     */
    public void returnBrokenObject(Object o)
    {
        broken(o);
    }
}
