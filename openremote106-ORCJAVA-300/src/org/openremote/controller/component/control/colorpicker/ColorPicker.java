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
package org.openremote.controller.component.control.colorpicker;

import java.util.ArrayList;
import java.util.List;

import org.openremote.controller.component.control.Control;


public class ColorPicker extends Control {
   
   /**
    * Valid action is either in the list of known actions as returned by getAvailableActions or
    * is a color value expressed in 6 characters hexadecimal format.
    */
   @Override
   public boolean isValidActionWith(String actionParam) {
      for (String action : availableActions) {
         if (action.equalsIgnoreCase(actionParam)) {
            return true;
         }
      }
      try {
         int colorValueLength = actionParam.length();
         if (colorValueLength != 6) {
            return false;
         }
         int red = Integer.parseInt(actionParam.substring(0, 2), 16);
         int green = Integer.parseInt(actionParam.substring(2, 4), 16);
         int blue = Integer.parseInt(actionParam.substring(4, colorValueLength), 16);
         if (red >=0 && red <= 255 && green >= 0 && green <= 255 && blue >= 0 && blue <= 255) {
            return true;
         }
         return false;
      } catch (NumberFormatException e) {
         return false;
      }
   }

   @Override
   protected List<String> getAvailableActions() {
      List<String> availableActions = new ArrayList<String>();
      availableActions.add("status");
      return availableActions;
   }

}
