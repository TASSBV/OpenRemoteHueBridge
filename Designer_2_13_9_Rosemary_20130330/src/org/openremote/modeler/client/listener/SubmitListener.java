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

import org.openremote.modeler.client.event.SubmitEvent;

import com.extjs.gxt.ui.client.event.Listener;

/**
 * Listener for data submit.
 * 
 * Usage:
 * <pre>
    FormPanel form = new FormPanel();
    form.addListener(SubmitEvent.Submit, new SubmitListener() {
            public void afterSubmit(SubmitEvent be) {
              Object data = be.getData();
              // do something
            }
         });
 * </pre>
 */
public abstract class SubmitListener implements Listener<SubmitEvent> {

   /**
    * {@inheritDoc}
    * @see com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
    */
   public void handleEvent(SubmitEvent be) {
      if (be.getType() == SubmitEvent.SUBMIT) {
         afterSubmit(be);
      }
   }

   /**
    * After submit.
    * 
    * @param be the SubmitEvent
    */
   public abstract void afterSubmit(SubmitEvent be);

}
