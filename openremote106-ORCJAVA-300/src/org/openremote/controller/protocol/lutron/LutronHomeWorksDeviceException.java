/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.protocol.lutron;

import org.openremote.controller.protocol.lutron.model.HomeWorksDevice;

@SuppressWarnings("serial")
public class LutronHomeWorksDeviceException extends Exception {

  private LutronHomeWorksAddress address;
  private Class<? extends HomeWorksDevice> deviceClass;
  
  public LutronHomeWorksDeviceException(String message, LutronHomeWorksAddress address, Class<? extends HomeWorksDevice> deviceClass, Throwable cause) {
    super(message, cause);
    this.address = address;
    this.deviceClass = deviceClass;
  }

  public LutronHomeWorksAddress getAddress() {
    return address;
  }

  public Class<? extends HomeWorksDevice> getDeviceClass() {
    return deviceClass;
  }
  
}
