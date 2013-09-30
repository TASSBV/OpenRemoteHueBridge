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

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.openremote.controller.exception.NoSuchCommandException;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public abstract class DataPointType
{

  // Class Members --------------------------------------------------------------------------------

  private final static Map<String, DataPointType> lookup = new HashMap<String, DataPointType>(100);

  public final static BooleanDataPointType SWITCH = new BooleanDataPointType(
      1, 1,
      Bool.OFF,  Bool.ON
  );
  public final static BooleanDataPointType BOOL = new BooleanDataPointType(
      1, 2,
      Bool.FALSE,  Bool.TRUE
  );
  public final static BooleanDataPointType ENABLE = new BooleanDataPointType(
      1, 3,
      Bool.DISABLE, Bool.ENABLE
  );
  public final static BooleanDataPointType RAMP = new BooleanDataPointType(
      1, 4,
      Bool.NO_RAMP, Bool.RAMP
  );
  public final static BooleanDataPointType ALARM = new BooleanDataPointType(
      1, 5,
      Bool.NO_ALARM, Bool.ALARM
  );
  public final static BooleanDataPointType BINARY_VALUE = new BooleanDataPointType(
      1, 6,
      Bool.LOW, Bool.HIGH
  );
  public final static BooleanDataPointType STEP = new BooleanDataPointType(
      1, 7,
      Bool.DECREASE, Bool.INCREASE
  );
  public final static BooleanDataPointType UP_DOWN = new BooleanDataPointType(
      1, 8,
      Bool.UP, Bool.DOWN
  );
  public final static BooleanDataPointType OPEN_CLOSE = new BooleanDataPointType(
      1, 9,
      Bool.OPEN, Bool.CLOSE
  );
  public final static BooleanDataPointType START = new BooleanDataPointType(
      1, 10,
      Bool.STOP, Bool.START
  );
  public final static BooleanDataPointType STATE = new BooleanDataPointType(
      1, 11,
      Bool.INACTIVE, Bool.ACTIVE
  );
  public final static BooleanDataPointType INVERT = new BooleanDataPointType(
      1, 12,
      Bool.NOT_INVERTED, Bool.INVERTED
  );
  public final static BooleanDataPointType DIM_SEND_STYLE = new BooleanDataPointType(
      1, 13,
      Bool.START_STOP, Bool.CYCLICALLY
  );
  public final static BooleanDataPointType INPUT_SOURCE = new BooleanDataPointType(
      1, 14,
      Bool.FIXED, Bool.CALCULATED
  );


  public final static Control3BitDataPointType CONTROL_DIMMING = new Control3BitDataPointType(
      BooleanDataPointType.STEP,
      3, 7
  );
  public final static Control3BitDataPointType CONTROL_BLINDS = new Control3BitDataPointType(
      BooleanDataPointType.UP_DOWN,
      3, 8
  );
  public final static Control3BitDataPointType MODE_BOILER = new Control3BitDataPointType(
      BooleanDataPointType.INPUT_SOURCE,
      3, 9
  );

  
  public final static Unsigned8BitValue SCALING = new Unsigned8BitValue(5, 1);
  public final static Unsigned8BitValue ANGLE = new Unsigned8BitValue(5, 3);
  public final static Unsigned8BitValue RELPOS_VALVE = new Unsigned8BitValue(5, 4);
  public final static Unsigned8BitValue VALUE_1_UCOUNT = new Unsigned8BitValue(5, 10);

  public final static TwoOctetFloat VALUE_TEMP = new TwoOctetFloat(9, 1);
  public final static TwoOctetFloat VALUE_TEMPD = new TwoOctetFloat(9, 2);
  public final static TwoOctetFloat VALUE_TEMPA = new TwoOctetFloat(9, 3);
  public final static TwoOctetFloat VALUE_LUX = new TwoOctetFloat(9, 4);
  public final static TwoOctetFloat VALUE_WSP = new TwoOctetFloat(9, 5);
  public final static TwoOctetFloat VALUE_PRES = new TwoOctetFloat(9, 6);
  public final static TwoOctetFloat VALUE_TIME1 = new TwoOctetFloat(9, 10);
  public final static TwoOctetFloat VALUE_TIME2 = new TwoOctetFloat(9, 11);
  public final static TwoOctetFloat VALUE_VOLT = new TwoOctetFloat(9, 20);
  public final static TwoOctetFloat VALUE_CURR = new TwoOctetFloat(9, 21);
  

  // Scene management
  public final static Unsigned8BitValue SCENE_NUMBER = new Unsigned8BitValue(17, 1);
  public final static Unsigned8BitValue SCENE_CONTROL = new Unsigned8BitValue(18, 1);


  //public final static Float2ByteValue VALUE_TEMP = new Float2ByteValue(9, 1);

  public static DataPointType lookup(String dptID)
  {
    dptID = dptID.toUpperCase().trim();

    if (dptID.startsWith("DPT"))
    {
      dptID = dptID.substring(3, dptID.length()).trim();
    }

    StringTokenizer tokenizer = new StringTokenizer(dptID, ".");

    int main, sub;

    try
    {
      main = Integer.parseInt(tokenizer.nextToken().trim());

      if (!tokenizer.hasMoreElements())
      {
        throw new NoSuchCommandException("Unable to parse DPT : '" + dptID + "'.");
      }

      sub = Integer.parseInt(tokenizer.nextToken().trim());

      return lookup.get(getDPTID(main, sub));
    }
    catch (NumberFormatException exception)
    {
      throw new NoSuchCommandException(
          "Cannot parse datapoint type '" + dptID + "', " + exception.getMessage(), exception
      );
    }
  }


  static String getDPTID(int main, int sub)
  {
    StringBuffer buffer = new StringBuffer(8);

    buffer.append(main)
          .append(".")
          .append("0");

    if (sub > 9)
    {
      buffer.append(sub);
    }

    else
    {
      buffer.append("0");
      buffer.append(sub);
    }

    return buffer.toString();
  }



  // Private Instance Fields ----------------------------------------------------------------------

  private int mainNumber;
  private int subNumber;
  private boolean is6BitDPT;


  // Constructors ---------------------------------------------------------------------------------

  DataPointType(int main, int sub, boolean is6Bit)
  {
    this.mainNumber = main;
    this.subNumber = sub;
    this.is6BitDPT = is6Bit;

    String dptID = getDPTID();

    lookup.put(dptID, this);

  }

  public boolean isSixBit()
  {
    return is6BitDPT;
  }

  public String getDPTID()
  {
    return DataPointType.getDPTID(getMainNumber(), getSubNumber());
  }

  int getMainNumber()
  {
    return mainNumber;
  }

  int getSubNumber()
  {
    return subNumber;
  }


  @Override public String toString()
  {
    // TODO

    return getDPTID();
  }


  // Nested Classes -------------------------------------------------------------------------------

  public static class BooleanDataPointType extends DataPointType
  {


    private Bool zeroEncoding;
    private Bool oneEncoding;

    private BooleanDataPointType(int main, int sub, Bool zero, Bool one)
    {
      super(main, sub, true);

      this.zeroEncoding = zero;
      this.oneEncoding = one;
    }

  }


  public static class Control1BitDataPointType extends DataPointType
  {
//    SWITCH_CONTROL, BOOL_CONTROL, ENABLE_CONTROL, RAMP_CONTROL, ALARM_CONTROL,
//    BINARY_VALUE_CONTROL, STEP_CONTROL, DIRECTION1_CONTROL, DIRECTION2_CONTROL,
//    START_CONTROL, STATE_CONTROL, INVERT_CONTROL

    private Control1BitDataPointType(int main, int sub)
    {
      super(main, sub, true);
    }
  }


  public static class Control3BitDataPointType extends DataPointType
  {

    private Control3BitDataPointType(BooleanDataPointType controlBit, int main, int sub)
    {
      super(main, sub, true);
    }
  }

  public static class Unsigned8BitValue extends DataPointType
  {

    private Unsigned8BitValue(int main, int sub)
    {
      super(main, sub, false);
    }
  }

  public static class TwoOctetFloat extends DataPointType
  {

    private TwoOctetFloat(int main, int sub)
    {
      super(main, sub, false);
    }
  }

  public static class Float2ByteValue extends DataPointType
  {
// VALUE_TEMP
     
    private Float2ByteValue(int main, int sub)
    {
      super(main, sub, false);
    }
  }

}
