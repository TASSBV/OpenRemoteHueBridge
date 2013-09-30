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
package org.openremote.modeler.client.listener;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.form.FormPanel;

/**
 * The listener invokes forms reset event.
 * 
 * @author Dan 2009-8-21
 */
public class FormResetListener extends SelectionListener<ButtonEvent> {
   
   /** The form. */
   private FormPanel form;
   
   
   /**
    * Instantiates a new form reset listener.
    * 
    * @param form
    *           the form
    */
   public FormResetListener(FormPanel form) {
      super();
      this.form = form;
   }



   /* (non-Javadoc)
    * @see com.extjs.gxt.ui.client.event.SelectionListener#componentSelected(com.extjs.gxt.ui.client.event.ComponentEvent)
    */
   @Override
   public void componentSelected(ButtonEvent ce) {
      if (form.isValid()) {
         form.reset();
      }
   }

}
