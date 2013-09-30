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

/**
 * KNX Indiviual Address defined in the Data Link Layer is a two byte field, consisting of
 * address high byte (a.k.a Octet 0) and low byte (a.k.a Octet 1).  <p>
 *
 * Individual addresses must unique. Each device in a KNX network must have an individual address.
 * The Device Address byte (see below) must be unique within a subnetwork. <p>
 *
 * Device Address 0 is reserved for a router in the subnetwork. Other devices have addresses in
 * the range [1..255].
 *
 *<pre>{@code
 *
 *               +-----------------------------------------------+
 *     16 bits   |              INDIVIDUAL ADDRESS               |
 *               +-----------------------+-----------------------+
 *               | OCTET 0 (high byte)   |  OCTET 1 (low byte)   |
 *               +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *        bits   | 7| 6| 5| 4| 3| 2| 1| 0| 7| 6| 5| 4| 3| 2| 1| 0|
 *               +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *               |  Subnetwork Address   |                       |
 *               +-----------+-----------+     Device Address    |
 *               |(Area Adrs)|(Line Adrs)|                       |
 *               +-----------------------+-----------------------+
 *
 * }</pre>
 *
 * Notable is that the specification recommends (but does not mandate) the Subnetwork Address to
 * be interpreted as shown above with Area Address and Line Address segments when hierarchical
 * topology is used in configuration. Area address is assumed to be unique within a network where
 * as line address is assumed to be unique within an area.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class IndividualAddress
{

  static String formatToAreaLineDevice(byte[] address)
  {
    String devc = (address[1] < 0)    ? "" + address[1] * -1             : "" + address[1];
    String line = (address[0] < 0)    ? "" + ((address[0] * -1) & 0x0F)  : "" + (address[0] & 0x0F);
    String area = (address[0] < 0)    ? "" + ((address[0] * -1) >> 4)    : "" + (address[0] >> 4);

    return area + "." + line + "." + devc;
  }
}
