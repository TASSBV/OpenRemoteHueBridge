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
import org.openremote.modeler.client.event.DeviceUpdatedEventHandler;
import org.openremote.modeler.client.event.DevicesCreatedEvent;
import org.openremote.modeler.client.event.DevicesCreatedEventHandler;
import org.openremote.modeler.client.event.DoubleClickEvent;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.gxtextends.SelectionServiceExt;
import org.openremote.modeler.client.gxtextends.SourceSelectionChangeListenerExt;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.ConfirmDeleteListener;
import org.openremote.modeler.client.listener.EditDelBtnSelectionListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.DeviceBeanModelProxy;
import org.openremote.modeler.client.proxy.DeviceCommandBeanModelProxy;
import org.openremote.modeler.client.proxy.SensorBeanModelProxy;
import org.openremote.modeler.client.proxy.SliderBeanModelProxy;
import org.openremote.modeler.client.proxy.SwitchBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.CommandRefItem;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.selenium.DebugId;
import org.openremote.modeler.shared.dto.DTO;
import org.openremote.modeler.shared.dto.DTOHelper;
import org.openremote.modeler.shared.dto.DTOReference;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.modeler.shared.dto.DeviceDetailsDTO;
import org.openremote.modeler.shared.dto.SensorDTO;
import org.openremote.modeler.shared.dto.SliderDTO;
import org.openremote.modeler.shared.dto.SliderDetailsDTO;
import org.openremote.modeler.shared.dto.SwitchDTO;
import org.openremote.modeler.shared.dto.SwitchDetailsDTO;
import org.openremote.modeler.shared.dto.UICommandDTO;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSupport;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.store.TreeStoreEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The panel contains a toolbar and a treePanel to manage the device, deviceCommand, sensor, switch and slider.
 * It display in the west part of the page.
 */
public class DevicePanel extends ContentPanel {

  private EventBus eventBus;
  
   /** The tree. */
   private TreePanel<BeanModel> tree;
   
   /** The selection service. */
   private SelectionServiceExt<BeanModel> selectionService;
   
   /** The icon. */
   private Icons icon = GWT.create(Icons.class);
   
   private Map<BeanModel, ChangeListener> changeListenerMap = new HashMap<BeanModel,ChangeListener>();

   /**
    * Instantiates a new device panel.
    */
   public DevicePanel(EventBus eventBus) {
     this.eventBus = eventBus;
     bind();
      setHeading("Device");
      setIcon(icon.device());
      setLayout(new FitLayout());
      selectionService = new SelectionServiceExt<BeanModel>();
      createMenu();
      createTreeContainer();
      show();
   }
   
   private void bind() {
     eventBus.addHandler(DeviceUpdatedEvent.TYPE, new DeviceUpdatedEventHandler() {
      @Override
      public void onDeviceUpdated(DeviceUpdatedEvent event) {
        final TreeStore<BeanModel> deviceTreeStore = tree.getStore();
        final BeanModel bm = DTOHelper.getBeanModel(event.getUpdatedDevice());
        
        // EBR : adding a load listener so we can expand the device once it has reloaded it's children
        // removing it when the load is done. It works but I don't like this approach, feels like a hack to me.
        final LoadListener ll = new LoadListener() {
          @Override
          public void loaderLoad(LoadEvent le) {
            tree.getSelectionModel().select(bm, true);
            tree.setExpanded(bm, true);
            super.loaderLoad(le);
            deviceTreeStore.getLoader().removeLoadListener(this);
          }
        };
        if (deviceTreeStore.contains(bm)) {
          // Important : for this to work, the DTO & BeanModel used for update must be the same instance as the one in the store
          // Current code in DevicePanel ensures that this is the case
          deviceTreeStore.update(bm);
          deviceTreeStore.getLoader().addLoadListener(ll);
          deviceTreeStore.getLoader().loadChildren(bm);
        }
      }       
     });
     eventBus.addHandler(DevicesCreatedEvent.TYPE, new DevicesCreatedEventHandler() {
      @Override
      public void onDevicesCreated(DevicesCreatedEvent event) {
        List<BeanModel> bms = DTOHelper.createModels(event.getDevices());
        tree.getStore().add(bms, true);
      } 
     });
   }

   /**
    * Creates the tree container.
    */
   private void createTreeContainer() {
      tree = TreePanelBuilder.buildDeviceTree();
      selectionService.addListener(new SourceSelectionChangeListenerExt(tree.getSelectionModel()));
      selectionService.register(tree.getSelectionModel());
      LayoutContainer treeContainer = new LayoutContainer() {
         @Override
         protected void onRender(Element parent, int index) {
            super.onRender(parent, index);
            add(tree);
         }
         
      };
      treeContainer.ensureDebugId(DebugId.DEVICE_TREE_CONTAINER);
   // overflow-auto style is for IE hack.
      treeContainer.addStyleName("overflow-auto");
      treeContainer.setStyleAttribute("backgroundColor", "white");
      treeContainer.setBorders(false);
      add(treeContainer);
      tree.addListener(DoubleClickEvent.DOUBLECLICK, new Listener<DoubleClickEvent>() {
         public void handleEvent(DoubleClickEvent be) {
            editSelectedModel();
         }
         
      });
      
      final Menu contextMenu = new Menu();
      MenuItem createSwitchMenuItem = new MenuItem();
      createSwitchMenuItem.setText("Create Switch from selection");
      createSwitchMenuItem.addSelectionListener(new SelectionListener<MenuEvent>() {
        public void componentSelected(MenuEvent ce) {
          List<BeanModel> selectedData = tree.getSelectionModel().getSelectedItems();
          DeviceCommandDTO onCmd = null;
          DeviceCommandDTO offCmd = null;
          SensorDTO sensor = null;
          BeanModel deviceBeanModel = null;
          for (BeanModel beanModel : selectedData)
          {
            DTO dto = beanModel.getBean();
            if ((dto instanceof SensorDTO) && ((SensorDTO)dto).getType() == SensorType.SWITCH) {
              sensor = (SensorDTO)dto;
              deviceBeanModel = tree.getStore().getParent(beanModel);
            }
            if ((dto instanceof DeviceCommandDTO) && ((DeviceCommandDTO)dto).getDisplayName().indexOf("(ON)") != -1) {
              onCmd = (DeviceCommandDTO)dto;
            }
            if ((dto instanceof DeviceCommandDTO) && ((DeviceCommandDTO)dto).getDisplayName().indexOf("(OFF)") != -1) {
              offCmd = (DeviceCommandDTO)dto;
            }
          }
          if ((onCmd == null) || (offCmd == null) || (sensor == null) || (selectedData.size() != 3)) {
            MessageBox.alert("Wrong selection for Switch", "Make sure you have 1 SwitchSensor, 1 'On' and 1 'Off' command selected.<br><br>" +
                    "To automatically find 'On' and 'Off' commands these have to have the String '(ON)' or '(OFF)' within the name.", null);
            return;
          }
          
          final DeviceDTO deviceDTO = (DeviceDTO)deviceBeanModel.getBean();
          final SwitchDetailsDTO newSwitch = new SwitchDetailsDTO();
          newSwitch.setSensor(new DTOReference(sensor.getOid()));
          newSwitch.setOnCommand(new DTOReference(onCmd.getOid()));
          newSwitch.setOffCommand(new DTOReference(offCmd.getOid()));
          MessageBox.prompt("Switch name", "Please enter a name for the switch", new Listener<MessageBoxEvent>() {  
            public void handleEvent(MessageBoxEvent be) {
              if (be.getButtonClicked().getItemId().equals(Dialog.OK)) {
                newSwitch.setName(be.getValue());
                
                SwitchBeanModelProxy.saveNewSwitch(newSwitch, deviceDTO.getOid(), new AsyncSuccessCallback<Void>() {
                  @Override
                  public void onSuccess(Void result) {
                    eventBus.fireEvent(new DeviceUpdatedEvent(deviceDTO));
                  };
                });
              }
            }  
          });
        }
      });
      contextMenu.add(createSwitchMenuItem);
      
      MenuItem createSliderMenuItem = new MenuItem();
      createSliderMenuItem.setText("Create Slider from selection");
      createSliderMenuItem.addSelectionListener(new SelectionListener<MenuEvent>() {
        public void componentSelected(MenuEvent ce) {
          List<BeanModel> selectedData = tree.getSelectionModel().getSelectedItems();
          DeviceCommandDTO setValuCmd = null;
          SensorDTO sensor = null;
          BeanModel deviceBeanModel = null;
          for (BeanModel beanModel : selectedData)
          {
            DTO dto = beanModel.getBean();
            if ((dto instanceof SensorDTO) && ((((SensorDTO)dto).getType() == SensorType.LEVEL) || (((SensorDTO)dto).getType() == SensorType.RANGE))) {
              sensor = (SensorDTO)dto;
              deviceBeanModel = tree.getStore().getParent(beanModel);
            }
            if (dto instanceof DeviceCommandDTO) {
              setValuCmd = (DeviceCommandDTO)dto;
            }
          }
          if ((setValuCmd == null) || (sensor == null) || (selectedData.size() != 2)) {
            MessageBox.alert("Wrong selection for Slider", "Make sure you have 1 Range-/Scale-Sensor and 1 command selected.", null);
            return;
          }
          final DeviceDTO deviceDTO = (DeviceDTO)deviceBeanModel.getBean();
          final SliderDetailsDTO newSlider = new SliderDetailsDTO();
          newSlider.setCommand(new DTOReference(setValuCmd.getOid()));
          newSlider.setSensor(new DTOReference(sensor.getOid()));
          MessageBox.prompt("Slider name", "Please enter a name for the slider", new Listener<MessageBoxEvent>() {  
            public void handleEvent(MessageBoxEvent be) {
              if (be.getButtonClicked().getItemId().equals(Dialog.OK)) {
                newSlider.setName(be.getValue());
                SliderBeanModelProxy.saveNewSlider(newSlider, deviceDTO.getOid(), new AsyncSuccessCallback<Void>() {
                  @Override
                  public void onSuccess(Void result) {
                    eventBus.fireEvent(new DeviceUpdatedEvent(deviceDTO));
                  };
                });
              }
            }  
          });
        }
      });
      contextMenu.add(createSliderMenuItem);
      
      tree.setContextMenu(contextMenu);
   }
   
   /**
    * Creates the toolbar which contains some menus.
    */
   private void createMenu() {      
      Button newButton = new Button("New");
      newButton.setToolTip("Create Device or DeviceCommand");
      newButton.ensureDebugId(DebugId.DEVICE_NEW_BTN);
      newButton.setIcon(icon.add());
      
      Menu newMenu = new Menu();
      newMenu.add(createNewDeviceMenuItem());
      newMenu.add(createNewRussoundDeviceMenuItem());
      final MenuItem newCommandMemuItem = createNewCommandMenu();

      final MenuItem importCommandMemuItem = createImportMenuItem();

      final MenuItem newSensorMenuItem = createNewSensorMenu();
      final MenuItem newSliderMenuItem = createNewSliderMenu();
      final MenuItem newSwitchMenuItem = createNewSwitchMenu();
      final MenuItem importKnxCommandMemuItem = createImportKnxMenuItem();
      final MenuItem newLutronImportMenuItem = createNewLutronImportMenu();

      final MenuItem importIRCommandFileMenuItem = createimportIRCommandFileImportMenu();
      
      newMenu.add(newCommandMemuItem);
      newMenu.add(importCommandMemuItem);
      newMenu.add(newSensorMenuItem);
      newMenu.add(newSliderMenuItem);
      newMenu.add(newSwitchMenuItem);
      newMenu.add(importKnxCommandMemuItem);
      newMenu.add(newLutronImportMenuItem);

     //  TODO:
     //     Enable once the IR Import service has been deployed.   [JPL]
     //
     // newMenu.add(importIRCommandFileMenuItem);
      
      // enable or disable sub menus by the selected tree model.
      newMenu.addListener(Events.BeforeShow, new Listener<MenuEvent>() {
         @Override
         public void handleEvent(MenuEvent be) {
            boolean enabled = false;
            BeanModel selectedBeanModel = tree.getSelectionModel().getSelectedItem();
            if (selectedBeanModel != null) {
               enabled = true;
            }
            newCommandMemuItem.setEnabled(enabled);
            importCommandMemuItem.setEnabled(enabled);
            newSensorMenuItem.setEnabled(enabled);
            newSliderMenuItem.setEnabled(enabled);
            newSwitchMenuItem.setEnabled(enabled);
            importKnxCommandMemuItem.setEnabled(enabled);
            newLutronImportMenuItem.setEnabled(enabled);
            importIRCommandFileMenuItem.setEnabled(enabled);
         }
         
      });
      newButton.setMenu(newMenu);
      
      ToolBar toolBar = new ToolBar();
      toolBar.add(newButton);
      
      List<Button> editDelBtns = new ArrayList<Button>(); 
      Button editBtn = createEditButton();
      editBtn.setEnabled(false);
      Button deleteBtn = createDeleteButton();
      deleteBtn.setEnabled(false);
      editDelBtns.add(editBtn);
      editDelBtns.add(deleteBtn);
      
      toolBar.add(editBtn);
      toolBar.add(deleteBtn);
      selectionService.addListener(new EditDelBtnSelectionListener(editDelBtns));
      setTopComponent(toolBar);
   }

   /**
    * Creates the new device menu.
    * 
    * @return the menu item
    */
   private MenuItem createNewDeviceMenuItem() {
      MenuItem newDeviceItem = new MenuItem("New Device");
      newDeviceItem.ensureDebugId(DebugId.NEW_DEVICE_MENU_ITEM);
      newDeviceItem.setIcon(icon.device());
      newDeviceItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            final DeviceWizardWindow deviceWindow = new DeviceWizardWindow(DTOHelper.getBeanModel(new DeviceDetailsDTO()));

            deviceWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  deviceWindow.hide();
                  DeviceDTO deviceDTO = (DeviceDTO) be.getData();
                  eventBus.fireEvent(new DevicesCreatedEvent(deviceDTO));
                  Info.display("Info", "Add device " + deviceDTO.getDisplayName() + " success.");                  
               }
            });
         }
      });
      return newDeviceItem;
   }
   
   private MenuItem createNewRussoundDeviceMenuItem() {
     MenuItem newDeviceItem = new MenuItem("New Russound Device");
     //newDeviceItem.ensureDebugId(DebugId.NEW_DEVICE_MENU_ITEM);
     newDeviceItem.setIcon(icon.device());
     newDeviceItem.addSelectionListener(new SelectionListener<MenuEvent>() {
        public void componentSelected(MenuEvent ce) {
           final RussoundWizardWindow deviceWindow = new RussoundWizardWindow();
           deviceWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
              @Override
              public void afterSubmit(SubmitEvent be) {
                 deviceWindow.hide();                 
                 ArrayList<DeviceDTO> devices = be.getData();
                 eventBus.fireEvent(new DevicesCreatedEvent(devices));
                 Info.display("Info", "Added " + devices.size() + " Russound zone devices successfully.");
              }
           });
        }
     });
     return newDeviceItem;
  }
   
   /**
    * Creates the new command menu.
    * 
    * @return the menu item
    */
   private MenuItem createNewCommandMenu() {
      MenuItem newCommandItem = new MenuItem("New Command");
      newCommandItem.ensureDebugId(DebugId.NEW_COMMAND_ITEM);
      newCommandItem.setIcon(icon.deviceCmd());
      newCommandItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            createDeviceCommand();
         }

      });
      return newCommandItem;
   }
   
   private MenuItem createNewSensorMenu() {
      MenuItem newCommandItem = new MenuItem("New Sensor");
      newCommandItem.setIcon(icon.sensorIcon());
      newCommandItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            createSensor();
         }

      });
      return newCommandItem;
   }
   
   private MenuItem createNewSliderMenu() {
      MenuItem newCommandItem = new MenuItem("New Slider");
      newCommandItem.setIcon(icon.sliderIcon());
      newCommandItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            createSlider();
         }

      });
      return newCommandItem;
   }
   
   private MenuItem createNewSwitchMenu() {
      MenuItem newCommandItem = new MenuItem("New Switch");
      newCommandItem.setIcon(icon.switchIcon());
      newCommandItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            createSwitch();
         }

      });
      return newCommandItem;
   }
   
   /**
    * Creates the import knx menu item.
    * 
    * @return the menu item
    */
   private MenuItem createNewLutronImportMenu() {
      MenuItem importCommandItem = new MenuItem("Import Lutron Commands from XML");
      importCommandItem.setIcon(icon.importFromDB());
      importCommandItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            importLutronCommand();
         }

      });
      return importCommandItem;
   }

   /**
    * Creates the device command.
    */
   private void createDeviceCommand() {
      final BeanModel deviceModel = getDeviceModel();
      if (deviceModel != null && deviceModel.getBean() instanceof DeviceDTO) {
         DeviceCommandWindow deviceCommandWindow = new DeviceCommandWindow(((DeviceDTO)deviceModel.getBean()), eventBus);
         
         // TODO deviceCommandWindow.show()
         /*
         deviceCommandWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               BeanModel deviceCommandModel = be.getData();
               tree.getStore().add(deviceModel, deviceCommandModel, false);
               tree.setExpanded(deviceModel, true);
               Info.display("Info", "Create command " + deviceCommandModel.get("name") + " success");
            }
         });
         */
      }
   }
   
   private BeanModel getDeviceModel() {
      BeanModel selectedModel = tree.getSelectionModel().getSelectedItem();
      if (selectedModel != null) {
         Object obj = selectedModel.getBean();
         if (obj instanceof DeviceDTO) {
            return selectedModel;
         } else if (obj instanceof DeviceCommandDTO || obj instanceof SensorDTO || obj instanceof SwitchDTO || obj instanceof SliderDTO) {
            selectedModel = tree.getStore().getParent(selectedModel);
         } else if (obj instanceof UICommandDTO) {
            selectedModel = tree.getStore().getParent(tree.getStore().getParent(selectedModel));
         }
      } else {
         MessageBox.info("Warn", "Please select a device", null);
      }
      return selectedModel;
   }
   private void createSensor() {
      final BeanModel deviceModel = getDeviceModel();
      if (deviceModel != null && deviceModel.getBean() instanceof DeviceDTO) {
         final SensorWindow sensorWindow = new SensorWindow(((DeviceDTO)deviceModel.getBean()), eventBus);
         sensorWindow.show();
         /*
         sensorWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               Sensor sensor = be.getData();
               tree.getStore().add(deviceModel, sensor.getBeanModel(), false);
               tree.setExpanded(deviceModel, true);
               sensorWindow.hide();
            }
         });
         */
      }
   }
   
   private void createSlider() {
      final BeanModel deviceModel = getDeviceModel();
      if (deviceModel != null && deviceModel.getBean() instanceof DeviceDTO) {
         final SliderWindow sliderWindow = new SliderWindow(((DeviceDTO)deviceModel.getBean()), eventBus);
         sliderWindow.show();
         /*
         sliderWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               BeanModel sliderBeanModel = be.getData();
               tree.getStore().add(deviceModel, sliderBeanModel, false);
               tree.setExpanded(deviceModel, true);
               sliderWindow.hide();
            }
         });
         */
      }
   }
   
   private void createSwitch() {
      final BeanModel deviceModel = getDeviceModel();
      if (deviceModel != null && deviceModel.getBean() instanceof DeviceDTO) {
        
         final SwitchWindow switchWindow = new SwitchWindow(((DeviceDTO)deviceModel.getBean()), eventBus);
         switchWindow.show();
         
         /*
         switchWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               BeanModel switchBeanModel = be.getData();
               tree.getStore().add(deviceModel, switchBeanModel, false);
               tree.setExpanded(deviceModel, true);
               switchWindow.hide();
            }
         });
         */
      }
   }
   /**
    * Creates the edit button.
    * 
    * @return the button
    */
   private Button createEditButton() {
      Button editBtn = new Button("Edit");
      editBtn.setToolTip("Edit Device or DeviceCommand");
      editBtn.ensureDebugId(DebugId.DEVICE_EDIT_BTN);
      editBtn.setIcon(icon.edit());
      editBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            editSelectedModel(); 
            
         }
      });
      return editBtn;
   }
   
   /**
    * Edits the device.
    * 
    * @param selectedModel the selected model
    */
   private void editDevice(BeanModel selectedModel) {
      final DeviceWindow editDeviceWindow = new DeviceWindow((DeviceDTO)selectedModel.getBean(), eventBus);
      editDeviceWindow.show();
   }
   
   /**
    * Edits the command.
    * 
    * @param selectedModel the selected model
    */
   private void editCommand(BeanModel selectedModel, final BeanModel parentModel) {
      DeviceCommandDTO cmd = selectedModel.getBean();
      if (cmd.getProtocolType().equalsIgnoreCase(Protocol.INFRARED_TYPE)) {
         MessageBox.alert("Warn", "Infrared command can not be edited", null);
         return;
      }

      final DeviceCommandWindow deviceCommandWindow = new DeviceCommandWindow(cmd, ((DeviceDTO)parentModel.getBean()), eventBus);
      // deviceCommandWindow.show(); // TODO EBR : this should be the correct way to do it, but having the show here messes up the size of the displayed window
   }
   
   private void editSensor(final BeanModel selectedModel, final BeanModel parentModel) {
      final SensorWindow sensorWindow = new SensorWindow(selectedModel, ((DeviceDTO)parentModel.getBean()), eventBus);
      sensorWindow.show();
      
  /*    
      sensorWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
         @Override
         public void afterSubmit(SubmitEvent be) {
            Sensor sensor = be.getData();
            tree.getStore().removeAll(selectedModel);
            tree.getStore().add(selectedModel, sensor.getSensorCommandRef().getBeanModel(), false);
            Info.display("Info", "Edit sensor " + sensor.getBeanModel().get("name") + " success.");
            sensorWindow.hide();
         }
      });
      */
   }
   
   private void editSlider(final BeanModel selectedModel, final BeanModel parentModel) {
      final SliderWindow sliderWindow = new SliderWindow(selectedModel, ((DeviceDTO)parentModel.getBean()), eventBus);
      sliderWindow.show();
      
      /*
      sliderWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
         @Override
         public void afterSubmit(SubmitEvent be) {
            BeanModel sliderBeanModel = be.getData();
            Slider slider = sliderBeanModel.getBean();
            tree.getStore().removeAll(selectedModel);
            if (slider.getSetValueCmd() != null) {
               tree.getStore().add(selectedModel, slider.getSetValueCmd().getBeanModel(), false);
            }
            tree.getStore().update(selectedModel);
            Info.display("Info", "Edit slider " + sliderBeanModel.get("name") + " success.");
            sliderWindow.hide();
         }
      });
      
      */
   }
   
   private void editSwitch(final BeanModel selectedModel, final BeanModel parentModel) {     
      final SwitchWindow switchWindow = new SwitchWindow(selectedModel, ((DeviceDTO)parentModel.getBean()), eventBus);
      switchWindow.show();
      /*
      switchWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
         @Override
         public void afterSubmit(SubmitEvent be) {
            BeanModel switchBeanModel = be.getData();
            Switch swh = switchBeanModel.getBean();
            tree.getStore().removeAll(selectedModel);
            tree.getStore().add(selectedModel, swh.getSwitchCommandOnRef().getBeanModel(), false);
            tree.getStore().add(selectedModel, swh.getSwitchCommandOffRef().getBeanModel(), false);
            Info.display("Info", "Edit switch " + switchBeanModel.get("name") + " success.");
            switchWindow.hide();
         }
      });
      */
   }
   /**
    * Creates the delete button.
    * 
    * @return the button
    */
   private Button createDeleteButton() {
      Button deleteBtn = new Button("Delete");
      deleteBtn.setToolTip("Delete Device or DeviceCommand");
      deleteBtn.ensureDebugId(DebugId.DELETE_DEVICE_BUTTON);
      deleteBtn.setIcon(icon.delete());
      deleteBtn.addSelectionListener(new ConfirmDeleteListener<ButtonEvent>() {
         public void onDelete(ButtonEvent ce) {
            List<BeanModel> selectedModels = tree.getSelectionModel().getSelectedItems();
            for (BeanModel selectedModel : selectedModels) {
               if (selectedModel != null && selectedModel.getBean() instanceof DeviceDTO) {
                  deleteDevice(selectedModel);
               } else if (selectedModel != null && selectedModel.getBean() instanceof DeviceCommandDTO) {
                  deleteCommand(selectedModel);
               } else if (selectedModel != null && selectedModel.getBean() instanceof SensorDTO) {
                  deleteSensor(selectedModel);
               } else if (selectedModel!=null && selectedModel.getBean() instanceof SliderDTO) {
                  deleteSlider(selectedModel);
               } else if (selectedModel !=null && selectedModel.getBean() instanceof SwitchDTO) {
                  deleteSwitch(selectedModel);
               }
            }
         }
      });
      return deleteBtn;
   }
   
   /**
    * Delete device.
    * 
    * @param deviceModel the device model
    */
   private void deleteDevice(final BeanModel deviceModel) {
      mask("Delete device...");
      DeviceBeanModelProxy.deleteDevice(deviceModel, new AsyncSuccessCallback<Void>() {
         @Override
         public void onSuccess(Void result) {
            unmask();
            tree.getStore().remove(deviceModel);
            Info.display("Info", "Delete success.");
         }
         
      });
   }
   
   /**
    * Delete command.
    * 
    * @param deviceCommnadModel the device command model
    */
   private void deleteCommand(final BeanModel deviceCommnadModel) {
      DeviceCommandBeanModelProxy.deleteDeviceCommand(deviceCommnadModel, new AsyncSuccessCallback<Boolean>() {
         @Override
         public void onSuccess(Boolean result) {
            if (result) {
               tree.getStore().remove(deviceCommnadModel);
               Info.display("Info", "Delete success.");
            } else {
               MessageBox.alert("Warn", "The command can't be delete, because it was referenced by other sensor, switch or slider.", null);
            }
         }
      });
   }
   
   private void deleteSensor(final BeanModel sensorBeanModel) {
      SensorBeanModelProxy.deleteSensor(sensorBeanModel, new AsyncCallback<Boolean>() {
         @Override
         public void onSuccess(Boolean result) {
            if (result) {
               tree.getStore().remove(sensorBeanModel);
               Info.display("Info", "Delete success.");
            } else {
               MessageBox.alert("Warn", "The sensor can't be delete, because it was referenced by a switch or slider.", null);
            }
         }
         
         @Override
         public void onFailure(Throwable caught) {
           MessageBox.alert("Error", "The Sensor you are deleting is being used", null);
         }
      });
   }
   
   private void deleteSlider(final BeanModel sensorBeanModel) {
      SliderBeanModelProxy.delete(sensorBeanModel, new AsyncSuccessCallback<Void>() {
         @Override
         public void onSuccess(Void result) {
            tree.getStore().remove(sensorBeanModel);
            return;
         }
      });
   }
   
   private void deleteSwitch(final BeanModel sensorBeanModel) {
      SwitchBeanModelProxy.delete(sensorBeanModel, new AsyncSuccessCallback<Void>() {
         @Override
         public void onSuccess(Void result) {
            tree.getStore().remove(sensorBeanModel);
            return;
         }

         @Override
         public void onFailure(Throwable caught) {
           MessageBox.alert("Error", "The Switch you are deleting is being used", null);
           super.checkTimeout(caught);
         }
      });
   }
   /**
    * Creates the import menu item.
    * 
    * @return the menu item
    */
   private MenuItem createImportMenuItem() {
      MenuItem importCommandItem = new MenuItem("Import IR Commands");
      importCommandItem.setIcon(icon.importFromDB());
      importCommandItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            importIRCommand();
         }

      });
      return importCommandItem;
   }
   
   
   /**
    * Creates the import knx menu item.
    * 
    * @return the menu item
    */
   private MenuItem createImportKnxMenuItem() {
      MenuItem importCommandItem = new MenuItem("Import ETS data");
      importCommandItem.setIcon(icon.importFromDB());
      importCommandItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            importKNXCommand();
         }

      });
      return importCommandItem;
   }

   /**
    * Creates the import IR File menu item.
    * 
    * @return the menu item
    */
   private MenuItem createimportIRCommandFileImportMenu() {
		MenuItem importIRCommandFileItem = new MenuItem("import IR Commands from file");
		importIRCommandFileItem.setIcon(icon.importFromDB());
		importIRCommandFileItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				importIRCommandFile();
			}
		});
		return importIRCommandFileItem;
	}
   
   private void importIRCommandFile() {
		 final BeanModel deviceModel = getDeviceModel();
		 if (deviceModel != null && deviceModel.getBean() instanceof DeviceDTO) {
			 final IRFileImportWindow selectIRFileWindow = new IRFileImportWindow(deviceModel);
			 selectIRFileWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
				
				@Override
				public void afterSubmit(SubmitEvent be) {
				  /*
				  List<BeanModel> deviceCommandModels = be.getData();
				  for (BeanModel deviceCommandModel : deviceCommandModels) {
				    tree.getStore().add(deviceModel, deviceCommandModel, false);
				  }
				  tree.setExpanded(deviceModel, true);
				  */
          eventBus.fireEvent(new DeviceUpdatedEvent((DeviceDTO)deviceModel.getBean())); // TODO review
				  selectIRFileWindow.hide();

				}
			});
			 
		 }
	  }
   /**
    * Import ir command.
    */
   private void importIRCommand() {
      final BeanModel deviceModel = getDeviceModel();
      if (deviceModel != null && deviceModel.getBean() instanceof DeviceDTO) {
         final IRCommandImportWindow selectIRWindow = new IRCommandImportWindow(deviceModel);
         selectIRWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
              /*
               List<BeanModel> deviceCommandModels = be.getData();
               for (BeanModel deviceCommandModel : deviceCommandModels) {
                  tree.getStore().add(deviceModel, deviceCommandModel, false);
               }
               tree.setExpanded(deviceModel, true);
               */
              eventBus.fireEvent(new DeviceUpdatedEvent((DeviceDTO)deviceModel.getBean())); // TODO review
               selectIRWindow.hide();
            }
         });
      }
   }


   /**
    * Import KNX command.
    */
   private void importKNXCommand() {
       final BeanModel deviceModel = getDeviceModel();
       if (deviceModel != null && deviceModel.getBean() instanceof DeviceDTO) {
          final KNXImportWindow knxImportWindow = new KNXImportWindow(deviceModel);
          knxImportWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
             @Override
             public void afterSubmit(SubmitEvent be) {
               /*
                List<BeanModel> deviceCommandModels = be.getData();
                for (BeanModel deviceCommandModel : deviceCommandModels) {
                   tree.getStore().add(deviceModel, deviceCommandModel, false);
                }
                tree.setExpanded(deviceModel, true);
                */
                eventBus.fireEvent(new DeviceUpdatedEvent((DeviceDTO)deviceModel.getBean())); // TODO review
                knxImportWindow.hide();
             }
          });
       }
   }
   
   /**
    * Import Lutron command.
    */
   private void importLutronCommand() {    
     final BeanModel deviceModel = getDeviceModel();
     if (deviceModel != null && deviceModel.getBean() instanceof DeviceDTO) {       
       LutronImportWizard importWizard = new LutronImportWizard((DeviceDTO) deviceModel.getBean(), eventBus);
       importWizard.show();
       importWizard.center();
     }
   }
   
   public TreePanel<BeanModel> getTree() {
      return tree;
   }

   public void setTree(TreePanel<BeanModel> tree) {
      this.tree = tree;
   }

   private void editSelectedModel() {
      BeanModel selectedModel = tree.getSelectionModel().getSelectedItem();
      if (selectedModel != null && selectedModel.getBean() instanceof DeviceDTO) {
         editDevice(selectedModel);
      } else if (selectedModel != null && selectedModel.getBean() instanceof DeviceCommandDTO) {
        BeanModel parent = tree.getStore().getParent(selectedModel);
        if (!(parent.getBean() instanceof DeviceDTO)) {
          // We're in a sensor, slider or switch, need to go one level higher
          parent = tree.getStore().getParent(parent);
        }
        editCommand(selectedModel, parent);
      } else if (selectedModel != null && selectedModel.getBean() instanceof SensorDTO) {
        BeanModel parent = tree.getStore().getParent(selectedModel);
        if (!(parent.getBean() instanceof DeviceDTO)) {
          // We're in a slider or switch, need to go one level higher
          parent = tree.getStore().getParent(parent);
        }
        editSensor(selectedModel, parent);
      } else if (selectedModel != null && selectedModel.getBean() instanceof SliderDTO) {
         editSlider(selectedModel, tree.getStore().getParent(selectedModel));
      } else if (selectedModel != null && selectedModel.getBean() instanceof SwitchDTO) {
         editSwitch(selectedModel, tree.getStore().getParent(selectedModel));
      }
   }
   
}

