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
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.protocol.knx.datatype.DataPointType;
import org.openremote.controller.protocol.knx.datatype.Unsigned8Bit;
import org.openremote.controller.utils.Strings;
import org.openremote.controller.exception.NoSuchCommandException;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.jdom.Element;

/**
 * Unit tests for KNX Range (KNX DPT 5.010 -- VALUE_1_UCOUNT) write command.
 *
 * @see DataPointType#VALUE_1_UCOUNT
 * 
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class RangeTest
{


  // Test Setup -----------------------------------------------------------------------------------

  private KNXCommandBuilder builder = null;

  @Before public void setUp()
  {
    builder = new KNXCommandBuilder(
        "127.0.0.1", 9999,
        "org.openremote.controller.protocol.bus.DatagramSocketPhysicalBus"
    );
  }



  // Tests ----------------------------------------------------------------------------------------


  /**
   * Test KNX command parsing with "RANGE" as the command string.
   *
   */
  @Test public void testRangeCommand() throws InvalidGroupAddressException
  {
    GroupAddress addr = new GroupAddress("5/5/5");

    Command cmd = getCommand("RANGE", addr, 0);

    Assert.assertTrue(cmd instanceof GroupValueWrite);

    KNXCommand knx = (KNXCommand)cmd;

    Assert.assertTrue(knx.getAddress().equals(addr));
    Assert.assertTrue(knx.getDataPointType() == DataPointType.VALUE_1_UCOUNT);
  }



  /**
   * Test KNX command parsing with "Range", "range" and "RAnge" as the command string.
   */
  @Test public void testRangeCommandMixedCase() throws InvalidGroupAddressException
  {
    GroupAddress addr1 = new GroupAddress("1/7/3");

    Command cmd1 = getCommand("Range", addr1, 255);

    Assert.assertTrue(cmd1 instanceof GroupValueWrite);

    KNXCommand knx1 = (KNXCommand)cmd1;

    Assert.assertTrue(knx1.getAddress().equals(addr1));
    Assert.assertTrue(knx1.getDataPointType() == DataPointType.VALUE_1_UCOUNT);

    Byte[] cemi1 = knx1.getCEMIFrame();

    // Expecting 8-bit value (CEMI frame 12 bytes)...

    Assert.assertTrue(cemi1.length == 12);

    // CEMI data length for 8 bit value is 2 bytes :
    //   - Byte 1 : APCI low bits + padding
    //   - Byte 2 : 8-bit (0-255) payload

    Assert.assertTrue(cemi1[KNXCommand.CEMI_DATALEN_OFFSET] == 2);

    Assert.assertTrue(
        "Expecting " + Strings.byteToUnsignedHexString((byte)255) +
        ", got " + Strings.byteToUnsignedHexString(cemi1[KNXCommand.CEMI_DATA1_OFFSET]),
        cemi1[KNXCommand.CEMI_DATA1_OFFSET] == (byte)0xFF
    );



    GroupAddress addr2 = new GroupAddress("0/1/255");

    Command cmd2 = getCommand("range", addr2, 1);

    Assert.assertTrue(cmd2 instanceof GroupValueWrite);

    KNXCommand knx2 = (KNXCommand)cmd2;

    Assert.assertTrue(knx2.getAddress().equals(addr2));
    Assert.assertTrue(knx2.getDataPointType() == DataPointType.VALUE_1_UCOUNT);

    Byte[] cemi2 = knx2.getCEMIFrame();

    // Expecting 8-bit value datatype (CEMI frame 12 bytes)...

    Assert.assertTrue(cemi2.length == 12);

    // CEMI data length for 8 bit value is 2 bytes :
    //   - Byte 1 : APCI low bits + padding
    //   - Byte 2 : 8-bit (0-255) payload

    Assert.assertTrue(cemi2[KNXCommand.CEMI_DATALEN_OFFSET] == 2);

    Assert.assertTrue(cemi2[KNXCommand.CEMI_DATA1_OFFSET] == 1);



    GroupAddress addr3 = new GroupAddress("2/2/255");

    Command cmd3 = getCommand("RAnge", addr3, 99);

    Assert.assertTrue(cmd3 instanceof GroupValueWrite);

    KNXCommand knx3 = (KNXCommand)cmd3;

    Assert.assertTrue(knx3.getAddress().equals(addr3));
    Assert.assertTrue(knx3.getDataPointType() == DataPointType.VALUE_1_UCOUNT);

    Byte[] cemi3 = knx3.getCEMIFrame();

    // Expecting 8-bit value datatype (CEMI frame 12 bytes)...

    Assert.assertTrue(cemi3.length == 12);

    // CEMI data length for 8 bit value is 2 bytes :
    //   - Byte 1 : APCI low bits + padding
    //   - Byte 2 : 8-bit (0-255) payload

    Assert.assertTrue(cemi3[KNXCommand.CEMI_DATALEN_OFFSET] == 2);

    Assert.assertTrue(cemi3[KNXCommand.CEMI_DATA1_OFFSET] == 99);



    GroupAddress addr4 = new GroupAddress("3/3/3");

    Command cmd4 = getCommand("Range", addr4, 254);

    Assert.assertTrue(cmd4 instanceof GroupValueWrite);

    KNXCommand knx4 = (KNXCommand)cmd4;

    Assert.assertTrue(knx4.getAddress().equals(addr4));
    Assert.assertTrue(knx4.getDataPointType() == DataPointType.VALUE_1_UCOUNT);

    Byte[] cemi4 = knx4.getCEMIFrame();

    // Expecting 8-bit value datatype (CEMI frame 12 bytes)...

    Assert.assertTrue(cemi4.length == 12);

    // CEMI data length for 8 bit value is 2 bytes :
    //   - Byte 1 : APCI low bits + padding
    //   - Byte 2 : 8-bit (0-255) payload

    Assert.assertTrue(cemi4[KNXCommand.CEMI_DATALEN_OFFSET] == 2);

    Assert.assertTrue(
        "Expecting " + Strings.byteToUnsignedHexString((byte)254) +
        ", got " + Strings.byteToUnsignedHexString(cemi4[KNXCommand.CEMI_DATA1_OFFSET]),
        cemi4[KNXCommand.CEMI_DATA1_OFFSET] == (byte)0xFE
    );



    GroupAddress addr5 = new GroupAddress("3/3/30");

    Command cmd5 = getCommand("Range", addr5, 250);

    Assert.assertTrue(cmd5 instanceof GroupValueWrite);

    KNXCommand knx5 = (KNXCommand)cmd5;

    Assert.assertTrue(knx5.getAddress().equals(addr5));
    Assert.assertTrue(knx5.getDataPointType() == DataPointType.VALUE_1_UCOUNT);

    Byte[] cemi5 = knx5.getCEMIFrame();

    // Expecting 8-bit value datatype (CEMI frame 12 bytes)...

    Assert.assertTrue(cemi5.length == 12);

    // CEMI data length for 8 bit value is 2 bytes :
    //   - Byte 1 : APCI low bits + padding
    //   - Byte 2 : 8-bit (0-255) payload

    Assert.assertTrue(cemi5[KNXCommand.CEMI_DATALEN_OFFSET] == 2);

    Assert.assertTrue(
        "Expecting " + Strings.byteToUnsignedHexString((byte)250) +
        ", got " + Strings.byteToUnsignedHexString(cemi5[KNXCommand.CEMI_DATA1_OFFSET]),
        cemi5[KNXCommand.CEMI_DATA1_OFFSET] == (byte)0xFA
    );
  }


  /**
   * Test the entire value range
   */
  @Test public void testValueRange() throws InvalidGroupAddressException
  {
    for (int value = 0; value <= 255; value++)
    {
      GroupAddress addr3 = new GroupAddress("13/3/13");

      Command cmd3 = getCommand("range", addr3, value);

      Assert.assertTrue(cmd3 instanceof GroupValueWrite);

      KNXCommand knx = (KNXCommand)cmd3;

      Byte[] cemi = knx.getCEMIFrame();

      // Expecting 8-bit value datatype (CEMI frame 12 bytes)...

      Assert.assertTrue(cemi.length == 12);

      // CEMI data length for 8 bit value is 2 bytes :
      //   - Byte 1 : APCI low bits + padding
      //   - Byte 2 : 8-bit (0-255) payload

      Assert.assertTrue(cemi[KNXCommand.CEMI_DATALEN_OFFSET] == 2);

      Assert.assertTrue(
          "Expecting " + Strings.byteToUnsignedHexString((byte)value) +
          ", got " + Strings.byteToUnsignedHexString(cemi[KNXCommand.CEMI_DATA1_OFFSET]),
          cemi[KNXCommand.CEMI_DATA1_OFFSET] == (byte)value
      );
    }
  }


  /**
   * Test 'Range' out of upper bounds value range
   */
  @Test
  public void testRangeOutOfUpperBounds() throws InvalidGroupAddressException
  {
    GroupAddress addr3 = new GroupAddress("13/3/13");

    try
    {
      Command cmd3 = getCommand("dim", addr3, 256);

      Assert.fail("Should not get here");
    }

    catch (NoSuchCommandException e)
    {
      // expected...
    }
  }

  /**
   * Test 'Range' out of lower bounds value range
   */
  @Test public void testRangeOutOfLowerBounds() throws InvalidGroupAddressException
  {
    GroupAddress addr3 = new GroupAddress("12/3/12");

    try
    {
      Command cmd3 = getCommand("dim", addr3, -1);

      Assert.fail("Was expecting 'NoSuchCommandException' runtime exception.");
    }

    catch (NoSuchCommandException e)
    {
      // expected..
    }
  }


  /**
   * Test missing command value argument with 'range'
   */
  @Test public void missingValueArgument()
  {
    try
    {
      Command cmd = getCommandNoArg("range", "10/1/10");

      Assert.fail("Should not get here, was expecting 'NoSuchCommandException'...");
    }

    catch (NoSuchCommandException e)
    {
      // expected...
    }
  }


  /**
   * Tests "RANGE" command equality.
   */
  @Test public void testRangeEquals()
  {
    Command cmd1 = getCommand("range", "10/1/10", 1);

    Assert.assertTrue(cmd1 instanceof GroupValueWrite);

    Command cmd2 = getCommand("RANGE", "10/1/10", 1);

    Assert.assertTrue(cmd2 instanceof GroupValueWrite);


    Assert.assertTrue("RANGE commands to same group address were not equal. See ORCJAVA-150", cmd1.equals(cmd2));
    Assert.assertTrue(cmd2.equals(cmd1));


    Command cmd3 = getCommandNoArg("ON", "10/1/10");

    Assert.assertFalse(cmd3.equals(cmd1));
    Assert.assertFalse(cmd1.equals(cmd3));
    Assert.assertFalse(cmd3.equals(cmd2));
    Assert.assertFalse(cmd2.equals(cmd3));


    Command cmd4 = getCommand("range", "10/1/10", 99);

    Assert.assertFalse(cmd4.equals(cmd1));
    Assert.assertFalse(cmd4.equals(cmd2));
    Assert.assertFalse(cmd1.equals(cmd4));
    Assert.assertFalse(cmd2.equals(cmd4));


    Command cmd5 = getCommand("range", "10/2/10", 100);

    Assert.assertFalse(cmd5.equals(cmd1));
    Assert.assertFalse(cmd5.equals(cmd2));
    Assert.assertFalse(cmd1.equals(cmd5));
    Assert.assertFalse(cmd2.equals(cmd5));
  }


  /**
   * Tests "RANGE" command group address accessor
   */
  @Test public void testRangeGroupAddress()
  {
    Command cmd1 = getCommand("RANGE", "4/4/4", 2);

    Assert.assertTrue(cmd1 instanceof GroupValueWrite);

    KNXCommand knx1 = (KNXCommand)cmd1;

    Assert.assertTrue(GroupAddress.formatToMainMiddleSub(knx1.getAddress().asByteArray()).equals("4/4/4"));
  }


  /**
   * Tests "Range" command DPT accessors
   */
  @Test public void testDimIncreaseDataTypeReturn()
  {
    Command cmd1 = getCommand("Range", "5/5/5", 1);

    Assert.assertTrue(cmd1 instanceof GroupValueWrite);
    KNXCommand knx1 = (KNXCommand)cmd1;

    Assert.assertTrue(knx1.getDataPointType() == DataPointType.VALUE_1_UCOUNT);
  }



  /**
   * Tests the APDU data content for 'Range' command.
   */
  @Test public void testRangeAPDUData() throws InvalidGroupAddressException
  {
    GroupAddress addr = new GroupAddress("1/1/1");

    Command cmd = getCommand("Range", addr, 50);

    Assert.assertTrue(cmd instanceof GroupValueWrite);

    KNXCommand knx = (KNXCommand)cmd;

    ApplicationProtocolDataUnit apdu = knx.getAPDU();

    // Datatype for 'Range' (VALUE_1_UCOUNT) is an unsigned 8-bit value

    Assert.assertTrue(apdu.getDataType() instanceof Unsigned8Bit);

    Unsigned8Bit u8 = (Unsigned8Bit)apdu.getDataType();

    // The data payload (actual value) size is 1 byte (8-bits)...

    Assert.assertTrue(u8.getData().length == 1);

    // The APDU length is 2 bytes :
    //  - Byte 1: APCI low bits + padding
    //  - Byte 2: actual value payload (8 bits)

    Assert.assertTrue(u8.getDataLength() == 2);

    // The KNX DPT for 'Range' is VALUE_1_UCOUNT...

    Assert.assertTrue(
        "Expecting " + DataPointType.VALUE_1_UCOUNT + ", got " + u8.getDataPointType(),
        u8.getDataPointType() == DataPointType.VALUE_1_UCOUNT
    );

    Assert.assertTrue(
        "Expecting " + Strings.byteToUnsignedHexString((byte)50) +
        ", got " + Strings.byteToUnsignedHexString(u8.getData() [0]),
        u8.getData() [0] == 50
    );
  }


  /**
   * Test PDU for range command
   */
  @Test public void testRangePDU()
  {
    Command cmd = getCommand("range", "8/1/10", 0xAF);

    Assert.assertTrue(cmd instanceof GroupValueWrite);

    KNXCommand knx = (KNXCommand)cmd;

    ApplicationProtocolDataUnit apdu = knx.getAPDU();

    Assert.assertTrue(apdu.getDataType() instanceof Unsigned8Bit);

    Byte[] pdu = apdu.getProtocolDataUnit();

    // 3 bytes :
    //   - Byte 1: TCPI bits (six most significant bits) + two APCI hi bits
    //   - Byte 2: APCI low bits (two most significant bits) + 6 bit padding (for 8-bit value datatype)
    //   - Byte 3: actual value payload (8 bit value)

    Assert.assertTrue(pdu.length == 3);

    Assert.assertTrue(pdu[0] == ApplicationLayer.Service.GROUPVALUE_WRITE.getTPCIAPCI());

    Assert.assertTrue(
        "Expecting " + Strings.byteToUnsignedHexString(
            (byte)ApplicationLayer.Service.GROUPVALUE_WRITE.getAPCIData()) +
        ", got " + Strings.byteToUnsignedHexString(pdu[1]),
        pdu[1] == (byte)ApplicationLayer.Service.GROUPVALUE_WRITE.getAPCIData());

    Assert.assertTrue(
        "Expecting 0xAF, got " + Strings.byteToUnsignedHexString(pdu[2]),
        pdu[2] == (byte)0xAF
    );
  }


  /**
   * Test APDU of range
   */
  @Test public void testAPDU()
  {
    Command cmd1 = getCommand("RANGE", "1/1/1", 0xC5);

    Assert.assertTrue(cmd1 instanceof GroupValueWrite);

    KNXCommand knx1 = (KNXCommand)cmd1;

    ApplicationProtocolDataUnit apdu1 = knx1.getAPDU();

    Assert.assertTrue(apdu1.getDataType() instanceof Unsigned8Bit);
    Assert.assertTrue(apdu1.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_WRITE);
    Assert.assertTrue(apdu1.getDataLength() == 2);
    Assert.assertTrue(apdu1.getDataPointType() == DataPointType.VALUE_1_UCOUNT);
  }


  /**
   * Test CEMI frame for range command
   */
  @Test public void testCEMIFrame() throws InvalidGroupAddressException
  {
    GroupAddress addr = new GroupAddress("3/3/3");

    Command cmd = getCommand("range", addr, 0xCA);

    Assert.assertTrue(cmd instanceof GroupValueWrite);

    KNXCommand knx = (KNXCommand)cmd;

    Byte[] cemi = knx.getCEMIFrame();

    // Expecting 8-bit value datatype (CEMI frame 12 bytes)...

    Assert.assertTrue(cemi.length == 12);

    // First byte in CEMI should be MessageCode: data request

    Assert.assertTrue(
        cemi[KNXCommand.CEMI_MESSAGECODE_OFFSET] == DataLink.MessageCode.DATA_REQUEST_BYTE
    );

    // Second byte in CEMI should be additional info field length -- assume zero

    Assert.assertTrue(cemi [KNXCommand.CEMI_ADDITIONALINFO_OFFSET] == 0x00);

    // Third byte is control 1 fields -- currently fixed set of bits

    Assert.assertTrue(
        "Expecting control1 bits 0x84, got " +
        Strings.byteToUnsignedHexString(cemi[KNXCommand.CEMI_CONTROL1_OFFSET]),
        cemi[KNXCommand.CEMI_CONTROL1_OFFSET] == (byte)(0x84 & 0xFF)
    );

    // Fourth byte is control 2 fields -- currently fixed set of bits

    Assert.assertTrue(cemi[KNXCommand.CEMI_CONTROL2_OFFSET] == (byte)(0xE0 & 0xFF));

    // Fifth and sixth bytes are source address -- filled by gateway

    Assert.assertTrue(cemi[KNXCommand.CEMI_SOURCEADDR_HIGH_OFFSET] == 0x00);
    Assert.assertTrue(cemi[KNXCommand.CEMI_SOURCEADDR_LOW_OFFSET] == 0x00);

    // Seventh and eighth bytes are destination address -- group address 3/3/3

    Assert.assertTrue(cemi[KNXCommand.CEMI_DESTADDR_HIGH_OFFSET] == addr.asByteArray() [0]);
    Assert.assertTrue(cemi[KNXCommand.CEMI_DESTADDR_LOW_OFFSET] == addr.asByteArray() [1]);

    // Ninth byte is the CEMI data length value -- this includes the APCI low bits
    // so for  unsigned 8-bit value, length is still two (Byte 1: APCI low bits + padding,
    // Byte 2: actual data payload (8-bits)

    Assert.assertTrue(cemi[KNXCommand.CEMI_DATALEN_OFFSET] == 0x02);

    // Tenth byte are part of PDU with TPCI control bits (6 most significant buts) +
    // APCI high bits (two least significant bits)

    Byte[] pdu = knx.getAPDU().getProtocolDataUnit();

    Assert.assertTrue(
        "Expecting TPCI/APCI bits " +
        Strings.byteToUnsignedHexString(pdu[0]) +
        ", got " + Strings.byteToUnsignedHexString(cemi[KNXCommand.CEMI_TPCI_APCI_OFFSET]),
        cemi[KNXCommand.CEMI_TPCI_APCI_OFFSET].equals(pdu[0])
    );

    // Eleventh byte is APCI low bits + padding (for 8-bit unsigned datatype)

    Assert.assertTrue(
        "Expecting APCI bits " +
        Strings.byteToUnsignedHexString(pdu[1]) +
        ", got " + Strings.byteToUnsignedHexString(cemi[KNXCommand.CEMI_APCI_DATA_OFFSET]),
        cemi[KNXCommand.CEMI_APCI_DATA_OFFSET].equals(pdu[1])
    );

    // Twelth byte is the actual data payload

    Assert.assertTrue(
        "Expecting data payload 0xCA, got " +
        Strings.byteToUnsignedHexString(cemi[KNXCommand.CEMI_DATA1_OFFSET]),
        cemi[KNXCommand.CEMI_DATA1_OFFSET] == (byte)0xCA
    );

  }




  // Helpers --------------------------------------------------------------------------------------


  private Command getCommand(String cmd, GroupAddress groupAddress, int value)
  {
    return getCommand(cmd, GroupAddress.formatToMainMiddleSub(groupAddress.asByteArray()), value);
  }


  private Command getCommand(String cmd, String groupAddress, int value)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "knx");
    ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, Integer.toString(value));

    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                          KNXCommandBuilder.KNX_XMLPROPERTY_GROUPADDRESS);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                          groupAddress);

    ele.addContent(propAddr);

    Element propAddr2 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                           KNXCommandBuilder.KNX_XMLPROPERTY_COMMAND);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                           cmd);

    ele.addContent(propAddr2);

    Element propAddr3 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr3.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                           KNXCommandBuilder.KNX_XMLPROPERTY_DPT);
    propAddr3.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                           DataPointType.VALUE_1_UCOUNT.getDPTID());

    ele.addContent(propAddr3);


    return builder.build(ele);
  }



  private Command getCommandNoArg(String cmd, String groupAddress)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "knx");

    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                          KNXCommandBuilder.KNX_XMLPROPERTY_GROUPADDRESS);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                          groupAddress);

    ele.addContent(propAddr);

    Element propAddr2 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                           KNXCommandBuilder.KNX_XMLPROPERTY_COMMAND);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                           cmd);

    ele.addContent(propAddr2);

    Element propAddr3 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr3.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                           KNXCommandBuilder.KNX_XMLPROPERTY_DPT);
    propAddr3.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                           DataPointType.VALUE_1_UCOUNT.getDPTID());

    ele.addContent(propAddr3);


    return builder.build(ele);
  }


}

