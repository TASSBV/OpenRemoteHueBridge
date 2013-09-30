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

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.button.Button;

/**
 * The listener interface for receiving editDelBtnSelection events.
 * The class that is interested in processing a editDelBtnSelection
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addEditDelBtnSelectionListener</code> method. When
 * the editDelBtnSelection event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see EditDelBtnSelectionEvent
 */
public class EditDelBtnSelectionListener extends SelectionChangedListener<BeanModel> {
   
   /** The btns. */
   private List<Button> btns;
   
   /**
    * Instantiates a new edits the del btn selection listener.
    * 
    * @param btns the btns
    */
   public EditDelBtnSelectionListener(List<Button> btns) {
      if (btns != null) {
         this.btns = btns;
      } else {
         this.btns = new ArrayList<Button>();
      }
   }

   /**
    * {@inheritDoc}
    */
   public void selectionChanged(SelectionChangedEvent<BeanModel> se) {
      List<BeanModel> sels = se.getSelection();
      if (sels.size() > 0 && isEditableAndDeletable(sels)) {
         if (!btns.get(0).isEnabled()) {
            for (Button btn : btns) {
               btn.setEnabled(true);
            }
         }
      } else {
         for (Button btn : btns) {
            btn.setEnabled(false);
         }
      }

   }
   
   /**
    * Checks if is editable and deletable.
    * 
    * @return true, if is editable and deletable
    */
   protected boolean isEditableAndDeletable(List<BeanModel> sels) {
      return true;
   }
   

}
