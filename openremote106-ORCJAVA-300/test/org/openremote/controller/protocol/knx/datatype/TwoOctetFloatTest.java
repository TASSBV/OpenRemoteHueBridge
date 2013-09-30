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
package org.openremote.controller.protocol.knx.datatype;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link org.openremote.controller.protocol.knx.datatype.TwoOctetFloat} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class TwoOctetFloatTest
{

  @Test public void testBasicValuesNoExponent()
  {
    // value 1.0 translates to 100 in mantissa (0.01 precision), hibyte should be zeroes

    TwoOctetFloat knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 1);
    byte[] data = knxFloat.getData();

    Assert.assertTrue("Expected 100, got " + data[1], data[1] == 100);
    Assert.assertTrue("Expected 0, got " + data[0], data[0] == 0);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    int value = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected 1, got " + value, value == 1);


    // value 2.0 translates to 200 (0xC8) in mantissa (0.01 precision), hibyte should be zeroes
    // note that Java's signed byte means the most significant bit when set is translated as
    // negative value

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 2);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 8, got " + (data[1] & 0xF), (data[1] & 0xF) == 8);
    Assert.assertTrue("Expected 12, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 12);
    Assert.assertTrue("Expected 0, got " + data[0], data[0] == 0);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected 2, got " + value, value == 2);



    // value 3.0 translates to 300 (0x12C) in mantissa (0.01 precision),
    // hibyte should be 1 (no exponent) and lobyte should be 0x2C

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 3);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 12, got " + (data[1] & 0xF), (data[1] & 0xF) == 12);
    Assert.assertTrue("Expected 2, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 2);
    Assert.assertTrue("Expected 1, got " + data[0], data[0] == 1);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected 3, got " + value, value == 3);



    // value 4.0 translates to 400 (0x190) in mantissa (0.01 precision),
    // hibyte should be 1 (no exponent) and lobyte should be 0x90
    // note that Java's signed byte means the most significant bit when set is translated as
    // negative value

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 4);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 0, got " + (data[1] & 0xF), (data[1] & 0xF) == 0);
    Assert.assertTrue("Expected 9, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 9);
    Assert.assertTrue("Expected 1, got " + data[0], data[0] == 1);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected 4, got " + value, value == 4);



    // value 5.0 translates to 500 (0x1F4) in mantissa (0.01 precision),
    // hibyte should be 1 (no exponent) and lobyte should be 0xF4
    // note that Java's signed byte means the most significant bit when set is translated as
    // negative value

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 5);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 4, got " + (data[1] & 0xF), (data[1] & 0xF) == 4);
    Assert.assertTrue("Expected 15, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 15);
    Assert.assertTrue("Expected 1, got " + data[0], data[0] == 1);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected 5, got " + value, value == 5);




    // value 10.0 translates to 1000 (0x3E8) in mantissa (0.01 precision),
    // hibyte should be 3 (no exponent) and lobyte should be 0xE8
    // note that Java's signed byte means the most significant bit when set is translated as
    // negative value

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 10);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 8, got " + (data[1] & 0xF), (data[1] & 0xF) == 8);
    Assert.assertTrue("Expected 14, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 14);
    Assert.assertTrue("Expected 3, got " + data[0], data[0] == 3);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected 10, got " + value, value == 10);




    // zero should be zero

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 0);
    data = knxFloat.getData();

    Assert.assertTrue(data[0] == 0);
    Assert.assertTrue(data[1] == 0);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected 0, got " + value, value == 0);



    // value 20.47 translates to 2047 (0x7FF) in mantissa (0.01 precision),
    // hibyte should be 7 (no exponent) and lobyte should be 0xFF
    // note that Java's signed byte means the most significant bit when set is translated as
    // negative value

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 20.47f);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 15, got " + (data[1] & 0xF), (data[1] & 0xF) == 15);
    Assert.assertTrue("Expected 15, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 15);
    Assert.assertTrue("Expected 7, got " + data[0], data[0] == 7);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().multiply(BigDecimal.TEN.pow(2)).intValue();

    Assert.assertTrue("Expected 2047, got " + value, value == 2047);


    // value 0.01 translates to 1 (0x1) in mantissa (0.01 precision),
    // hibyte should be 0 (no exponent) and lobyte should be 0x01
    // note that Java's signed byte means the most significant bit when set is translated as
    // negative value

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, .01f);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 1, got " + (data[1] & 0xF), (data[1] & 0xF) == 1);
    Assert.assertTrue("Expected 0, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 0);
    Assert.assertTrue("Expected 0, got " + data[0], data[0] == 0);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().multiply(BigDecimal.TEN.pow(2)).intValue();

    Assert.assertTrue("Expected 1, got " + value, value == 1);

  }


  @Test public void testBasicNegativeValuesNoExponent()
  {
    // value -1.0 translates to 100 in mantissa (0.01 precision), hibyte should be -128 (0x80)

    TwoOctetFloat knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -1);
    byte[] data = knxFloat.getData();

    Assert.assertTrue("Expected 100, got " + data[1], data[1] == 100);
    Assert.assertTrue("Expected -128, got " + data[0], data[0] == -128);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    int value = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected -1, got " + value, value == -1);

    // value -2.0 translates to 200 (0xC8) in mantissa (0.01 precision), hibyte should be -128 (0x80)
    // note that Java's signed byte means the most significant bit when set is translated as
    // negative value

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -2);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 8, got " + (data[1] & 0xF), (data[1] & 0xF) == 8);
    Assert.assertTrue("Expected 12, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 12);
    Assert.assertTrue("Expected -128, got " + data[0], data[0] == -128);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected -2, got " + value, value == -2);



    // value -3.0 translates to 300 (0x12C) in mantissa (0.01 precision),
    // hibyte should be -127 (0x81 == 1000 0001) and lobyte should be 0x2C

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -3);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 12, got " + (data[1] & 0xF), (data[1] & 0xF) == 12);
    Assert.assertTrue("Expected 2, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 2);
    Assert.assertTrue("Expected -127, got " + data[0], data[0] == -127);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected -3, got " + value, value == -3);



    // value -4.0 translates to 400 (0x190) in mantissa (0.01 precision),
    // hibyte should be -127 (0x81 == 1000 0001) and lobyte should be 0x90
    // note that Java's signed byte means the most significant bit when set is translated as
    // negative value

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -4);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 0, got " + (data[1] & 0xF), (data[1] & 0xF) == 0);
    Assert.assertTrue("Expected 9, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 9);
    Assert.assertTrue("Expected -127, got " + data[0], data[0] == -127);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected -4, got " + value, value == -4);



    // value -5.0 translates to 500 (0x1F4) in mantissa (0.01 precision),
    // hibyte should be -127 (0x80 == 1000 0001) and lobyte should be 0xF4
    // note that Java's signed byte means the most significant bit when set is translated as
    // negative value

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -5);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 4, got " + (data[1] & 0xF), (data[1] & 0xF) == 4);
    Assert.assertTrue("Expected 15, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 15);
    Assert.assertTrue("Expected -127, got " + data[0], data[0] == -127);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected -5, got " + value, value == -5);




    // value -10.0 translates to 1000 (0x3E8) in mantissa (0.01 precision),
    // hibyte should be -125 (0x83 == 1000 0011) and lobyte should be 0xE8
    // note that Java's signed byte means the most significant bit when set is translated as
    // negative value

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -10);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 8, got " + (data[1] & 0xF), (data[1] & 0xF) == 8);
    Assert.assertTrue("Expected 14, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 14);
    Assert.assertTrue("Expected -125, got " + data[0], data[0] == -125);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected -10, got " + value, value == -10);


    // value -0.01 translates to 1 (0x1) in mantissa (0.01 precision),
    // hibyte should be 0x8000 (negative with no exponent) and lobyte should be 0x01
    // note that Java's signed byte means the most significant bit when set is translated as
    // negative value

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -0.01f);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 1, got " + (data[1] & 0xF), (data[1] & 0xF) == 1);
    Assert.assertTrue("Expected 0, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 0);
    Assert.assertTrue("Expected 0, got " + (data[0] & 0xF), (data[0] & 0xF) == 0);
    Assert.assertTrue("Expected 8, got " + ((data[0] & 0xF0) >> 4), ((data[0] & 0xF0) >> 4) == 8);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().multiply(BigDecimal.TEN.pow(2)).intValue();

    Assert.assertTrue("Expected -1, got " + value, value == -1);



    // value -20.47 translates to 2047 (0x7FF) in mantissa (0.01 precision),
    // hibyte should be 0x87 (negative with no exponent) and lobyte should be 0xFF
    // note that Java's signed byte means the most significant bit when set is translated as
    // negative value

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -20.47f);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 15, got " + (data[1] & 0xF), (data[1] & 0xF) == 15);
    Assert.assertTrue("Expected 15, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 15);
    Assert.assertTrue("Expected 7, got " + (data[0] & 0xF), (data[0] & 0xF) == 7);
    Assert.assertTrue("Expected 8, got " + ((data[0] & 0xF0) >> 4), ((data[0] & 0xF0) >> 4) == 8);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().multiply(BigDecimal.TEN.pow(2)).intValue();

    Assert.assertTrue("Expected -2047, got " + value, value == -2047);


  }




  @Test public void testBasicValuesWithExponent()
  {
    // value 100.0 translates to 10000 which is broken down to 1250 (0x4E2) in mantissa
    // (0.04 precision) and exponent of 3, hibyte should be 0x1C == 0001 1100 == 0x4 + 0x3 << 3
    // and lobyte should be 0xE2
    //
    // note that Java's signed byte means the most significant bit when set is translated as
    // negative value
    TwoOctetFloat knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 100);
    byte[] data = knxFloat.getData();

    Assert.assertTrue("Expected 2, got " + (data[1] & 0xF), (data[1] & 0xF) == 2);
    Assert.assertTrue("Expected 14, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 14);
    Assert.assertTrue("Expected 12, got " + (data[0] & 0xF), (data[0] & 0xF) == 12);
    Assert.assertTrue("Expected 1, got " + ((data[0] & 0XF0) >> 4), ((data[0] & 0xF0) >> 4) == 1);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    BigDecimal value = knxFloat.resolve();

    Assert.assertTrue(
        "Expected 100.00 +- 0.02, got " + value,
        value.compareTo(new BigDecimal(99.98)) >= 0 &&
        value.compareTo(new BigDecimal(100.02)) <= 0);




    // value 1000.00 translates to 100000 which is broken down to 1563 (0x61B) in mantissa
    // (0.64 precision) and exponent of 6, hibyte should be 0x36  == 0011 0110 == 0x6 + 0x6 << 3
    // and lobyte should be 0x1B
    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 1000);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 11, got " + (data[1] & 0xF), (data[1] & 0xF) == 0xB);
    Assert.assertTrue("Expected 1, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 0x1);
    Assert.assertTrue("Expected 6, got " + (data[0] & 0xF), (data[0] & 0xF) == 0x6);
    Assert.assertTrue("Expected 3, got " + ((data[0] & 0XF0) >> 4), ((data[0] & 0xF0) >> 4) == 0x3);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve();

    Assert.assertTrue(
        "Expected 1000.00 +- 0.32, got " + value,
        value.compareTo(new BigDecimal(999.68)) >= 0 &&
        value.compareTo(new BigDecimal(1000.32)) <= 0
    );


    // value 2000.0 translates to 200000 which is broken down to 1563 (0x61B) in mantissa
    // (1.28 precision) and exponent of 7, hibyte should be 0x3E == 0011 1110 == 0x6 + 0x7 << 3
    // and lobyte should be 0x1B
    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 2000);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 11, got " + (data[1] & 0xF), (data[1] & 0xF) == 0xB);
    Assert.assertTrue("Expected 1, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 1);
    Assert.assertTrue("Expected 14, got " + (data[0] & 0xF), (data[0] & 0xF) == 0xE);
    Assert.assertTrue("Expected 3, got " + ((data[0] & 0XF0) >> 4), ((data[0] & 0xF0) >> 4) == 3);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve();

    Assert.assertTrue(
        "Expected 2000.00 +- 0.64, got " + value,
        value.compareTo(new BigDecimal(1999.36)) >= 0 &&
        value.compareTo(new BigDecimal(2000.64)) <= 0
    );

    // value 10000.0 translates to 1,000,000 which is broken down to 1953 (0x7A1) in mantissa
    // (5.12 precision) and exponent of 9, hibyte should be 0x4F == 0100 1111 == 0x7 + 0x9 << 3
    // and lobyte should be 0xA1
    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 10000);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 1, got " + (data[1] & 0xF), (data[1] & 0xF) == 0x1);
    Assert.assertTrue("Expected 10, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 0xA);
    Assert.assertTrue("Expected 15, got " + (data[0] & 0xF), (data[0] & 0xF) == 0xF);
    Assert.assertTrue("Expected 4, got " + ((data[0] & 0XF0) >> 4), ((data[0] & 0xF0) >> 4) == 4);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve();

    Assert.assertTrue(
        "Expected 10000.00 +- 2.56, got " + value,
        value.compareTo(new BigDecimal(9998.44)) >= 0 &&
        value.compareTo(new BigDecimal(10002.56)) <= 0
    );

    // value 20000.0 translates to 2,000,000 which is broken down to 1953 (0x7A1) in mantissa
    // (10.24 precision) and exponent of 10, hibyte should be 0x57 == 0101 0111 == 0x7 + 0xA << 3
    // and lobyte should be 0xA1
    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 20000);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 1, got " + (data[1] & 0xF), (data[1] & 0xF) == 1);
    Assert.assertTrue("Expected 10, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 0xA);
    Assert.assertTrue("Expected 7, got " + (data[0] & 0xF), (data[0] & 0xF) == 7);
    Assert.assertTrue("Expected 5, got " + ((data[0] & 0XF0) >> 4), ((data[0] & 0xF0) >> 4) == 5);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve();

    Assert.assertTrue(
        "Expected 20000.00 +- 5.12, got " + value,
        value.compareTo(new BigDecimal(19994.88)) >= 0 &&
        value.compareTo(new BigDecimal(20005.12)) <= 0
    );

  }





  @Test public void testBasicNegativeWithExponent()
  {
    // value -100.0 translates to 10000 which is broken down to 965 (0x3C5) in mantissa
    // (0.04 precision) and exponent of 2, hibyte should be 0x93 == 1001 0011 == 0x80 + 0x3 + 0x2 << 3
    // and lobyte should be 0xC5
    //
    // note that Java's signed byte means the most significant bit when set is translated as
    // negative value
    TwoOctetFloat knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -100);
    byte[] data = knxFloat.getData();

    Assert.assertTrue("Expected 5, got " + (data[1] & 0xF), (data[1] & 0xF) == 5);
    Assert.assertTrue("Expected 12, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 12);
    Assert.assertTrue("Expected 3, got " + (data[0] & 0xF), (data[0] & 0xF) == 3);
    Assert.assertTrue("Expected 9, got " + ((data[0] & 0XF0) >> 4), ((data[0] & 0xF0) >> 4) == 9);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    BigDecimal value = knxFloat.resolve();

    Assert.assertTrue(
        "Expected -100.00 +- 0.02, got " + value,
        value.compareTo(new BigDecimal(-99.98)) <= 0 &&
        value.compareTo(new BigDecimal(-100.02)) >= 0);




    // value -1000.0 translates to 100000 which is broken down to 1142 (0x476) in mantissa
    // (0.32 precision) and exponent of 5, hibyte should be 0xAC  == 1010 1100 == 0x80 + 0x4 + 0x5 << 3
    // and lobyte should be 0x76
    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -1000);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 6, got " + (data[1] & 0xF), (data[1] & 0xF) == 6);
    Assert.assertTrue("Expected 7, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 7);
    Assert.assertTrue("Expected 12, got " + (data[0] & 0xF), (data[0] & 0xF) == 0xC);
    Assert.assertTrue("Expected 10, got " + ((data[0] & 0XF0) >> 4), ((data[0] & 0xF0) >> 4) == 0xA);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve();

    Assert.assertTrue(
        "Expected -1000.00 +- 0.16, got " + value,
        value.compareTo(new BigDecimal(-999.84)) <= 0 &&
        value.compareTo(new BigDecimal(-1000.16)) >= 0
    );


    // value -2000.0 translates to 200000 which is broken down to 1110 (0x456) in mantissa
    // (0.64 precision) and exponent of 6, hibyte should be 0xB4 == 1011 0100 == 0x80 + 0x4 + 0x6 << 3
    // and lobyte should be 0x56
    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -2000);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 6, got " + (data[1] & 0xF), (data[1] & 0xF) == 6);
    Assert.assertTrue("Expected 5, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 5);
    Assert.assertTrue("Expected 4, got " + (data[0] & 0xF), (data[0] & 0xF) == 4);
    Assert.assertTrue("Expected 11, got " + ((data[0] & 0XF0) >> 4), ((data[0] & 0xF0) >> 4) == 0xB);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve();

    Assert.assertTrue(
        "Expected -2000.00 +- 0.32, got " + value,
        value.compareTo(new BigDecimal(-1999.68)) <= 0 &&
        value.compareTo(new BigDecimal(-2000.32)) >= 0
    );

    // value -10000.0 translates to 1,000,000 which is broken down to 1867 (0x74B) in mantissa
    // (2.56 precision) and exponent of 8, hibyte should be 0xC7 == 1100 0111 == 0x80 + 0x7 + 0x8 << 3
    // and lobyte should be 0x4B
    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -10000);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 11, got " + (data[1] & 0xF), (data[1] & 0xF) == 0xB);
    Assert.assertTrue("Expected 4, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 4);
    Assert.assertTrue("Expected 7, got " + (data[0] & 0xF), (data[0] & 0xF) == 7);
    Assert.assertTrue("Expected 12, got " + ((data[0] & 0XF0) >> 4), ((data[0] & 0xF0) >> 4) == 0xC);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve();

    Assert.assertTrue(
        "Expected -10000.00 +- 1.28, got " + value,
        value.compareTo(new BigDecimal(-9998.72)) <= 0 &&
        value.compareTo(new BigDecimal(-10001.28)) >= 0
    );

    // value -20000.0 translates to 2,000,000 which is broken down to 1863 (0x747) in mantissa
    // (5.12 precision) and exponent of 9, hibyte should be 0xCF == 1100 1111 == 0x80 + 0x7 + 0x9 << 3
    // and lobyte should be 0x47
    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -20000);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 7, got " + (data[1] & 0xF), (data[1] & 0xF) == 7);
    Assert.assertTrue("Expected 4, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 4);
    Assert.assertTrue("Expected 15, got " + (data[0] & 0xF), (data[0] & 0xF) == 0xF);
    Assert.assertTrue("Expected 12, got " + ((data[0] & 0XF0) >> 4), ((data[0] & 0xF0) >> 4) == 0xC);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve();

    Assert.assertTrue(
        "Expected -20000.00 +- 2.56, got " + value,
        value.compareTo(new BigDecimal(-19997.44)) <= 0 &&
        value.compareTo(new BigDecimal(-20002.56)) >= 0
    );

  }




  @Test public void testPositiveZeroExponentRange()
  {
    byte[] mantissaBitPattern = new byte[2048];

    BigDecimal val = new BigDecimal(0.01).setScale(2, RoundingMode.HALF_UP);
    BigDecimal increment = new BigDecimal(0.01).setScale(2, RoundingMode.HALF_UP);
    BigDecimal boundary = new BigDecimal(20.47).setScale(2, RoundingMode.HALF_UP);

    for (; val.compareTo(boundary) <= 0; val = val.add(increment))
    {
      TwoOctetFloat knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, val.floatValue());
      byte[] data = knxFloat.getData();

      Assert.assertTrue((data[0] & 0xF8) == 0);

      int mantissaValue = data[1];
      mantissaValue &= 0xFF;
      mantissaValue += (data[0] & 0x7) << 8;

      mantissaBitPattern[mantissaValue] += 1;

      knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
      BigDecimal value = knxFloat.resolve();

      Assert.assertTrue(value.compareTo(val) == 0);
    }


    Assert.assertTrue(mantissaBitPattern[0] == 0);
    
    for (int i = 1; i < 2048; ++i)
    {
      Assert.assertTrue(
          "Found " + mantissaBitPattern[i] + " at index " + i,
          mantissaBitPattern[i] == 1
      );
    }
  }

  @Test public void testNegativeZeroExponentRange()
  {
    byte[] mantissaBitPattern = new byte[2048];

    BigDecimal val = new BigDecimal(-0.01).setScale(2, RoundingMode.HALF_UP);
    BigDecimal increment = new BigDecimal(-0.01).setScale(2, RoundingMode.HALF_UP);
    BigDecimal boundary = new BigDecimal(-20.47).setScale(2, RoundingMode.HALF_UP);

    for (; val.compareTo(boundary) >= 0; val = val.add(increment))
    {
      TwoOctetFloat knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, val.floatValue());
      byte[] data = knxFloat.getData();

      Assert.assertTrue((data[0] & 0xF8) == 0x80);

      int mantissaValue = data[1];
      mantissaValue &= 0xFF;
      mantissaValue += (data[0] & 0x7) << 8;

      mantissaBitPattern[mantissaValue] += 1;


      knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
      BigDecimal value = knxFloat.resolve();

      Assert.assertTrue(value.compareTo(val) == 0);
    }

    for (int i = 1; i < 2048; ++i)
    {
      Assert.assertTrue(
          "Found " + mantissaBitPattern[i] + " at index " + i,
          mantissaBitPattern[i] == 1
      );
    }
  }

  @Test public void testFirstExponentLowBoundaryRoundingError()
  {
    // value 20.48 demonstrates a rounding error due to switching to a higher exponent --
    // this increases precision from 0.01*M*2^0 == 0.01 to 0.01*M*2^1 == 0.02
    // therefore 20.48 falls beyond upper bound of zero exponent max value of 20.47 and below
    // first exponent minimum value of 20.49
    //
    // this implementation rounds down -- therefore as far as the bit pattern is concerned,
    // values 20.47 and 20.48 cannot be distinguished
    // value 20.47 (and 20.48) translates to 2047 (0x7FF) in mantissa (0.01 precision),
    // hibyte should be 7 (no exponent) and lobyte should be 0xFF

    TwoOctetFloat knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 20.48f);
    byte[] data = knxFloat.getData();

    Assert.assertTrue("Expected 15, got " + (data[1] & 0xF), (data[1] & 0xF) == 0xF);
    Assert.assertTrue("Expected 15, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 0xF);
    Assert.assertTrue("Expected 7, got " + (data[0] & 0xF), (data[0] & 0xF) == 7);
    Assert.assertTrue("Expected 0, got " + ((data[0] & 0XF0) >> 4), ((data[0] & 0xF0) >> 4) == 0);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    int value = knxFloat.resolve().multiply(BigDecimal.TEN.pow(2)).intValue();

    Assert.assertTrue("Expected 2047, got " + value, value == 2047);
  }


  @Test public void testPositiveFirstExponentRange()
  {
    byte[] mantissaBitPattern = new byte[2048];

    BigDecimal val = new BigDecimal(20.49).setScale(2, RoundingMode.HALF_UP);
    BigDecimal increment = new BigDecimal(0.02).setScale(2, RoundingMode.HALF_UP);
    BigDecimal boundary = new BigDecimal(20.47 + 20.47*2).setScale(2, RoundingMode.HALF_UP);

    for (; val.compareTo(boundary) <= 0; val = val.add(increment))
    {
      TwoOctetFloat knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, val.floatValue());
      byte[] data = knxFloat.getData();

      Assert.assertTrue(
          "Expected 0x8, got " + (data[0] & 0xF8) + " at value " + val,
          (data[0] & 0xF8) == 0x8
      );

      int mantissaValue = data[1];
      mantissaValue &= 0xFF;
      mantissaValue += (data[0] & 0x7) << 8;

      mantissaBitPattern[mantissaValue] += 1;

      knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
      BigDecimal value = knxFloat.resolve();

      Assert.assertTrue(value.compareTo(val) == 0);

    }

    for (int i = 1; i < 2048; ++i)
    {
      Assert.assertTrue(
          "Found " + mantissaBitPattern[i] + " at index " + i,
          mantissaBitPattern[i] == 1
      );
    }
  }


  @Test public void testNegativeFirstExponentRange()
  {
    byte[] mantissaBitPattern = new byte[2048];

    BigDecimal val = new BigDecimal(-20.49).setScale(2, RoundingMode.HALF_UP);
    BigDecimal increment = new BigDecimal(-0.02).setScale(2, RoundingMode.HALF_UP);
    BigDecimal boundary = new BigDecimal(-20.47 - 20.47*2).setScale(2, RoundingMode.HALF_UP);

    for (; val.compareTo(boundary) >= 0; val = val.add(increment))
    {
      TwoOctetFloat knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, val.floatValue());
      byte[] data = knxFloat.getData();

      Assert.assertTrue(
          "Expected 0x8, got " + (data[0] & 0x8) + " at value " + val,
          (data[0] & 0x8) == 0x8
      );

      Assert.assertTrue(
          "Expected 0x8, got " + ((data[0] & 0x80) >> 4) + " at value " + val,
          ((data[0] & 0x80) >> 4) == 0x8
      );
      
      int mantissaValue = data[1];
      mantissaValue &= 0xFF;
      mantissaValue += (data[0] & 0x7) << 8;

      mantissaBitPattern[mantissaValue] += 1;


      knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
      BigDecimal value = knxFloat.resolve();

      Assert.assertTrue(value.compareTo(val) == 0);
    }

    for (int i = 1; i < 2048; ++i)
    {
      Assert.assertTrue(
          "Found " + mantissaBitPattern[i] + " at index " + i,
          mantissaBitPattern[i] == 1
      );
    }
  }

  @Test public void testPositiveFirstExponentRangeBitPatternOverlap()
  {
    byte[] mantissaBitPattern = new byte[2048];

    BigDecimal val = new BigDecimal(20.49).setScale(2, RoundingMode.HALF_UP);
    BigDecimal increment = new BigDecimal(0.01).setScale(2, RoundingMode.HALF_UP);
    BigDecimal boundary = new BigDecimal(20.47 + 20.47*2).setScale(2, RoundingMode.HALF_UP);

    for (; val.compareTo(boundary) <= 0; val = val.add(increment))
    {
      TwoOctetFloat knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, val.floatValue());
      byte[] data = knxFloat.getData();

      Assert.assertTrue(
          "Expected 0x8, got " + (data[0] & 0xF8) + " at value " + val,
          (data[0] & 0xF8) == 0x8
      );

      int mantissaValue = data[1];
      mantissaValue &= 0xFF;
      mantissaValue += (data[0] & 0x7) << 8;

      if (mantissaValue > 2047)
      {
        Assert.fail("got mantissa overflow with " + mantissaValue + " from " + val);
      }

      mantissaBitPattern[mantissaValue] += 1;


      knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
      BigDecimal value = knxFloat.resolve();

      Assert.assertTrue(
          value.compareTo(val.subtract(increment)) >= 0 &&
          value.compareTo(val.add(increment)) <= 0
      );
    }


    Assert.assertTrue(mantissaBitPattern[1] == 1);

    for (int i = 2; i < 2048; ++i)
    {
      Assert.assertTrue(
          "Found " + mantissaBitPattern[i] + " at index " + i,
          mantissaBitPattern[i] == 2
      );
    }
  }


}

