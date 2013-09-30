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
package org.openremote.modeler.client.widget.buildingmodeler;

import org.openremote.modeler.client.ir.ProntoFileImportResultOverlay;
import org.openremote.modeler.client.proxy.UtilsProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.selenium.DebugId;
import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.Uniform;
import org.restlet.client.resource.ClientResource;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * The window allows user to import Infrared Command from a file.
 * 
 * 
 */
public class IRFileImportWindow extends FormWindow {

   private final IRFileImportWindow importWindow;
   private FileUploadField fileUploadField;
   private IRFileImportForm importForm;
   private FlowPanel deviceChooser = new FlowPanel();
   private VerticalPanel fileUploadPanel = new VerticalPanel();
   private LabelField errorLabel;
   private Button loadBtn;
   private Button resetBtn;
   private String prontoFileHandle;

   public IRFileImportWindow(BeanModel deviceBeanModel) {
      super();
      importWindow = this;
      setSize(800, 610);
      importForm = new IRFileImportForm(this, deviceBeanModel);
      initial("Import IR Command from file");
      this.ensureDebugId(DebugId.IMPORT_WINDOW);
      deviceChooser.add(importForm);
      deviceChooser.setLayoutData(new FillLayout());
      add(deviceChooser);
      importForm.disable();
      show();
   }
   
   /**
    * Initialize the window
    * 
    * @param heading
    */
   private void initial(String heading) {
      setHeading(heading);
      
      UtilsProxy.getIrServiceRestRootUrl(new AsyncSuccessCallback<String>() {        
        @Override
        public void onSuccess(final String result) {
          form.setAction(result + "ProntoFile");
          importForm.setIrServiceRootRestURL(result);
          
          addWindowListener(new WindowListener() {

            @Override
            public void windowHide(WindowEvent we) {
              if (prontoFileHandle != null) {
                // Clean-up imported Pronto file as we're done importing
                ClientResource clientResource = new ClientResource(result + "ProntoFile/" + prontoFileHandle);
                clientResource.setOnResponse(new Uniform() {
                  // Even if empty, the onReponse handler is required or call does not go through
                  public void handle(Request request, Response response) {
                  }
                });
                clientResource.delete();
              }
              super.windowHide(we);
            }            
          });
        }
      });
      form.setEncoding(Encoding.MULTIPART);
      form.setMethod(Method.POST);

      createFileUploadField();
      createLoadResetButton();
      errorLabel = new LabelField();
      errorLabel.setVisible(false);
      errorLabel.setStyleName("importErrorMessage");
      errorLabel.setAutoWidth(true);

      form.setWidth(800);

      form.add(fileUploadPanel);
      HorizontalPanel buttonPanel = new HorizontalPanel();
      buttonPanel.setSpacing(3);
      buttonPanel.add(loadBtn);
      buttonPanel.add(resetBtn);
      form.add(buttonPanel);
      form.add(errorLabel);
      deviceChooser.add(form);
      addListenersToForm();
   }

   /**
    * adds form listeners
    */
   private void addListenersToForm() {
      form.addListener(Events.Submit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
            importForm.hideComboBoxes();
            // We get an id back from IRService and pass that to importForm so brands can be loaded
            if (be.getResultHtml() == null) {
              reportError("Communication error");
            } else {
              ProntoFileImportResultOverlay importResult = ProntoFileImportResultOverlay.fromJSONString(be.getResultHtml());
              if (importResult.getErrorMessage() != null) {
                 reportError(importResult.getErrorMessage());
              } else {
                 errorLabel.setVisible(false);
                 importForm.setVisible(true);
                 importWindow.unmask();
                 importForm.enable();               
                 prontoFileHandle = importResult.getResult();
                 importForm.setProntoFileHandle(prontoFileHandle);               
                 importForm.showBrands();
              }
            }
         }
      });
   }

   /**
    * report errors in a label
    * 
    * @param errorMessage
    */
   private void reportError(String errorMessage) {
      unmask();
      form.clear();
      form.clearState();
      loadBtn.enable();
      setErrorMessage(errorMessage);
   }
   
   public void setErrorMessage(String errorMessage) {
     errorLabel.setText(errorMessage);
     errorLabel.setVisible(true);
   }

   /**
    * adds load and clear button
    */
   private void createLoadResetButton() {
      loadBtn = new Button("Load");
      loadBtn.ensureDebugId(DebugId.IRFILE_IMPORT_WINDOW_LOAD_BTN);
      loadBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

         @Override
         public void componentSelected(ButtonEvent ce) {
            if (form.isValid()) {
               importWindow
                     .mask("Please wait while file is loaded and processed");
               form.submit();
            }

         }
      });

      resetBtn = new Button("Clear");
      resetBtn.ensureDebugId(DebugId.IRFILE_IMPORT_WINDOW_CLEAR_BTN);
      resetBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            errorLabel.setVisible(false);
            importForm.setVisible(false);
            importForm.hideComboBoxes();
            fileUploadField.clear();
         }
      });

   }

   /**
    * creates the FileUploadField
    */
   private void createFileUploadField() {
      fileUploadField = new FileUploadField();
      fileUploadField.setName("fileToUpload");
      fileUploadField.setAllowBlank(false);
      fileUploadField.setFieldLabel("File");
      fileUploadField.setStyleAttribute("overflow", "hidden");
      fileUploadPanel.setSpacing(3);
      fileUploadPanel.add(fileUploadField);
   }

}
