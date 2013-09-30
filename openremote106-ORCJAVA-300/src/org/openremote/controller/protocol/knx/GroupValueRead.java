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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.math.RoundingMode;


import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.protocol.knx.datatype.Bool;
import org.openremote.controller.protocol.knx.datatype.DataPointType;
import org.openremote.controller.protocol.knx.datatype.DataType;
import org.openremote.controller.protocol.knx.datatype.Unsigned8Bit;
import org.openremote.controller.protocol.knx.datatype.Float2Byte;
import org.openremote.controller.protocol.knx.datatype.TwoOctetFloat;
import org.openremote.controller.utils.Logger;


/**
 * Read command representing KNX Group Value Read service. This class implements the
 * {@link StatusCommand} interface and therefore acts as an entry point in controller/protocol SPI.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
class GroupValueRead extends KNXCommand implements StatusCommand
{


  // Class Members --------------------------------------------------------------------------------

  /**
   * KNX logger. Uses a common category for all KNX related logging.
   */
  private final static Logger log = Logger.getLogger(KNXCommandBuilder.KNX_LOG_CATEGORY);

  /**
   * Lookup map from user defined command strings in the designer (from which they end up
   * into controller.xml) to type safe APDUs for KNX CEMI frames.
   */
  private final static Map<String, ApplicationProtocolDataUnit> booleanCommandLookup =
      new ConcurrentHashMap<String, ApplicationProtocolDataUnit>();

  /*
   * IMPLEMENTATION NOTE:
   *
   *   if new valid values for command names are added, the unit tests should be added
   *   accordingly into KNXCommandBuilderTest
   */
  static
  {
    booleanCommandLookup.put("STATUS", ApplicationProtocolDataUnit.READ_SWITCH_STATE);
  }


  /**
   * Factory method for creating new KNX read command instances based on user configured
   * command name. The command name must be one of the pre-specified command names in this class.
   *
   * @param name      User-configured command name used in tools and configuration files. This
   *                  name is mapped to a typed KNX Application Protocol Data Unit instance.
   * @param mgr       Connection manager reference this command will use for transmission.
   * @param address   Destination group address for this command.
   * @param dpt       KNX datapoint type associated with this command
   *
   * @return          a new KNX read command instance, or <code>null</code> if the lookup name
   *                  could not be matched to any command
   */
  static GroupValueRead createCommand(String name, KNXIpConnectionManager mgr,
                                      GroupAddress address, DataPointType dpt)
  {
    name = name.trim().toUpperCase();

    ApplicationProtocolDataUnit apdu = booleanCommandLookup.get(name);

    if (apdu == null)
      return null;
    
    return new GroupValueRead(mgr, address, apdu, dpt);
  }
  


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new KNXReadCommand instance with a given connection manager, group address
   * and protocol data unit.
   *
   * @param connectionManager   connection manager used to send this KNX command
   * @param groupAddress        destination group address for this command
   * @param apdu                APDU payload for this command
   * @param dpt                 KNX datapoint type associated with this command
   */
  private GroupValueRead(KNXIpConnectionManager connectionManager, GroupAddress groupAddress,
                         ApplicationProtocolDataUnit apdu, DataPointType dpt)
  {
    super(connectionManager, groupAddress, apdu, dpt);
  }


  // Implements StatusCommand ---------------------------------------------------------------------

  /**
   * TODO
   *
   * @param sensorType
   *
   * @param statusMap
   * @return
   */
  public String read(EnumSensorType sensorType, Map<String, String> statusMap)
  {

    log.debug("Polling device status for " + this);

    ApplicationProtocolDataUnit responseAPDU = super.read(this);

    if (responseAPDU == null)
    {
        return "";      // TODO : check how caller handles invalid return values
    }
	
    // Get the DataPointType from this object instead of from the APDU associated with 
    // the KNX command name.This will be the right type (that entered by the user as dpt in
    // the GUI.
    DataPointType dpt = getDataPointType();
    DataType datatype = getAPDU().getDataType();

    if (sensorType == EnumSensorType.SWITCH)
    {
      if (dpt == DataPointType.BooleanDataPointType.SWITCH)
      {
          Bool bool = (Bool)responseAPDU.getDataType();

          if (bool == Bool.ON)
          {
            return "on";
          }
          else
          {
            return "off";
          }
      }

      else
      {
        log.warn("Only support SWITCH sensor mapping to KNX Switch DPT (1.001)");

        return "";    // TODO : check how caller handles invalid return types
      }
    }

    else if (sensorType == EnumSensorType.LEVEL)
    {
      if (dpt == DataPointType.Unsigned8BitValue.SCALING)
      {
        Unsigned8Bit valueDPT = (Unsigned8Bit)responseAPDU.getDataType();

        int resolution = valueDPT.resolve();

        return Integer.toString(resolution);
      }

      else if (dpt == DataPointType.Unsigned8BitValue.ANGLE)
      {
        Unsigned8Bit valueDPT = (Unsigned8Bit)responseAPDU.getDataType();

        return Integer.toString((int)(valueDPT.resolve() / 3.6));
      }

      else if (dpt == DataPointType.Unsigned8BitValue.RELPOS_VALVE)
      {
        Unsigned8Bit valueDPT = (Unsigned8Bit)responseAPDU.getDataType();

        return Integer.toString((int)(valueDPT.resolve() / 2.55));
      }

      else if (dpt == DataPointType.Unsigned8BitValue.VALUE_1_UCOUNT)
      {
        Unsigned8Bit valueDPT = (Unsigned8Bit)responseAPDU.getDataType();

        return Integer.toString((int)(valueDPT.resolve() / 2.55));
      }

      else if (dpt == DataPointType.Unsigned8BitValue.SCENE_NUMBER)
      {
        Unsigned8Bit valueDPT = (Unsigned8Bit)responseAPDU.getDataType();

        return Integer.toString((int)(valueDPT.resolve()));
      }
      
      else
      {
        throw new Error("Unrecognized datatype for LEVEL sensor: " + dpt);
      }
    }

    else if (sensorType == EnumSensorType.RANGE)
    {
      // TODO :
      //    need to merge the fixes that gives range min/max values so return values
      //    can be scaled accordingly

      if (dpt instanceof DataPointType.Unsigned8BitValue)
      {
        Unsigned8Bit valueDPT = (Unsigned8Bit)responseAPDU.getDataType();

        return Integer.toString(valueDPT.resolve());
      }

      else if (dpt instanceof DataPointType.TwoOctetFloat)
      {
        TwoOctetFloat valueDPT = (TwoOctetFloat)responseAPDU.getDataType();

        return Integer.toString(valueDPT.resolve().intValue());
      }

      else if (dpt instanceof DataPointType.Float2ByteValue)
      {
        Float2Byte valueDPT = (Float2Byte)responseAPDU.getDataType();

        int resolution = (int)valueDPT.resolve();

        return Integer.toString(resolution);
      }

      else
      {
        throw new Error("Currently only Unsigned 8 bit datatype supported for RANGE sensor type.");
      }
    }

    else if (sensorType == EnumSensorType.CUSTOM)
    {

//      if (dpt == DataPointType.Float2ByteValue.VALUE_TEMP)
//      {
//        Float2Byte valueDPT = (Float2Byte)responseAPDU.getDataType();
//
//        float resolution = valueDPT.resolve();
//        return Float.toString(resolution);
//      }

      if (dpt instanceof DataPointType.TwoOctetFloat)
      {
        TwoOctetFloat valueDPT = (TwoOctetFloat)responseAPDU.getDataType();

        if (statusMap.containsKey("precision"))
        {
          String precision = statusMap.get("precision");

          if (precision.equals("1") || precision.equals("0.1"))
          {
            return valueDPT.resolve().setScale(1, RoundingMode.HALF_UP).toString();
          }

          else if (precision.equals("2") || precision.equals("0.01"))
          {
            return valueDPT.resolve().setScale(2, RoundingMode.HALF_UP).toString();
          }
        }

        return valueDPT.resolve().setScale(1, RoundingMode.HALF_UP).toString();
      }

      else
      {
        throw new Error("Unrecognized datapoint type " + dpt + " on CUSTOM sensor.");
      }
    }

    else
    {
      throw new Error("Unrecognized sensor type " + sensorType);

    }
  }
}
