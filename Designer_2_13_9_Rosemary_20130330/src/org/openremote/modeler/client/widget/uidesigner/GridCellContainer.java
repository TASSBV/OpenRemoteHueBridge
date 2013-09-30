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
import org.openremote.modeler.client.widget.component.ScreenComponent;
import org.openremote.modeler.client.widget.propertyform.PropertyForm;
import org.openremote.modeler.domain.Cell;

/**
 * The container for storing cell component, which placed in grid container.
 */
public class GridCellContainer extends ComponentContainer { 

   private Cell cell;
   private GridLayoutContainer gridContainer = null;
   
   private ScreenComponent screenComponent;
   
   public GridCellContainer(ScreenCanvas canvas, GridLayoutContainer gridContainer, WidgetSelectionUtil widgetSelectionUtil) {
      super(canvas, widgetSelectionUtil);
      this.gridContainer = gridContainer;
   }
   public GridCellContainer(ScreenCanvas canvas, Cell cell, ScreenComponent screenComponent,
         GridLayoutContainer gridContainer, WidgetSelectionUtil widgetSelectionUtil) {
      super(canvas, widgetSelectionUtil);
      this.cell = cell;
      this.screenComponent = screenComponent;
      this.gridContainer = gridContainer;
      addStyleName("cursor-move");
      setStyleAttribute("position", "absolute");
      add(this.screenComponent);
   }

   public Cell getCell() {
      return cell;
   }

   public ScreenComponent getScreenComponent() {
      return screenComponent;
   }
   
   public void setCellPosition(int posX, int posY) {
      cell.setPosX(posX);
      cell.setPosY(posY);
   }

   public void setCellSpan(int colspan, int rowspan) {
      cell.setColspan(colspan);
      cell.setRowspan(rowspan);
   }
   
   public GridLayoutContainer getGridContainer() {
      return gridContainer;
   }
   @Override
   public void setSize(int width, int height) {
      super.setSize(width, height);
      screenComponent.setSize(width - 2, height - 2);
   }
   
   /**
    * Fill the cell holds area.
    */
   public void fillArea(boolean[][] btnArea) {
      for (int i = 0; i < cell.getColspan(); i++) {
         int x = cell.getPosX() + i;
         for (int j = 0; j < cell.getRowspan(); j++) {
            int y = cell.getPosY() + j;
            btnArea[x][y] = true;
         }
      }
   }

   /**
    * Clear the cell holds area.
    */
   public void clearArea(boolean[][] btnArea) {
      for (int i = 0; i < cell.getColspan(); i++) {
         int x = cell.getPosX() + i;
         for (int j = 0; j < cell.getRowspan(); j++) {
            int y = cell.getPosY() + j;
            btnArea[x][y] = false;
         }
      }
   }
   
   @Override
   public PropertyForm getPropertiesForm() {
     return this.screenComponent.getPropertiesForm();
   }
   
   public void setScreenComponent(ScreenComponent screenComponent) {
      if (this.screenComponent != null) {
         remove(this.screenComponent);
      }
      this.screenComponent = screenComponent;
      add(this.screenComponent);
   }
}
