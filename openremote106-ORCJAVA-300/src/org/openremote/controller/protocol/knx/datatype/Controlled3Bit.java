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
   * Controlled 3-bit datatype as defined in KNX 1.1 Volume 3: System specifications Part 7:
 * Interworking, Chapter 2, Datapoint Types. <p>
 *
 * KNX Controlled 3-bit datatype is a 4-bit value represented by a one-bit control and
 * a 3-bit (range [0..7] integer value. <p>
 *
 * The control bit represents one of the following Boolean DPTs: DPT 1.007 (INCREASE/DECREASE),
 * DPT 1.008 (UP/DOWN) or DTP 1.014 (FIXED/CALCULATED). <p>
 *
 * There are 3 datapoint types that use 3-bit controlled datatype:
 *
 * <ol>
 * <li>DPT 3.007 - DPT_Control_Dimming</li>
 * <li>DPT 3.008 - DPT_Control_Blinds</li>
 * <li>DPT 3.009 - DPT_Mode_Boiler</li>
 * </ol>
 *
 * DPT 3.007 control dimming must use DPT 1.007 boolean increase/decrease as its control bit.
 * DPT 3.008 control blinds must use DPT 1.008 boolean up/down as its control bit.
 * And DPT 3.009 boiler mode must use DTP 1.014 boolean fixed/calculated as its control bit. <p>
 *
 * For DTP 3.007 and DPT 3.008 control dimming and blinds the 3-bit value is interpreted as a
 * step range between [1-7]. For DPT 3.009 boiler mode the 3-bit value is interpreted as one of
 * three separate modes where value 1 maps to Mode 0, value 2 maps to Mode 1 and value 4 maps to
 * Mode 2.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Controlled3Bit implements DataType
{

  // Private Instance Fields --------------------------------------------------------------------

  private DataPointType dpt;
  private Bool controlBit;
  private int value = 0;


  // Constructors -------------------------------------------------------------------------------

  public Controlled3Bit(DataPointType.Control3BitDataPointType dpt, Bool controlBitValue, int value)
  {
    if (controlBitValue != Bool.INCREASE && controlBitValue != Bool.DECREASE &&
        controlBitValue != Bool.UP && controlBitValue != Bool.DOWN &&
        controlBitValue != Bool.FIXED && controlBitValue != Bool.CALCULATED)
    {
      throw new Error("Control bit must be DPT 1.007, DPT 1.008 or DPT 1.014");
    }

    if (value < 0 || value > 7)
    {
      throw new Error("Control dim range must be between [0-7] (received: " + value + ").");
    }

    if (dpt == DataPointType.Control3BitDataPointType.MODE_BOILER)
    {
      if (value != 1 && value != 2 && value != 4)
      {
        throw new Error("Boiler mode value must be 1, 2 or 4.");
      }
    }

    this.dpt = dpt;
    this.controlBit = controlBitValue;
    this.value = value;
  }

  public int getDataLength()
  {
    return 1;
  }

  public byte[] getData()
  {
    int controlData = controlBit.getData() [0];

    controlData = controlData << 3;

    return new byte[] { (byte)(controlData + value) };
  }

  public DataPointType getDataPointType()
  {
    return dpt;
  }

}

