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
package org.openremote.modeler.client.rpc;

import org.openremote.modeler.exception.BeehiveNotAvailableException;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;

/**
 * Inherited from {@link AsyncCallback}. For global error handle.
 * 
 * @param <T> 
 * 
 * @author allen.wei
 */
public abstract class AsyncSuccessCallback<T> implements AsyncCallback<T> {

   /**
    * Because If the beehive is not available, this exception will be thrown many times because of initializing template
    * list, downloading user resources etc.  That means there will be a lot of alert windows show to user. Therefore,
    * we can replace most of alert information with silent "Info". If you want to let user know what the error
    * information is, you can just override this method in its subclass.
    * 
    * @param caught the caught
    * 
    * @see com.google.gwt.user.client.rpc.AsyncCallback#onFailure(java.lang.Throwable)
    */
   public void onFailure(Throwable caught) {
      if (checkTimeout(caught)) {
         return;
      }
      if (caught instanceof BeehiveNotAvailableException) {
         Info.display("ERROR", "Beehive is not available right now! ");
      } else {
         MessageBox.alert("ERROR", caught.getMessage(), null);
      }
   }


   
   
   /**
    * onSuccess.
    * 
    * @param result the result
    * 
    * @see com.google.gwt.user.client.rpc.AsyncCallback#onSuccess(java.lang.Object)
    */
   public abstract void onSuccess(T result);
   
   protected boolean checkTimeout(Throwable caught) {
      if (caught instanceof StatusCodeException) {
         StatusCodeException sce = (StatusCodeException) caught;
         if (sce.getStatusCode() == 401) {
            MessageBox.confirm("Timeout", "Your session has timeout, please login again. ", new Listener<MessageBoxEvent>() {
               @Override
               public void handleEvent(MessageBoxEvent be) {
                  if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                     Window.open("login.jsp", "_self", "");
                  }
               }
            });
            return true;
         }
      }
      return false;
   }

}
