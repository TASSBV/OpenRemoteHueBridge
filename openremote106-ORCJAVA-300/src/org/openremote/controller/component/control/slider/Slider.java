/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.component.control.slider;

import java.util.ArrayList;
import java.util.List;

import org.openremote.controller.component.Sensory;
import org.openremote.controller.component.control.Control;

/**
 * Slider control for controlling devices with slide action.
 * 
 * @author Handy.Wang 2009-11-10
 */
public class Slider extends Control implements Sensory {

   /** The actions which slider allows. */
   public static final String[] AVAILABLE_ACTIONS = { "status" };
   
   /** The container element name of executable command refence of slider. */
   public static final String EXECUTE_CONTENT_ELEMENT_NAME = "setvalue";

   
   @Override
   public boolean isValidActionWith(String actionParam) {
      for (String action : availableActions) {
         if (action.equalsIgnoreCase(actionParam)) {
            return true;
         }
      }
      try {
         Integer.parseInt(actionParam);
         return true;
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

   @Override
   public int fetchSensorID() {
      return getSensor().getSensorID();
   }
}
