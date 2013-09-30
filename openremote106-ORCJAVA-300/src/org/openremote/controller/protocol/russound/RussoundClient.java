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
package org.openremote.controller.protocol.russound;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.utils.Logger;

/**
 * This class is doing the communication with the Russound controller.
 * Only one instance is created during initialization of the RussoundCommandBuilder.
 * This class is a thread that is started in the constructor. The thread is used to listen for data on the InputStream.
 * When a Sensor is added for the first time a status request is sent to initialize the zone data.
 * A second Sensor for a different property of zone data is just updated by the already loaded zone data.
 * If a statusPollingInterval is given, a second thread is created which polls the Russound for it's status.
 * 
 * @author marcus
 *
 */
public class RussoundClient extends Thread {

   // Constants ------------------------------------------------------------------------------------
   public final static int SOCKET_CONNECT_TIMEOUT = 3000;
   
   // Class Members --------------------------------------------------------------------------------
   private final static Logger logger = Logger.getLogger(RussoundCommandBuilder.RUSSOUND_PROTOCOL_LOG_CATEGORY);

   // Instance Fields ------------------------------------------------------------------------------
   private int keypadId; 
   final InputStream is;
   final OutputStream os;
   final HashMap<String, ZoneData> zonesWithSensors = new HashMap<String, ZoneData>();

   // Constructors ----------------------------------------------------------------------------------
   public RussoundClient(String ipAddress, int port, String keypadId, String serialDevice, final int statusPollingInterval) throws Exception {
      super("Russound Client");
      this.keypadId = (byte) ((Character.digit(keypadId.charAt(0), 16) << 4) + Character.digit(keypadId.charAt(1), 16));
     
      if (serialDevice == null) {
         Socket clientSocket = new Socket();
         InetSocketAddress sockAddr = new InetSocketAddress(ipAddress, port);
         clientSocket.connect(sockAddr, SOCKET_CONNECT_TIMEOUT);
         is = clientSocket.getInputStream();
         os = clientSocket.getOutputStream();
      } else {
         CommPortIdentifier id = CommPortIdentifier.getPortIdentifier(serialDevice);
         SerialPort serialPort = (SerialPort) id.open("ORBController", 2000);
         serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
         is = serialPort.getInputStream();
         os = serialPort.getOutputStream();
      }
      
      this.start();

      //If a statusPollingInterval is defined in config.properties an extra thread for polling is creazed
      if (statusPollingInterval > 0) {
         
         Thread pollingThread = new Thread(new Runnable() {
            @Override
            public void run() {
               while(true) {
                  updateAllZones();
                  try { Thread.sleep(statusPollingInterval); } catch (InterruptedException e) { e.printStackTrace(); }
               }
            }
         });
         pollingThread.setName("Russound status polling");
         pollingThread.start();
      }
   }

   // Methods ------------------------------------------------------------------------------
   @Override
   public void run() {
      int avail = 0;
      int total = 0;
      byte[] data = new byte[1024];
      while (true) {
         try {
            avail = is.available();
            if (avail == 0) {
               try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
               continue;
            }
            int readCount = is.read(data, total, avail);
            total += readCount;

            if (data[total-1] == (byte)0xf7) {  //end-flag received, do some work with the data
               byte[] trimmedData = Arrays.copyOf(data, total);
               if ((trimmedData.length==34) && (trimmedData[0]==(byte)0xF0) && (trimmedData[9]==(byte)0x04)) {  //zone-status
                  consumeStatus(trimmedData);
               } else if ((trimmedData.length==24) && (trimmedData[0]==(byte)0xF0) && (trimmedData[9]==(byte)0x05) && (trimmedData[13]==(byte)0x00)) {  //turn-on-volume
                  consumeTurnonVolumeStatus(trimmedData);
               }
               data = new byte[1024];
               total = 0;
            }
         } catch (IOException e) {
            logger.error("Could not receive Russound data", e);
            e.printStackTrace();
         }
      }
   }

   private void consumeTurnonVolumeStatus(byte[] data) {
      String msg = String.format("Received Russound data: Controller:%1$02X Zone:%2$02X TurnOnVolume:%3$02X", data[4]+1, data[12]+1, data[21]);
      logger.debug(msg);
      
      String controller = Integer.toString(data[4]+1);
      String zone = Integer.toString(data[12]+1);
      int turnOnVolume = new Integer(data[21]) * 2;
      ZoneData zoneData = zonesWithSensors.get(controller+","+zone);
      if (zoneData != null) {
         zoneData.setTurnOnVolume(turnOnVolume);
      }
   }

   private void consumeStatus(byte[] data) {
      String msg = String.format("Received Russound data: Controller:%1$02X Zone:%2$02X Status:%3$02X src:%4$02X vol:%5$02X bass:%6$02X treb:%7$02X loud:%8$02X bal:%9$02X sys:%10$02X shrsrc:%11$02X party:%12$02X,DnD:%13$02X",
            data[4]+1, data[12]+1, data[20], data[21]+1, data[22], data[23], data[24], data[25], data[26], data[27], data[28], data[29], data[30]);
      logger.debug(msg);
         
      String controller = Integer.toString(data[4]+1);
      String zone = Integer.toString(data[12]+1);
      boolean powerStatus = (data[20] == 0x01);
      int selectedSource = new Integer(data[21]+1);
      int volume = new Integer(data[22]) * 2;
      int bassLevel = new Integer(data[23]) - 10;
      int trebleLevel = new Integer(data[24]) - 10;
      boolean loudness = (data[25] == 0x01);
      int balanceLevel = new Integer(data[26]) - 10;
      boolean systemOnState = (data[27] == 0x01);
      boolean sharedSource = (data[28] == 0x01);
      int partyMode = new Integer(data[29]);
      boolean doNotDisturb = (data[30] == 0x01);
           
      ZoneData zoneData = zonesWithSensors.get(controller+","+zone);
      if (zoneData != null) {
         zoneData.setPower(powerStatus);
         zoneData.setSource(selectedSource);
         zoneData.setVolume(volume);
         zoneData.setBalanceLevel(balanceLevel);
         zoneData.setBassLevel(bassLevel);
         zoneData.setDoNotDisturb(doNotDisturb);
         zoneData.setLoudness(loudness);
         zoneData.setPartyMode(partyMode);
         zoneData.setTrebleLevel(trebleLevel);
         zoneData.setSharedSource(sharedSource);
         zoneData.setSystemOnState(systemOnState);
         zoneData.setDataAvailable(true);
      }
   }

   private int russChecksum(int[] data) {
      int sum = 0;
      for (int i = 0; i < data.length-2; i++) {
         sum = sum + data[i];
      }
      sum = sum + data.length-2;
      int checksum = sum & 0x007F;
      return checksum;
   }
   
   private int[] hexStringToByteArray(String s) {
      int len = s.length();
      int[] data = new int[len / 2];
      for (int i = 0; i < len; i += 2) {
          data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
      }
      return data;
   }
  
   private void sendData(int[] data) {
      for (int i = 0; i < data.length; i++) {
         try {
            os.write(data[i]);
         } catch (IOException e) {
            logger.error("Could not write command to Russound", e);
         }
      }
   }

   public void addSensor(String controller, String zone, RussCmdEnum command, Sensor sensor) {
      String mapKey = controller+","+zone;
      if (zonesWithSensors.get(mapKey) != null) {
         zonesWithSensors.get(mapKey).addSensor(command, sensor);
      } else {
         ZoneData zoneWithSensor = new ZoneData();
         zoneWithSensor.addSensor(command, sensor);
         zonesWithSensors.put(mapKey, zoneWithSensor);
         requestStatus(controller, zone);
         try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
      }
   }
   
   public void removeSensor(String controller, String zone, RussCmdEnum command, Sensor sensor) {
      String mapKey = controller+","+zone;
      if (zonesWithSensors.get(mapKey) != null) {
         zonesWithSensors.get(mapKey).removeSensor(command, sensor);
      }
   }
   
   public void setAllOnOffPower(int power) {
      String setAllOnOffRequest = "F0 7E 00 7F 00 00 kk 05 02 02 00 00 F1 22 00 xx 00 00 00 01 00 F7";
      int[] data = hexStringToByteArray(setAllOnOffRequest.replaceAll(" ", "").toLowerCase());
      data[6] = keypadId;
      data[15] = power;
      data[data.length - 2] = russChecksum(data);
      sendData(data);
   }

   public void setPower(String controller, String zone, int power) {
      int cc = Integer.parseInt(controller)-1;
      int zz = Integer.parseInt(zone)-1;
      
      String setPowerRequest = "F0 cc 00 7F cc zz kk 05 02 02 00 00 F1 23 00 pp 00 zz 00 01 00 F7";
      int[] data = hexStringToByteArray(setPowerRequest.replaceAll(" ", "").toLowerCase());
      data[1] = cc;
      data[4] = cc;
      data[5] = zz;
      data[6] = keypadId;
      data[15] = power;
      data[17] = zz;
      data[data.length - 2] = russChecksum(data);
      sendData(data);
   }

   public void setVolume(String controller, String zone, String paramValue) {
      int cc = Integer.parseInt(controller)-1;
      int zz = Integer.parseInt(zone)-1;
      int volume = Integer.parseInt(paramValue)/2;
      
      String setVolumeRequest = "F0 cc 00 7F cc zz kk 05 02 02 00 00 F1 21 00 vv 00 zz 00 01 00 F7";
      int[] data = hexStringToByteArray(setVolumeRequest.replaceAll(" ", "").toLowerCase());
      data[1] = cc;
      data[4] = cc;
      data[5] = zz;
      data[6] = keypadId;
      data[15] = volume;
      data[17] = zz;
      data[data.length - 2] = russChecksum(data);
      sendData(data);
   }
   
   public void setVolumeUp(String controller, String zone) {
      int cc = Integer.parseInt(controller)-1;
      int zz = Integer.parseInt(zone)-1;
      
      String setVolumeUpRequest = "F0 cc 00 7F cc zz kk 05 02 02 00 00 7F 00 00 00 00 00 01 00 F7";
      int[] data = hexStringToByteArray(setVolumeUpRequest.replaceAll(" ", "").toLowerCase());
      data[1] = cc;
      data[4] = cc;
      data[5] = zz;
      data[6] = keypadId;
      data[data.length - 2] = russChecksum(data);
      sendData(data);
   }

   public void setVolumeDown(String controller, String zone) {
      int cc = Integer.parseInt(controller)-1;
      int zz = Integer.parseInt(zone)-1;
      
      String setVolumeUpRequest = "F0 cc 00 7F cc zz kk 05 02 02 00 00 F1 7F 00 00 00 00 00 01 00 F7";
      int[] data = hexStringToByteArray(setVolumeUpRequest.replaceAll(" ", "").toLowerCase());
      data[1] = cc;
      data[4] = cc;
      data[5] = zz;
      data[6] = keypadId;
      data[data.length - 2] = russChecksum(data);
      sendData(data);
   }
   
   public void setSource(String controller, String zone, String paramValue) {
      int cc = Integer.parseInt(controller)-1;
      int zz = Integer.parseInt(zone)-1;
      int source = Integer.parseInt(paramValue) - 1;
      
      String setSourceRequest = "F0 cc 00 7F cc zz kk 05 02 00 00 00 F1 3E 00 00 00 ss 00 01 00 F7";
      int[] data = hexStringToByteArray(setSourceRequest.replaceAll(" ", "").toLowerCase());
      data[1] = cc;
      data[4] = cc;
      data[5] = zz;
      data[6] = keypadId;
      data[17] = source;
      data[data.length - 2] = russChecksum(data);
      sendData(data);
   }


   public void setSettings(String controller, String zone, RussCmdEnum command, String value) {
      int cc = Integer.parseInt(controller)-1;
      int zz = Integer.parseInt(zone)-1;
      int xx = 00;
      
      String setSettingsRequest = "F0 cc 00 7F cc zz kk 00 05 02 00 zz 00 00 00 00 00 01 00 01 00 xx 00 F7";
      int[] data = hexStringToByteArray(setSettingsRequest.replaceAll(" ", "").toLowerCase());
      data[1] = cc;
      data[4] = cc;
      data[5] = zz;
      data[6] = keypadId;
      data[11] = zz;
      
      switch (command) {
         case SET_LOUDNESS_OFF:
         case SET_LOUDNESS_ON:
            xx = Integer.parseInt(value);
            data[13] = 0x02;
            break;
         case SET_PARTYMODE_OFF:
         case SET_PARTYMODE_ON:
            xx = Integer.parseInt(value);
            data[13] = 0x07;
            break;
         case SET_BASS_LEVEL:
            xx = Integer.parseInt(value) + 10;
            break;
         case SET_TREBLE_LEVEL:
            xx = Integer.parseInt(value) + 10;
            data[13] = 0x01;
            break;
         case SET_BALANCE_LEVEL:
            xx = Integer.parseInt(value) + 10;
            data[13] = 0x03;
            break;
         case SET_TURNON_VOLUME:
            xx = Integer.parseInt(value)/2;
            data[13] = 0x04;
            break;
      }
      data[21] = xx;
      
      data[data.length - 2] = russChecksum(data);
      sendData(data);
      
   }
   
   public void updateAllZones() {
      for (String zoneIdentifier : zonesWithSensors.keySet()) {
         try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
         String controller = zoneIdentifier.substring(0, 1);
         String zone = zoneIdentifier.substring(2, 3);
         requestStatus(controller, zone);
      }
   }
   
   public void requestStatus(String controller, String zone) {
      int cc = Integer.parseInt(controller)-1;
      int zz = Integer.parseInt(zone)-1;
      
      String turnOnVolumeRequest = "F0 cc 00 7F cc zz kk 01 05 02 00 zz 00 04 00 00 00 F7";
      int[] data = hexStringToByteArray(turnOnVolumeRequest.replaceAll(" ", "").toLowerCase());
      data[1] = cc;
      data[4] = cc;
      data[5] = zz;
      data[6] = keypadId;
      data[11] = zz;
      data[data.length - 2] = russChecksum(data);
      sendData(data);  //request zone turnonVolume
      
      try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
      
      String status = "F0 cc 00 7F cc zz kk 01 04 02 00 zz 07 00 00 00 F7";
      data = hexStringToByteArray(status.replaceAll(" ", "").toLowerCase());
      data[1] = cc;
      data[4] = cc;
      data[5] = zz;
      data[6] = keypadId;
      data[11] = zz;
      data[data.length - 2] = russChecksum(data);
      sendData(data);  //request zone status
   }


}
