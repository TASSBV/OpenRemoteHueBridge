/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.controller.protocol.lutron;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lutron address format:
 * optionnaly enclosed in square brackets
 * 3 to five components
 * separated by period, colon, slash, backslash or dash
 * letters and spaces are ignored
 *
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class LutronHomeWorksAddress {

  // Instance Fields ------------------------------------------------------------------------------

  private byte addressElements[];

  private static Pattern addressPattern = Pattern.compile("\\x5b?(\\d{1,2})[.:/\\\\-](\\d{1,2})[.:/\\\\-](\\d{1,2})(?:[.:/\\\\-](\\d{1,2}))?(?:[.:/\\\\-](\\d{1,2}))?\\x5d?");

  // Constructors ---------------------------------------------------------------------------------

  public LutronHomeWorksAddress(String addressRepresentation) throws InvalidLutronHomeWorksAddressException {
    Matcher matcher = addressPattern.matcher(addressRepresentation.replaceAll("[\\sa-zA-Z]*", "")); // Blanks and letters are removed from string before parsing
    if (!matcher.matches()) {
      throw new InvalidLutronHomeWorksAddressException("Incorrect format for Lutron address (" + addressRepresentation + ")", addressRepresentation);
    }
    if (addressRepresentation.startsWith("[") && !addressRepresentation.endsWith("]")) {
      throw new InvalidLutronHomeWorksAddressException("Lutron address starting with [ must end with ] (" + addressRepresentation + ")", addressRepresentation);
    }

    // Note that last groups can be null and group count will be 5
    // Check for non null groups in order and have that the number of elements
    int numElements = 0;
    for (int i = 1; i <= matcher.groupCount(); i++) {
      if (matcher.group(i) != null) {
        numElements++;
      }
    }

    if (numElements < 3 || numElements > 5) {
      throw new InvalidLutronHomeWorksAddressException("Incorrect number of elements in Lutron address (" + addressRepresentation + ")", addressRepresentation);
    }

    addressElements = new byte[numElements];
    for (int i = 1; i <= numElements; i++) {
      addressElements[i - 1] = Byte.parseByte(matcher.group(i));
    }

    if (addressElements[0] < 1 || addressElements[0] > 16) {
      throw new InvalidLutronHomeWorksAddressException("Incorrect processor number (" + addressElements[0] + "), must be between 1 and 16", addressRepresentation);
    }

    switch (addressElements[1]) {
      case 1: // RPM Dimmer/Switch address
        if (numElements != 5) {
          throw new InvalidLutronHomeWorksAddressException("RPM Dimmer / Switch address must have 5 elements", addressRepresentation);
        }
        if (addressElements[2] < 0 || addressElements[2] > 15) {
          throw new InvalidLutronHomeWorksAddressException("Incorrect Router number (" + addressElements[2] + "), must be between 0 and 15", addressRepresentation);
        }
        if (addressElements[3] < 1 || addressElements[3] > 8) {
          throw new InvalidLutronHomeWorksAddressException("Incorrect Module number (" + addressElements[3] + "), must be between 1 and 8", addressRepresentation);
        }
        if (addressElements[4] < 1 || addressElements[4] > 4) {
          throw new InvalidLutronHomeWorksAddressException("Incorrect Output number (" + addressElements[4] + "), must be between 1 and 4", addressRepresentation);
        }
        break;
      case 4:
      case 5:
      case 6:
        switch (numElements) {
          case 3: // Keypad/Sivoia Control/CCO/CCI/TEL-9
            // Note, a GRAFIK Eye Main Unit has the same format and the allowed values are only in the 1-8 range.
            // It is however impossible to detect this scenario, so we only test for the broader 1-32 range.
            if (addressElements[2] < 1 || addressElements[2] > 32) {
              throw new InvalidLutronHomeWorksAddressException("Incorrect keypad number (" + addressElements[2] + "), must be between 1 and 32", addressRepresentation);
            }
            break;
          case 4: // Grafik Eye Main Unit Single Zone
            if (addressElements[2] < 1 || addressElements[2] > 8) {
              throw new InvalidLutronHomeWorksAddressException("Incorrect GRAFIK Eye number (" + addressElements[2] + "), must be between 1 and 8", addressRepresentation);
            }
            if (addressElements[3] < 1 || addressElements[3] > 8) {
              throw new InvalidLutronHomeWorksAddressException("Incorrect Output number (" + addressElements[3] + "), must be between 1 and 8", addressRepresentation);
            }
            break;
          case 5: // D48 or H48 Dimmer/Switch
            // H48 has more limited ranges but we can't detect we're in this case, so accept broader D48 range
            if (addressElements[2] < 1 || addressElements[2] > 4) {
              throw new InvalidLutronHomeWorksAddressException("Incorrect Router number (" + addressElements[2] + "), must be between 1 and 4", addressRepresentation);
            }
            if (addressElements[3] < 1 || addressElements[3] > 12) {
              throw new InvalidLutronHomeWorksAddressException("Incorrect Bus number (" + addressElements[3] + "), must be between 1 and 12", addressRepresentation);
            }
            if (addressElements[4] < 1 || addressElements[4] > 8) {
              throw new InvalidLutronHomeWorksAddressException("Incorrect Dimmer number (" + addressElements[4] + "), must be between 1 and 8", addressRepresentation);
            }
            break;
        }
        break;
      case 8:
        if (numElements != 4) {
          throw new InvalidLutronHomeWorksAddressException("All link 8 address must have 4 elements", addressRepresentation);
        }
        switch (addressElements[2]) {
          case 1:
            // RF Dimmer/Switch
            if (addressElements[3] < 1 || addressElements[3] > 64) {
              throw new InvalidLutronHomeWorksAddressException("Incorrect Dimmer number (" + addressElements[3] + "), must be between 1 and 64", addressRepresentation);             
            }
            break;
          case 2:
            // RF Keypad
            if (addressElements[3] < 1 || addressElements[3] > 32) {
              throw new InvalidLutronHomeWorksAddressException("Incorrect Keypad number (" + addressElements[3] + "), must be between 1 and 32", addressRepresentation);             
            }
            break;
          case 3:
            // RF Repeater
            if (addressElements[3] < 1 || addressElements[3] > 4) {
              throw new InvalidLutronHomeWorksAddressException("Incorrect Repeater number (" + addressElements[3] + "), must be between 1 and 4", addressRepresentation);             
            }
            break;
          default:
            throw new InvalidLutronHomeWorksAddressException("Incorrect link 8 device type", addressRepresentation);            
        }
        break;
      default:
        throw new InvalidLutronHomeWorksAddressException("Not a supported address", addressRepresentation);
        
    }
  }

  /**
   * Returns string representation of this group address with the following conventions
   * - address enclosed between [ and ]
   * - using semi-colon as separator
   * - elements on 2 digits, 0 padded
   *
   * @return Lutron address appropriately formatted
   */
  @Override
  public String toString() {
    StringBuffer temp = new StringBuffer("[");
    for (int i = 0; i < addressElements.length; i++) {
      temp.append(String.format("%02d", addressElements[i]));
      if (i != addressElements.length - 1) {
        temp.append(":");
      }
    }
    temp.append("]");
    return temp.toString();
  }
  
  /**
   * Tests address type and validity.
   * @return whether or not address is one of a keypad
   */
  public boolean isValidKeypadAddress() {
    switch (addressElements.length) {
      case 3: // Wired keypad
        return (addressElements[1] >= 4 && addressElements[1] <= 6 && addressElements[2] >= 1 && addressElements[2] <= 32);
      case 4: // RF keypad
        return (addressElements[1] == 8 && addressElements[2] == 2 && addressElements[3] >= 1 && addressElements[3] <= 32);
      default:
        return false;
    }
  }
  
  /**
   * Tests address type and validity.
   * @return whether or not address is one of a dimmer
   */
  public boolean isValidDimmerAddress() {
    switch (addressElements.length) {
      case 4: // RF Dimmer
        return (addressElements[1] != 8 && addressElements[2] !=1 && addressElements[3] >= 1 && addressElements[3] <= 64);
      case 5:
        switch (addressElements[1]) {
          case 1: // RPM Dimmer
            return (addressElements[2] >= 0 && addressElements[2] <= 15 && addressElements[3] >= 1 && addressElements[3] <= 8 && addressElements[4] >= 1 && addressElements[4] <= 4);
          case 4:
          case 5:
          case 6:
            // D48 / H48 Dimmer
            if (addressElements[3] > 6 && addressElements[4] > 4) {
              return false;
            }
            return (addressElements[2] >= 1 && addressElements[2] <= 4 && addressElements[3] >= 1 && addressElements[3] <= 12 && addressElements[4] >= 1 && addressElements[4] <= 8); 
          default:
            return false;
        }
    }
    return false;
  }
  
  /**
   * Tests address type and validity.
   * @return whether or not address is one of a GRAFIK Eye
   */
  public boolean isValidGrafikEyeAddress() {
    if (addressElements.length != 3) {
      return false;
    }
    return (addressElements[2] >= 1 && addressElements[2] <= 8);
  }
  
  /**
   * Tests address type and validity.
   * @return whether or not address is one of a GRAFIK Eye single zone
   */
  public boolean isValidGrafikEyeSingleZoneAddress() {
    if (addressElements.length != 4) {
      return false;
    }
    return (addressElements[1] >= 4 && addressElements[1] <= 6);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof LutronHomeWorksAddress)) {
      return false;
    }
    return Arrays.equals(addressElements, ((LutronHomeWorksAddress) obj).addressElements);
  }

  @Override
  public int hashCode() {
    int hash = addressElements.length;
    for (int i = 0; i < addressElements.length; i++) {
      hash += addressElements[i];
    }
    return hash;
  }

}
