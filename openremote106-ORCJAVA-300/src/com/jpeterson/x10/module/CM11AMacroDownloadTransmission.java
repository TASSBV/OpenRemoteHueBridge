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

import com.jpeterson.util.HexFormat;

/**
 * Create a standard CM11 transmission event to transmit. Standard
 * transmission events are transmitted to the CM11 by a protocol
 * that provides safeguards to ensure that the message is sent
 * to the CM11 device correctly.  The safeguards implemeted are
 * a checksum algorythm.
 *
 * @author Jesse Peterson <jesse@jpeterson.com>
 */
public class CM11AMacroDownloadTransmission extends CM11AStandardTransmission
{
    /**
     * Create a macro download CM11 transmission event to transmit the
     * specified packet of bytes.
     *
     * @param packet The packet of bytes to transmit to the CM11 interface
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public CM11AMacroDownloadTransmission(byte[] packet)
    {
        super(packet);
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

        // the checksum on a macro download skips the first byte
        for (int i = 1; i < packet.length; i++)
        {
            sum += packet[i];
        }
        return((byte)sum);
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

        buffer.append("CM11AMacroDownloadTransmission - packet: [");
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




