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
package org.openremote.modeler.client.widget.buildingmodeler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openremote.modeler.client.event.DeviceWizardEvent;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.shared.dto.DTO;
import org.openremote.modeler.shared.dto.DTOHelper;
import org.openremote.modeler.shared.dto.DTOReference;
import org.openremote.modeler.shared.dto.DeviceCommandDetailsDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * The wizard window to create new sensor, but not save into server.
 */
public class SensorWizardWindow extends SensorWindow {

  private TreeStore<BeanModel> commandTreeStore; 
  
   public SensorWizardWindow(ArrayList<DeviceCommandDetailsDTO> commands) {
      super(null, null); // Pass dummy data, not used
      // Must populate store here, as buildCommandSelectTree is called by our parent's constructor,
      // so we don't have time to store commands before that method gets called for it to use while building tree 
      for (DeviceCommandDetailsDTO deviceCommand : commands) {
        commandTreeStore.add(DTOHelper.getBeanModel(deviceCommand), false);
      }
      addNewCommandButton();
      form.removeAllListeners();
      onSubmit();
   }
   
   @Override
   protected void buildCommandSelectTree() {
      commandTreeStore = new TreeStore<BeanModel>();
      commandSelectTree = new TreePanel<BeanModel>(commandTreeStore);
      commandSelectTree.setBorders(false);
      commandSelectTree.setStateful(true);
      commandSelectTree.setDisplayProperty("name");
      commandSelectTree.setStyleAttribute("overflow", "auto");
      commandSelectTree.setHeight("100%");
      commandSelectTree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            return ((Icons) GWT.create(Icons.class)).deviceCmd();
         }
      });
   }

   private void addNewCommandButton() {
      Button newCommandButton = new Button("New command..");
      newCommandButton.addSelectionListener(new NewCommandListener());
      AdapterField newCommandField = new AdapterField(newCommandButton);
      newCommandField.setLabelSeparator("");
      form.insert(newCommandField, 2);
      layout();
   }
   
   /**
    * Add the new sensor into the current device.
    * According to the different sensor type, get the different sensor content.
    */
   private void onSubmit() {
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         @SuppressWarnings("unchecked")
         public void handleEvent(FormEvent be) {
           BeanModel selectedCommand = commandSelectTree.getSelectionModel().getSelectedItem();
           if (typeList.getValue() == null) {
              MessageBox.alert("Warn", "A sensor must have a type", null);
              submitBtn.enable();
              typeList.focus();
              return;
           }
           if (selectedCommand == null || !(selectedCommand.getBean() instanceof DeviceCommandDetailsDTO)) {
              MessageBox.alert("Warn", "A sensor must have a device command", null);
              submitBtn.enable();
              commandSelectTree.focus();
              return;
           }
           
           SensorType type = ((ComboBoxDataModel<SensorType>) typeList.getValue()).getData();            
           sensorDTO.setType(type);
           
           if (type == SensorType.RANGE) {
             sensorDTO.setMinValue(Integer.valueOf(minField.getRawValue()));
             sensorDTO.setMaxValue(Integer.valueOf(maxField.getRawValue()));
           } else if (type == SensorType.CUSTOM) {
             HashMap<String,String> sensorStates = new HashMap<String,String>();

              List<BaseModelData> states = grid.getStore().getModels();
              for (BaseModelData stateModel : states) {
                sensorStates.put((String)stateModel.get("name"), (String)stateModel.get("value"));
              }
              sensorDTO.setStates(sensorStates);
           }
           sensorDTO.setName(nameField.getValue());
           
           DTO cmd = selectedCommand.getBean();
           sensorDTO.setCommand(new DTOReference(cmd));
           fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(sensorDTO));
         }

      });
   }
   
   /**
    * This listener pops up a command window  to create a new device command for the current device.
    * And the current sensor can select the new command. 
    */
   private final class NewCommandListener extends SelectionListener<ButtonEvent> {
      @Override
      public void componentSelected(ButtonEvent ce) {
         DeviceCommandWizardWindow deviceCommandWizardWindow = new DeviceCommandWizardWindow();
         deviceCommandWizardWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               DeviceCommandDetailsDTO deviceCommand = be.getData();
               BeanModel deviceCommandModel = DTOHelper.getBeanModel(deviceCommand);
               commandSelectTree.getStore().add(deviceCommandModel, false);
               commandSelectTree.getSelectionModel().select(deviceCommandModel, false);
               fireEvent(DeviceWizardEvent.ADD_CONTENT, new DeviceWizardEvent(deviceCommandModel));
               Info.display("Info", "Create command " + deviceCommand.getName() + " success");
            }
         });
      }
   }
}
