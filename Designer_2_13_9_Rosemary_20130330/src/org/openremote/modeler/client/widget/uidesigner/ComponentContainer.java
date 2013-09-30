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

import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.propertyform.PropertyForm;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * The parent of GridLayoutContainer and AbsoluteLayout. 
 * A ComponentContainer is a container for screen canvas to store component. 
 * @author Javen
 *
 */
public class ComponentContainer extends LayoutContainer {
   private ScreenCanvas screenCanvas = null;
   
   protected WidgetSelectionUtil widgetSelectionUtil;
   
   public ComponentContainer(WidgetSelectionUtil widgetSelectionUtil) {
     this.widgetSelectionUtil = widgetSelectionUtil;
   }
   
   public ComponentContainer(ScreenCanvas screenCanvas, WidgetSelectionUtil widgetSelectionUtil) {
     this(widgetSelectionUtil);
      this.screenCanvas = screenCanvas;
   }

   public ScreenCanvas getScreenCanvas() {
      return screenCanvas;
   }

   public void setScreenCanvas(ScreenCanvas screenCanvas) {
      this.screenCanvas = screenCanvas;
   }
   /**
    * hide the background for moving component. 
    */
   public void hideBackground() {
      screenCanvas.hideBackground();
   }
   
   public PropertyForm getPropertiesForm() {
      return new PropertyForm(this, widgetSelectionUtil);
   }
}
