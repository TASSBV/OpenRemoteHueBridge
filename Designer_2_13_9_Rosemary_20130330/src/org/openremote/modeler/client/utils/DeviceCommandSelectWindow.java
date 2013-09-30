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

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.shared.dto.DTO;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;

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
 * The window is for selecting device command under a device.
 * @author Javen
 *
 */
public class DeviceCommandSelectWindow extends Dialog{
   
   protected TreePanel<BeanModel> deviceCommandTree;
   
   public DeviceCommandSelectWindow(long deviceId) {
      setHeading("Select Command");
      setMinHeight(260);
      setWidth(200);
      setLayout(new FitLayout());
      setModal(true);
      initCommandTree(deviceId);
      setButtons(Dialog.OKCANCEL);
      setHideOnButtonClick(true);
      addButtonListener();
      show();
   }

   private void initCommandTree(long deviceId) {
      ContentPanel deviceCommandPanel = new ContentPanel();
      deviceCommandPanel.setBorders(false);
      deviceCommandPanel.setBodyBorder(false);
      deviceCommandPanel.setHeaderVisible(false);
      if (deviceCommandTree == null) {
         createCommandTree(deviceId);
         deviceCommandPanel.add(deviceCommandTree);
      }
      // overflow-auto style is for IE hack.
      deviceCommandPanel.addStyleName("overflow-auto");
      add(deviceCommandPanel);
   }
   
   protected void createCommandTree(long deviceId) {
      deviceCommandTree = TreePanelBuilder.buildCommandTree(deviceId, null);
   }
   
   protected Class<? extends DTO> getBeanClass() {
     return DeviceCommandDTO.class;
   }
   
   private void addButtonListener() {
      addListener(Events.BeforeHide, new Listener<WindowEvent>() {
         public void handleEvent(WindowEvent be) {
            if (be.getButtonClicked() == getButtonById("ok")) {
               BeanModel beanModel = deviceCommandTree.getSelectionModel().getSelectedItem();
               if (beanModel == null) {
                  MessageBox.alert("Error", "Please select a command.", null);
                  be.cancelBubble();
               } else {
                  if (beanModel.getBean().getClass() == getBeanClass()) {
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
