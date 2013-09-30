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

/**
 * String related utilities.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author <a href="mailto:marcus@openremote.org">Marcus Redeker</a>
 */
public class Strings
{

  /**
   * Translates a signed Java byte into an unsigned hex string.
   *
   * For example:
   *
   * <pre>{@code
   *
   *    1  -> 0x01
   *   15  -> 0x0F
   *   16  -> 0x10
   *   -1  -> 0xFF
   * -128  -> 0xC0
   *
   * }</pre>
   *
   * @param b   byte to convert
   *
   * @return    Unsigned hex representation of the byte value with a '0x' prefix. Returned values
   *            are always padded to two digits, so '1' becomes '0x01'.
   */
  public static String byteToUnsignedHexString(byte b)
  {
    int value = b;

    value &= 0xFF;
    
    return ( value < 16 ? "0x0" + Integer.toHexString(value).toUpperCase()
                        : "0x"  + Integer.toHexString(value).toUpperCase());
  }
  
  /**
   * Converts a String which represents a pollingInterval into an int which can be used as delay
   * for Thread.sleep();  <p>
   *
   * Following conversion will happen:  <p>
   *
   * null || "" = -1                  <br>
   * 500 = 500                        <br>
   * 1s = 1000 * 1 = 1000             <br>
   * 1m = 1000 * 60 = 60000           <br>
   * 1h = 1000 * 60 * 60 = 3600000    <br>
   * 
   * @param pollingInterval   interval string, such as '1m' as millisecond value
   *
   * @return  interval in milliseconds
   */
  public static int convertPollingIntervalString(String pollingInterval)
  {
    if ((pollingInterval != null) && (pollingInterval.trim().length() != 0))
    {
      pollingInterval = pollingInterval.trim();
      char lastChar = pollingInterval.charAt(pollingInterval.length()-1);
      String timePart = pollingInterval.substring(0, pollingInterval.length()-1);

      switch (lastChar)
      {
        case 's':
          return Integer.parseInt(timePart) * 1000;
        case 'm':
          return Integer.parseInt(timePart) * 1000 * 60;
        case 'h':
          return Integer.parseInt(timePart) * 1000 * 60 * 60;
        default:
          return Integer.parseInt(pollingInterval);
      }
    }

    return -1;
  }


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Only utility class methods.
   */
  private Strings()
  {}

}
