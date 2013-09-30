package com.jpeterson.x10.beans;

import com.jpeterson.x10.event.X10Event;

/**
 * This interface is provides template for device that can 
 * monitors X10 events.
 * 
 * This was the missing element from the source that was obtained from
 * http://www.jpeterson.com/x10/x10.zip
 * 
 * @author Manish Pandya <manish at meetamanish dot com>
 *
 */
public interface X10TransmissionListener {
    public void x10Transmission(X10Event evt);
}
