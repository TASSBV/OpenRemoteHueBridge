package org.openremote.controller.protocol.knx;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.command.CommandParameter;
import org.openremote.controller.exception.ConversionException;
import org.openremote.controller.protocol.knx.datatype.DataPointType;

public class RealKNXIpConnectionManagerTest {

   @Before
   public void setUp() throws IOException, InterruptedException {
   }

   @Test
   public void testDiscover() throws ConnectionException, IOException, InterruptedException, ConversionException {
      KNXIpConnectionManager mgr = new KNXIpConnectionManager();
      mgr.start();
      KNXConnection c = mgr.getConnection();
      c.send(GroupValueWrite.createCommand("on", DataPointType.BINARY_VALUE, mgr, new GroupAddress((byte) 0x80,
            (byte) 0x01), new CommandParameter("0")));
      c.send(GroupValueWrite.createCommand("off", DataPointType.BINARY_VALUE, mgr, new GroupAddress((byte) 0x80,
            (byte) 0x01), new CommandParameter("0")));
      mgr.stop();
   }
}
