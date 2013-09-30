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
import org.openremote.modeler.client.widget.WizardWindow;

import com.extjs.gxt.ui.client.widget.form.FormPanel;


/**
 * Russound Wizard Window to create a new Russound device, including all commands, sensors, sliders and switches.
 * 
 * @author Marcus
 */
public class RussoundWizardWindow extends WizardWindow {


   /** The Step index DEVICE_INFO_STEP. */
   public static final int DEVICE_INFO_STEP = 0;
   
   
   /**
    * Instantiates a new device wizard window.
    * 
    * @param deviceBeanModel
    *           the device bean model
    */
   public RussoundWizardWindow() {
      super(null);
      setHeading("New Russound Device");
      show();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void initForms() {
      forms = new CommonForm[]{
            new RussoundInfoWizardForm(this)
            };
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void postProcess(int step, FormPanel currentForm) {
      switch (step) {
      case DEVICE_INFO_STEP:
         break;
      default:
         break;
      }
   }


}
