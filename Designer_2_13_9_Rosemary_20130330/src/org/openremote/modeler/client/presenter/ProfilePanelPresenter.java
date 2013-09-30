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
package org.openremote.modeler.client.presenter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.event.ScreenSelectedEvent;
import org.openremote.modeler.client.event.ScreenTableLoadedEvent;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.event.UIElementEditedEvent;
import org.openremote.modeler.client.event.UIElementEditedEventHandler;
import org.openremote.modeler.client.event.UIElementSelectedEvent;
import org.openremote.modeler.client.listener.PanelTreeStoreChangeListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.UtilsProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.widget.uidesigner.CustomPanelWindow;
import org.openremote.modeler.client.widget.uidesigner.GroupEditWindow;
import org.openremote.modeler.client.widget.uidesigner.PanelWindow;
import org.openremote.modeler.client.widget.uidesigner.ProfilePanel;
import org.openremote.modeler.client.widget.uidesigner.ScreenWindow;
import org.openremote.modeler.domain.BusinessEntity;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPairRef;
import org.openremote.modeler.exception.UIRestoreException;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.event.shared.EventBus;

public class ProfilePanelPresenter implements Presenter {
  
  private EventBus eventBus;
  private ProfilePanel view;
  
  public ProfilePanelPresenter(EventBus eventBus, ProfilePanel view) {
    super();
    this.eventBus = eventBus;
    this.view = view;
    
    bind();
  }

  private void bind() {
    final TreePanel<BeanModel> panelTree = this.view.getPanelTree();
    
    panelTree.addListener(Events.Render, new Listener<TreePanelEvent<ModelData>>() {
      public void handleEvent(TreePanelEvent<ModelData> be) {
        initTreeWithAutoSavedPanels();
      }
    });
    
    panelTree.addListener(Events.OnClick, new Listener<TreePanelEvent<ModelData>>() {
      public void handleEvent(TreePanelEvent<ModelData> be) {
        
        BeanModel beanModel = panelTree.getSelectionModel().getSelectedItem();
        if (beanModel != null && beanModel.getBean() instanceof ScreenPairRef) {
          eventBus.fireEvent(new ScreenSelectedEvent((ScreenPairRef) beanModel.getBean()));
        }

        if (beanModel != null) {
          // TODO EBR 
          // Above event is not required, ScreenPanelPresenter can listen on UIElementSelectedEvent
          // and act only if bean in ScreenPairRef
          // Might not be a bad thing that the 2 different events are fired, limit the
          // amount of events the ScreenPanelPresenter will see
          eventBus.fireEvent(new UIElementSelectedEvent(beanModel));
        }
      };
    });
    
    panelTree.addListener(Events.OnDoubleClick, new Listener<TreePanelEvent<ModelData>>() {
      @Override
      public void handleEvent(TreePanelEvent<ModelData> be) {
        editSelectedModel();
      }        
    });
    
    this.view.getEditButton().addSelectionListener(new SelectionListener<ButtonEvent>() {
       @Override
       public void componentSelected(ButtonEvent ce) {
          editSelectedModel();
       }
    });
    
    // TODO EBR : seems not required for screen but required for panel / group
    // investigate what triggers the update for screen
    eventBus.addHandler(UIElementEditedEvent.TYPE, new UIElementEditedEventHandler() {      
      @Override
      public void onElementEdited(UIElementEditedEvent event) {
        BusinessEntity bean = event.getElement();
        if (bean instanceof Panel || bean instanceof GroupRef || bean instanceof ScreenPairRef) {
          panelTree.getStore().update(bean.getBeanModel());
        }
      }
    });
  }
  
  /**
   * 
   */
  private void editSelectedModel() {
     BeanModel selectedModel = this.view.getPanelTree().getSelectionModel().getSelectedItem();
     if (selectedModel != null) {
        if (selectedModel.getBean() instanceof Panel) {
           editPanel(selectedModel);
        } else if (selectedModel.getBean() instanceof GroupRef) {
           editGroup(selectedModel);
        } else if (selectedModel.getBean() instanceof ScreenPairRef) {
           editScreen(selectedModel);
        }
     }
  }
  
  /**
   * Edits the group.
   * 
   * @param panelBeanModel
   *           the group bean model
   */
  private void editPanel(final BeanModel panelBeanModel) {
    final TreePanel<BeanModel> panelTree = this.view.getPanelTree();
     Panel panel = panelBeanModel.getBean();
     if (Constants.CUSTOM_PANEL.equals(panel.getType())) {
        final CustomPanelWindow editCustomPanelWindow = new CustomPanelWindow(panel);
        editCustomPanelWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
           @Override
           public void afterSubmit(SubmitEvent be) {
              editCustomPanelWindow.hide();
              Panel panel = be.<Panel> getData();
              panelTree.getStore().update(panel.getBeanModel());
              for (GroupRef groupRef : panel.getGroupRefs()) {
                 for (ScreenPairRef screenRef : groupRef.getGroup().getScreenRefs()) {
                    ScreenPair screenPair = screenRef.getScreen();
                    screenPair.setTouchPanelDefinition(panel.getTouchPanelDefinition());
                    BeanModelDataBase.screenTable.update(screenPair.getBeanModel());
                 }
              }
              Info.display("Info", "Edit panel " + panel.getName() + " success.");
           }
        });
     } else {
        final PanelWindow panelWindow = new PanelWindow(panelBeanModel);
        panelWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
           @Override
           public void afterSubmit(SubmitEvent be) {
              panelWindow.hide();
              Panel panel = be.<Panel> getData();
              panelTree.getStore().update(panel.getBeanModel());
              Info.display("Info", "Edit panel " + panel.getName() + " success.");
              eventBus.fireEvent(new UIElementSelectedEvent(panelBeanModel));
           }
        });
     }
  }
  
  private void editGroup(final BeanModel groupRefBeanModel) {
    final TreePanel<BeanModel> panelTree = this.view.getPanelTree();
     final GroupEditWindow groupEditWindow = new GroupEditWindow(groupRefBeanModel);
     groupEditWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
        @Override
        public void afterSubmit(SubmitEvent be) {
           groupEditWindow.hide();
           BeanModel groupRefModel = be.getData();
           GroupRef groupRef = groupRefModel.getBean();
           panelTree.getStore().removeAll(groupRefModel);
           for (ScreenPairRef screenRef : groupRef.getGroup().getScreenRefs()) {
              if (screenRef.getScreen().getRefCount() > 1) {
                 BeanModelDataBase.screenTable.update(screenRef.getScreen().getBeanModel());
              }
              panelTree.getStore().add(groupRefModel, screenRef.getBeanModel(), false);
           }
           panelTree.getStore().update(groupRefModel);
           BeanModelDataBase.groupTable.update(groupRef.getGroup().getBeanModel());
           panelTree.setExpanded(groupRefModel, true);
           panelTree.getSelectionModel().select(groupRefModel, false);
           BeanModelDataBase.screenTable.clearUnuseData();
           Info.display("Info", "Edit Group " + groupRef.getGroup().getName() + " success.");
           eventBus.fireEvent(new UIElementSelectedEvent(groupRefBeanModel));
        }
     });
  }

  private void editScreen(final BeanModel screenRefBeanModel) {
    final TreePanel<BeanModel> panelTree = this.view.getPanelTree();
     final ScreenWindow screenWizard = new ScreenWindow(screenRefBeanModel, ScreenWindow.Operation.EDIT);
     screenWizard.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
        @Override
        public void afterSubmit(SubmitEvent be) {
           screenWizard.hide();
           ScreenPairRef screenRef = be.<ScreenPairRef> getData();
           panelTree.getStore().update(screenRef.getBeanModel());
           BeanModelDataBase.screenTable.update(screenRef.getScreen().getBeanModel());
           Info.display("Info", "Edit screen " + screenRef.getScreen().getName() + " success.");           
           eventBus.fireEvent(new UIElementSelectedEvent(screenRefBeanModel));
        }        
     });
  }

  private void initTreeWithAutoSavedPanels() {
    final TreePanel<BeanModel> panelTree = this.view.getPanelTree();

    UtilsProxy.loadPanelsFromSession(new AsyncSuccessCallback<Collection<Panel>>() {
       @Override
       public void onSuccess(Collection<Panel> panels) {
          if (panels.size() > 0) {
             initModelDataBase(panels);
             panelTree.getStore().removeAll();
             new PanelTreeStoreChangeListener(panelTree);
             for (Panel panel : panels) {
                BeanModel panelBeanModel = panel.getBeanModel();
                panelTree.getStore().add(panelBeanModel, false);
                for (GroupRef groupRef : panel.getGroupRefs()) {
                   panelTree.getStore().add(panelBeanModel, groupRef.getBeanModel(), false);
                   for (ScreenPairRef screenRef : groupRef.getGroup().getScreenRefs()) {
                      panelTree.getStore().add(groupRef.getBeanModel(), screenRef.getBeanModel(), false);
                   }
                }
             }
             panelTree.expandAll();
             eventBus.fireEvent(new ScreenTableLoadedEvent());
          } else {
             panelTree.unmask();
          }
          UtilsProxy.loadMaxID(new AsyncSuccessCallback<Long>() {
             @Override
             public void onSuccess(Long maxID) {
                if (maxID > 0) {              // set the layout component's max id after refresh page.
                   IDUtil.setCurrentID(maxID.longValue());
                }
                ProfilePanelPresenter.this.view.setInitialized(true);
             }
             
          });
       }
       @Override
       public void onFailure(Throwable caught) {
          if (caught instanceof UIRestoreException) {
            ProfilePanelPresenter.this.view.setInitialized(true);
          }
          panelTree.unmask();
          super.onFailure(caught);
          super.checkTimeout(caught);
       }

       private void initModelDataBase(Collection<Panel> panels) {
          BeanModelDataBase.panelTable.clear();
          BeanModelDataBase.groupTable.clear();
          BeanModelDataBase.screenTable.clear();
          Set<Group> groups = new LinkedHashSet<Group>();
          Set<ScreenPair> screens = new LinkedHashSet<ScreenPair>();
          for (Panel panel : panels) {
             List<GroupRef> groupRefs = panel.getGroupRefs();
             for (GroupRef groupRef : groupRefs) {
                groups.add(groupRef.getGroup());
             }
             BeanModelDataBase.panelTable.insert(panel.getBeanModel());
          }
          
          for (Group group : groups) {
             List<ScreenPairRef> screenRefs = group.getScreenRefs();
             for (ScreenPairRef screenRef : screenRefs) {
                screens.add(screenRef.getScreen());
                BeanModelDataBase.screenTable.insert(screenRef.getScreen().getBeanModel());
             }
             BeanModelDataBase.groupTable.insert(group.getBeanModel());
          }
       }
    });
    
 }
}
