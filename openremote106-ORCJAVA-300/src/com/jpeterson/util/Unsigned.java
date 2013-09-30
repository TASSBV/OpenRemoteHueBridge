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

package com.jpeterson.util;

/**
 * This class allows a number to be converted to it's unsigned value. This
 * works for bytes, shorts, and ints, as the value is returned as a long
 *
 * @author Jesse Peterson <jesse@jpeterson.com>
 *
 * @version 1.0
 */
public class Unsigned extends Object
{
    /**
     * Return the unsigned value of the number.
     *
     * @param number a byte value
     * @return the unsigned value
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public static long unsigned(byte number)
    {
	long result = 0;
	BinaryFormat format = new BinaryFormat();
	String binary = format.format(number);

	StringBuffer buffer = new StringBuffer(binary);
	buffer.reverse();
	int length = buffer.length();
	for (int i = 0; i < length; i++) {
	    result += (buffer.charAt(i) == '1') ? 1 << i : 0;
	}

	return(result);
    }

    /**
     * Return the unsigned value of the number.
     *
     * @param number a short value
     * @return the unsigned value
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public static long unsigned(short number)
    {
	long result = 0;
	BinaryFormat format = new BinaryFormat();
	String binary = format.format(number);

	StringBuffer buffer = new StringBuffer(binary);
	buffer.reverse();
        int length = buffer.length();
	for (int i = 0; i < length; i++) {
	    result += (buffer.charAt(i) == '1') ? 1 << i : 0;
	}

	return(result);
    }

    /**
     * Return the unsigned value of the number.
     *
     * @param number an int value
     * @return the unsigned value
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public static long unsigned(int number)
    {
	long result = 0;
	BinaryFormat format = new BinaryFormat();
	String binary = format.format(number);

	StringBuffer buffer = new StringBuffer(binary);
	buffer.reverse();
	int length = buffer.length();
	for (int i = 0; i < length; i++) {
	    result += (buffer.charAt(i) == '1') ? 1 << i : 0;
	}

	return(result);
    }

    ///////////////
    // self test //
    ///////////////
    public static void main(String[] args)
    {
	long expected, result;

	byte aByte = (byte)0xff;
	expected = 255;
	result = Unsigned.unsigned(aByte);
	if (result == expected) {
	    System.out.print("Success => ");
	} else {
	    System.out.print("FAILURE => ");
	}
	System.out.println("expected: " + expected + " | result: " + result);

	aByte = (byte)0x80;
	expected = 128;
	result = Unsigned.unsigned(aByte);
	if (result == expected) {
	    System.out.print("Success => ");
	} else {
	    System.out.print("FAILURE => ");
	}
	System.out.println("expected: " + expected + " | result: " + result);

	short aShort = (short)0xffff;
	expected = 65535;
	result = Unsigned.unsigned(aShort);
	if (result == expected) {
	    System.out.print("Success => ");
	} else {
	    System.out.print("FAILURE => ");
	}
	System.out.println("expected: " + expected + " | result: " + result);
    }
}

