/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2011, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.knx.ip;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import org.openremote.controller.protocol.knx.ip.KnxIpException.Code;
import org.openremote.controller.protocol.knx.ip.message.Hpai;
import org.openremote.controller.protocol.knx.ip.message.IpDiscoverReq;
import org.openremote.controller.protocol.knx.ip.message.IpDiscoverResp;
import org.openremote.controller.protocol.knx.ip.message.IpMessage;

public class IpDiscoverer implements IpProcessorListener {
   private IpProcessor processor;
   private DiscoveryListener discoveryListener;
   private InetAddress srcAddr;

   public IpDiscoverer(InetAddress srcAddr, DiscoveryListener discoveryListener, String physicalBusClazz) {
      this.discoveryListener = discoveryListener;
      this.srcAddr = srcAddr;
      this.processor = new IpProcessor(this, physicalBusClazz);
   }

   public void start() throws KnxIpException, IOException, InterruptedException {
      InetAddress multicastAddr = InetAddress.getByName("224.0.23.12");
      this.processor.start("discovery", this.srcAddr, IpDiscoverer.getOutSocket(srcAddr, multicastAddr));

      // Start discovery process
      // TODO send regularly requests until a response is received
      this.processor.send(new IpDiscoverReq(new Hpai((InetSocketAddress) this.processor.getSrcSocketAddr())),
            new InetSocketAddress(multicastAddr, 3671));
   }

   public void stop() throws InterruptedException {
      this.processor.stop();
   }

   @Override
   public void notifyMessage(IpMessage message) {
      // Handle discovery responses only, ignore other messages
      if (message instanceof IpDiscoverResp) {
         this.discoveryListener.notifyDiscovery(this, ((IpDiscoverResp) message).getControlEndpoint().getAddress());
      }
   }

   public InetAddress getSrcAddr() {
      return this.processor.getSrcSocketAddr().getAddress();
   }

   private static DatagramSocket getOutSocket(InetAddress srcAddr, InetAddress multicastAddr) throws IOException,
         KnxIpException {
      DatagramSocket s = null;
      if (multicastAddr != null) {
         s = new MulticastSocket(new InetSocketAddress(srcAddr, 0));
         try {
            ((MulticastSocket) s).joinGroup(multicastAddr);
         } catch (UnknownHostException e) {
            throw new KnxIpException(Code.unknownHost, e.getMessage());
         }
      }
      return s;
   }
}
