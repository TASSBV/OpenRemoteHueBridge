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
import org.openremote.modeler.client.widget.propertyform.GridPropertyForm;
import org.openremote.modeler.client.widget.propertyform.PropertyForm;
import org.openremote.modeler.domain.component.UIGrid;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * The container handle to indicate that the grid can be drag, which include a handle and a grid container.
 * Its property form is to edit grid properties.
 */
public class GridLayoutContainerHandle extends ScreenComponent {
   
   public static final int DEFALUT_HANDLE_WIDTH = 16;
   public static final int DEFAULT_HANDLE_HEIGHT = 16;
   public static final String GRID_DISTANCE_NAME = "gridDistance";
   private GridLayoutContainer gridlayoutContainer = null;

   public GridLayoutContainerHandle(ScreenCanvas canvas, GridLayoutContainer gridlayoutContainer, WidgetSelectionUtil widgetSelectionUtil) {
      super(canvas, widgetSelectionUtil);
      this.gridlayoutContainer = gridlayoutContainer;
      setSize(DEFALUT_HANDLE_WIDTH, DEFAULT_HANDLE_HEIGHT);
      setStyleAttribute("position", "absolute");
      LayoutContainer handle = new LayoutContainer();
      handle.setSize(DEFALUT_HANDLE_WIDTH, DEFAULT_HANDLE_HEIGHT);
//      handle.setStyleAttribute("background-color", "red");
      handle.addStyleName("move-cursor");
      add(handle);
      gridlayoutContainer.setPosition(DEFALUT_HANDLE_WIDTH, DEFAULT_HANDLE_HEIGHT);
      gridlayoutContainer.addStyleName("cursor-move");
      add(gridlayoutContainer);
   }

   public GridLayoutContainer getGridlayoutContainer() {
      return gridlayoutContainer;
   }

   @Override
   public void setPosition(int left, int top) {
      if (gridlayoutContainer != null) {
         UIGrid grid = gridlayoutContainer.getGrid();
         grid.setLeft(left + DEFALUT_HANDLE_WIDTH);
         grid.setTop(top + DEFAULT_HANDLE_HEIGHT);
      }
      super.setPosition(left, top);
   }

   @Override
   public PropertyForm getPropertiesForm() {
      return new GridPropertyForm(this, widgetSelectionUtil);
   }

   @Override
   public String getName() {
      return "gridContainer";
   }
   
   public void update() {
      UIGrid grid = gridlayoutContainer.getGrid();
      gridlayoutContainer.refreshGrid();
      setPosition(grid.getLeft() - DEFALUT_HANDLE_WIDTH, grid.getTop() - DEFAULT_HANDLE_HEIGHT);
      layout();
   }
   
   /*@Override
   public boolean equals(Object o) {
      if (o instanceof GridContainer && o.getClass() == this.getClass()) {
         GridContainer container = (GridContainer) o;
         boolean hasSameGridLayoutContainer = container.getGridlayoutContainer() == gridlayoutContainer;
         boolean positionChanged = getGridlayoutContainer().getGrid().getLeft() == container.getGridlayoutContainer()
               .getGrid().getLeft()
               && getGridlayoutContainer().getGrid().getTop() == container.getGridlayoutContainer().getGrid().getTop();
         
         return hasSameGridLayoutContainer && positionChanged;
      }
      return false;
   }
   
   @Override 
   public Object clone(){
      GridLayoutContainer container = this.getGridlayoutContainer();
      GridContainer result = new GridContainer(gridlayoutContainer.getScreenCanvas(),container);
      UIGrid grid = gridlayoutContainer.getGrid();
      result.setPosition(grid.getLeft()-DEFALUT_WIDTH, grid.getTop()-DEFAULT_HEIGHT);
      return result;
   }*/
}
