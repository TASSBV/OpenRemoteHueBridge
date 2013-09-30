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

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.protocol.knx.datatype.DataPointType;
import org.openremote.controller.protocol.knx.datatype.Unsigned8Bit;
import org.openremote.controller.utils.Strings;
import org.openremote.controller.exception.NoSuchCommandException;
import org.jdom.Element;
import static junit.framework.Assert.assertTrue;

/**
 * Unit tests for KNX Scaling (dim) command.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ScalingTest
{

  // Test Setup -----------------------------------------------------------------------------------

  private KNXCommandBuilder builder = null;

  @Before
  public void setUp()
  {
    builder = new KNXCommandBuilder("127.0.0.1", 9999, "org.openremote.controller.protocol.bus.DatagramSocketPhysicalBus");
  }



  // Tests ----------------------------------------------------------------------------------------


  /**
   * Test KNX command parsing with "DIM" as the command string.
   */
  @Test
  public void testDimCommand() throws InvalidGroupAddressException
  {
    GroupAddress addr = new GroupAddress("5/5/5");

    Command cmd = getCommand("DIM", addr, 0);

    assertTrue(cmd instanceof GroupValueWrite);

    KNXCommand knx = (KNXCommand)cmd;

    assertTrue(knx.getAddress().equals(addr));
    assertTrue(knx.getDataPointType() == DataPointType.SCALING);
  }

  /**
   * Test KNX command parsing with "Dim", "dim" and "diM" as the command string.
   */
  @Test public void testDimCommandMixedCase() throws InvalidGroupAddressException
  {
    GroupAddress addr1 = new GroupAddress("0/0/0");

    Command cmd1 = getCommand("Dim", addr1, 100);

    assertTrue(cmd1 instanceof GroupValueWrite);

    KNXCommand knx1 = (KNXCommand)cmd1;

    assertTrue(knx1.getAddress().equals(addr1));
    assertTrue(knx1.getDataPointType() == DataPointType.SCALING);

    Byte[] cemi1 = knx1.getCEMIFrame();

    assertTrue(cemi1.length == 12);
    assertTrue(cemi1[KNXCommand.CEMI_DATALEN_OFFSET] == 2);

    assertTrue(
        "Expecting " + Strings.byteToUnsignedHexString((byte)255) +
        ", got " + Strings.byteToUnsignedHexString(cemi1[KNXCommand.CEMI_DATA1_OFFSET]),
        cemi1[KNXCommand.CEMI_DATA1_OFFSET] == (byte)0xFF
    );



    GroupAddress addr2 = new GroupAddress("0/0/255");

    Command cmd2 = getCommand("dim", addr2, 1);

    assertTrue(cmd2 instanceof GroupValueWrite);

    KNXCommand knx2 = (KNXCommand)cmd2;

    assertTrue(knx2.getAddress().equals(addr2));
    assertTrue(knx2.getDataPointType() == DataPointType.SCALING);

    Byte[] cemi2 = knx2.getCEMIFrame();

    assertTrue(cemi2.length == 12);
    assertTrue(cemi2[KNXCommand.CEMI_DATALEN_OFFSET] == 2);

    assertTrue(cemi2[KNXCommand.CEMI_DATA1_OFFSET] == (byte)Math.round(1 * 2.55));



    GroupAddress addr3 = new GroupAddress("0/0/255");

    Command cmd3 = getCommand("diM", addr3, 99);

    assertTrue(cmd3 instanceof GroupValueWrite);

    KNXCommand knx3 = (KNXCommand)cmd3;

    assertTrue(knx3.getAddress().equals(addr3));
    assertTrue(knx3.getDataPointType() == DataPointType.SCALING);

    Byte[] cemi3 = knx3.getCEMIFrame();

    assertTrue(cemi3.length == 12);
    assertTrue(cemi3[KNXCommand.CEMI_DATALEN_OFFSET] == 2);

    assertTrue(cemi3[KNXCommand.CEMI_DATA1_OFFSET] == (byte)Math.round(99 * 2.55));

  }



  /**
   * Test KNX command parsing with "SCALE" as the command string.
   */
  @Test public void testScale() throws InvalidGroupAddressException
  {

    GroupAddress addr = new GroupAddress("8/7/6");

    Command cmd = getCommand("SCALE", addr, 98);

    assertTrue(cmd instanceof GroupValueWrite);

    KNXCommand knx = (KNXCommand)cmd;

    assertTrue(knx.getAddress().equals(addr));
    assertTrue(knx.getDataPointType() == DataPointType.SCALING);

    Byte[] cemi = knx.getCEMIFrame();

    assertTrue(cemi.length == 12);
    assertTrue(cemi[KNXCommand.CEMI_DATALEN_OFFSET] == 2);

    byte unsignedValue = (byte)Math.round(98 * 2.55);

    assertTrue(
        "Was expecting " + Strings.byteToUnsignedHexString(unsignedValue) +
        ", got " + Strings.byteToUnsignedHexString(cemi[KNXCommand.CEMI_DATA1_OFFSET]),
        cemi[KNXCommand.CEMI_DATA1_OFFSET] == unsignedValue
    );
  }


  /**
   * Test KNX command parsing with "scale", "Scale" and "SCale" as the command strings.
   */
  @Test public void testScaleMixedCase() throws InvalidGroupAddressException
  {
    GroupAddress addr1 = new GroupAddress("1/2/3");

    Command cmd1 = getCommand("scale", addr1, 97);

    assertTrue(cmd1 instanceof GroupValueWrite);

    KNXCommand knx1 = (KNXCommand)cmd1;

    assertTrue(knx1.getAddress().equals(addr1));
    assertTrue(knx1.getDataPointType() == DataPointType.SCALING);

    Byte[] cemi1 = knx1.getCEMIFrame();

    assertTrue(cemi1.length == 12);
    assertTrue(cemi1[KNXCommand.CEMI_DATALEN_OFFSET] == 2);

    assertTrue(cemi1[KNXCommand.CEMI_DATA1_OFFSET] == (byte)Math.round(97 * 2.55));





    GroupAddress addr2 = new GroupAddress("3/2/1");

    Command cmd2 = getCommand("Scale", addr2, 96);

    assertTrue(cmd2 instanceof GroupValueWrite);

    KNXCommand knx2 = (KNXCommand)cmd2;

    assertTrue(knx2.getAddress().equals(addr2));
    assertTrue(knx2.getDataPointType() == DataPointType.SCALING);

    Byte[] cemi2 = knx2.getCEMIFrame();

    assertTrue(cemi2.length == 12);
    assertTrue(cemi2[KNXCommand.CEMI_DATALEN_OFFSET] == 2);

    assertTrue(cemi2[KNXCommand.CEMI_DATA1_OFFSET] == (byte)Math.round(96 * 2.55));




    GroupAddress addr3 = new GroupAddress("3/3/3");

    Command cmd3 = getCommand("SCale", addr3, 50);

    assertTrue(cmd3 instanceof GroupValueWrite);

    KNXCommand knx3 = (KNXCommand)cmd3;

    assertTrue(knx3.getAddress().equals(addr3));
    assertTrue(knx3.getDataPointType() == DataPointType.SCALING);

    Byte[] cemi3 = knx3.getCEMIFrame();

    assertTrue(cemi3.length == 12);
    assertTrue(cemi3[KNXCommand.CEMI_DATALEN_OFFSET] == 2);

    assertTrue(cemi3[KNXCommand.CEMI_DATA1_OFFSET] == (byte)Math.round(50 * 2.55));

  }


  /**
   * Test 'DIM' value range
   */
  @Test public void testDimValueRange() throws InvalidGroupAddressException
  {
    for (int value = 0; value <= 100; value++)
    {
      GroupAddress addr3 = new GroupAddress("13/3/13");

      Command cmd3 = getCommand("dim", addr3, value);

      assertTrue(cmd3 instanceof GroupValueWrite);

      KNXCommand knx = (KNXCommand)cmd3;

      Byte[] cemi = knx.getCEMIFrame();

      assertTrue(cemi.length == 12);
      assertTrue(cemi[KNXCommand.CEMI_DATALEN_OFFSET] == 2);

      assertTrue(cemi[KNXCommand.CEMI_DATA1_OFFSET] == (byte)Math.round(value * 2.55));
    }
  }


  /**
   * Test 'DIM' out of upper bounds value range
   */
  @Test (expected = NoSuchCommandException.class)
  public void testDimOutOfUpperBounds() throws InvalidGroupAddressException
  {
    GroupAddress addr3 = new GroupAddress("13/3/13");

    Command cmd3 = getCommand("dim", addr3, 101);
  }

  /**
   * Test 'DIM' out of lower bounds value range
   */
  @Test (expected = NoSuchCommandException.class)
  public void testDimOutOfLowerBounds() throws InvalidGroupAddressException
  {
    GroupAddress addr3 = new GroupAddress("12/3/12");

    Command cmd3 = getCommand("dim", addr3, -1);
  }


  /**
   * Test "Scale" upper bound
   */
  @Test public void testScaleUpperBound() throws InvalidGroupAddressException
  {
    GroupAddress addr = new GroupAddress("11/1/11");

    Command cmd = getCommand("scale", addr, 100);

    assertTrue(cmd instanceof GroupValueWrite);

    KNXCommand knx = (KNXCommand)cmd;

    Byte[] cemi = knx.getCEMIFrame();

    assertTrue(cemi.length == 12);
    assertTrue(cemi[KNXCommand.CEMI_DATA1_OFFSET] == (byte)0xFF);
  }


  /**
   * Test "Scale" lower bound values
   */
  @Test public void testScaleLowerBoundValues() throws InvalidGroupAddressException
  {
    GroupAddress addr = new GroupAddress("12/2/2");

    Command cmd1 = getCommand("scale", addr, 1);

    assertTrue(cmd1 instanceof GroupValueWrite);

    KNXCommand knx1 = (KNXCommand)cmd1;

    Byte[] cemi1 = knx1.getCEMIFrame();

    assertTrue(cemi1[KNXCommand.CEMI_DATA1_OFFSET] == 0x03);




    Command cmd2 = getCommand("scale", addr, 2);

    assertTrue(cmd2 instanceof GroupValueWrite);

    KNXCommand knx2 = (KNXCommand)cmd2;

    Byte[] cemi2 = knx2.getCEMIFrame();

    assertTrue(cemi2[KNXCommand.CEMI_DATA1_OFFSET] == 0x05);




    Command cmd3 = getCommand("scale", addr, 3);

    assertTrue(cmd3 instanceof GroupValueWrite);

    KNXCommand knx3 = (KNXCommand)cmd3;

    Byte[] cemi3 = knx3.getCEMIFrame();

    assertTrue(cemi3[KNXCommand.CEMI_DATA1_OFFSET] == 0x08);





    Command cmd4 = getCommand("scale", addr, 0);

    assertTrue(cmd4 instanceof GroupValueWrite);

    KNXCommand knx4 = (KNXCommand)cmd4;

    Byte[] cemi4 = knx4.getCEMIFrame();

    assertTrue(cemi4[KNXCommand.CEMI_DATA1_OFFSET] == 0x00);
  
  }

  /**
   * Test scale with parameter value as part of the command name.
   */
  @Test public void testScaleEmbeddedParameter()
  {
     String addr = "12/2/2";

     Command cmd1 = getCommandNoArg("SCALE 0", addr);

     assertTrue(cmd1 instanceof GroupValueWrite);

     KNXCommand knx1 = (KNXCommand)cmd1;

     Byte[] cemi1 = knx1.getCEMIFrame();

     assertTrue(cemi1[KNXCommand.CEMI_DATA1_OFFSET] == 0x00);


     Command cmd2 = getCommandNoArg("SCALE 100", addr);

     assertTrue(cmd2 instanceof GroupValueWrite);

     KNXCommand knx2 = (KNXCommand)cmd2;

     Byte[] cemi2 = knx2.getCEMIFrame();

     assertTrue("Expected 0xFF but got " + Integer.toString(cemi2[KNXCommand.CEMI_DATA1_OFFSET], 16), cemi2[KNXCommand.CEMI_DATA1_OFFSET] == (byte)0xFF);
  }

  /**
   * Test command name with different cases for scale command with parameter value as part of the command name.
   */
  @Test public void testScaleEmbeddedMixedCaseParameter()
  {
     String addr = "12/2/2";

     Command cmd1 = getCommandNoArg("Scale 0", addr);

     assertTrue(cmd1 instanceof GroupValueWrite);

     KNXCommand knx1 = (KNXCommand)cmd1;

     Byte[] cemi1 = knx1.getCEMIFrame();

     assertTrue(cemi1[KNXCommand.CEMI_DATA1_OFFSET] == 0x00);


     Command cmd2 = getCommandNoArg("scAle 100", addr);

     assertTrue(cmd2 instanceof GroupValueWrite);

     KNXCommand knx2 = (KNXCommand)cmd2;

     Byte[] cemi2 = knx2.getCEMIFrame();

     assertTrue("Expected 0xFF but got " + Integer.toString(cemi2[KNXCommand.CEMI_DATA1_OFFSET], 16), cemi2[KNXCommand.CEMI_DATA1_OFFSET] == (byte)0xFF);
  }
  
  /**
   * Test scale with parameter value as part of the command name, varying the number of space character between the SCALE command and the parameter value.
   */
  @Test public void testScaleEmbeddedParameterSpacesVariation()
  {
     String addr = "12/2/2";

     Command cmd1 = getCommandNoArg("SCALE0", addr);

     assertTrue(cmd1 instanceof GroupValueWrite);

     KNXCommand knx1 = (KNXCommand)cmd1;

     Byte[] cemi1 = knx1.getCEMIFrame();

     assertTrue(cemi1[KNXCommand.CEMI_DATA1_OFFSET] == 0x00);


     Command cmd2 = getCommandNoArg("SCALE    100", addr);

     assertTrue(cmd2 instanceof GroupValueWrite);

     KNXCommand knx2 = (KNXCommand)cmd2;

     Byte[] cemi2 = knx2.getCEMIFrame();

     assertTrue("Expected 0xFF but got " + Integer.toString(cemi2[KNXCommand.CEMI_DATA1_OFFSET], 16), cemi2[KNXCommand.CEMI_DATA1_OFFSET] == (byte)0xFF);
  }
  
  /**
   * Test dim with parameter value as part of the command name.
   */
  @Test public void testDimEmbeddedParameter()
  {
     String addr = "12/2/2";

     Command cmd1 = getCommandNoArg("DIM 0", addr);

     assertTrue(cmd1 instanceof GroupValueWrite);

     KNXCommand knx1 = (KNXCommand)cmd1;

     Byte[] cemi1 = knx1.getCEMIFrame();

     assertTrue(cemi1[KNXCommand.CEMI_DATA1_OFFSET] == 0x00);


     Command cmd2 = getCommandNoArg("DIM 100", addr);

     assertTrue(cmd2 instanceof GroupValueWrite);

     KNXCommand knx2 = (KNXCommand)cmd2;

     Byte[] cemi2 = knx2.getCEMIFrame();

     assertTrue("Expected 0xFF but got " + Integer.toString(cemi2[KNXCommand.CEMI_DATA1_OFFSET], 16), cemi2[KNXCommand.CEMI_DATA1_OFFSET] == (byte)0xFF);
  }

  /**
   * Test command name with different cases for dim command with parameter value as part of the command name.
   */
  @Test public void testDimEmbeddedMixedCaseParameter()
  {
     String addr = "12/2/2";

     Command cmd1 = getCommandNoArg("Dim 0", addr);

     assertTrue(cmd1 instanceof GroupValueWrite);

     KNXCommand knx1 = (KNXCommand)cmd1;

     Byte[] cemi1 = knx1.getCEMIFrame();

     assertTrue(cemi1[KNXCommand.CEMI_DATA1_OFFSET] == 0x00);


     Command cmd2 = getCommandNoArg("dIm 100", addr);

     assertTrue(cmd2 instanceof GroupValueWrite);

     KNXCommand knx2 = (KNXCommand)cmd2;

     Byte[] cemi2 = knx2.getCEMIFrame();

     assertTrue("Expected 0xFF but got " + Integer.toString(cemi2[KNXCommand.CEMI_DATA1_OFFSET], 16), cemi2[KNXCommand.CEMI_DATA1_OFFSET] == (byte)0xFF);
  }
  
  /**
   * Test dim with parameter value as part of the command name, varying the number of space character between the DIM command and the parameter value.
   */
  @Test public void testDimEmbeddedParameterSpacesVariation()
  {
     String addr = "12/2/2";

     Command cmd1 = getCommandNoArg("DIM0", addr);

     assertTrue(cmd1 instanceof GroupValueWrite);

     KNXCommand knx1 = (KNXCommand)cmd1;

     Byte[] cemi1 = knx1.getCEMIFrame();

     assertTrue(cemi1[KNXCommand.CEMI_DATA1_OFFSET] == 0x00);


     Command cmd2 = getCommandNoArg("DIM    100", addr);

     assertTrue(cmd2 instanceof GroupValueWrite);

     KNXCommand knx2 = (KNXCommand)cmd2;

     Byte[] cemi2 = knx2.getCEMIFrame();

     assertTrue("Expected 0xFF but got " + Integer.toString(cemi2[KNXCommand.CEMI_DATA1_OFFSET], 16), cemi2[KNXCommand.CEMI_DATA1_OFFSET] == (byte)0xFF);
  }

  /**
   * Test missing command value argument with 'DIM'
   */
  @Test (expected = NoSuchCommandException.class)
  public void missingValueArgumentDim()
  {
    Command cmd = getCommandNoArg("dim", "10/1/10");
  }

  /**
   * Test missing command value argument with 'Scale'
   */
  @Test (expected = NoSuchCommandException.class)
  public void missingValueArgumentScale()
  {
    Command cmd = getCommandNoArg("Scale", "10/1/10");
  }


  /**
   * Tests "DIM" command equality.
   */
  @Test public void testDimEquals()
  {
    Command cmd1 = getCommand("dim", "10/1/10", 1);

    assertTrue(cmd1 instanceof GroupValueWrite);

    Command cmd2 = getCommand("DIM", "10/1/10", 1);

    assertTrue(cmd2 instanceof GroupValueWrite);


    assertTrue("DIM commands to same group address were not equal. See ORCJAVA-150", cmd1.equals(cmd2));
    assertTrue(cmd2.equals(cmd1));


    Command cmd3 = getCommandNoArg("ON", "10/1/10");

    Assert.assertFalse(cmd3.equals(cmd1));
    Assert.assertFalse(cmd1.equals(cmd3));
    Assert.assertFalse(cmd3.equals(cmd2));
    Assert.assertFalse(cmd2.equals(cmd3));


    Command cmd4 = getCommand("scale", "10/1/10", 99);

    Assert.assertFalse(cmd4.equals(cmd1));
    Assert.assertFalse(cmd4.equals(cmd2));
    Assert.assertFalse(cmd1.equals(cmd4));
    Assert.assertFalse(cmd2.equals(cmd4));


    Command cmd5 = getCommand("scale", "10/2/10", 100);

    Assert.assertFalse(cmd5.equals(cmd1));
    Assert.assertFalse(cmd5.equals(cmd2));
    Assert.assertFalse(cmd1.equals(cmd5));
    Assert.assertFalse(cmd2.equals(cmd5));


  }

  /**
   * Tests "Scale" command equality.
   */
  @Test public void testScaleEquals()
  {
    Command cmd1 = getCommand("scale", "10/1/10", 100);

    assertTrue(cmd1 instanceof GroupValueWrite);

    Command cmd2 = getCommand("SCALE", "10/1/10", 100);

    assertTrue(cmd2 instanceof GroupValueWrite);


    assertTrue("SCALE commands to same group address were not equal. See ORCJAVA-150", cmd1.equals(cmd2));
    assertTrue(cmd2.equals(cmd1));


    Command cmd3 = getCommandNoArg("SWITCH ON", "10/1/10");

    Assert.assertFalse(cmd3.equals(cmd1));
    Assert.assertFalse(cmd1.equals(cmd3));
    Assert.assertFalse(cmd3.equals(cmd2));
    Assert.assertFalse(cmd2.equals(cmd3));


    Command cmd4 = getCommand("scale", "10/1/10", 99);

    Assert.assertFalse(cmd4.equals(cmd1));
    Assert.assertFalse(cmd4.equals(cmd2));
    Assert.assertFalse(cmd1.equals(cmd4));
    Assert.assertFalse(cmd2.equals(cmd4));


    Command cmd5 = getCommand("scale", "10/2/10", 100);

    Assert.assertFalse(cmd5.equals(cmd1));
    Assert.assertFalse(cmd5.equals(cmd2));
    Assert.assertFalse(cmd1.equals(cmd5));
    Assert.assertFalse(cmd2.equals(cmd5));

  }



  /**
   * Tests "DIM" and "SCALE" command group address accessor
   */
  @Test public void testScaleAndDimGroupAddress()
  {
    Command cmd1 = getCommand("DIM", "4/4/4", 2);

    assertTrue(cmd1 instanceof GroupValueWrite);

    KNXCommand knx1 = (KNXCommand)cmd1;

    assertTrue(GroupAddress.formatToMainMiddleSub(knx1.getAddress().asByteArray()).equals("4/4/4"));



    Command cmd2 = getCommand("SCALE", "4/2/4", 10);

    assertTrue(cmd2 instanceof GroupValueWrite);

    KNXCommand knx2 = (KNXCommand)cmd2;

    assertTrue(GroupAddress.formatToMainMiddleSub(knx2.getAddress().asByteArray()).equals("4/2/4"));

  }




  /**
   * Tests "Dim" and "Scale" command DPT accessors
   */
  @Test public void testDimIncreaseDataTypeReturn()
  {
    Command cmd1 = getCommand("Dim", "5/5/5", 1);
    Command cmd2 = getCommand("SCALE", "30/6/254", 99);

    assertTrue(cmd1 instanceof GroupValueWrite);
    assertTrue(cmd2 instanceof GroupValueWrite);

    KNXCommand knx1 = (KNXCommand)cmd1;
    KNXCommand knx2 = (KNXCommand)cmd2;

    assertTrue(knx1.getDataPointType() == DataPointType.SCALING);
    assertTrue(knx2.getDataPointType() == DataPointType.SCALING);
  }




  /**
   * Tests the APDU data content for 'DIM' command.
   */
  @Test public void testDimAPDUData() throws InvalidGroupAddressException
  {
    GroupAddress addr = new GroupAddress("1/1/1");

    Command cmd = getCommand("Dim", addr, 50);

    assertTrue(cmd instanceof GroupValueWrite);

    KNXCommand knx = (KNXCommand)cmd;

    ApplicationProtocolDataUnit apdu = knx.getAPDU();

    assertTrue(apdu.getDataType() instanceof Unsigned8Bit);

    Unsigned8Bit u8 = (Unsigned8Bit)apdu.getDataType();

    assertTrue(u8.getData().length == 1);
    assertTrue(u8.getDataLength() == 2);

    assertTrue(
        "Expecting " + DataPointType.SCALING + ", got " + u8.getDataPointType(),
        u8.getDataPointType() == DataPointType.SCALING
    );

    assertTrue(
        "Expecting " +
        Strings.byteToUnsignedHexString((byte)Math.round(50 * 2.55)) +
        ", got " + Strings.byteToUnsignedHexString(u8.getData() [0]),
        u8.getData() [0] == Math.round(50 * 2.55)
    );
  }



  /**
   * Test PDU for dim command
   */
  @Test public void testDimPDU()
  {
    Command cmd = getCommand("DiM", "8/1/10", 75);

    assertTrue(cmd instanceof GroupValueWrite);

    KNXCommand knx = (KNXCommand)cmd;

    ApplicationProtocolDataUnit apdu = knx.getAPDU();

    assertTrue(apdu.getDataType() instanceof Unsigned8Bit);

    Byte[] pdu = apdu.getProtocolDataUnit();

    assertTrue(pdu.length == 3);
    assertTrue(pdu[0] == ApplicationLayer.Service.GROUPVALUE_WRITE.getTPCIAPCI());

    assertTrue(
        "Expecting " + Strings.byteToUnsignedHexString(
            (byte)ApplicationLayer.Service.GROUPVALUE_WRITE.getAPCIData()) +
        ", got " + Strings.byteToUnsignedHexString(pdu[1]),
        pdu[1] == (byte)ApplicationLayer.Service.GROUPVALUE_WRITE.getAPCIData());

    assertTrue(
        "Expecting " + Strings.byteToUnsignedHexString((byte)Math.round(75 * 2.55)) +
        ", got " + pdu[2],
        pdu[2] == (byte)Math.round(75 * 2.55)
    );

  }

  /**
   * Test APDU of dim
   */
  @Test public void testAPDU()
  {
    Command cmd1 = getCommand("dim", "1/1/1", 90);

    assertTrue(cmd1 instanceof GroupValueWrite);

    KNXCommand knx1 = (KNXCommand)cmd1;

    ApplicationProtocolDataUnit apdu1 = knx1.getAPDU();

    assertTrue(apdu1.getDataType() instanceof Unsigned8Bit);
    assertTrue(apdu1.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_WRITE);
    assertTrue(apdu1.getDataLength() == 2);
    assertTrue(apdu1.getDataPointType() == DataPointType.SCALING);

    Command cmd2 = getCommand("dim", "1/1/1", 33);

    assertTrue(cmd2 instanceof GroupValueWrite);

    KNXCommand knx2 = (KNXCommand)cmd2;

    ApplicationProtocolDataUnit apdu2 = knx2.getAPDU();

    assertTrue(apdu2.getDataType() instanceof Unsigned8Bit);
    assertTrue(apdu2.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_WRITE);
    assertTrue(apdu2.getDataLength() == 2);
    assertTrue(apdu2.getDataPointType() == DataPointType.SCALING);
  }


  /**
   * Test CEMI frame for dim increase command
   */
  @Test public void testCEMIFrame() throws InvalidGroupAddressException
  {
    GroupAddress addr = new GroupAddress("3/3/3");

    Command cmd = getCommand("dim", addr, 95);

    assertTrue(cmd instanceof GroupValueWrite);

    KNXCommand knx = (KNXCommand)cmd;

    Byte[] cemi = knx.getCEMIFrame();

    assertTrue(cemi.length == 12);

    assertTrue(cemi [0] == 0x11);
    assertTrue(cemi [1] == 0x00);

    assertTrue(
        "Expecting control1 bits 0x84, got " + Strings.byteToUnsignedHexString(cemi[2]),
        cemi [2] == (byte)(0x84 & 0xFF)
    );

    assertTrue(cemi [3] == (byte)(0xE0 & 0xFF));
    assertTrue(cemi [4] == 0x00);
    assertTrue(cemi [5] == 0x00);
    assertTrue(cemi [6] == 0x1B);
    assertTrue(cemi [7] == 0x03);
    assertTrue(cemi [8] == 0x02);

    assertTrue(
        "Expecting TPCI/APCI bits 0x00, got " + Strings.byteToUnsignedHexString(cemi[9]),
        cemi [9] == (byte)0x00
    );

    assertTrue(
        "Expecting APCI bits 0x80, got " + Strings.byteToUnsignedHexString(cemi[10]),
        cemi [10] == (byte)0x80
    );

    assertTrue(
        "Expecting data payload " + Strings.byteToUnsignedHexString((byte)Math.round(95 * 2.55)) +
        ", got " + Strings.byteToUnsignedHexString(cemi[11]),
        cemi [11] == (byte)Math.round(95 * 2.55)
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
                           DataPointType.SCALING.getDPTID());

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
                           DataPointType.SCALING.getDPTID());

    ele.addContent(propAddr3);


    return builder.build(ele);
  }


}

