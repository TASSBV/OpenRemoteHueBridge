package org.openremote.controller.protocol.knx.ip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.openremote.controller.protocol.bus.DatagramSocketMessage;
import org.openremote.controller.protocol.bus.Message;
import org.openremote.controller.protocol.bus.PhysicalBus;
import org.openremote.controller.protocol.knx.ip.message.IpConnectReq;
import org.openremote.controller.protocol.knx.ip.message.IpConnectionStateReq;
import org.openremote.controller.protocol.knx.ip.message.IpDisconnectReq;
import org.openremote.controller.protocol.knx.ip.message.IpDiscoverReq;
import org.openremote.controller.protocol.knx.ip.message.IpTunnelingAck;
import org.openremote.controller.protocol.knx.ip.message.IpTunnelingReq;

// TODO add a KNX response send mechanism
public class PhysicalBusMock implements PhysicalBus {
   private static final byte[] DISCOVER_RESP = new byte[] { 0x06, 0x10, 0x02, 0x02, 0x00, 0x0E, 0x08, 0x01, 127, 0, 0,
         1, 0x0E, 0x56, 0, 0 };
   private static final byte[] CONNECT_RESP = new byte[] { 0x06, 0x10, 0x02, 0x06, 0x00, 0x12, 0x15, 0x00, 0x08, 0x01,
         127, 0, 0, 1, 0x0E, 0x56, 0x04, 0x04, 0x11, 0x0A };
   private static final byte[] DISCONNECT_RESP = new byte[] { 0x06, 0x10, 0x02, 0x0A, 0x00, 0x08, 0x15, 0x00 };
   private static final byte[] CONNECTIONSTATE_RESP = new byte[] { 0x06, 0x10, 0x02, 0x08, 0x00, 0x08, 0x15, 0x00 };
   private static final byte[] TUNNELING_ACK = new byte[] { 0x06, 0x10, 0x04, 0x21, 0x00, 0x0A, 0x04, 0x15, 0x00, 0x00 };
   private InetSocketAddress srcAddr;
   private byte seq;
   private Queue<byte[]> notifications;

   public PhysicalBusMock() {
      this.notifications = new ConcurrentLinkedQueue<byte[]>();
   }

   @Override
   public void start(Object inChannel, Object outChannel) {
      this.srcAddr = (InetSocketAddress) ((DatagramSocket) inChannel).getLocalSocketAddress();
      this.seq = 0;
   }

   @Override
   public void stop() {
   }

   @Override
   public void send(Message message) throws IOException {
      byte[] req = message.getContent();
      int sti = (req[2] << 8) + req[3];
      switch (sti) {
      case IpDiscoverReq.STI:
         this.notifyTelegram(DISCOVER_RESP);
         break;
      case IpConnectReq.STI:
         this.notifyTelegram(CONNECT_RESP);
         break;
      case IpDisconnectReq.STI:
         this.notifyTelegram(DISCONNECT_RESP);
         break;
      case IpConnectionStateReq.STI:
         this.notifyTelegram(CONNECTIONSTATE_RESP);
         break;
      case IpTunnelingAck.STI:
         this.seq++;
         break;
      case IpTunnelingReq.STI:
         byte[] resp = TUNNELING_ACK;
         resp[8] = req[8];
         this.notifyTelegram(TUNNELING_ACK);

         // Notify a con telegram for GroupValue_Write.req and GroupValue_Read.req
         if ((((req[19] & 0x3) == 0) && ((req[20] & 0xC0) == 0x80))
               || (((req[19] & 0x3) == 0) && ((req[20] & 0xC0) == 0x00))) {
            
            // Prepare confirmation telegram
            byte[] con = Arrays.copyOfRange(req, 10, req.length);
            con[0] = 0x2E;
            IpTunnelingReq r = new IpTunnelingReq(req[7], this.seq, con);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            r.write(os);
            this.notifyTelegram(os.toByteArray());
         }
         break;
      }
   }

   private void notifyTelegram(byte[] message) {
      synchronized (this.notifications) {
         this.notifications.add(message);
         this.notifications.notify();
      }
   }

   @Override
   public Message receive() throws IOException {
      synchronized (this.notifications) {
         byte[] m = this.notifications.poll();
         if (m != null) return new DatagramSocketMessage(this.srcAddr, m);
         try {
            this.notifications.wait();
            return new DatagramSocketMessage(this.srcAddr, this.notifications.poll());
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         }
      }
      return null;
   }
}
