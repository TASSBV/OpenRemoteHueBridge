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
package org.openremote.modeler.client.widget;

import java.util.List;

import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPairRef;
import org.openremote.modeler.domain.ScreenPair.OrientationType;
import org.openremote.modeler.domain.component.Navigate;
import org.openremote.modeler.domain.component.Navigate.ToLogicalType;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * The NavigateFieldSet is for editing navigation.
 */
public class NavigateFieldSet extends FieldSet {
   
   /** The navigate be edit. */
   private Navigate navigate;
   
   /** The panel's groups, for editing "toGroup" navigation. */
   private List<Group> groups;
   
   /** The radio is to make the "to logical" action part be enable or not. */
   private Radio toLogical = new Radio();
   
   /** The radio is to make the "to group" part be enable or not. */
   private Radio toGroup = new Radio();
   
   /** The type list combobox is for selecting "to logical" action type. */
   private ComboBoxExt typeList = new ComboBoxExt();
   
   /** The group list combobox is for selecting a group. */
   private ComboBoxExt groupList = new ComboBoxExt();
   
   /** The screen list combobox is for selecting a screen in a group. */
   private ComboBoxExt screenList = new ComboBoxExt();
   
   /**
    * Instantiates a new navigate field set.
    * It has two parts: the left part has the radios to select, the right part 
    * has comboboxes to select.
    * 
    * @param navigate the navigate
    * @param groups the groups
    */
   public NavigateFieldSet(Navigate navigate, List<Group> groups) {
      this.navigate = navigate;
      this.groups = groups;
      init();
      createLeftContainer();
      createRightComboBoxes();
   }
   private void init() {
      setLayout(new ColumnLayout());
      setHeading("Add Navigation");
   }
   
   /**
    * The left container includes: action radio and screen radio.
    */
   private void createLeftContainer() {
      LayoutContainer leftContainer = new LayoutContainer();
      
      RadioGroup radioGroup = new RadioGroup();
      radioGroup.setOrientation(Orientation.VERTICAL);
      
      toLogical.setBoxLabel("Action");
      toGroup.setBoxLabel("Screen");
      toGroup.addListener(Events.Change, new Listener<FieldEvent>() {
       @Override
       public void handleEvent(FieldEvent be) {
          Boolean value = (Boolean) be.getValue();
          if (value) {
             groupList.enable();
             screenList.enable();
             navigate.clearToLogical();
             typeList.clearSelections();
             typeList.disable();
          } else {
             navigate.setToGroup(-1);
             navigate.setToScreen(-1);
             screenList.clearSelections();
             groupList.clearSelections();
             groupList.disable();
             screenList.disable();
             typeList.enable();
          }
       }
       
    });

      radioGroup.add(toLogical);
      radioGroup.add(toGroup);

      //Text toScreen = new Text("ToScreen");
      //toScreen.setStyleAttribute("textAlign", "center");
      
      leftContainer.add(radioGroup);
      //leftContainer.add(toScreen);
      add(leftContainer);
   }
   
   /**
    * The right combo boxes includes: logical types, groups and screens.
    */
   private void createRightComboBoxes() {
      LayoutContainer rightComboBoxes = new LayoutContainer();
      FormLayout layout = new FormLayout();
      layout.setHideLabels(true);
      layout.setDefaultWidth(140);
      rightComboBoxes.setLayout(layout);
      rightComboBoxes.setLayoutOnChange(true);

      
      typeList.setEmptyText("--type--");
      ToLogicalType[] toTypes = ToLogicalType.values();
      for (int i = 0; i < toTypes.length; i++) {
         ComboBoxDataModel<ToLogicalType> toTypeItem = new ComboBoxDataModel<ToLogicalType>(toTypes[i].toString(),
               toTypes[i]);
         typeList.getStore().add(toTypeItem);
      }
      
      groupList.setEmptyText("--group--");
      groupList.disable();
      for (Group group : groups) {
         ComboBoxDataModel<Group> data = new ComboBoxDataModel<Group>(group.getName(), group);
         groupList.getStore().add(data);
      }
      screenList.setEmptyText("--screen--");
      screenList.disable();
      
      rightComboBoxes.add(typeList);
      rightComboBoxes.add(groupList);
      rightComboBoxes.add(screenList);
      
      typeList.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
         @SuppressWarnings("unchecked")
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            navigate.setToLogical(((ComboBoxDataModel<ToLogicalType>) se.getSelectedItem()).getData());
         }
      });
      
      groupList.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            updateScreenList(se);
         }
      });
      
      screenList.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
       @SuppressWarnings("unchecked")
       public void selectionChanged(SelectionChangedEvent<ModelData> se) {
          Screen selectedScreen = ((ComboBoxDataModel<Screen>) se.getSelectedItem()).getData();
          navigate.setToScreen(selectedScreen.getOid());
       }
       
    });
      add(rightComboBoxes);
   }
   
   /**
    * Update screen list in screen combobox after change the selected group.
    * 
    * @param se the se
    */
   @SuppressWarnings("unchecked")
   private void updateScreenList(SelectionChangedEvent<ModelData> se) {
      Group selectedGroup = ((ComboBoxDataModel<Group>) se.getSelectedItem()).getData();
      navigate.setToGroup(selectedGroup.getOid());
      screenList.clearSelections();
      screenList.getStore().removeAll();
      for (ScreenPairRef screenRef : selectedGroup.getScreenRefs()) {
         ScreenPair screenPair = screenRef.getScreen();
         if (OrientationType.PORTRAIT.equals(screenPair.getOrientation())) {
            ComboBoxDataModel<Screen> data = new ComboBoxDataModel<Screen>(screenPair.getPortraitScreen().getNameWithOrientation(), screenPair.getPortraitScreen());
            screenList.getStore().add(data);
            if (navigate.getToScreen() == screenPair.getPortraitScreen().getOid()) {
               screenList.setValue(data);
            }
         } else if(OrientationType.LANDSCAPE.equals(screenPair.getOrientation())) {
            ComboBoxDataModel<Screen> data = new ComboBoxDataModel<Screen>(screenPair.getLandscapeScreen().getNameWithOrientation(), screenPair.getLandscapeScreen());
            screenList.getStore().add(data);
            if (navigate.getToScreen() == screenPair.getLandscapeScreen().getOid()) {
               screenList.setValue(data);
            }
         } else if (OrientationType.BOTH.equals(screenPair.getOrientation())) {
            ComboBoxDataModel<Screen> data1 = new ComboBoxDataModel<Screen>(screenPair.getPortraitScreen().getNameWithOrientation(), screenPair.getPortraitScreen());
            screenList.getStore().add(data1);
            if (navigate.getToScreen() == screenPair.getPortraitScreen().getOid()) {
               screenList.setValue(data1);
            }
            ComboBoxDataModel<Screen> data2 = new ComboBoxDataModel<Screen>(screenPair.getLandscapeScreen().getNameWithOrientation(), screenPair.getLandscapeScreen());
            screenList.getStore().add(data2);
            if (navigate.getToScreen() == screenPair.getLandscapeScreen().getOid()) {
               screenList.setValue(data2);
            }
         }
      }
      if (screenList.getValue() == null) {
         navigate.setToScreen(-1);
      }
   }

   /**
    * Update the field set by navigate.
    * 
    * @param navigate the navigate
    */
   public void update(Navigate navigate) {
      this.navigate = navigate;
      if (navigate.isToLogic()) {
         toLogical.setValue(true);
         typeList.setValue(new ComboBoxDataModel<ToLogicalType>(navigate.getToLogical().toString(), navigate.getToLogical()));
         toGroup.setValue(false);
      } else if (navigate.toGroup()) {
         toGroup.setValue(true);
         for (Group group : groups) {
            ComboBoxDataModel<Group> data = new ComboBoxDataModel<Group>(group.getName(), group);
            if (navigate.getToGroup() == group.getOid()) {
               groupList.setValue(data);
          }
         }
         toLogical.setValue(false);
      } else {
         toLogical.setValue(false);
         toGroup.setValue(false);
      }
   }
}
