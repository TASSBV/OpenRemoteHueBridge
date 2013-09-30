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

import java.beans.*;

public class HouseCodeEditor extends PropertyEditorSupport
{
    // the current house code
    protected char houseCode;

    /**
     * Set the object being edited.
     *
     * @param o Character object representing the house code
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void setValue(Object o)
    {
	// get the primitive data value from the object version
	houseCode = ((Character)o).charValue();
    }

    /**
     * Get the current value of the property
     *
     * @return House code as a Character object
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public Object getValue()
    {
	// return the object version of the house code
	return(new Character(houseCode));
    }

    /**
     * Get the value of the property as text
     *
     * @return House code as a String representation
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public String getAsText()
    {
	return(new Character(houseCode).toString());
    }

    /**
     * Set the value of the property as text
     *
     * @param s House code as a String representation
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void setAsText(String s)
    {
	// get the from the object and
	// use it as the house code
	char[] str = s.toUpperCase().toCharArray();

	if (str.length > 0) {
	    houseCode = str[0];
	}

	// let any interested listeners know about the change
	firePropertyChange();
    }

    /**
     * Get the string tags
     *
     * @return Return a string of all of the possible house codes to
     *         choose from
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public String[] getTags()
    {
	// the only valid strings are A through P
	String[] s = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
		      "K", "L", "M", "N", "O", "P"};
	return(s);
    }
}
