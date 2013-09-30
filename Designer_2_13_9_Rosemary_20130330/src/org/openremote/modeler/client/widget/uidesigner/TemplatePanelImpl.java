/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
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
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.EditDelBtnSelectionListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.model.TreeFolderBean;
import org.openremote.modeler.client.proxy.TemplateProxy;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.client.widget.buildingmodeler.TemplateCreateWindow;
import org.openremote.modeler.domain.Template;
import org.openremote.modeler.selenium.DebugId;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Template panel.
 *
 * @author Javen
 * @author <a href = "mailto:juha@openremote.org">Juha Lindfors</a>
 *
 */
public class TemplatePanelImpl extends ContentPanel implements TemplatePanel {
  
  private Presenter presenter;
  
   private TreePanel<BeanModel> templateTree = TreePanelBuilder.buildTemplateTree(this);
   
   private LayoutContainer treeContainer;
   
   private Template templateInEditing = null;
   
   private static final int AUTO_SAVE_INTERVAL_MS = 30000;

   private Timer timer;

   private Icons icon = GWT.create(Icons.class);
   
   private SelectionServiceExt<BeanModel> selectionService;
   
   private Button deleteButton;
   private Button editButton;
   
   public TemplatePanelImpl() {
      selectionService = new SelectionServiceExt<BeanModel>();
      setExpanded(false);
      setHeading("Template");
      setIcon(icon.templateIcon());
      setLayout(new FitLayout());
      createMenu();
//      createTreeContainer();
      createAutoSaveTimer();
   }

   /**
    * Creates the menu.
    */
   private void createMenu() {
      ToolBar toolBar = new ToolBar();
      toolBar.add(createNewTemplateMenuItem());

      editButton = createEditTemplateMenuItem();
      editButton.setEnabled(false);
      
      deleteButton = createDeleteBtn();
      deleteButton.setEnabled(false); 
      
      List<Button> editDelBtns = new ArrayList<Button>();
      editDelBtns.add(editButton);
      editDelBtns.add(deleteButton);
      
      selectionService.addListener(new EditDelBtnSelectionListener(editDelBtns) {
         @Override
         protected boolean isEditableAndDeletable(List<BeanModel> sels) {
            BeanModel selectModel = sels.get(0);
            if (selectModel != null && selectModel.getBean() instanceof Template) {
               return true;
            }
            return false;
         }
      });
      
      toolBar.add(editButton);
      toolBar.add(deleteButton);
      setTopComponent(toolBar);
   }


   /**
    * Creates the delete button.
    * 
    * @return the button
    */
   private Button createDeleteBtn() {
      Button deleteBtn = new Button("Delete");
      deleteBtn.setIcon(icon.delete());
      return deleteBtn;
   }

   private Button createEditTemplateMenuItem() {
      Button editTemplateMenuItem = new Button("Edit");
      editTemplateMenuItem.setIcon(icon.edit());
      return editTemplateMenuItem;
   }
   
   private Button createNewTemplateMenuItem() {
      Button newTemplateMenuItem = new Button("New");
      newTemplateMenuItem.setIcon(icon.add());
      newTemplateMenuItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            final BeanModel privateTemplateTopNode = templateTree.getStore().getChild(0);
            final BeanModel publicTemplateTopNode = templateTree.getStore().getChild(1);
            BeanModel selectedModel = templateTree.getSelectionModel().getSelectedItem();
            boolean isShare = false;
            if (selectedModel != null) {
               if (selectedModel.getBean() instanceof TreeFolderBean && publicTemplateTopNode == selectedModel) {
                  isShare = true;
               } else if (selectedModel.getBean() instanceof Template && ((Template) selectedModel.getBean()).isShared()) {
                  isShare = true;
               }
            }
            final TemplateCreateWindow templateCreateWindow = new TemplateCreateWindow(isShare);
            templateCreateWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  Template template = be.getData();
                  if (template.isShared()) {
                     templateTree.getStore().add(publicTemplateTopNode, template.getBeanModel(),false);
                  } else {
                     templateTree.getStore().add(privateTemplateTopNode, template.getBeanModel(),false);
                  }
                  layout();
               }

            });

         }
      });
      return newTemplateMenuItem;
   }

   
   private void createTreeContainer() {
      treeContainer = new LayoutContainer() {
         @Override
         protected void onRender(Element parent, int index) {
            super.onRender(parent, index);
            add(templateTree);
         }
         
      };
      treeContainer.ensureDebugId(DebugId.DEVICE_TREE_CONTAINER);
   // overflow-auto style is for IE hack.
      treeContainer.addStyleName("overflow-auto");
      treeContainer.setStyleAttribute("backgroundColor", "white");
      treeContainer.setBorders(false);
      add(treeContainer);
      
      selectionService.addListener(new SourceSelectionChangeListenerExt(templateTree.getSelectionModel()));
      selectionService.register(templateTree.getSelectionModel());
      
   }
   
   
   private void createAutoSaveTimer() {
      timer = new Timer() {
         @Override
         public void run() {
            saveTemplateUpdates();
         }
      };
      timer.scheduleRepeating(AUTO_SAVE_INTERVAL_MS);
      
   }
   
   public void saveTemplateUpdates() {
      if (templateInEditing != null) {
         TemplateProxy.updateTemplate(templateInEditing, new AsyncCallback<Template>(){

            @Override
            public void onFailure(Throwable caught) {
               Info.display("Error","Update template: "+templateInEditing.getName()+" failed");
            }

            @Override
            public void onSuccess(Template result) {
               if (result != null && result.getOid() == templateInEditing.getOid()) {
                  templateInEditing.setContent(result.getContent());
                  Info.display("Success", "Save template " + templateInEditing.getName()+" successfully !");
               }
               
            }
            
         });
      }
   }

   public Template getTemplateInEditing() {
      return templateInEditing;
   }

  public void setTemplateInEditing(Template templateInEditing) {
     this.templateInEditing = templateInEditing;
   }
   
  @Override
   protected void onExpand() {
      if (treeContainer == null) {
        createTreeContainer();
      }
     super.onExpand();
   }

  public SelectionServiceExt<BeanModel> getSelectionService() {
    return selectionService;
  }

  public Button getDeleteButton() {
    return deleteButton;
  }

  public Button getEditButton() {
    return editButton;
  }

  public TreePanel<BeanModel> getTemplateTree() {
    return templateTree;
  }

  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  public void templateClicked(Template template) {
    if (presenter != null) {
      presenter.setTemplateInEditing(template);
    }
  }
}
