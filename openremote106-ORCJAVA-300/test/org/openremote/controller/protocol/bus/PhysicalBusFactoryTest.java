package org.openremote.controller.protocol.bus;

import junit.framework.Assert;

import org.junit.Test;

public class PhysicalBusFactoryTest {
   @Test
   public void testCreatePhysicalBus() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
      PhysicalBus b = PhysicalBusFactory
            .createPhysicalBus("org.openremote.controller.protocol.bus.DatagramSocketPhysicalBus");
      Assert.assertTrue(b instanceof PhysicalBus);
   }
}
