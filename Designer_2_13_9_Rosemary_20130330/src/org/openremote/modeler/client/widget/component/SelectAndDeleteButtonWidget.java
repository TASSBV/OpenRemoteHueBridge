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
package org.openremote.modeler.client.widget.component;

import org.openremote.modeler.client.icon.Icons;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.core.client.GWT;

/**
 * The Class SelectAndDeleteButtonWidget is store a select button and a delete button in a layout container.
 */
public class SelectAndDeleteButtonWidget extends LayoutContainer {

   private Button selectButton = new Button("Select");
   private Button deleteButton = new Button();
   public SelectAndDeleteButtonWidget() {
      selectButton.setWidth(125);
      selectButton.setStyleAttribute("float", "left");
      deleteButton.setIcon(((Icons) GWT.create(Icons.class)).delete());
      deleteButton.setStyleAttribute("float", "left");
      deleteButton.setStyleAttribute("paddingLeft", "3px");
      add(selectButton);
      add(deleteButton);
   }
   
   public void addSelectionListener(SelectionListener<ButtonEvent> selectionListener) {
      this.selectButton.addSelectionListener(selectionListener);
   }
   
   public void addDeleteListener(SelectionListener<ButtonEvent> selectionListener) {
      this.deleteButton.addSelectionListener(selectionListener);
   }
   
   public void setText(String text) {
      if (text.length() > 15) {
         this.selectButton.setText(text.substring(0, 15) + "...");
      } else {
         this.selectButton.setText(text);
      }
      this.selectButton.setToolTip(text);
   }
}
