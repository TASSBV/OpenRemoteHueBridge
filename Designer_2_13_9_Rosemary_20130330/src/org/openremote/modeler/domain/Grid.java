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

import java.util.ArrayList;
import java.util.List;

/**
 * The grid define a relative layout, but its in screen's absolute position.
 * A grid has cells, and the cell has component.
 */
public class Grid extends BusinessEntity {

   private static final long serialVersionUID = 1151373704125427323L;

   /** The left. */
   private int left;
   
   /** The top. */
   private int top;
   
   /** The width. */
   private int width;
   
   /** The Height. */
   private int height;
   
   /** The row count. */
   private int rowCount;

   /** The column count. */
   private int columnCount;

   /** The cells. */
   private List<Cell> cells = new ArrayList<Cell>();
   
   /**
    * Instantiates a new grid.
    */
   public Grid() {
   }
   
   /**
    * Instantiates a new grid.
    * 
    * @param rowCount the row count
    * @param columnCount the column count
    */
   public Grid(int rowCount, int columnCount) {
      this.rowCount = rowCount;
      this.columnCount = columnCount;
   }
   
   /**
    * Gets the row count.
    * 
    * @return the row count
    */
   public int getRowCount() {
      return rowCount;
   }

   /**
    * Gets the column count.
    * 
    * @return the column count
    */
   public int getColumnCount() {
      return columnCount;
   }

   /**
    * Sets the row count.
    * 
    * @param rowCount the new row count
    */
   public void setRowCount(int rowCount) {
      this.rowCount = rowCount;
   }

   /**
    * Sets the column count.
    * 
    * @param columnCount the new column count
    */
   public void setColumnCount(int columnCount) {
      this.columnCount = columnCount;
   }

   /**
    * Gets the left.
    * 
    * @return the left
    */
   public int getLeft() {
      return left;
   }

   /**
    * Gets the top.
    * 
    * @return the top
    */
   public int getTop() {
      return top;
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

   /**
    * Sets the left.
    * 
    * @param left the new left
    */
   public void setLeft(int left) {
      this.left = left;
   }

   /**
    * Sets the top.
    * 
    * @param top the new top
    */
   public void setTop(int top) {
      this.top = top;
   }

   /**
    * Sets the width.
    * 
    * @param width the new width
    */
   public void setWidth(int width) {
      this.width = width;
   }

   /**
    * Sets the height.
    * 
    * @param height the new height
    */
   public void setHeight(int height) {
      this.height = height;
   }

   /**
    * Gets the cells.
    * 
    * @return the cells
    */
   public List<Cell> getCells() {
      return cells;
   }

   /**
    * Sets the cells.
    * 
    * @param cells the new cells
    */
   public void setCells(List<Cell> cells) {
      this.cells = cells;
   }
   
   /**
    * Adds the cell.
    * 
    * @param cell the cell
    */
   public void addCell(Cell cell) {
      cells.add(cell);
   }
   
   /**
    * Removes the cell.
    * 
    * @param cell the cell
    */
   public void removeCell(Cell cell) {
      cells.remove(cell);
   }
}
