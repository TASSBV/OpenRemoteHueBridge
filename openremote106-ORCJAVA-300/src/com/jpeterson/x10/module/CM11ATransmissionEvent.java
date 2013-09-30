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
import com.jpeterson.x10.InterruptedTransmissionException;
import com.jpeterson.x10.TooManyAttemptsException;

/**
 * @author Jesse Peterson <jesse@jpeterson.com>
 */
public interface CM11ATransmissionEvent
{
    /**
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void transmit(InputStream in, OutputStream out)
        throws TooManyAttemptsException, InterruptedTransmissionException,
        EOFException, IOException;

    /**
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public int getNumAttempts();

    /**
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void setMaxAttempts(int maxAttempts);
}
