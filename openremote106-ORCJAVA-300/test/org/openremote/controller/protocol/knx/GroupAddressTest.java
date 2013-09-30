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

/**
 * KNX GroupAddress unit tests
 * {@link org.openremote.controller.protocol.knx.GroupAddress}.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class GroupAddressTest
{

  @Test public void testGroupAddressFormattingBasic()
  {
    byte one = 0x00;
    byte two = 0x00;

    Assert.assertTrue(GroupAddress.formatToMainMiddleSub(new byte[] { one, two }).equals("0/0/0"));

    one = 0x00;
    two = 0x01;

    Assert.assertTrue(GroupAddress.formatToMainMiddleSub(new byte[] { one, two }).equals("0/0/1"));

    one = 0x01;
    two = 0x01;

    Assert.assertTrue(GroupAddress.formatToMainMiddleSub(new byte[] { one, two }).equals("0/1/1"));
  }

  @Test public void testGroupAddressFormattingHibits()
  {
    byte one = 0;
    byte two = -1;

    String address = GroupAddress.formatToMainMiddleSub(new byte[] { one, two });

    Assert.assertTrue(
        "Was expecting 0/0/255, got '" + address + "'.",
        address.equals("0/0/255")
    );

    one = -1;
    two = -1;

    address = GroupAddress.formatToMainMiddleSub(new byte[] { one, two });

    Assert.assertTrue(
        "Was expecting 31/7/255, got '" + address + "'.",
        address.equals("31/7/255")
    );
  }


  @Test public void basicConstructor() throws InvalidGroupAddressException
  {
    GroupAddress addr = new GroupAddress("0/0/0");

    byte[] b = addr.asByteArray();

    Assert.assertTrue(b[1] == 0);
    Assert.assertTrue(b[0] == 0);

    addr = new GroupAddress("1/1/1");

    b = addr.asByteArray();

    Assert.assertTrue("Was expecting 1, got " + b[1], b[1] == 1);
    Assert.assertTrue("Was expecting 9, got " + b[0], b[0] == 9);

    addr = new GroupAddress("31/7/255");

    b = addr.asByteArray();

    Assert.assertTrue("Was expecting -1 [1], got " + b[1], b[1] == -1);
    Assert.assertTrue("Was expecting -1 [0], got " + b[0], b[0] == -1);

  }


  @Test public void constructor2()
  {
    byte b1 = 0x00;
    byte b2 = 0x00;
    GroupAddress addr = new GroupAddress(b1, b2);

    byte[] bytes = addr.asByteArray();
    Assert.assertTrue(bytes[0] == b1);
    Assert.assertTrue(bytes[1] == b2);


    b1 = 0x01;
    b2 = 0x01;
    addr = new GroupAddress(b1, b2);

    bytes = addr.asByteArray();
    Assert.assertTrue(bytes[0] == b1);
    Assert.assertTrue(bytes[1] == b2);

    b1 = -128;
    b2 = -128;
    addr = new GroupAddress(b1, b2);

    bytes = addr.asByteArray();
    Assert.assertTrue(bytes[0] == b1);
    Assert.assertTrue(bytes[1] == b2);

    b1 = -1;
    b2 = -1;
    addr = new GroupAddress(b1, b2);

    bytes = addr.asByteArray();
    Assert.assertTrue(bytes[0] == b1);
    Assert.assertTrue(bytes[1] == b2);

    b1 = 127;
    b2 = 127;
    addr = new GroupAddress(b1, b2);

    bytes = addr.asByteArray();
    Assert.assertTrue(bytes[0] == b1);
    Assert.assertTrue(bytes[1] == b2);
    
  }

  
  @Test (expected = InvalidGroupAddressException.class)
  public void testOutOfBoundsMain() throws InvalidGroupAddressException
  {
    new GroupAddress("32/0/0");
  }

  @Test (expected = InvalidGroupAddressException.class)
  public void testOutOfBoundsMid() throws InvalidGroupAddressException
  {
    new GroupAddress("2/8/0");
  }

  @Test (expected = InvalidGroupAddressException.class)
  public void testOutOfBoundsSub() throws InvalidGroupAddressException
  {
    new GroupAddress("2/1/256");
  }

  @Test public void testValidRange()
  {
    for (int main = 0; main < 32; ++main)
    {
      for (int middle = 0; middle < 8; ++middle)
      {
        for (int sub = 0; sub < 256; ++sub)
        {
          byte mainmiddle     = (byte)(main << 3);
          mainmiddle = (byte)(mainmiddle + middle);
          byte subbyte = (byte)sub;

          GroupAddress addr = new GroupAddress(mainmiddle, subbyte);

          byte[] bytes = addr.asByteArray();

          int byte0 = bytes[0] & 0xFF;
          int byte1 = bytes[1] & 0xFF;

          int gotMain = (byte0 & 0xF8) >> 3;
          int gotMiddle = byte0 & 0x07;

          Assert.assertTrue("Was expecting main " + main + ", got " + gotMain, gotMain == main);
          Assert.assertTrue("Was expecting middle " + middle + ", got " + gotMiddle, gotMiddle == middle);
          Assert.assertTrue("Was expecting sub " + sub + ", got " + byte1, byte1 == sub);
        }
      }
    }
  }

  @Test (expected = InvalidGroupAddressException.class)
  public void testMainSubGroupAddressFormat() throws InvalidGroupAddressException
  {
    new GroupAddress("0/0");
  }


  @Test (expected = InvalidGroupAddressException.class)
  public void testUnformattedGroupAddress() throws InvalidGroupAddressException
  {
    new GroupAddress("1");
  }

  
  @Test public void testEquals() throws InvalidGroupAddressException
  {
    GroupAddress a1 = new GroupAddress("0/0/0");
    GroupAddress a2 = new GroupAddress("0/0/0");

    GroupAddress a3 = new GroupAddress("31/7/255");
    GroupAddress a4 = new GroupAddress("31/7/255");

    Assert.assertTrue("Expecting A1 to equals A2", a1.equals(a2));
    Assert.assertTrue("Expecting A2 to equal A1", a2.equals(a1));     // reflective

    Assert.assertFalse("Expecting A1 NOT to equal A3", a1.equals(a3));
    Assert.assertFalse("Expecting A3 NOT to equal A1", a3.equals(a1));

    Assert.assertTrue("Expecting A3 equals A4", a3.equals(a4));
    Assert.assertTrue("Expecting A4 equals A3", a4.equals(a3));

    Assert.assertFalse(a1.equals(null));
    Assert.assertFalse(a1.equals(new Object()));

  }

  @Test public void testHash() throws InvalidGroupAddressException
  {
    GroupAddress a1 = new GroupAddress("16/1/127");
    GroupAddress a2 = new GroupAddress("16/1/127");

    Assert.assertTrue(a1.hashCode() == a2.hashCode());
    Assert.assertTrue(a1.equals(a2));

  }

}
