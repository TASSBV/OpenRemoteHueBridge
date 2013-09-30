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

package com.jpeterson.x10server;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import com.jpeterson.pool.SocketObjectPool;

/**
 * A pool of <CODE>Socket</CODE>'s for <CODE>X10Server</CODE>'s
 *
 * @author Jesse Peterson (<A HREF="mailto:jesse@jpeterson.com">jesse@jpeterson.com</A>)
 */
public class X10ServerSocketObjectPool extends SocketObjectPool
{
    /**
     * Create an object pool of <CODE>Socket</CODE>'s for the specified
     * hostname and port. Expiration is set to the default value.
     *
     * @param hostname Host name or IP address for sockets in the pool.
     * @param port Port for sockets in the pool.
     *
     * @author Jesse Peterson (<A HREF="mailto:jesse@jpeterson.com">jesse@jpeterson.com</A>)
     */
    public X10ServerSocketObjectPool(String hostname, int port)
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
    public X10ServerSocketObjectPool(String hostname, int port, long expiration)
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
    public X10ServerSocketObjectPool(InetAddress address, int port)
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
    public X10ServerSocketObjectPool(InetAddress address, int port, long expiration)
    {
        super(address, port, expiration);
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
        BufferedInputStream x10rsp;
        Socket socket = (Socket)super.create();
        byte[] bytes = new byte[80];
        int count;
        StringBuffer buf = new StringBuffer();

        if (System.getProperty("DEBUG") != null)
        {
            System.out.println("Creating new X10Server connection.");
        }
        try
        {
            if (socket != null)
            {
                // ensure connection is good. Wait for prompt
                x10rsp = new BufferedInputStream(socket.getInputStream());

                do
                {
                    count = x10rsp.read(bytes);

                    if (count < 0)
                    {
                        // end of connection
                        socket = null;
                    }
                    if (count > 0)
                    {
                        buf.append(new String(bytes, 0, count));
                    }
                } while ((buf.toString()).indexOf(X10Server.PROMPT) == -1);
            }
        }
        catch (IOException e)
        {
            try
            {
                if (socket != null)
                {
                    socket.close();
                }
            }
            catch (IOException ex)
            {
            }
            finally
            {
                socket  = null;
            }
        }

        return(socket);
    }

    /**
     * Make sure that the X10Server is still there.
     *
     * @return Always returns <CODE>true</CODE>.
     *
     * @author Jesse Peterson (<A HREF="mailto:jesse@jpeterson.com">jesse@jpeterson.com</A>)
     */
    protected boolean validate(Object o)
    {
        Socket socket;
        StringBuffer buf;
        int count;

        if (o instanceof Socket)
        {
            String command = "\n";
            byte[] bytes = command.getBytes();
            socket = (Socket)o;
            try
            {
                buf = new StringBuffer();
                OutputStream out = socket.getOutputStream();
                out.write(bytes);
                out.flush();
                InputStream in = socket.getInputStream();
                bytes = new byte[20];
                do
                {
                    if (in.available() == 0)
                    {
                        try
                        {
                            Thread.sleep(100);
                        }
                        catch (InterruptedException e)
                        {
                        }
                    }
                    count = in.read(bytes);
                    if (count < 0)
                    {
                        // end of stream
                        return(false);
                    }
                    if (count > 0)
                    {
                        buf.append(new String(bytes, 0, count));
                    }
                } while ((buf.toString()).indexOf("\n>") == -1);
                return(true);
            }
            catch (IOException e)
            {
                return(false);
            }
        }
        return(false);
    }
}
