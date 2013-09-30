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

import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.UtilsProxy;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.utils.TouchPanels;
import org.openremote.modeler.client.widget.ComboBoxExt;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPairRef;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;

/**
 * The window to create or update a panel with the predefined type, the panel is not custom panel.
 */
public class PanelWindow extends FormWindow {

   private static final String PANEL_NAME = "panelName";
   private BeanModel panelModel = null;
   private TextField<String> panelNameField = null;
//   private CheckBox createScreen = new CheckBox();
   private ComboBoxExt predefinedPanel = null;
   
   private Button submitBtn;
   
   /**
    * Instantiates a window to create a new panel.
    */
   public PanelWindow() {
      super();
      initial("New Panel");
      show();
   }

   /**
    * Instantiates a window to edit the panel's name.
    * 
    * @param panelModel the panel model
    */
   public PanelWindow(BeanModel panelModel) {
      super();
      this.panelModel = panelModel;
      initial("Edit Panel");
      show();
   }

   private void initial(String heading) {
      setWidth(320);
      setAutoHeight(true);
      setHeading(heading);
      setLayout(new FlowLayout());
      createFields();
      createButtons();
      addListenersToForm();
   }

   private void createFields() {
      form.setFrame(true);
      form.setHeaderVisible(false);
      form.setBorders(false);
      
      panelNameField = new TextField<String>();
      panelNameField.setName(PANEL_NAME);
      panelNameField.setFieldLabel("Name");
      panelNameField.setAllowBlank(false);
      panelNameField.setValue(Panel.getNewDefaultName());
      if (panelModel != null) {
         Panel panel = panelModel.getBean();
         panelNameField.setValue(panel.getName());
      }
      
//      createScreen.setBoxLabel("Create a new screen");
//      createScreen.setHideLabel(true);
//      createScreen.setValue(true);
      
      form.add(panelNameField);
      if (panelModel == null) {
         form.add(createTypeField());
//         form.add(createScreen);
      }
      form.setLabelWidth(60);
      add(form);
   }

   private ComboBoxExt createTypeField() {
      predefinedPanel = new ComboBoxExt();
      
      Map<String, List<TouchPanelDefinition>> predefinedPanels = TouchPanels.getInstance();
      predefinedPanel.setFieldLabel("Panel type");
      predefinedPanel.setName("predefine");
      predefinedPanel.setAllowBlank(false);
      ComboBoxDataModel<TouchPanelDefinition> iphoneData = null;
      for (String key : predefinedPanels.keySet()) {
         for (TouchPanelDefinition touchPanel : predefinedPanels.get(key)) {
            ComboBoxDataModel<TouchPanelDefinition> data = new ComboBoxDataModel<TouchPanelDefinition>(touchPanel
                  .getName(), touchPanel);
            if ("iPhone".equals(touchPanel.getName())) {
               iphoneData = data;
            }
            predefinedPanel.getStore().add(data);
         }
      }
      predefinedPanel.setEmptyText("Please Select Panel...");
      predefinedPanel.setValue(iphoneData); // temp select iphone panel.
      
      return predefinedPanel;
   }
   
   private void createButtons() {
      submitBtn = new Button("Submit");
      Button resetBtn = new Button("Reset");

      submitBtn.addSelectionListener(new FormSubmitListener(form, submitBtn));
      resetBtn.addSelectionListener(new FormResetListener(form));

      form.addButton(submitBtn);
      form.addButton(resetBtn);
   }

   private void addListenersToForm() {
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         @SuppressWarnings("unchecked")
         @Override
         public void handleEvent(FormEvent be) {
            Panel panel = new Panel();
            String panelName = panelNameField.getValue();
            if (!UtilsProxy.isPanelNameAvailable(panelName)) {
               MessageBox.alert("Warn", "'" + panelName + "' already exists, please select another name.", null);
               submitBtn.enable();
               return;
            }
            if (panelModel == null) {
               panel.setOid(IDUtil.nextID());
               Panel.increaseDefaultNameIndex();
               ComboBoxDataModel<TouchPanelDefinition> prededinedPanel = (ComboBoxDataModel<TouchPanelDefinition>) predefinedPanel
                     .getValue();
               panel.setTouchPanelDefinition(prededinedPanel.getData());
               Group defaultGroup = new Group();
               defaultGroup.setParentPanel(panel);
               defaultGroup.setOid(IDUtil.nextID());
               defaultGroup.setName(Constants.DEFAULT_GROUP);
               GroupRef groupRef = new GroupRef(defaultGroup);
               panel.addGroupRef(groupRef);
               groupRef.setPanel(panel);
//               if (createScreen.getValue()) {
                  Screen defaultScreen = new Screen();
                  defaultScreen.setOid(IDUtil.nextID());
                  defaultScreen.setName(Constants.DEFAULT_SCREEN);
                  defaultScreen.setTouchPanelDefinition(panel.getTouchPanelDefinition());
                  
                  ScreenPair screenPair = new ScreenPair();
                  screenPair.setOid(IDUtil.nextID());
                  screenPair.setTouchPanelDefinition(panel.getTouchPanelDefinition());
                  screenPair.setPortraitScreen(defaultScreen);
                  screenPair.setParentGroup(defaultGroup);
                  ScreenPairRef screenRef = new ScreenPairRef(screenPair);
                  screenRef.setTouchPanelDefinition(panel.getTouchPanelDefinition());
                  screenRef.setGroup(defaultGroup);
                  defaultGroup.addScreenRef(screenRef);
                  BeanModelDataBase.screenTable.insert(screenPair.getBeanModel());
//               }
               BeanModelDataBase.groupTable.insert(defaultGroup.getBeanModel());
            } else {
               panel = panelModel.getBean();
            }
            panel.setName(panelName);
            BeanModelDataBase.panelTable.insert(panel.getBeanModel());
            fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(panel));
         }
      });
   }
   
}
