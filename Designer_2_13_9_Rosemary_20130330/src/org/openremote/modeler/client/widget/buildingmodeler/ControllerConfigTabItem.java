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

import java.util.HashSet;
import java.util.LinkedHashSet;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.client.proxy.ControllerConfigBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.ConfigCategory;
import org.openremote.modeler.shared.dto.ControllerConfigDTO;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
/**
 * A tab item for configuring the controller under a specific category. 
 * It would display at the layout's center part.
 * @author javen
 *
 */
public class ControllerConfigTabItem extends TabItem {

   private ConfigCategory category;
   private HashSet<ControllerConfigDTO> configs = null;
   private HashSet<ControllerConfigDTO> newConfigs = null;               //new configurations after the Controller-Config-2.0-M7.xml updated. 
   private Text hintContent = new Text();
   
   /** The config container includes all config infos. */
   private FormPanel configContainer = new FormPanel();
   
   /** The hint field set show the selected field's hint message. */
   private FieldSet hintFieldSet = new FieldSet();
   
   /**
    * Instantiates a new controller config tab item.
    * It includes configuration category and can manage it.
    * 
    * @param category the category
    */
   public ControllerConfigTabItem(ConfigCategory category){
      this.category = category;
      this.setHeight(500);
      this.setScrollMode(Scroll.AUTO);
      
      setText(category.getName());
      setClosable(true);
      setLayout(new FitLayout());  
      
      FormLayout layout = new FormLayout();
      layout.setLabelWidth(200);
      layout.setDefaultWidth(400);
      
      configContainer.setLayout(layout);
      configContainer.setAutoHeight(true);
      configContainer.setScrollMode(Scroll.AUTOY);
      configContainer.setButtonAlign(HorizontalAlignment.RIGHT);
      configContainer.setBorders(false);
      configContainer.setBodyBorder(false);
      configContainer.setLabelWidth(55);
      configContainer.setPadding(5);
      configContainer.setHeaderVisible(false);
      configContainer.setWidth(500);
      configContainer.setHeight(300);
      Button submitBtn =   new Button("Submit");
      submitBtn.addSelectionListener(new SaveListener());
      
      configContainer.addButton(submitBtn);
      add(configContainer);
      
      hintFieldSet.setLayout(new FitLayout());
      hintFieldSet.setHeading("Hint");
      
      
      hintContent.setText(category.getDescription());
      hintContent.setWidth("100%");
      hintContent.setHeight("34%");
      hintContent.setStyleAttribute("fontSize", "11px");
      hintContent.setStyleAttribute("fontFamily", Constants.DEFAULT_FONT_FAMILY);
      setStyleAttribute("overflowY", "auto");
      initForm();
   }
   
   
   /**
    * Gets current account's controller configurations and initializes the form. 
    * If the Controller-Config-2.0-M7.xml has updated, gets the new configurations too.
    */
   private void initForm() {
      if (configs == null) {
         ControllerConfigBeanModelProxy.getConfigDTOs(category, new AsyncSuccessCallback<HashSet<ControllerConfigDTO>>() {

            @Override
            public void onSuccess(HashSet<ControllerConfigDTO> result) {
               configs = result;
               for (ControllerConfigDTO config : configs) {
                  createProperty(config,false);
               }

               if (newConfigs == null) {
                  ControllerConfigBeanModelProxy.listAllMissingConfigDTOs(category.getName(),
                        new AsyncCallback<HashSet<ControllerConfigDTO>>() {

                           @Override
                           public void onFailure(Throwable caught) {
                              Info.display("Error", "Failed to load new controller configuration. ");
                           }

                           @Override
                           public void onSuccess(HashSet<ControllerConfigDTO> result) {
                              newConfigs = result;
                              for (ControllerConfigDTO config : newConfigs) {
                                 createProperty(config, true);
                              }
                              if (newConfigs.size() > 0) {
                                 LabelField label = new LabelField();
                                 label.setHideLabel(true);
                                 label.setText("(new configuration is marked as red)");
                                 label.setStyleAttribute("fontSize", "11px");
                                 label.setStyleAttribute("fontFamily", Constants.DEFAULT_FONT_FAMILY);
                                 configContainer.add(label);
                                 
                                 Info.display("Info",
                                 "The controller has be updated, you need to update your configurations.");
                              }
                              hintFieldSet.add(hintContent);
                              configContainer.add(hintFieldSet);
                              layout();
                           }

                        });
               }
               layout();
            }
         });
      }
   }


   public ConfigCategory getCategory() {
      return category;
   }


   public void setCategory(ConfigCategory category) {
      this.category = category;
   }
   
   /**
    * Creates the controllerConfig property as a field and add it into the configContainer.
    * If the property is new, highlight it with red color.
    * 
    * @param config the config
    * @param isNewConfig the is new config
    */
   private void createProperty(ControllerConfigDTO config,boolean isNewConfig) {
      if (config.getOptions().trim().length() == 0) {
        TextField<String> configValueField = new TextField<String>();
        if (config.getName().equals("rules.editor")) {
          configValueField = new TextArea();
          configValueField.setSize(200, 400);
        } else {
          configValueField = new TextField<String>();
          configValueField.setRegex(config.getValidation());
          configValueField.getMessages().setRegexText("This property must match: " + config.getValidation());
        }
        if (isNewConfig) {
          configValueField.setFieldLabel("<font color=\"red\">"+config.getName()+"</font>");
        } else {
          configValueField.setFieldLabel(config.getName());
        }
        configValueField.setName(config.getName());
        configValueField.setValue(config.getValue());
        addUpdateListenerToTextField(config,configValueField);
        configContainer.add(configValueField);
      } else {
         final ComboBox<ModelData> optionComboBox = new ComboBox<ModelData>();
         optionComboBox.setTriggerAction(TriggerAction.ALL);
         ListStore<ModelData> store = new ListStore<ModelData>();
         String[] options = config.optionsArray();
         for (int i = 0; i < options.length; i++) {
            ComboBoxDataModel<String> option = new ComboBoxDataModel<String>(options[i], options[i]);
            store.add(option);
         }
         optionComboBox.setValue(new ComboBoxDataModel<String>(config.getValue(),config.getValue()));
         optionComboBox.setStore(store);
         optionComboBox.setDisplayField(ComboBoxDataModel.getDisplayProperty());
         if (isNewConfig) {
            optionComboBox.setFieldLabel("<font color=\"red\">"+config.getName()+"</font>");
         } else {
            optionComboBox.setFieldLabel(config.getName());
         }
         optionComboBox.setName(config.getName() + "Options");
         optionComboBox.setAllowBlank(false);
         addUpdateListenerToComboBox(config,optionComboBox);
         configContainer.add(optionComboBox);
      }
   }
   
   /**
    * Adds the update listener to text field.
    * If blur, update the controllerConfig value. If focus, sets the hint message to hintContent.
    * 
    * @param config the config
    * @param configValueField the config value field
    */
   private void addUpdateListenerToTextField(final ControllerConfigDTO config,final TextField<String> configValueField){
      configValueField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            if(configValueField.isValid()){
               config.setValue(configValueField.getValue());
               hintContent.setText(category.getDescription());
            }
         }
      });
      configValueField.addListener(Events.Focus, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            hintContent.setText(config.getHint());
         }
      });
   }
   
   /**
    * Adds the update listener to combo box.
    * If select changed, update the controllerConfig value.
    * If focus or blur, sets the hint message to hintContent.
    * 
    * @param config the config
    * @param configValueComboBox the config value combo box
    */
   private void addUpdateListenerToComboBox(final ControllerConfigDTO config,final ComboBox<ModelData> configValueComboBox){
      configValueComboBox.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

         @SuppressWarnings("unchecked")
         @Override
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            ComboBoxDataModel<String> optionItem = (ComboBoxDataModel<String>) se.getSelectedItem();;
            String option = optionItem.getData();
            config.setValue(option);
         }
         
      });
      configValueComboBox.addListener(Events.Focus, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            hintContent.setText(config.getHint());
         }
      });
      
      configValueComboBox.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            hintContent.setText(category.getDescription());
         }
      });
   }
   
 /**
  * The listener interface for receiving save events.
  * The class that is interested in processing a save
  * event implements this interface, and the object created
  * with that class is registered with a component using the
  * component's <code>addSaveListener<code> method. When
  * the save event occurs, that object's appropriate
  * method is invoked.
  * 
  * It is for saving all controllerConfigs.
  * 
  * @see SaveEvent
  */
 class SaveListener extends SelectionListener<ButtonEvent>{
   @Override
   public void componentSelected(ButtonEvent ce) {
      HashSet<ControllerConfigDTO> allConfigs = new LinkedHashSet<ControllerConfigDTO>();
      allConfigs.addAll(configs);
      allConfigs.addAll(newConfigs);
      ControllerConfigBeanModelProxy.saveAllConfigDTOs(allConfigs, new AsyncSuccessCallback<HashSet<ControllerConfigDTO>>(){

        @Override
        public void onSuccess(HashSet<ControllerConfigDTO> result) {
          Info.display("save", "Property saved successfully");
          fireEvent(SubmitEvent.SUBMIT,new SubmitEvent(this)); // This will trigger refresh of the current selection, no need to do it here
        }         
      });
   }
    
 }
}
