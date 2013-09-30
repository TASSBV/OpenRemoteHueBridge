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

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPairRef;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.CheckBoxListView;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * The window to edit group properties.
 */
public class GroupEditWindow extends FormWindow {

   private TextField<String> nameField = null;
   private CheckBoxListView<BeanModel> screenPairListView = null;
   private BeanModel groupRefBeanModel = null;
   public GroupEditWindow(BeanModel groupRefBeanModel) {
      this.groupRefBeanModel = groupRefBeanModel;
      setHeading("Edit Group");
      setSize(370, 260);
      createFields();
      createButtons();
      add(form);
      form.setLabelWidth(50);
      form.setFieldWidth(260);
      show();
   }
   
   private void createFields() {
      GroupRef groupRef = (GroupRef) groupRefBeanModel.getBean();
      
      nameField = new TextField<String>();
      nameField.setName("name");
      nameField.setFieldLabel("Name");
      nameField.setAllowBlank(false);
      if (groupRef.getGroup().getName() != null) {
         nameField.setValue(groupRef.getGroup().getName());
      }
      
      AdapterField screenField = new AdapterField(createScreenPairList(groupRef));
      screenField.setFieldLabel("Screen");
      form.add(nameField);
      form.add(screenField);
      
   }
   
   private ContentPanel createScreenPairList(GroupRef groupRef) {
      TouchPanelDefinition touchPanel = groupRef.getPanel().getTouchPanelDefinition();
      
      ContentPanel screenContainer = new ContentPanel();
      screenContainer.setHeaderVisible(false);
      screenContainer.setWidth(260);
      screenContainer.setHeight(150);
      screenContainer.setLayout(new FitLayout());
      // overflow-auto style is for IE hack.
      screenContainer.addStyleName("overflow-auto");
      
      screenPairListView = new CheckBoxListView<BeanModel>();
      ListStore<BeanModel> store = new ListStore<BeanModel>();
      
      List<BeanModel> otherModels = new ArrayList<BeanModel>();
      List<BeanModel> screenPairModels = BeanModelDataBase.screenTable.loadAll();
      List<BeanModel> selectedModels = new ArrayList<BeanModel>();
      for (ScreenPairRef screenRef: groupRef.getGroup().getScreenRefs()) {
         selectedModels.add(screenRef.getScreen().getBeanModel());
      }
      for (BeanModel screenPairModel : screenPairModels) {
         if (((ScreenPair) screenPairModel.getBean()).getTouchPanelDefinition().equals(touchPanel)) {
            store.add(screenPairModel);
         } else if (((ScreenPair) screenPairModel.getBean()).getTouchPanelDefinition().getCanvas().equals(touchPanel.getCanvas())){
            otherModels.add(screenPairModel);
         }
      }
      
      store.add(otherModels);
      for (BeanModel selectedModel : selectedModels) {
         screenPairListView.setChecked(selectedModel, true);
      }
      screenPairListView.setStore(store);
      screenPairListView.setDisplayProperty("panelName");
      screenPairListView.setStyleAttribute("overflow", "auto");
      screenPairListView.setSelectStyle("screen-view-item-sel");
      screenContainer.add(screenPairListView);
      return screenContainer;
   }
   
   private void createButtons() {
      Button submitBtn = new Button("Submit");
      Button resetBtn = new Button("Reset");
      submitBtn.addSelectionListener(new FormSubmitListener(form, submitBtn));
      resetBtn.addSelectionListener(new FormResetListener(form));
      
      form.addButton(submitBtn);
      form.addButton(resetBtn);
      addBeforSubmitListener();
   }
   private void addBeforSubmitListener() {
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
            List<BeanModel> storeModels = screenPairListView.getStore().getModels();
            List<BeanModel> screenPairModels = screenPairListView.getChecked();
            Group group = ((GroupRef) groupRefBeanModel.getBean()).getGroup();
            storeModels.removeAll(screenPairModels);
            String screenNames = "";
            for (BeanModel beanModel : storeModels) {
               for (ScreenPairRef screenPairRef : group.getScreenRefs()) {
                  if (screenPairRef.getScreen() == beanModel.getBean() && screenPairRef.getScreen().getRefCount() == 1) {
                     screenNames = screenNames + screenPairRef.getScreen().getName() + ",";
                  }
               }
            }
            if (!"".equals(screenNames)) {
               screenNames = screenNames.substring(0, screenNames.lastIndexOf(","));
               MessageBox.confirm("Confirm delete screens", "Are you sure you want to delete " + screenNames + "?", new Listener<MessageBoxEvent>() {
                  public void handleEvent(MessageBoxEvent be) {
                     if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                        confirmSubmit();
                     } else if (be.getButtonClicked().getItemId().equals(Dialog.NO)) {
                        return;
                     }
                  }
               });
            } else {
               confirmSubmit();
            }
         }

         private void confirmSubmit() {
            Group group = ((GroupRef) groupRefBeanModel.getBean()).getGroup();
            TouchPanelDefinition touchPanelDefinition = ((GroupRef) groupRefBeanModel.getBean()).getPanel().getTouchPanelDefinition();
            for (ScreenPairRef screenPairRef : group.getScreenRefs()) {
               screenPairRef.getScreen().releaseRef();
            }
            group.getScreenRefs().clear();
            group.setName(nameField.getValue());
            List<BeanModel> screenPairModels = screenPairListView.getChecked();
            if (screenPairModels.size() > 0) {
               for (BeanModel screenPairModel : screenPairModels) {
                  ScreenPairRef screenPairRef = new ScreenPairRef((ScreenPair) screenPairModel.getBean());
                  screenPairRef.setTouchPanelDefinition(touchPanelDefinition);
                  screenPairRef.setGroup(group);
                  group.addScreenRef(screenPairRef);
               }
            }
            fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(groupRefBeanModel));
         }
      });
   }
}
