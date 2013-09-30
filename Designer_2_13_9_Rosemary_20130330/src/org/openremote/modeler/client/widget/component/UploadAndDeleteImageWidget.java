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
import org.openremote.modeler.client.widget.ImageUploadField;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.core.client.GWT;

/**
 * The widget is to upload or delete a image for the screen component.
 */
public class UploadAndDeleteImageWidget extends LayoutContainer {

   private ImageUploadField uploadFiled;
   private Button deleteButton = new Button();
   public UploadAndDeleteImageWidget(String fieldName) {
      this.uploadFiled = new ImageUploadField(fieldName);
      uploadFiled.setWidth(105);
      uploadFiled.setStyleAttribute("float", "left");
      uploadFiled.setStyleAttribute("marginRight", "20");
      deleteButton.setIcon(((Icons) GWT.create(Icons.class)).delete());
      deleteButton.setStyleAttribute("float", "left");
      add(uploadFiled);
      add(deleteButton);
   }
   
   public void addUploadListener(EventType eventType,Listener<FieldEvent> uploadListener) {
      this.uploadFiled.addListener(eventType, uploadListener);
   }
   
   public void addDeleteListener(SelectionListener<ButtonEvent> selectionListener) {
      this.deleteButton.addSelectionListener(selectionListener);
   }
   
   public void setActionToForm(FormPanel form) {
      this.uploadFiled.setActionToForm(form);
   }
   
   public void setImage(String imageUrl) {
      if (imageUrl != null && !"".equals(imageUrl)) {
         this.uploadFiled.setValue(imageUrl);
      }
   }
}
