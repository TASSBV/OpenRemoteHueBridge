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

import java.text.*;

/**
 * This class allows a number to be easily formatted as
 * a binary number. The representation uses 1's and 0's.
 *
 * @author  Jesse Peterson <jesse@jpeterson.com>
 *
 * @version 1.0
 */
public class BinaryFormat extends Format
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * spacer between a digit
     */
    private String divider;

    /**
     * Create a new BinaryFormat object with no divider.
     *
     * @author  Jesse Peterson <jesse@jpeterson.com>
     * @since 1.0
     */    
    public BinaryFormat()
    {
        divider = "";
    }

    /**
     * Format an object in a binary representation. The object
     * <CODE>number</CODE> must be an integer Number; Byte,
     * Short, Integer, or Long. If the parameter
     * <CODE>number</CODE> is not one of these, this method
     * will throw a <CODE>IllegalArgumentException</CODE>.
     *
     * @param number the number to format
     * @param toAppendTo where the text is to be appended
     * @param pos not used
     * @return the formatted binary number
     *
     * @author  Jesse Peterson <jesse@jpeterson.com>
     * @since 1.0
     */
    public StringBuffer format(Object number,
			       StringBuffer toAppendTo,
			       FieldPosition pos)
    {
        if (number instanceof Byte) {
            format(((Number)number).byteValue(), toAppendTo, pos);
        } else if (number instanceof Short) {
            format(((Number)number).shortValue(), toAppendTo, pos);
        } else if (number instanceof Integer) {
            format(((Number)number).intValue(), toAppendTo, pos);
        } else if (number instanceof Long) {
            format(((Number)number).longValue(), toAppendTo, pos);
        } else {
            throw new IllegalArgumentException("Cannot format given Object as a Byte, Short, Integer, or Long");
        }

        return(toAppendTo);
    }

    /**
     * Format a byte, returning an 8 bit binary number.
     *
     * @param number the byte to format
     * @return the formatted binary number
     *
     * @author Jesse Peterson  <jesse@jpeterson.com>
     * @since 1.0
     */
    public final String format(byte number)
    {
        return(format(number, new StringBuffer(),
                      new FieldPosition(0)).toString());
    }

    /**
     * Format a byte, returning an 8 bit binary number.
     *
     * @param number the number to format
     * @param toAppendTo where the text is to be appended
     * @param pos not used
     * @return the formatted binary number
     *
     * @author Jesse Peterson  <jesse@jpeterson.com>
     * @since 1.0
     */
    public StringBuffer format(byte number,
                   StringBuffer toAppendTo,
                   FieldPosition pos)
    {
        String prefix = "";

        prefix = "";

	for (int i = 8; i-- > 0; ) {
	    toAppendTo.append(prefix);
	    toAppendTo.append(((number & (1 << i)) != 0) ? 1 : 0);
	    prefix = divider;
	}

        return(toAppendTo);
    }

    /**
     * Format an array of bytes, returning 8 bits per byte.
     * The byte at index zero is the most significant byte,
     * making it possible to enter a stream of bytes received
     * from a serial connection very easily.
     *
     * @param number the bytes to format
     * @return the formatted binary number
     *
     * @author Jesse Peterson  <jesse@jpeterson.com>
     * @since 1.0
     */
    public final String format(byte[] number)
    {
        return(format(number, new StringBuffer(),
                      new FieldPosition(0)).toString());
    }

    /**
     * Format an array of bytes, returning 8 bits per bytes.
     * The byte at index zero is the most significant byte,
     * making it possible to enter a stream of bytes received
     * from a serial connection very easily.
     *
     * @param number the number to format
     * @param toAppendTo where the text is to be appended
     * @param pos not used
     * @return the formatted binary number
     *
     * @author Jesse Peterson  <jesse@jpeterson.com>
     * @since 1.0
     */
    public StringBuffer format(byte[] number,
                   StringBuffer toAppendTo,
                   FieldPosition pos)
    {
        String prefix = "";

        prefix = "";

        for (int i = 0; i < number.length; i++) {
	    toAppendTo.append(prefix);
	    format(number[i], toAppendTo, pos);
	    prefix = divider;
        }

        return(toAppendTo);
    }

    /**
     * Format a short value, returning a 16 bit binary number.
     *
     * @param number the short to format
     * @return the formatted binary number
     *
     * @author Jesse Peterson  <jesse@jpeterson.com>
     * @since 1.0
     */
    public String format(short number)
    {
        return(format(number, new StringBuffer(),
                      new FieldPosition(0)).toString());
    }

    /**
     * Format a short value, returning a 16 bit binary number.
     *
     * @param number the number to format
     * @param toAppendTo where the text is to be appended
     * @param pos not used
     * @return the formatted binary number
     *
     * @author Jesse Peterson  <jesse@jpeterson.com>
     * @since 1.0
     */
    public StringBuffer format(short number,
                   StringBuffer toAppendTo,
                   FieldPosition pos)
    {
        byte[] array = new byte[2];

        array[0] = (byte)((number >>> 8) & 0xff);
        array[1] = (byte)(number & 0xff);

        return(format(array, toAppendTo, pos));
    }

    /**
     * Format an int value, returning a 32 bit binary number.
     *
     * @param number the int to format
     * @return the formatted binary number
     *
     * @author Jesse Peterson  <jesse@jpeterson.com>
     * @since 1.0
     */
    public String format(int number)
    {
        return(format(number, new StringBuffer(),
                      new FieldPosition(0)).toString());
    }

    /**
     * Format an int value, returning a 32 bit binary number.
     *
     * @param number the number to format
     * @param toAppendTo where the text is to be appended
     * @param pos not used
     * @return the formatted binary number
     *
     * @author Jesse Peterson  <jesse@jpeterson.com>
     * @since 1.0
     */
    public StringBuffer format(int number,
                   StringBuffer toAppendTo,
                   FieldPosition pos)
    {
        byte[] array = new byte[4];

        array[0] = (byte)((number >>> 24) & 0xff);
        array[1] = (byte)((number >>> 16) & 0xff);
        array[2] = (byte)((number >>> 8) & 0xff);
        array[3] = (byte)(number & 0xff);

        return(format(array, toAppendTo, pos));
    }

    /**
     * Format a long value, returning a 64 bit binary number.
     *
     * @param number the long to format
     * @return the formatted binary number
     *
     * @author Jesse Peterson  <jesse@jpeterson.com>
     * @since 1.0
     */
    public String format(long number)
    {
        return(format(number, new StringBuffer(),
                      new FieldPosition(0)).toString());
    }

    /**
     * Format a long value, returning a 64 bit binary number.
     *
     * @param number the number to format
     * @param toAppendTo where the text is to be appended
     * @param pos not used
     * @return the formatted binary number
     *
     * @author Jesse Peterson  <jesse@jpeterson.com>
     * @since 1.0
     */
    public StringBuffer format(long number,
                   StringBuffer toAppendTo,
                   FieldPosition pos)
    {
        byte[] array = new byte[8];

        array[0] = (byte)((number >>> 56) & 0xff);
        array[1] = (byte)((number >>> 48) & 0xff);
        array[2] = (byte)((number >>> 40) & 0xff);
        array[3] = (byte)((number >>> 32) & 0xff);
        array[4] = (byte)((number >>> 24) & 0xff);
        array[5] = (byte)((number >>> 16) & 0xff);
        array[6] = (byte)((number >>> 8) & 0xff);
        array[7] = (byte)(number & 0xff);

        return(format(array, toAppendTo, pos));
    }

    /**
     * Parse a binary number into a Number object.
     * If up to 8 bits are parsed, returns a Byte. If more than 8 and up to
     * 16 bits are parsed, return a Short. If more than 16 and up to 32 bits
     * are parsed, return an Integer. If more than 32 and up to 64 bits are
     * parsed, return a Long.
     *
     * @param source a binary number
     * @return return an integer form of Number object if parse is
     *         successful
     * @exception ParseException thrown if source is cannot be converted to
     *            a Byte, Short, Int, or Long.
     *
     * @author Jesse Peterson  <jesse@jpeterson.com>
     * @since 1.0
     */
    public Number parse(String source) throws ParseException
    {
	int startIndex = 0;
	Number result;

	ParsePosition parsePosition = new ParsePosition(startIndex);
	result = parse(source, parsePosition);

	if (result == null) {
	    throw new ParseException("Unable to parse " + source + " using BinaryFormat", parsePosition.getIndex());
	}

	return(result);
    }

    /**
     * Parse a binary number into a Number object.
     * If up to 8 bits are parsed, returns a Byte. If more than 8 and up to
     * 16 bits are parsed, return a Short. If more than 16 and up to 32 bits
     * are parsed, return an Integer. If more than 32 and up to 64 bits are
     * parsed, return a Long.
     *
     * @param text a binary number
     * @param parsePosition position to start parsing from
     * @return return an integer form of Number object if parse is
     *         successful; <CODE>null</CODE> otherwise
     *
     * @author Jesse Peterson  <jesse@jpeterson.com>
     * @since 1.0
     */
    public Number parse(String text, ParsePosition parsePosition)
    {
	boolean skipWhitespace = true;
	int startIndex, bits;

	// remove whitespace
	StringCharacterIterator iter =
	    new StringCharacterIterator(text,
					parsePosition.getIndex());
	for (char c = iter.current(); c != CharacterIterator.DONE;
	     c = iter.next()) {
	    if (skipWhitespace && Character.isWhitespace(c)) {
		// skip whitespace
		continue;
	    }
	}
	parsePosition.setIndex(iter.getIndex());

	startIndex = parsePosition.getIndex();
	Number result = (Number)parseObject(text, parsePosition);

	if (result == null) {
	    return(result);
	}

	bits = parsePosition.getIndex() - startIndex;
	if (bits <= 8) {
	    result = new Byte(result.byteValue());
	} else if (bits <= 16) {
	    result = new Short(result.shortValue());
	} else if (bits <= 32) {
	    result = new Integer(result.intValue());
	} else if (bits <= 64) {
	    result = new Long(result.longValue());
	}
	return(result);
    }

    /**
     * Parse a binary number, skipping leading whitespace. Does not throw
     * an exception; if no object can be parsed, index is unchanged!
     *
     * @param source the string to parse
     * @param status the string index to start at
     * @return The binary number as a Long object.
     *
     * @author Jesse Peterson  <jesse@jpeterson.com>
     * @since 1.0
     */
    public Object parseObject(String source, ParsePosition status)
    {
	int start = status.getIndex();
	boolean success = false;
	boolean skipWhitespace = true;
	StringBuffer buffer = new StringBuffer();

	StringCharacterIterator iter = new StringCharacterIterator(source,
								   start);
	
	for (char c = iter.current(); c != CharacterIterator.DONE; c = iter.next()) {
	    if (skipWhitespace && Character.isWhitespace(c)) {
		// skip whitespace
		continue;
	    }
	    skipWhitespace = false;

	    if ((c == '1') || (c == '0')) {
		success = true;
		buffer.append(c);
	    } else {
		break;
	    }
	}

	if (!success) {
	    return(null);
	}
	
	// convert binary to long
	if (buffer.length() > 64) {
	    // larger than a long, error
	    return(null);
	}

	long result = 0;
	buffer.reverse();
	int length = buffer.length();
	for (int i = 0; i < length; i++) {
	    result += (buffer.charAt(i) == '1') ? 1 << i : 0;
	}
	status.setIndex(iter.getIndex());
	return(new Long(result));
    }

    /**
     * Set the string used to seperate bits. Is useful some times
     * to insert a space between bits for readability.
     *
     * @param divider String to insert between bits
     *
     * @see getDivider
     * @author Jesse Peterson  <jesse@jpeterson.com>
     * @since 1.0
     */
    public void setDivider(String divider)
    {
        this.divider = divider;
    }

    /**
     * Get the string used to seperate bits.
     *
     * @return the string used to seperate bits
     *
     * @see setDivider
     * @author Jesse Peterson  <jesse@jpeterson.com>
     * @since 1.0
     */
    public String getDivider()
    {
	return(divider);
    }

    ///////////////
    // self test //
    ///////////////
    public static void main(String[] args)
    {
        String result;
        BinaryFormat format = new BinaryFormat();
        format.setDivider(" ");

        // byte
        byte bNumber = 0x33;
        result = format.format(bNumber);
        if (result.equals("0 0 1 1 0 0 1 1")) {
            System.out.print("Success => ");
        } else {
            System.out.print("FAILURE => ");
        }
        System.out.println("Byte: " + bNumber + " (" + result + ")");
    
        // byte
        bNumber = (byte)0x85;
        result = format.format(bNumber);
        if (result.equals("1 0 0 0 0 1 0 1")) {
            System.out.print("Success => ");
        } else {
            System.out.print("FAILURE => ");
        }
        System.out.println("Byte: " + bNumber + " (" + result + ")");
    
        // short
        short sNumber = (short)0xa2b6;
        result = format.format(sNumber);
        if (result.equals("1 0 1 0 0 0 1 0 1 0 1 1 0 1 1 0")) {
            System.out.print("Success => ");
        } else {
            System.out.print("FAILURE => ");
        }
        System.out.println("Byte: " + sNumber + " (" + result + ")");

        // int
        format.setDivider("");
        int iNumber = (int)0x4321fedc;
        result = format.format(iNumber);
        if (result.equals("01000011001000011111111011011100")) {
            System.out.print("Success => ");
        } else {
            System.out.print("FAILURE => ");
        }
        System.out.println("Byte: " + iNumber + " (" + result + ")");

        // long
        format.setDivider("");
        long lNumber = (long)0x4321fedc4321fedcL;
        result = format.format(lNumber);
        if (result.equals("0100001100100001111111101101110001000011001000011111111011011100")) {
            System.out.print("Success => ");
        } else {
            System.out.print("FAILURE => ");
        }
        System.out.println("Byte: " + lNumber + " (" + result + ")");
    }
}
