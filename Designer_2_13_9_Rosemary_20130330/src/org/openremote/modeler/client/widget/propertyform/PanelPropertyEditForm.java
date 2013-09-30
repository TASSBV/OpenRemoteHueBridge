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
package org.openremote.modeler.client.widget.propertyform;

import org.openremote.modeler.client.widget.component.PanelPropertyEditable;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * A panel for editing panel's name. 
 */
public class PanelPropertyEditForm extends PropertyForm {
   private PanelPropertyEditable editor = null;

   public PanelPropertyEditForm(PanelPropertyEditable editor) {
      super(editor);
      this.editor = editor;
      addFields();
      show();
   }

   private void addFields() {
      // initial name field.
      final TextField<String> name = new TextField<String>();
      name.setFieldLabel("Name");
      name.setValue(editor.getName());
      name.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            if (name.getValue() != null && name.getValue().trim().length() > 0) {
               editor.setName(name.getValue());
            } else {
               MessageBox.alert("Error", "Empty name is not allowed!", null);
               name.setValue(editor.getName());
            }
         }
      });

      add(name);
   }
}
