/*
 * Copyright (C) 1999  Jesse E. Peterson
 * Copyright (C) 2009  OpenRemote, Inc.
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
import com.jpeterson.x10.module.event.CM11AStatusEvent;
import com.jpeterson.x10.module.event.CM11AStatusListener;

/**
 * Create a status request. The CM11A performs monitoring on a certain house code. The status
 * request downloads the status of the monitored house code.
 * <p>
 *
 * A CM11A status request (code 0x8b) can be sent to the PLM unit at any time. The expected response
 * is a 14 byte payload structured as follows:
 *
 * <pre>
 * Bit range	Description
 * ---------- --------------------------------------------
 * 111 to 96  Battery timer (set to 0xffff on reset)
 *  95 to 88  Current time (seconds)
 *  87 to 80  Current time (minutes ranging from 0 to 119)
 *  79 to 72  Current time (hours/2, ranging from 0 to 11)
 *  71 to 63  Current year day (MSB bit 63)
 *  62 to 56  Day mask (SMTWTFS)
 *  55 to 52  Monitored house code
 *  51 to 48  Firmware revision level 0 to 15
 *  47 to 32  Currently addressed monitored devices
 *  31 to 16  On / Off status of the monitored devices
 *   15 to 0  Dim status of the monitored devices
 * </pre>
 *
 * @author Jesse Peterson <jesse@jpeterson.com>
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class CM11AStatusTransmission implements CM11ATransmissionEvent
{

    // Constants ----------------------------------------------------------------------------------

    /**
     * The CM11A code to send for a status request (0x8b).
     */
    private static final byte STATUS_REQ = (byte)0x8b;

    /**
     * The expected lenght in bytes of the CM11A response to a status request.
     */
    private static final int STATUS_SIZE = 14;

    /**
     * Timeout used (in milliseconds) where it is necessary to block the thread to wait for
     * I/O responses.
     */
    private static final int IO_TIMEOUT = 3000;


    // Instance Variables -------------------------------------------------------------------------

   /**
    * Indicates if protocol debug output should be enabled.
    *
    * TODO:
    *   The original implementation fetched the DEBUG system property before all checks to write
    *   debug statements -- this instance field shares the state for all use checks. Ultimately
    *   should integrate the class with a proper logging system.
    *                                                                      [JPL]
    */
    private boolean debug = false;

    private int attempts;
    private int maxAttempts;
    private CM11A cm11a;
    private CM11AStatusListener listener;


    // Constructors -------------------------------------------------------------------------------

    /**
     * Create a standard CM11 transmission event to request the status of the monitoring performed
     * by the CM11 interface.
     *
     * @param parent    The CM11A device to retrieve the status of.
     * @param listener  CM11AStatusListener to notify when status retrieved.
     *                  May be null.
     */
    public CM11AStatusTransmission(CM11A parent, CM11AStatusListener listener)
    {
        debug = System.getProperty("DEBUG") != null;

        attempts = 0;
        setMaxAttempts(3);
        cm11a = parent;
        this.listener = listener;
    }

    /**
     * Transmit a CM11 status command.
     *
     * @param   in  Input stream to read from
     * @param   out Output stream to write to
     *
     * @throws TooManyAttemptsException if too many retries have occurred
     * @throws InterruptedTransmissionException if an unsolicited interrupt has been received
     *         during the transmission.
     * @throws IOException if some sort of I/O or I/O protocol error has occurred
     */
    public void transmit(InputStream in, OutputStream out)
        throws TooManyAttemptsException, InterruptedTransmissionException,
        EOFException, IOException
    {
        byte[] buffer = new byte[STATUS_SIZE];
        int numBytesRead = 0;
        CM11AStatusEvent event;
        HexFormat hex = new HexFormat();


        // mark off an attempt...

        ++attempts;

        if (attempts > maxAttempts)
        {
            throw new TooManyAttemptsException();
        }

        if (debug)
        {
            System.out.println("Sending CM11AStatusTransmission");
            System.out.println("PC->CM11A: 0x" + hex.format(STATUS_REQ));
        }

        // send status request...

        out.write(STATUS_REQ);


        /*
         * IMPLEMENTATION NOTE:
         *
         *   The assumption on I/O use in original implementation doesn't match up to what real
         *   devices are doing -- responses to status request (sent above) do not return immediately
         *   or completely, despite the retries that are implemented which are executed too fast
         *   for CM11A unit to keep up (unless you get lucky).
         *
         *   Therefore adding some blocking here as a quick fix for issues showing up (tested
         *   against Marmitek CM11A unit). However, the rest of the implementation assumptions
         *   on unsolicited events still looks suspect, so this should be considered just a quick
         *   fix for the common use case (receive correct and full status response to status req.)
         *
         *   Longer term the I/O usage here should be revisited, and probably reimplemented, with
         *   different assumptions. The blocking and timeout usage means a different threading
         *   model may be necessary if more concurrency is desired.
         *                                                                                  [JPL]
         */
        final int blockingDelayIncrement = 100;  // milliseconds
        int currentBlockingDelay = 0;

        // Under normal circumstances we are expecting a 14 byte response from the CM11A unit, so
        // let's give it some time to arrive before we continue...

        while (currentBlockingDelay < IO_TIMEOUT)
        {
          if (in.available() < STATUS_SIZE)
          {
            try
            {
              currentBlockingDelay += blockingDelayIncrement;
              Thread.sleep(blockingDelayIncrement);
            }
            catch (InterruptedException e)
            {
              // Our thread was interrupted -- restore the interrupted status and exit...

              Thread.currentThread().interrupt();

              throw new IOException(
                  "Thread was interrupted while waiting for an answer to CM11A status request." + e
              );
            }
          }
          else
          {
            currentBlockingDelay = Integer.MAX_VALUE;
          }
        }


        if (debug)
        {
            System.out.println("Bytes available: " + in.available());
        }

        // expect STATUS_SIZE bytes to be returned...

        if (in.available() < STATUS_SIZE)
        {
            if (debug)
            {
                System.out.println("Not enough bytes for a status message yet");
            }

            // if we got here, we don't have the correct number of bytes
            // available, maybe we have an unsolicited event...

            int result;
            byte byteValue;

            result = in.read();

            if (result == -1)
            {
                throw new EOFException("Unexpected end of stream indicator received while retrieving status.");
            }

            byteValue = (byte)result;

            if (debug)
            {
                System.out.println("byte read: 0x" + hex.format(byteValue));
                System.out.println("Second chance bytes available: " + in.available());
            }

            if ((byteValue == CM11A.CM11_RECEIVE_EVENT) ||
                (byteValue == CM11A.CM11_POWER_FAILURE) ||
                (byteValue == CM11A.CM11_MACRO_INITIATED))
            {
                throw new InterruptedTransmissionException(byteValue);
            }

            else if (in.available() >= (STATUS_SIZE - 1))
            {
                if (debug)
                {
                    System.out.println("Now, enough bytes are available.");
                }

                // may be slow to get the bytes uploaded, give it another chance...

                buffer[0] = byteValue;
                numBytesRead = in.read(buffer, 1, buffer.length - 1) + 1;

                // received the status buffer, continue on...
            }
            else
            {
                System.err.println("Breakdown in protocol, consuming all bytes in CM11AStatusTransmission.");

                // consume all bytes in input stream...

                while (in.available() > 0)
                {
                    in.read(buffer);
                }

                // retransmit...

                transmit(in, out);
                return;
            }
        }
        else
        {
            if (debug)
            {
                System.out.println("Reading...");
            }
            numBytesRead = in.read(buffer);
        }

        if (debug)
        {
            System.out.println("Checking number of bytes read...");
        }
        if (numBytesRead != STATUS_SIZE)
        {
            System.err.println("Invalid status buffer size.  Received " + numBytesRead + " bytes out of " + STATUS_SIZE + " bytes.");

            // consume all bytes in input stream...

            while (in.available() > 0)
            {
                in.read(buffer);
            }

            // retransmit...

            transmit(in, out);
            return;
        }

        if (debug)
        {
            System.out.println("Calling method to decode status...");
        }
        event = cm11a.decodeStatus(buffer, 0, buffer.length);

        if ((listener != null) && (event != null))
        {
            // notify listener
            listener.status(event);
        }
        // transmission complete
    }


    /**
     * Retrieve the number of transmission attempts.
     *
     * @return the number of transmission attempts
     */
    public int getNumAttempts()
    {
        return(attempts);
    }

    /**
     * Set the number of transmission attempts
     *
     * @param maxAttempts the maximum number of transmission attempts
     */
    public void setMaxAttempts(int maxAttempts)
    {
        this.maxAttempts = maxAttempts;
    }

    /**
     * Create a string representation of the transmission.
     *
     * @return String representation of the transmission.
     */
    @Override public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("CM11AStatusTransmission");
        return(buffer.toString());
    }
}
