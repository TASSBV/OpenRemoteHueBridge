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

import gnu.io.SerialPort;
import com.jpeterson.util.HexFormat;
import com.jpeterson.x10.event.AddressEvent;
import com.jpeterson.x10.event.BrightEvent;
import com.jpeterson.x10.event.DimEvent;
import com.jpeterson.x10.event.OffEvent;
import com.jpeterson.x10.event.OnEvent;

/**
 * Encapsulates the commands to send and then sends them.
 *
 * @author Jesse Peterson <jesse@jpeterson.com>
 */
public class CM17AStandardTransmission extends Object
        implements CM17ATransmissionEvent
{
    /** data packets are composed of five bytes. Byte 0 and 1 are
        header, 2 and 3 are the data, and 5 is the footer. */
    private byte[] packet = {(byte)0xD5,  // header high byte
                             (byte)0xAA,  // header low byte
                             (byte)0,     // data high byte
                             (byte)0,     // data low byte
                             (byte)0xAD}; // footer byte

    /** The device number only requires 8 unique values. Devices 9 through
        16 are repesented as the same device number for 1 through 8, with
        one bit set to indicate that the device is from the upper range. */
    private static final byte[] devices = {(byte)0x00,   // Device 1 (9)
                                           (byte)0x10,   // Device 2 (10)
                                           (byte)0x08,   // Device 3 (11)
                                           (byte)0x18,   // Device 4 (12)
                                           (byte)0x40,   // Device 5 (13)
                                           (byte)0x50,   // Device 6 (14)
                                           (byte)0x48,   // Device 7 (15)
                                           (byte)0x58};  // Device 8 (16)

    /** Bit set when the device number is 9 through 16 */
    private static final byte highDevice = (byte)0x04;

    /** Function for Bright command. */
    private static final byte bright = (byte)0x88;
    /** Function for Dim command. */
    private static final byte dim = (byte)0x98;

    /** House codes */
    private static final byte[] houseCodes = {(byte)0x60,  // 'A'
                                              (byte)0x70,  // 'B'
                                              (byte)0x40,  // 'C'
                                              (byte)0x50,  // 'D'
                                              (byte)0x80,  // 'E'
                                              (byte)0x90,  // 'F'
                                              (byte)0xA0,  // 'G'
                                              (byte)0xB0,  // 'H'
                                              (byte)0xE0,  // 'I'
                                              (byte)0xF0,  // 'J'
                                              (byte)0xC0,  // 'K'
                                              (byte)0xD0,  // 'L'
                                              (byte)0x00,  // 'M'
                                              (byte)0x10,  // 'N'
                                              (byte)0x20,  // 'O'
                                              (byte)0x30}; // 'P'

    /** Function bit for Off command. When this bit is set, it is an Off
        command. When not set, it is an On command. */
    private static final byte off = (byte)0x20;

    /** Index into packet for the high byte and low byte of the data. */
    private static final int HIGH_BYTE = 2;
    private static final int LOW_BYTE = 3;

    /** Number of times to transmit the command represented by this object.
        Required for transmitting multiple Dim/Bright commands as each
        command represents a 5% change. -jep It looks like it sends 12%
        per command. */
    private int numberToTransmit;
    private static final int PERCENT = 12;

    private static final int WAIT = 1;
    private static final int STANDBY = 50;
    private static final int DONE = 1000;

    /**
     * Create
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public CM17AStandardTransmission(AddressEvent address, OnEvent event)
    {
        int device;

        device = address.getDeviceCode();
        packet[HIGH_BYTE] = getHouseByte(address.getHouseCode());
        packet[LOW_BYTE] = getDeviceByte(device);
        if (device > 8)
        {
            packet[HIGH_BYTE] |= highDevice;
        }
        numberToTransmit = 1;
    }
    
    /**
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public CM17AStandardTransmission(AddressEvent address, OffEvent event)
    {
        int device;

        device = address.getDeviceCode();
        packet[HIGH_BYTE] = getHouseByte(address.getHouseCode());
        packet[LOW_BYTE] = getDeviceByte(device);
        if (device > 8)
        {
            packet[HIGH_BYTE] |= highDevice;
        }
        packet[LOW_BYTE] |= off;
        numberToTransmit = 1;
    }
    
    /**
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public CM17AStandardTransmission(DimEvent event)
    {
        int dims, dimMax, percent, factor, remainder;

        packet[HIGH_BYTE] = getHouseByte(event.getHouseCode());
        packet[LOW_BYTE] = dim;

        dims = event.getDims();
        dimMax = event.getDimMax();
        percent = Math.round((float)(((float)dims / (float)dimMax) * 100.0));

        remainder = percent % PERCENT;

        if (remainder > 0)
        {
            factor = PERCENT - (remainder);

            if (factor >= Math.round((float)((float)PERCENT / 2.0)))
            {
                percent += factor;
            }
            else
            {
                percent -= factor;
            }
        }
        numberToTransmit = (int)(percent / PERCENT);
    }
    
    /**
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public CM17AStandardTransmission(BrightEvent event)
    {
        int dims, dimMax, percent, factor, remainder;

        packet[HIGH_BYTE] = getHouseByte(event.getHouseCode());
        packet[LOW_BYTE] = bright;

        dims = event.getDims();
        dimMax = event.getDimMax();
        percent = Math.round((float)(((float)dims / (float)dimMax) * 100.0));

        remainder = percent % PERCENT;

        if (remainder > 0)
        {
            factor = PERCENT - (remainder);

            if (factor >= Math.round((float)((float)PERCENT / 2.0)))
            {
                percent += factor;
            }
            else
            {
                percent -= factor;
            }
        }
        numberToTransmit = (int)(percent / PERCENT);
    }

    /**
     * Get the byte representing the house code.
     *
     * @param houseCode The character for the house code; 'A' through 'P',
     *        upper case, inclusive.
     * @exception IllegalArgumentException Thrown if the house code is
     *            invalid.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    protected static byte getHouseByte(char houseCode)
    {
        if ((houseCode < 'A') || (houseCode > 'P'))
        {
            throw new IllegalArgumentException("House code " + houseCode + " is invalid. House code must be 'A' through 'P', upper case, inclusive.");
        }

        return(houseCodes[houseCode - 'A']);
    }

    /**
     * Get the byte representing the device code.
     *
     * @param device The number for the device code; 1 through 16,
     *        inclusive.
     * @exception IllegalArgumentException Thrown if the device code is
     *            invalid.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    protected static byte getDeviceByte(int device)
    {
        if ((device < 1) || (device > 16))
        {
            throw new IllegalArgumentException("Device " + device + " is invalid. Device must be 1 through 16, inclusive.");
        }

        return(devices[(device - 1) % 8]);
    }
    
    /**
     * Transmit this event to the CM17A device.
     *
     * @param port Serial port to send the event to.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void transmit(SerialPort port)
    {
        if (System.getProperty("DEBUG") != null)
        {
            System.out.println("Sending CM17AStandardTransmission");
            HexFormat hex = new HexFormat();
            System.out.print("PC->CM17A: ");
            String prefix = "";
            for (int k = 0; k < packet.length; k++)
            {
                System.out.print(prefix + "0x" + hex.format(packet[k]));
                prefix = ",";
            }
            System.out.println();
            System.out.println("Number to transmit: " + numberToTransmit);
        }

        // standby
        if (!port.isRTS())
        {
            port.setRTS(true);
        }
        if (!port.isDTR())
        {
            port.setDTR(true);
        }
        // wait
        try
        {
            Thread.sleep(STANDBY);
        }
        catch (InterruptedException e)
        {
        }

        if (port != null)
        {
            for (int i = 0; i < numberToTransmit; i++)
            {
                if (System.getProperty("DEBUG") != null)
                {
                    System.out.println("Transmission " + i);
                }
                for (int j = 0; j < packet.length; j++)
                {
                    for (int k = 7; k >= 0; k--)
                    {
                        // send wait
//System.out.print("  Wait:");
                        if (!port.isRTS())
                        {
//System.out.print(" set RTS");
                            port.setRTS(true);
                        }
                        if (!port.isDTR())
                        {
//System.out.print(" set DTR");
                            port.setDTR(true);
                        }

                        // wait
                        try
                        {
                            Thread.sleep(WAIT);
                        }
                        catch (InterruptedException e)
                        {
                        }

                        // send data
                        if ((packet[j] & (1 << k)) == (1 << k))
                        {
                            // logical 1
//System.out.print("  1: reset DTR");
                            port.setDTR(false);
                        }
                        else
                        {
                            // logical 0
//System.out.print("  0: reset DTR");
                            port.setRTS(false);
                        }

                        // wait
                        try
                        {
                            Thread.sleep(WAIT);
                        }
                        catch (InterruptedException e)
                        {
                        }
                    }
                }

                // send wait
                if (!port.isRTS())
                {
                    port.setRTS(true);
                }
                if (!port.isDTR())
                {
                    port.setDTR(true);
                }

                // wait
                try
                {
                    Thread.sleep(DONE);
                }
                catch (InterruptedException e)
                {
                }
            }

            if (numberToTransmit > 0)
            {
                // send wait
//System.out.print("  Wait:");
                if (!port.isRTS())
                {
//System.out.print(" set RTS");
                    port.setRTS(true);
                }
                if (!port.isDTR())
                {
//System.out.print(" set DTR");
                    port.setDTR(true);
                }

                // wait
                try
                {
                    Thread.sleep(WAIT);
                }
                catch (InterruptedException e)
                {
                }
            }
        }
    }
}
