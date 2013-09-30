/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.utils;

import org.junit.Test;
import org.junit.Assert;

/**
 * String utility tests {@link Strings}
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author <a href="mailto:marcus@openremote.org">Marcus Redeker</a>
 */
public class StringsTest
{

  /**
   * Test single digits from 1 to 9.
   */
  @Test public void testSingleDigits()
  {
    byte[] digits = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    for (int i = 0; i < digits.length; ++i)
    {
      String str = Strings.byteToUnsignedHexString(digits[i]);

      String expected = "0x0" + (i+1);

      Assert.assertTrue(
          "Expecting '" + expected + "', got '" + str + "'.",
          str.equals("0x0" + (i + 1))
      );
    }
  }

  /**
   * Test range from 10 to 15 (0x0A to 0x0F)
   */
  @Test public void testHex()
  {
    byte A = 10;
    byte B = 11;
    byte C = 12;
    byte D = 13;
    byte E = 14;
    byte F = 15;

    String strA = Strings.byteToUnsignedHexString(A);
    String strB = Strings.byteToUnsignedHexString(B);
    String strC = Strings.byteToUnsignedHexString(C);
    String strD = Strings.byteToUnsignedHexString(D);
    String strE = Strings.byteToUnsignedHexString(E);
    String strF = Strings.byteToUnsignedHexString(F);

    Assert.assertTrue(strA.equals("0x0A"));
    Assert.assertTrue(strB.equals("0x0B"));
    Assert.assertTrue(strC.equals("0x0C"));
    Assert.assertTrue(strD.equals("0x0D"));
    Assert.assertTrue(strE.equals("0x0E"));
    Assert.assertTrue(strF.equals("0x0F"));

  }


  /**
   * Test two digit hex values
   */
  @Test public void testDoubleDigits()
  {
    byte a10 = 0x10;
    byte a20 = 0x20;
    byte a35 = 0x35;
    byte a4A = 0x4A;
    byte a5F = 0x5F;

    String str10 = Strings.byteToUnsignedHexString(a10);
    String str20 = Strings.byteToUnsignedHexString(a20);
    String str35 = Strings.byteToUnsignedHexString(a35);
    String str4A = Strings.byteToUnsignedHexString(a4A);
    String str5F = Strings.byteToUnsignedHexString(a5F);

    Assert.assertTrue(str10.equals("0x10"));
    Assert.assertTrue(str20.equals("0x20"));
    Assert.assertTrue(str35.equals("0x35"));
    Assert.assertTrue(str4A.equals("0x4A"));
    Assert.assertTrue(str5F.equals("0x5F"));

  }


  /**
   * Test highest bit (sign handling) with negative values.
   */
  @Test public void testHighBits()
  {
    byte a_1 = -1;
    byte a_2 = -2;
    byte a_128 = -128;
    byte a_127 = -127;

    String str_1 = Strings.byteToUnsignedHexString(a_1);
    String str_2 = Strings.byteToUnsignedHexString(a_2);
    String str_128 = Strings.byteToUnsignedHexString(a_128);
    String str_127 = Strings.byteToUnsignedHexString(a_127);

    Assert.assertTrue(
        "Expecting '0xFF', got '" + str_1 + "'.",
        str_1.equals("0xFF")
    );


    Assert.assertTrue(
        "Expecting '0xFE', got '" + str_2 + "'.",
        str_2.equals("0xFE")
    );


    Assert.assertTrue(
        "Expecting '0x80', got '" + str_128 + "'.",
        str_128.equals("0x80")
    );


    Assert.assertTrue(
        "Expecting '0x81', got '" + str_127 + "'.",
        str_127.equals("0x81")
    );
  }

  /**
   * Test zero value
   */
  @Test public void testZero()
  {
    byte zero = 0;

    Assert.assertTrue(Strings.byteToUnsignedHexString(zero).equals("0x00"));
  }
  
  /**
   * 
   */
  @Test public void testPollingIntervalConvertion() 
  {
    String _empty = "";
    String _empty2 = " ";
    String _null = null;

    String _500milli = "500";
    String _2s = "2s";
    String _23s = "23s";
    String _3m = "3m";
    String _132m = "132m";
    String _2h = "2h";
    String _13h = "13h";

    Assert.assertEquals(-1, Strings.convertPollingIntervalString(_empty));
    Assert.assertEquals(-1, Strings.convertPollingIntervalString(_empty2));
    Assert.assertEquals(-1, Strings.convertPollingIntervalString(_null));

    Assert.assertEquals(500, Strings.convertPollingIntervalString(_500milli));
    Assert.assertEquals(2 * 1000, Strings.convertPollingIntervalString(_2s));
    Assert.assertEquals(23 * 1000, Strings.convertPollingIntervalString(_23s));
    Assert.assertEquals(3 * 1000 * 60, Strings.convertPollingIntervalString(_3m));
    Assert.assertEquals(132 * 1000 * 60, Strings.convertPollingIntervalString(_132m));
    Assert.assertEquals(2 * 1000 * 60 * 60, Strings.convertPollingIntervalString(_2h));
    Assert.assertEquals(13 * 1000 * 60 * 60, Strings.convertPollingIntervalString(_13h));
     
  }

}
