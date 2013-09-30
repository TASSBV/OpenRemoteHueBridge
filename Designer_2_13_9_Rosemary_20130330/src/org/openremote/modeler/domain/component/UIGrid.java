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
package org.openremote.modeler.domain.component;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.domain.Cell;
import org.openremote.modeler.domain.PositionableAndSizable;
/**
 * used to store grid's information. 
 * @author Javen
 *
 */

public class UIGrid extends UIComponent implements PositionableAndSizable {

  private static final long serialVersionUID = 7321863137565245106L;

  public static final int DEFAULT_LEFT = 1;
   public static final int DEFALUT_TOP = 1;
   public static final int DEFALUT_WIDTH = 200;
   public static final int DEFAULT_HEIGHT = 200;
   public static final int DEFALUT_ROW_COUNT = 4;
   public static final int DEFAULT_COL_COUNT = 4;
   
   private int left;
   private int top;
   private int width;
   private int height;
   private int rowCount;
   private int columnCount;
   private List<Cell> cells = new ArrayList<Cell>();
   
   public UIGrid(UIGrid grid) {
      this.left = grid.left;
      this.top = grid.top;
      this.width = grid.width;
      this.height = grid.height;
      this.rowCount = grid.rowCount;
      this.columnCount = grid.columnCount;
      this.cells = grid.cells;
   }
   public UIGrid() {
      super();
      this.left = DEFAULT_LEFT;
      this.top = DEFALUT_TOP;
      this.width = DEFALUT_WIDTH;
      this.height = DEFAULT_HEIGHT;
      this.columnCount = DEFAULT_COL_COUNT;
      this.rowCount = DEFALUT_ROW_COUNT;
   }

   public UIGrid(int left, int top, int width, int height, int rowCount, int columnCount) {
      super();
      this.height = height;
      this.columnCount = columnCount;
      this.left = left;
      this.rowCount = rowCount;
      this.top = top;
      this.width = width;
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

   public int getRowCount() {
      return rowCount;
   }

   public void setRowCount(int rowCount) {
      this.rowCount = rowCount;
   }

   public int getColumnCount() {
      return columnCount;
   }

   public void setColumnCount(int columnCount) {
      this.columnCount = columnCount;
   }

   public List<Cell> getCells() {
      return cells;
   }

   public void setCells(List<Cell> cells) {
      this.cells = cells;
   }

   public void addCell(Cell cell) {
      cells.add(cell);
   }
   
   public void removeCell(Cell cell) {
      cells.remove(cell);
   }
   
   @Override
   public String getPanelXml() {
      // TODO Auto-generated method stub
      return null;
   }
   @Override
   public String getName() {
     return "Grid";
   }
   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      UIGrid other = (UIGrid) obj;
      if (left == other.left && top == other.top && width == other.width && height == other.height
            && columnCount == other.columnCount && rowCount == other.rowCount && cells.equals(other.cells)) {
         return true;
      }
      return false;
   }
}
