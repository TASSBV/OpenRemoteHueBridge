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

import java.util.HashMap;

import org.openremote.controller.model.sensor.Sensor;

/**
 * This class holds the current state of each zone. If for any given attribute a sensor is registered,
 * the sensor is updated, when the value is set.
 * 
 * @author marcus
  */
public class ZoneData {
   
   private boolean power;
   private int source;
   private int volume;
   private int turnOnVolume;
   private int bassLevel;
   private int trebleLevel;
   private boolean loudness;
   private int balanceLevel;
   private boolean sharedSource;
   private int partyMode;
   private boolean doNotDisturb;
   private boolean systemOnState;
   
   private boolean dataAvailable;
   
   private HashMap<RussCmdEnum, Sensor> linkedSensors = new HashMap<RussCmdEnum, Sensor>();
  
   public boolean isPower() {
      return power;
   }
   
   public void setPower(boolean power) {
      this.power = power;
      if (linkedSensors.get(RussCmdEnum.GET_POWER_STATUS) != null) {
         linkedSensors.get(RussCmdEnum.GET_POWER_STATUS).update((power)?"on":"off");
      }
   }
   
   public int getSource() {
      return source;
   }
   
   public void setSource(int source) {
      this.source = source;
      if (linkedSensors.get(RussCmdEnum.GET_SOURCE) != null) {
         linkedSensors.get(RussCmdEnum.GET_SOURCE).update(""+source);
      }
   }
   
   public int getVolume() {
      return volume;
   }
   
   public void setVolume(int volume) {
      this.volume = volume;
      if (linkedSensors.get(RussCmdEnum.GET_VOLUME) != null) {
         linkedSensors.get(RussCmdEnum.GET_VOLUME).update(""+volume);
      }
   }
   
   public int getTurnOnVolume() {
      return turnOnVolume;
   }
   
   public void setTurnOnVolume(int turnOnVolume) {
      this.turnOnVolume = turnOnVolume;
      if (linkedSensors.get(RussCmdEnum.GET_TURNON_VOLUME) != null) {
         linkedSensors.get(RussCmdEnum.GET_TURNON_VOLUME).update(""+turnOnVolume);
      }
   }
   
   public int getBassLevel() {
      return bassLevel;
   }
   
   public void setBassLevel(int bassLevel) {
      this.bassLevel = bassLevel;
      if (linkedSensors.get(RussCmdEnum.GET_BASS_LEVEL) != null) {
         linkedSensors.get(RussCmdEnum.GET_BASS_LEVEL).update(""+bassLevel);
      }
   }
   
   public int getTrebleLevel() {
      return trebleLevel;
   }
   
   public void setTrebleLevel(int trebleLevel) {
      this.trebleLevel = trebleLevel;
      if (linkedSensors.get(RussCmdEnum.GET_TREBLE_LEVEL) != null) {
         linkedSensors.get(RussCmdEnum.GET_TREBLE_LEVEL).update(""+trebleLevel);
      }
   }
   
   public boolean isLoudness() {
      return loudness;
   }
   
   public void setLoudness(boolean loudness) {
      this.loudness = loudness;
      if (linkedSensors.get(RussCmdEnum.GET_LOUDNESS_MODE) != null) {
         linkedSensors.get(RussCmdEnum.GET_LOUDNESS_MODE).update((loudness)?"on":"off");
      }
   }
   
   public int getBalanceLevel() {
      return balanceLevel;
   }
   
   public void setBalanceLevel(int balanceLevel) {
      this.balanceLevel = balanceLevel;
      if (linkedSensors.get(RussCmdEnum.GET_BALANCE_LEVEL) != null) {
         linkedSensors.get(RussCmdEnum.GET_BALANCE_LEVEL).update(""+balanceLevel);
      }
   }
   
   public boolean isSharedSource() {
      return sharedSource;
   }
   
   public void setSharedSource(boolean sharedSource) {
      this.sharedSource = sharedSource;
      if (linkedSensors.get(RussCmdEnum.GET_SHARED_SOURCE) != null) {
         linkedSensors.get(RussCmdEnum.GET_SHARED_SOURCE).update((sharedSource)?"on":"off");
      }
   }
   
   public int getPartyMode() {
      return partyMode;
   }
   
   public void setPartyMode(int partyMode) {
      this.partyMode = partyMode;
      if (linkedSensors.get(RussCmdEnum.GET_PARTY_MODE) != null) {
         linkedSensors.get(RussCmdEnum.GET_PARTY_MODE).update(""+partyMode);
      }
   }
   
   public boolean isDoNotDisturb() {
      return doNotDisturb;
   }
   
   public void setDoNotDisturb(boolean doNotDisturb) {
      this.doNotDisturb = doNotDisturb;
      if (linkedSensors.get(RussCmdEnum.GET_DO_NOT_DISTURB) != null) {
         linkedSensors.get(RussCmdEnum.GET_DO_NOT_DISTURB).update((doNotDisturb)?"on":"off");
      }
   }

   
   public boolean isSystemOnState() {
      return systemOnState;
   }

   public void setSystemOnState(boolean systemOnState) {
      this.systemOnState = systemOnState;
      if (linkedSensors.get(RussCmdEnum.GET_SYSTEM_ON_STATE) != null) {
         linkedSensors.get(RussCmdEnum.GET_SYSTEM_ON_STATE).update((systemOnState)?"on":"off");
      }
   }

   public boolean isDataAvailable() {
      return dataAvailable;
   }

   public void setDataAvailable(boolean dataAvailable) {
      this.dataAvailable = dataAvailable;
   }

   public void addSensor(RussCmdEnum command, Sensor sensor) {
      linkedSensors.put(command, sensor);
      if (dataAvailable) {  //The zone was already updates let's tell the sensor
         switch (command) {
            case GET_SYSTEM_ON_STATE:
               sensor.update((systemOnState)?"on":"off");
               break;
            case GET_DO_NOT_DISTURB:
               sensor.update((doNotDisturb)?"on":"off");
               break;
            case GET_PARTY_MODE:
               sensor.update(""+partyMode);
               break;
            case GET_SHARED_SOURCE:
               sensor.update((sharedSource)?"on":"off");
               break;
            case GET_BALANCE_LEVEL:
               sensor.update(""+balanceLevel);
               break;
            case GET_LOUDNESS_MODE:
               sensor.update((loudness)?"on":"off");
               break;
            case GET_TREBLE_LEVEL:
               sensor.update(""+trebleLevel);
               break;
            case GET_BASS_LEVEL:
               sensor.update(""+bassLevel);
               break;
            case GET_TURNON_VOLUME:
               sensor.update(""+turnOnVolume);
               break;
            case GET_VOLUME:
               sensor.update(""+volume);
               break;
            case GET_SOURCE:
               sensor.update(""+source);
               break;
            case GET_POWER_STATUS:
               sensor.update((power)?"on":"off");
               break;
         }
      }
   }

   public void removeSensor(RussCmdEnum command, Sensor sensor) {
      this.linkedSensors.remove(command);
   }
   
   
   
}


