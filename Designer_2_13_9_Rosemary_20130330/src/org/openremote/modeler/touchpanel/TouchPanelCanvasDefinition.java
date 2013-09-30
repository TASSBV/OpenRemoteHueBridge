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

/**
 * The Class GridDefinition defined the touch panel inner grid.
 */
public class TouchPanelCanvasDefinition implements Serializable {

   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = -1549498650675835443L;
   
   /** The width. */
   private int width;
   
   /** The height. */
   private int height;

   /**
    * Instantiates a new touch panel grid definition.
    */
   public TouchPanelCanvasDefinition() {
   }
   /**
    * Instantiates a new grid definition.
    * 
    * @param width the width
    * @param height the height
    */
   public TouchPanelCanvasDefinition(int width, int height) {
      this.width = width;
      this.height = height;
   }
   
   /**
    * Gets the width.
    * 
    * @return the width
    */
   public int getWidth() {
      return width;
   }

   /**
    * Gets the height.
    * 
    * @return the height
    */
   public int getHeight() {
      return height;
   }

   public void setWidth(int width) {
      this.width = width;
   }
   public void setHeight(int height) {
      this.height = height;
   }
   
   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }
      TouchPanelCanvasDefinition that = (TouchPanelCanvasDefinition) o;
      if (width != that.width) {
         return false;
      }
      if (height != that.height) {
         return false;
      }
      return true;
   }
   @Override
   public int hashCode() {
      int result = width;
      result = 31 * result + height;
      return result;
   }
   
   
}
