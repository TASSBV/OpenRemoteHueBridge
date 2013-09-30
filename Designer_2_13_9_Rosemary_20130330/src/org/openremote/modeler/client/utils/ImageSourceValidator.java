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
package org.openremote.modeler.client.utils;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.Window;
/**
 * A validator for validating a image source 
 * @author Javen
 *
 */
public class ImageSourceValidator {
   public static String validate(String resultHtml) {
      String result = resultHtml;
      if (resultHtml != null) {
      	//remove unnecessary string for chrome browser. 
         result = resultHtml.replaceAll("^<pre[^>]*>", "").replaceAll("</pre>$", "");
         //Timeout
         if (! result.matches(".+?\\.(png|gif|jpg|jpeg|PNG|GIF|JPG|GPEG)") && result.contains("401")) {
            MessageBox.confirm("Timeout", "Your session has timeout, please login again. ", new Listener<MessageBoxEvent>() {

               @Override
               public void handleEvent(MessageBoxEvent be) {
                  if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                     Window.open("login.jsp", "_self", "");
                  }
               }
               
            });
            return "";
         }
      }
      return result;
   }
   
}
