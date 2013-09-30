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

import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.shared.dto.DTOHelper;
import org.openremote.modeler.shared.dto.MacroItemDetailsDTO;
import org.openremote.modeler.shared.dto.MacroItemType;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;

/**
 * Creates a delay command for the macro.
 */
public class DelayWindow extends FormWindow {

   /** The Constant DELAY. */
   public static final String DELAY = "delay";
   
   MacroItemDetailsDTO macroItem;

   /**
    * Instantiates a new delay window.
    */
   public DelayWindow() {
      super();
      initial("Add Delay");
      macroItem = new MacroItemDetailsDTO(); // Important that this is after initial call, as macroItem == null means field is not populated
      macroItem.setType(MacroItemType.Delay);
   }
   
   /**
    * Instantiates a new delay window.
    * 
    * @param commandDelayModel the command delay model
    */
   public DelayWindow(MacroItemDetailsDTO macroItem) {
      super();
      this.macroItem = macroItem;
      initial("Edit Delay");
   }
   
   /**
    * Initial.
    * 
    * @param heading the heading
    */
   private void initial(String heading) {
      setHeading(heading);
      setSize(280, 140);

      Button addBtn = new Button("OK");      
      addBtn.addSelectionListener(new FormSubmitListener(form, addBtn));
      form.addButton(addBtn);
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
            List<Field<?>> list = form.getFields();
            String delay = list.get(0).getValue().toString();
            macroItem.setDelay(Integer.parseInt(delay));
            fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(DTOHelper.getBeanModel(macroItem)));
         }         
      });
      createField();
      add(form);
   }
   
   /**
    * Creates the field.
    */
   private void createField() {
      NumberField delayField = new NumberField();
      delayField.setName(DELAY);
      delayField.setFieldLabel("Delay(ms)");
      delayField.setAllowBlank(false);
      delayField.setAutoWidth(true);
      delayField.setRegex("^[1-9][0-9]*$");
      delayField.getMessages().setRegexText("The delay must be a positive integer");
      if (macroItem != null) {
         delayField.setValue(macroItem.getDelay());
      }
      form.add(delayField);
   }
}
