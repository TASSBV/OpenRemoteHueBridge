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

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.utils.Logger;

/**
 *
 * @author Marcus
 */
public class RussoundCommand implements EventListener, ExecutableCommand {

   // Class Members --------------------------------------------------------------------------------
   private final static Logger logger = Logger.getLogger(RussoundCommandBuilder.RUSSOUND_PROTOCOL_LOG_CATEGORY);

   // Instance Fields ------------------------------------------------------------------------------
   private String controller;
   private String zone;
   private RussCmdEnum command;
   private String paramValue;
   private RussoundClient commClient;


   // Constructor ---------------------------------------------------------------------
   public RussoundCommand(String controller, String zone, RussCmdEnum command, String paramValue, RussoundClient commClient) {
      this.controller = controller;
      this.zone = zone;
      this.command = command;
      this.paramValue = paramValue;
      this.commClient = commClient;
   }

   

   @Override
   public void send() {
      switch (command) {
         case ALL_ON:
            commClient.setAllOnOffPower(1);
            break;
         case ALL_OFF:
            commClient.setAllOnOffPower(0);
            break;
         case POWER_OFF:
            commClient.setPower(controller, zone, 0);
            break;
         case POWER_ON:
            commClient.setPower(controller, zone, 1);
            break;
         case SET_VOLUME:
            commClient.setVolume(controller, zone, paramValue);
            break;
         case VOL_UP:
            commClient.setVolumeUp(controller, zone);
            break;
         case VOL_DOWN:
            commClient.setVolumeDown(controller, zone);
            break;
         case SELECT_SOURCE1:
            commClient.setSource(controller, zone, "1");
            break;
         case SELECT_SOURCE2:
            commClient.setSource(controller, zone, "2");
            break;
         case SELECT_SOURCE3:
            commClient.setSource(controller, zone, "3");
            break;
         case SELECT_SOURCE4:
            commClient.setSource(controller, zone, "4");
            break;
         case SELECT_SOURCE5:
            commClient.setSource(controller, zone, "5");
            break;
         case SELECT_SOURCE6:
            commClient.setSource(controller, zone, "6");
            break;
         case SELECT_SOURCE7:
            commClient.setSource(controller, zone, "7");
            break;
         case SELECT_SOURCE8:
            commClient.setSource(controller, zone, "8");
            break;
         case SET_SOURCE:
            commClient.setSource(controller, zone, paramValue);
            break;
         case SET_LOUDNESS_OFF:
            commClient.setSettings(controller, zone, command, "0");
            break;
         case SET_LOUDNESS_ON:
            commClient.setSettings(controller, zone, command, "1");
            break;
         case SET_PARTYMODE_OFF:
            commClient.setSettings(controller, zone, command, "0");
            break;
         case SET_PARTYMODE_ON:
            commClient.setSettings(controller, zone, command, "1");
            break;
         case SET_BASS_LEVEL:
            commClient.setSettings(controller, zone, command, paramValue);
            break;
         case SET_TREBLE_LEVEL:
            commClient.setSettings(controller, zone, command, paramValue);
            break;
         case SET_BALANCE_LEVEL:
            commClient.setSettings(controller, zone, command, paramValue);
            break;
         case SET_TURNON_VOLUME:
            commClient.setSettings(controller, zone, command, paramValue);
            break;
      }
      
      //Give it some time before requesting the changed status
      try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }
      
      //Request new status after sending a command
      if ((command == RussCmdEnum.ALL_OFF) || (command == RussCmdEnum.ALL_OFF)) {
         //Wait a little longer after the "ALL" commands since the russound has more todo
         try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
         commClient.updateAllZones();
      } else {
         commClient.requestStatus(controller, zone);
      }
   }


   @Override
   public void setSensor(Sensor sensor) {
      logger.debug("*** setSensor called as part of EventListener init *** sensor is: " + sensor);
      commClient.addSensor(controller, zone, command, sensor);
   }



   @Override
   public void stop(Sensor sensor) {
      commClient.removeSensor(controller, zone, command, sensor);
   }

}
