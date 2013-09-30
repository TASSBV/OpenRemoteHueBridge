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
package org.openremote.modeler.shared.lutron;

import net.customware.gwt.dispatch.shared.Action;

import org.openremote.modeler.shared.dto.DeviceDTO;

public class ImportLutronConfigAction implements Action<ImportLutronConfigResult> {
  
  private DeviceDTO device;
  private ImportConfig config;
  
  public ImportLutronConfigAction() {
  }
  
  public ImportLutronConfigAction(ImportConfig config) {
    this.config = config;
  }

  public ImportConfig getConfig() {
    return config;
  }

  public void setConfig(ImportConfig config) {
    this.config = config;
  }
  
  public DeviceDTO getDevice() {
    return device;
  }

  public void setDevice(DeviceDTO device) {
    this.device = device;
  }

}
