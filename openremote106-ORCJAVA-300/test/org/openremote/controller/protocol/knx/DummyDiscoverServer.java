package org.openremote.controller.protocol.knx;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class DummyDiscoverServer extends Thread {
   public static final int PORT = 3671;
   private static final byte[] R1 = { 0x06, 0x10, 0x02, 0x02, 0x00, 0x0E, 0x08, 0x01, 127, 0, 0, 1, 0x0E, 0x56, 0, 0 };
   private MulticastSocket discoverSocket;
   private byte[] buffer;

   public DummyDiscoverServer() throws IOException {
      super("DummyDiscoverServer");
      this.buffer = new byte[1024];
      this.discoverSocket = new MulticastSocket(PORT);
      this.discoverSocket.joinGroup(InetAddress.getByName("224.0.23.12"));
   }

   @Override
   public void run() {
      synchronized (this) {
         this.notify();
      }
      while (!this.isInterrupted()) {
         DatagramPacket p = new DatagramPacket(this.buffer, this.buffer.length);
         try {
            this.discoverSocket.receive(p);
            byte[] req = p.getData();
            InetAddress rAddr = InetAddress.getByAddress(new byte[] { req[8], req[9], req[10], req[11] });
            int rPort = ((req[12] & 0xFF) << 8) + (req[13] & 0xFF);
            System.out.println("Discover server received request from " + rAddr + ":" + rPort);
            DatagramSocket s = new DatagramSocket(3672);
            s.send(new DatagramPacket(R1, R1.length, rAddr, rPort));
         } catch (IOException e) {
            System.out.println("Discover server caught " + e.getMessage());
         }
      }
   }

   @Override
   public void interrupt() {
      this.discoverSocket.close();
      super.interrupt();
   }
}
