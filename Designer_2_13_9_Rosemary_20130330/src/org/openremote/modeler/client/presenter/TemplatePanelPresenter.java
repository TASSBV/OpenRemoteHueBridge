/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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

import java.util.List;

import org.openremote.modeler.client.event.ScreenSelectedEvent;
import org.openremote.modeler.client.event.ScreenSelectedEventHandler;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.event.TemplateSelectedEvent;
import org.openremote.modeler.client.event.TemplateSelectedEventHandler;
import org.openremote.modeler.client.listener.ConfirmDeleteListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.TemplateProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.buildingmodeler.TemplateCreateWindow;
import org.openremote.modeler.client.widget.uidesigner.TemplatePanel;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.Template;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TemplatePanelPresenter implements Presenter, org.openremote.modeler.client.widget.uidesigner.TemplatePanel.Presenter {

  private EventBus eventBus;
  private TemplatePanel view;
  
  public TemplatePanelPresenter(EventBus eventBus, TemplatePanel view) {
    super();
    this.eventBus = eventBus;
    this.view = view;
    view.setPresenter(this);
    bind();
  }

  private void bind() {
    this.view.getSelectionService().addListener(new SelectionChangedListener<BeanModel>() {
      @Override
      public void selectionChanged(SelectionChangedEvent<BeanModel> se) {
        List<BeanModel> selection = se.getSelection();
        if (selection.size() == 1) {
          BeanModel selectModel = selection.get(0);
          if (selectModel.getBean() instanceof Template) {
            Template template = selectModel.getBean();
            TemplatePanelPresenter.this.setTemplateInEditing(template);
          }
        }
      }      
    });
   
    // Listen to template selection event just to know when no template is selected anymore
    // If template is selected, above code is called or setTemplateInEditing method is called
    eventBus.addHandler(TemplateSelectedEvent.TYPE, new TemplateSelectedEventHandler() {
      @Override
      public void onTemplateSelected(TemplateSelectedEvent event) {
        if (event.getTemplate() == null) {
          view.setTemplateInEditing(null);
        }
      }
    });
    // When a screen is selected, consider we're not editing any template
    eventBus.addHandler(ScreenSelectedEvent.TYPE, new ScreenSelectedEventHandler() {
      @Override
      public void onScreenSelected(ScreenSelectedEvent event) {
        view.setTemplateInEditing(null);
      }
    });
    
    this.view.getDeleteButton().addSelectionListener(new ConfirmDeleteListener<ButtonEvent>() {
      final TreePanel<BeanModel> templateTree = view.getTemplateTree();
      @Override
      public void onDelete(ButtonEvent ce) {
         List<BeanModel> templateBeanModels = templateTree.getSelectionModel().getSelectedItems();
         if (templateBeanModels == null || templateBeanModels.size() == 0) {
            MessageBox.alert("Error", "Please select a template.", null);
            ce.cancelBubble();
         } else {
            for (final BeanModel templateBeanModel : templateBeanModels) {
               Template template = templateBeanModel.getBean();
               Long oid = template.getOid();
               TemplateProxy.deleteTemplateById(oid, new AsyncSuccessCallback<Boolean>() {
                  @Override
                  public void onSuccess(Boolean success) {
                     if (success) {
                        templateTree.getStore().remove(templateBeanModel);
                        // Ensures a screen does not disappear if it was displayed in ScreenPanel
                        if (view.getTemplateInEditing() != null) {
                          eventBus.fireEvent(new TemplateSelectedEvent(null));
                          view.setTemplateInEditing(null);
                        }
                        Info.display("Delete Template", "Template deleted successfully.");
                     }
                  }

                  @Override
                  public void onFailure(Throwable caught) {
                     MessageBox.alert("Error", "Failed to delete template :\"" + caught.getMessage() + "\"", null);
                     super.checkTimeout(caught);
                  }

               });

            }
         }
      }
   });

    this.view.getEditButton().addSelectionListener(new SelectionListener<ButtonEvent>() {
      final TreePanel<BeanModel> templateTree = view.getTemplateTree();
      public void componentSelected(ButtonEvent ce) {
        BeanModel selectedBean = templateTree.getSelectionModel().getSelectedItem();
        if( selectedBean == null || !(selectedBean.getBean() instanceof Template)) {
          MessageBox.alert("Warn","A template must be selected!",null);
          return;
        }
        //remember the share type information before being updated. 

        final BeanModel privateTemplateTopNode = templateTree.getStore().getChild(0);
        final BeanModel publicTemplateTopNode = templateTree.getStore().getChild(1);
        final Template template = selectedBean.getBean();
        final boolean shareType = template.isShared();

        final TemplateCreateWindow templateCreateWindow = new TemplateCreateWindow(template);

        templateCreateWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
          @Override
          public void afterSubmit(SubmitEvent be) {
            Template t = be.getData();
            template.setContent(t.getContent());
            template.setScreen(t.getScreen());
            template.setKeywords(t.getKeywords());
            template.setShared(t.isShared());
            if (t.isShared() == shareType) {
              templateTree.getStore().update(template.getBeanModel());
            } else {
              templateTree.getStore().remove(template.getBeanModel());
              BeanModel parentNode = template.isShared()?publicTemplateTopNode:privateTemplateTopNode;
              templateTree.getStore().add(parentNode, template.getBeanModel(),false);
            }
            eventBus.fireEvent(new TemplateSelectedEvent(template));
          }
        });
      }
    }); 
  }

  // TODO EBR : method moved from TemplatePanel was synchronized. Is this required here?
  public synchronized void setTemplateInEditing(final Template templateInEditing) {
    if (templateInEditing != null && view.getTemplateInEditing() != null ) {
       if (templateInEditing.getOid() == view.getTemplateInEditing().getOid()) return;
    }
    if (view.getTemplateInEditing() != null) {
       // 1, save previous template.
       view.mask("Saving previous template.....");
       TemplateProxy.updateTemplate(view.getTemplateInEditing(), new AsyncCallback<Template>() {

          @Override
          public void onFailure(Throwable caught) {
             view.unmask();
             Info.display("Error", "Update template: " + view.getTemplateInEditing().getName() + " failed");
             buildScreen(templateInEditing);
          }

          @Override
          public void onSuccess(Template result) {
             // 2, make sure the content for the previous template be updated. 
             if (result.getOid() == view.getTemplateInEditing().getOid()){
                view.getTemplateInEditing().setContent(result.getContent());
                Info.display("Success", "Save template " + view.getTemplateInEditing().getName() + " successfully !");
             }
             view.mask("Building screen and downloading resources ...");
             // 3, edit another template.
             buildScreen(templateInEditing);
          }
       });
    } else {
       view.mask("Building screen and downloading resources ...");
       buildScreen(templateInEditing);
    }
 }
 
 private void buildScreen(final Template templateInEditing) {
    if (templateInEditing.getScreen() != null) {
       view.unmask();
       view.setTemplateInEditing(templateInEditing);
       eventBus.fireEvent(new TemplateSelectedEvent(templateInEditing));
       return;
    }
    
    TemplateProxy.buildScreen(templateInEditing, new AsyncCallback<ScreenPair>() {

       @Override
       public void onFailure(Throwable caught) {
          MessageBox.alert("Error", "Failed to preview Template: " + templateInEditing.getName(), null);
          view.unmask();
       }

       @Override
       public void onSuccess(ScreenPair screen) {
          view.unmask();
          templateInEditing.setScreen(screen);
          view.setTemplateInEditing(templateInEditing);
          eventBus.fireEvent(new TemplateSelectedEvent(templateInEditing));
       }

    });
 }
 }
