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
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.widget.CommonForm;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPairRef;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.CheckBoxListView;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * Selects screens when create a new group in groupWizardWindow.
 * It has a checkBoxListView to show the screens and a "show other panel type" 
 * checkBox.
 */
public class SelectScreenForm extends CommonForm {

   private CheckBoxListView<BeanModel> screenListView = null;
   private CheckBox showAll = null;
   protected BeanModel groupRefBeanModel = null;
   protected Component wrapper;
   private List<BeanModel> otherModels = new ArrayList<BeanModel>();
   private Boolean oldValue = false;
   public SelectScreenForm(Component wrapper, BeanModel groupRefBeanModel) {
      super();
      this.wrapper = wrapper;
      this.groupRefBeanModel = groupRefBeanModel;
      setLabelAlign(LabelAlign.TOP);
      createFields();
      addBeforeSubmitListener();
      setFieldWidth(280);
   }
   
   
   private void createFields() {
      AdapterField screenField = new AdapterField(createScreenList());
      screenField.setFieldLabel("Screens with the same dimension");
      
      showAll = new CheckBox();
      showAll.setHideLabel(true);
      showAll.setBoxLabel("show other panel type");
      showAll.addListener(Events.Change, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            if (!oldValue.toString().equals(be.getValue().toString())) {
               if ("true".equals(be.getValue().toString())) {
                  screenListView.getStore().add(otherModels);
               } else if ("false".equals(be.getValue().toString())) {
                  for (BeanModel otherModel : otherModels) {
                     screenListView.getStore().remove(otherModel);
                  }
               }
            }
            oldValue = new Boolean(be.getValue().toString());
         }
         
      });
      add(screenField);
      add(showAll);
   }
   
   private ContentPanel createScreenList() {
      ContentPanel screenContainer = new ContentPanel();
      screenContainer.setHeaderVisible(false);
      screenContainer.setWidth(280);
      screenContainer.setHeight(150);
      screenContainer.setLayout(new FitLayout());
      // overflow-auto style is for IE hack.
      screenContainer.addStyleName("overflow-auto");
      
      screenListView = new CheckBoxListView<BeanModel>();
      ListStore<BeanModel> store = new ListStore<BeanModel>();
      screenListView.setHeight(150);
      screenListView.setStore(store);
      screenListView.setDisplayProperty("panelName");
      screenListView.setStyleAttribute("overflow", "auto");
      screenListView.setSelectStyle("screen-view-item-sel");
      screenContainer.add(screenListView);
      return screenContainer;
   }
   @Override
   public boolean isNoButton() {
      return true;
   }
   
   @Override
   public void show() {
      super.show();
      ((Window) wrapper).setSize(360, 200);
   }
   
   public void update(Panel panel) {
      TouchPanelDefinition touchPanel = panel.getTouchPanelDefinition();
      ListStore<BeanModel> store = screenListView.getStore();
      store.removeAll();
      otherModels.clear();
      List<BeanModel> screenModels = BeanModelDataBase.screenTable.loadAll();
      for (BeanModel screenModel : screenModels) {
         if (((ScreenPair) screenModel.getBean()).getTouchPanelDefinition().equals(touchPanel)) {
            store.add(screenModel);
            screenListView.getSelectionModel().select(screenModel, true);
         } else if (((ScreenPair) screenModel.getBean()).getTouchPanelDefinition().getCanvas().equals(touchPanel.getCanvas())){
            otherModels.add(screenModel);
         }
      }
      showAll.setValue(false);
         
   }
   
   private void addBeforeSubmitListener() {
      addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
            Group group = ((GroupRef) groupRefBeanModel.getBean()).getGroup();
            TouchPanelDefinition touchPanelDefinition = ((GroupRef) groupRefBeanModel.getBean()).getPanel().getTouchPanelDefinition();
            for (ScreenPairRef screenRef : group.getScreenRefs()) {
               screenRef.getScreen().releaseRef();
            }
            group.getScreenRefs().clear();
            List<BeanModel> screenModels = screenListView.getChecked();
            if (screenModels.size() > 0) {
               for (BeanModel screenModel : screenModels) {
                  ScreenPairRef screenRef = new ScreenPairRef((ScreenPair) screenModel.getBean());
                  screenRef.setTouchPanelDefinition(touchPanelDefinition);
                  screenRef.setGroup(group);
                  group.addScreenRef(screenRef);
               }
            }
            wrapper.fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(groupRefBeanModel));
         }

      });
   }
}
