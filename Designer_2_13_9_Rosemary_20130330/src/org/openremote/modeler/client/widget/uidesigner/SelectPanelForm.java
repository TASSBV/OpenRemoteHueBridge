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

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.widget.CommonForm;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * Select panel in groupWizardWindow.
 * The form has a name field and a panel listView.
 */
public class SelectPanelForm extends CommonForm {

   private TextField<String> nameField = null;
   private ListView<BeanModel> panelListView = null;
   protected BeanModel groupRefBeanModel = null;
   protected Component wrapper;
   
   public SelectPanelForm(Component wrapper, BeanModel groupRefBeanModel) {
      super();
      this.wrapper = wrapper;
      this.groupRefBeanModel = groupRefBeanModel;
      createFields();
      addBeforeSubmitListener();
   }
   
   private void createFields() {
      GroupRef groupRef = (GroupRef) groupRefBeanModel.getBean();
      
      nameField = new TextField<String>();
      nameField.setName("name");
      nameField.setFieldLabel("Name");
      nameField.setAllowBlank(false);
      
      AdapterField panelField = new AdapterField(createPanelList(groupRef)) {
         @Override
         public boolean isValid(boolean silent) {
            if (panelListView.getSelectionModel().getSelectedItem() == null) {
               MessageBox.alert("ERROR", "Please select a panel to create group", null);
               return false;
            }
            return super.isValid(silent);
         }
      };
      panelField.setFieldLabel("Panel");
      add(nameField);
      add(panelField);
      
   }
   
   private ContentPanel createPanelList(GroupRef groupRef) {
      ContentPanel panelListContainer = new ContentPanel();
      panelListContainer.setHeaderVisible(false);
      panelListContainer.setWidth(210);
      panelListContainer.setHeight(150);
      panelListContainer.setLayout(new FitLayout());
      // overflow-auto style is for IE hack.
      panelListContainer.addStyleName("overflow-auto");
      
      panelListView = new ListView<BeanModel>();
      
      ListStore<BeanModel> store = new ListStore<BeanModel>();
      List<BeanModel> panelModels = BeanModelDataBase.panelTable.loadAll();
      for (BeanModel panelModel : panelModels) {
         store.add(panelModel);
      }
      panelListView.setHeight(150);
      panelListView.setStore(store);
      panelListView.setDisplayProperty("displayName");
      panelListView.setStyleAttribute("overflow", "auto");
      panelListContainer.add(panelListView);
      
      if (groupRef.getPanel() != null) {
         panelListView.getSelectionModel().select(groupRef.getPanel().getBeanModel(), false);
      }
       return panelListContainer;
   }
   
   private void addBeforeSubmitListener() {
      addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
            GroupRef groupRef = (GroupRef) groupRefBeanModel.getBean();
            groupRef.getGroup().setName(nameField.getValue());
            Panel selectedPanel = (Panel) panelListView.getSelectionModel().getSelectedItem().getBean();
            groupRef.setPanel(selectedPanel);
            groupRef.getGroup().setParentPanel(selectedPanel);
            selectedPanel.addGroupRef(groupRef);
            wrapper.fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(groupRefBeanModel));
         }

      });
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
   
   public BeanModel getSelectedItem() {
      return panelListView.getSelectionModel().getSelectedItem();
   }
}
