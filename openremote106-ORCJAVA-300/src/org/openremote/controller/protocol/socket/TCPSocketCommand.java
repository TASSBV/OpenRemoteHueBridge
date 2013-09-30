/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.*;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.model.sensor.Sensor;

/**
 * TODO
 *
 * @author Marcus Redeker 2009-4-26
 * @author Phillip Lavender
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Ivan Martinez
 */
public class TCPSocketCommand implements ExecutableCommand, StatusCommand {

   /** The logger. */
   private static Logger logger = Logger.getLogger(TCPSocketCommand.class.getName());

   /** A name to identify event in controller.xml. */
   private String name;

   /** A pipe separated list of command string that are sent over the socket */
   private String command;

   /** The IP to which the socket is opened */
   private String ip;

   /** The port that is opened */
   private String port;


   /**
    * Gets the command.
    *
    * @return the command
    */
   public String getCommand() {
      return command;
   }

   /**
    * Sets the command.
    *
    * @param command the new command
    */
   public void setCommand(String command) {
      this.command = command;
   }

   /**
    * Gets the name.
    *
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the name.
    *
    * @param name the new name
    */
   public void setName(String name) {
      this.name = name;
   }


   /**
    * Gets the ip
    * @return the ip
    */
   public String getIp() {
      return ip;
   }

   /**
    * Sets the ip
    * @param ip the new ip
    */
   public void setIp(String ip) {
      this.ip = ip;
   }

   /**
    * Gets the port
    * @return the port
    */
   public String getPort() {
      return port;
   }

	/**
	 * Sets the port
	 * @param port the new port
	 */
   public void setPort(String port) {
      this.port = port;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void send() {
      requestSocket();
   }

   private String requestSocket() {
      Socket socket = null;
      try {
         socket = new Socket(getIp(), Integer.parseInt(getPort()));
         OutputStream out = socket.getOutputStream();

         StringTokenizer st = new StringTokenizer(getCommand(), "|");
         while (st.hasMoreElements()) {
            String cmd = (String) st.nextElement();
            byte[] bytes;
            if (cmd.startsWith("0x")) {
               String tmp = getCommand().substring(2);
               bytes = hexStringToByteArray(tmp.replaceAll(" ", "").toLowerCase());
            } else {
               bytes = (cmd + "\r").getBytes();
            }
            out.write(bytes);
         }

         String result = readReply(socket);
         logger.info("received message: " + result);
         return result;
      } catch (Exception e) {
         logger.error("Socket event could not execute", e);
      } finally {
         if (socket != null) {
            try {
               socket.close();
            } catch (IOException e) {
               logger.error("Socket could not be closed", e);
            }
         }
      }
      return "";
   }

   private String readReply(java.net.Socket socket) throws IOException {
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      char[] buffer = new char[200];
      int readChars = bufferedReader.read(buffer, 0, 200); // blocks until message received
      if (readChars > 0) {
         String reply = new String(buffer, 0, readChars);
         return reply;
      } else {
         return "";
      }
   }

   
   private byte[] hexStringToByteArray(String s) {
      int len = s.length();
      byte[] data = new byte[len / 2];
      for (int i = 0; i < len; i += 2) {
          data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
      }
      return data;
   }


  @Override public String read(EnumSensorType sensorType, Map<String, String> stateMap)
  {
    String regexResult = null;
    String strState = null;

    // Write the command to socket...
    //
    // TODO : rather poorly named method

    String rawResult = requestSocket();

    // Strip response from control characters (non-ASCII)...
    //
    // Patch provided by Phillip Lavender

    Pattern p = Pattern.compile("\\p{Cntrl}");
    Matcher m = p.matcher(rawResult);
    regexResult = m.replaceAll("");
	   
    if ("".equals(regexResult))
    {
       return UNKNOWN_STATUS;
    }

    switch (sensorType)
    {
      case RANGE:
         break;

      case LEVEL:
         String min = stateMap.get(Sensor.RANGE_MIN_STATE);
         String max = stateMap.get(Sensor.RANGE_MAX_STATE);

         try
         {
            int val = Integer.valueOf(regexResult);

            if (min != null && max != null)
            {
               int minVal = Integer.valueOf(min);
               int maxVal = Integer.valueOf(max);

               return String.valueOf(100 * (val - minVal)/ (maxVal - minVal));
            } 
         }

         catch (ArithmeticException e)
         {
            logger.warn("Level sensor values cannot be parsed: " + e.getMessage(), e);
         }

         break;

      default://NOTE: if sensor type is RANGE, this map only contains min/max states.

        // If custom sensor type has been configured, map the 'raw' return value to configured
        // 'wanted' return value
        //
        // TODO :
        //   no reason to put this implementation burden on protocol implementations, the
        //   calling code could do it instead

        for (String state : stateMap.keySet())
        {
          strState = stateMap.get(state);

          if (regexResult.equals(strState))
          {
            return state;
          }
        }
      }


      return regexResult;
  }

}
