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
package org.openremote.modeler.client.widget.uidesigner;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.gxtextends.SelectionServiceExt;
import org.openremote.modeler.client.gxtextends.SourceSelectionChangeListenerExt;
import org.openremote.modeler.client.gxtextends.TreePanelDragSourcePanelTreeExt;
import org.openremote.modeler.client.gxtextends.TreePanelDropTargetPanelTreeExt;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.ConfirmDeleteListener;
import org.openremote.modeler.client.listener.EditDelBtnSelectionListener;
import org.openremote.modeler.client.listener.PanelTreeStoreChangeListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.utils.ScreenFromTemplate;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPairRef;
import org.openremote.modeler.domain.component.UITabbarItem;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
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
import com.google.gwt.user.client.Element;

/**
 * The content panel includes a toolbar and a treePanel for managing panels, groups and screens.
 */
public class ProfilePanel extends ContentPanel {

   private TreePanel<BeanModel> panelTree;
   private Icons icon = GWT.create(Icons.class);
   private SelectionServiceExt<BeanModel> selectionService;
   private boolean initialized = false;
   private Button editButton;
   
   /**
    * Instantiates a new profile panel.
    */
   public ProfilePanel() {
      selectionService = new SelectionServiceExt<BeanModel>();
      setHeading("Panel");
      setIcon(icon.panelIcon());
      setLayout(new FitLayout());
      createMenu();
      createPanelTree();
      new TreePanelDragSourcePanelTreeExt(panelTree);
      new TreePanelDropTargetPanelTreeExt(panelTree);
   }

   /**
    * Creates the menu.
    */
   private void createMenu() {
      ToolBar toolBar = new ToolBar();
      List<Button> editDelBtns = new ArrayList<Button>();
      toolBar.add(createNewBtn());

      editButton = createEditBtn();
      Button deleteBtn = createDeleteBtn();
      deleteBtn.setEnabled(false);

      toolBar.add(editButton);
      toolBar.add(deleteBtn);
      editDelBtns.add(editButton);
      editDelBtns.add(deleteBtn);
      selectionService.addListener(new EditDelBtnSelectionListener(editDelBtns) {
         @Override
         protected boolean isEditableAndDeletable(List<BeanModel> sels) {
            BeanModel selectModel = sels.get(0);
            if (selectModel != null) {
               return true;
            }
            return false;
         }
      });

      setTopComponent(toolBar);
   }

   /**
    * Creates the screen tree.
    */
   private void createPanelTree() {
      panelTree = TreePanelBuilder.buildPanelTree();
      
      selectionService.addListener(new SourceSelectionChangeListenerExt(panelTree.getSelectionModel()));
      selectionService.register(panelTree.getSelectionModel());
      LayoutContainer treeContainer = new LayoutContainer() {
         @Override
         protected void onRender(Element parent, int index) {
            super.onRender(parent, index);
//            addTreeStoreEventListener();
            new PanelTreeStoreChangeListener(panelTree);
            add(panelTree);
         }
      };
      // overflow-auto style is for IE hack.
      treeContainer.addStyleName("overflow-auto");
      treeContainer.setStyleAttribute("backgroundColor", "white");
      treeContainer.setBorders(false);
      add(treeContainer);
   }
   
   /**
    * Creates the new btn.
    * 
    * @return the button
    */
   private Button createNewBtn() {
      Button newButton = new Button("New");
      newButton.setIcon(icon.add());
      Menu newMenu = new Menu();
      newMenu.add(createNewPanelMenuItem());
      newMenu.add(createCustomPanelMenuItem());
      final MenuItem newGroupMenu = createNewGroupMenuItem();
      newMenu.add(newGroupMenu);
      final MenuItem newScreenMenu = createNewScreenMenuItem();
      newMenu.add(newScreenMenu);
      final MenuItem newScreenFromTemplateMenu = createNewScreenFromTemplateMenuItem();
      newMenu.add(newScreenFromTemplateMenu);
//      final MenuItem configTabbarItem = createConfigTabbarMenuItem();
//      newMenu.add(configTabbarItem);
      newMenu.addListener(Events.BeforeShow, new Listener<MenuEvent>() {
         @Override
         public void handleEvent(MenuEvent be) {
//            boolean enabled = false;
//            BeanModel selectedBeanModel = panelTree.getSelectionModel().getSelectedItem();
//            if (selectedBeanModel != null && !(selectedBeanModel.getBean() instanceof ScreenPairRef)) {
//               enabled = true;
//            }
//            configTabbarItem.setEnabled(enabled);
            if (BeanModelDataBase.panelTable.loadAll().size() > 0) {
               newGroupMenu.setEnabled(true);
            } else {
               newGroupMenu.setEnabled(false);
            }
            if (BeanModelDataBase.groupTable.loadAll().size() > 0) {
               newScreenMenu.setEnabled(true);
               newScreenFromTemplateMenu.setEnabled(true);
            } else {
               newScreenMenu.setEnabled(false);
               newScreenFromTemplateMenu.setEnabled(false);
            }
         }
         
      });
      newButton.setMenu(newMenu);
      return newButton;
   }

   /**
    * Creates the edit btn.
    * 
    * @return the button
    */
   private Button createEditBtn() {
      Button editBtn = new Button("Edit");
      editBtn.setIcon(icon.edit());
      editBtn.setEnabled(false);
      return editBtn;
   }

   /**
    * Creates the delete btn.
    * 
    * @return the button
    */
   private Button createDeleteBtn() {
      Button deleteBtn = new Button("Delete");
      deleteBtn.setIcon(icon.delete());
      deleteBtn.addSelectionListener(new ConfirmDeleteListener<ButtonEvent>() {
         @Override
         public void onDelete(ButtonEvent ce) {
            List<BeanModel> selectedModels = panelTree.getSelectionModel().getSelectedItems();
            for (BeanModel selectedModel : selectedModels) {
               if (selectedModel != null && selectedModel.getBean() instanceof Panel) {
                  Panel panel = selectedModel.getBean();
                  BeanModelDataBase.panelTable.delete(selectedModel);
                  panelTree.getStore().remove(selectedModel);
                  for (GroupRef groupRef : panel.getGroupRefs()) {
                     groupRef.getGroup().releaseRef();
                     if (groupRef.getGroup().getRefCount() == 0) {
                        BeanModelDataBase.groupTable.delete(groupRef.getGroupId());
                     }
                     for (ScreenPairRef screenRef : groupRef.getGroup().getScreenRefs()) {
                        screenRef.getScreen().releaseRef();
                        if (screenRef.getScreen().getRefCount() == 0) {
                           BeanModelDataBase.screenTable.delete(screenRef.getScreenId());
                        } else if (screenRef.getScreen().getRefCount() == 1) {
                           BeanModelDataBase.screenTable.update(screenRef.getScreen().getBeanModel());
                        }
                     }
                  }
                  Info.display("Info", "Delete panel " + selectedModel.get("name") + " success.");
               } else if (selectedModel != null && selectedModel.getBean() instanceof GroupRef) {
                  panelTree.getStore().remove(selectedModel);
                  GroupRef groupRef = selectedModel.getBean();
                  groupRef.getPanel().removeGroupRef(groupRef);
                  groupRef.getGroup().releaseRef();
                  if (groupRef.getGroup().getRefCount() == 0) {
                     BeanModelDataBase.groupTable.delete(groupRef.getGroupId());
                  }
                  for (ScreenPairRef screenRef : groupRef.getGroup().getScreenRefs()) {
                     screenRef.getScreen().releaseRef();
                     if (screenRef.getScreen().getRefCount() == 0) {
                        BeanModelDataBase.screenTable.delete(screenRef.getScreenId());
                     } else if (screenRef.getScreen().getRefCount() == 1) {
                        BeanModelDataBase.screenTable.update(screenRef.getScreen().getBeanModel());
                     }
                  }
               } else if (selectedModel != null && selectedModel.getBean() instanceof ScreenPairRef) {
                  ScreenPairRef screenRef = (ScreenPairRef) selectedModel.getBean();
                  screenRef.getGroup().removeScreenRef(screenRef);
                  panelTree.getStore().remove(selectedModel);
                  screenRef.getScreen().releaseRef();
                  if (screenRef.getScreen().getRefCount() == 0) {
                     BeanModelDataBase.screenTable.delete(screenRef.getScreenId());
                  } else if (screenRef.getScreen().getRefCount() == 1) {
                     BeanModelDataBase.screenTable.update(screenRef.getScreen().getBeanModel());
                  }
               }
            }
         }
      });
      return deleteBtn;
   }

   private void afterCreatePanel(Panel panel) {
      BeanModel panelBeanModel = panel.getBeanModel();
      panelTree.getStore().add(panelBeanModel, false);
      for (GroupRef groupRef : panel.getGroupRefs()) {
         panelTree.getStore().add(panelBeanModel, groupRef.getBeanModel(), false);
         for (ScreenPairRef screenRef : groupRef.getGroup().getScreenRefs()) {
            panelTree.getStore().add(groupRef.getBeanModel(), screenRef.getBeanModel(), false);
         }
      }
      panelTree.setExpanded(panelBeanModel, true, true);
      panelTree.getSelectionModel().select(panelBeanModel, false);
      Info.display("Info", "Create Panel " + panel.getName() + " success.");
   }

   private MenuItem createNewPanelMenuItem() {
      MenuItem newPanelItem = new MenuItem("New Panel");
      newPanelItem.setIcon(icon.panelIcon());
      newPanelItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            final PanelWindow panelWindow = new PanelWindow();
            panelWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  panelWindow.hide();
                  afterCreatePanel(be.<Panel> getData());
               }

            });
            
         }
      });
      return newPanelItem;
   }
   
   private MenuItem createCustomPanelMenuItem() {
      MenuItem customPanelItem = new MenuItem("Custom Panel");
      customPanelItem.setIcon(icon.panelIcon());
      customPanelItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            final CustomPanelWindow customPanelWindow = new CustomPanelWindow();
            customPanelWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  customPanelWindow.hide();
                  afterCreatePanel(be.<Panel> getData());
               }

            });
            
         }
      });
      return customPanelItem;
   }
   private MenuItem createNewGroupMenuItem() {
      MenuItem newGroupItem = new MenuItem("New Group");
      newGroupItem.setIcon(icon.groupIcon());
      newGroupItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         @Override
         public void componentSelected(MenuEvent ce) {
            final Group group = new Group();
            group.setOid(IDUtil.nextID());
            GroupRef groupRef = new GroupRef(group);
            BeanModel selectedBeanModel = panelTree.getSelectionModel().getSelectedItem();
            if (selectedBeanModel != null) {
               Panel panel = null;
               if (selectedBeanModel.getBean() instanceof Panel) {
                  panel = (Panel) selectedBeanModel.getBean();
               } else if (selectedBeanModel.getBean() instanceof GroupRef) {
                  panel = (Panel) panelTree.getStore().getParent(selectedBeanModel).getBean();
               } else if (selectedBeanModel.getBean() instanceof ScreenPairRef) {
                  panel = (Panel) panelTree.getStore().getParent(panelTree.getStore().getParent(selectedBeanModel)).getBean();
               }
               groupRef.setPanel(panel);
               group.setParentPanel(panel);
            }
            final GroupWizardWindow  groupWizardWindow = new GroupWizardWindow(groupRef.getBeanModel());
            groupWizardWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  groupWizardWindow.hide();
                  BeanModel groupRefModel = be.getData();
                  GroupRef groupRef = groupRefModel.getBean();
                  group.setParentPanel(groupRef.getPanel());
                  panelTree.getStore().add(groupRef.getPanel().getBeanModel(), groupRefModel, false);
                  for (ScreenPairRef screenRef : groupRef.getGroup().getScreenRefs()) {
                     if (screenRef.getScreen().getRefCount() > 1) {
                        BeanModelDataBase.screenTable.update(screenRef.getScreen().getBeanModel());
                     }
                     panelTree.getStore().add(groupRefModel, screenRef.getBeanModel(), false);
                  }
                  BeanModelDataBase.groupTable.insert(groupRef.getGroup().getBeanModel());
                  panelTree.setExpanded(groupRefModel, true);
                  panelTree.getSelectionModel().select(groupRefModel, false);
                  Info.display("Info", "Add Group " + groupRef.getGroup().getName() + " success.");
                  
               }
            });
         }
         
      });
      return newGroupItem;
   }

   private MenuItem createNewScreenFromTemplateMenuItem() {
      MenuItem newScreenItem = new MenuItem("New Screen From Template ");
      newScreenItem.setIcon(icon.screenIcon());
      newScreenItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            BeanModel selectedItem = panelTree.getSelectionModel().getSelectedItem();
            
            if (selectedItem == null) {
               MessageBox.alert("Warn", "A group should be selected! ", null);
               return;
            } else if (selectedItem.getBean() instanceof ScreenPairRef) {
               selectedItem = panelTree.getStore().getParent(selectedItem);
            } else if (selectedItem.getBean() instanceof Panel) {
               selectedItem = panelTree.getStore().getChild(selectedItem, 0);
            }
            final GroupRef groupRef = selectedItem.getBean();
            final NewScreenFromTemplateWindow screenWindow = new NewScreenFromTemplateWindow();
            screenWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  screenWindow.hide();
                  ScreenPairRef screenRef = null;
                  if (be.getData() instanceof ScreenFromTemplate) {
                     ScreenFromTemplate screenFromTemplate = be.<ScreenFromTemplate> getData();
                     ScreenPair screen = screenFromTemplate.getScreen();
                     screen.setTouchPanelDefinition(groupRef.getPanel().getTouchPanelDefinition());
                     screen.setParentGroup(groupRef.getGroup());
                     screenRef = new ScreenPairRef(screen);
                     screenRef.setTouchPanelDefinition(screen.getTouchPanelDefinition());
                     screenRef.setOid(IDUtil.nextID());
                     groupRef.getGroup().addScreenRef(screenRef);
                     screenRef.setGroup(groupRef.getGroup());
                     updatePanelTree(screenRef);
                     BeanModelDataBase.screenTable.insert(screen.getBeanModel());
                  }
               }

               private void updatePanelTree(ScreenPairRef screenRef) {
                  panelTree.getStore().add(groupRef.getBeanModel(), screenRef.getBeanModel(), false);
                  panelTree.setExpanded(groupRef.getBeanModel(), true);
                  panelTree.getSelectionModel().select(screenRef.getBeanModel(), false);
               }

            });
         }
      });
      return newScreenItem;
   }
   private MenuItem createNewScreenMenuItem() {
      MenuItem newScreenItem = new MenuItem("New Screen");
      newScreenItem.setIcon(icon.screenIcon());
      newScreenItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            BeanModel selectItem = panelTree.getSelectionModel().getSelectedItem();
            if (selectItem != null) {
               if (selectItem.getBean() instanceof ScreenPairRef) {
                  selectItem = panelTree.getStore().getParent(selectItem);
               } else if (selectItem.getBean() instanceof Panel) {
                  selectItem = panelTree.getStore().getChild(selectItem, 0);
               }
            }
            final ScreenWindow screenWindow = new ScreenWindow(selectItem);
            screenWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  screenWindow.hide();
                  ScreenPairRef screenRef = null;
                  if (be.getData() instanceof ScreenPairRef) {
                     screenRef = be.<ScreenPairRef>getData();
                     updatePanelTree(screenRef);
                     BeanModelDataBase.screenTable.insert(screenRef.getScreen().getBeanModel());
                  }
               }
               
               private void updatePanelTree(ScreenPairRef screenRef) {
                  panelTree.getStore().add(screenWindow.getSelectedGroupRefModel(), screenRef.getBeanModel(), false);
                  panelTree.setExpanded(screenWindow.getSelectedGroupRefModel(), true);
                  panelTree.getSelectionModel().select(screenRef.getBeanModel(), false);
               }

            });

         }
      });
      return newScreenItem;
   }

   public TreePanel<BeanModel> getPanelTree() {
      return panelTree;
   }

   public Button getEditButton() {
     return editButton;
   }
   
   public void setInitialized(boolean initialized) {
    this.initialized = initialized;
  }

  public boolean isInitialized() {
      return initialized;
   }
   
}
