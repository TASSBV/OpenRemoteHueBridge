/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.protocol.test.mockup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;

/**
 * A mockup command via HTTP.
 * 
 * @author handy.wang 2010-03-18
 *
 */
public class MockupCommand implements ExecutableCommand, StatusCommand {
   
   private String url;
   private Logger logger = Logger.getLogger(this.getClass().getName());

   public MockupCommand() {
      super();
   }

   public MockupCommand(String url) {
      super();
      this.url = url;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   @Override
   public void send() {
      BufferedReader in = null;
      StringBuffer result = new StringBuffer();
      try {
         URL url = new URL(getUrl());
         // logger.info("Had send executable command : " + getUrl());
         in = new BufferedReader(new InputStreamReader(url.openStream()));
         String str;
         while ((str = in.readLine()) != null) {
            result.append(str);
         }
         // logger.info("Received message: " + result);
      } catch (ConnectException ce) {
         logger.error("MockupExecutableCommand excute fail: " + ce.getMessage());
      } catch (Exception e) {
         logger.error("MockupExecutableCommand excute fail: " + e.getMessage());
      } finally {
         if (in != null) {
            try {
               in.close();
            } catch (IOException e) {
               logger.error("BufferedReader could not be closed", e);
            }
         }
      }
   }

   @SuppressWarnings("finally")
   @Override
   public String read(EnumSensorType sensorType, Map<String, String> stateMap) {
      BufferedReader in = null;
      StringBuffer result = new StringBuffer();
      try {
         URL url = new URL(getUrl());
         // logger.info("Had send status command : " + getUrl());
         in = new BufferedReader(new InputStreamReader(url.openStream()));
         String str;
         while ((str = in.readLine()) != null) {
            result.append(str);
         }
         // logger.info("Received message: " + result);
      } catch (ConnectException ce) {
         logger.error("MockupStatusCommand excute fail: " + ce.getMessage());
      } catch (Exception e) {
         logger.error("MockupStatusCommand excute fail: " + e.getMessage());
      } finally {
         if (in != null) {
            try {
               in.close();
            } catch (IOException e) {
               logger.error("BufferedReader could not be closed", e);
            }
         }
         return result.toString();
      }
   }
}
