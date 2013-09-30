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

import org.openremote.modeler.domain.component.ImageSource;

/**
 * The Class GridDefinition defined the touch panel inner grid.
 */
public class TouchPanelTabbarDefinition implements Serializable {
   private static final long serialVersionUID = 1342495193441933268L;
   public static final String IPHONE_TABBAR_BACKGROUND = "resources/images/iphone_tabbar_bg.png";
   private int height = 44;
   private ImageSource background = new ImageSource(IPHONE_TABBAR_BACKGROUND);
   
   public TouchPanelTabbarDefinition() {}

   public int getHeight() {
      return height;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public ImageSource getBackground() {
      return background;
   }

   public void setBackground(ImageSource background) {
      this.background = background;
   }
}
