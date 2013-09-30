/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.controller.protocol.rrdgraphurl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.utils.Strings;

/**
 * OpenRemote rrdGraphUrl command implementation. This is a virtual command which is used to manipulate the graph url
 * for the RRD4j grap servlet.
 * <p>
 * 
 * @author <a href="mailto:marcus@openremote.org">Marcus Redeker</a>
 */
public class RrdGraphUrlCommand implements ExecutableCommand, EventListener {

   // Class Members --------------------------------------------------------------------------------

   /**
    * Logging. Use common log category for all related classes.
    */
   private final static Logger log = Logger.getLogger(RrdGraphUrlCommandBuilder.LOG_CATEGORY);

   private final static Map<String, RrdGraphUrlCommand> graphUrls = new ConcurrentHashMap<String, RrdGraphUrlCommand>(20);

   private final static SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HH-mm");

   // Instance Fields ------------------------------------------------------------------------------

   private String graphName = null;
   private String command = null;
   private String width = null;
   private String height = null;
   private String start = null;
   private String end = null;
   private String ip = null;
   private String port = null;

   /** The sensor which is updated */
   private Sensor sensor;

   // Constructors ---------------------------------------------------------------------------------

   public RrdGraphUrlCommand(String graphName, String command, String width, String height, String start, String end,
         String ip, String port) {
      this.graphName = graphName;
      this.command = command;
      this.width = width;
      this.height = height;
      this.start = start;
      this.end = end;
      this.ip = ip;
      this.port = port;
   }

   // Implements ExecutableCommand -----------------------------------------------------------------
   @Override
   public void send() {
      RrdGraphUrlCommand tmp = RrdGraphUrlCommand.graphUrls.get(graphName);
      try {
         GregorianCalendar startOld = new GregorianCalendar();
         GregorianCalendar endOld = new GregorianCalendar();
         Date now = new Date();
         if (tmp.start.startsWith("+")) {
            startOld.setTime(new Date(now.getTime() + Strings.convertPollingIntervalString(tmp.start.substring(1))));
         } else if (tmp.start.startsWith("-")) {
            startOld.setTime(new Date(now.getTime() - Strings.convertPollingIntervalString(tmp.start.substring(1))));
         } else {
            startOld.setTime(df.parse(tmp.start));
         }
         if (tmp.end.startsWith("+")) {
            endOld.setTime(new Date(now.getTime()+Strings.convertPollingIntervalString(tmp.end.substring(1))));
         } else if (tmp.end.startsWith("-")) {
            endOld.setTime(new Date(now.getTime()-Strings.convertPollingIntervalString(tmp.end.substring(1))));
         } else {
            endOld.setTime(df.parse(tmp.end));
         }
         
         if (command.equalsIgnoreCase("startMinus1Day")) {
            startOld.add(Calendar.DAY_OF_MONTH, -1);
         } else if (command.equalsIgnoreCase("startMinus1Hour")) {
            startOld.add(Calendar.HOUR_OF_DAY, -1);
         } else if (command.equalsIgnoreCase("startMinus1Month")) {
            startOld.add(Calendar.MONTH, -1);
         } else if (command.equalsIgnoreCase("startMinus1Year")) {
            startOld.add(Calendar.YEAR, -1);
         } else if (command.equalsIgnoreCase("endMinus1Hour")) {
            endOld.add(Calendar.HOUR_OF_DAY, -1);
         } else if (command.equalsIgnoreCase("endMinus1Day")) {
            endOld.add(Calendar.DAY_OF_MONTH, -1);
         } else if (command.equalsIgnoreCase("endMinus1Month")) {
            endOld.add(Calendar.MONTH, -1);
         } else if (command.equalsIgnoreCase("endMinus1Year")) {
            endOld.add(Calendar.YEAR, -1);
         } else if (command.equalsIgnoreCase("startPlus1Day")) {
            startOld.add(Calendar.DAY_OF_MONTH, 1);
         } else if (command.equalsIgnoreCase("startPlus1Hour")) {
            startOld.add(Calendar.HOUR_OF_DAY, 1);
         } else if (command.equalsIgnoreCase("startPlus1Month")) {
            startOld.add(Calendar.MONTH, 1);
         } else if (command.equalsIgnoreCase("startPlus1Year")) {
            startOld.add(Calendar.YEAR, 1);
         } else if (command.equalsIgnoreCase("endPlus1Hour")) {
            endOld.add(Calendar.HOUR_OF_DAY, 1);
         } else if (command.equalsIgnoreCase("endPlus1Day")) {
            endOld.add(Calendar.DAY_OF_MONTH, 1);
         } else if (command.equalsIgnoreCase("endPlus1Month")) {
            endOld.add(Calendar.MONTH, 1);
         } else if (command.equalsIgnoreCase("endPlus1Year")) {
            endOld.add(Calendar.YEAR, 1);
         }
         if (endOld.before(startOld)) {
            return;
         }
         tmp.start = df.format(startOld.getTime());
         tmp.end = df.format(endOld.getTime());
         tmp.sensor.update(createUrl(tmp));
      } catch (ParseException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

   }

   // Implements EventListener -----------------------------------------------------------------
   @Override
   public void setSensor(Sensor sensor) {
      this.sensor = sensor;
      RrdGraphUrlCommand.graphUrls.put(graphName, this);
      sensor.update(createUrl(this));
   }

   @Override
   public void stop(Sensor sensor) {
   }

   private String createUrl(RrdGraphUrlCommand cmd) {
      if (cmd.ip == null) {
         ControllerConfiguration configuration = ControllerConfiguration.readXML();
         try {
            if ((configuration.getWebappIp() != null) && (!configuration.getWebappIp().isEmpty())) {
               cmd.ip = configuration.getWebappIp();
            } else {
               cmd.ip = InetAddress.getLocalHost().getHostAddress();               
            }
         } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
      if (cmd.port == null) {
         ControllerConfiguration configuration = ControllerConfiguration.readXML();
         cmd.port = "" + configuration.getWebappPort();
      }
      String url = "http://" + cmd.ip + ":" + cmd.port + "/controller/graph?name=" + cmd.graphName 
         + "&amp;start=" + cmd.start 
         + "&amp;end=" + cmd.end 
         + "&amp;width=" + cmd.width 
         + "&amp;height=" + cmd.height;
      return url;
   }
}
