/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.modeler.client.utils;

import java.util.HashSet;
import java.util.Set;

import org.openremote.modeler.domain.BusinessEntity;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.ScreenPair;

/**
 * Defines a screen which generate from a template, which has devices, macros and a screenPair.
 * 
 * @author javen
 *
 */
public class ScreenFromTemplate extends BusinessEntity{

   private static final long serialVersionUID = 4268344360518764171L;
   
   private Set<Device> devices = new HashSet<Device>();
   private Set<DeviceMacro> macros = new HashSet<DeviceMacro>();
   private ScreenPair screen = null;
   
   public ScreenFromTemplate(){}
   
   public ScreenFromTemplate(Set<Device> devices,ScreenPair screen,Set<DeviceMacro> macros) {
      this.devices = devices;
      this.screen = screen;
      this.macros = macros;
   }

   public Set<Device> getDevices() {
      return devices;
   }

   public void setDevices(Set<Device> devices) {
      this.devices = devices;
   }

   public ScreenPair getScreen() {
      return screen;
   }

   public void setScreen(ScreenPair screen) {
      this.screen = screen;
   }

   public Set<DeviceMacro> getMacros() {
      return macros;
   }

   public void setMacros(Set<DeviceMacro> macros) {
      this.macros = macros;
   }
   
}
