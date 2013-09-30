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

import org.openremote.modeler.client.event.ResponseJSONEvent;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.selenium.DebugId;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.google.gwt.core.client.GWT;


/**
 * The Class ImportWindow.
 * 
 * @author handy.wang
 */
public class ImportZipWindow extends FormWindow {
   
   /**
    * Instantiates a new import window.
    */
   public ImportZipWindow() {
      super();
      initial("Import");
      this.ensureDebugId(DebugId.IMPORT_WINDOW);
      show();
   }

   /**
    * Initial.
    * 
    * @param heading the heading
    */
   private void initial(String heading) {
      setSize(360, 140);
      setHeading(heading);
      createFields();
      createButtons();
      form.setAction(GWT.getModuleBaseURL() + "fileUploadController.htm?method=importFile");
      form.setEncoding(Encoding.MULTIPART);
      form.setMethod(Method.POST);
      addListenersToForm();
   }

   /**
    * Creates the fields.
    */
   private void createFields() {
      FileUploadField fileUploadField = new FileUploadField();
      fileUploadField.setName("file");
      fileUploadField.setAllowBlank(false);
      fileUploadField.setFieldLabel("File");
      fileUploadField.setStyleAttribute("overflow", "hidden");
      form.add(fileUploadField);
   }

   /**
    * Creates the buttons.
    */
   private void createButtons() {
      Button importBtn = new Button("import");
      importBtn.ensureDebugId(DebugId.IMPORT_WINDOW_UPLOAD_BTN);
      Button cancelBtn = new Button("cancel");
      cancelBtn.ensureDebugId(DebugId.IMPORT_WINDOW_CANCEL_BTN);

      importBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            if (form.isValid()) {
               MessageBox box = new MessageBox();
               box.setButtons(MessageBox.YESNO);
               box.setIcon(MessageBox.QUESTION);
               box.setTitle("Import");
               box.setMessage("The activities tree will be rebuilt. Are you sure you want to import?");
               box.addCallback(new Listener<MessageBoxEvent>() {
                   public void handleEvent(MessageBoxEvent be) {
                       if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                          form.submit();
                          mask("Importing, please wait.");
                       }
                   }
               });
               box.show();       
            }
         }
      });
      final ImportZipWindow that = this;
      cancelBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            that.hide();
         }
      });
      form.addButton(importBtn);
      form.addButton(cancelBtn);
   }

   /**
    * Adds the listeners to form.
    */
   private void addListenersToForm() {
      form.addListener(Events.Submit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
            fireEvent(ResponseJSONEvent.RESPONSEJSON, new ResponseJSONEvent(be.getResultHtml()));
         }
      });
      add(form);
   }
}
