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
package org.openremote.controller.protocol.wol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;

public class WakeOnLanCommand implements ExecutableCommand {

   /** The logger. */
   private static Logger logger = Logger.getLogger(WakeOnLanCommandBuilder.WOL_PROTOCOL_LOG_CATEGORY);

   /** The MAC-Address to which the WOL package is sent */
   private String macAddress;

   /** The brodcast IP into which net the WOL package is sent */
   private String broadcastIp;

   public static final int PORT = 9;

   /**
    * WakeOnLanCommand is a protocol to wakeup PC's via a magic packet from the network.
    * @param macAddress
    * @param broadcastIp
    */
   public WakeOnLanCommand(String macAddress, String broadcastIp) {
      this.broadcastIp = broadcastIp;
      this.macAddress = macAddress;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public void send() {
      logger.debug("Will send WOL magic packet to broadcast IP: " + broadcastIp + " and use MAC-Address: " + macAddress);
      try {
         byte[] macBytes = getMacBytes(macAddress);
         byte[] bytes = new byte[6 + 16 * macBytes.length];
         for (int i = 0; i < 6; i++) {
            bytes[i] = (byte) 0xff;
         }
         for (int i = 6; i < bytes.length; i += macBytes.length) {
            System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
         }

         InetAddress address = InetAddress.getByName(broadcastIp);
         DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
         DatagramSocket socket = new DatagramSocket();
         socket.send(packet);
         socket.close();

         logger.debug("Wake-on-LAN packet sent.");
      } catch (Exception e) {
         logger.error("Failed to send Wake-on-LAN packet", e);
      }
   }

   private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
      byte[] bytes = new byte[6];
      String[] hex = macStr.split("(\\:|\\-)");
      if (hex.length != 6) {
         throw new IllegalArgumentException("Invalid MAC address.");
      }
      try {
         for (int i = 0; i < 6; i++) {
            bytes[i] = (byte) Integer.parseInt(hex[i], 16);
         }
      } catch (NumberFormatException e) {
         throw new IllegalArgumentException("Invalid hex digit in MAC address.");
      }
      return bytes;
   }

   public String getMacAddress() {
      return macAddress;
   }

   public String getBroadcastIp() {
      return broadcastIp;
   }

}
