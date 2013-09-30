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
package org.openremote.controller.protocol.knx;

import org.apache.log4j.Logger;

/**
 * KNX Addressing on the common EMI wireformat is a two byte field, consisting of address
 * high byte (a.k.a Octet 0) and low byte (a.k.a Octet 1) [KNX 1.1].  <p>
 *
 * [KNX 1.1] Volume 3: Systems Specifications, Part 3 Chapter 2: Data Link Layer General
 * defines Group Address bit structure (1.4 Definitions on page 6-7) as follows: <p>
 *
 * <pre>{@code
 *
 *            +-----------------------------------------------+
 *  16 bits   |                 GROUP ADDRESS                 |
 *            +-----------------------+-----------------------+
 *            | OCTET 0 (high byte)   |  OCTET 1 (low byte)   |
 *            +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *     bits   | 7| 6| 5| 4| 3| 2| 1| 0| 7| 6| 5| 4| 3| 2| 1| 0|
 *            +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *            |  |  Main Group (S13)  |   Sub Group (S13)     |
 *            +--+--------------------+-----------------------+
 *
 * }</pre>
 *
 * KNX Group addresses do not need to be unique and a device may have more than one group
 * address. Group addresses are defined globally for the entire KNX network (however, in
 * the message frames it is possible to restrict the number of routers that can be crossed
 * to reach devices).  A group address zero is sent to every device (it is a broadcast).
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
class GroupAddress
{
  //
  // NOTE:
  //        Supplement 13 (S13) to KNX 1.1 specification shows additional structure with
  //        Main Group (what appears a 7 bit value) and Sub Group (a 8 bit value) but gives
  //        no further definition for these fields.
  //                                                                                  [JPL]
  //
  // NOTE:
  //        Regarding the group address segments, I haven't found the corresponding
  //        KNX specification to support the common convention of main/middle/sub levels
  //        (5/3/8 bits respectively) or main/sub levels (5/11 bits respectively).
  //
  //        This implementation will however treat '/' separated group address fields
  //        according to these conventions in an attempt to maintain interoperability.
  //        If there's a spec recommendation for this convention somewhere, let me know
  //        (haven't looked through KNX 2.0 to see if it clarifies group address structure).
  //
  //                                                                                  [JPL]
  //


  // Class Members --------------------------------------------------------------------------------

  /**
   * KNX logger. Uses a common category for all KNX related logging.
   */
  private final static Logger log = Logger.getLogger(KNXCommandBuilder.KNX_LOG_CATEGORY);


  /**
   * Formats a two byte group address into a common convention of main/middle/sub string
   * (5/3/8 bits, respectively.
   *
   * @param address two byte array representing a KNX group address.
   *
   * @return
   */
  static String formatToMainMiddleSub(byte[] address)
  {
    int firstByte  = address[0];
    int secondByte  = address[1];

    String sub  = "" + (secondByte & 0xFF);
    String mid  = "" + (firstByte & 0x07);
    String main = "" + ((firstByte & 0xFF) >> 3);

    return main + "/" + mid + "/" + sub;
  }

  // Instance Fields ------------------------------------------------------------------------------

  /**
   * The first byte of a group address.
   */
  private byte hiByte = 0x00;

  /**
   * The second byte of a group address.
   */
  private byte loByte = 0x00;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a KNX group address instance from a string definition. <p>
   *
   * The assumed convention of the string is 'main/middle/sub' (5/3/8 bits respectively).
   * The largest group address therefore is '31/7/255'. Note that group address 0/0/0 is used
   * for system broadcasts.
   *
   * @param groupAddress  KNX group address as a string
   *
   * @throws InvalidGroupAddressException if parsing the group address string fails for any reason
   */
  GroupAddress(String groupAddress) throws InvalidGroupAddressException
  {
    if (groupAddress.contains("/"))
    {
      // We take a forward slash ('/') to mean group address semantics -- either as
      // three-level 5bit/3bit/8bit (main/middle/sub) hierarchy or 5bit/11bit (main/sub)
      // hierarchy...

      parseForwardSlashConvention(groupAddress);
    }

    else
    {
      // TODO :
      //   if no address structure, could interpret as two byte value address. But for now
      //   we mandate forward slash convention.

      throw new InvalidGroupAddressException(
          "KNX Group Address must be in format main/middle/sub or main/sub (" + groupAddress + ")",
          groupAddress
      );
    }
  }

  /**
   * Constructs group address from a two-byte representation (as is usually found in common EMI
   * frame, for instance).
   *
   * @param hibyte    the first byte (first octet) of the group address
   * @param lobyte    the second byte (second octet) of the group address
   */
  GroupAddress(byte hibyte, byte lobyte)
  {
    this.hiByte = hibyte;
    this.loByte = lobyte;
  }


  // Object Overrides -----------------------------------------------------------------------------


  /**
   * Compares the two-byte representation (KNX wire format) of two group addresses. Equal
   * address values return true.
   *
   * @param o     group address to compare to
   * @return      true if equal, false otherwise
   */
  @Override public boolean equals(Object o)
  {
    if (o == null)
      return false;

    if (!o.getClass().equals(this.getClass()))
      return false;

    GroupAddress addr = (GroupAddress)o;

    return addr.hiByte == this.hiByte && addr.loByte == this.loByte;
  }

  /**
   * {@inheritDoc}
   */
  @Override public int hashCode()
  {
    int hash = hiByte;
    hash = hash << 8;

    return hash + loByte;
  }

  /**
   * Returns string representation of this group address as defined in
   * {@link #formatToMainMiddleSub(byte[])}
   *
   * @return group address formatted to main/mid/sub convention
   */
  @Override public String toString()
  {
    return formatToMainMiddleSub(new byte[] { hiByte, loByte });
  }


  // Package-Private Instance Methods -------------------------------------------------------------

  /**
   * Returns the group address as a 2-byte array.
   *
   * @return array of exactly two bytes, with group address high byte first
   *         (main/middle level bits), followed by low byte (sub level 8 bits).
   */
  byte[] asByteArray()
  {
    return new byte[] { hiByte, loByte };
  }

  
  
  // Private Instance Methods ---------------------------------------------------------------------

  /**
   * Attempts to parse the string with a main/mid/sub level string convention.
   *
   * @param groupAddress    group address as a string
   *
   * @throws InvalidGroupAddressException if parse fails for any reason
   */
  private void parseForwardSlashConvention(String groupAddress) throws InvalidGroupAddressException
  {
    String [] elements = groupAddress.split("/");

    // Interpret group address in 3 segments as 5/3/8 bit sequence...

    if (elements.length == 3)
    {

      try
      {
        int hibits = new Integer(elements[0]);
        int midbits = new Integer(elements[1]);
        int lowbits = new Integer(elements[2]);

        // Sanity checks on address field sizes -- 5 bits is at most 31 decimal...

        if (hibits < 0 || hibits > 31)
        {
          throw new InvalidGroupAddressException(
              "Group address value '" + hibits + "' in '" + groupAddress + "' is too large.",
              groupAddress
          );
        }

        // ...middle bits is max 7 decimal (%111)...

        if (midbits < 0 || midbits > 7)
        {
          throw new InvalidGroupAddressException(
              "Group address value '" + midbits + "' in '" + groupAddress + "' is too large.",
              groupAddress
          );
        }

        // ...low bits max 255 (8 bits)...

        if (lowbits < 0 || lowbits > 255)
        {
          throw new InvalidGroupAddressException(
              "Group address value '" + lowbits + "' in '" + groupAddress + "' is too large.",
              groupAddress
          );
        }

        // shift hibits by 3 to the left to make space for midbits in the first address byte..

        hibits = hibits << 3;

        // and merge with the middle bits...

        hibits = hibits | midbits;

        // store in two bytes for cEMI frame...

        hiByte = (byte)hibits;
        loByte = (byte)lowbits;
      }
      catch (NumberFormatException exception)
      {
        throw new InvalidGroupAddressException(
            "Cannot parse group address '" + groupAddress +
            "' (assuming 5/3/8 bit format): " + exception.getMessage(),
            groupAddress,
            exception
        );
      }
    }

    // Interpret group address as 5/11 bit segments...

    else if (elements.length == 2)
    {
      // TODO...

      throw new InvalidGroupAddressException(
          "Dual segment (main/sub) style group addresses not implemented yet ('" +
          groupAddress + "')", groupAddress
      );
    }

    else
    {
      throw new InvalidGroupAddressException(
          "Unknown group address structure in '" + groupAddress + "'.", groupAddress
      );
    }
  }

}
