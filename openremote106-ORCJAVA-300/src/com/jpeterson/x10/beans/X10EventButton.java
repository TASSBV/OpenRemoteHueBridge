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

package com.jpeterson.x10.beans;

import java.awt.event.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;
import javax.swing.*;
import com.jpeterson.x10.event.*;

public class X10EventButton extends JPanel implements ActionListener
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JButton transmit;
    private transient Vector<X10TransmissionListener> x10TransmissionListeners = null;
    private char houseCode = 'A';
    private int deviceCode = 1;
    private boolean on;

    public X10EventButton()
    {
	transmit = new JButton("Off");
	transmit.setActionCommand("transmit");
	transmit.addActionListener(this);
	add(transmit);
	on = false;
    }

    public void actionPerformed(ActionEvent e)
    {
	if (e.getActionCommand().equals("transmit")) {
	    fireX10Event();
	}
    }

    /**
     * Fire an X10 Transmission.
     *
     * @param l X10 Transmission listener to add.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    @SuppressWarnings("unchecked")
    public void fireX10Event()
    {
	X10Event address;
	X10Event function;

	// address
	address = new AddressEvent(this, houseCode, deviceCode);

	// on or off event, as appropriate
	if (on) {
	    transmit.setText("Off");
	    function = new OnEvent(this, houseCode);
	} else {
	    function = new OffEvent(this, houseCode);
	    transmit.setText("On");
	}

	// toggle on status
	on = (on ? false : true);

	// make a copy of the listener object vector so that it cannot
	// be changed while we are firing events
	Vector<X10TransmissionListener> v = null;
	synchronized(this) {
	    if (x10TransmissionListeners != null) {
		v = (Vector<X10TransmissionListener>) x10TransmissionListeners.clone();
	    }
	}

	if (v != null) {
	    // fire the event to all listeners
	    int cnt = v.size();
	    for (int i = 0; i < cnt; i++) {
		X10TransmissionListener client = (X10TransmissionListener)v.elementAt(i);
		client.x10Transmission(address);
		client.x10Transmission(function);
	    }
	}
    }

    /**
     * Add an X10 Transmission listener.
     *
     * @param l X10 Transmission listener to add.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public synchronized void addX10TransmissionListener(X10TransmissionListener l)
    {
	if (x10TransmissionListeners == null) {
	    x10TransmissionListeners = new Vector<X10TransmissionListener>();
	}
	
	// add a listener if it is not already registered
	if (!x10TransmissionListeners.contains(l)) {
	    x10TransmissionListeners.addElement(l);
	}
    }

    /**
     * Remove an X10 Transmission listener.
     *
     * @param l X10 Transmission listener to remove.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public synchronized void removeX10TransmissionListener(X10TransmissionListener l)
    {
	// remove it if it is registered
	if (x10TransmissionListeners != null) {
	    x10TransmissionListeners.removeElement(l);
	}
    }

    public void setHouseCode(char houseCode)
    {
	this.houseCode = houseCode;
    }

    public char getHouseCode()
    {
	return(houseCode);
    }

    public void setDeviceCode(int deviceCode)
    {
	if ((deviceCode > 16) || (deviceCode < 1)) {
	    throw new IllegalArgumentException("Invalid device code");
	}

	this.deviceCode = deviceCode;
    }

    public int getDeviceCode()
    {
	return(deviceCode);
    }

    public boolean isOn()
    {
	return(on);
    }

    /**
     * Serialize the object.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    @SuppressWarnings("unchecked")
    private void writeObject(ObjectOutputStream stream)
	throws IOException
    {
	// perform default writing first
	stream.defaultWriteObject();

	// clone the vector in case one is added or removed
	Vector<X10TransmissionListener> v = null;
	synchronized(this) {
	    if (x10TransmissionListeners != null) {
		v = (Vector<X10TransmissionListener>)x10TransmissionListeners.clone();
	    }
	}

	// if we have a collection
	if (v != null) {
	    int cnt = v.size();
	    for (int i = 0; i < cnt; i++) {
		// get the listener element from the collection
		X10TransmissionListener l =
		    (X10TransmissionListener)v.elementAt(i);

		// if the listener is serializable, write it to the stream
		if (l instanceof Serializable) {
		    stream.writeObject(l);
		}
	    }
	}

	// a null object marks the end of the listeners
	stream.writeObject(null);
    }

    /**
     * Deserialize the object.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    private void readObject(ObjectInputStream stream)
	throws IOException, ClassNotFoundException
    {
	stream.defaultReadObject();

	Object l;
	while (null != (l = stream.readObject())) {
	    addX10TransmissionListener((X10TransmissionListener)l);
	}
    }
}
