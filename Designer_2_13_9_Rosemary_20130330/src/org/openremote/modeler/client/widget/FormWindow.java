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
package org.openremote.modeler.client.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;

/**
 * The Window with Form.
 * 
 * @author Dan 2009-8-14
 */
public class FormWindow extends Window {
   
   /** The form. */
   protected FormPanel form = new FormPanel();
   
   /**
    * Instantiates a new form window.
    */
   public FormWindow() {
      setWindowStyles();
      setFormStyles();
      enableEnterToSubmit(true);
   }



   /**
    * Sets the form styles.
    */
   private void setFormStyles() {
      form.setFrame(true);
      form.setHeaderVisible(false);
      form.setBorders(false);
      form.setButtonAlign(HorizontalAlignment.CENTER);
   }



   /**
    * Sets the window styles.
    */
   private void setWindowStyles() {
      setLayout(new FillLayout());
      setModal(true);
      setBodyBorder(false);
   }
   




   /**
    * {@inheritDoc}
    */
   @Override
   public void show() {
      setFocusWidget(form.getWidget(0));
      super.show();
   }

   /**
    * Gets the attr map.
    * 
    * @return the attr map
    */
   public Map<String, String> getAttrMap() {
      List<Field<?>> list = form.getFields();
      Map<String, String> attrMap = new HashMap<String, String>();
      for (Field<?> field : list) {
         attrMap.put(field.getName(), field.getValue().toString());
      }
      return attrMap;
   }
   
   public void enableEnterToSubmit(final boolean enableEnter) {
      new KeyNav<ComponentEvent>(this) {
         @Override
         public void onEnter(ComponentEvent ce) {
            if (enableEnter && form.isValid()) {
               form.submit();
            } 
         }
       };

   }
}
