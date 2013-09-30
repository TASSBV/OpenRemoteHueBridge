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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.openremote.controller.Constants;
import org.openremote.controller.protocol.bus.DatagramSocketMessage;
import org.openremote.controller.protocol.bus.Message;
import org.openremote.controller.protocol.bus.PhysicalBus;
import org.openremote.controller.protocol.bus.PhysicalBusFactory;
import org.openremote.controller.protocol.knx.ip.KnxIpException.Code;
import org.openremote.controller.protocol.knx.ip.message.IpConnectResp;
import org.openremote.controller.protocol.knx.ip.message.IpConnectionStateResp;
import org.openremote.controller.protocol.knx.ip.message.IpDisconnectResp;
import org.openremote.controller.protocol.knx.ip.message.IpDiscoverResp;
import org.openremote.controller.protocol.knx.ip.message.IpMessage;
import org.openremote.controller.protocol.knx.ip.message.IpMessage.Primitive;
import org.openremote.controller.protocol.knx.ip.message.IpTunnelingAck;
import org.openremote.controller.protocol.knx.ip.message.IpTunnelingReq;
import org.openremote.controller.utils.Logger;

/**
 * IP message processor, able to :
 * <ul>
 * <li>send requests,</li>
 * <li>synchronize requests and responses,</li>
 * <li>handle incoming requests.</li>
 * </ul>
 */
class IpProcessor {
   /**
    * A common log category name intended to be used across all classes related to KNX implementation.
    */
   public final static String KNXIP_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "knx.ip";

   private final static Logger log = Logger.getLogger(KNXIP_LOG_CATEGORY);
   private Object syncLock;
   private IpMessage con;
   private PhysicalBusListener busListener;
   private IpProcessorListener listener;
   private PhysicalBus bus;
   private DatagramSocket inSocket;
   private String physicalBusClazz;

   private class PhysicalBusListener extends Thread {

      public PhysicalBusListener(String name) {
         super("PhysicalBusListener for " + name);
      }

      @Override
      public void run() {
         synchronized (this) {
            this.notify();
         }
         while (!this.isInterrupted()) {
            try {
               Message b = IpProcessor.this.bus.receive();
               // TODO Check sender address?

               // Create an IpMessage from received data
               IpMessage m = IpProcessor.this.create(new ByteArrayInputStream(b.getContent()));

               // Handle Discovery responses specifically
               if (m instanceof IpDiscoverResp) {
                  IpProcessor.this.listener.notifyMessage(m);
               } else {
                  // Handle other messages
                  if (m.getPrimitive() == Primitive.RESP) {

                     // Handle responses
                     synchronized (IpProcessor.this.syncLock) {
                        IpProcessor.this.con = m;
                        IpProcessor.this.syncLock.notify();
                     }
                  } else {
                     // Handle requests
                     IpProcessor.this.listener.notifyMessage(m);
                  }
               }
            } catch (IOException x) {
               // Socket read error, stop thread
               log.info("KNX-IP socket listener IOException", x);
               Thread.currentThread().interrupt();
            } catch (KnxIpException x) {
               // Invalid message, stop thread
               log.warn("KNX-IP socket listener KnxIpException", x);
               Thread.currentThread().interrupt();
            }
         }
         log.warn("KNX-IP socket listener stopped");
      }
   }

   IpProcessor(IpProcessorListener listener, String physicalBusClazz) {
      this.syncLock = new Object();
      this.listener = listener;
      this.physicalBusClazz = physicalBusClazz;
   }

   void start(String src, InetAddress srcAddr, DatagramSocket outSocket) throws KnxIpException, IOException,
         InterruptedException {
      this.bus = PhysicalBusFactory.createPhysicalBus(this.physicalBusClazz);
      this.inSocket = new DatagramSocket(new InetSocketAddress(srcAddr, 0));

      // Start bus
      this.bus.start(this.inSocket, outSocket == null ? this.inSocket : outSocket);
      
      // Start bus listener
      this.busListener = new PhysicalBusListener(src);
      PhysicalBusFactory.createPhysicalBus(this.physicalBusClazz);
      synchronized (this.busListener) {
         this.busListener.start();
         this.busListener.wait();
      }
   }

   InetSocketAddress getSrcSocketAddr() {
      return (InetSocketAddress) this.inSocket.getLocalSocketAddress();
   }

   void stop() throws InterruptedException {
      // Stop bus
      this.bus.stop();

      // Stop IpListener
      this.busListener.interrupt();
      this.busListener.join();
   }

   synchronized IpMessage service(IpMessage message, InetSocketAddress destAddr) throws InterruptedException,
         IOException, KnxIpException {
      IpMessage out = null;
      synchronized (this.syncLock) {
         this.con = null;
         this.send(message, destAddr);
         long dt = 0;
         while (out == null && dt < message.getSyncSendTimeout()) {
            long st = System.currentTimeMillis();
            this.syncLock.wait(message.getSyncSendTimeout() - dt);
            dt += System.currentTimeMillis() - st;
            if (this.con != null && !(con instanceof IpDiscoverResp)) out = this.con;
         }
      }
      return out;
   }

   void send(IpMessage message, InetSocketAddress destAddr) throws IOException {
      this.send(message, destAddr, this.bus);
   }

   private void send(IpMessage message, InetSocketAddress destAddr, PhysicalBus bus) throws IOException {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      message.write(os);
      byte[] b = os.toByteArray();
      bus.send(new DatagramSocketMessage(destAddr, b));
   }

   private IpMessage create(InputStream is) throws IOException, KnxIpException {
      IpMessage out = null;

      // Check header is 0x06 0x10
      if ((is.read() != 0x06) || (is.read() != 0x10)) throw new KnxIpException(Code.invalidHeader,
            "Create message failed");

      // Extract Service Type Identifier
      int sti = (is.read() << 8) + is.read();

      // Extract message length
      int l = ((is.read() << 8) + is.read()) - 6;

      // Instantiate message
      switch (sti) {
      case IpConnectResp.STI:
         out = new IpConnectResp(is, l);
         break;
      case IpDisconnectResp.STI:
         out = new IpDisconnectResp(is, l);
         break;
      case IpDiscoverResp.STI:
         out = new IpDiscoverResp(is, l);
         break;
      case IpTunnelingAck.STI:
         out = new IpTunnelingAck(is, l);
         break;
      case IpTunnelingReq.STI:
         out = new IpTunnelingReq(is, l);
         break;
      case IpConnectionStateResp.STI:
         out = new IpConnectionStateResp(is, l);
         break;
      default:
         throw new KnxIpException(Code.unexpectedServiceType, "Could not create message");
      }
      return out;
   }
}
