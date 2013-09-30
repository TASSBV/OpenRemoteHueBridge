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
import java.util.Map;

import org.openremote.modeler.client.event.DeviceUpdatedEvent;
import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.client.proxy.SensorBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.ComboBoxExt;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.shared.dto.DTOReference;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.modeler.shared.dto.SensorDetailsDTO;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.event.shared.EventBus;

/**
 * The window to create or update a sensor.
 */
public class SensorWindow extends FormWindow {

  private EventBus eventBus;
  
  private DeviceDTO device;
  
   protected SensorDetailsDTO sensorDTO;
   
   private boolean edit;
   
   protected TextField<String> nameField = new TextField<String>();
   
   /** The tree for selecting a command for the sensor. */
   protected TreePanel<BeanModel> commandSelectTree = null;
   
   /** The combobox for selecting a type for the sensor. */
   protected ComboBoxExt typeList = new ComboBoxExt();
   
   /** The field set for managing custom sensor states. */
   private FieldSet customFieldSet = null;
   
   /** The min field for range sensor. */
   protected TextField<Integer> minField = new TextField<Integer>();
   
   /** The max field for range sensor. */
   protected TextField<Integer> maxField = new TextField<Integer>();
   
   /** The grid to store custom sensor states. */
   protected EditorGrid<BaseModelData> grid = null;
   private int stateRowIndex = -1;
   
   protected Button submitBtn;
   
   /**
    * Instantiates a window to edit a sensor.
    * 
    * @param sensorModel the sensor model
    */
   public SensorWindow(BeanModel sensorModel, DeviceDTO device, EventBus eventBus) {
     this.device = device;
     this.eventBus = eventBus;
      setHeading("Edit sensor");
      edit = true;

      // TODO : have a display stating we're loading
      
      SensorBeanModelProxy.loadSensorDetails(sensorModel, new AsyncSuccessCallback<BeanModel>() {  
        public void onSuccess(BeanModel result) {
          SensorWindow.this.sensorDTO = result.getBean();
          init();
          setHeight(300); // Somehow setting the height her is required for the autoheight calculation to work when layout is called 
          layout();
        }
      });
   }
   
   /**
    * Instantiates a window to create a new sensor.
    * 
    * @param device the device
    */
   public SensorWindow(DeviceDTO device, EventBus eventBus) {
     super();
     this.device = device;
     this.eventBus = eventBus;
     sensorDTO = new SensorDetailsDTO();
      setHeading("New sensor");
      edit = false;
      init();
   }
   
   /**
    * Inits the window style and the form contents.
    */
   private void init() {
      setWidth(360);
      setAutoHeight(true);
      setLayout(new FlowLayout());
      form.setFrame(true);
      form.setHeaderVisible(false);
      form.setBorders(false);
      form.setLabelWidth(60);
      form.setFieldWidth(230);
      createButtons();
      createFields();
      add(form);
      addListenerToForm();
   }
   
   /**
    * Creates the fields.
    */
   private void createFields() {
      nameField.setFieldLabel("Name");
      nameField.setAllowBlank(false);
      
      AdapterField commandField = new AdapterField(createCommandTreeView());
      commandField.setFieldLabel("Command");
      commandField.setBorders(true);
      
      SensorType[] sensorTypes = SensorType.values();
      typeList.setFieldLabel("Type");
      typeList.setEmptyText("--type--");
      for (int i = 0; i < sensorTypes.length; i++) {
         ComboBoxDataModel<SensorType> typeItem = new ComboBoxDataModel<SensorType>(sensorTypes[i].toString(), sensorTypes[i]);
         typeList.getStore().add(typeItem);
      }
      
      form.add(nameField);
      form.add(commandField);
      form.add(typeList);
      
      final FieldSet rangeSet = createRangeSet();
      form.add(rangeSet);
      rangeSet.hide();
      
      typeList.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
         @SuppressWarnings("unchecked")
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            SensorType type = ((ComboBoxDataModel<SensorType>) se.getSelectedItem()).getData();
            if (type == SensorType.RANGE) {
               if (customFieldSet != null) {
                  customFieldSet.hide();
               }
               rangeSet.show();
            } else if (type == SensorType.CUSTOM) {
               rangeSet.hide();
               if (customFieldSet == null) {
                  customFieldSet = createCustomSet();
                  form.add(customFieldSet);
               }
               customFieldSet.show();
            } else {
               if (customFieldSet != null) {
                  customFieldSet.hide();
               }
               rangeSet.hide();
            }
            form.layout();
            layout();
         }
         
      });
      
      if (edit) {
        nameField.setValue(sensorDTO.getName());
         if (sensorDTO.getType() != null) {
            typeList.setValue(new ComboBoxDataModel<SensorType>(sensorDTO.getType().toString(), sensorDTO.getType()));
            if (sensorDTO.getType() == SensorType.RANGE) {
               minField.setValue(sensorDTO.getMinValue());
               maxField.setValue(sensorDTO.getMaxValue());
//               minField.disable();
//               maxField.disable();
            } else if (sensorDTO.getType() == SensorType.CUSTOM) {
              // TODO EBR : see if we can avoid this transcoding of data and use the HashMap directly for display
              
              for (Map.Entry<String, String> e : sensorDTO.getStates().entrySet()) {
                BaseModelData bmd = new BaseModelData();
                bmd.set("name", e.getKey());
                bmd.set("value", e.getValue());
                grid.getStore().add(bmd);
              }
//               customFieldSet.disable();
            }
         }
//         commandSelectTree.disable();
         typeList.disable();         
      }
   }
   
   /**
    * Creates a view to contain the command tree.
    * 
    * @return the layout container
    */
   private LayoutContainer createCommandTreeView() {
      LayoutContainer deviceCommandTreeContainer = new LayoutContainer();
      deviceCommandTreeContainer.setSize(230, 120);
      deviceCommandTreeContainer.setLayout(new FitLayout());
      // overflow-auto style is for IE hack.
//      deviceCommandTreeContainer.addStyleName("overflow-auto");
      deviceCommandTreeContainer.setStyleAttribute("backgroundColor", "white");
      
      buildCommandSelectTree();
      deviceCommandTreeContainer.add(commandSelectTree);
      
      return deviceCommandTreeContainer;
   }
   
   /**
    * Builds the device's commands as a tree to select.
    */
   protected void buildCommandSelectTree() {      
      Long selectedCommandId = null;

      if (sensorDTO != null) {
         selectedCommandId = sensorDTO.getCommandId();
      }
      commandSelectTree = TreePanelBuilder.buildCommandTree(device.getOid(), selectedCommandId);
   }
   
   /**
    * Creates a filed set for the range sensor.
    * It includes min and max fields.
    * 
    * @return the field set
    */
   private FieldSet createRangeSet() {
      FieldSet rangeSet = new FieldSet();
      rangeSet.setWidth(300);
      FormLayout layout = new FormLayout();
      layout.setLabelWidth(50);
      rangeSet.setLayout(layout);
      rangeSet.setHeading("Range properties");
      
      minField.setFieldLabel("Min");
      minField.setAllowBlank(false);
      minField.setValue(0);
      
      maxField.setFieldLabel("Max");
      maxField.setAllowBlank(false);
      maxField.setValue(1);
      
      rangeSet.add(minField);
      rangeSet.add(maxField);
      
      return rangeSet;
   }
   
   /**
    * The custom set includes a grid and two buttons to manage the custom sensor states.
    * 
    * @return the field set
    */
   private FieldSet createCustomSet() {
      FieldSet customSet = new FieldSet();
      customSet.setHeading("Custom state items");
      customSet.setWidth(300);
      
      LayoutContainer customStatesContainer = new LayoutContainer();
      customStatesContainer.setBorders(false);
      customStatesContainer.setSize(280, 110);
      HBoxLayout tabbarContainerLayout = new HBoxLayout();
      tabbarContainerLayout.setHBoxLayoutAlign(HBoxLayoutAlign.TOP);
      customStatesContainer.setLayout(tabbarContainerLayout);
      
      ContentPanel stateItemsContainer = new ContentPanel();
      stateItemsContainer.setBorders(false);
      stateItemsContainer.setBodyBorder(false);
      stateItemsContainer.setHeaderVisible(false);
      stateItemsContainer.setWidth(200);
      stateItemsContainer.setHeight(100);
      stateItemsContainer.setLayout(new FitLayout());
      stateItemsContainer.setScrollMode(Scroll.AUTOY);
      
      List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
      ColumnConfig nameColumn = new ColumnConfig();
      nameColumn.setId("name");
      nameColumn.setHeader("Name");
      nameColumn.setWidth(100);
      
      ColumnConfig valueColumn = new ColumnConfig();
      valueColumn.setId("value");
      valueColumn.setHeader("Value");
      valueColumn.setWidth(100);

      TextField<String> text = new TextField<String>();
      text.setAllowBlank(false);
      nameColumn.setEditor(new CellEditor(text));  
      valueColumn.setEditor(new CellEditor(new TextField<String>()));
      configs.add(nameColumn);
      configs.add(valueColumn);
      
      grid = new EditorGrid<BaseModelData>(new ListStore<BaseModelData>(), new ColumnModel(configs));
      grid.setClicksToEdit(ClicksToEdit.TWO);
      grid.setAutoExpandColumn("name");
//      grid.setHideHeaders(true);
      stateItemsContainer.add(grid);
      
      grid.addListener(Events.RowClick, new Listener<GridEvent<BaseModelData>>() {
         @Override
         public void handleEvent(GridEvent<BaseModelData> be) {
            stateRowIndex = be.getRowIndex();
         }
      });
      grid.addListener(Events.AfterEdit, new Listener<GridEvent<BaseModelData>>() {
         @Override
         public void handleEvent(GridEvent<BaseModelData> be) {
            grid.getStore().commitChanges();
         }
      });
      
      LayoutContainer buttonsContainer = new LayoutContainer();
      buttonsContainer.setSize(80, 100);
      buttonsContainer.setBorders(false);
      buttonsContainer.setLayout(new RowLayout(Orientation.VERTICAL));
      
      Button addItemBtn = new Button("Add");
      addItemBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            grid.stopEditing();
            grid.getStore().add(new BaseModelData());
            grid.startEditing(grid.getStore().getCount() - 1, 0);
         }
      });
      
      Button deleteItemBtn = new Button("Delete");
      deleteItemBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            if (stateRowIndex != -1) {
               grid.getStore().remove(grid.getStore().getAt(stateRowIndex));
            }
         }
      });
      
      buttonsContainer.add(addItemBtn, new RowData(80, -1, new Margins(10)));
      buttonsContainer.add(deleteItemBtn, new RowData(80, -1, new Margins(10)));
      
      customStatesContainer.add(stateItemsContainer);
      customStatesContainer.add(buttonsContainer);
      
      AdapterField statesField = new AdapterField(customStatesContainer);
      statesField.setHideLabel(true);
      customSet.add(statesField);
      return customSet;
   }
   
   private void createButtons() {
      submitBtn = new Button("Submit");
      Button resetBtn = new Button("Reset");

      submitBtn.addSelectionListener(new FormSubmitListener(form, submitBtn));
      resetBtn.addSelectionListener(new FormResetListener(form));

      form.addButton(submitBtn);
      form.addButton(resetBtn);
   }
   
   private void addListenerToForm() {
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
            if (selectedCommand == null || !(selectedCommand.getBean() instanceof DeviceCommandDTO)) {
               MessageBox.alert("Warn", "A sensor must have a device command", null);
               submitBtn.enable();
               commandSelectTree.focus();
               return;
            }
            
            SensorType type = ((ComboBoxDataModel<SensorType>) typeList.getValue()).getData();            
            if (!edit) {
              sensorDTO.setType(type);
            }
            
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
            DeviceCommandDTO cmd = selectedCommand.getBean();
            sensorDTO.setCommand(new DTOReference(cmd.getOid()));

            if (!edit) {
              SensorBeanModelProxy.saveNewSensor(sensorDTO, device.getOid(), new AsyncSuccessCallback<Void>() {
                 public void onSuccess(Void result) {
                   hide();
                   eventBus.fireEvent(new DeviceUpdatedEvent(device));
                 }
              });
            } else {
               SensorBeanModelProxy.updateSensorWithDTO(sensorDTO, new AsyncSuccessCallback<Void>() {
                  public void onSuccess(Void result) {
                    hide();
                    eventBus.fireEvent(new DeviceUpdatedEvent(device)); // TODO : have specific event for sensor update
                  }
               });
            }
         }

      });
   }

}
