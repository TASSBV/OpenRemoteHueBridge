package org.openremote.controller.protocol.knx.ip.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.protocol.knx.ip.message.Hpai;

public class HpaiTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testHpai() throws IOException {
    Hpai h1 = new Hpai(new InetSocketAddress(InetAddress.getByName("255.128.127.1"), 2555));
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    h1.write(os);
    byte[] r = os.toByteArray();
    Assert.assertArrayEquals(new byte[] { 0x08, 0x01, (byte) 255, (byte) 128, 127, 1, 0x09, (byte) 0xFB }, r);
    byte[] i = new byte[] { 0x08, 0x01, (byte) 255, (byte) 128, 127, 1, 0x09, (byte) 0xFB };
    ByteArrayInputStream is = new ByteArrayInputStream(i);
    Hpai h2 = new Hpai(is);
    Assert.assertEquals(new InetSocketAddress(InetAddress.getByName("255.128.127.1"), 2555), h2.getAddress());
  }
}
