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

/**
 * Floating 2 Byte datatype as defined in KNX 1.1 Volume 3: System specifications Part 7:
 * Interworking, Chapter 2, Datapoint Types. <p>
 *
 * KNX Unsigned 8-bit datatype is a 8-bit value in the range of [0..255]. <p>
 *
 * There are several datapoint types that use Floating 2 Byte datatype:
 *
 * <ol>
 * <li>DPT 9.001 - DPT_Value_Temp</li>
 * <li>DPT 9.023 - DPT_KELVINPERPERCENT</li>          
 * <li>DPT 9.024 - DPT_POWER</li>          
 * <li>DPT 9.022 - DPT_POWERDENSITY</li>          
 * <li>DPT 9.008 - DPT_VALUE_AIRQUALITY</li>          
 * <li>DPT 9.021 - DPT_VALUE_CUR</li>          
 * <li>DPT 9.007 - DPT_VALUE_HUMIDITY</li>          
 * <li>DPT 9.004 - DPT_VALUE_LUX</li>          
 * <li>DPT 9.006 - DPT_VALUE_PRES</li>          
 * <li>DPT 9.001 - DPT_VALUE_TEMP</li>          
 * <li>DPT 9.003 - DPT_VALUE_TEMPA</li>          
 * <li>DPT 9.002 - DPT_VALUE_TEMPD</li>          
 * <li>DPT 9.010 - DPT_VALUE_TIME1</li>          
 * <li>DPT 9.011 - DPT_VALUE_TIME2</li>          
 * <li>DPT 9.020 - DPT_VALUE_VOLT</li>          
 * <li>DPT 9.025 - DPT_VALUE_VOLUME_FLOW</li>          
 * <li>DPT 9.005 - DPT_VALUE_WSP</li>          
 * </ol>
 *
 * For DPT 9.001 value temperature, the range value is a 2 byte floating value.  <p>
 *
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Float2Byte implements DataType
{

  // Private Instance Fields --------------------------------------------------------------------

  private byte[] value;
  private DataPointType dpt;


  // Constructors -------------------------------------------------------------------------------

  public Float2Byte(DataPointType.Float2ByteValue dpt, byte[] value)
  {
         this.value = (byte[])(value);

         this.dpt = dpt;
 }


  // Implements DataType ------------------------------------------------------------------------

  public int getDataLength()
  {
    return 2;
  }

  public byte[] getData()
  {
    return value;
  }

  public DataPointType getDataPointType()
  {
    return dpt;
  }


  // To convert from decimal representation to DPT_Value_Temp (9.001), the following steps shall be done:
  //   1. The value has to be noted with a precision of 0,01
  //   EXAMPLE 315 ¬∞C is noted as 31500
  //   2. This is divided by 2 until you have a value smaller than 2047. With each division, the Exponent is incremented. This gives the smalled exponent, which is the most accurate coding.
  //   EXAMPLE 1968,75 with 4 devisions by 2.
  //   3. The mantissa can only carry the integer part of the value, so 1968,75 becomes 1968. Here of course, truncation errors occur.
  //   4. The integer value of the mantissa is binary encoded.
  //   EXAMPLE 1968 becomes 11110110000b
  //   5. The DPT_Value_Temp value is composed as in the given format: M EEEE MMM MMMM MMMM
  //   EXAMPLE 0 0100 111 1011 0000 is hexadecimally noted 27B0h.
  //
  // So to convert DPT_Value_Temp (9.001) to decimal representation, follows this steps from 5 to 1
  
  public float resolve()
  {
    if (dpt == DataPointType.Float2ByteValue.VALUE_TEMP)
    {
      float temperature = 0;

      // false for Positive sign and true for negative sign
      boolean sign = (value[0] & 0x80)== 0x80;
      byte exponent = (byte) ((value[0] & 0x78)>>3);
      int unsigned = (int)(value[1] & 0xFF);

      int mantisse = (int) ((value[0]&0x07)<<8)+unsigned;

      for (byte i=0; i<exponent; i++) 
    	mantisse = mantisse*2;

      if (!sign)
    	temperature = (float)mantisse/100;
      else
    	temperature = -(float)mantisse/100;
    	
      return temperature;
    }

    else
    {
      throw new Error("Unrecognized Float 2 Byte datapoint type " + dpt);
    }
  }
  

  
}

