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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.event.DeviceUpdatedEvent;
import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.client.proxy.DeviceCommandBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.utils.Protocols;
import org.openremote.modeler.client.widget.ComboBoxExt;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.protocol.ProtocolAttrDefinition;
import org.openremote.modeler.protocol.ProtocolDefinition;
import org.openremote.modeler.protocol.ProtocolValidator;
import org.openremote.modeler.selenium.DebugId;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.DeviceCommandDetailsDTO;
import org.openremote.modeler.shared.dto.DeviceDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.event.shared.EventBus;

/**
 * The window creates or updates a deviceCommand into server.
 */
public class DeviceCommandWindow extends FormWindow {
   
   /** The Constant DEVICE_COMMAND_NAME. */
   public static final String DEVICE_COMMAND_NAME = "device_command_name";
   
   /** The Constant DEVICE_COMMAND_PROTOCOL. */
   public static final String DEVICE_COMMAND_PROTOCOL = "protocol";
   
   private EventBus eventBus;
   
   private DeviceDTO device;
   protected DeviceCommandDetailsDTO deviceCommand = null;
   
   protected boolean hideWindow = true;
   
   protected LabelField info;
   protected static final String INFO_FIELD = "infoField";
   /**
    * Instantiates a new device command window.
    * 
    * @param device the device
    */
   public DeviceCommandWindow(DeviceDTO device, EventBus eventBus) {
      super();
      this.device = device;
      this.eventBus = eventBus;
      setHeading("New command");
      this.deviceCommand = new DeviceCommandDetailsDTO();
      initial();
      show();
   }
   
   /**
    * Instantiates a new device command window.
    * 
    * @param command the command
    */
   public DeviceCommandWindow(final DeviceCommandDTO command, DeviceDTO device, EventBus eventBus) {
      super();
      this.device = device;
      this.eventBus = eventBus;
      setHeading("Edit command");
      AsyncServiceFactory.getDeviceCommandServiceAsync().loadCommandDetailsDTO(command.getOid(),
            new AsyncSuccessCallback<DeviceCommandDetailsDTO>() {
         public void onSuccess(DeviceCommandDetailsDTO cmd) {
           deviceCommand = cmd;
           initial(); // TODO: review to display so form of wait message while loading the command
           show(); // TODO EBR: this should just be layout, show() being called by caller, but doesn't work
         }
      });
   }
   
   /**
    * Initial.
    */
   private void initial() {
      setWidth(380);
      setAutoHeight(true);
      setLayout(new FlowLayout());
      
      form.setWidth(370);

      Button submitBtn = new Button("Submit");
      form.addButton(submitBtn);

      submitBtn.addSelectionListener(new FormSubmitListener(form, submitBtn));
      if (deviceCommand.getOid() == null) {
         info = new LabelField();
         info.setName(INFO_FIELD);
         form.add(info);
         info.hide();
         Button continueButton = new Button("Submit and Continue");
         form.addButton(continueButton);
         continueButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
               if (form.isValid()) {
                  hideWindow = false;
                  mask("Saving command......");
                  form.submit();
               }
            }
         });
      }
      
      Button resetButton = new Button("Reset");
      resetButton.addSelectionListener(new FormResetListener(form));
      form.addButton(resetButton);
      
      
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         @SuppressWarnings("unchecked")
         public void handleEvent(FormEvent be) {
            populateDeviceCommandFromFields();

            AsyncSuccessCallback<Void> callback = new AsyncSuccessCallback<Void>() {
              @Override
              public void onSuccess(Void result) {
                eventBus.fireEvent(new DeviceUpdatedEvent(device)); // TODO have a specific event for command update
                 if (hideWindow) {
                    hide();
                 } else {
                    hideWindow = true;
                    unmask();
                    info.setText("Command '" + deviceCommand.getName() + "' is saved.");
                    info.show();
                 }
              }
           };
           if (deviceCommand.getOid() == null) {
              DeviceCommandBeanModelProxy.saveNewDeviceCommand(deviceCommand, device.getOid(), callback);
            } else {              
              DeviceCommandBeanModelProxy.updateDeviceCommandWithDTO(deviceCommand, callback);
            }
         }

      });
      createFields(Protocols.getInstance());
      add(form);
   }
   
   protected void populateDeviceCommandFromFields() {
     List<Field<?>> list = form.getFields();
     HashMap<String, String> attrMap = new HashMap<String, String>();
     for (Field<?> f : list) {
        if (DEVICE_COMMAND_PROTOCOL.equals(f.getName())) {
           Field<BaseModelData> p = (Field<BaseModelData>) f;
           attrMap.put(DEVICE_COMMAND_PROTOCOL, p.getValue().get(ComboBoxDataModel.getDisplayProperty())
                 .toString());
        } else {
           if (f.getValue() != null && !"".equals(f.getValue().toString()) && ! INFO_FIELD.equals(f.getName())) {
              attrMap.put(f.getName(), f.getValue().toString());
           }
        }
     }
     deviceCommand.setName(attrMap.get(DEVICE_COMMAND_NAME));
     deviceCommand.setProtocolType(attrMap.get(DEVICE_COMMAND_PROTOCOL));
     attrMap.remove(DEVICE_COMMAND_NAME);
     attrMap.remove(DEVICE_COMMAND_PROTOCOL);
     deviceCommand.setProtocolAttributes(attrMap);
   }

   /**
    * Creates the fields.
    * 
    * @param protocols the protocols
    */
   private void createFields(Map<String, ProtocolDefinition> protocols) {
      TextField<String> nameField = new TextField<String>();
      nameField.setName(DEVICE_COMMAND_NAME);
      nameField.setFieldLabel("Name");
      nameField.setAllowBlank(false);
      nameField.ensureDebugId(DebugId.DEVICE_COMMAND_NAME_FIELD);

      ComboBoxExt protocol = new ComboBoxExt();
      protocol.setFieldLabel("Protocol");
      protocol.setName(DEVICE_COMMAND_PROTOCOL);
      protocol.setAllowBlank(false);
      protocol.ensureDebugId(DebugId.DEVICE_COMMAND_PROTOCOL_FIELD);
      
      for (String key : protocols.keySet()) {
         if (!key.equalsIgnoreCase(Protocol.INFRARED_TYPE)) {
            ComboBoxDataModel<ProtocolDefinition> data = new ComboBoxDataModel<ProtocolDefinition>(key, protocols.get(key));
            protocol.getStore().add(data);
         }
      }

      protocol.setDisplayField(ComboBoxDataModel.getDisplayProperty());
      protocol.setEmptyText("Please Select Protocol...");
      protocol.setValueField(ComboBoxDataModel.getDisplayProperty());

      form.add(nameField);
      form.add(protocol);
      protocol.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
         @SuppressWarnings("unchecked")
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            if (form.getItems().size() > 2) {
               if (info == null) {
                  form.getItem(2).removeFromParent();
               } else if (form.getItems().size() > 3) {
                  form.getItem(3).removeFromParent();
               }
            }
            addAttrs((ComboBoxDataModel<ProtocolDefinition>) se.getSelectedItem());
         }
      });

      if (deviceCommand.getOid() != null) {
         String protocolName = deviceCommand.getProtocolType();
         nameField.setValue(deviceCommand.getName());
         if (protocols.containsKey(protocolName)) {
            ComboBoxDataModel<ProtocolDefinition> data = new ComboBoxDataModel<ProtocolDefinition>(protocolName, protocols
                  .get(protocolName));
            protocol.setValue(data);
         }
//         protocol.disable();
      }
      form.layout();
   }
   
   /**
    * Adds the attrs.
    * 
    * @param data the data
    */
   private void addAttrs(ComboBoxDataModel<ProtocolDefinition> data) {
      FieldSet attrSet = new FieldSet();
      FormLayout layout = new FormLayout();
      layout.setLabelWidth(80);
      attrSet.setLayout(layout);
      attrSet.setHeading(data.getLabel() + " attributes");

      for (ProtocolAttrDefinition attrDefinition : data.getData().getAttrs()) {
         List<String> options = attrDefinition.getOptions();
         String value = "";
         if (attrDefinition.getValue() != null) {
            value = attrDefinition.getValue();
         } else if (deviceCommand.getOid() != null) {
            for (Map.Entry<String, String> entry : deviceCommand.getProtocolAttributes().entrySet()) {
               if (attrDefinition.getName().equals(entry.getKey())) {
                  value = entry.getValue();
               }
            }
         }

         if (options.size() > 0) {
            ComboBoxExt comboAttrField = new ComboBoxExt();
            comboAttrField.setName(attrDefinition.getName());
            if (attrDefinition.getTooltipMessage() != null) {
              comboAttrField.setToolTip(attrDefinition.getTooltipMessage());
            }
            comboAttrField.setFieldLabel(attrDefinition.getLabel());
            ComboBoxExt.ComboBoxMessages comboBoxMessages = comboAttrField.getMessages();
            for (String option : options) {
               if (!"".equals(option)) {
                  StringComboBoxData comboData = new StringComboBoxData(option, option);
                  comboAttrField.getStore().add(comboData);
                  if (value.equals(option)) {
                     comboAttrField.setValue(comboData);
                  }
               }
            }
            setComboBoxValidators(comboAttrField, comboBoxMessages, attrDefinition.getValidators());
            attrSet.add(comboAttrField);
         } else {
            TextField<String> attrField = new TextField<String>();
            attrField.setName(attrDefinition.getName());
            if (attrDefinition.getTooltipMessage() != null) {
              attrField.setToolTip(attrDefinition.getTooltipMessage());
            }
            TextField<String>.TextFieldMessages messages = attrField.getMessages();
            attrField.setFieldLabel(attrDefinition.getLabel());
            if (!"".equals(value)) {
               attrField.setValue(value);
            }
            setValidators(attrField, messages, attrDefinition.getValidators());
            attrSet.add(attrField);
         }
      }
      form.add(attrSet);
      form.layout();
      if (isRendered()) {
         center();
      }
   }
   
   /**
    * Sets the validators.
    * 
    * @param attrField the attr file
    * @param messages the messages
    * @param protocolValidators the protocol validators
    */
   private void setValidators(TextField<String> attrField, TextField<String>.TextFieldMessages messages,
         List<ProtocolValidator> protocolValidators) {
      for (ProtocolValidator protocolValidator : protocolValidators) {
         if (protocolValidator.getType() == ProtocolValidator.ALLOW_BLANK_TYPE) {
            if (Boolean.valueOf(protocolValidator.getValue())) {
               attrField.setAllowBlank(true);
            } else {
               attrField.setAllowBlank(false);
               messages.setBlankText(protocolValidator.getMessage());
            }
         } else if (protocolValidator.getType() == ProtocolValidator.MAX_LENGTH_TYPE) {
            attrField.setMaxLength(Integer.valueOf(protocolValidator.getValue()));
            messages.setMaxLengthText(protocolValidator.getMessage());
         } else if (protocolValidator.getType() == ProtocolValidator.MIN_LENGTH_TYPE) {
            attrField.setMinLength(Integer.valueOf(protocolValidator.getValue()));
            messages.setMinLengthText(protocolValidator.getMessage());
         } else if (protocolValidator.getType() == ProtocolValidator.REGEX_TYPE) {
            attrField.setRegex(protocolValidator.getValue());
            messages.setRegexText(protocolValidator.getMessage());
         }
      }
   }
   
   private void setComboBoxValidators(ComboBoxExt comboField, ComboBoxExt.ComboBoxMessages messages,
         List<ProtocolValidator> protocolValidators) {
      for (ProtocolValidator protocolValidator : protocolValidators) {
         if (protocolValidator.getType() == ProtocolValidator.ALLOW_BLANK_TYPE) {
            if (Boolean.valueOf(protocolValidator.getValue())) {
               comboField.setAllowBlank(true);
            } else {
               comboField.setAllowBlank(false);
               messages.setBlankText(protocolValidator.getMessage());
            }
         } else if (protocolValidator.getType() == ProtocolValidator.MAX_LENGTH_TYPE) {
            comboField.setMaxLength(Integer.valueOf(protocolValidator.getValue()));
            messages.setMaxLengthText(protocolValidator.getMessage());
         } else if (protocolValidator.getType() == ProtocolValidator.MIN_LENGTH_TYPE) {
            comboField.setMinLength(Integer.valueOf(protocolValidator.getValue()));
            messages.setMinLengthText(protocolValidator.getMessage());
         } else if (protocolValidator.getType() == ProtocolValidator.REGEX_TYPE) {
            comboField.setRegex(protocolValidator.getValue());
            messages.setRegexText(protocolValidator.getMessage());
         }
      }
   }
   
   /**
    * The data model to store String value in ComboBoxExt.
    */
   private class StringComboBoxData extends ComboBoxDataModel<String> {
      private static final long serialVersionUID = 1L;
      public StringComboBoxData(String label, String t) {
         super(label, t);
      }
      public String toString() {
         return super.getData();
      }
   }
}
