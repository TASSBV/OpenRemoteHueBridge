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

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.form.AdapterField;

/**
 * The Class ImageSelectAdapterField is to select a image and delete a image.
 */
public class ImageSelectAdapterField extends AdapterField {

   public ImageSelectAdapterField(String fieldLabel) {
      super(new SelectAndDeleteButtonWidget());
      setFieldLabel(fieldLabel);
   }

   public void addSelectionListener(SelectionListener<ButtonEvent> selectionListener) {
      ((SelectAndDeleteButtonWidget)this.widget).addSelectionListener(selectionListener);
   }
   
   public void addDeleteListener(SelectionListener<ButtonEvent> selectionListener) {
      ((SelectAndDeleteButtonWidget)this.widget).addDeleteListener(selectionListener);
   }
   
   public void setText(String text) {
     if (text != null && !"".equals(text)) {
       ((SelectAndDeleteButtonWidget)this.widget).setText(text);
     } else {
       removeImageText();
     }
   }
   
   public void removeImageText() {
      setText("Select");
   }
}
