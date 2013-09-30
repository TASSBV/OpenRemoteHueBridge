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
import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPairRef;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
/**
 * A wizard for creating a new screen from existing groups.
 *
 * @author Javen
 * @author <a href = "mailto:juha@openremote.org">Juha Lindfors</a>
 *
 */
public class ScreenWindow extends FormWindow {
   
   private Screen screen = null;
   
   private TextField<String> nameField = null;
   private BeanModel selectItem = null;
   
   private Operation operation = Operation.NEW;
   private TreePanel<BeanModel> groupSelectTree = null;
   
   private Button submitBtn;
   
   public ScreenWindow(BeanModel selectItem, Operation operation) {
      super();
      this.operation = operation;
      this.selectItem = selectItem;
      
      setSize(350, 300);
      setHeading("New Screen");
      if(operation == Operation.EDIT){
         setSize(350, 150);
         setHeading("Edit Screen");
      }
      setLayout(new FillLayout());
      setModal(true);
      createButtons();
      createFields();
      setBodyBorder(false);
      add(form);
      show();
   }

   public ScreenWindow(BeanModel selectItem) {
      this(selectItem, Operation.NEW);
   }
   
   
   public void createFields() {
      form.setHeaderVisible(false);
      form.setBorders(false);
      form.setBodyBorder(true);
      form.setLabelWidth(60);
      nameField = new TextField<String>();
      nameField.setAllowBlank(false);
      nameField.setFieldLabel("Name");
      nameField.setName("name");
      form.add(nameField);
      
      if (operation == Operation.NEW) {
         AdapterField adapterField = new AdapterField(createGroupTreeView());
         adapterField.setFieldLabel("Group");
         adapterField.setBorders(true);
         form.add(adapterField);
      } else if (operation == Operation.EDIT){
         nameField.setValue(((ScreenPairRef) selectItem.getBean()).getScreen().getName());
      }
      addBeforHideListener();
   }
   
   private void createButtons() {
      submitBtn = new Button("Submit");
      Button resetBtn = new Button("Reset");
      submitBtn.addSelectionListener(new FormSubmitListener(form, submitBtn));
      resetBtn.addSelectionListener(new FormResetListener(form));
      
      form.addButton(submitBtn);
      form.addButton(resetBtn);
   }
   private void addBeforHideListener() {
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {

         @Override
         public void handleEvent(FormEvent be) {
            ScreenPairRef screenRef = null;
            if (operation == Operation.EDIT) {
               screenRef = (ScreenPairRef) selectItem.getBean();
               screenRef.getScreen().setName(nameField.getValue());
            } else {
               BeanModel groupModel = groupSelectTree.getSelectionModel().getSelectedItem();
               if (groupModel == null || !(groupModel.getBean() instanceof GroupRef)) {
                  MessageBox.alert("New Screen Error", "Please select a group.", null);
                  submitBtn.enable();
                  be.cancelBubble();
                  return;
               }
               Object bean = groupModel.getBean();
               if (bean != null && bean instanceof GroupRef) {
                  final GroupRef groupRef = (GroupRef) bean;
                  screenRef = createScreenRef(groupRef);
                  screen.setName(nameField.getValue());
                  if (groupRef.getGroup().getTabbarItems().size() > 0
                        || groupRef.getPanel().getTabbarItems().size() > 0) {
                     screen.setHasTabbar(true);
                  }
                  BeanModelDataBase.screenTable.insert(screenRef.getScreen().getBeanModel());
                  screen.setName(nameField.getValue());
                  screenRef.setGroup(groupRef.getGroup());

               }
            }
            fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(screenRef));
           
         }


      });
   }

   private ScreenPairRef createScreenRef(GroupRef selectedGroup) {
      screen = new Screen();
      screen.setOid(IDUtil.nextID());
      screen.setTouchPanelDefinition(selectedGroup.getPanel().getTouchPanelDefinition());
      ScreenPair screenPair = new ScreenPair();
      screenPair.setOid(IDUtil.nextID());
      screenPair.setTouchPanelDefinition(selectedGroup.getPanel().getTouchPanelDefinition());
      screenPair.setPortraitScreen(screen);
      screenPair.setParentGroup(selectedGroup.getGroup());
      
      ScreenPairRef screenRef = new ScreenPairRef(screenPair);
      screenRef.setTouchPanelDefinition(selectedGroup.getPanel().getTouchPanelDefinition());
      selectedGroup.getGroup().addScreenRef(screenRef);
      return screenRef;
   }
   
   private ContentPanel createGroupTreeView() {
      ContentPanel groupTreeContainer = new ContentPanel();
      groupTreeContainer.setHeaderVisible(false);
      groupTreeContainer.setSize(210, 150);
      groupTreeContainer.setLayout(new FitLayout());
      // overflow-auto style is for IE hack.
      groupTreeContainer.addStyleName("overflow-auto");
      List<BeanModel> panels = BeanModelDataBase.panelTable.loadAll();
      groupSelectTree = buildGroupSelectTree(panels);
      groupTreeContainer.add(groupSelectTree);
      groupTreeContainer.setEnabled(operation==Operation.NEW);
      groupTreeContainer.setStyleAttribute("backgroundColor", "white");

//      if (null != this.selectItem) {
//         if (this.selectItem.getBean() instanceof GroupRef && (operation==Operation.NEW)) {
//            groupSelectTree.getSelectionModel().select(selectItem, false);
//         } 
//      }
      return groupTreeContainer;
   }

   private TreePanel<BeanModel> buildGroupSelectTree(List<BeanModel> panels) {
      TreeStore<BeanModel> groups = new TreeStore<BeanModel>();
      TreePanel<BeanModel> groupTree = TreePanelBuilder.buildPanelTree(groups);
      groups.add(panels, false);
      for (BeanModel panelModel : panels) {
         Panel panel = panelModel.getBean();
         List<GroupRef> groupRefs = panel.getGroupRefs();
         for (GroupRef ref : groupRefs) {
            groups.add(panelModel, ref.getBeanModel(), false);
         }
      }
      return groupTree;
   }


   public BeanModel getSelectedGroupRefModel() {
      return (BeanModel) groupSelectTree.getSelectionModel().getSelectedItem();
   }
  
   public static enum Operation{
      NEW,EDIT;
   }

   protected void afterRender() {
      super.afterRender();
      if (null != this.selectItem) {
         if (this.selectItem.getBean() instanceof GroupRef && (operation==Operation.NEW)) {
            groupSelectTree.getSelectionModel().select(selectItem, false);
         } 
      }
   }
   
   
}
