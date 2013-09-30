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

import org.openremote.modeler.client.model.ORBounds;
import org.openremote.modeler.client.utils.AbsoluteBoundsListenerManager;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.component.ScreenComponent;
import org.openremote.modeler.client.widget.propertyform.PropertyForm;
import org.openremote.modeler.domain.Absolute;

/**
 * A layout container act as absolute position in screen canvas.
 */
public class AbsoluteLayoutContainer extends ComponentContainer {
   public static final String ABSOLUTE_DISTANCE_NAME = "distance";
   private Absolute absolute;
   private ScreenComponent screenComponent;
   /**
    * Instantiates a new absolute layout container.
    */
   public AbsoluteLayoutContainer(ScreenCanvas screenCanvas, Absolute absolute, ScreenComponent screenComponent, WidgetSelectionUtil widgetSelectionUtil) {
      super(screenCanvas, widgetSelectionUtil);
      addStyleName("cursor-move");
      setStyleAttribute("position", "absolute");
      this.absolute = absolute;
      this.screenComponent = screenComponent;
      add(screenComponent);
   }

   public Absolute getAbsolute() {
      return absolute;
   }

   public ScreenComponent getScreenComponent() {
      return screenComponent;
   }

   @Override
   public void setSize(int width, int height) {
      super.setSize(width, height);
      absolute.setSize(width, height);
      screenComponent.setSize(width - 2, height - 2);
      AbsoluteBoundsListenerManager.getInstance().notifyAbsoluteBoundsListener(this,
            new ORBounds(absolute.getLeft(), absolute.getTop(), width, height));
   }

   @Override
   public void setPosition(int left, int top) {
      super.setPosition(left, top);
      // TODO: this should not be here, modifying to underlying model should not be done from visual object
      absolute.setPosition(left, top);
      AbsoluteBoundsListenerManager.getInstance().notifyAbsoluteBoundsListener(this,
            new ORBounds(left, top, absolute.getWidth(), absolute.getHeight()));
   }

   @Override
   public PropertyForm getPropertiesForm() {
     return this.screenComponent.getPropertiesForm();
   }
   
}
