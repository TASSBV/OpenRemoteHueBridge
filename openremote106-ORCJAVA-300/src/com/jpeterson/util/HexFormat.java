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
 * This class allows a number to be easily formatted as a
 * hexadecimal number.  The representation uses 0-f.
 *
 * @author Jesse Peterson <jesse@jpeterson.com>
 *
 * @version 1.0
 */
public class HexFormat extends Format
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * should upper case letters be used
     */
    private boolean upperCase;

    private String hexDigits = "0123456789abcdefABCDEF";

    /**
     * Create a new HexFormat object. By default the lower case letters
     * 'a'-'f' are used.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     * @since 1.0
     */
    public HexFormat()
    {
	upperCase = false;
    }

    /**
     * Format an object in a hexadecimal representation. The object
     * <CODE>number</CODE> must be an integer Number; Byte, Short,
     * Integer, or Long. If the parameter <CODE>number</CODE> is not
     * one of these, this method will throw a
     * <CODE>IllegalArgumentException</CODE>.
     *
     * @param number the number to format
     * @param toAppendTo where the text is to be appended
     * @param pos not used
     * @return the formatted hex number
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
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
     * Format a byte, returning an 8 bit hex number. (2 digits, with
     * leading zeros)
     *
     * @param number the byte to format
     * @return the formatted hex number
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
     * Format a byte, returning an 8 bit hex number. (2 digits, with
     * leading zeros)
     *
     * @param number the number to format
     * @param toAppendTo where the text is to be appended
     * @param pos not used
     * @return the formatted binary number
     *
     * @author Jesse Peterson  <jesse@jpeterson.com>
     */
    public StringBuffer format(byte number,
                   StringBuffer toAppendTo,
                   FieldPosition pos)
    {
	int hiNibble, loNibble;
	String hiDigit, loDigit;

	hiNibble = (number >>> 4) & 0x0f;
	loNibble = number & 0x0f;
	hiDigit = Integer.toHexString(hiNibble);
	loDigit = Integer.toHexString(loNibble);
	if (upperCase) {
	    hiDigit = hiDigit.toUpperCase();
	    loDigit = loDigit.toUpperCase();
	} else {
	    hiDigit = hiDigit.toLowerCase();
	    loDigit = loDigit.toLowerCase();
	}
	toAppendTo.append(hiDigit).append(loDigit);

        return(toAppendTo);
    }

    /**
     * Format an array of bytes, returning 8 bits per byte.
     * (2 digits with leading zeros, per byte)
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
     * (2 digits with leading zeros, per byte)
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
	for (int i = 0; i < number.length; i++) {
	    format(number[i], toAppendTo, pos);
	}

	return(toAppendTo);
    }

    /**
     * Format a short value, returning a 16 bit hexadecimal number.
     * (4 digits with leading zeros)
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
     * Format a short value, returning a 16 bit hexadecimal number.
     * (4 digits with leading zeros)
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
     * Format an int value, returning a 32 bit hexadecimal number.
     * (8 digits with leading zeros)
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
     * Format an int value, returning a 32 bit hexadecimal number.
     * (8 digits with leading zeros)
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
     * Format a long value, returning a 64 bit hexadecimal number.
     * (16 digits with leading zeros)
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
     * Format a long value, returning a 64 bit hexadecimal number.
     * (16 digits with leading zeros)
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
     * Parse a hex number into a Number object. Hexadecimal numbers may
     * be indicated with a leading character designation of '0x'.
     * If up to 1 byte is parsed, returns a Byte. If more than 1 and up to
     * 2 bytes are parsed, return a Short. If more than 2 and up to 4 bytes
     * are parsed, return an Integer. If more than 4 and up to 8 bytes are
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
	    throw new ParseException("Unable to parse " + source + " using HexFormat", parsePosition.getIndex());
	}

	return(result);
    }

    /**
     * Parse a hex number into a Number object. Hexadecimal numbers may
     * be indicated with a leading character designation of '0x'.
     * If up to 1 byte is parsed, returns a Byte. If more than 1 and up to
     * 2 bytes are parsed, return a Short. If more than 2 and up to 4 bytes
     * are parsed, return an Integer. If more than 4 and up to 8 bytes are
     * parsed, return a Long.
     *
     * @param text a hexadecimal number
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
	int startIndex, nibbles;

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
	    break;
	}

	// skip a leading hex designation of the characters '0x'
	if (text.regionMatches(iter.getIndex(), "0x", 0, 2)) {
	    parsePosition.setIndex(iter.getIndex() + 2);
	} else {
	    parsePosition.setIndex(iter.getIndex());
	}

	startIndex = parsePosition.getIndex();
	Number result = (Number)parseObject(text, parsePosition);

	if (result == null) {
	    return(result);
	}

	nibbles = parsePosition.getIndex() - startIndex;
	if (nibbles <= 2) {
	    result = new Byte(result.byteValue());
	} else if (nibbles <= 4) {
	    result = new Short(result.shortValue());
	} else if (nibbles <= 8) {
	    result = new Integer(result.intValue());
	} else if (nibbles <= 16) {
	    result = new Long(result.longValue());
	}
	return(result);
    }

    /**
     * Parse a hexadecimal number, skipping leading whitespace. Does not throw
     * an exception; if no object can be parsed, index is unchanged!
     * Hexadecimal numbers may be indicated with a leading character
     * designation of '0x'.
     *
     * @param source the string to parse
     * @param status the string index to start at
     * @return The hexadecimal number as a Long object.
     *
     * @author Jesse Peterson  <jesse@jpeterson.com>
     * @since 1.0
     */
    public Object parseObject(String source, ParsePosition status)
    {
	int start = status.getIndex();
	boolean success = false;
	StringBuffer buffer = new StringBuffer();
	char c, c2;
	long result;

	StringCharacterIterator iter = new StringCharacterIterator(source,
								   start);
	
	for (c = iter.current(); c != CharacterIterator.DONE; c = iter.next()) {
	    if (Character.isWhitespace(c)) {
		// skip whitespace
		continue;
	    }
	    break;
	}
	
	if (c == CharacterIterator.DONE) {
	    return(null);
	}

	if (c == '0') {
	    c2 = iter.next();

	    if (c2 == CharacterIterator.DONE) {
		return(null);
	    }
	    
	    if (c2 == 'x') {
		// has a leading '0x' designation, so skip over it
	    } else {
		// replace the two characters
		iter.previous();
		iter.previous();
	    }
	} else {
	    // skip back one character
	    iter.previous();
	}

	// gather valid hex digits
	for (c = iter.next(); c != CharacterIterator.DONE; c = iter.next()) {
	    if (hexDigits.indexOf(c) != -1) {
		success = true;
		buffer.append(c);
	    } else {
		break;
	    }
	}

	if (!success) {
	    // no valid hex digits
	    return(null);
	}
	
	// convert hex to long
	if (buffer.length() > 16) {
	    // larger than a long, error
	    // with a buffer full of nibbles, the maximum nibbles in a
	    // 64 bit number is 16 nibbles
	    return(null);
	}

	// parse number
	try {
	    result = Long.parseLong(buffer.toString(), 16);
	} catch (NumberFormatException e) {
	    // unable to parse number
	    return(null);
	}

	status.setIndex(iter.getIndex());
	return(new Long(result));
    }

    /**
     * Set upper case mode for alpha characters.
     *
     * @param upperCase true if upper case alpha characters should be used;
     *        false otherwise
     *
     * @see getUpperCase
     * @see isUpperCase
     * @see isLowerCase
     * @author Jesse Peterson <jesse@jpeterson.com>
     * @since 1.0
     */
    public void setUpperCase(boolean upperCase)
    {
	this.upperCase = upperCase;
    }

    /**
     * Get upper case mode for alpha characters.
     *
     * @return true if upper case alpha characters should be used;
     *         false otherwise
     *
     * @see setUpperCase
     * @see isUpperCase
     * @see isLowerCase
     * @author Jesse Peterson <jesse@jpeterson.com>
     * @since 1.0
     */
    public boolean getUpperCase()
    {
	return(upperCase);
    }

    /**
     * Is upper case mode for alpha characters in affect?
     *
     * @return true if upper case alpha characters should be used;
     *         false otherwise
     *
     * @see setUpperCase
     * @see getUpperCase
     * @see isLowerCase
     * @author Jesse Peterson <jesse@jpeterson.com>
     * @since 1.0
     */
    public boolean isUpperCase()
    {
	return(getUpperCase());
    }

    /**
     * Is lower case mode for alpha characters in affect?
     *
     * @return true if lower case alpha characters should be used;
     *         false otherwise
     *
     * @see setUpperCase
     * @see getUpperCase
     * @see isUpperCase
     * @author Jesse Peterson <jesse@jpeterson.com>
     * @since 1.0
     */
    public boolean isLowerCase()
    {
	return(!getUpperCase());
    }

    ///////////////
    // self test //
    ///////////////
    public static void main(String[] args)
    {
        String result;
        HexFormat format = new HexFormat();

	// byte
	byte bNumber = 0x33;
	result = format.format(bNumber);
	if (result.equals("33")) {
	    System.out.print("Success => ");
	} else {
	    System.out.print("FAILURE => ");
	}
	System.out.println("Byte: " + bNumber + " (0x" + result + ")");

	bNumber = (byte)0x85;
	result = format.format(bNumber);
	if (result.equals("85")) {
	    System.out.print("Success => ");
	} else {
	    System.out.print("FAILURE => ");
	}
	System.out.println("Byte: " + bNumber + " (0x" + result + ")");

	bNumber = (byte)0x0f;
	result = format.format(bNumber);
	if (result.equals("0f")) {
	    System.out.print("Success => ");
	} else {
	    System.out.print("FAILURE => ");
	}
	System.out.println("Byte: " + bNumber + " (0x" + result + ")");

	short sNumber = (short)0xa2b6;
	result = format.format(sNumber);
	if (result.equals("a2b6")) {
	    System.out.print("Success => ");
	} else {
	    System.out.print("FAILURE => ");
	}
	System.out.println("Byte: " + bNumber + " (0x" + result + ")");

	format.setUpperCase(true);

	int iNumber = (int)0x4321fedc;
	result = format.format(iNumber);
	if (result.equals("4321FEDC")) {
	    System.out.print("Success => ");
	} else {
	    System.out.print("FAILURE => ");
	}
	System.out.println("Byte: " + bNumber + " (0x" + result + ")");

	long lNumber = (long)0x4321fedc4321fedcL;
	result = format.format(lNumber);
	if (result.equals("4321FEDC4321FEDC")) {
	    System.out.print("Success => ");
	} else {
	    System.out.print("FAILURE => ");
	}
	System.out.println("Byte: " + bNumber + " (0x" + result + ")");

	String num = "0xfe";
	Number number = null;
	Byte bExpect = new Byte((byte)0xfe);
	try {
	    number = format.parse(num);
	} catch (ParseException e) {
	    System.out.println(e);
	    e.printStackTrace();
	}
	if ((number instanceof Byte) && (number.equals(bExpect))) {
	    System.out.print("Success => ");
	} else {
	    System.out.print("FAILURE => ");
	}
	System.out.println("Byte: " + bExpect + " result: " + number);

	num = "0xf";
	number = null;
	bExpect = new Byte((byte)0xf);
	try {
	    number = format.parse(num);
	} catch (ParseException e) {
	    System.out.println(e);
	    e.printStackTrace();
	}
	if ((number instanceof Byte) && (number.equals(bExpect))) {
	    System.out.print("Success => ");
	} else {
	    System.out.print("FAILURE => ");
	}
	System.out.println("Byte: " + bExpect + " result: " + number);

	num = "0xf0f0";
	number = null;
	Short sExpect = new Short((short)0xf0f0);
	try {
	    number = format.parse(num);
	} catch (ParseException e) {
	    System.out.println(e);
	    e.printStackTrace();
	}
	if ((number instanceof Short) && (number.equals(sExpect))) {
	    System.out.print("Success => ");
	} else {
	    System.out.print("FAILURE => ");
	}
	System.out.println("Short: " + sExpect + " result: " + number);

	num = "0xdEAdbEEf";
	number = null;
	Integer iExpect = new Integer((int)0xdEAdbEEf);
	try {
	    number = format.parse(num);
	} catch (ParseException e) {
	    System.out.println(e);
	    e.printStackTrace();
	}
	if ((number instanceof Integer) && (number.equals(iExpect))) {
	    System.out.print("Success => ");
	} else {
	    System.out.print("FAILURE => ");
	}
	System.out.println("Integer: " + iExpect + " result: " + number);
    }
}






