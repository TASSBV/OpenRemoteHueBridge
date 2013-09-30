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

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;

/**
 * The screenIndicator indicate that the current group's screen size in the specified orientation.
 * It can highlight the current screen with white color, it would be in the topside of all screen contents.
 * It is used for iPhone and iPad.
 */
public class ScreenIndicator extends LayoutContainer {

   public ScreenIndicator(int screenCount, int screenIndex, int width, int height) {
      setStyleAttribute("position", "absolute");
      setSize(width, height);
      setLayout(new CenterLayout());
      if (screenCount > 1) {
         LayoutContainer indicatorContainer = new LayoutContainer();
         indicatorContainer.setSize(14 * screenCount, 14);
         for (int i = 0; i < screenCount; i++) {
            LayoutContainer indicator = new LayoutContainer();
            indicator.setSize(14, 14);
            if (i != screenIndex) {
               indicator.setStyleName("indicator-default");
            } else {
               indicator.setStyleName("indicator-current");
            }
            indicatorContainer.add(indicator);
         }
         add(indicatorContainer);
      }
   }
   
   @Override
   protected void afterRender() {
      super.afterRender();
      super.el().updateZIndex(1);
   }
}
