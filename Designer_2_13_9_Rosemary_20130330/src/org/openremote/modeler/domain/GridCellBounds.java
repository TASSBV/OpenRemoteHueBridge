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
package org.openremote.modeler.domain;

import org.openremote.modeler.client.widget.uidesigner.GridCellContainer;
import org.openremote.modeler.domain.component.UIGrid;

import com.extjs.gxt.ui.client.util.Rectangle;
/**
 * Used to record the information for GridCellContainer. 
 * @author Javen
 *
 */
public class GridCellBounds {
   private int left = 0;
   private int top = 0;
   private int width = 0;
   private int height = 0;
   
   public GridCellBounds(GridCellContainer container, UIGrid grid) {
      int cellWidth = grid.getWidth() / grid.getColumnCount();
      int cellHeight = grid.getHeight() / grid.getRowCount();
      this.left = container.getAbsoluteLeft();
      this.top = container.getAbsoluteTop();
      Cell cell = container.getCell();
      this.width = cell.getColspan() * cellWidth;
      this.height = cell.getRowspan() * cellHeight;
   }

   public Rectangle getBounds() {
      return new Rectangle(left, top, width, height);
   }

   public int getLeft() {
      return left;
   }

   public void setLeft(int left) {
      this.left = left;
   }

   public int getTop() {
      return top;
   }

   public void setTop(int top) {
      this.top = top;
   }

   public int getWidth() {
      return width;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public int getHeight() {
      return height;
   }

   public void setHeight(int height) {
      this.height = height;
   }
   
}