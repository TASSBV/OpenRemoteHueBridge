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

/**
 * This class provides a base for gateway modules that are based on
 * the serial port.  This base class provides methods to set and get
 * the serial port parameters.
 */
public abstract class SerialGateway extends GatewayImpl
{
    protected String portName;
    protected int baudRate;
    protected int dataBits;
    protected int stopBits;
    protected int parity;

    /**
     * Constructor only calls parent's constructor.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    protected SerialGateway()
    {
        super();
    }

    /**
     * Set the serial port to use. For Microsoft Windows, typical values
     * are COM1, COM2, COM3, or COM4.
     *
     * @param portName the name of the serial port
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void setPortName(String portName)
    {
        this.portName = portName;
    }

    /**
     * Get the serial port to use.
     *
     * @return Name of serial port to use.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public String getPortName()
    {
        return(portName);
    }

    /**
     * Set the serial port communication rate.
     *
     * @param baudRate the serial port communication rate
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void setBaudRate(int baudRate)
    {
        this.baudRate = baudRate;
    }

    /**
     * Get the serial port communication rate.
     *
     * @return Baud rate.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public int getBaudRate()
    {
        return(baudRate);
    }

    /**
     * Set the number of serial port data bits to use.
     *
     * @param dataBits the number of data bits to use
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void setDataBits(int dataBits)
    {
        this.dataBits = dataBits;
    }

    /**
     * Get the number of data bits to use.
     *
     * @return Number of data bits.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public int getDataBits()
    {
        return(dataBits);
    }

    /**
     * Set the serial port stop bits.
     *
     * @param stopBits the serial port stop bits.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void setStopBits(int stopBits)
    {
        this.stopBits = stopBits;
    }

    /**
     * Get the serial port stop bits.
     *
     * @return The serial port stop bits.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public int getStopBits()
    {
        return(stopBits);
    }

    /**
     * Set the serial port parity.
     *
     * @param parity the serial port parity.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void setParity(int parity)
    {
        this.parity = parity;
    }

    /**
     * Get the serial port parity.
     *
     * @return The serial port parity.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public int getParity()
    {
        return(parity);
    }
}
