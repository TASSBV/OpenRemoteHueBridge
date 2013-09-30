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

import org.openremote.modeler.client.event.ResponseJSONEvent;

import com.extjs.gxt.ui.client.event.Listener;

/**
 * Listener for JSON response.
 * 
 * Usage:
 * <pre>
    FormPanel form = new FormPanel();
    form.addListener(ResponseJSONEvent.ResponseJSON, new ResponseJSONListener() {
            public void afterSubmit(ResponseJSONEvent rje) {
              Object data = rje.getData();
              // do something
            }
         });
 * </pre>
 */
public abstract class ResponseJSONListener implements Listener<ResponseJSONEvent> {

   /**
    * {@inheritDoc}
    * @see com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
    */
   public void handleEvent(ResponseJSONEvent rje) {
      if (rje.getType() == ResponseJSONEvent.RESPONSEJSON) {
         afterSubmit(rje);
      }
   }

   /**
    * After submit.
    * 
    * @param rje the rje
    */
   public abstract void afterSubmit(ResponseJSONEvent rje);

}
