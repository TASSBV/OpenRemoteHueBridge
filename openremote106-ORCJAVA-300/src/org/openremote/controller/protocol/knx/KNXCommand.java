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

import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandParameter;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.protocol.knx.datatype.DataPointType;
import org.openremote.controller.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is an abstract superclass for KNX protocol read/write commands.
 *
 * @see GroupValueWrite
 * @see GroupValueRead
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
abstract class KNXCommand implements Command
{

  /*
   * IMPLEMENTATION NOTE:
   *
   *  - This class should be immutable
   */


  // Constants ------------------------------------------------------------------------------------

  /**
   * Byte array offset in Common EMI frame for message code field.
   */
  final static int CEMI_MESSAGECODE_OFFSET      = 0;

  /**
   * Byte array offset in Common EMI frame for additional info length field.
   */
  final static int CEMI_ADDITIONALINFO_OFFSET   = 1;

  /**
   * Byte array offset in Common EMI frame for control field 1.
   *
   * If additional info is included, this offset must be adjusted with addtional info length.
   */
  final static int CEMI_CONTROL1_OFFSET         = 2;

  /**
   * Byte array offset in Common EMI frame for control field 2.
   *
   * If additional info is included, this offset must be adjusted with addtional info length.
   */
  final static int CEMI_CONTROL2_OFFSET         = 3;

  /**
   * Byte array offset in Common EMI frame for source address high byte.
   *
   * If additional info is included, this offset must be adjusted with addtional info length.
   */
  final static int CEMI_SOURCEADDR_HIGH_OFFSET  = 4;

  /**
   * Byte array offset in Common EMI frame for source address low byte.
   *
   * If additional info is included, this offset must be adjusted with addtional info length.
   */
  final static int CEMI_SOURCEADDR_LOW_OFFSET   = 5;

  /**
   * Byte array offset in Common EMI frame for destination address high byte.
   *
   * If additional info is included, this offset must be adjusted with addtional info length.
   */
  final static int CEMI_DESTADDR_HIGH_OFFSET    = 6;

  /**
   * Byte array offset in Common EMI frame for destination address low byte.
   *
   * If additional info is included, this offset must be adjusted with addtional info length.
   */
  final static int CEMI_DESTADDR_LOW_OFFSET     = 7;

  /**
   * Byte array offset in Common EMI frame for data payload length.
   *
   * If additional info is included, this offset must be adjusted with addtional info length.
   */
  final static int CEMI_DATALEN_OFFSET          = 8;

  /**
   * Byte array offset in Common EMI frame for TPCI/APCI bits
   *
   * If additional info is included, this offset must be adjusted with addtional info length.
   */
  final static int CEMI_TPCI_APCI_OFFSET        = 9;

  /**
   * Byte array offset in Common EMI frame for APCI bits and 6-bit data payload.
   *
   * If additional info is included, this offset must be adjusted with addtional info length.
   */
  final static int CEMI_APCI_DATA_OFFSET        = 10;

  /**
   * Offset of first data byte in CEMI frame containing larger than 6-bit data payload.
   *
   * If additional info is included, this offset must be adjusted with addtional info length.
   */
  final static int CEMI_DATA1_OFFSET = 11;

  /**
   * Offset of second data byte in CEMI frame containing larger than 6-bit data payload.
   *
   * If additional info is included, this offset must be adjusted with addtional info length.
   */
  final static int CEMI_DATA2_OFFSET = 12;

  /**
   * Offset of third data byte in CEMI frame containing larger than 6-bit data payload.
   *
   * If additional info is included, this offset must be adjusted with addtional info length.
   */
  final static int CEMI_DATA3_OFFSET = 13;

  /**
   * Offset of fourth data byte in CEMI frame containing larger than 6-bit data payload.
   *
   * If additional info is included, this offset must be adjusted with addtional info length.
   */
  final static int CEMI_DATA4_OFFSET = 14;



  // Class Members --------------------------------------------------------------------------------

  /**
   * KNX logger. Uses a common category for all KNX related logging.
   */
  private final static Logger log = Logger.getLogger(KNXCommandBuilder.KNX_LOG_CATEGORY);


  /**
   * Factory method for creating KNX command instances {@link GroupValueWrite} and
   * {@link GroupValueRead} based on a human-readable configuration strings.  <p>
   *
   * Each KNX command instance is associated with a link to a connection manager and a
   * destination group address.
   *
   * @param name      Command lookup name. This is usually a human-readable string used in
   *                  configuration and tools. Note that multiple lookup names can be used to
   *                  return Java equal() (but not same instance) commands.
   * @param dpt       KNX datapoint type associated with this command
   * @param mgr       KNX connection manager used to transmit this command
   * @param address   KNX destination group address.
   * @param parameter parameter for this command or <tt>null</tt> if not available
   *
   * @throws NoSuchCommandException if command cannot be created by its lookup name
   *
   * @return  new KNX command instance
   */
  static KNXCommand createCommand(String name, DataPointType dpt, KNXIpConnectionManager mgr,
                                  GroupAddress address, CommandParameter parameter)
  {
    name = name.trim().toUpperCase();
    
    IpInterfaceMonitor monitorCmd = IpInterfaceMonitor.createCommand(name, mgr, address, dpt);
    if(monitorCmd != null)
       return monitorCmd;

    GroupValueWrite writeCmd = GroupValueWrite.createCommand(name, dpt, mgr, address, parameter);

    if (writeCmd != null)
      return writeCmd;

    GroupValueRead readCmd = GroupValueRead.createCommand(name, mgr, address, dpt);

    if (readCmd != null)
      return readCmd;

    throw new NoSuchCommandException("Unknown command '" + name + "'.");
  }


  
  // Private Instance Fields ----------------------------------------------------------------------


  /**
   * Command payload (APDU).
   */
  private ApplicationProtocolDataUnit apdu;

  /**
   * Command datapoint type (DPT)
   */
  private DataPointType dpt;

  /**
   * Destination address for this command.
   */
  private GroupAddress address;

  /**
   * Connection manager to be used to transmit this command.
   */
  private KNXIpConnectionManager connectionManager;



  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a KNX command with a given connection manager, destination group address and
   * an APDU payload.
   *
   * @param connectionManager KNX connection manager instance used for transmitting this commnad
   * @param address           destination group address
   * @param apdu              command payload
   * @param dpt               KNX datapoint type associated with this command
   */
  KNXCommand(KNXIpConnectionManager connectionManager, GroupAddress address,
             ApplicationProtocolDataUnit apdu, DataPointType dpt)
  {
    this.address = address;
    this.connectionManager = connectionManager;
    this.apdu = apdu;
    this.dpt = dpt;
  }



  // Object Overrides -----------------------------------------------------------------------------

  /**
   * Returns a string representation of this command. Expected output is:
   *
   * <pre>{@code
   *
   * [FRAME] <CEMI Message Code> <source address> -> <dest. address> Data: <unsigned hex bytes>
   *
   * }</pre>
   *
   * @return  this command as string
   */
  @Override public String toString()
  {

    if (apdu == null)
    {
      return "[FRAME] null";
    }
    
    // TODO : need to adjust for CEMI frames that come with additional info fields...

    Byte[] frame = getCEMIFrame();

    StringBuffer buffer = new StringBuffer(2048);

    String msgCode = DataLink.findServicePrimitiveByMessageCode(frame[CEMI_MESSAGECODE_OFFSET]);

    String sourceAddr = IndividualAddress.formatToAreaLineDevice(
        new byte[] { frame[CEMI_SOURCEADDR_HIGH_OFFSET], frame[CEMI_SOURCEADDR_LOW_OFFSET] }
    );

    String destAddr = GroupAddress.formatToMainMiddleSub(
        new byte[] { frame[CEMI_DESTADDR_HIGH_OFFSET], frame[CEMI_DESTADDR_LOW_OFFSET] }
    );

    String data = apdu.dataAsString();

    buffer
        .append("[FRAME] ")
        .append(msgCode)
        .append(" ")
        .append(sourceAddr)
        .append(" -> ")
        .append(destAddr)
        .append(" Data: ")
        .append(data);

    return buffer.toString();
  }


  // Package-Private Instance Methods -------------------------------------------------------------

  /**
   * Relay a write command to an open KNX/IP connection.
   *
   * @param command   KNX write command
   */
  void write(GroupValueWrite command)
  {
    KNXConnection connection = connectionManager.getCurrentConnection();

    if (connection == null)
    {
      log.info("No KNX connection available, did not send " + command);
    }

    else
    {
      connection.send(command);
    }
  }


  /**
   * Relay a read command to an open KNX/IP connection.
   *
   * TODO : call semantics on return value 
   *
   * @param command   KNX read command
   *
   * @return  Returns the application protocol data unit (APDU) for a Group Value Read Response
   *          frame. This frame contains the response value from the device. <P>
   *
   *          NOTE: may return <code>null</code> in case there's a connection exception or the
   *          read response is not available from the device yet.
   */
  ApplicationProtocolDataUnit read(GroupValueRead command)
  {
    KNXConnection connection = connectionManager.getCurrentConnection();

    if (connection == null)
    {
       log.info("KNX connection not available.");

       return null;
    }

    else
    {
      return connection.read(command);
    }
  }
  

  /**
   * The KNX Common External Message Interface (a.k.a cEMI) frame has a variable length
   * and structure depending on the Common EMI frame message code (first byte) and additional
   * info length (second byte).   <p>
   *
   * In a very generic fashion, a Common EMI frame can be defined as follows
   * (KNX 1.1 Application Note 033 - Common EMI Specification, 2.4 Basic Message Structure,
   * page 8):
   *
   * <pre>{@code
   *
   * +----+----+---- ... ----+-------- ... --------+
   * | MC | AI |  Add. Info  |   Service Info      |
   * +----+----+---- ... ----+-------- ... --------+
   *
   * MC = Message Code
   * AI = Additional Info Length (0x00 if no additional info is included)
   *
   * }</pre>
   *
   * KNX communication stack defines a frame transfer service (known as L_Data Service) in the
   * data link layer (KNX 1.1 -- Volume 3 System Specification, Part 2 Communication,
   * Chapter 2 Data Link Layer General, section 2.1 L_Data Service, page 8).  <p>
   *
   * Link layer data services are available in "normal" mode (vs. bus monitor mode). A data
   * request (known as L_Data.req primitive) is used to transmit a frame. The corresponding
   * Common EMI frame for L_Data.req is defined as shown below (KNX 1.1 Application Note 033,
   * section 2.5.33 L_Data.req, page 13). Example assumes a standard (non-extended) frame with
   * no additional info fields set in the frame. The application protocol data unit (APDU) is
   * for a short data (<= 6 bits) group value write request (A_GroupValue_Write.req)
   *
   * <pre>{@code
   *
   * +--------+--------+--------+--------+----------------+----------------+--------+----------------+
   * |  Msg   |Add.Info| Ctrl 1 | Ctrl 2 | Source Address | Dest. Address  |  Data  |      APDU      |
   * | Code   | Length |        |        |                |                | Length |                |
   * +--------+--------+--------+--------+----------------+----------------+--------+----------------+
   *  1 byte   1 byte   1 byte   1 byte      2 bytes          2 bytes       1 byte      2 bytes
   *
   * Message Code    = 0x11 - a L_Data.req primitive
   * Add.Info Length = 0x00 - no additional info
   * Control Field 1 = see the bit structure below
   * Control Field 2 = see the bit structure below
   * Source Address  = 0x0000 - filled in by router/gateway with its source address which is
   *                   part of the KNX subnet
   * Dest. Address   = KNX group or individual address (2 byte)
   * Data Length     = Number of bytes of data in the APDU excluding the TPCI/APCI bits
   * APDU            = Application Protocol Data Unit - the actual payload including transport
   *                   protocol control information (TPCI), application protocol control
   *                   information (APCI) and data passed as an argument from higher layers of
   *                   the KNX communication stack
   *
   * }</pre>
   *
   * Common External Message Interface Control Fields [KNX 1.1 Application Note 033]  <p>
   *
   * Common External Message Interface (EMI) defines two control fields in its frame format
   * (one byte each). The bit structure of each control field is defined in the KNX 1.1
   * Application Note 033: Common EMI Specification, section 2.4 Basic Message Structure:
   *
   *
   * <pre>{@code
   *
   *        Control Field 1
   *
   *    Bit  |
   *   ------+---------------------------------------------------------------
   *     7   | Frame Type  - 0x0 for extended frame
   *         |               0x1 for standard frame
   *   ------+---------------------------------------------------------------
   *     6   | Reserved
   *         |
   *   ------+---------------------------------------------------------------
   *     5   | Repeat Flag - 0x0 repeat frame on medium in case of an error
   *         |               0x1 do not repeat
   *   ------+---------------------------------------------------------------
   *     4   | System Broadcast - 0x0 system broadcast
   *         |                    0x1 broadcast
   *   ------+---------------------------------------------------------------
   *     3   | Priority    - 0x0 system
   *         |               0x1 normal
   *   ------+               0x2 urgent
   *     2   |               0x3 low
   *         |
   *   ------+---------------------------------------------------------------
   *     1   | Acknowledge Request - 0x0 no ACK requested
   *         | (L_Data.req)          0x1 ACK requested
   *   ------+---------------------------------------------------------------
   *     0   | Confirm      - 0x0 no error
   *         | (L_Data.con) - 0x1 error
   *   ------+---------------------------------------------------------------
   *
   *       //   Control Field 2
   *
   *    Bit  |
   *   ------+---------------------------------------------------------------
   *     7   | Destination Address Type - 0x0 individual address
   *         |                          - 0x1 group address
   *   ------+---------------------------------------------------------------
   *    6-4  | Hop Count (0-7)
   *   ------+---------------------------------------------------------------
   *    3-0  | Extended Frame Format - 0x0 for standard frame
   *   ------+---------------------------------------------------------------
   *
   * }</pre>
   *
   * @return returns a Common EMI frame representing this command as a byte array
   */
  Byte[] getCEMIFrame()
  {
    final int NO_ADDITIONAL_INFORMATION = 0x00;       // Additional info length = 0
    final int SOURCE_ADDRESS_HIBYTE     = 0x00;       // Source address will be filled in by
    final int SOURCE_ADDRESS_LOBYTE     = 0x00;       // KNX gateway/router

    //   Control Field 1

    /* A bit for standard common EMI frame type (not extended) in the first control field. */
    final int STANDARD_FRAME_TYPE = 0x01 << 7;

    /* Use frame repeat in the first control field. */
    final int REPEAT_FRAME = 0x00;

    /* Use system broadcast in the first control field. */
    final int SYSTEM_BROADCAST = 0x00;

    /* Bits for normal frame priority (%01) in the first control field of the common EMI frame. */
    final int NORMAL_PRIORITY = 0x01 << 2;

    /* Bit for requesting an ACK (L_Data.req only) for the frame in the first control field. */
    /*
     * 2011-04-14 OG : We force this bit to 0 (ACK not requested). If set to 1, the KNX/IP interface from Jung (IPS
     * 100 REG) and the Hager as well don't transmit telegrams to the KNX bus, for some unknown reason. Not requesting
     * the ACK is not a big deal as the GroupValue_Write.con telegram sent by the the server acts as an applicative
     * ACK.
     */
    final int REQUEST_ACK = 0x00 << 1;


    //   Control Field 2

    /* Destination Address Type bit for group address in the second control field of the common
     * EMI frame - most significant bit of the byte. */
    final int GROUP_ADDRESS = 0x01 << 7;

    /* Hop count. Default to six. Bits 4 to 6 in the second control field of the cEMI frame. */
    final int HOP_COUNT =  0x06 << 4;

    /* Non-extended frame format in the second control field of the common EMI frame
     *(four zero bits) */
    final int NON_EXTENDED_FRAME_FORMAT = 0x0;


    byte[] destinationAddress = address.asByteArray();
    Byte[] protocolDataUnit = apdu.getProtocolDataUnit();
    int apduDataLength = apdu.getDataLength();

    List<Byte> cemi = new ArrayList<Byte>(11);

    cemi.add(DataLink.Service.DATA.getRequestMessageCode());  // Message Code
    cemi.add((byte)NO_ADDITIONAL_INFORMATION);                // Additional Info Length

    cemi.add(                                                 // Control Field 1
        (byte)(STANDARD_FRAME_TYPE +
              REPEAT_FRAME +
              SYSTEM_BROADCAST +
              NORMAL_PRIORITY +
              REQUEST_ACK)
    );

    cemi.add(                                                 // Control Field 2
        (byte)(GROUP_ADDRESS +
               HOP_COUNT +
               NON_EXTENDED_FRAME_FORMAT)
    );

    cemi.add((byte)SOURCE_ADDRESS_HIBYTE);                    // Source address
    cemi.add((byte)SOURCE_ADDRESS_LOBYTE);

    cemi.add(destinationAddress[0]);                          // Destination address
    cemi.add(destinationAddress[1]);

    cemi.add((byte)apduDataLength);                           // Data Length
    cemi.add(protocolDataUnit[0]);                            // TPCI + APCI high bits
    cemi.add(protocolDataUnit[1]);                            // APCI low bits + data

    if (protocolDataUnit.length > 2)
    {
      // Sanity check...

      if (apduDataLength != protocolDataUnit.length - 1)
      {
        throw new Error(
            "APDU reported data length does not match the actual data length : " +
            apduDataLength + " != " + (protocolDataUnit.length - 1) + "(" + this.apdu + ")."
        );
      }

      for (int pduIndex = 2; pduIndex < protocolDataUnit.length; ++pduIndex)
      {
        cemi.add(protocolDataUnit[pduIndex]);
      }
    }

    Byte[] cemiBytes = new Byte[cemi.size()];

    return cemi.toArray(cemiBytes);
  }

  /**
   * Returns a KNX destination group address associated with this command.
   * 
   * @return  destination group address of this command
   */
  GroupAddress getAddress()
  {
    return address;
  }

  /**
   * Returns the application protocol data unit (APDU) associated with this command.
   * 
   * @return  KNX APDU payload of this command
   */
  ApplicationProtocolDataUnit getAPDU()
  {
    return apdu;
  }

  /**
   * Returns the KNX datapoint type associated with this command.
   *
   * @see DataPointType
   *
   * @return datapoint type of this command
   */
  DataPointType getDataPointType()
  {
    return dpt;
  }
}
