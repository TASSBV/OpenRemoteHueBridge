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

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Window;

/**
 * Wizard form for {@link DeviceInfoForm}, this is a part of {@link DeviceWizardWindow}.
 * 
 * @author Dan 2009-8-21
 */
public class DeviceInfoWizardForm extends DeviceInfoForm {

   /**
    * Instantiates a new device info wizard form.
    * 
    * @param wrapper
    *           the wrapper
    * @param deviceBeanModel
    *           the device bean model
    */
   public DeviceInfoWizardForm(Component wrapper, BeanModel deviceBeanModel) {
      super(wrapper, deviceBeanModel);
   }

   /* (non-Javadoc)
    * @see org.openremote.modeler.client.widget.CommonForm#isNoButton()
    */
   @Override
   public boolean isNoButton() {
      return true;
   }

   /* (non-Javadoc)
    * @see com.extjs.gxt.ui.client.widget.Component#show()
    */
   @Override
   public void show() {
      super.show();
      ((Window) wrapper).setSize(360, 200);
   }
   
   
   

}
