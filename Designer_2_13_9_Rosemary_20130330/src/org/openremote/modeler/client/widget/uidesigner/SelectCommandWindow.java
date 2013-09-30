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
package org.openremote.modeler.client.widget.uidesigner;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.MacroDTO;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

/**
 * A window for select command from devicesAndMacrosTree.
 */
public class SelectCommandWindow extends Dialog {
   private TreePanel<BeanModel> devicesAndMacrosTree;
   public SelectCommandWindow() {
      setHeading("Select Command");
      setHeight(300);
      setWidth(260);
      setLayout(new FitLayout());
      setModal(true);
      initDevicesAndMacrosTree();
      setButtons(Dialog.OKCANCEL);
      setHideOnButtonClick(true);
      addButtonListener();
      show();
   }

   private void initDevicesAndMacrosTree() {
      ContentPanel devicesAndMacrosTreeContainer = new ContentPanel();
      devicesAndMacrosTreeContainer.setBorders(false);
      devicesAndMacrosTreeContainer.setBodyBorder(false);
      devicesAndMacrosTreeContainer.setHeaderVisible(false);
      devicesAndMacrosTreeContainer.setLayout(new FitLayout());
      if (devicesAndMacrosTree == null) {
         devicesAndMacrosTree = TreePanelBuilder.buildCommandAndMacroTree();
         devicesAndMacrosTreeContainer.add(devicesAndMacrosTree);
      }
      
      // overflow-auto style is for IE hack.
      devicesAndMacrosTreeContainer.addStyleName("overflow-auto");
      add(devicesAndMacrosTreeContainer);
   }
   
   private void addButtonListener() {
      addListener(Events.BeforeHide, new Listener<WindowEvent>() {
         public void handleEvent(WindowEvent be) {
            if (be.getButtonClicked() == getButtonById("ok")) {
               BeanModel beanModel = devicesAndMacrosTree.getSelectionModel().getSelectedItem();
               if (beanModel == null) {
                  MessageBox.alert("Error", "Please select a command.", null);
                  be.cancelBubble();
               } else {
                  if ((beanModel.getBean() instanceof DeviceCommandDTO) || (beanModel.getBean() instanceof MacroDTO)) {
                     fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(beanModel));
                  } else {
                     MessageBox.alert("Error", "Please select a command.", null);
                     be.cancelBubble();
                  }
               }
            }
         }
      }); 
   }
}
