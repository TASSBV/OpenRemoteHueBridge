package org.openremote.controller.protocol.hsc40;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openremote.controller.utils.Logger;

public class Hsc40IpClient extends Thread {

   // Constants ------------------------------------------------------------------------------------
   public final static int HSC40_CONNECT_TIMEOUT = 3000;
   
   // Class Members --------------------------------------------------------------------------------
   private final static Logger logger = Logger.getLogger(Hsc40CommandBuilder.HSC40_PROTOCOL_LOG_CATEGORY);

   // Instance Fields ------------------------------------------------------------------------------
   private Socket clientSocket;
   final InputStream is;
   final OutputStream os;
   final HashMap<Integer, ZWaveDevice> statusDevices = new HashMap<Integer, ZWaveDevice>();

   // Constructors ----------------------------------------------------------------------------------
   public Hsc40IpClient(String ipAddress, int port) throws Exception {
      clientSocket = new Socket();
      InetSocketAddress sockAddr = new InetSocketAddress(ipAddress, port);
      clientSocket.connect(sockAddr, HSC40_CONNECT_TIMEOUT); 
      
      is = clientSocket.getInputStream();
      os = clientSocket.getOutputStream();
      this.start();
   }

   // Methods ------------------------------------------------------------------------------
   @Override
   public void run() {
      List<String> status = new ArrayList<String>();
      boolean useStatus = false;
      int d = 0;
      while (true) {
         try {
            d = is.read();
            if (d == 192) {  //Status starts with 'C0'
               useStatus = true;
            }
            if (useStatus) {
               status.add(String.format("%02X", d));
            }
            if (d == 193) {  //Status ends with 'C1'
               consumeStatus(status);
               status = new ArrayList<String>();
               useStatus = false;
            }
         } catch (Exception ex) {
            logger.error("Could not process HSC-40 input", ex);
         }
      }
   }

   /**
    * Take a list of hex strings and updates the status of each device which is in the statusDevices map.
    * 
    * Note: The HSC-40 protocol has a weird way of handling devices that send more then 1 byte status.
    * This is based on hard-coded device ID's and is not yet implemented.
    * 
    * @param status
    */
   private void consumeStatus(List<String> status) {
      logger.debug("Received HSC-40 status: " + status);
      if (!status.isEmpty()) {
         int deviceCount = (Integer.valueOf(status.get(2),16)-7) / 3;
         int start = 5;
         for (int i = 0; i < deviceCount; i++) {
            Integer mapKey = Integer.valueOf(status.get(start+1) + status.get(start), 16);
            if (statusDevices.get(mapKey) != null) {
               statusDevices.get(mapKey).setData(status.get(start+2));
            }
            start += 3;
         }
      }
   }

   public void sendBasicSetCommand(String data, String deviceId, String portId) {
      // byte[] dataBytes = "GET /r16716033,65535 HTTP/1.0\n\n".getBytes();
      // byte[] dataBytes = "GET /r4353,65535 HTTP/1.0\n\n".getBytes();
      int setData = Integer.valueOf(data + deviceId + portId, 16);
      String hsc40Command = "GET /r" + setData + ",65535 HTTP/1.0\n\n";
      byte[] dataBytes = hsc40Command.getBytes();
      for (int i = 0; i < dataBytes.length; i++) {
         try {
            os.write(dataBytes[i]);
         } catch (IOException e) {
            logger.error("Could not write command to HSC-40", e);
         }
      }
   }

   public void addStatusDevice(ZWaveDevice device) {
      Integer mapKey = Integer.valueOf(device.getDeviceId() + device.getPortId(), 16);
      if (statusDevices.get(mapKey) != null) {
         statusDevices.get(mapKey).addSensor(device.getSensors().get(0));
      } else {
         statusDevices.put(mapKey, device);
      }
   }

}
