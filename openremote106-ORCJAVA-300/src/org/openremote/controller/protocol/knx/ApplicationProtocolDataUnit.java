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


import org.openremote.controller.utils.Strings;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.protocol.knx.datatype.DataPointType;
import org.openremote.controller.protocol.knx.datatype.DataType;
import org.openremote.controller.protocol.knx.datatype.Bool;
import org.openremote.controller.protocol.knx.datatype.Controlled3Bit;
import org.openremote.controller.protocol.knx.datatype.Unsigned8Bit;
import org.openremote.controller.protocol.knx.datatype.Float2Byte;
import org.openremote.controller.protocol.knx.datatype.TwoOctetFloat;
import org.openremote.controller.exception.ConversionException;
import org.openremote.controller.command.CommandParameter;

import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;
import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * This class represents application protocol data unit (APDU) in KNX specification. <p>
 *
 * APDU is defined in KNX 1.1 Volume 3: System Specifications, Part 3 Communication,
 * Chapter 7, Application Layer. <p>
 *
 * In the Common EMI frame, the APDU payload is defined as follows:
 *
 * <pre>{@code
 *
 *  +--------+--------+--------+--------+--------+
 *  | TPCI + | APCI + |  Data  |  Data  |  Data  |
 *  |  APCI  |  Data  |        |        |        |
 *  +--------+--------+--------+--------+--------+
 *    byte 1   byte 2  byte 3     ...     byte 16
 *
 *}</pre>
 *
 * For data that is 6 bits or less in length, only the first two bytes are used in a Common EMI
 * frame. Common EMI frame also carries the information of the expected length of the Protocol
 * Data Unit (PDU). Data payload can be at most 14 bytes long.  <p>
 *
 * The first byte is a combination of transport layer control information (TPCI) and application
 * layer control information (APCI). First 6 bits are dedicated for TPCI while the two least
 * significant bits of first byte hold the two most significant bits of APCI field, as follows:
 *
 * <pre>{@code
 *
 *    Bit 1    Bit 2    Bit 3    Bit 4    Bit 5    Bit 6    Bit 7    Bit 8      Bit 1   Bit 2
 *  +--------+--------+--------+--------+--------+--------+--------+--------++--------+----....
 *  |        |        |        |        |        |        |        |        ||        |
 *  |  TPCI  |  TPCI  |  TPCI  |  TPCI  |  TPCI  |  TPCI  | APCI   |  APCI  ||  APCI  |
 *  |        |        |        |        |        |        |(bit 1) |(bit 2) ||(bit 3) |
 *  +--------+--------+--------+--------+--------+--------+--------+--------++--------+----....
 *  +                            B  Y  T  E    1                            ||       B Y T E  2
 *  +-----------------------------------------------------------------------++-------------....
 *
 * }</pre>
 *
 * Total number of APCI control bits can be either 4 or 10, depending on which
 * {@link org.openremote.controller.protocol.knx.ApplicationLayer.Service
 * application layer service} is being used. The second byte bit structure is as follows:
 *
 * <pre>{@code
 *
 *    Bit 1    Bit 2    Bit 3    Bit 4    Bit 5    Bit 6    Bit 7    Bit 8      Bit 1   Bit 2
 *  +--------+--------+--------+--------+--------+--------+--------+--------++--------+----....
 *  |        |        |        |        |        |        |        |        ||        |
 *  |  APCI  |  APCI  | APCI/  |  APCI/ |  APCI/ |  APCI/ | APCI/  |  APCI/ ||  Data  |  Data
 *  |(bit 3) |(bit 4) | Data   |  Data  |  Data  |  Data  | Data   |  Data  ||        |
 *  +--------+--------+--------+--------+--------+--------+--------+--------++--------+----....
 *  +                            B  Y  T  E    2                            ||       B Y T E  3
 *  +-----------------------------------------------------------------------++-------------....
 *
 * }</pre>
 *
 * @see DataType
 * 
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
class ApplicationProtocolDataUnit
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * Represents the full APDU (APCI + data) for Group Value Write service request with DPT 1.001
   * (Switch) to state 'ON'.
   *
   * @see org.openremote.controller.protocol.knx.ApplicationLayer.Service#GROUPVALUE_WRITE_6BIT
   * @see org.openremote.controller.protocol.knx.datatype.Bool#ON
   */
  final static ApplicationProtocolDataUnit WRITE_SWITCH_ON = new ApplicationProtocolDataUnit
  (
      ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT,
      Bool.ON
  );

  /**
   * Represents the full APDU (APCI + data) for Group Value Write service request with
   * DPT 1.001 (Switch) to state 'OFF'.
   *
   * @see ApplicationLayer.Service#GROUPVALUE_WRITE_6BIT
   * @see org.openremote.controller.protocol.knx.datatype.Bool#OFF
   */
  final static ApplicationProtocolDataUnit WRITE_SWITCH_OFF = new ApplicationProtocolDataUnit
  (
      ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT,
      Bool.OFF
  );


  /**
   * Represents the full APDU (APCI bits) for Group Value Read request for data points with
   * DPT 1.001 (Switch) type.
   *
   * @see ApplicationLayer.Service#GROUPVALUE_READ
   * @see org.openremote.controller.protocol.knx.datatype.Bool#READ_SWITCH
   */
  final static ApplicationProtocolDataUnit READ_SWITCH_STATE = new ApplicationProtocolDataUnit
  (
      ApplicationLayer.Service.GROUPVALUE_READ,
      Bool.READ_SWITCH
  );



  // Class Members --------------------------------------------------------------------------------

  /**
   * KNX logger. Uses a common category for all KNX related logging.
   */
  private final static Logger log = Logger.getLogger(KNXCommandBuilder.KNX_LOG_CATEGORY);


  /**
   * Determines from the Application Protocol Control Information (APCI) bits in an application
   * protocol data unit (APDU) bytes whether the application level service corresponds to Group
   * Value Response service.
   *
   * @see org.openremote.controller.protocol.knx.ApplicationLayer.Service#GROUPVALUE_RESPONSE_6BIT
   *
   * @param apdu  Byte array containing the application protocol data unit payload. Only the first
   *              two bytes are inspected. This parameter can therefore contain only a partial
   *              APDU with only the first two bytes of APCI information or the entire APDU with
   *              data included.
   *
   * @return      true if the APDU corresponds to Group Value Response service; false otherwise
   */
  static boolean isGroupValueResponse(byte[] apdu)
  {
    // Expected bit structure :
    //
    //  Byte 1 : bits xxxxxx00          - first six bits are part of TPCI
    //  Byte 2 : bits 01xxxxxx          - last six bits are either data (6 bit return values)
    //                                    or zero for larger than 6 bit return values
    return ((apdu[0] & 0x3) == 0x00 && (apdu[1] & 0xC0) == 0x40);
  }


  /**
   * TODO
   *
   * @param apdu
   * @return
   */
  static boolean isGroupValueWriteReq(byte[] apdu)
  {
    return ((apdu[0] & 0x3) == 0x00 && (apdu[1] & 0xC0) == 0x80);
  }

  
  /**
   * Constructs an APDU corresponding to a Group Value Write service for a device expecting
   * a 3-bit dim control data point type (DPT 3.007). <p>
   *
   * The control bit value must correspond to DPT 1.007, 1.008 or 1.014
   * ({@link org.openremote.controller.protocol.knx.datatype.Bool#INCREASE}/
   * {@link org.openremote.controller.protocol.knx.datatype.Bool#DECREASE},
   * {@link org.openremote.controller.protocol.knx.datatype.Bool#UP}/
   * {@link org.openremote.controller.protocol.knx.datatype.Bool#DOWN},
   * {@link org.openremote.controller.protocol.knx.datatype.Bool#FIXED}/
   * {@link org.openremote.controller.protocol.knx.datatype.Bool#CALCULATED}, respectively). <p>
   *
   * The dim value must be a 3-bit value in the range of [0-7].
   *
   * @see org.openremote.controller.protocol.knx.datatype.DataPointType.Control3BitDataPointType#CONTROL_DIMMING
   * @see org.openremote.controller.protocol.knx.datatype.Controlled3Bit
   *
   * @param   controlValue  must be one of DataType.Boolean.INCREASE, DataType.Boolean.DECREASE,
   *                        DataType.Boolean.UP, DataType.Boolean.DOWN, DataType.Boolean.FIXED
   *                        or DataType.Boolean.CALCULATED
   *
   * @param   dimValue      must be in range of [0-7]
   *
   * @return  APDU instance for a 3-bit dim control Group Value Write service with a given control
   *          bit and range bits
   */
  static ApplicationProtocolDataUnit create3BitDimControl(Bool controlValue,
                                                          int dimValue)
  {
    return new ApplicationProtocolDataUnit(
        ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT,
        new Controlled3Bit(
            DataPointType.Control3BitDataPointType.CONTROL_DIMMING,
            controlValue,
            dimValue)
    );
  }


  /**
   * Constructs an APDU corresponding to a Group Value Write service for a device expecting
   * an 8-bit unsigned scaling value (DPT 5.001). <p>
   *
   * Valid parameter value range is [0-100]. This is interpreted as a percentage value.
   * 
   * @param   parameter     scaling value for percentage range
   *
   * @return  APDU instance for a 8-bit unsigned scaling value
   *
   * @throws  ConversionException   if the value is not in a given range or cannot be scaled to the
   *                                desired range
   */
  static ApplicationProtocolDataUnit createScaling(CommandParameter parameter)
      throws ConversionException
  {
    int value = parameter.getValue().intValue();

    if (value < 0 || value > 100)
    {
      throw new ConversionException(
          "Expected value is in range [0-100] (percentage), received " + value
      );
    }

    // scale it up to [0-255] range of the unsigned byte that is sent on the wire...

    value = (int)Math.round(value * 2.55);

    return new ApplicationProtocolDataUnit(
        ApplicationLayer.Service.GROUPVALUE_WRITE,
        new Unsigned8Bit(
            DataPointType.Unsigned8BitValue.SCALING,
            value)
    );
  }


  /**
  * Constructs an APDU corresponding to a Group Value Write service for a device expecting an
  * 8-bit unsigned counter value (DPT 5.010).
  * <p>
  *
  * Valid parameter value range is [0-255].
  *
  * @param parameter
  *           counter value for range
  *
  * @return APDU instance for a 8-bit unsigned counter value
  *
  * @throws ConversionException
  *            if the value is not in a given range
  */
  static ApplicationProtocolDataUnit createRange(CommandParameter parameter) 
      throws ConversionException
  {
    int value = parameter.getValue().intValue();

    if (value < 0 || value > 255)
    {
      throw new ConversionException("Expected value is in range [0-255] , received " + value);
    }

    return new ApplicationProtocolDataUnit(
        ApplicationLayer.Service.GROUPVALUE_WRITE,
        new Unsigned8Bit(
          DataPointType.Unsigned8BitValue.VALUE_1_UCOUNT,
          value)
    );
  }


  /**
   * Constructs an APDU corresponding to a Group Value Write service for a device expecting an
   * 6-bit unsigned scene number value (DPT 17.001).
   * <p>
   *
   * Valid parameter value range is [0-63].
   *
   * @param parameter
   *           scene number value
   * @param learn
   *           <code>true</code> if command to learn scene
   *
   * @return APDU instance for a 8-bit unsigned counter value
   *
   * @throws ConversionException
   *            if the value is not in a given range
   */
  static ApplicationProtocolDataUnit createSceneNumber(CommandParameter parameter, boolean learn)
     throws ConversionException
  {
    int value = parameter.getValue().intValue();

    if (value < 1 || value > 64)
    {
      throw new ConversionException("Expected value is in range [1-64] , received " + value);
    }
    value = value - 1;
    
    return new ApplicationProtocolDataUnit(
        ApplicationLayer.Service.GROUPVALUE_WRITE,
        new Unsigned8Bit(
          DataPointType.Unsigned8BitValue.VALUE_1_UCOUNT,
          learn ? 0x80 | value : value)
    );
  }

  /**
   * TODO
   *
   * @param parameter
   * @return
   */
  static ApplicationProtocolDataUnit createIntegerCelsiusTemp(CommandParameter parameter)
      throws ConversionException
  {
    BigDecimal value = parameter.getValue();

    return createCelsiusTemp(value);
  }

  /**
   * TODO
   *
   * @param parameter
   * @return
   */
  static ApplicationProtocolDataUnit createSingleDecimalCelsiusTemp(CommandParameter parameter)
      throws ConversionException
  {
    BigDecimal value = parameter.getValue().divide(new BigDecimal(10));

    return createCelsiusTemp(value);
  }

  /**
   * TODO
   *
   * @param parameter
   * @return
   */
  static ApplicationProtocolDataUnit createDoubleDecimalCelsiusTemp(CommandParameter parameter)
      throws ConversionException
  {
    BigDecimal value = parameter.getValue().divide(new BigDecimal(100));

    return createCelsiusTemp(value);
  }



  private static ApplicationProtocolDataUnit createCelsiusTemp(BigDecimal decimal)
      throws ConversionException
  {
    float value = decimal.setScale(2, RoundingMode.HALF_UP).floatValue();
    
    if (value < -273.00 || value > 670760.00)
    {
      DecimalFormat df = new DecimalFormat("######.##");

      throw new ConversionException(
          "Expected celsius temperature value range is [-273,00..+670760,00] -- received " +
          df.format(value)
      );
    }

    return new ApplicationProtocolDataUnit(
        ApplicationLayer.Service.GROUPVALUE_WRITE,
        new TwoOctetFloat(DataPointType.VALUE_TEMP, value)
    );
  }


  // Private Instance Fields ----------------------------------------------------------------------

  /**
   * Datatype associated with this APDU.
   *
   * @see DataType
   */
  private DataType datatype;

  /**
   * Application Layer Service associated with this APDU.
   *
   * @see ApplicationLayer
   */
  private ApplicationLayer.Service applicationLayerService;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new APDU with a given application layer service and datatype.
   *
   * @see org.openremote.controller.protocol.knx.ApplicationLayer.Service
   * @see DataType
   *
   * @param service   application layer service as defined in
   *                  {@link org.openremote.controller.protocol.knx.ApplicationLayer.Service}
   * @param datatype  KNX data type
   */
  private ApplicationProtocolDataUnit(ApplicationLayer.Service service, DataType datatype)
  {
    this.applicationLayerService  = service;
    this.datatype = datatype;
  }


  // Object Overrides -----------------------------------------------------------------------------


  /**
   * Returns a string representation of this APDU including the TPCI & APCI bits and data payload.
   *
   * @return  application layer service name associated with this APDU and the contents of this
   *          APDU as unsigned hex values.
   */
  @Override public String toString()
  {
    StringBuffer buffer = new StringBuffer(256);

    buffer.append(applicationLayerService.name());
    buffer.append(" ");

    Byte[] pdu = getProtocolDataUnit();

    for (byte b : pdu)
    {
      buffer.append(Strings.byteToUnsignedHexString(b));
      buffer.append(" ");
    }

    return buffer.toString().trim();
  }




  // Package-Private Instance Methods ------------------------------------------------------------


  /**
   * Returns the actual data payload without application protocol control information (APCI) bits
   * as a string value. The bytes in the payload are formatted as unsigned hex strings. An example
   * output could look like:
   *
   * <pre>{@code
   *
   *   0x00 0x0F
   *
   * }</pre>
   *
   * @return APDU data payload formatted as a sequence of unsigned hex strings
   */
  String dataAsString()
  {
    byte[] data = datatype.getData();

    StringBuffer buffer = new StringBuffer(256);

    for (byte b : data)
    {
      buffer.append(Strings.byteToUnsignedHexString(b));
      buffer.append(" ");
    }

    return buffer.toString().trim();
  }


  /**
   * Returns the data length of the data type associated with this APDU.
   * See {@link DataType#getDataLength()} for details.
   *
   * @see DataType#getDataLength()
   *
   * @return returns the APDU data payload length in bytes, as specified in
   *         {@link DataType#getDataLength()}
   */
  int getDataLength()
  {
    return datatype.getDataLength();
  }

  /**
   * Returns the KNX datatype associated with this APDU.
   *
   * @see DataType
   *
   * @return  KNX datatype
   */
  DataType getDataType()
  {
    return datatype;
  }

  /**
   * Returns the KNX datapoint type associated with this APDU.
   *
   * @see DataPointType
   *
   * @return  KNX datapoint type
   */
  DataPointType getDataPointType()
  {
    return datatype.getDataPointType();
  }

  /**
   * Returns the application layer service associated with this application protocol data unit.
   *
   * @see ApplicationLayer.Service
   * 
   * @return application layer service
   */
  ApplicationLayer.Service getApplicationLayerService()
  {
    return applicationLayerService;
  }


  /**
   * Attempts to convert the data payload of this APDU into a KNX Boolean datatype value.  <p>
   *
   * Integer value 0 is encoded as FALSE, integer value 1 is encoded as TRUE.
   *
   * @see org.openremote.controller.protocol.knx.datatype.Bool#TRUE
   * @see org.openremote.controller.protocol.knx.datatype.Bool#FALSE
   *
   * @return {@link org.openremote.controller.protocol.knx.datatype.Bool#TRUE} for value 1, {@link org.openremote.controller.protocol.knx.datatype.Bool#FALSE} for value 0.
   *
   * @throws ConversionException  if the data payload cannot be converted to KNX Boolean type.
   */
  Bool convertToBooleanDataType() throws ConversionException
  {
    byte[] data = datatype.getData();

    if (data.length == 1)
    {
      if (data[0] == 1)
      {
        return Bool.TRUE;
      }

      else if (data[0] == 0)
      {
        return Bool.FALSE;
      }

      else
      {
        throw new ConversionException("Expected boolean value 0x0 or 0x1 but found " + data[0]);
      }
    }

    else
    {
      throw new ConversionException(
          "Was expecting 6-bit boolean datatype in payload but data was " +
          data.length + " bytes long."
      );
    }
  }


  /**
   * Returns the application protocol data unit (APDU) including the Control Information (ACPI)
   * bits and data value. <p>
   *
   * Returned byte array is at minimum 2 bytes long (for 6-bit data values), and maximum of 16
   * bytes long (with a largest possible 14 byte data value). <p>
   *
   * The six most significant bits of the first byte in the array are Transport Protocol Control
   * Information (TCPI) bits which are all set to zero.
   *
   * @return full APDU as byte array with APCI bits and data value set
   */
  Byte[] getProtocolDataUnit()
  {
    final int TRANSPORT_LAYER_CONTROL_FIELDS = 0x00;

    byte[] apduData = datatype.getData();

    List<Byte> pdu = new ArrayList<Byte>(20);

    pdu.add((byte)(TRANSPORT_LAYER_CONTROL_FIELDS + applicationLayerService.getTPCIAPCI()));

    // TODO : fix the brittle if statement with isSixBit() method on app layer service instance

    if (applicationLayerService == ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT ||
        applicationLayerService == ApplicationLayer.Service.GROUPVALUE_RESPONSE_6BIT ||
        applicationLayerService == ApplicationLayer.Service.GROUPVALUE_READ)
    {
      pdu.add((byte)(applicationLayerService.getAPCIData() + apduData[0]));
    }

    else
    {
      pdu.add((byte)applicationLayerService.getAPCIData());

      for (byte data : apduData)
      {
        pdu.add(data);
      }
    }

    Byte[] pduBytes = new Byte[pdu.size()];
    return pdu.toArray(pduBytes);
  }



  // Nested Classes -------------------------------------------------------------------------------


  /**
   *  TODO
   */
  static class ResponseAPDU extends ApplicationProtocolDataUnit
  {
    /**
     * TODO
     *
     * @param apdu
     * @return
     */
    static ResponseAPDU create6BitResponse(final byte[] apdu)
    {
      return new ResponseAPDU(
          ApplicationLayer.Service.GROUPVALUE_RESPONSE_6BIT,
          1 /* data length */,
          new byte[] { (byte)(apdu[1] & 0x3F)}
      );
    }

    /**
     * TODO
     *
     * @param apdu
     * @return
     */
    static ResponseAPDU create8BitResponse(final byte[] apdu)
    {
      return new ResponseAPDU(
          ApplicationLayer.Service.GROUPVALUE_RESPONSE,
          2, /* Data length */
          new byte[] { apdu[2] }
      );
    }

    /**
     * TODO
     *
     * @param apdu
     * @return
     */
    static ResponseAPDU createTwoByteResponse(final byte[] apdu)
    {
      return new ResponseAPDU(
          ApplicationLayer.Service.GROUPVALUE_RESPONSE,
          3, /* Data length */
          new byte[] { apdu[2], apdu[3] }
      );
    }

    /**
     * TODO
     *
     * @param apdu
     * @return
     */
    static ResponseAPDU createThreeByteResponse(final byte[] apdu)
    {
      return new ResponseAPDU(
          ApplicationLayer.Service.GROUPVALUE_RESPONSE,
          4, /* Data length */
          new byte[] { apdu[2], apdu[3], apdu[4] }
      );
    }

    /**
     * TODO
     *
     * @param apdu
     * @return
     */
    static ResponseAPDU createFourByteResponse(final byte[] apdu)
    {
      return new ResponseAPDU(
          ApplicationLayer.Service.GROUPVALUE_RESPONSE,
          5, /* Data length */
          new byte[] { apdu[2], apdu[3], apdu[4], apdu[5] }
      );
    }

    /**
     * TODO
     *
     * @param apdu
     * @return
     */
    static ResponseAPDU createStringResponse(final byte[] apdu)
    {
      int len = apdu.length;
      byte[] stringData = new byte[len - 2];

      System.arraycopy(apdu, 2, stringData, 0, stringData.length);

      return new ResponseAPDU(
          ApplicationLayer.Service.GROUPVALUE_RESPONSE,
          stringData.length + 1, /* Data length */
          stringData
      );
    }

    /**
     * TODO
     *
     * @param service
     * @param dataLen
     * @param data
     */
    private ResponseAPDU(ApplicationLayer.Service service, final int dataLen, final byte[] data)
    {
      super(service, new DataType()
      {
        public int getDataLength()
        {
          return dataLen;
        }

        public byte[] getData()
        {
          return data;
        }

        public DataPointType getDataPointType()
        {
          throw new Error("Response APDU must be resolved to a datapoint type first.");
        }
      });
    }


    /**
     * TODO
     *
     * @param dpt
     * @return
     */
    ApplicationProtocolDataUnit resolve(DataPointType dpt)
    {
      if (dpt instanceof DataPointType.BooleanDataPointType)
      {
        DataPointType.BooleanDataPointType bool = (DataPointType.BooleanDataPointType)dpt;
        return new ApplicationProtocolDataUnit(
            getApplicationLayerService(),
            resolveToBoolean(bool, getDataType().getData() [0])
        );
      }

      else if (dpt instanceof DataPointType.Unsigned8BitValue)
      {
        DataPointType.Unsigned8BitValue value = (DataPointType.Unsigned8BitValue)dpt;

        return new ApplicationProtocolDataUnit(
            getApplicationLayerService(),
            resolveTo8BitValue(value, getDataType().getData() [0])
        );
      }

      else if (dpt instanceof DataPointType.TwoOctetFloat)
      {
        DataPointType.TwoOctetFloat value = (DataPointType.TwoOctetFloat)dpt;

        return new ApplicationProtocolDataUnit(
            getApplicationLayerService(),
            resolveToTwoOctetFloat(value, getDataType().getData())
        );
      }
      else if (dpt instanceof DataPointType.Float2ByteValue)
      {
        DataPointType.Float2ByteValue value = (DataPointType.Float2ByteValue) dpt;

        return new ApplicationProtocolDataUnit(
            getApplicationLayerService(),
            resolveToFloat2ByteValue(value, getDataType().getData()));
      }


      else
      {
        throw new Error("Unrecognized datapoint type " + dpt);
      }
    }


    /**
     * TODO
     * 
     * @param dpt
     * @param value
     * @return
     */
    private Unsigned8Bit resolveTo8BitValue(DataPointType.Unsigned8BitValue dpt, int value)
    {
      return new Unsigned8Bit(dpt, value);
    }


    /**
     * TODO
     *
     * @param dpt
     * @param value
     * @return
     */
    private Float2Byte resolveToFloat2ByteValue(DataPointType.Float2ByteValue dpt, byte[] value)
    {
      return new Float2Byte(dpt, value);
    }

    private TwoOctetFloat resolveToTwoOctetFloat(DataPointType.TwoOctetFloat dpt, byte[] value)
    {
      return new TwoOctetFloat(dpt, value);
    }

    /**
     * TODO
     *
     * @param value
     * @return
     */
    private Bool resolveToBoolean(DataPointType.BooleanDataPointType dpt, int value)
    {
      if (dpt == DataPointType.BooleanDataPointType.SWITCH)
          return (value == 0) ? Bool.OFF : Bool.ON;

      else if (dpt == DataPointType.BooleanDataPointType.BOOL)
          return (value == 0) ? Bool.FALSE : Bool.TRUE;

      else if (dpt == DataPointType.BooleanDataPointType.ENABLE)
          return (value == 0) ? Bool.DISABLE : Bool.ENABLE;

      else if (dpt == DataPointType.BooleanDataPointType.RAMP)
          return (value == 0) ? Bool.NO_RAMP : Bool.RAMP;

      else if (dpt == DataPointType.BooleanDataPointType.ALARM)
          return (value == 0) ? Bool.NO_ALARM : Bool.ALARM;

      else if (dpt == DataPointType.BooleanDataPointType.BINARY_VALUE)
          return (value == 0) ? Bool.LOW : Bool.HIGH;

      else if (dpt == DataPointType.BooleanDataPointType.STEP)
          return (value == 0) ? Bool.DECREASE : Bool.INCREASE;

      else if (dpt == DataPointType.BooleanDataPointType.UP_DOWN)
          return (value == 0) ? Bool.UP : Bool.DOWN;

      else if (dpt == DataPointType.BooleanDataPointType.OPEN_CLOSE)
          return (value == 0) ? Bool.OPEN : Bool.CLOSE;

      else if (dpt == DataPointType.BooleanDataPointType.START)
          return (value == 0) ? Bool.STOP : Bool.START;

      else if (dpt == DataPointType.BooleanDataPointType.STATE)
          return (value == 0) ? Bool.INACTIVE : Bool.ACTIVE;

      else if (dpt == DataPointType.BooleanDataPointType.INVERT)
          return (value == 0) ? Bool.NOT_INVERTED : Bool.INVERTED;

      else if (dpt == DataPointType.BooleanDataPointType.DIM_SEND_STYLE)
          return (value == 0) ? Bool.START_STOP : Bool.CYCLICALLY;

      else if (dpt == DataPointType.BooleanDataPointType.INPUT_SOURCE)
          return (value == 0) ? Bool.FIXED : Bool.CALCULATED;

      else
      {
        throw new Error("Unrecognized datapoint type : " + dpt);
      }
    }

  }


}
