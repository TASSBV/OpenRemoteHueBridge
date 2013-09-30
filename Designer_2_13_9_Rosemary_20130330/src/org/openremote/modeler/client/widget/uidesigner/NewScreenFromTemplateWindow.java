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

import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.proxy.TemplateProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.utils.ScreenFromTemplate;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.Template;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
/**
 * A wizard for creating a new screen from existing groups.
 *
 * @author Javen
 *
 */
public class NewScreenFromTemplateWindow extends FormWindow {
   public static final int MAX_TEMPLATES_SIZE_PER_PAGE = 10;
   
   private String currentKeywords = "";
   
   private int currentPage = 0;

   private ScreenPair screen = null;

   private TextField<String> nameField = null;

   private Radio shareToAllRadio = new Radio();
   
   private Radio shareNoneRadio = new Radio();
   
   private ListView<BeanModel> templateView = null;
   
   private TextField<String> keywordsField = new TextField<String> ();
   
   private ShareRadioChangeListener shareRadioChangeListener = new ShareRadioChangeListener();
   
   
   private Button previousPage = new Button();
   private Button nextPage = new Button();
   
   private Button submitBtn;

   public NewScreenFromTemplateWindow() {
      super();
      setSize(400, 450);
      setHeading("New Screen From Template");
      setLayout(new FillLayout());
      setModal(true);
      createFormButtons();
      createFields();
      createTemplateView();
      setBodyBorder(false);
      add(form);
      show();
   }

   public void createFields() {
      form.setHeaderVisible(false);
      form.setBorders(false);
      form.setBodyBorder(true);
      form.setLabelWidth(60);
      form.setFieldWidth(246);
      nameField = new TextField<String>();
      nameField.setAllowBlank(false);
      nameField.setFieldLabel("Name");
      nameField.setName("name");
      form.add(nameField);
      addBeforHideListener();
   }

   private void createFormButtons() {
      previousPage.setText("< Previous");
      previousPage.addSelectionListener(new PageListener());
      previousPage.setEnabled(false);
      nextPage.setText("Next >");
      nextPage.addSelectionListener(new PageListener());
      nextPage.setEnabled(false);
      
      submitBtn = new Button("Submit");
      Button resetBtn = new Button("Reset");
      submitBtn.addSelectionListener(new FormSubmitListener(form, submitBtn));
      resetBtn.addSelectionListener(new FormResetListener(form));

      form.addButton(submitBtn);
      form.addButton(resetBtn);
   }

   private void addBeforHideListener() {
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         @Override
         public void handleEvent(FormEvent be) {
            buildScreenFromTemplate(be);
         }
      });
   }

   private void buildScreenFromTemplate(FormEvent be) {
      BeanModel templateBeanModel = templateView.getSelectionModel().getSelectedItem();
      if (templateBeanModel == null) {
         MessageBox.alert("Error", "Please select a template.", null);
         submitBtn.enable();
         be.cancelBubble();
      } else {
         NewScreenFromTemplateWindow.this.mask("Downloading resources for this template... ");
         Template template = templateBeanModel.getBean();
         TemplateProxy.buildScreenFromTemplate(template, new AsyncSuccessCallback<ScreenFromTemplate>() {

            @Override
            public void onSuccess(ScreenFromTemplate result) {
               NewScreenFromTemplateWindow.this.unmask();
               screen = result.getScreen();
               screen.setOid(IDUtil.nextID());
               screen.setName(nameField.getValue());
               fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(result));
            }

            @Override
            public void onFailure(Throwable caught) {
               MessageBox
                     .alert("Error", "Failed to create screen from template: \"" + caught.getMessage() + "\"", null);
               NewScreenFromTemplateWindow.this.unmask();
               super.checkTimeout(caught);
            }

         });
      }
   }

   private void createTemplateView() {
      FieldSet templateFieldSet = new FieldSet();
      templateFieldSet.setHeading("Select from template");
      templateFieldSet.setLayout(new FormLayout());
      
      LabelField keywordsLabel = new LabelField();
      keywordsLabel.setText("Keywords");
      
      keywordsField.setFieldLabel("Keywords");
      Button searchBtn = new Button();
      searchBtn.addSelectionListener(new SearchListener());
      searchBtn.setText("Search");
      
      final ContentPanel searchContainer = new ContentPanel();
      searchContainer.setScrollMode(Scroll.NONE);
      searchContainer.setHeaderVisible(false);
      FormLayout searchContainerLayout = new FormLayout();
      searchContainerLayout.setLabelAlign(LabelAlign.LEFT);
      searchContainerLayout.setLabelWidth(80);
      searchContainerLayout.setDefaultWidth(208);
      searchContainer.setLayout(searchContainerLayout);
      searchContainer.add(keywordsField);
      searchContainer.addButton(searchBtn);
      searchContainer.hide();
      
      
      ContentPanel templateSelectContainer = new ContentPanel();
      templateSelectContainer.setHeaderVisible(false);
      FormLayout templateLayout = new FormLayout();
      templateLayout.setLabelAlign(LabelAlign.LEFT);
      templateLayout.setLabelWidth(80);
      templateLayout.setDefaultWidth(215);
      templateSelectContainer.setLayout(templateLayout);
      
      buildTemplateList();
      
      
      
      RadioGroup shareRadioGroup = new RadioGroup();

      shareNoneRadio.setBoxLabel("Private");
      shareNoneRadio.setValue(true);
      shareNoneRadio.addListener(Events.Change,new ShareRadioChangeListener(){

         @Override
         public void handleEvent(FieldEvent be) {
            super.handleEvent(be);
            boolean showPrivate = shareNoneRadio.getValue();
            if (showPrivate) {
               searchContainer.hide();
            } else {
               searchContainer.show();
            }
         }
         
      });

      shareToAllRadio.setName("Public");
      shareToAllRadio.setBoxLabel("Public");
      shareRadioGroup.setFieldLabel("From:");
      shareRadioGroup.add(shareNoneRadio);
      shareRadioGroup.add(shareToAllRadio);
      
      
      
      shareRadioGroup.setFieldLabel("From");
      shareRadioGroup.setTitle("From");
      templateFieldSet.add(shareRadioGroup);
      templateFieldSet.add(searchContainer);
      templateSelectContainer.add(templateView);
      
      templateSelectContainer.addButton(previousPage);
      templateSelectContainer.addButton(nextPage);
      templateFieldSet.add(templateSelectContainer);
      templateFieldSet.setSize(330, 300);
      form.add(templateFieldSet);
   }

   private void buildTemplateList() {
      templateView = new ListView<BeanModel>();
      templateView.setStateful(true);
      templateView.setBorders(false);
      templateView.setHeight("150px");
      templateView.setWidth("300px");
      templateView.setDisplayProperty("displayName");
      ListStore<BeanModel> store = new ListStore<BeanModel>();
      templateView.setStore(store);
      initTemplateView(true);
   }

   private void searchTemplates(final String keywords,final int page) {
      TemplateProxy.searchTemplates(keywords, page, new AsyncCallback<List<Template>>() {

         @Override
         public void onFailure(Throwable caught) {
            templateView.unmask();
         }

         @Override
         public void onSuccess(List<Template> result) {
            currentKeywords = keywords;
            nextPage.setEnabled(result.size() == MAX_TEMPLATES_SIZE_PER_PAGE);
            previousPage.setEnabled(currentPage > 0);
            templateView.unmask();
            shareNoneRadio.removeListener(Events.Change, shareRadioChangeListener);
            shareNoneRadio.setValue(false);
            shareToAllRadio.setValue(true);
            templateView.getStore().removeAll();
            templateView.getStore().add(Template.createModels(result));
            shareNoneRadio.addListener(Events.Change, shareRadioChangeListener);
         }
         
      });
      templateView.mask("searching ... ");
   }
   private void initTemplateView(boolean fromPrivate) {
      templateView.mask("initializing templates... ");
      if (fromPrivate) {
         TemplateProxy.getTemplates(true, new AsyncCallback<List<Template>>() {
   
            @Override
            public void onFailure(Throwable caught) {
               templateView.unmask();
               templateView.getStore().removeAll();
            }
   
            @Override
            public void onSuccess(List<Template> result) {
               templateView.unmask();
               templateView.getStore().removeAll();
               templateView.getStore().add(Template.createModels(result));
            }
   
         });
      } else {
         searchTemplates("",0);
      }
   }
   
   class SearchListener extends SelectionListener<ButtonEvent> {
      @Override
      public void componentSelected(ButtonEvent ce) {
         if (keywordsField.getValue() == null || keywordsField.getValue().trim().length()==0) {
           //search public templates without caring keywords.  
           searchTemplates(null,0); 
         } else {
            searchTemplates(keywordsField.getValue(),0);
         }
         currentPage = 0;
      }
   }
   
   class PageListener extends SelectionListener<ButtonEvent> {

      @Override
      public void componentSelected(ButtonEvent ce) {
         Button btn = ce.getComponent();
         if (btn.equals(previousPage)&& currentPage > 0) {
            searchTemplates(currentKeywords, --currentPage);
         } else if (btn.equals(nextPage) ) {
            searchTemplates(currentKeywords, ++currentPage);
         }
      }
      
   }
   
   class ShareRadioChangeListener implements Listener<FieldEvent> {

      @Override
      public void handleEvent(FieldEvent be) {
         if (be.getSource() instanceof Radio && be.getSource().equals(shareNoneRadio)) {
            Boolean showPrivate = (Boolean) be.getValue();
            initTemplateView(showPrivate);
            nextPage.setEnabled(false);
            previousPage.setEnabled(false);
         }
      }
      
   }
}
