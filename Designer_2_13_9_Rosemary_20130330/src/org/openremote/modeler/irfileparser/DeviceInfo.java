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
package org.openremote.modeler.irfileparser;

import org.openremote.ir.domain.BrandInfo;

import com.extjs.gxt.ui.client.data.BeanModelTag;

/**
 * Adds BeanModelTag capability to DeviceInfo for compatibility with GXT stores.
 * 
 * @author Eric Bariaux (eric@openremote.org)
 *
 */
public class DeviceInfo extends org.openremote.ir.domain.DeviceInfo implements BeanModelTag {

  private static final long serialVersionUID = 1L;

  public DeviceInfo() {
    super();
  }

  public DeviceInfo(BrandInfo brand, String modelName) {
    super(brand, modelName);
  }

}