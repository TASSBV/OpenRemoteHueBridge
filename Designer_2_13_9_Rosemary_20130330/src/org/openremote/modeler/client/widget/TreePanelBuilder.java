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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.event.DoubleClickEvent;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.model.TreeFolderBean;
import org.openremote.modeler.client.proxy.ConfigCategoryBeanModelProxy;
import org.openremote.modeler.client.proxy.DeviceBeanModelProxy;
import org.openremote.modeler.client.proxy.DeviceCommandBeanModelProxy;
import org.openremote.modeler.client.proxy.DeviceMacroBeanModelProxy;
import org.openremote.modeler.client.proxy.TemplateProxy;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.buildingmodeler.ControllerConfigTabItem;
import org.openremote.modeler.client.widget.uidesigner.TemplatePanelImpl;
import org.openremote.modeler.domain.ConfigCategory;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.ScreenPairRef;
import org.openremote.modeler.domain.Template;
import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.UIGrid;
import org.openremote.modeler.domain.component.UIImage;
import org.openremote.modeler.domain.component.UILabel;
import org.openremote.modeler.domain.component.UISlider;
import org.openremote.modeler.domain.component.UISwitch;
import org.openremote.modeler.domain.component.UITabbar;
import org.openremote.modeler.domain.component.UITabbarItem;
import org.openremote.modeler.shared.dto.DTOHelper;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.modeler.shared.dto.DeviceWithChildrenDTO;
import org.openremote.modeler.shared.dto.MacroDTO;
import org.openremote.modeler.shared.dto.MacroItemDTO;
import org.openremote.modeler.shared.dto.MacroItemType;
import org.openremote.modeler.shared.dto.SensorDTO;
import org.openremote.modeler.shared.dto.SliderDTO;
import org.openremote.modeler.shared.dto.SwitchDTO;

import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * The Class is used for create tree.
 */
public class TreePanelBuilder {

   /**
    * Not be instantiated.
    */
   private TreePanelBuilder() {
   }

   /** The Constant icon. */
   private static final Icons ICON = GWT.create(Icons.class);

   /** The device command treestore. */
   private static TreeStore<BeanModel> deviceTreeStore = null;

   private static TreeStore<BeanModel> deviceAndCmdTreeStore = null;
   
   private static TreeStore<BeanModel> commandsAndMacrosTreeStore = null;

   /** The macro tree store. */
   private static TreeStore<BeanModel> macroTreeStore = null;

   private static TreeStore<BeanModel> widgetTreeStore = null;
   private static TreeStore<BeanModel> panelTreeStore = null;
   private static TreeStore<BeanModel> controllerConfigCategoryTreeStore = null;
   private static TreeStore<BeanModel> templateTreeStore = null;

   /**
    * Builds a device command tree.
    * It list all devices on top level and when expanded, each device lists its commands.
    * 
    * @return the a new device command tree
    */
   public static TreePanel<BeanModel> buildDeviceCommandTree() {
      // if (commandTreeStore == null) {
      RpcProxy<List<BeanModel>> loadDeviceRPCProxy = new RpcProxy<List<BeanModel>>() {
         @Override
         protected void load(Object o, final AsyncCallback<List<BeanModel>> listAsyncCallback) {
            DeviceBeanModelProxy.loadDeviceAndCommand((BeanModel) o, new AsyncSuccessCallback<List<BeanModel>>() {
               public void onSuccess(List<BeanModel> result) {
                  listAsyncCallback.onSuccess(result);
               }
            });
         }
      };
      TreeLoader<BeanModel> loadDeviceTreeLoader = new BaseTreeLoader<BeanModel>(loadDeviceRPCProxy) {
         @Override
         public boolean hasChildren(BeanModel beanModel) {
            if (beanModel.getBean() instanceof DeviceDTO) {
               return true;
            }
            return false;
         }
      };
      deviceAndCmdTreeStore = new TreeStore<BeanModel>(loadDeviceTreeLoader);
      // }
      final TreePanel<BeanModel> tree = new TreePanel<BeanModel>(deviceAndCmdTreeStore) {
         @Override
         protected void afterRender() {
            super.afterRender();
            mask("Loading...");
            removeStyleName("x-masked");
         }
      };

      tree.setBorders(false);
      tree.setStateful(true);
      tree.setDisplayProperty("displayName");
      tree.setStyleAttribute("overflow", "auto");
      tree.setHeight("100%");
      tree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            if (thisModel.getBean() instanceof DeviceCommandDTO) {
               return ICON.deviceCmd();
            } else if (thisModel.getBean() instanceof DeviceDTO) {
               return ICON.device();
            } else {
               return ICON.folder();
            }
         }
      });
      return tree;
   }

   /**
    * Builds a device command tree.
    * It list all devices on top level and when expanded, each device lists its commands.
    * 
    * @return the a new device command tree
    */
   public static TreePanel<BeanModel> buildCommandAndMacroTree() {
      RpcProxy<List<BeanModel>> loadDeviceRPCProxy = new RpcProxy<List<BeanModel>>() {
         @Override
         protected void load(Object o, final AsyncCallback<List<BeanModel>> listAsyncCallback) {
           BeanModel beanModel = (BeanModel)o;
           
           if (beanModel.getBean() instanceof TreeFolderBean) {
             TreeFolderBean treeFolderBean = (TreeFolderBean)beanModel.getBean();
             if (treeFolderBean.getType() == Constants.DEVICES) {
               AsyncServiceFactory.getDeviceServiceAsync().loadAllDTOs(new AsyncSuccessCallback<ArrayList<DeviceDTO>>() {
                 public void onSuccess(ArrayList<DeviceDTO> result) {
                   listAsyncCallback.onSuccess(DTOHelper.createModels(result));
                 }        
               });
             } else if (treeFolderBean.getType() == Constants.MACROS) {
               AsyncServiceFactory.getDeviceMacroServiceAsync().loadAllDTOs(new AsyncSuccessCallback<ArrayList<MacroDTO>>() {
                @Override
                public void onSuccess(ArrayList<MacroDTO> result) {
                  listAsyncCallback.onSuccess(DTOHelper.createModels(result));
                }
               });
             }
           } else if(beanModel.getBean() instanceof DeviceDTO) {
               DeviceDTO device = (DeviceDTO) beanModel.getBean();
               AsyncServiceFactory.getDeviceServiceAsync().loadDeviceWithCommandChildrenDTOById(device.getOid(), new AsyncSuccessCallback<DeviceWithChildrenDTO>() {
  
                  @Override
                  public void onSuccess(DeviceWithChildrenDTO result) {
                    listAsyncCallback.onSuccess(DTOHelper.createModels(result.getDeviceCommands()));
                  }
               });
           }
         }
      };
      TreeLoader<BeanModel> loadDeviceTreeLoader = new BaseTreeLoader<BeanModel>(loadDeviceRPCProxy) {
         @Override
         public boolean hasChildren(BeanModel beanModel) {
            if (beanModel.getBean() instanceof TreeFolderBean || beanModel.getBean() instanceof DeviceDTO) {
               return true;
            }
            return false;
         }
      };
      commandsAndMacrosTreeStore = new TreeStore<BeanModel>(loadDeviceTreeLoader);

      TreeFolderBean devicesBean = new TreeFolderBean();
      devicesBean.setDisplayName("Devices");
      devicesBean.setType(Constants.DEVICES);
      TreeFolderBean macrosBean = new TreeFolderBean();
      macrosBean.setDisplayName("Macros");
      macrosBean.setType(Constants.MACROS);
      commandsAndMacrosTreeStore.add(devicesBean.getBeanModel(), true);
      commandsAndMacrosTreeStore.add(macrosBean.getBeanModel(), true);
      
      final TreePanel<BeanModel> tree = new TreePanel<BeanModel>(commandsAndMacrosTreeStore);
      tree.setBorders(false);
      tree.setStateful(true);
      tree.setDisplayProperty("displayName");
      tree.setStyleAttribute("overflow", "auto");
      tree.setHeight("100%");
      tree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            if (thisModel.getBean() instanceof DeviceCommandDTO) {
               return ICON.deviceCmd();
            } else if (thisModel.getBean() instanceof DeviceDTO) {
               return ICON.device();
            } else if (thisModel.getBean() instanceof MacroDTO) {
                 return ICON.macroIcon();
            } else {
               return ICON.folder();
            }
         }
      });
      return tree;
   }

   /**
    * Builds a tree that list all devices on top level. When expanded, a device lists all its children : commands, sensors, switches and sliders.
    * 
    * @return
    */
   public static TreePanel<BeanModel> buildDeviceTree() {
      if (deviceTreeStore == null) {
         RpcProxy<List<BeanModel>> loadDeviceRPCProxy = new RpcProxy<List<BeanModel>>() {
            @Override
            protected void load(Object o, final AsyncCallback<List<BeanModel>> listAsyncCallback) {
               DeviceBeanModelProxy.loadDevice((BeanModel) o, new AsyncSuccessCallback<List<BeanModel>>() {
                  public void onSuccess(List<BeanModel> result) {
                     listAsyncCallback.onSuccess(result);
                  }
               });
            }
         };
         final TreeLoader<BeanModel> loadDeviceTreeLoader = new BaseTreeLoader<BeanModel>(loadDeviceRPCProxy) {
            @Override
            public boolean hasChildren(BeanModel beanModel) {
               if (beanModel.getBean() instanceof DeviceCommandDTO || beanModel.getBean() instanceof UICommand) { // TODO EBR : can there be UICommand here ?
                  return false;
               }
               return true;
            }
         };
         deviceTreeStore = new TreeStore<BeanModel>(loadDeviceTreeLoader);
      }
      final TreePanel<BeanModel> tree = new TreePanel<BeanModel>(deviceTreeStore) {
         @SuppressWarnings("unchecked")
         @Override
         protected void onDoubleClick(TreePanelEvent tpe) {
            super.onDoubleClick(tpe);
            this.fireEvent(DoubleClickEvent.DOUBLECLICK, new DoubleClickEvent());
         }

         @Override
         protected void afterRender() {
            super.afterRender();
            mask("Loading devices...");
            removeStyleName("x-masked");
         }
      };

      tree.setBorders(false);
      tree.setStateful(true);
      tree.setDisplayProperty("displayName");
      tree.setStyleAttribute("overflow", "auto");
      tree.setHeight("100%");
      tree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            if (thisModel.getBean() instanceof DeviceCommandDTO) {
               return ICON.deviceCmd();
            } else if (thisModel.getBean() instanceof DeviceDTO) {
               return ICON.device();
            } else if (thisModel.getBean() instanceof SensorDTO) {
               return ICON.sensorIcon();
            } else if (thisModel.getBean() instanceof SwitchDTO) {
               return ICON.switchIcon();
            } else if (thisModel.getBean() instanceof SliderDTO) {
               return ICON.sliderIcon();
            } else if (thisModel.getBean() instanceof UICommand) { // TODO EBR : can there be UICommand here ?
               return ICON.deviceCmd();
            } else {
               return ICON.folder();
            }
         }

      });
      return tree;
   }

   public static TreePanel<BeanModel> buildCommandTree(final Long deviceId, final Long selectedCommandId) {
     RpcProxy<List<BeanModel>> loadDeviceRPCProxy = new RpcProxy<List<BeanModel>>() {
        @Override
        protected void load(Object o, final AsyncCallback<List<BeanModel>> listAsyncCallback) {
           DeviceCommandBeanModelProxy.loadDeviceCommandsDTOFromDeviceId(deviceId,
                 new AsyncSuccessCallback<ArrayList<DeviceCommandDTO>>() {

                    @Override
                    public void onSuccess(ArrayList<DeviceCommandDTO> result) {
                       listAsyncCallback.onSuccess(DTOHelper.createModels(result));
                    }

                 });
        }
     };
     TreeLoader<BeanModel> loadDeviceTreeLoader = new BaseTreeLoader<BeanModel>(loadDeviceRPCProxy) {
        @Override
        public boolean hasChildren(BeanModel beanModel) {
           if (beanModel.getBean() instanceof DeviceDTO) {
              return true;
           }
           return false;
        }

     };
     TreeStore<BeanModel> commandTree = new TreeStore<BeanModel>(loadDeviceTreeLoader);
     final TreePanel<BeanModel> tree = new TreePanel<BeanModel>(commandTree);
     loadDeviceTreeLoader.addLoadListener(new LoadListener() {
        public void loaderLoad(LoadEvent le) {
           super.loaderLoad(le);
           if (selectedCommandId != null) {
             for (BeanModel bm : ((List<BeanModel>)le.getData())) {
               DeviceCommandDTO dto = bm.getBean();
               if (dto.getOid().equals(selectedCommandId)) {
                 tree.getSelectionModel().select(bm, false);
               }
             }
           }
        }
        
     });

      tree.setBorders(false);
      tree.setStateful(true);
      tree.setDisplayProperty("displayName");
      tree.setStyleAttribute("overflow", "auto");
      tree.setHeight("100%");
      tree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            if (thisModel.getBean() instanceof DeviceCommandDTO) {
               return ICON.deviceCmd();
            } else if (thisModel.getBean() instanceof DeviceDTO) {
               return ICON.device();
            } else {
               return ICON.folder();
            }
         }

      });
      return tree;
   }

   /**
    * Builds a new macro tree.
    * 
    * @return a new macro tree
    */
   public static TreePanel<BeanModel> buildMacroTree() {
      if (macroTreeStore == null) {
         RpcProxy<List<BeanModel>> loadDeviceMacroRPCProxy = new RpcProxy<List<BeanModel>>() {

            protected void load(Object o, final AsyncCallback<List<BeanModel>> listAsyncCallback) {
               DeviceMacroBeanModelProxy.loadDeviceMaro((BeanModel) o, new AsyncSuccessCallback<List<BeanModel>>() {

                  public void onSuccess(List<BeanModel> result) {
                     listAsyncCallback.onSuccess(result);
                  }
               });
            }
         };
         BaseTreeLoader<BeanModel> loadDeviceMacroTreeLoader = new BaseTreeLoader<BeanModel>(loadDeviceMacroRPCProxy) {
            @Override
            public boolean hasChildren(BeanModel beanModel) {
               if (beanModel.getBean() instanceof MacroDTO) {
                  return true;
               }
               return false;
            }
         };
         macroTreeStore = new TreeStore<BeanModel>(loadDeviceMacroTreeLoader);
      }

      final TreePanel<BeanModel> tree = new TreePanel<BeanModel>(macroTreeStore) {
         @SuppressWarnings("unchecked")
         @Override
         protected void onDoubleClick(TreePanelEvent tpe) {
            super.onDoubleClick(tpe);
            this.fireEvent(DoubleClickEvent.DOUBLECLICK, new DoubleClickEvent());
         }
      };
      tree.setStateful(true);
      tree.setBorders(false);
      tree.setHeight("100%");
      tree.setDisplayProperty("displayName");
      tree.setStyleAttribute("overflow", "auto");

      tree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {

            if (thisModel.getBean() instanceof MacroDTO) {
               return ICON.macroIcon();
            } else if (thisModel.getBean() instanceof MacroItemDTO) {
              MacroItemType type = ((MacroItemDTO)thisModel.getBean()).getType();
              switch (type) {
                case Command:
                  return ICON.deviceCmd();
                case Delay:
                  return ICON.delayIcon();
                default:
                  return ICON.macroIcon();
              }
            } else {
               return ICON.macroIcon();
            }
         }
      });
      return tree;
   }

   /**
    * Builds the widget tree, it contain all kind's of component.
    * 
    * @return the tree panel< bean model>
    */
   public static TreePanel<BeanModel> buildWidgetTree() {
      if (widgetTreeStore == null) {
         widgetTreeStore = new TreeStore<BeanModel>();
      }
      TreePanel<BeanModel> widgetTree = new TreePanel<BeanModel>(widgetTreeStore);
      widgetTree.setStateful(true);
      widgetTree.setBorders(false);
      widgetTree.setHeight("100%");
      widgetTree.setDisplayProperty("name");
      widgetTree.setStyleAttribute("overflow", "auto");

      widgetTreeStore.add(new UIGrid().getBeanModel(), true);
      widgetTreeStore.add(new UILabel().getBeanModel(), true);
      widgetTreeStore.add(new UIImage().getBeanModel(), true);
      widgetTreeStore.add(new UIButton().getBeanModel(), true);
      widgetTreeStore.add(new UISwitch().getBeanModel(), true);
      widgetTreeStore.add(new UISlider().getBeanModel(), true);
      widgetTreeStore.add(new UITabbar().getBeanModel(), true);
      widgetTreeStore.add(new UITabbarItem().getBeanModel(), true);

      widgetTree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            if (thisModel.getBean() instanceof UIButton) {
               return ICON.buttonIcon();
            } else if (thisModel.getBean() instanceof UISwitch) {
               return ICON.switchIcon();
            } else if (thisModel.getBean() instanceof UILabel) {
               return ICON.labelIcon();
            } else if (thisModel.getBean() instanceof UIImage) {
               return ICON.imageIcon();
            } else if (thisModel.getBean() instanceof UISlider) {
               return ICON.sliderIcon();
            } else if (thisModel.getBean() instanceof UIGrid) {
               return ICON.gridIcon();
            } else if (thisModel.getBean() instanceof UITabbar) {
               return ICON.tabbarConfigIcon();
            } else if (thisModel.getBean() instanceof UITabbarItem) {
               return ICON.tabbarItemIcon();
            } else {
               return ICON.buttonIcon();
            }
         }
      });

      return widgetTree;
   }

   public static TreePanel<BeanModel> buildPanelTree() {
      if (panelTreeStore == null) {
         panelTreeStore = new TreeStore<BeanModel>();
      }
      TreePanel<BeanModel> panelTree = new TreePanel<BeanModel>(panelTreeStore) {
         @Override
         protected void afterRender() {
            super.afterRender();
            mask("Loading panels...");
            removeStyleName("x-masked");
         }

      };
      panelTree.setStateful(true);
      panelTree.setBorders(false);
      panelTree.setHeight("100%");
      panelTree.setDisplayProperty("displayName");
      panelTree.setStyleAttribute("overflow", "auto");

      panelTree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            if (thisModel.getBean() instanceof Panel) {
               return ICON.panelIcon();
            } else if (thisModel.getBean() instanceof GroupRef) {
               return ICON.groupIcon();
            } else if (thisModel.getBean() instanceof ScreenPairRef) {
               if (((ScreenPairRef) thisModel.getBean()).getScreen().getRefCount() > 1) {
                  return ICON.screenLinkIcon();
               }
               return ICON.screenIcon();
            } else {
               return ICON.panelIcon();
            }
         }
      });

      return panelTree;
   }

   public static TreePanel<BeanModel> buildPanelTree(TreeStore<BeanModel> store) {
      TreePanel<BeanModel> panelTree = new TreePanel<BeanModel>(store);

      panelTree.setStateful(true);
      panelTree.setBorders(false);
      panelTree.setHeight("100%");
      panelTree.setDisplayProperty("displayName");
      panelTree.setStyleAttribute("overflow", "auto");

      panelTree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            if (thisModel.getBean() instanceof Panel) {
               return ICON.panelIcon();
            } else if (thisModel.getBean() instanceof GroupRef) {
               return ICON.groupIcon();
            } else if (thisModel.getBean() instanceof ScreenPairRef) {
               return ICON.screenIcon();
            } else {
               return ICON.panelIcon();
            }
         }
      });

      return panelTree;
   }

   public static TreePanel<BeanModel> buildControllerConfigCategoryPanelTree(final TabPanel configTabPanel) {
      if (controllerConfigCategoryTreeStore == null) {
         controllerConfigCategoryTreeStore = new TreeStore<BeanModel>();
         ConfigCategoryBeanModelProxy.getAllCategory(new AsyncSuccessCallback<Set<ConfigCategory>>() {

            @Override
            public void onSuccess(Set<ConfigCategory> result) {
               for (ConfigCategory category : result) {
                  controllerConfigCategoryTreeStore.add(category.getBeanModel(), false);
               }
            }
         });
      }
      TreePanel<BeanModel> tree = new TreePanel<BeanModel>(controllerConfigCategoryTreeStore) {
         @Override
         public void onBrowserEvent(Event event) {
            if (event.getTypeInt() == Event.ONCLICK) {
               BeanModel beanModel = this.getSelectionModel().getSelectedItem();
               final ConfigCategory  category = beanModel.getBean();
               configTabPanel.removeAll();
               ControllerConfigTabItem configTabItem = new ControllerConfigTabItem(category);
               configTabPanel.add(configTabItem);
               configTabItem.addListener(SubmitEvent.SUBMIT, new SubmitListener() {

                  @Override
                  public void afterSubmit(SubmitEvent be) {
                     configTabPanel.removeAll();
                     ControllerConfigTabItem configTabItem = new ControllerConfigTabItem(category);
                     configTabPanel.add(configTabItem);
                  }
                  
               });
            }
            super.onBrowserEvent(event);
         }
      };
      tree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            return ICON.configIcon();
         }
      });

      tree.setStateful(true);
      tree.setBorders(false);
      tree.setHeight("100%");
      tree.setDisplayProperty("name");
      return tree;
   }

   public static TreePanel<BeanModel> buildTemplateTree(final TemplatePanelImpl templatePanel) {
      TreeFolderBean privateTemplatesBean = new TreeFolderBean();
      privateTemplatesBean.setDisplayName("My private templates");

      TreeFolderBean publicTemplatesBean = new TreeFolderBean();
      publicTemplatesBean.setDisplayName("My public templates");
      RpcProxy<List<BeanModel>> loadTemplateRPCProxy = new RpcProxy<List<BeanModel>>() {

         @Override
         protected void load(Object loadConfig, final AsyncCallback<List<BeanModel>> callback) {
            if (loadConfig != null && loadConfig instanceof BeanModel) {
               BeanModel model = (BeanModel) loadConfig;
               if (model.getBean() instanceof TreeFolderBean) {
                  TreeFolderBean folderBean = model.getBean();
                  if (folderBean.getDisplayName().contains("rivate")) {
                     TemplateProxy.getTemplates(true, new AsyncSuccessCallback<List<Template>>() {

                        @Override
                        public void onSuccess(List<Template> result) {
                           callback.onSuccess(Template.createModels(result));
                        }

                     });
                  } else {
                     TemplateProxy.getTemplates(false, new AsyncSuccessCallback<List<Template>>() {

                        @Override
                        public void onSuccess(List<Template> result) {
                           callback.onSuccess(Template.createModels(result));
                        }

                     });
                  }
               }
            }
         }

      };
      
      TreeLoader<BeanModel> templateLoader = new BaseTreeLoader<BeanModel>(loadTemplateRPCProxy) {
         @Override
         public boolean hasChildren(BeanModel beanModel) {
            if (beanModel.getBean() instanceof TreeFolderBean) {
               return true;
            }
            return false;
         }
      };

      if (templateTreeStore == null) {
         templateTreeStore = new TreeStore<BeanModel>(templateLoader);
      }

      //set private template folder as the first node
      templateTreeStore.add(privateTemplatesBean.getBeanModel(), false);
      
      //set public template folder as the second node. 
      templateTreeStore.add(publicTemplatesBean.getBeanModel(), false);

      TreePanel<BeanModel> tree = new TreePanel<BeanModel>(templateTreeStore) {
        @Override
        public void onBrowserEvent(Event event) {
           super.onBrowserEvent(event);
           if (event.getTypeInt() == Event.ONCLICK) {
              BeanModel beanModel = this.getSelectionModel().getSelectedItem();
              if (beanModel != null && beanModel.getBean() instanceof Template) {
                // When template is already selected in tree, user then goes on to select a screen,
                // comes back to templates and clicks on a template to display it
                // If template still selected, no selection event -> must "simulate" one
                // This call will eventually fire on event on the event bus
                templatePanel.templateClicked((Template)beanModel.getBean());
              }
           }
        }
     };

      tree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            if (thisModel.getBean() instanceof TreeFolderBean) {
               return ICON.folder();
            }
            return ICON.templateIcon();
         }
      });
      
      tree.setStateful(true);
      tree.setBorders(false);
      tree.setHeight("100%");
      tree.setDisplayProperty("displayName");
      return tree;
   }
}
