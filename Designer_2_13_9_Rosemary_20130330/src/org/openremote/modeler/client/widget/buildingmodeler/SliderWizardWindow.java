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
import java.util.List;

import org.openremote.modeler.client.event.DeviceWizardEvent;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.DeviceWizardListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.client.utils.DeviceCommandWizardSelectWindow;
import org.openremote.modeler.shared.dto.DTOHelper;
import org.openremote.modeler.shared.dto.DTOReference;
import org.openremote.modeler.shared.dto.DeviceCommandDetailsDTO;
import org.openremote.modeler.shared.dto.SensorDetailsDTO;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.Field;

/**
 * The wizard window to create a new slider for the current device.
 */
public class SliderWizardWindow extends SliderWindow {

  private ArrayList<DeviceCommandDetailsDTO> commands;

  public SliderWizardWindow(ArrayList<DeviceCommandDetailsDTO> commands, ArrayList<SensorDetailsDTO> sensors) {
      super(null, null);
      this.commands = commands;
      populateSensorFieldStore(sensors);
      addNewSensorButton();
      addSensorFieldListener();
      addCommandSelectListener();
      form.removeAllListeners();
      onSubmit();
   }

  @Override
  protected void populateSensorFieldStore() {
    // Don't load anything here, we'll load just after coming back from our parent's constructor
  }

  private void populateSensorFieldStore(ArrayList<SensorDetailsDTO> sensors) {
     ListStore<ModelData> sensorStore = sensorField.getStore();
     sensorStore.removeAll();
     for (SensorDetailsDTO sensor : sensors) {
        ComboBoxDataModel<SensorDetailsDTO> sensorRefSelector = new ComboBoxDataModel<SensorDetailsDTO>(sensor.getName(), sensor);
        sensorStore.add(sensorRefSelector);
     }
  }

  private void addSensorFieldListener() {
    sensorField.removeAllListeners();
    sensorField.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

       @SuppressWarnings("unchecked")
       @Override
       public void selectionChanged(SelectionChangedEvent<ModelData> se) {
         ComboBoxDataModel<SensorDetailsDTO> sensorItem = (ComboBoxDataModel<SensorDetailsDTO>) se.getSelectedItem();
         sliderDTO.setSensor(new DTOReference(sensorItem.getData()));
       }
    });
  }

   /**
    * Adds the button for adding a new sensor to the current device.
    */
   private void addNewSensorButton() {
      Button newSensorButton = new Button("New sensor..");
      newSensorButton.addSelectionListener(new NewSensorListener());
      AdapterField newSensorField = new AdapterField(newSensorButton);
      newSensorField.setLabelSeparator("");
      form.insert(newSensorField, 2);
      layout();
   }
   
   private void addCommandSelectListener() {
      setValueBtn.removeAllListeners();
      setValueBtn.addSelectionListener(new CommandSelectionListener());
   }
   
   private void onSubmit() {
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
           if (sliderDTO.getCommand() == null) {
             MessageBox.alert("Slider", "A slider must have a command defined to set its value", null);
             submitBtn.enable();
             return;
           }
           if (sliderDTO.getSensor() == null) {
             MessageBox.alert("Slider", "A slider must have a sensor defined to read its value", null);
             submitBtn.enable();
             return;
           }

           List<Field<?>> fields = form.getFields();
            for (Field<?> field : fields) {
               if (SLIDER_NAME_FIELD_NAME.equals(field.getName())) {
                  sliderDTO.setName(field.getValue().toString());
                  break;
               }
            }
            // TODO : account ? should be set on backend
            
            fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(sliderDTO));
         }
      });
   }

   /**
    * The listener to create a new sensor for the current device.
    */
   private final class NewSensorListener extends SelectionListener<ButtonEvent> {
      @Override
      public void componentSelected(ButtonEvent ce) {
        final SensorWizardWindow sensorWizardWindow = new SensorWizardWindow(commands);
        sensorWizardWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
           @Override
           public void afterSubmit(SubmitEvent be) {
              SensorDetailsDTO sensor = be.getData();
              BeanModel sensorModel = DTOHelper.getBeanModel(sensor);               
              ComboBoxDataModel<SensorDetailsDTO> sensorRefSelector = new ComboBoxDataModel<SensorDetailsDTO>(sensor.getName(), sensor);
              sensorField.getStore().add(sensorRefSelector);
              sensorField.setValue(sensorRefSelector);
              sliderDTO.setSensor(new DTOReference(sensor));               
              sensorWizardWindow.hide();               
              fireEvent(DeviceWizardEvent.ADD_CONTENT, new DeviceWizardEvent(sensorModel));
           }
        });
        
        // When new command is created from the sensor wizard window, this passes it down so it's added to the "device" command lists 
        sensorWizardWindow.addListener(DeviceWizardEvent.ADD_CONTENT, new DeviceWizardListener() {
           @Override
           public void afterAdd(DeviceWizardEvent be) {
              fireEvent(DeviceWizardEvent.ADD_CONTENT, new DeviceWizardEvent(be.getData()));
           }
        });
        sensorWizardWindow.show();
      }
   }
   
   private final class CommandSelectionListener extends SelectionListener<ButtonEvent> {
      @Override
      public void componentSelected(ButtonEvent ce) {
         final DeviceCommandWizardSelectWindow selectCommandWindow = new DeviceCommandWizardSelectWindow(commands);
         final Button command = ce.getButton();
         selectCommandWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               BeanModel dataModel = be.<BeanModel> getData();
               DeviceCommandDetailsDTO deviceCommand = dataModel.getBean();
               command.setText(deviceCommand.getName());
               sliderDTO.setCommand(new DTOReference(deviceCommand));
            }
         });
      }
   }
}
