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

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.protocol.knx.datatype.DataPointType;
import org.openremote.controller.protocol.knx.datatype.Controlled3Bit;
import org.openremote.controller.protocol.knx.datatype.Bool;
import org.openremote.controller.utils.Strings;
import org.jdom.Element;
import static junit.framework.Assert.assertTrue;

/**
 * Unit tests for KNX Dim Increase/Decrease commands.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class DimIncreaseDecreaseTest
{


  // Test Setup -----------------------------------------------------------------------------------

  private KNXCommandBuilder builder = null;

  @Before
  public void setUp() {
    builder = new KNXCommandBuilder("127.0.0.1", 9999, "org.openremote.controller.protocol.bus.DatagramSocketPhysicalBus");
  }



  // Tests ----------------------------------------------------------------------------------------


  /**
   * Test KNX command parsing with "DIM INCREASE" as the command string and 1/1/1 as group address.
   */
  @Test
  public void testDimIncrease()
  {
    Command cmd = getCommand("DIM INCREASE", "1/1/1");

    assertTrue(cmd instanceof GroupValueWrite);
  }

  /**
   * Test KNX command parsing with "Dim Increase", "dim increase" and "Dim increase" as the command
   * string and 1/1/1 as group address.
   */
  @Test public void testDimIncreaseMixedCase()
  {
    Command cmd1 = getCommand("Dim Increase", "1/1/1");

    assertTrue(cmd1 instanceof GroupValueWrite);

    Command cmd2 = getCommand("dim increase", "1/1/1");

    assertTrue(cmd2 instanceof GroupValueWrite);

    Command cmd3 = getCommand("Dim increase", "1/1/1");

    assertTrue(cmd3 instanceof GroupValueWrite);
  }

  /**
   * Test KNX command parsing with "DIM_INCREASE" as the command string and 1/1/1 as group address.
   */
  @Test public void testDimIncrease2()
  {
    Command cmd = getCommand("DIM_INCREASE", "1/1/1");

    assertTrue(cmd instanceof GroupValueWrite);
  }

  /**
   * Test KNX command parsing with "Dim_Increase", "dim_increase" and "Dim_increase" as the command
   * string and 1/1/1 as group address.
   */
  @Test public void testDimIncreaseMixedCase2()
  {
    Command cmd1 = getCommand("Dim_Increase", "1/1/1");

    assertTrue(cmd1 instanceof GroupValueWrite);

    Command cmd2 = getCommand("dim_increase", "1/1/1");

    assertTrue(cmd2 instanceof GroupValueWrite);

    Command cmd3 = getCommand("Dim_increase", "1/1/1");

    assertTrue(cmd3 instanceof GroupValueWrite);
  }

  /**
   * Tests two "DIM INCREASE" command equality.
   */
  @Test public void testDimIncreaseEquals()
  {
    Command cmd1 = getCommand("DIM INCREASE", "1/1/1");

    assertTrue(cmd1 instanceof GroupValueWrite);

    Command cmd2 = getCommand("DIM INCREASE", "1/1/1");

    assertTrue(cmd2 instanceof GroupValueWrite);


    assertTrue("DIM INCREASE commands to same group address were not equal (see ORCJAVA-150)", cmd1.equals(cmd2));
    assertTrue(cmd2.equals(cmd1));


    Command cmd3 = getCommand("ON", "1/1/1");

    Assert.assertFalse(cmd3.equals(cmd1));
    Assert.assertFalse(cmd1.equals(cmd3));
    Assert.assertFalse(cmd3.equals(cmd2));
    Assert.assertFalse(cmd2.equals(cmd3));
  }

  /**
   * Tests "Dim Increase/Decrease" commands with same group address
   */
  @Test public void testDimIncreaseGroupAddress()
  {
    Command cmd1 = getCommand("DIM INCREASE", "0/0/0");
    Command cmd2 = getCommand("DIM decrease", "31/7/255");

    assertTrue(cmd1 instanceof GroupValueWrite);
    assertTrue(cmd2 instanceof GroupValueWrite);

    KNXCommand knx1 = (KNXCommand)cmd1;
    KNXCommand knx2 = (KNXCommand)cmd2;

    assertTrue(GroupAddress.formatToMainMiddleSub(knx1.getAddress().asByteArray()).equals("0/0/0"));
    assertTrue(GroupAddress.formatToMainMiddleSub(knx2.getAddress().asByteArray()).equals("31/7/255"));
  }

  /**
   * Tests "Dim Increase/Decrease" command DPT returns
   */
  @Test public void testDimIncreaseDataTypeReturn()
  {
    Command cmd1 = getCommand("Dim INCREASE", "1/1/1");
    Command cmd2 = getCommand("DIM_decrease", "31/7/255");

    assertTrue(cmd1 instanceof GroupValueWrite);
    assertTrue(cmd2 instanceof GroupValueWrite);

    KNXCommand knx1 = (KNXCommand)cmd1;
    KNXCommand knx2 = (KNXCommand)cmd2;

    assertTrue(knx1.getDataPointType() == DataPointType.CONTROL_DIMMING);
    assertTrue(knx2.getDataPointType() == DataPointType.CONTROL_DIMMING);
  }

  /**
   * Tests the APDU data content for Dim Increase command.
   */
  @Test public void testDimIncreaseAPDUData()
  {
    Command cmd = getCommand("Dim_increase", "1/1/1");

    assertTrue(cmd instanceof GroupValueWrite);

    KNXCommand knx = (KNXCommand)cmd;

    ApplicationProtocolDataUnit apdu = knx.getAPDU();

    assertTrue(apdu.getDataType() instanceof Controlled3Bit);

    Controlled3Bit c3 = (Controlled3Bit)apdu.getDataType();

    assertTrue(c3.getData().length == 1);
    assertTrue(c3.getDataLength() == 1);

    assertTrue(
        "Expecting " + DataPointType.CONTROL_DIMMING + ", got " + c3.getDataPointType(),
        c3.getDataPointType() == DataPointType.CONTROL_DIMMING
    );

    int boolBit = Bool.INCREASE.getData() [0] << 3;
    int valBits = GroupValueWrite.DIMCONTROL_INCREASE_VALUE;

    byte apduData = (byte)((boolBit + valBits) & 0xFF);

    assertTrue(
        "Expecting " +
        Strings.byteToUnsignedHexString(apduData) +
        ", got " + Strings.byteToUnsignedHexString(c3.getData() [0]),
        c3.getData() [0] == apduData
    );
  }

  /**
   * Tests the APDU data content for Dim decrease command.
   */
  @Test public void testDimDecreaseAPDUData()
  {
    Command cmd = getCommand("DiM decrease", "1/1/1");

    assertTrue(cmd instanceof GroupValueWrite);

    KNXCommand knx = (KNXCommand)cmd;

    ApplicationProtocolDataUnit apdu = knx.getAPDU();

    assertTrue(apdu.getDataType() instanceof Controlled3Bit);

    Controlled3Bit c3 = (Controlled3Bit)apdu.getDataType();

    assertTrue(c3.getData().length == 1);
    assertTrue(c3.getDataLength() == 1);

    assertTrue(
        "Expecting " + DataPointType.CONTROL_DIMMING + ", got " + c3.getDataPointType(),
        c3.getDataPointType() == DataPointType.CONTROL_DIMMING
    );

    int boolBit = Bool.DECREASE.getData() [0] << 3;
    int valBits = GroupValueWrite.DIMCONTROL_DECREASE_VALUE;

    byte apduData = (byte)((boolBit + valBits) & 0xFF);

    assertTrue(
        "Expecting " +
        Strings.byteToUnsignedHexString(apduData) +
        ", got " + Strings.byteToUnsignedHexString(c3.getData() [0]),
        c3.getData() [0] == apduData
    );
  }


  /**
   * Test PDU for dim increase command
   */
  @Test public void testDimIncreasePDU()
  {
    Command cmd = getCommand("DiM IncRease", "1/1/1");

    assertTrue(cmd instanceof GroupValueWrite);

    KNXCommand knx = (KNXCommand)cmd;

    ApplicationProtocolDataUnit apdu = knx.getAPDU();

    assertTrue(apdu.getDataType() instanceof Controlled3Bit);

    Byte[] pdu = apdu.getProtocolDataUnit();

    assertTrue(pdu.length == 2);
    assertTrue(pdu[0] == ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT.getTPCIAPCI());

    int boolBit = Bool.INCREASE.getData() [0] << 3;
    int valBits = GroupValueWrite.DIMCONTROL_INCREASE_VALUE;

    byte data = (byte)((boolBit + valBits) & 0xFF);

    byte apduData = (byte)((ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT.getAPCIData() + data) & 0xFF);

    assertTrue(
        "Expecting " + Strings.byteToUnsignedHexString(apduData) + ", got " + pdu[1],
        pdu[1] == apduData
    );

  }


  /**
   * Test PDU for dim increase command
   */
  @Test public void testDimDecreasePDU()
  {
    Command cmd = getCommand("dim DECRease", "1/1/1");

    assertTrue(cmd instanceof GroupValueWrite);

    KNXCommand knx = (KNXCommand)cmd;

    ApplicationProtocolDataUnit apdu = knx.getAPDU();

    assertTrue(apdu.getDataType() instanceof Controlled3Bit);

    Byte[] pdu = apdu.getProtocolDataUnit();

    assertTrue(pdu.length == 2);
    assertTrue(pdu[0] == ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT.getTPCIAPCI());

    int boolBit = Bool.DECREASE.getData() [0] << 3;
    int valBits = GroupValueWrite.DIMCONTROL_DECREASE_VALUE;

    byte data = (byte)((boolBit + valBits) & 0xFF);

    byte apduData = (byte)((ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT.getAPCIData() + data) & 0xFF);

    assertTrue(
        "Expecting " + Strings.byteToUnsignedHexString(apduData) + ", got " + pdu[1],
        pdu[1] == apduData
    );
  }


  /**
   * Test APDU of dim increase/decrease
   */
  @Test public void testAPDU()
  {
    Command cmd1 = getCommand("dim decrease", "1/1/1");

    assertTrue(cmd1 instanceof GroupValueWrite);

    KNXCommand knx1 = (KNXCommand)cmd1;

    ApplicationProtocolDataUnit apdu1 = knx1.getAPDU();

    assertTrue(apdu1.getDataType() instanceof Controlled3Bit);
    assertTrue(apdu1.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT);
    assertTrue(apdu1.getDataLength() == 1);
    assertTrue(apdu1.getDataPointType() == DataPointType.CONTROL_DIMMING);

    Command cmd2 = getCommand("dim increase", "1/1/1");

    assertTrue(cmd2 instanceof GroupValueWrite);

    KNXCommand knx2 = (KNXCommand)cmd2;

    ApplicationProtocolDataUnit apdu2 = knx2.getAPDU();

    assertTrue(apdu2.getDataType() instanceof Controlled3Bit);
    assertTrue(apdu2.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT);
    assertTrue(apdu2.getDataLength() == 1);
    assertTrue(apdu2.getDataPointType() == DataPointType.CONTROL_DIMMING);
  }


  /**
   * Test CEMI frame for dim increase command
   */
  @Test public void testCEMIFrame() throws InvalidGroupAddressException
  {
    GroupAddress addr = new GroupAddress("2/2/2");

    Command cmd = getCommand("dim increase", addr);

    assertTrue(cmd instanceof GroupValueWrite);

    KNXCommand knx = (KNXCommand)cmd;

    Byte[] cemi = knx.getCEMIFrame();

    assertTrue(cemi.length == 11);

    assertTrue(cemi [0] == DataLink.Service.DATA.getRequestMessageCode());
    assertTrue(cemi [1] == 0x00);

    assertTrue(
        "Expecting control1 bits 0x84, got " + Strings.byteToUnsignedHexString(cemi[2]),
        cemi [2] == (byte)(0x84 & 0xFF)
    );
    
    assertTrue(cemi [3] == (byte)(0xE0 & 0xFF));
    assertTrue(cemi [4] == 0x00);
    assertTrue(cemi [5] == 0x00);
    assertTrue(cemi [6] == 0x12);
    assertTrue(cemi [7] == 0x02);
    assertTrue(cemi [8] == 0x01);

    assertTrue(
        "Expecting TPCI/APCI bits 0x00, got " + Strings.byteToUnsignedHexString(cemi[9]),
        cemi [9] == (byte)(0x00 & 0xFF)
    );

    int data = 0x80 + (1 << 3) + GroupValueWrite.DIMCONTROL_INCREASE_VALUE;
    byte dataByte = (byte)(data & 0xFF);

    assertTrue(
        "Expecting datapayload " + Strings.byteToUnsignedHexString(dataByte) +
        ", got " + Strings.byteToUnsignedHexString(cemi[10]),
        cemi [10] == dataByte
    );
  }


  /**
   * Test CEMI frame for dim decrease command
   */
  @Test public void testCEMIFrameForDecrease() throws InvalidGroupAddressException
  {
    GroupAddress addr = new GroupAddress("31/7/255");

    Command cmd = getCommand("dim_decrease", addr);

    assertTrue(cmd instanceof GroupValueWrite);

    KNXCommand knx = (KNXCommand)cmd;

    Byte[] cemi = knx.getCEMIFrame();

    assertTrue(cemi.length == 11);

    assertTrue(cemi [0] == DataLink.Service.DATA.getRequestMessageCode());
    assertTrue(cemi [1] == 0x00);

    assertTrue(
        "Expecting control1 bits 0x84, got " + Strings.byteToUnsignedHexString(cemi[2]),
        cemi [2] == (byte)(0x84 & 0xFF)
    );

    assertTrue(cemi [3] == (byte)(0xE0 & 0xFF));
    assertTrue(cemi [4] == 0x00);
    assertTrue(cemi [5] == 0x00);
    assertTrue(cemi [6] == (byte)(0xFF & 0xFF));
    assertTrue(cemi [7] == (byte)(0xFF & 0xFF));
    assertTrue(cemi [8] == 0x01);

    assertTrue(
        "Expecting TPCI/APCI bits 0x00, got " + Strings.byteToUnsignedHexString(cemi[9]),
        cemi [9] == (byte)(0x00 & 0xFF)
    );

    int data = 0x80 + GroupValueWrite.DIMCONTROL_DECREASE_VALUE;
    byte dataByte = (byte)(data & 0xFF);

    assertTrue(
        "Expecting datapayload " + Strings.byteToUnsignedHexString(dataByte) +
        ", got " + Strings.byteToUnsignedHexString(cemi[10]),
        cemi [10] == dataByte
    );
  }



  // Helpers --------------------------------------------------------------------------------------


  private Command getCommand(String cmd, GroupAddress groupAddress)
  {
    return getCommand(cmd, GroupAddress.formatToMainMiddleSub(groupAddress.asByteArray()));
  }

  private Command getCommand(String cmd, String groupAddress)
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
                           DataPointType.Control3BitDataPointType.CONTROL_DIMMING.getDPTID());

    ele.addContent(propAddr3);

    return builder.build(ele);
  }



}

