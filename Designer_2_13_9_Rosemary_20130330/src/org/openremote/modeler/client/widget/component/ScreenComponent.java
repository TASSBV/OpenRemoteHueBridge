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
package org.openremote.modeler.client.widget.component;

import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.uidesigner.ComponentContainer;
import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.UIComponent;
import org.openremote.modeler.domain.component.UIImage;
import org.openremote.modeler.domain.component.UILabel;
import org.openremote.modeler.domain.component.UISlider;
import org.openremote.modeler.domain.component.UISwitch;
import org.openremote.modeler.domain.component.UITabbar;

/**
 * ScreenControl as the component's super class.
 */
public abstract class ScreenComponent extends ComponentContainer {
   public ScreenComponent(ScreenCanvas screenCanvas, WidgetSelectionUtil widgetSelectionUtil) {
      super(screenCanvas, widgetSelectionUtil);
   }

   public abstract String getName();

   /**
    * Builds the ScreenControl according to uiControl type.
    */
   public static ScreenComponent build(ScreenCanvas canvas, WidgetSelectionUtil widgetSelectionUtil, UIComponent uiComponent) {
      if (uiComponent instanceof UIButton) {
         return new ScreenButton(canvas, (UIButton) uiComponent, widgetSelectionUtil);
      } else if (uiComponent instanceof UISwitch) {
         return new ScreenSwitch(canvas, (UISwitch) uiComponent, widgetSelectionUtil);
      } else if (uiComponent instanceof UISlider) {
         return new ScreenSlider(canvas,(UISlider)uiComponent, widgetSelectionUtil);
      } else if (uiComponent instanceof UILabel) {
         return new ScreenLabel(canvas, (UILabel) uiComponent, widgetSelectionUtil);
      } else if (uiComponent instanceof UIImage) {
         return new ScreenImage(canvas, (UIImage) uiComponent, widgetSelectionUtil);
      } else if (uiComponent instanceof UITabbar) {
         return new ScreenTabbar(canvas, (UITabbar)uiComponent, widgetSelectionUtil);
      }
      return null;
   }

}
