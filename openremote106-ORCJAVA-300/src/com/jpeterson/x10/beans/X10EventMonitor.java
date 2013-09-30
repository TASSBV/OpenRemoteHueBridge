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

import javax.swing.*;
import javax.swing.text.*;
import com.jpeterson.x10.event.X10Event;

/**
 * Monitors X10 events.
 *
 * @version 1.0
 * @author Jesse Peterson <jesse@jpeterson.com>
 */
public class X10EventMonitor extends JScrollPane
    implements X10TransmissionListener
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    JTextArea textArea;

    /**
     * Create a new X10 event monitor.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public X10EventMonitor()
    {
	super();
	textArea = new JTextArea("X10EventMonitor\n", 4, 32);
	getViewport().add(textArea);

	setVisible(true);
    }

    /**
     * Log the X10 event transmission.
     *
     * @param evt X10 event.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void x10Transmission(X10Event evt)
    {
	textArea.append(evt.toString());
	textArea.append("\n");
    }

    /**
     * Clear the display.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void clear()
    {
	Document doc = textArea.getDocument();
	try {
	    doc.remove(0, doc.getLength());
	} catch (BadLocationException e) {
	    // do nothing
	}
    }
}
