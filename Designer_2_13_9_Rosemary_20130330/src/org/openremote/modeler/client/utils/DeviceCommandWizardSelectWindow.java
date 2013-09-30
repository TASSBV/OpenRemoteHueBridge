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

import java.util.ArrayList;

import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.shared.dto.DTO;
import org.openremote.modeler.shared.dto.DTOHelper;
import org.openremote.modeler.shared.dto.DeviceCommandDetailsDTO;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * The Class is for select device command round under a device.
 */
public class DeviceCommandWizardSelectWindow extends DeviceCommandSelectWindow {
  
  TreeStore<BeanModel> commandTreeStore;
  
   public DeviceCommandWizardSelectWindow(ArrayList<DeviceCommandDetailsDTO> commands) {
      super(0L); // not used, can be dummy data
      for (DeviceCommandDetailsDTO deviceCommandDTO : commands) {
        commandTreeStore.add(DTOHelper.getBeanModel(deviceCommandDTO), false);
      }
   }

   @Override
   protected Class<? extends DTO> getBeanClass() {
     return DeviceCommandDetailsDTO.class;
   }

   @Override
   protected void createCommandTree(long deviceId) {
      commandTreeStore = new TreeStore<BeanModel>();

      deviceCommandTree = new TreePanel<BeanModel>(commandTreeStore);
      deviceCommandTree.setBorders(false);
      deviceCommandTree.setStateful(true);
      deviceCommandTree.setDisplayProperty("name");
      deviceCommandTree.setStyleAttribute("overflow", "auto");
      deviceCommandTree.setHeight("100%");
      deviceCommandTree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            return ((Icons) GWT.create(Icons.class)).deviceCmd();
         }

      });
   }

}
