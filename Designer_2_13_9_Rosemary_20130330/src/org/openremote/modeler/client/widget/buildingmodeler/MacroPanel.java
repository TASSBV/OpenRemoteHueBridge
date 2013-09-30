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

import org.openremote.modeler.client.event.DoubleClickEvent;
import org.openremote.modeler.client.event.MacroUpdatedEvent;
import org.openremote.modeler.client.event.MacroUpdatedEventHandler;
import org.openremote.modeler.client.event.MacrosCreatedEvent;
import org.openremote.modeler.client.event.MacrosCreatedEventHandler;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.gxtextends.SelectionServiceExt;
import org.openremote.modeler.client.gxtextends.SourceSelectionChangeListenerExt;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.ConfirmDeleteListener;
import org.openremote.modeler.client.listener.EditDelBtnSelectionListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.DeviceMacroBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.selenium.DebugId;
import org.openremote.modeler.shared.dto.DTOHelper;
import org.openremote.modeler.shared.dto.MacroDTO;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSupport;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Element;

/**
 * The panel contains a toolbar and a treePanel to manage macro.
 * It display in the west part of the page.
 */
public class MacroPanel extends ContentPanel {

  private EventBus eventBus;
  
   /** The icons. */
   private Icons icons = GWT.create(Icons.class);

   /** The macro tree. */
   private TreePanel<BeanModel> macroTree = null;

   /** The macro list container. */
   private LayoutContainer macroListContainer = null;

   /** The change listener map. */
   private Map<BeanModel, ChangeListener> changeListenerMap = null;

   /** The selection service. */
   private SelectionServiceExt<BeanModel> selectionService;
   
   /**
    * Instantiates a new macro panel.
    */
   public MacroPanel(EventBus eventBus) {
     this.eventBus = eventBus;
     bind();
      setHeading("Macros");
      setLayout(new FitLayout());
      selectionService = new SelectionServiceExt<BeanModel>();
      createMenu();
//      createMacroTree();
      setIcon(icons.macroIcon());
      getHeader().ensureDebugId(DebugId.DEVICE_MACRO_PANEL_HEADER);
   }

   private void bind() {
     eventBus.addHandler(MacroUpdatedEvent.TYPE, new MacroUpdatedEventHandler() {
      @Override
      public void onMacroUpdated(MacroUpdatedEvent event) {
        final TreeStore<BeanModel> macroTreeStore = macroTree.getStore();
        final BeanModel bm = DTOHelper.getBeanModel(event.getMacro());

        // EBR : adding a load listener so we can expand the macro once it has reloaded it's children
        // removing it when the load is done. It works but I don't like this approach, feels like a hack to me.
        final LoadListener ll = new LoadListener() {
          @Override
          public void loaderLoad(LoadEvent le) {
            macroTree.getSelectionModel().select(bm, true);
            macroTree.setExpanded(bm, true);
            super.loaderLoad(le);
            macroTreeStore.getLoader().removeLoadListener(this);
          }
        };
        if (macroTreeStore.contains(bm)) {
          // Important : for this to work, the DTO & BeanModel used for update must be the same instance as the one in the store
          // Current code in MacroWindow ensures that this is the case
          macroTreeStore.update(bm);
          macroTreeStore.getLoader().addLoadListener(ll);
          macroTreeStore.getLoader().loadChildren(bm);
        }
      }       
     });
     eventBus.addHandler(MacrosCreatedEvent.TYPE, new MacrosCreatedEventHandler() {
      @Override
      public void onMacrosCreated(MacrosCreatedEvent event) {
        List<BeanModel> bms = DTOHelper.createModels(event.getMacros());
        macroTree.getStore().add(bms, true);
      } 
     });
   }
   /**
    * Creates the menu.
    */
   private void createMenu() {
      ToolBar macroToolBar = new ToolBar();
      Button newMacroBtn = new Button("New");
      newMacroBtn.setToolTip("Create Macro");
      newMacroBtn.setIcon(icons.macroAddIcon());
      newMacroBtn.ensureDebugId(DebugId.NEW_MACRO_BTN);
      newMacroBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            final MacroWindow macroWindow = new MacroWindow();

            macroWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                 eventBus.fireEvent(new MacrosCreatedEvent((MacroDTO) be.getData()));
                  macroWindow.hide();
               }
            });
         }
      });
      macroToolBar.add(newMacroBtn);
      
      List<Button> editDelBtns = new ArrayList<Button>();
      Button editMacroBtn = new Button("Edit");
      editMacroBtn.setEnabled(false);
      editMacroBtn.setToolTip("Edit Macro");
      editMacroBtn.setIcon(icons.macroEditIcon());
      editMacroBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            onEditDeviceMacroBtnClicked();

         }
      });
      macroToolBar.add(editMacroBtn);
      Button deleteMacroBtn = new Button("Delete");
      deleteMacroBtn.setEnabled(false);
      deleteMacroBtn.setToolTip("Delete Macro");
      deleteMacroBtn.setIcon(icons.macroDeleteIcon());
      
      deleteMacroBtn.addSelectionListener(new ConfirmDeleteListener<ButtonEvent>() {
         @Override
         public void onDelete(ButtonEvent ce) {
            onDeleteDeviceMacroBtnClicked();
         }
      });
      macroToolBar.add(deleteMacroBtn);
      editDelBtns.add(editMacroBtn);
      editDelBtns.add(deleteMacroBtn);
      selectionService.addListener(new EditDelBtnSelectionListener(editDelBtns));
      setTopComponent(macroToolBar);
   }

   /**
    * Creates the macro tree.
    */
   private void createMacroTree() {
      macroListContainer = new LayoutContainer() {
         @Override
         protected void onRender(Element parent, int index) {
            super.onRender(parent, index);
            if (macroTree == null) {
               macroTree = TreePanelBuilder.buildMacroTree();
               selectionService.addListener(new SourceSelectionChangeListenerExt(macroTree.getSelectionModel()));
               selectionService.register(macroTree.getSelectionModel());
               macroListContainer.add(macroTree);
            }
            add(macroTree);
            macroTree.addListener(DoubleClickEvent.DOUBLECLICK, new Listener<DoubleClickEvent>() {
               public void handleEvent(DoubleClickEvent be) {
                  onEditDeviceMacroBtnClicked();
               }
               
            });
         }
      };
      // overflow-auto style is for IE hack.
      macroListContainer.addStyleName("overflow-auto");
      macroListContainer.setStyleAttribute("backgroundColor", "white");
      macroListContainer.setBorders(false);
      macroListContainer.setLayoutOnChange(true);
      macroListContainer.setHeight("100%");
      add(macroListContainer);
   }

   /**
    * On edit device macro btn clicked.
    */
   private void onEditDeviceMacroBtnClicked() {
      if (macroTree.getSelectionModel().getSelectedItem() != null && macroTree.getSelectionModel().getSelectedItem().getBean() instanceof MacroDTO) {
        MacroDTO macro = macroTree.getSelectionModel().getSelectedItem().getBean();
         final MacroWindow macroWindow = new MacroWindow(macro);
         macroWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
              eventBus.fireEvent(new MacroUpdatedEvent((MacroDTO) be.getData()));
               macroWindow.hide();
            }
         });
         macroWindow.show();
      }
   }

   /**
    * On delete device macro btn clicked.
    */
   private void onDeleteDeviceMacroBtnClicked() {
      if (macroTree.getSelectionModel().getSelectedItems().size() > 0) {
         for (final BeanModel data : macroTree.getSelectionModel().getSelectedItems()) {
            if (data.getBean() instanceof MacroDTO) {
               DeviceMacroBeanModelProxy.deleteDeviceMacro(data, new AsyncSuccessCallback<Void>() {
                  @Override
                  public void onSuccess(Void result) {
                     macroTree.getStore().remove(data);
                     Info.display("Info", "Delete success.");
                  }
               });
            }

         }
      }
   }

   /**
    * Gets the drag source bean model change listener.
    * 
    * @param target
    *           the target
    * 
    * @return the drag source bean model change listener
    */
   private ChangeListener getDragSourceBeanModelChangeListener(final BeanModel target) {
      if (changeListenerMap == null) {
         changeListenerMap = new HashMap<BeanModel, ChangeListener>();
      }
      ChangeListener changeListener = changeListenerMap.get(target);
      if (changeListener == null) {
         changeListener = new ChangeListener() {
            public void modelChanged(ChangeEvent changeEvent) {
               if (changeEvent.getType() == ChangeEventSupport.Remove) {
                  macroTree.getStore().remove(target);
               }
               if (changeEvent.getType() == ChangeEventSupport.Update) {
                  BeanModel source = (BeanModel) changeEvent.getItem();
                  if (source.getBean() instanceof DeviceMacro) {
                     DeviceMacro deviceMacro = (DeviceMacro) source.getBean();
                     DeviceMacroRef deviceMacroRef = (DeviceMacroRef) target.getBean();
                     deviceMacroRef.setTargetDeviceMacro(deviceMacro);
                  } else if (source.getBean() instanceof DeviceCommand) {
                     DeviceCommand deviceCommand = (DeviceCommand) source.getBean();
                     DeviceCommandRef deviceCommandRef = (DeviceCommandRef) target.getBean();
                     deviceCommandRef.setDeviceCommand(deviceCommand);
                  } else if (source.getBean() instanceof Device) {
                     Device device = (Device) source.getBean();
                     DeviceCommandRef targetDeviceCommandRef = (DeviceCommandRef) target.getBean();
                     targetDeviceCommandRef.setDeviceName(device.getName());
                  }
                  macroTree.getStore().update(target);
               }
            }
         };
         changeListenerMap.put(target, changeListener);
      }
      return changeListener;
   }

  @Override
  protected void onExpand() {
     if (macroListContainer == null) {
       createMacroTree();
     }
    super.onExpand();
  }
   
}
