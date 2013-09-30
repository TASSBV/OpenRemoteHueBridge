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
package org.openremote.modeler.touchpanel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class TouchPanelContainer to get all touchPanels.
 */
public class TouchPanelContainer implements Serializable {

   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = -2809945297496042401L;

   /** The panels. */
   private static Map<String, List<TouchPanelDefinition>> panels = new HashMap<String, List<TouchPanelDefinition>>();

   /** The instance. */
   private static TouchPanelContainer instance = new TouchPanelContainer();
   
   /**
    * Instantiates a new panel container.
    */
   private TouchPanelContainer() {
   }
   
   /**
    * Gets the single instance of PanelContainer.
    * 
    * @return single instance of PanelContainer
    */
   public static synchronized TouchPanelContainer getInstance() {
      return instance;
   }
   
   /**
    * Gets the panels.
    * 
    * @return the panels
    */
   public Map<String, List<TouchPanelDefinition>> getPanels() {
      return panels;
   }

   /**
    * Sets the panels.
    * 
    * @param panels the panels
    */
   public void setPanels(Map<String, List<TouchPanelDefinition>> panels) {
      TouchPanelContainer.panels = panels;
   }
   
}
