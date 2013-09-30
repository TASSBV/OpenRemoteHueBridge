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

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.event.WidgetDeleteEvent;
import org.openremote.modeler.client.gxtextends.ScreenDropTarget;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.component.ScreenButton;
import org.openremote.modeler.client.widget.component.ScreenComponent;
import org.openremote.modeler.client.widget.component.ScreenImage;
import org.openremote.modeler.client.widget.component.ScreenLabel;
import org.openremote.modeler.client.widget.component.ScreenSwitch;
import org.openremote.modeler.domain.Cell;
import org.openremote.modeler.domain.GridCellBounds;
import org.openremote.modeler.domain.component.UIComponent;
import org.openremote.modeler.domain.component.UIGrid;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.ResizeEvent;
import com.extjs.gxt.ui.client.event.ResizeListener;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * A layout container to display as grid, the inner is relative position.
 */
public class GridLayoutContainer extends ComponentContainer {
   public static final String BOUNDS_RECORD_NAME = "boundsRecord";
   /** The screen. */

   /** The Constant POSITION. */
   public static final String POSITION = "position";

   /** The btn in area. */
   private boolean[][] btnInArea;
   
   private UIGrid grid = null;

   private FlexTable screenTable = new FlexTable();
   
   private List<GridCellContainer> cellContainers = new ArrayList<GridCellContainer>();
   private int cellWidth = 0;
   private int cellHeight = 0;
   public GridLayoutContainer(ScreenCanvas screenCanvas, UIGrid grid, WidgetSelectionUtil widgetSelectionUtil) {
      super(screenCanvas, widgetSelectionUtil);
      this.grid = grid;
      btnInArea = new boolean[grid.getColumnCount()][grid.getRowCount()];
      addStyleName("absolute");
//      addStyleName("screen-background");
      
      initCellInArea(grid);
      createGrid();
      addTempDropTarget(this);
   }

   /**
    * Inits the cell in area.
    */
   private void initCellInArea(UIGrid grid) {
      for (int x = 0; x < grid.getColumnCount(); x++) {
         for (int y = 0; y < grid.getRowCount(); y++) {
            btnInArea[x][y] = false;
         }
      }
   }

   /**
    * Creates the grid, and init it's cells.
    * 
    * @param grid the grid
    */
   public void createGrid() {
      screenTable = new FlexTable();
      screenTable.setCellPadding(0);
      screenTable.setCellSpacing(0);
      screenTable.addStyleName("panel-table");
      add(screenTable);
      int gridWidth = grid.getWidth();
      int gridHeight = grid.getHeight();
      screenTable.setPixelSize(gridWidth, gridHeight);
      refreshGrid();
   }

   private void resizeBtnInArea() {
     boolean newArray[][] = new boolean[grid.getColumnCount()][grid.getRowCount()];
     for (int i = 0; i < newArray.length; i++) {
       for (int j = 0; j < newArray[i].length; j++) {
         if (i < btnInArea.length && j < btnInArea[i].length) {
           newArray[i][j] = btnInArea[i][j];
         } else {
           newArray[i][j] = false;
         }
       }
     }     
     btnInArea = newArray;
   }
   
   public void refreshGrid() {
    resizeBtnInArea();     
      for (int i = cellContainers.size() - 1; i >= 0; i--) {
         cellContainers.get(i).removeFromParent();
         cellContainers.remove(i);
      }
      int gridWidth = grid.getWidth();
      int gridHeight = grid.getHeight();
      cellWidth = (gridWidth - (grid.getColumnCount() + 1)) / grid.getColumnCount();
      cellHeight = (gridHeight - (grid.getRowCount() + 1)) / grid.getRowCount();
      DNDListener dndListener = new DNDListener() {
         @Override
         public void dragEnter(DNDEvent e) {
            Object data = e.getData();
            if(data instanceof ComponentContainer){
              ((ComponentContainer) data).hideBackground();
            }
            super.dragEnter(e);
         }
         @SuppressWarnings("unchecked")
         public void dragDrop(DNDEvent e) {
            LayoutContainer targetCell = (LayoutContainer) e.getDropTarget().getComponent();
            Point targetPosition = (Point) targetCell.getData(POSITION);
            GridCellContainer cellContainer = new GridCellContainer(getScreenCanvas(), GridLayoutContainer.this, widgetSelectionUtil);
            Object data = e.getData();
            if (data instanceof AbsoluteLayoutContainer) {
               AbsoluteLayoutContainer container = (AbsoluteLayoutContainer) data;
               cellContainer = addAbsoluteWidgetToGrid(grid, cellWidth + 1, cellHeight + 1, targetCell, targetPosition,
                     container);
               container.hideBackground();
            } else if (data instanceof GridCellContainer) {
               cellContainer = moveCellInGrid(grid, targetCell, targetPosition, data);
            } else if (data instanceof List) {
               List<ModelData> models = (List<ModelData>) data;
               if (models.size() > 0 && ((BeanModel) models.get(0).get("model")).getBean() instanceof UIGrid) {
                  e.setCancelled(true);
                  return;
               }
               cellContainer = addNewWidget(grid, cellWidth, cellHeight, e, targetCell, targetPosition,
                     cellContainer);
            } else if (data instanceof GridLayoutContainerHandle) {
               ((ComponentContainer) data).hideBackground();
               ((ComponentContainer) data).show();
               e.setCancelled(true);
               return;
            }
            cellContainer.fillArea(btnInArea);
            widgetSelectionUtil.setSelectWidget(cellContainer);
            makeCellContainerResizable(cellWidth, cellHeight, cellContainer);
            layout();
            cellContainers.add(cellContainer);
            super.dragDrop(e);
         }

      };
      for (int i = 0; i < grid.getRowCount(); i++) { // Initial the screen table, make it can be drop buttons.
         for (int j = 0; j < grid.getColumnCount(); j++) {
            LayoutContainer cell = new LayoutContainer();
            cell.setSize(cellWidth, cellHeight);
            screenTable.setWidget(i, j, cell);
            cell.setData(POSITION, new Point(j, i));
            ScreenDropTarget dropTarget = new ScreenDropTarget(cell);
            dropTarget.setGroup(Constants.CONTROL_DND_GROUP);
            dropTarget.setOverStyle("background-color");
            dropTarget.addDNDListener(dndListener);
         }
      }
      if (grid.getCells().size() > 0) { // If the Panel has closed, there may have some buttons on screen to be
         // rendered.
         List<Cell> cells = grid.getCells();
         for (Cell cell : cells) {
            GridCellContainer cellContainer = createCellContainer(grid, cell, (cellWidth + 1) * cell.getColspan()+1,
                  (cellHeight + 1) * cell.getRowspan()+1);
            makeCellContainerResizable(cellWidth, cellHeight, cellContainer);
            cellContainer.setPosition(cellWidth * cell.getPosX() + cell.getPosX() + 1, cellHeight * cell.getPosY()
                  + cell.getPosY() + 1);
            cellContainer.setCellSpan(cell.getColspan(), cell.getRowspan());
            add(cellContainer);
            cellContainer.fillArea(btnInArea);
            createDragSource(cellContainer);
            layout();

         }
      }
      screenTable.setPixelSize(gridWidth, gridHeight);
      setSize(gridWidth, gridHeight);
      setBorders(false);
      layout();
   }

   /**
    * init a cell with it's width and height.
    * 
    */
   private GridCellContainer createCellContainer(final UIGrid grid, Cell cell, int cellWidth, int cellHeight) {
      final ScreenComponent screenComponent = ScreenComponent.build(this.getScreenCanvas(), widgetSelectionUtil, cell.getUiComponent());
      final GridCellContainer cellContainer = new GridCellContainer(getScreenCanvas(), cell, screenComponent, this, widgetSelectionUtil) {
         @Override
         public void onComponentEvent(ComponentEvent ce) {
            if (ce.getEventTypeInt() == Event.ONMOUSEDOWN) {
               widgetSelectionUtil.setSelectWidget((GridCellContainer) this);
               if (screenComponent instanceof ScreenButton) {
                  ((ScreenButton)screenComponent).setPressedImage();
               } else if (screenComponent instanceof ScreenSwitch) {
                  ((ScreenSwitch)screenComponent).onStateChange();
               } else if (screenComponent instanceof ScreenLabel) {
                  ((ScreenLabel)screenComponent).onStateChange();
               } else if (screenComponent instanceof ScreenImage) {
                  ((ScreenImage)screenComponent).onStateChange();
               }
            } else if (ce.getEventTypeInt() == Event.ONMOUSEUP){
               if (screenComponent instanceof ScreenButton) {
                  ((ScreenButton)screenComponent).setDefaultImage();
               }
            } else if (ce.getEventTypeInt() == Event.ONDBLCLICK) {
               widgetSelectionUtil.setSelectWidget((GridLayoutContainerHandle)this.getGridContainer().getParent());
            }
            ce.cancelBubble();
            super.onComponentEvent(ce);
         }
      };
      cellContainer.sinkEvents(Event.ONMOUSEUP);
      cellContainer.sinkEvents(Event.ONDBLCLICK);
      cellContainer.addListener(WidgetDeleteEvent.WIDGETDELETE, new Listener<WidgetDeleteEvent>() {
         public void handleEvent(WidgetDeleteEvent be) {
            grid.removeCell(cellContainer.getCell());
            cellContainer.removeFromParent();
         }
         
      });
      new KeyNav<ComponentEvent>() {
         @Override
         public void onDelete(ComponentEvent ce) {
            super.onDelete(ce);
            MessageBox box = new MessageBox();
            box.setButtons(MessageBox.YESNO);
            box.setIcon(MessageBox.QUESTION);
            box.setTitle("Delete");
            box.setMessage("Are you sure you want to delete?");
            box.addCallback(new Listener<MessageBoxEvent>() {
               public void handleEvent(MessageBoxEvent be) {
                  if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                     grid.removeCell(cellContainer.getCell());
                     cellContainer.removeFromParent();
                     widgetSelectionUtil.resetSelection();
                  }
               }
            });
            box.show();
         }
         public void onBackspace(ComponentEvent ce) {
            super.onBackspace(ce);
            this.onDelete(ce);
         }
      } .bind(cellContainer);
      cellContainer.setSize(cellWidth + 1, cellHeight + 1);
      cellContainers.add(cellContainer);
      return cellContainer;
   }
   
   private GridCellContainer cloneCellContainer(GridCellContainer container) {
      final Cell cell = container.getCell();
      final ScreenComponent screenComponent = container.getScreenComponent();
      final GridCellContainer cellContainer =  new GridCellContainer(getScreenCanvas(), cell, container.getScreenComponent(), this, widgetSelectionUtil) {
         @Override
         public void onComponentEvent(ComponentEvent ce) {
            if (ce.getEventTypeInt() == Event.ONMOUSEDOWN) {
               widgetSelectionUtil.setSelectWidget((GridCellContainer) this);
               if (screenComponent instanceof ScreenButton) {
                  ((ScreenButton)screenComponent).setPressedImage();
               } else if (screenComponent instanceof ScreenSwitch) {
                  ((ScreenSwitch)screenComponent).onStateChange();
               } else if (screenComponent instanceof ScreenLabel) {
                  ((ScreenLabel)screenComponent).onStateChange();
               } else if (screenComponent instanceof ScreenImage) {
                  ((ScreenImage)screenComponent).onStateChange();
               } 
            } else if (ce.getEventTypeInt() == Event.ONMOUSEUP){
               if (screenComponent instanceof ScreenButton) {
                  ((ScreenButton)screenComponent).setDefaultImage();
               }
            } else if (ce.getEventTypeInt() == Event.ONDBLCLICK) {
               widgetSelectionUtil.setSelectWidget((GridLayoutContainerHandle)this.getGridContainer().getParent());
            }
            ce.cancelBubble();
            super.onComponentEvent(ce);
         }
      };
      cellContainer.sinkEvents(Event.ONMOUSEUP);
      cellContainer.sinkEvents(Event.ONDBLCLICK);
      cellContainer.addListener(WidgetDeleteEvent.WIDGETDELETE, new Listener<WidgetDeleteEvent>() {
         public void handleEvent(WidgetDeleteEvent be) {
            grid.removeCell(cell);
            cellContainer.removeFromParent();
         }
         
      });
      new KeyNav<ComponentEvent>() {
         @Override
         public void onDelete(ComponentEvent ce) {
            super.onDelete(ce);
            MessageBox box = new MessageBox();
            box.setButtons(MessageBox.YESNO);
            box.setIcon(MessageBox.QUESTION);
            box.setTitle("Delete");
            box.setMessage("Are you sure you want to delete?");
            box.addCallback(new Listener<MessageBoxEvent>() {
                public void handleEvent(MessageBoxEvent be) {
                    if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                       grid.removeCell(cellContainer.getCell());
                       cellContainer.removeFromParent();
                       widgetSelectionUtil.resetSelection();
                    }
                }
            });
            box.show();
         }
         public void onBackspace(ComponentEvent ce) {
            super.onBackspace(ce);
            this.onDelete(ce);
         }
      } .bind(cellContainer);
      cellContainers.add(cellContainer);
      return cellContainer;
   }
   /**
    * Find max x direction in grid when resize the cell container.
    * 
    * @param cellContainer the cell container
    * @param grid the grid
    * 
    */
   private int findMaxXWhenResize(GridCellContainer cellContainer, UIGrid grid) {
      Cell cell = cellContainer.getCell();
      int maxX = cell.getPosX();
      for (; maxX < grid.getColumnCount() - 1; maxX++) {
         for (int tmpY = cell.getPosY(); ((tmpY < grid.getRowCount()) && (tmpY < cell.getPosY()
               + cell.getRowspan())); tmpY++) {
            if ((btnInArea[maxX + 1][tmpY])
                  && ((maxX + 1) > (cell.getPosX() + cell.getColspan() - 1))) {
               return maxX;
            }
         }
      }
      return maxX;
   }

   /**
    * Find max y direction in grid when resize the cell container.
    * 
    * @param cellContainer the cell container
    * @param grid the grid
    * 
    */
   private int findMaxYWhenResize(GridCellContainer cellContainer, UIGrid grid) {
      Cell cell = cellContainer.getCell();
      int maxY = cell.getPosY();
      for (; maxY < grid.getRowCount() - 1; maxY++) {
         for (int tmpX = cell.getPosX(); ((tmpX < grid.getColumnCount()) && (tmpX < cell.getPosX()
               + cell.getColspan())); tmpX++) {
            if ((btnInArea[tmpX][maxY + 1])
                  && ((maxY + 1) > (cell.getPosY() + cell.getRowspan() - 1))) {
               return maxY;
            }
         }
      }
      return maxY;
   }

   /**
    * Compute the cell container if can be drop in the position(x,y).
    */
   private boolean canDrop(int x, int y, Cell cell, UIGrid grid) {
      for (int tmpX = x; tmpX < x + cell.getColspan(); tmpX++) {
         for (int tmpY = y; tmpY < y + cell.getRowspan(); tmpY++) {
            if (tmpX > grid.getColumnCount() - 1 || tmpY > grid.getRowCount() - 1) {
               return false;
            }
            if (btnInArea[tmpX][tmpY]) {
               return false;
            }
         }
      }

      return true;
   }

   /**
    * Creates the drag source.
    * 
    * @param cellContainer the cell container
    */
   private void createDragSource(final GridCellContainer cellContainer) {
      DragSource source = new DragSource(cellContainer) {

         @Override
         protected void onDragDrop(DNDEvent event) {
            super.onDragDrop(event);
         }

         @Override
         protected void onDragStart(DNDEvent event) {
            GridCellBounds boundsRecorder = new GridCellBounds(cellContainer, grid);
            cellContainer.clearArea(btnInArea);
            Point distance = new Point(boundsRecorder.getWidth(), boundsRecorder.getHeight());
            cellContainer.setData(AbsoluteLayoutContainer.ABSOLUTE_DISTANCE_NAME, distance);
            /*
             * record the location information for a GridCellContainer before being removing. 
             */
            cellContainer.setData(GridLayoutContainer.BOUNDS_RECORD_NAME, boundsRecorder);
            /*
             * set the background size for the cell.
             */
            getScreenCanvas().getMoveBackGround().setSize(boundsRecorder.getWidth(), boundsRecorder.getHeight());
            event.setData(cellContainer);
            cellContainer.removeFromParent();
            event.getStatus().setStatus(true);
            event.getStatus().update("1 item selected");
            event.cancelBubble();
            ScreenComponent screenComponent = cellContainer.getScreenComponent();
            if (screenComponent instanceof ScreenButton) {
               ((ScreenButton)screenComponent).setDefaultImage();
            }
            grid.removeCell(cellContainer.getCell());
         }
      };
      source.setGroup(Constants.CONTROL_DND_GROUP);
      source.enable();

      addTempDropTarget(cellContainer);
   }

   private void addTempDropTarget(final LayoutContainer container) {
      DropTarget target = new DropTarget(container);
      target.setGroup(Constants.CONTROL_DND_GROUP);
      target.addDNDListener(new DNDListener() {

         @Override
         public void dragDrop(DNDEvent e) {
            GridCellContainer cellContainer = new GridCellContainer(getScreenCanvas(), GridLayoutContainer.this, widgetSelectionUtil);
            Object data = e.getData();
            if (data instanceof GridCellContainer) {
               GridCellContainer container = (GridCellContainer) data;
               GridCellBounds recorder = container.getData(GridLayoutContainer.BOUNDS_RECORD_NAME);
               cellContainer = cloneCellContainer(container);
               cellContainer.setBounds(recorder.getBounds());
               add(cellContainer);
               createDragSource(cellContainer);
               layout();
            }
         }

      });
   }

   /*
    * Make cell container resizable.
    * 
    * @param cellWidth the cell width
    * @param cellHeight the cell height
    * @param cellContainer the cell container
    */
   private void makeCellContainerResizable(final int cellWidth, final int cellHeight, GridCellContainer cellContainer) {
      final Resizable resizable = new Resizable(cellContainer, Constants.RESIZABLE_HANDLES);
      resizable.addResizeListener(new ResizeListener() {

         @Override
         public void resizeEnd(ResizeEvent re) {

            GridCellContainer resizedCellContainer = (GridCellContainer) re.getComponent();
            int vSize = (int) Math.round((float) resizedCellContainer.getHeight() / cellHeight);
            int hSize = (int) Math.round((float) resizedCellContainer.getWidth() / cellWidth);
            resizedCellContainer.setHeight(vSize * cellHeight + vSize - 1);
            resizedCellContainer.setWidth(hSize * cellWidth + hSize - 1);
            resizedCellContainer.setCellSpan(hSize, vSize);
            resizedCellContainer.fillArea(btnInArea);
            cellContainers.add(resizedCellContainer);
         }

         @Override
         public void resizeStart(ResizeEvent re) {
            GridCellContainer resizeCellContainer = (GridCellContainer) re.getComponent();
//            if (resizeCellContainer.getWidth() / cellWidth == 2) { // In IE if the button width is 2 cell width, it can't be resize to 1 cell width.
//               resizeCellContainer.adjustCenter(cellWidth);
//            }
//            int maxX = findMaxXWhenResize(resizeCellContainer, screen.getGrid());
//            int maxY = findMaxYWhenResize(resizeCellContainer, screen.getGrid());
            int maxX = findMaxXWhenResize(resizeCellContainer, grid);
            int maxY = findMaxYWhenResize(resizeCellContainer, grid);
            resizable.setMaxWidth((maxX - resizeCellContainer.getCell().getPosX() + 1) * cellWidth);
            resizable.setMaxHeight((maxY - resizeCellContainer.getCell().getPosY() + 1) * cellHeight);
            resizeCellContainer.clearArea(btnInArea);
         }

      });
   }

   /**
    * Creates the new cell container according to uiControl type.
    * 
    */
   private GridCellContainer createNewCellContainer(UIComponent uiComponent, UIGrid grid, int cellWidth, int cellHeight) {
      Cell cell = new Cell(IDUtil.nextID());
      cell.setUiComponent(UIComponent.createNew(uiComponent));
      grid.addCell(cell);
      return createCellContainer(grid, cell, cellWidth, cellHeight);
   }
   
   private GridCellContainer AbsoluteToCell(UIComponent uiComponent, UIGrid grid, int cellWidth, int cellHeight) {
      Cell cell = new Cell(IDUtil.nextID());
      cell.setUiComponent(UIComponent.copy(uiComponent));
      grid.addCell(cell);
      return createCellContainer(grid, cell, cellWidth, cellHeight);
   }
   private GridCellContainer addAbsoluteWidgetToGrid(UIGrid grid, int cellWidth,
        int cellHeight, LayoutContainer targetCell, Point targetPosition, AbsoluteLayoutContainer container) {
      getScreenCanvas().getScreen().removeAbsolute(container.getAbsolute());                                   //remove it from screen. 
      GridCellContainer cellContainer;
      container.removeFromParent();
      container.hideBackground();
      cellContainer = AbsoluteToCell(container.getAbsolute().getUiComponent(), grid, cellWidth, cellHeight);
      cellContainer.getClass();
      cellContainer.setCellSpan(1, 1);
      cellContainer.setCellPosition(targetPosition.x, targetPosition.y);
      cellContainer.setPagePosition(targetCell.getAbsoluteLeft(), targetCell.getAbsoluteTop());

      add(cellContainer);
      createDragSource(cellContainer);
      return cellContainer;
   }
   @SuppressWarnings("unchecked")
   private GridCellContainer addNewWidget(UIGrid grid, int cellWidth, int cellHeight,
         DNDEvent e, LayoutContainer targetCell, Point targetPosition, GridCellContainer cellContainer) {
      List<ModelData> models = (List<ModelData>) e.getData();
      if (models.size() > 0) {
         BeanModel dataModel = models.get(0).get("model");
         cellContainer = createNewCellContainer((UIComponent) dataModel.getBean(), grid, cellWidth, cellHeight);
         cellContainer.setCellSpan(1, 1);
         cellContainer.setCellPosition(targetPosition.x, targetPosition.y);
         cellContainer.setPagePosition(targetCell.getAbsoluteLeft(), targetCell.getAbsoluteTop());

         add(cellContainer);
         cellContainers.add(cellContainer);
         createDragSource(cellContainer);
      }
      return cellContainer;
   }

   private GridCellContainer moveCellInGrid(UIGrid grid, LayoutContainer targetCell, Point targetPosition,
         Object data) {
      GridCellContainer cellContainer;
      GridCellContainer container = (GridCellContainer) data;
      GridCellBounds recorder = container.getData(GridLayoutContainer.BOUNDS_RECORD_NAME);
      cellContainer = cloneCellContainer(container);
      cellContainer.setBounds(recorder.getBounds());
      if (canDrop(targetPosition.x, targetPosition.y, cellContainer.getCell(), grid)) {
         cellContainer.setCellPosition(targetPosition.x, targetPosition.y);
         cellContainer.setPagePosition(targetCell.getAbsoluteLeft(), targetCell.getAbsoluteTop());
      }
      grid.addCell(container.getCell());
      add(cellContainer);
      createDragSource(cellContainer);
      return cellContainer;
   }


   public UIGrid getGrid() {
      return grid;
   }

   public void setGrid(UIGrid grid) {
      this.grid = grid;
   }

   public FlexTable getScreenTable() {
      return screenTable;
   }

   public void setScreenTable(FlexTable screenTable) {
      this.screenTable = screenTable;
   }

   public void addGridCellContainer(GridCellContainer cellContainer) {
      GridCellBounds recorder = cellContainer.getData(GridLayoutContainer.BOUNDS_RECORD_NAME);
      GridCellContainer container = cloneCellContainer(cellContainer);
      container.setScreenComponent(ScreenComponent.build(getScreenCanvas(), widgetSelectionUtil, cellContainer.getCell().getUiComponent()));
      container.setBounds(recorder.getBounds());
      container.layout();
      add(container);
      createDragSource(container);
      container.fillArea(btnInArea);
      widgetSelectionUtil.setSelectWidget(container);
      makeCellContainerResizable(cellWidth, cellHeight, container);
      layout();
   }
}
