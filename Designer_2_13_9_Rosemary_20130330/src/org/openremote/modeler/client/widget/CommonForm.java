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

import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.selenium.DebugId;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;

/**
 * Common Form with common style and submit/reset buttons.
 * 
 * @author Dan 2009-8-19
 */
public class CommonForm extends FormPanel {
   
   
   /**
    * Instantiates a new common form with submit/reset button..
    * 
    */
   public CommonForm() {
      super();
      setFrame(true);
      setHeaderVisible(false);
      setBorders(false);
      setButtonAlign(HorizontalAlignment.CENTER);
      if (!isNoButton()) {
         addButtons();
      }
   }

   /**
    * Gets the attr map.
    * 
    * @return the attr map
    */
   public Map<String, String> getFieldMap() {
      List<Field<?>> list = getFields();
      Map<String, String> attrMap = new HashMap<String, String>();
      for (Field<?> field : list) {
         attrMap.put(field.getName(), field.getValue().toString());
      }
      return attrMap;
   }
   
   /**
    * Adds the buttons. By default Submit and Reset button are added. 
    *
    */
   protected void addButtons() {
      Button submitBtn = new Button("Submit");
      submitBtn.ensureDebugId(DebugId.COMMON_SUBMIT_BTN);
      Button resetButton = new Button("Reset");
      submitBtn.addSelectionListener(new FormSubmitListener(this, submitBtn));
      resetButton.addSelectionListener(new FormResetListener(this));
      addButton(submitBtn);
      addButton(resetButton);
   }

   /**
    * Checks if is no button.
    * 
    * @return true, if is you needn't submit/reset button. default is false.
    */
   public boolean isNoButton() {
      return false;
   }
   
   
   
}
