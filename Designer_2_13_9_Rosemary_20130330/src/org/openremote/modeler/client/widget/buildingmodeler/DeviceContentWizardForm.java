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
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.DeviceWizardListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.DeviceBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.CommonForm;
import org.openremote.modeler.shared.dto.DTOHelper;
import org.openremote.modeler.shared.dto.DeviceCommandDetailsDTO;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.modeler.shared.dto.DeviceDetailsDTO;
import org.openremote.modeler.shared.dto.SensorDetailsDTO;
import org.openremote.modeler.shared.dto.SliderDetailsDTO;
import org.openremote.modeler.shared.dto.SwitchDetailsDTO;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * The wizard form supports to create deviceCommands, sensors, switches and sliders for a device
 */
public class DeviceContentWizardForm extends CommonForm {

  /** The Constant icon. */
  private static final Icons ICON = GWT.create(Icons.class);

   private DeviceDetailsDTO device = null;
   
   private ArrayList<DeviceCommandDetailsDTO> commands = new ArrayList<DeviceCommandDetailsDTO>();
   private ArrayList<SensorDetailsDTO> sensors = new ArrayList<SensorDetailsDTO>();
   private ArrayList<SwitchDetailsDTO> switches = new ArrayList<SwitchDetailsDTO>();
   private ArrayList<SliderDetailsDTO> sliders = new ArrayList<SliderDetailsDTO>();
   
   private TreePanel<BeanModel> deviceContentTree;
   
   /** The wrapper is the window which wrap the form. */
   private Component wrapper;
   
   /**
    * Instantiates a new device content wizard form by a device model.
    * 
    * @param wrapper the wrapper
    * @param deviceBeanModel the device bean model
    */
   public DeviceContentWizardForm(Component wrapper, BeanModel deviceBeanModel) {
      super();
      this.wrapper = wrapper;
      this.device = deviceBeanModel.getBean();
      setHeight(240);
      init();
   }
   
   /**
    * Inits the form's style and content.
    */
   private void init() {
      FormLayout formLayout = new FormLayout();
      formLayout.setLabelAlign(LabelAlign.TOP);
      formLayout.setLabelWidth(200);
      formLayout.setDefaultWidth(340);
      setLayout(formLayout);
      
      AdapterField contentField = new AdapterField(createContentContainer());
      contentField.setFieldLabel("Commands,sensors,switches and sliders");
      add(contentField);
      onSubmit();
   }

   /**
    * Creates the content container.
    * It has a treePanel container to store the device content(deviceCommand, sensor, switch and slider),
    * five buttons to add or delete the device content.
    * 
    * @return the layout container
    */
   private LayoutContainer createContentContainer() {
      LayoutContainer contentContainer = new LayoutContainer();
      contentContainer.setBorders(false);
      contentContainer.setSize(340, 170);
      HBoxLayout tabbarContainerLayout = new HBoxLayout();
      tabbarContainerLayout.setHBoxLayoutAlign(HBoxLayoutAlign.TOP);
      contentContainer.setLayout(tabbarContainerLayout);
      
      LayoutContainer contentItemsContainer = new LayoutContainer();
      contentItemsContainer.setBorders(true);
      contentItemsContainer.setWidth(230);
      contentItemsContainer.setHeight(160);
      contentItemsContainer.setLayout(new FitLayout());
      contentItemsContainer.setStyleAttribute("backgroundColor", "white");
      // overflow-auto style is for IE hack.
      contentItemsContainer.addStyleName("overflow-auto");
      
      TreeStore<BeanModel> deviceContentTreeStore = new TreeStore<BeanModel>();
      deviceContentTree = buildDeviceContentTree(deviceContentTreeStore);
      contentItemsContainer.add(deviceContentTree);
      
      LayoutContainer buttonsContainer = new LayoutContainer();
      buttonsContainer.setSize(110, 160);
      buttonsContainer.setBorders(false);
      buttonsContainer.setLayout(new RowLayout(Orientation.VERTICAL));
      
      Button addCommandBtn = new Button("Add command");
      addCommandBtn.addSelectionListener(new AddCommandListener());
      
      Button addSensorBtn = new Button("Add sensor");
      addSensorBtn.addSelectionListener(new AddSensorListener());
      
      Button addSwitchBtn = new Button("Add switch");
      addSwitchBtn.addSelectionListener(new AddSwitchListener());
      
      Button addSliderBtn = new Button("Add slider");
      addSliderBtn.addSelectionListener(new AddSliderListener());
      
      Button deleteBtn = new Button("Delete");
      deleteBtn.addSelectionListener(new DeleteContentListener());
      
      buttonsContainer.add(addCommandBtn, new RowData(110, -1, new Margins(5)));
      buttonsContainer.add(addSensorBtn, new RowData(110, -1, new Margins(5)));
      buttonsContainer.add(addSwitchBtn, new RowData(110, -1, new Margins(5)));
      buttonsContainer.add(addSliderBtn, new RowData(110, -1, new Margins(5)));
      buttonsContainer.add(deleteBtn, new RowData(110, -1, new Margins(5)));
      
      contentContainer.add(contentItemsContainer);
      contentContainer.add(buttonsContainer);
      return contentContainer;
   }
   
   private TreePanel<BeanModel> buildDeviceContentTree(TreeStore<BeanModel> store) {
     TreePanel<BeanModel> deviceContentTree = new TreePanel<BeanModel>(store);

     deviceContentTree.setStateful(true);
     deviceContentTree.setBorders(false);
     deviceContentTree.setHeight("100%");
     deviceContentTree.setDisplayProperty("name");
     deviceContentTree.setStyleAttribute("overflow", "auto");

     deviceContentTree.setIconProvider(new ModelIconProvider<BeanModel>() {
        public AbstractImagePrototype getIcon(BeanModel thisModel) {
           if (thisModel.getBean() instanceof DeviceCommandDetailsDTO) {
              return ICON.deviceCmd();
           } else if (thisModel.getBean() instanceof SensorDetailsDTO) {
              return ICON.sensorIcon();
           } else if (thisModel.getBean() instanceof SwitchDetailsDTO) {
              return ICON.switchIcon();
           } else if (thisModel.getBean() instanceof SliderDetailsDTO) {
              return ICON.sliderIcon();
           } else {
              return ICON.folder();
           }
        }
     });

     return deviceContentTree;
  }

   @Override
   public boolean isNoButton() {
      return true;
   }
   
   @Override
   public void show() {
      super.show();
      ((Window) wrapper).setSize(390, 240);
   }
   
   /**
    * Add submit listener to save the device with its content.
    */
   private void onSubmit() {
      addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
           
            DeviceBeanModelProxy.saveNewDeviceWithChildren(device, commands, sensors, switches, sliders, new AsyncSuccessCallback<DeviceDTO>() {
               @Override
               public void onSuccess(DeviceDTO result) {
                 wrapper.fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(result));
               }
            });
         }
         
      });
   }

   /**
    * Add a listener to add deviceCommand to the device.
    */
   private final class AddCommandListener extends SelectionListener<ButtonEvent> {
      @Override
      public void componentSelected(ButtonEvent ce) {
         DeviceCommandWizardWindow deviceCommandWizardWindow = new DeviceCommandWizardWindow();
         deviceCommandWizardWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               DeviceCommandDetailsDTO deviceCommand = be.getData();
               commands.add(deviceCommand);
               deviceContentTree.getStore().add(DTOHelper.getBeanModel(deviceCommand), false);
               Info.display("Info", "Create command " + deviceCommand.getName() + " success");
            }
         });
      }
   }
   
   /**
    * Add a listener to add sensor to the device.
    */
   private final class AddSensorListener extends SelectionListener<ButtonEvent> {
      @Override
      public void componentSelected(ButtonEvent ce) {
         final SensorWizardWindow sensorWizardWindow = new SensorWizardWindow(commands);
         sensorWizardWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               SensorDetailsDTO sensor = be.getData();
               sensors.add(sensor);
               deviceContentTree.getStore().add(DTOHelper.getBeanModel(sensor), false);
               sensorWizardWindow.hide();
            }
         });
         sensorWizardWindow.addListener(DeviceWizardEvent.ADD_CONTENT, new DeviceWizardListener() {
            @Override
            public void afterAdd(DeviceWizardEvent be) {
               BeanModel deviceCommandModel = be.getData();
               commands.add((DeviceCommandDetailsDTO)deviceCommandModel.getBean());
               deviceContentTree.getStore().add(deviceCommandModel, false);
            }
         });
         sensorWizardWindow.show();
      }
   }
   
   /**
    * Add a listener to add switch to the device.
    */
   private final class AddSwitchListener extends SelectionListener<ButtonEvent> {
      @Override
      public void componentSelected(ButtonEvent ce) {
         final SwitchWizardWindow switchWizardWindow = new SwitchWizardWindow(commands, sensors);
         switchWizardWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               SwitchDetailsDTO switchToggle = be.getData();
               switches.add(switchToggle);
               deviceContentTree.getStore().add(DTOHelper.getBeanModel(switchToggle), false);
               switchWizardWindow.hide();
            }
         });
         switchWizardWindow.addListener(DeviceWizardEvent.ADD_CONTENT, new DeviceWizardListener() {
            @Override
            public void afterAdd(DeviceWizardEvent be) {
               BeanModel beanModel = be.getData();
               if (beanModel.getBean() instanceof DeviceCommandDetailsDTO) {
                 // Happens when user does new sensor, then new command from the new sensor window
                 commands.add((DeviceCommandDetailsDTO)beanModel.getBean());
                 deviceContentTree.getStore().add(beanModel, false);
               } else if (beanModel.getBean() instanceof SensorDetailsDTO) {
                 sensors.add((SensorDetailsDTO)beanModel.getBean());
                 deviceContentTree.getStore().add(beanModel, false);
               }
            }
         });
         switchWizardWindow.show();
      }
   }
   
   /**
    * Add a listener to add slider to the device.
    */
   private final class AddSliderListener extends SelectionListener<ButtonEvent> {
      @Override
      public void componentSelected(ButtonEvent ce) {
         final SliderWizardWindow sliderWizardWindow = new SliderWizardWindow(commands, sensors);
         sliderWizardWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               SliderDetailsDTO slider = be.getData();
               sliders.add(slider);
               deviceContentTree.getStore().add(DTOHelper.getBeanModel(slider), false);
               sliderWizardWindow.hide();
            }
         });
         sliderWizardWindow.addListener(DeviceWizardEvent.ADD_CONTENT, new DeviceWizardListener() {
            @Override
            public void afterAdd(DeviceWizardEvent be) {
              BeanModel beanModel = be.getData();
              if (beanModel.getBean() instanceof DeviceCommandDetailsDTO) {
                // Happens when user does new sensor, then new command from the new sensor window
                commands.add((DeviceCommandDetailsDTO)beanModel.getBean());
                deviceContentTree.getStore().add(beanModel, false);
              } else if (beanModel.getBean() instanceof SensorDetailsDTO) {
                sensors.add((SensorDetailsDTO)beanModel.getBean());
                deviceContentTree.getStore().add(beanModel, false);
              }
            }
         });
         sliderWizardWindow.show();
      }
   }
   
   /**
    * Add a listener to delete the selected device content.
    */
   private final class DeleteContentListener extends SelectionListener<ButtonEvent> {
      @Override
      public void componentSelected(ButtonEvent ce) {
         List<BeanModel> selectedModels = deviceContentTree.getSelectionModel().getSelectedItems();
         
         for (BeanModel beanModel : selectedModels) {
            if (beanModel.getBean() instanceof DeviceCommandDetailsDTO) {
              DeviceCommandDetailsDTO commandToDelete = beanModel.getBean();
              for (SensorDetailsDTO s : sensors) {
                if (s.getCommand().getDto() == commandToDelete) {
                  MessageBox.alert("Warn", "Command is referenced by sensor " + s.getName() + ", it can't be deleted.", null);
                  return;
                }
              }
              for (SwitchDetailsDTO s : switches) {
                if (s.getOnCommand().getDto() == commandToDelete || s.getOffCommand().getDto() == commandToDelete) {
                  MessageBox.alert("Warn", "Command is referenced by switch " + s.getName() + ", it can't be deleted.", null);
                  return;
                }
              }
              for (SliderDetailsDTO s : sliders) {
                if (s.getCommand().getDto() == commandToDelete) {
                  MessageBox.alert("Warn", "Command is referenced by slider " + s.getName() + ", it can't be deleted.", null);
                  return;
                }
              }
              commands.remove(commandToDelete);
            } else if (beanModel.getBean() instanceof SensorDetailsDTO) {
              SensorDetailsDTO sensorToDelete = beanModel.getBean();
              for (SwitchDetailsDTO s : switches) {
                if (s.getSensor().getDto() == sensorToDelete) {
                  MessageBox.alert("Warn", "Sensor is referenced by switch " + s.getName() + ", it can't be deleted.", null);
                  return;
                }
              }
              for (SliderDetailsDTO s : sliders) {
                if (s.getSensor().getDto() == sensorToDelete) {
                  MessageBox.alert("Warn", "Sensor is referenced by slider " + s.getName() + ", it can't be deleted.", null);
                  return;
                }
              }
              sensors.remove(sensorToDelete);
            } else if(beanModel.getBean() instanceof SliderDetailsDTO) {
              sliders.remove(beanModel.getBean());
            } else if(beanModel.getBean() instanceof SwitchDetailsDTO) {
               switches.remove(beanModel.getBean());
            }
            deviceContentTree.getStore().remove(beanModel);
         }
      }      
   }
}
