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

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;

/**
 * This listener asks for a confirmation before something is deleted.
 * <code>onDelete</code> method should be implemented.
 * 
 * @param <E> any ComponentEvent
 * 
 * @see DeleteSelectionEvent
 * 
 * @author Dan 2009-9-7
 */
public abstract class ConfirmDeleteListener<E extends ComponentEvent> extends SelectionListener<E> {

   @Override
   public void componentSelected(final E ce) {
      MessageBox box = new MessageBox();
      box.setButtons(MessageBox.YESNO);
      box.setIcon(MessageBox.QUESTION);
      box.setTitle("Delete");
      box.setMessage("Are you sure you want to delete?");
      box.addCallback(new Listener<MessageBoxEvent>() {

          public void handleEvent(MessageBoxEvent be) {
              if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                 onDelete(ce);
              }
          }
      });
      box.show();
   }
   /**
    * Fires when a component is deleted.
    * 
    * @param ce the component event
    */
   public abstract void onDelete(E ce);

}
