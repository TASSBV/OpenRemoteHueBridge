package org.openremote.controller.protocol.knx;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

public class DummyServer extends Thread {
   public static final int PORT = 3670;
   private static final byte[] R1 = { 0x06, 0x10, 0x02, 0x06, 0x00, 0x12, 0x15, 0x00, 0x08, 0x01, 127, 0, 0, 1, 0x0E,
         0x56, 0x04, 0x04, 0x11, 0x0A };
   private static final byte[] R2 = { 0x06, 0x10, 0x02, 0x0A, 0x00, 0x08, 0x15, 0x00 };
   private static final byte[] R3 = { 0x06, 0x10, 0x04, 0x21, 0x00, 0x0A, 0x04, 0x15, 0x00, 0x00 };
   private DatagramSocket socket;
   private byte[] buffer;
   private String error;
   private InetAddress rAddr;
   private int rPort;

   public DummyServer() throws IOException {
      super("DummyServer");
      this.buffer = new byte[1024];
      this.socket = new MulticastSocket(PORT);
      this.error = null;
   }

   @Override
   public void run() {
      synchronized (this) {
         this.notify();
      }
      while (!this.isInterrupted()) {
         DatagramPacket p = new DatagramPacket(this.buffer, this.buffer.length);
         try {
            this.socket.receive(p);
            byte[] b = p.getData();
            switch ((b[2] << 8) + b[3]) {
            case 0x205:
               this.rAddr = InetAddress.getByAddress(new byte[] { b[8], b[9], b[10], b[11] });
               this.rPort = ((b[12] & 0xFF) << 8) + (b[13] & 0xFF);
               System.out.println("Server received connect request from " + this.rAddr + ":" + this.rPort);
               this.socket.send(new DatagramPacket(R1, R1.length, this.rAddr, this.rPort));
               break;
            case 0x209:
               int channelId = b[6];
               if (channelId != 0x15) this.setError("disconnect req wrong channel");
               InetAddress rA = InetAddress.getByAddress(new byte[] { b[10], b[11], b[12], b[13] });
               int rP = ((b[14] & 0xFF) << 8) + (b[15] & 0xFF);
               System.out.println("Server received disconnect request from " + rA + ":" + rP);
               this.socket.send(new DatagramPacket(R2, R2.length, rA, rP));
               break;
            case 0x420:
               channelId = b[7];
               if (channelId != 0x15) this.setError("tunneling req wrong channelId : " + channelId);
               byte[] r = Arrays.copyOf(R3, R3.length);
               r[8] = b[8];
               System.out.println("Server received tunneling request with channelId " + channelId + ", seq = " + r[8]);
               this.socket.send(new DatagramPacket(r, r.length, this.rAddr, this.rPort));
               break;
            default:
               this.setError("Server received unexpected request");
               System.out.println("Server received unexpected request");
            }
         } catch (IOException e) {
            this.setError(e.getMessage());
         }
      }
   }

   public String getError() {
      return this.error;
   }

   public void setError(String error) {
      this.error = error;
   }

   @Override
   public void interrupt() {
      this.socket.close();
      super.interrupt();
   }
}
