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

/**
 * These are all allowed command values for the russound protocol
 */
public enum RussCmdEnum {
   
   ALL_ON,
   ALL_OFF,
   POWER_ON,
   POWER_OFF,
   VOL_UP,
   VOL_DOWN,
   SELECT_SOURCE1,
   SELECT_SOURCE2,
   SELECT_SOURCE3,
   SELECT_SOURCE4,
   SELECT_SOURCE5,
   SELECT_SOURCE6,
   SELECT_SOURCE7,
   SELECT_SOURCE8,
   SET_SOURCE,
   SET_VOLUME,
   SET_TURNON_VOLUME,
   SET_BASS_LEVEL,
   SET_TREBLE_LEVEL,
   SET_BALANCE_LEVEL,
   SET_LOUDNESS_ON,
   SET_LOUDNESS_OFF,
   SET_PARTYMODE_ON,
   SET_PARTYMODE_OFF,
   GET_VOLUME,
   GET_TURNON_VOLUME,
   GET_POWER_STATUS,
   GET_SOURCE,
   GET_BASS_LEVEL,
   GET_TREBLE_LEVEL,
   GET_BALANCE_LEVEL,
   GET_LOUDNESS_MODE,
   GET_PARTY_MODE,
   GET_SHARED_SOURCE,
   GET_SYSTEM_ON_STATE,
   GET_DO_NOT_DISTURB;

   private RussCmdEnum() {
      this.value = null;
   }

   private RussCmdEnum(String value) {
      this.value = value;
   }

   private RussCmdEnum(RussCmdEnum otherKey) {
      this(otherKey.getValue());
   }

   private String value;

   public String getValue() {
      if (value == null) {
         return this.name();
      }
      return value;
   }
   

}