/*
* Copyright (C) 2011 Tom Quist
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You can get the GNU General Public License at
* http://www.gnu.org/licenses/gpl.html
* 
* The code is slightly modified for Java and to fit OpenRemote's needs.
*/
package org.openremote.controller.protocol.samsungtv;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.openremote.controller.utils.Logger;

/**
 * 
 * @author marcus
 *
 */
public class SamsungTVSession {

   // Constants ------------------------------------------------------------------------------------
   public final static int SAMSUNG_CONNECT_TIMEOUT = 3000;
   
   private static final String APP_STRING = "iphone.iapp.samsung";
   private static final String TV_APP_STRING = "iphone..iapp.samsung";

   private static final char[] ALLOWED_BYTES = new char[] { 0x64, 0x00, 0x01, 0x00 };
   private static final char[] DENIED_BYTES = new char[] { 0x64, 0x00, 0x00, 0x00 };
   private static final char[] TIMEOUT_BYTES = new char[] { 0x65, 0x00 };

   public static final String ALLOWED = "ALLOWED";
   public static final String DENIED = "DENIED";
   public static final String TIMEOUT = "TIMEOUT";

   // Class Members --------------------------------------------------------------------------------
   private final static Logger logger = Logger
         .getLogger(SamsungTVRemoteCommandBuilder.SAMSUNG_TV_PROTOCOL_LOG_CATEGORY);

   // Instance Fields ------------------------------------------------------------------------------
   private String ipAddress;
   private int port;
   private String applicationName;

   private Socket socket;
   private InputStreamReader reader;
   private BufferedWriter writer;

   public SamsungTVSession(String samsungTVIp, int port, String applicationName) {
      this.ipAddress = samsungTVIp;
      this.port = port;
      this.applicationName = applicationName;

      String result = null;
      try {
         result = this.initialize();
      } catch (Exception e) {
         throw new RuntimeException("Could not create Samsung TV session : " + ipAddress+":"+port, e);
      }

      if (result.equals(DENIED)) {
         throw new RuntimeException("Samsung TV denied the connection : " + ipAddress+":"+port);
      } else if (result.equals(TIMEOUT)) {
         throw new RuntimeException("Samsung TV did not reply on init request on time : " + ipAddress+":"+port);
      }
   }

   private String initialize() throws Exception {
      logger.debug("Creating socket for host " + ipAddress + " on port " + port);
      
      socket = new Socket();
      InetSocketAddress sockAddr = new InetSocketAddress(ipAddress, port);
      socket.connect(sockAddr, SAMSUNG_CONNECT_TIMEOUT); 
      
      logger.debug("Socket successfully created and connected");
      InetAddress localAddress = socket.getLocalAddress();
      logger.debug("Local address is " + localAddress.getHostAddress());

      logger.debug("Sending registration message");
      writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      writer.append((char) 0x00);
      writeText(writer, APP_STRING);
      writeText(writer, getRegistrationPayload(localAddress.getHostAddress()));
      writer.flush();

      InputStream in = socket.getInputStream();
      reader = new InputStreamReader(in);
      String result = readRegistrationReply(reader);
      int i;
      while ((i = in.available()) > 0) {
         in.skip(i);
      }
      return result;
   }

   private static Writer writeText(Writer writer, String text) throws IOException {
      return writer.append((char) text.length()).append((char) 0x00).append(text);
   }

   private String getRegistrationPayload(String ip) throws IOException {
      StringWriter writer = new StringWriter();
      writer.append((char) 0x64);
      writer.append((char) 0x00);
      writeBase64Text(writer, ip);
      writeBase64Text(writer, ip);
      writeBase64Text(writer, applicationName);
      writer.flush();
      return writer.toString();
   }

   private String readRegistrationReply(Reader reader) throws IOException {
      logger.debug("Reading registration reply");
      reader.read(); // Unknown byte 0x02
      String text1 = readText(reader); // Read "unknown.livingroom.iapp.samsung" for new RC and "iapp.samsung" for
      // already registered RC
      logger.debug("Received ID: " + text1);
      char[] result = readCharArray(reader); // Read result sequence
      if (Arrays.equals(result, ALLOWED_BYTES)) {
         logger.debug("Registration successfull");
         return ALLOWED;
      } else if (Arrays.equals(result, DENIED_BYTES)) {
         logger.debug("Registration denied");
         return DENIED;
      } else if (Arrays.equals(result, TIMEOUT_BYTES)) {
         logger.debug("Registration timed out");
         return TIMEOUT;
      } else {
         StringBuilder sb = new StringBuilder();
         for (char c : result) {
            sb.append(Integer.toHexString(c));
            sb.append(' ');
         }
         String hexReturn = sb.toString();
         if (logger != null) {
            logger.debug("Received unknown registration reply: " + hexReturn);
         }
         return hexReturn;
      }
   }

   private static String readText(Reader reader) throws IOException {
      char[] buffer = readCharArray(reader);
      return new String(buffer);
   }

   private static char[] readCharArray(Reader reader) throws IOException {
      if (reader.markSupported()) reader.mark(1024);
      int length = reader.read();
      int delimiter = reader.read();
      if (delimiter != 0) {
         if (reader.markSupported()) reader.reset();
         throw new IOException("Unsupported reply exception");
      }
      char[] buffer = new char[length];
      reader.read(buffer);
      return buffer;
   }

   private static Writer writeBase64Text(Writer writer, String text) throws IOException {
      byte[] b64 = Base64.encodeBase64(text.getBytes());
      return writeText(writer, new String(b64));
   }

   private void internalSendKey(Key key) throws IOException {
      writer.append((char) 0x00);
      writeText(writer, TV_APP_STRING);
      writeText(writer, getKeyPayload(key));
      writer.flush();
      int i = reader.read(); // Unknown byte 0x00
      String t = readText(reader); // Read "iapp.samsung"
      char[] c = readCharArray(reader);
      logger.debug("Data received after sending key: '" + i + "' - '" + t + "' - '" + new String(c) + "'" );
   }

   public void sendKey(Key key) throws Exception {
      logger.debug("Sending key " + key.getValue() + "...");
      checkConnection();
      try {
         internalSendKey(key);
      } catch (SocketException e) {
         logger.debug("Could not send key because the server closed the connection. Reconnecting...");
         initialize();
         logger.debug("Sending key " + key.getValue() + " again...");
         internalSendKey(key);
      }
      logger.debug("Successfully sent key " + key.getValue());
   }

   private String getKeyPayload(Key key) throws IOException {
      StringWriter writer = new StringWriter();
      writer.append((char) 0x00);
      writer.append((char) 0x00);
      writer.append((char) 0x00);
      writeBase64Text(writer, key.getValue());
      writer.flush();
      return writer.toString();
   }

   private void checkConnection() throws Exception {
      if (socket.isClosed() || !socket.isConnected()) {
         logger.debug("Connection closed, trying to reconnect...");
         try {
            socket.close();
         } catch (IOException e) {
            // Ignore any exception
         }
         initialize();
         logger.debug("Reconnected to server");
      }
   }

   public void destroy() {
      try {
         socket.close();
      } catch (IOException e) {
         // Ignore exception
      }
   }

}
