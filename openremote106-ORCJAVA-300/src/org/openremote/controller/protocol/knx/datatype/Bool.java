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
   * Boolean datatype as defined in KNX 1.1 Volume 3: System specifications Part 7: Interworking,
 * Chapter 2, Datapoint Types. <p>
 *
 * KNX Boolean datatype is a 1-bit value represented by either 0 or 1 integer value. There are
 * 14 different datapoint types that use Boolean, each with its own encoding for the 1-bit value:
 *
 * <ol>
 * <li>DPT 1.001 - DPT_Switch</li>
 * <li>DPT 1.002 - DPT_Bool</li>
 * <li>DPT 1.003 - DPT_Enable</li>
 * <li>DPT 1.004 - DPT_Ramp</li>
 * <li>DPT 1.005 - DPT_Alarm</li>
 * <li>DPT 1.006 - DPT_BinaryValue</li>
 * <li>DPT 1.007 - DPT_Step</li>
 * <li>DPT 1.008 - DPT_UpDown</li>
 * <li>DPT 1.009 - DPT_OpenClose</li>
 * <li>DPT 1.010 - DPT_Start</li>
 * <li>DPT 1.011 - DPT_State</li>
 * <li>DPT 1.012 - DPT_Invert</li>
 * <li>DPT 1.013 - DPT_DimSendStyle</li>
 * <li>DPT 1.014 - DPT_InputSource</li>
 * </ol>
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Bool implements DataType
{

  // Constants ----------------------------------------------------------------------------------


  /**
   * Value type for 'Boolean' datatype (KNX 1.1 Vol 3: Part 7.2 - Datapoint Types): {@value}
   */
  private final static byte DATATYPE_BOOLEAN_ONE = 0x01;

  /**
   * Value type for 'Boolean' datatype (KNX 1.1 Vol 3: Part 7.2 - Datapoint Types): {@value}
   */
  private final static byte DATATYPE_BOOLEAN_ZERO = 0x00;

  /**
   * DPT 1.001 - DPT_Switch. Value 0 = OFF, general use.
   */
  public final static Bool OFF = new Bool(
      "1.001",
      DataPointType.BooleanDataPointType.SWITCH,
      DATATYPE_BOOLEAN_ZERO
  );

  /**
   * DPT 1.001 - DPT_Switch. Value 1 = ON, general use.
   */
  public final static Bool ON = new Bool(
      "1.001",
      DataPointType.BooleanDataPointType.SWITCH,
      DATATYPE_BOOLEAN_ONE
  );

  /**
   * DPT 1.002 - DPT_Bool. Value 0 = FALSE, general use.
   */
  public final static Bool FALSE = new Bool(
      "1.002",
      DataPointType.BooleanDataPointType.BOOL,
      DATATYPE_BOOLEAN_ZERO
  );

  /**
   * DPT 1.002 - DPT_Bool. Value 1 = TRUE, general use.
   */
  public final static Bool TRUE = new Bool(
      "1.002",
      DataPointType.BooleanDataPointType.BOOL,
      DATATYPE_BOOLEAN_ONE
  );

  /**
   * DPT 1.003 - DPT_Enable. Value 0 = DISABLE, general use.
   */
  public final static Bool DISABLE = new Bool(
      "1.003",
      DataPointType.BooleanDataPointType.ENABLE,
      DATATYPE_BOOLEAN_ZERO
  );

  /**
   * DPT 1.003 - DPT_Enable. Value 1 = ENABLE, general use.
   */
  public final static Bool ENABLE = new Bool(
      "1.003",
      DataPointType.BooleanDataPointType.ENABLE,
      DATATYPE_BOOLEAN_ONE
  );

  /**
   * DPT 1.004 - DPT_Ramp. Value 0 = NO_RAMP, functional blocks only.
   */
  public final static Bool NO_RAMP = new Bool(
      "1.004",
      DataPointType.BooleanDataPointType.RAMP,
      DATATYPE_BOOLEAN_ZERO
  );

  /**
   * DPT 1.004 - DPT_Ramp. Value 1 = RAMP, functional blocks only.
   */
  public final static Bool RAMP = new Bool(
      "1.004",
      DataPointType.BooleanDataPointType.RAMP,
      DATATYPE_BOOLEAN_ONE
  );

  /**
   * DPT 1.005 - DPT_Alarm. Value 0 = NO_ALARM, functional blocks only.
   */
  public final static Bool NO_ALARM = new Bool(
      "1.005",
      DataPointType.BooleanDataPointType.ALARM,
      DATATYPE_BOOLEAN_ZERO
  );

  /**
   * DPT 1.005 - DPT_Alarm. Value 1 = ALARM, functional blocks only.
   */
  public final static Bool ALARM = new Bool(
      "1.005",
      DataPointType.BooleanDataPointType.ALARM,
      DATATYPE_BOOLEAN_ONE
  );

  /**
   * DPT 1.006 - DPT_BinaryValue. Value 0 = LOW, functional blocks only.
   */
  public final static Bool LOW = new Bool(
      "1.006",
      DataPointType.BooleanDataPointType.BINARY_VALUE,
      DATATYPE_BOOLEAN_ZERO
  );

  /**
   * DPT 1.006 - DPT_BinaryValue. Value 1 = HIGH, functional blocks only.
   */
  public final static Bool HIGH = new Bool(
      "1.006",
      DataPointType.BooleanDataPointType.BINARY_VALUE,
      DATATYPE_BOOLEAN_ONE
  );

  /**
   * DPT 1.007 - DPT_Step. Value 0 = DECREASE, functional blocks only.
   */
  public final static Bool DECREASE = new Bool(
      "1.007",
      DataPointType.BooleanDataPointType.STEP,
      DATATYPE_BOOLEAN_ZERO
  );

  /**
   * DPT 1.007 - DPT_Step. Value 1 = INCREASE, functional blocks only.
   */
  public final static Bool INCREASE = new Bool(
      "1.007",
      DataPointType.BooleanDataPointType.STEP,
      DATATYPE_BOOLEAN_ONE
  );

  /**
   * DPT 1.008 - DPT_UpDown. Value 0 = UP, general use.
   */
  public final static Bool UP = new Bool(
      "1.008",
      DataPointType.BooleanDataPointType.UP_DOWN,
      DATATYPE_BOOLEAN_ZERO
  );

  /**
   * DPT 1.008 - DPT_UpDown. Value 1 = DOWN, general use.
   */
  public final static Bool DOWN = new Bool(
      "1.008",
      DataPointType.BooleanDataPointType.UP_DOWN,
      DATATYPE_BOOLEAN_ONE
  );

  /**
   * DPT 1.009 - DPT_OpenClose. Value 0 = OPEN, general use.
   */
  public final static Bool OPEN = new Bool(
      "1.009",
      DataPointType.BooleanDataPointType.OPEN_CLOSE,
      DATATYPE_BOOLEAN_ZERO
  );

  /**
   * DPT 1.009 - DPT_OpenClose. Value 1 = CLOSE, general use.
   */
  public final static Bool CLOSE = new Bool(
      "1.009",
      DataPointType.BooleanDataPointType.OPEN_CLOSE,
      DATATYPE_BOOLEAN_ONE
  );

  /**
   * DPT 1.010 - DPT_Start. Value 0 = STOP, general use.
   */
  public final static Bool STOP = new Bool(
      "1.010",
      DataPointType.BooleanDataPointType.START,
      DATATYPE_BOOLEAN_ZERO
  );

  /**
   * DPT 1.010 - DPT_Start. Value 1 = START, general use.
   */
  public final static Bool START = new Bool(
      "1.010",
      DataPointType.BooleanDataPointType.START,
      DATATYPE_BOOLEAN_ONE
  );

  /**
   * DPT 1.011 - DPT_State. Value 0 = INACTIVE, functional blocks only.
   */
  public final static Bool INACTIVE = new Bool(
      "1.011",
      DataPointType.BooleanDataPointType.STATE,
      DATATYPE_BOOLEAN_ZERO
  );

  /**
   * DPT 1.011 - DPT_State. Value 1 = ACTIVE, functional blocks only.
   */
  public final static Bool ACTIVE = new Bool(
      "1.011",
      DataPointType.BooleanDataPointType.STATE,
      DATATYPE_BOOLEAN_ONE
  );

  /**
   * DPT 1.012 - DPT_Invert. Value 0 = NOT INVERTED, functional blocks only.
   */
  public final static Bool NOT_INVERTED = new Bool(
      "1.012",
      DataPointType.BooleanDataPointType.INVERT,
      DATATYPE_BOOLEAN_ZERO
  );

  /**
   * DPT 1.012 - DPT_Invert. Value 1 = INVERTED, functional blocks only.
   */
  public final static Bool INVERTED = new Bool(
      "1.012",
      DataPointType.BooleanDataPointType.INVERT,
      DATATYPE_BOOLEAN_ONE
  );

  /**
   * DPT 1.013 - DPT_DimSendStyle. Value 0 = START_STOP, functional blocks only.
   */
  public final static Bool START_STOP = new Bool(
      "1.013",
      DataPointType.BooleanDataPointType.DIM_SEND_STYLE,
      DATATYPE_BOOLEAN_ZERO
  );

  /**
   * DPT 1.013 - DPT_DimSendStyle. Value 1 = CYCLICALLY, functional blocks only.
   */
  public final static Bool CYCLICALLY = new Bool(
      "1.013",
      DataPointType.BooleanDataPointType.DIM_SEND_STYLE,
      DATATYPE_BOOLEAN_ONE
  );

  /**
   * DPT 1.014 - DPT_InputSource. Value 0 = FIXED, functional blocks only.
   */
  public final static Bool FIXED = new Bool(
      "1.014",
      DataPointType.BooleanDataPointType.INPUT_SOURCE,
      DATATYPE_BOOLEAN_ZERO
  );

  /**
   * DPT 1.014 - DPT_InputSource. Value 1 = CALCULATED, functional blocks only.
   */
  public final static Bool CALCULATED = new Bool(
      "1.014",
      DataPointType.BooleanDataPointType.INPUT_SOURCE,
      DATATYPE_BOOLEAN_ONE
  );


  /**
   * Datatype used for Group Value Read service with 'SWITCH' device.
   *
   * @see org.openremote.controller.protocol.knx.datatype.DataPointType.BooleanDataPointType#SWITCH
   */
  public final static DataType READ_SWITCH = new DataType()
  {
    public int getDataLength()
    {
      return 1;
    }

    public byte[] getData()
    {
      return new byte[] { 0x00 };
    }

    public DataPointType getDataPointType()
    {
      return DataPointType.BooleanDataPointType.SWITCH;
    }
  };



  // Class Members ------------------------------------------------------------------------------


  /**
   * TODO
   *
   * @param apdu
   * @return
   */
  public static Bool createSwitchResponse(byte[] apdu)
  {
    return new Bool(
        "1.001",
        DataPointType.BooleanDataPointType.SWITCH,
        (byte)(apdu[1] & 0x3F)
    );
  }



  // Private Instance Fields --------------------------------------------------------------------

  /**
   * The boolean value held by this datatype instance.
   */
  private byte value = 0x00;

  /**
   * Datapoint type for this boolean datatype.
   */
  private DataPointType.BooleanDataPointType dataPointType;


  // Constructors -------------------------------------------------------------------------------

  /**
   * New KNX Boolean datatype with a given value. Valid values are 0x00 and 0x01.
   *
   * @param dptID   TODO
   * @param dpt     TODO
   * @param value   either 0x00 or 0x01, see constants in this class for how these
   *                two values are encoded (on/off, start/stop, etc.)
   */
  public Bool(String dptID, DataPointType.BooleanDataPointType dpt, byte value)
  {
    if (value < 0 || value > 1)
      throw new Error("Implementation Error: Boolean value must be either 1 or 0.");

    this.value = value;
    this.dataPointType = dpt;
  }


  // Implements DataType ------------------------------------------------------------------------

  /**
   * Returns data length of 1 for all boolean datatype points.
   *
   * @return value of 1
   */
  public int getDataLength()
  {
    return 1;
  }

  /**
   * Returns a single byte array for all boolean datatype points. The first and only byte
   * in the array contains the current datatype value, either 0x00 or 0x01.
   *
   * @return datatype point value as a single byte array
   */
  public byte[] getData()
  {
    return new byte[] { value };
  }

  /**
   * {@inheritDoc}
   */
  public DataPointType getDataPointType()
  {
    return dataPointType;
  }

}

