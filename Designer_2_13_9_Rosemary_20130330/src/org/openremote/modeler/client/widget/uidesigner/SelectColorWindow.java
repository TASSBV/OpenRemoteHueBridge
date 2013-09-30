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
package org.openremote.modeler.client.widget.uidesigner;

import org.openremote.modeler.client.event.SubmitEvent;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.ColorPalette;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
/**
 * A window for select a color 
 * @author Javen 
 *
 */
public class SelectColorWindow extends Dialog {
     private ColorPalette colorPalette = new ColorPalette();
     
     public SelectColorWindow(){
        setHeading("Select Color");
        setMinHeight(260);
        setWidth(200);
        setLayout(new FitLayout());
        setModal(true);
        setButtons(Dialog.OKCANCEL);
        add(colorPalette);
        addListenerToPalette();
        setHideOnButtonClick(true);
        show();
     }
     
     private void addListenerToPalette(){
        addListener(Events.BeforeHide, new Listener<WindowEvent>() {
           public void handleEvent(WindowEvent be) {
              if (be.getButtonClicked() == getButtonById("ok")) {
                 String color = colorPalette.getValue();
                 fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(color));
              }
           }
        }); 
        
     }
     
     public void setDefaultColor(String color){
        colorPalette.setValue(color);
     }
}
