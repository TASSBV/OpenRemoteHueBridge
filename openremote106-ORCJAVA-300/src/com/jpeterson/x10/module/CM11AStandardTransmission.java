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

import java.io.EOFException;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.jpeterson.util.HexFormat;
import com.jpeterson.x10.InterruptedTransmissionException;
import com.jpeterson.x10.TooManyAttemptsException;

/**
 * Create a standard CM11 transmission event to transmit. Standard
 * transmission events are transmitted to the CM11 by a protocol
 * that provides safeguards to ensure that the message is sent
 * to the CM11 device correctly.  The safeguards implemeted are
 * a checksum algorythm.
 *
 * @author Jesse Peterson <jesse@jpeterson.com>
 */
public class CM11AStandardTransmission extends Object
    implements CM11ATransmissionEvent
{
    protected byte[] packet;
    private int attempts;
    private int maxAttempts;

    private static final byte CHECKSUM_OK = (byte)0x00;
    private static final byte INTERFACE_READY = (byte)0x55;

    /**
     * Create a standard CM11 transmission event to transmit the specified
     * packet of bytes.
     *
     * @param packet The packet of bytes to transmit to the CM11 interface
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public CM11AStandardTransmission(byte[] packet)
    {
        this.packet = packet;
        attempts = 0;
        setMaxAttempts(3);
    }

    /**
     * Transmit a standard CM11 command from the PC to the CM11 interface.
     * The standard transmission sends a packet of bytes, receives a 
     * checksum, validates the checksum, then recieves a transmission
     * success message from the interface indicating successful completion
     * of the transmission. If validation of the checksum or reception
     * of a transmission success message fails, the transmission is retried.
     *
     * @param in Input stream to read from
     * @param out Output stream to write to
     * @exception TooManyAttemptsException Too many retries have occurred
     * @exception InterruptedTransmissionException An unsolicited interrupt
     *            has been received during the transmission.
     * @exception IOException Some sort of I/O or I/O protocol error has
     *            occurred
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void transmit(InputStream in, OutputStream out)
            throws TooManyAttemptsException, InterruptedTransmissionException,
            EOFException, IOException
    {
        int result;
        byte byteValue;
        HexFormat hex = new HexFormat();

        // mark off an attempt
        ++attempts;

        if (attempts > maxAttempts)
        {
            throw new TooManyAttemptsException();
        }

        if (System.getProperty("DEBUG") != null)
        {
            System.out.println("Sending CM11AStandardTransmission");
            System.out.print("PC->CM11A: ");
            String prefix = "";
            for (int k = 0; k < packet.length; k++)
            {
                System.out.print(prefix + "0x" + hex.format(packet[k]));
                prefix = ",";
            }
            System.out.println();
        }

        // send packet
        out.write(packet);

        // read checksum
        if ((result = in.read()) == -1)
        {
            throw new EOFException("Expected checksum, received end of stream indicator.");
        }
        byteValue = (byte)result;
        if (System.getProperty("DEBUG") != null)
        {
            System.out.println("Received checksum: " + hex.format(byteValue));
            System.out.println("Expected checksum: " + hex.format(getChecksum()));
        }

        if (byteValue != getChecksum())
        {
            if ((byteValue == CM11A.CM11_RECEIVE_EVENT) ||
                (byteValue == CM11A.CM11_POWER_FAILURE) ||
                (byteValue == CM11A.CM11_MACRO_INITIATED))
            {
                throw new InterruptedTransmissionException(byteValue);
            }
            else
            {
                System.err.println("Breakdown in protocol, consuming all bytes in CM11AStandardTransmission after getChecksum().");

                // consume all bytes in input stream
                byte[] buffer = new byte[20];
                while (in.available() > 0)
                {
                    in.read(buffer);
                }

                // retransmit
                transmit(in, out);
                return;
            }
        }

        if (System.getProperty("DEBUG") != null)
        {
            System.out.println("PC<-CM11A: 0x" + hex.format(byteValue));
        }

        // checksum correct
        out.write(CHECKSUM_OK);

        if (System.getProperty("DEBUG") != null)
        {
            System.out.println("PC->CM11A: 0x" + hex.format(CHECKSUM_OK));
        }

 
        /*
         * 9th October 2007
         * 
         * It looks like  RCA HC60CRX takes about 700-800 milliseconds to execute 
         * the command (as I understand, this is by design of X10 protocol).
         * 
         * So, after acknowledging that the checksum perceived by CM11A was correct 
         * (i.e. sending  CHECKSUM_OK [(byte)0x00]), let's wait for the device 
         * to be ready to send INTERFACE_READY [(byte)0x55].
         * 
         * - Manish Pandya <manish at meetamanish dot com>
         */
        while (in.available() == 0) 
        {
            try 
            {
                Thread.sleep(100);
            } catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
        }


        // read "Interface Ready"
        if ((result = in.read()) == -1)
        {
            throw new EOFException("Expected 'Interface Ready', received end of stream indicator.");
        }
        byteValue = (byte)result;

        if (byteValue != INTERFACE_READY)
        {
            if ((byteValue == CM11A.CM11_RECEIVE_EVENT) ||
                (byteValue == CM11A.CM11_POWER_FAILURE) ||
                (byteValue == CM11A.CM11_MACRO_INITIATED))
            {
                throw new InterruptedTransmissionException(byteValue);
            }
            else
            {
                System.err.println("Breakdown in protocol, consuming all bytes in CM11AStandardTransmission after 'Interface Ready'.");

                // consume all bytes in input stream
                byte[] buffer = new byte[20];
                while (in.available() > 0)
                {
                    in.read(buffer);
                }

                // retransmit
                transmit(in, out);
                return;
            }
        }

        if (System.getProperty("DEBUG") != null)
        {
            System.out.println("PC<-CM11A: 0x" + hex.format(byteValue));
        }

        // transmission complete
    }

    /**
     * Retrieve the checksum of the bytes in the message or the X10
     * transmission.
     *
     * @return the checksum
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    protected byte getChecksum()
    {
        int sum = 0;

        for (int i = 0; i < packet.length; i++)
        {
            sum += packet[i];
        }
        return((byte)sum);
    }

    /**
     * Retrieve the number of transmission attempts.
     *
     * @return the number of transmission attempts
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public int getNumAttempts()
    {
        return(attempts);
    }

    /**
     * Set the number of transmission attempts
     *
     * @param maxAttempts the maximum number of transmission attempts
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void setMaxAttempts(int maxAttempts)
    {
        this.maxAttempts = maxAttempts;
    }

    /**
     * Create a string representation of the transmission.
     *
     * @return String representation of the transmission.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        HexFormat hexFormat = new HexFormat();
        String prefix = "";

        buffer.append("CM11AStandardTransmission - packet: [");
        for (int i = 0; i < packet.length; i++)
        {
            buffer.append(prefix).append("0x");
            buffer.append(hexFormat.format(packet[i]));
            prefix = ", ";
        }
        buffer.append("]");
        return(buffer.toString());
    }
}




