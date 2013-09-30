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

package com.jpeterson.x10;

/**
 * Utility classes to support X10 classes.
 *
 * @version 1.0
 * @author Jesse Peterson <jesse@jpeterson.com>
 */
public class X10Util
{
    public static final int X10_DEVICE_CODE_1  = 1;
    public static final int X10_DEVICE_CODE_2  = 2;
    public static final int X10_DEVICE_CODE_3  = 3;
    public static final int X10_DEVICE_CODE_4  = 4;
    public static final int X10_DEVICE_CODE_5  = 5;
    public static final int X10_DEVICE_CODE_6  = 6;
    public static final int X10_DEVICE_CODE_7  = 7;
    public static final int X10_DEVICE_CODE_8  = 8;
    public static final int X10_DEVICE_CODE_9  = 9;
    public static final int X10_DEVICE_CODE_10 = 10;
    public static final int X10_DEVICE_CODE_11 = 11;
    public static final int X10_DEVICE_CODE_12 = 12;
    public static final int X10_DEVICE_CODE_13 = 13;
    public static final int X10_DEVICE_CODE_14 = 14;
    public static final int X10_DEVICE_CODE_15 = 15;
    public static final int X10_DEVICE_CODE_16 = 16;

    public static final int X10_HOUSE_CODE_A = 1;
    public static final int X10_HOUSE_CODE_B = 2;
    public static final int X10_HOUSE_CODE_C = 3;
    public static final int X10_HOUSE_CODE_D = 4;
    public static final int X10_HOUSE_CODE_E = 5;
    public static final int X10_HOUSE_CODE_F = 6;
    public static final int X10_HOUSE_CODE_G = 7;
    public static final int X10_HOUSE_CODE_H = 8;
    public static final int X10_HOUSE_CODE_I = 9;
    public static final int X10_HOUSE_CODE_J = 10;
    public static final int X10_HOUSE_CODE_K = 11;
    public static final int X10_HOUSE_CODE_L = 12;
    public static final int X10_HOUSE_CODE_M = 13;
    public static final int X10_HOUSE_CODE_N = 14;
    public static final int X10_HOUSE_CODE_O = 15;
    public static final int X10_HOUSE_CODE_P = 16;

    public static final byte X10_FUNCTION_ALL_UNITS_OFF = (byte)0;
    public static final byte X10_FUNCTION_ALL_LIGHTS_ON = (byte)1;
    public static final byte X10_FUNCTION_ON = (byte)2;
    public static final byte X10_FUNCTION_OFF = (byte)3;
    public static final byte X10_FUNCTION_DIM = (byte)4;
    public static final byte X10_FUNCTION_BRIGHT = (byte)5;
    public static final byte X10_FUNCTION_ALL_LIGHTS_OFF = (byte)6;
    public static final byte X10_FUNCTION_EXTENDED_CODE = (byte)7;
    public static final byte X10_FUNCTION_HAIL_REQUEST = (byte)8;
    public static final byte X10_FUNCTION_HAIL_ACKNOWLEDGE = (byte)9;
    public static final byte X10_FUNCTION_PRESET_DIM_1 = (byte)10;
    public static final byte X10_FUNCTION_PRESET_DIM_2 = (byte)11;
    public static final byte X10_FUNCTION_EXTENDED_DATA_TRANSFER = (byte)12;
    public static final byte X10_FUNCTION_STATUS_ON = (byte)13;
    public static final byte X10_FUNCTION_STATUS_OFF = (byte)14;
    public static final byte X10_FUNCTION_STATUS_REQUEST = (byte)15;

    private static final byte[] code2value = {-1,   // invalid
                                               6,   // Device 1
                                               14,  // Device 2
                                               2,   // Device 3
                                               10,  // Device 4
                                               1,   // Device 5
                                               9,   // Device 6
                                               5,   // Device 7
                                               13,  // Device 8
                                               7,   // Device 9
                                               15,  // Device 10
                                               3,   // Device 11
                                               11,  // Device 12
                                               0,   // Device 13
                                               8,   // Device 14
                                               4,   // Device 15
                                               12}; // Device 16

    private static final int[] value2deviceCode = {13,  // position 0
                                                   5,   // position 1
                                                   3,   // position 2
                                                   11,  // position 3
                                                   15,  // position 4
                                                   7,   // position 5
                                                   1,   // position 6
                                                   9,   // position 7
                                                   14,  // position 8
                                                   6,   // position 9
                                                   4,   // position 10
                                                   12,  // position 11
                                                   16,  // position 12
                                                   8,   // position 13
                                                   2,   // position 14
                                                   10}; // position 15

    private static final char[] value2houseCode = {'M',  // value 0
                                                   'E',  // value 1
                                                   'C',  // value 2
                                                   'K',  // value 3
                                                   'O',  // value 4
                                                   'G',  // value 5
                                                   'A',  // value 6
                                                   'I',  // value 7
                                                   'N',  // value 8
                                                   'F',  // value 9
                                                   'D',  // value 10
                                                   'L',  // value 11
                                                   'P',  // vlaue 12
                                                   'H',  // value 13
                                                   'B',  // value 14
                                                   'J'}; // value 15

    /**
     * Convert a house code to it's corresponding X10 value.
     *
     * @param houseCode character representation of the house code; 'A' - 'P'
     * @return Value of house code
     * @exception IllegalArgumentException Thrown if the house code
     *            not equal to 'A'-'P'.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public static byte houseCode2byte(char houseCode)
        throws IllegalArgumentException
    {
        switch (houseCode)
        {
        case 'A':
        case 'a':
            return(code2value[X10_HOUSE_CODE_A]);

        case 'B':
        case 'b':
            return(code2value[X10_HOUSE_CODE_B]);

        case 'C':
        case 'c':
            return(code2value[X10_HOUSE_CODE_C]);

        case 'D':
        case 'd':
            return(code2value[X10_HOUSE_CODE_D]);

        case 'E':
        case 'e':
            return(code2value[X10_HOUSE_CODE_E]);

        case 'F':
        case 'f':
            return(code2value[X10_HOUSE_CODE_F]);

        case 'G':
        case 'g':
            return(code2value[X10_HOUSE_CODE_G]);

        case 'H':
        case 'h':
            return(code2value[X10_HOUSE_CODE_H]);

        case 'I':
        case 'i':
            return(code2value[X10_HOUSE_CODE_I]);

        case 'J':
        case 'j':
            return(code2value[X10_HOUSE_CODE_J]);

        case 'K':
        case 'k':
            return(code2value[X10_HOUSE_CODE_K]);

        case 'L':
        case 'l':
            return(code2value[X10_HOUSE_CODE_L]);

        case 'M':
        case 'm':
            return(code2value[X10_HOUSE_CODE_M]);

        case 'N':
        case 'n':
            return(code2value[X10_HOUSE_CODE_N]);

        case 'O':
        case 'o':
            return(code2value[X10_HOUSE_CODE_O]);

        case 'P':
        case 'p':
            return(code2value[X10_HOUSE_CODE_P]);

        default:
            throw new IllegalArgumentException("Invalid house code: " +
                                               houseCode);
        }
    }

    /**
     * Convert a value to it's corresponding house code.
     *
     * @param nibble the house code value.
     * @return Character 'A' through 'P' that corresponds to the house
     *         code value.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public static char byte2houseCode(byte nibble)
    {
        return(value2houseCode[nibble]);
    }

    /**
     * Convert a device code to it's corresponding X10 value.
     *
     * @param deviceCode numeric representation of the device code; 1 - 16
     * @return Value of device code
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public static byte deviceCode2byte(int deviceCode)
    {
        return(code2value[deviceCode]);
    }

    /**
     * Convert a value to it's corresponding house code.
     *
     * @param nibble the house code value.
     * @return Character 'A' through 'P' that corresponds to the house
     *         code value.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public static int byte2deviceCode(byte nibble)
    {
        return(value2deviceCode[nibble]);
    }
}



