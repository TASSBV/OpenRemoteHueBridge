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

package org.openremote.modeler.client.widget.buildingmodeler;

import org.openremote.modeler.client.widget.CommonForm;
import org.openremote.modeler.client.widget.CommonWindow;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.event.shared.EventBus;


/**
 * The window allows user to import Infrared Command.
 * 
 * @author Dan 2009-8-21
 */
public class IRCommandImportWindow extends CommonWindow {

   /** The ir command import form. */
   private CommonForm irCommandImportForm;
   
   /**
    * Instantiates a new iR command import window.
    * 
    * @param deviceBeanModel the device bean model
    */
   public IRCommandImportWindow(BeanModel deviceBeanModel) {
      setupWindow();
      layout();
      irCommandImportForm = new IRCommandImportForm(this, deviceBeanModel);
      add(irCommandImportForm);
      show();
   }

   /**
    * Setup window.
    */
   private void setupWindow() {
      setSize(570, 330);
      setModal(true);
      setHeading("Import IR Commands from Beehive");
      
      setLayout(new RowLayout(Orientation.VERTICAL));

   }


}
