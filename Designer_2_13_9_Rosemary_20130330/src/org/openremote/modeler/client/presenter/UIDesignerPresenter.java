/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.modeler.client.presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.event.UIElementEditedEvent;
import org.openremote.modeler.client.event.WidgetSelectedEvent;
import org.openremote.modeler.client.event.WidgetSelectedEventHandler;
import org.openremote.modeler.client.model.AutoSaveResponse;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.UtilsProxy;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.utils.TouchPanels;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.view.UIDesignerView;
import org.openremote.modeler.client.widget.uidesigner.AbsoluteLayoutContainer;
import org.openremote.modeler.client.widget.uidesigner.ComponentContainer;
import org.openremote.modeler.client.widget.uidesigner.GridLayoutContainerHandle;
import org.openremote.modeler.client.widget.uidesigner.ScreenTabItem;
import org.openremote.modeler.client.widget.uidesigner.UIDesignerToolbar;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.domain.BusinessEntity;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.PositionableAndSizable;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.component.UIGrid;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.InfoConfig;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public class UIDesignerPresenter implements Presenter, UIDesignerToolbar.Presenter {

  private EventBus eventBus;
  private UIDesignerView view;
  
  private ProfilePanelPresenter profilePanelPresenter;
  private TemplatePanelPresenter templatePanelPresenter;
  private ScreenPanelPresenter screenPanelPresenter;
  private PropertyPanelPresenter propertyPanelPresenter;
  private WidgetSelectionUtil widgetSelectionUtil;

  /** The auto_save_interval millisecond. */
  private static final int AUTO_SAVE_INTERVAL_MS = 300000;

  private Timer timer;

  public UIDesignerPresenter(EventBus eventBus, UIDesignerView view, WidgetSelectionUtil widgetSelectionUtil) {
    super();
    this.widgetSelectionUtil = widgetSelectionUtil;
    this.eventBus = eventBus;
    this.view = view;
    this.view.getToolbar().setPresenter(this);
    
    this.profilePanelPresenter = new ProfilePanelPresenter(eventBus, view.getProfilePanel());
    this.templatePanelPresenter = new TemplatePanelPresenter(eventBus, view.getTemplatePanel());
    this.screenPanelPresenter = new ScreenPanelPresenter(eventBus, widgetSelectionUtil, view.getScreenPanel());
    this.propertyPanelPresenter = new PropertyPanelPresenter(eventBus, widgetSelectionUtil, view.getPropertyPanel());
    
    AsyncServiceFactory.getUtilsRPCServiceAsync().getAccountPath(new AsyncSuccessCallback<String>() {
      public void onFailure(Throwable caught) {
        Info.display("Error", "failed to get account path.");
        super.checkTimeout(caught);
      }

      public void onSuccess(String result) {
        Cookies.setCookie(Constants.CURRETN_RESOURCE_PATH, result);
      }

    });

    prepareData();
    createAutoSaveTimer();
    
    bind();
  }

  private void bind() {
    eventBus.addHandler(WidgetSelectedEvent.TYPE, new WidgetSelectedEventHandler() {
      @Override
      public void onSelectionChanged(WidgetSelectedEvent event) {
        // Enable/disable toolbar buttons based on selection
        int numSelectedWidgets = (event.getSelectedWidgets() != null)?event.getSelectedWidgets().size():0;        
        view.getToolbar().getHorizontalLeftAlignButton().setEnabled(numSelectedWidgets >= 2);
        view.getToolbar().getHorizontalCenterAlignButton().setEnabled(numSelectedWidgets >= 2);
        view.getToolbar().getHorizontalRightAlignButton().setEnabled(numSelectedWidgets >= 2);
        view.getToolbar().getVerticalTopAlignButton().setEnabled(numSelectedWidgets >= 2);
        view.getToolbar().getVerticalCenterAlignButton().setEnabled(numSelectedWidgets >= 2);
        view.getToolbar().getVerticalBottomAlignButton().setEnabled(numSelectedWidgets >= 2);
        view.getToolbar().getSameSizeButton().setEnabled(numSelectedWidgets >= 2);
        view.getToolbar().getHorizontalSpreadButton().setEnabled(numSelectedWidgets >= 2);
        view.getToolbar().getVerticalSpreadButton().setEnabled(numSelectedWidgets >= 2);
        view.getToolbar().getHorizontalCenterButton().setEnabled(numSelectedWidgets >= 1);
        view.getToolbar().getVerticalCenterButton().setEnabled(numSelectedWidgets >= 1);
      }
    });
  }
  /**
   * Creates the auto save timer.
   */
  private void createAutoSaveTimer() {
    timer = new Timer() {
      @Override
      public void run() {
        autoSaveUiDesignerLayout();
      }
    };
    timer.scheduleRepeating(AUTO_SAVE_INTERVAL_MS);
  }

  /**
   * Auto save ui designer layout json.
   */
  public void autoSaveUiDesignerLayout() {
    if (view.getProfilePanel().isInitialized()) {
      UtilsProxy.autoSaveUiDesignerLayout(getAllPanels(), IDUtil.currentID(), new AsyncSuccessCallback<AutoSaveResponse>() {
        @Override
        public void onSuccess(AutoSaveResponse result) {
          if (result != null && result.isUpdated()) {
            Info.display("Info", "UI designer layout saved at " + DateTimeFormat.getFormat("HH:mm:ss").format(new Date()));
          }
          Window.setStatus("Auto-Saving: UI designer layout saved at: " + DateTimeFormat.getFormat("HH:mm:ss").format(new Date()));
        }

        @Override
        public void onFailure(Throwable caught) {
          timer.cancel();
          boolean timeout = super.checkTimeout(caught);
          if (!timeout) {
            Info.display(new InfoConfig("Error", caught.getMessage() + " " + DateTimeFormat.getFormat("HH:mm:ss").format(new Date())));
          }
          Window.setStatus("Failed to save UI designer layout at: " + DateTimeFormat.getFormat("HH:mm:ss").format(new Date()));
        }
      });
      Window.setStatus("Saving ....");
    } else {
      Window.setStatus("Auto-Saving: Unable to save UI designer because panel list has not been initialized. ");
    }
  }

  /**
   * Save ui designer layout, if the template panel is expanded, save its data, else save the profile panel's data.
   */
  public void saveUiDesignerLayout() {
     if (view.getTemplatePanel() != null && view.getTemplatePanel().isExpanded()) {
       view.getTemplatePanel().saveTemplateUpdates();
     } else {
        if (view.getProfilePanel().isInitialized()) {
           UtilsProxy.saveUiDesignerLayout(getAllPanels(), IDUtil.currentID(),
                 new AsyncSuccessCallback<AutoSaveResponse>() {
                    @Override
                    public void onSuccess(AutoSaveResponse result) {
                       if (result != null && result.isUpdated()) {
                          Info.display("Info", "UI designer layout saved at "
                                + DateTimeFormat.getFormat("HH:mm:ss").format(new Date()));
                       }
                       Window.setStatus("UI designer layout saved at: "
                             + DateTimeFormat.getFormat("HH:mm:ss").format(new Date()));
                    }
  
                    @Override
                    public void onFailure(Throwable caught) {
                       timer.cancel();
                       boolean timeout = super.checkTimeout(caught);
                       if (!timeout) {
                          Info.display(new InfoConfig("Error", caught.getMessage() + " "
                                + DateTimeFormat.getFormat("HH:mm:ss").format(new Date())));
                       }
                       Window.setStatus("Failed to save UI designer layout at: "
                             + DateTimeFormat.getFormat("HH:mm:ss").format(new Date()));
  
                    }
  
                 });
           Window.setStatus("Saving ....");
        } else {
           Window.setStatus("Unable to save UI designer because panel list has not been initialized. ");
        }
     }
  }

  /**
   * Load the touchPanels from server.
   */
  private void prepareData() {
    TouchPanels.load();
  }

  /**
   * Gets the all panels from panelTable.
   * 
   * @return the all panels
   */
  public List<Panel> getAllPanels() {
    List<Panel> panelList = new ArrayList<Panel>();
    for (BeanModel panelBeanModel : BeanModelDataBase.panelTable.loadAll()) {
      panelList.add((Panel) panelBeanModel.getBean());
    }
    return panelList;
  }
  
  // TODO EBR : see if possible to implement those function without code copy/paste
  // Maybe Visitor (+ Command) pattern
  
  public void onHorizontalLeftAlignButtonClicked() {
    int leftPosition = 0;
    if (widgetSelectionUtil.getSelectedWidgets().size() > 1) {
      ComponentContainer firstComponent = widgetSelectionUtil.getSelectedWidgets().get(0);
      if (firstComponent instanceof AbsoluteLayoutContainer) {
        leftPosition = ((AbsoluteLayoutContainer)firstComponent).getAbsolute().getLeft();
      } else if (firstComponent instanceof GridLayoutContainerHandle) {
        leftPosition = ((GridLayoutContainerHandle)firstComponent).getGridlayoutContainer().getGrid().getLeft();
      }
      for (ComponentContainer cc : widgetSelectionUtil.getSelectedWidgets()) {
        if (cc instanceof AbsoluteLayoutContainer) {
          Absolute absolute = ((AbsoluteLayoutContainer)cc).getAbsolute();
          absolute.setLeft(leftPosition);
          eventBus.fireEvent(new UIElementEditedEvent(absolute));
        } else if (cc instanceof GridLayoutContainerHandle) {        
          UIGrid grid = ((GridLayoutContainerHandle)cc).getGridlayoutContainer().getGrid();
          grid.setLeft(leftPosition);
          eventBus.fireEvent(new UIElementEditedEvent(grid));
        }
      }
    }
  }
  
  public void onHorizontalCenterAlignButtonClicked() {
    int middlePosition = 0;
    if (widgetSelectionUtil.getSelectedWidgets().size() > 1) {
      ComponentContainer firstComponent = widgetSelectionUtil.getSelectedWidgets().get(0);
      if (firstComponent instanceof AbsoluteLayoutContainer) {
        Absolute absolute = ((AbsoluteLayoutContainer)firstComponent).getAbsolute(); 
        middlePosition = absolute.getLeft() + absolute.getWidth() / 2;
      } else if (firstComponent instanceof GridLayoutContainerHandle) {
        UIGrid grid = ((GridLayoutContainerHandle)firstComponent).getGridlayoutContainer().getGrid();
        middlePosition = grid.getLeft() + grid.getWidth() / 2;
      }
      for (ComponentContainer cc : widgetSelectionUtil.getSelectedWidgets()) {
        if (cc instanceof AbsoluteLayoutContainer) {
          Absolute absolute = ((AbsoluteLayoutContainer)cc).getAbsolute();
          absolute.setLeft(middlePosition - absolute.getWidth() / 2);
          eventBus.fireEvent(new UIElementEditedEvent(absolute));
        } else if (cc instanceof GridLayoutContainerHandle) {
          UIGrid grid = ((GridLayoutContainerHandle)cc).getGridlayoutContainer().getGrid();
          grid.setLeft(middlePosition - (grid.getWidth() / 2));
          eventBus.fireEvent(new UIElementEditedEvent(grid));
        }
      }
    }
  }
  
  public void onHorizontalRightAlignButtonClicked() {
    int rightPosition = 0;
    if (widgetSelectionUtil.getSelectedWidgets().size() > 1) {
      ComponentContainer firstComponent = widgetSelectionUtil.getSelectedWidgets().get(0);
      if (firstComponent instanceof AbsoluteLayoutContainer) {
        Absolute absolute = ((AbsoluteLayoutContainer)firstComponent).getAbsolute(); 
        rightPosition = absolute.getLeft() + absolute.getWidth();
      } else if (firstComponent instanceof GridLayoutContainerHandle) {
        UIGrid grid = ((GridLayoutContainerHandle)firstComponent).getGridlayoutContainer().getGrid();
        rightPosition = grid.getLeft() + grid.getWidth();
      }
      for (ComponentContainer cc : widgetSelectionUtil.getSelectedWidgets()) {
        if (cc instanceof AbsoluteLayoutContainer) {
          Absolute absolute = ((AbsoluteLayoutContainer)cc).getAbsolute();
          absolute.setLeft(rightPosition - absolute.getWidth());
          eventBus.fireEvent(new UIElementEditedEvent(absolute));
        } else if (cc instanceof GridLayoutContainerHandle) {
          UIGrid grid = ((GridLayoutContainerHandle)cc).getGridlayoutContainer().getGrid();
          grid.setLeft(rightPosition - grid.getWidth());
          eventBus.fireEvent(new UIElementEditedEvent(grid));
        }
      }
    }
  }
  
  public void onVerticalTopAlignButtonClicked() {
    int topPosition = 0;
    if (widgetSelectionUtil.getSelectedWidgets().size() > 1) {
      ComponentContainer firstComponent = widgetSelectionUtil.getSelectedWidgets().get(0);
      if (firstComponent instanceof AbsoluteLayoutContainer) {
        topPosition = ((AbsoluteLayoutContainer)firstComponent).getAbsolute().getTop();
      } else if (firstComponent instanceof GridLayoutContainerHandle) {
        topPosition = ((GridLayoutContainerHandle)firstComponent).getGridlayoutContainer().getGrid().getTop();
      }
      for (ComponentContainer cc : widgetSelectionUtil.getSelectedWidgets()) {
        if (cc instanceof AbsoluteLayoutContainer) {
          Absolute absolute = ((AbsoluteLayoutContainer)cc).getAbsolute();
          absolute.setTop(topPosition);
          eventBus.fireEvent(new UIElementEditedEvent(absolute));
        } else if (cc instanceof GridLayoutContainerHandle) {
          UIGrid grid = ((GridLayoutContainerHandle)cc).getGridlayoutContainer().getGrid();
          grid.setTop(topPosition);
          eventBus.fireEvent(new UIElementEditedEvent(grid));
        }
      }
    }
  }
  
  public void onVerticalCenterAlignButtonClicked() {
    int middlePosition = 0;
    if (widgetSelectionUtil.getSelectedWidgets().size() > 1) {
      ComponentContainer firstComponent = widgetSelectionUtil.getSelectedWidgets().get(0);
      if (firstComponent instanceof AbsoluteLayoutContainer) {
        Absolute absolute = ((AbsoluteLayoutContainer)firstComponent).getAbsolute();
        middlePosition = absolute.getTop() + (absolute.getHeight() / 2);
      } else if (firstComponent instanceof GridLayoutContainerHandle) {
        UIGrid grid = ((GridLayoutContainerHandle)firstComponent).getGridlayoutContainer().getGrid();
        middlePosition = grid.getTop() + (grid.getHeight() / 2);
      }
      for (ComponentContainer cc : widgetSelectionUtil.getSelectedWidgets()) {
        if (cc instanceof AbsoluteLayoutContainer) {
          Absolute absolute = ((AbsoluteLayoutContainer)cc).getAbsolute();
          absolute.setTop(middlePosition - (absolute.getHeight() / 2));
          eventBus.fireEvent(new UIElementEditedEvent(absolute));
        } else if (cc instanceof GridLayoutContainerHandle) {        
          UIGrid grid = ((GridLayoutContainerHandle)cc).getGridlayoutContainer().getGrid();
          grid.setTop(middlePosition - (grid.getHeight() / 2));
          eventBus.fireEvent(new UIElementEditedEvent(grid));
        }
      }
    }
  }

  public void onVerticalBottomAlignButtonClicked() {
    int bottomPosition = 0;
    if (widgetSelectionUtil.getSelectedWidgets().size() > 1) {
      ComponentContainer firstComponent = widgetSelectionUtil.getSelectedWidgets().get(0);
      if (firstComponent instanceof AbsoluteLayoutContainer) {
        Absolute absolute = ((AbsoluteLayoutContainer)firstComponent).getAbsolute();
        bottomPosition = absolute.getTop() + absolute.getHeight();
      } else if (firstComponent instanceof GridLayoutContainerHandle) {
        UIGrid grid = ((GridLayoutContainerHandle)firstComponent).getGridlayoutContainer().getGrid();
        bottomPosition = grid.getTop() + grid.getHeight();
      }
      for (ComponentContainer cc : widgetSelectionUtil.getSelectedWidgets()) {
        if (cc instanceof AbsoluteLayoutContainer) {
          Absolute absolute = ((AbsoluteLayoutContainer)cc).getAbsolute();
          absolute.setTop(bottomPosition - absolute.getHeight());
          eventBus.fireEvent(new UIElementEditedEvent(absolute));
        } else if (cc instanceof GridLayoutContainerHandle) {        
          UIGrid grid = ((GridLayoutContainerHandle)cc).getGridlayoutContainer().getGrid();
          grid.setTop(bottomPosition - grid.getHeight());
          eventBus.fireEvent(new UIElementEditedEvent(grid));
        }
      }
    }
  }

  @Override
  public void onSameSizeButtonClicked() {
    int referenceWidth = 0, referenceHeight = 0;
    if (widgetSelectionUtil.getSelectedWidgets().size() > 1) {
      ComponentContainer firstComponent = widgetSelectionUtil.getSelectedWidgets().get(0);
      if (firstComponent instanceof AbsoluteLayoutContainer) {
        Absolute absolute = ((AbsoluteLayoutContainer)firstComponent).getAbsolute();
        referenceWidth = absolute.getWidth();
        referenceHeight = absolute.getHeight();
      } else if (firstComponent instanceof GridLayoutContainerHandle) {
        UIGrid grid = ((GridLayoutContainerHandle)firstComponent).getGridlayoutContainer().getGrid();
        referenceWidth = grid.getWidth();
        referenceHeight = grid.getHeight();
      }
      for (ComponentContainer cc : widgetSelectionUtil.getSelectedWidgets()) {
        if (cc instanceof AbsoluteLayoutContainer) {
          Absolute absolute = ((AbsoluteLayoutContainer)cc).getAbsolute();
          absolute.setWidth(referenceWidth);
          absolute.setHeight(referenceHeight);
          eventBus.fireEvent(new UIElementEditedEvent(absolute));
        } else if (cc instanceof GridLayoutContainerHandle) {        
          UIGrid grid = ((GridLayoutContainerHandle)cc).getGridlayoutContainer().getGrid();
          grid.setWidth(referenceWidth);
          grid.setHeight(referenceHeight);
          eventBus.fireEvent(new UIElementEditedEvent(grid));
        }
      }
    }
  }

  @Override
  public void onHorizontalSpreadButtonClicked() {
    if (widgetSelectionUtil.getSelectedWidgets().size() > 1) {
      PositionableAndSizable leftMost = null, rightMost = null;
      int leftBorder = Integer.MAX_VALUE, rightBorder = 0, totalWidth = 0;
      List<PositionableAndSizable> elementsToProcess = new ArrayList<PositionableAndSizable>();

      // On first iteration, search for left and right margin of the area we need to spread over
      for (ComponentContainer cc : widgetSelectionUtil.getSelectedWidgets()) {
        if (cc instanceof AbsoluteLayoutContainer) {
          Absolute absolute = ((AbsoluteLayoutContainer)cc).getAbsolute();
          if (absolute.getLeft() < leftBorder) {
            leftBorder = absolute.getLeft();
            leftMost = absolute;
          }
          if (absolute.getLeft() + absolute.getWidth() > rightBorder) {
            rightBorder = absolute.getLeft() + absolute.getWidth();
            rightMost = absolute;
          }
          totalWidth += absolute.getWidth();
          elementsToProcess.add(absolute);
        } else if (cc instanceof GridLayoutContainerHandle) {        
          UIGrid grid = ((GridLayoutContainerHandle)cc).getGridlayoutContainer().getGrid();
          if (grid.getLeft() < leftBorder) {
            leftBorder = grid.getLeft();
            leftMost = grid;
          }
          if (grid.getLeft() + grid.getWidth() > rightBorder) {
            rightBorder = grid.getLeft() + grid.getWidth();
            rightMost = grid;
          }
          totalWidth += grid.getWidth();
          elementsToProcess.add(grid);
        }
      }

      // Don't move the border elements
      elementsToProcess.remove(leftMost);
      elementsToProcess.remove(rightMost);
      
      // Sort elements from left to right position
      Collections.sort(elementsToProcess, new Comparator<PositionableAndSizable>() {
        @Override
        public int compare(PositionableAndSizable o1, PositionableAndSizable o2) {
          return o1.getLeft() - o2.getLeft();
        }        
      });
      
      if (totalWidth < rightBorder - leftBorder) {
        // There is enough space to fit all elements, use constant spacing between elements

        float spacing = (float)(rightBorder - leftBorder - totalWidth) / (elementsToProcess.size() + 1);
        float currentLeftPosition = leftMost.getLeft() + leftMost.getWidth() + spacing;
        for (PositionableAndSizable pas : elementsToProcess) {
          pas.setLeft((int)currentLeftPosition);
          currentLeftPosition += pas.getWidth() + spacing;
          eventBus.fireEvent(new UIElementEditedEvent((BusinessEntity)pas));
        }
      } else {
        // Fallback to other algorithm, distribute all elements between left and right most one on their center axis 

        // Spacing between the center axis of all elements to be moved
        int centerSpacing = (rightBorder - leftBorder) / (elementsToProcess.size() + 1);
        int index = 1;
        for (PositionableAndSizable pas : elementsToProcess) {
          pas.setLeft(leftBorder + (index * centerSpacing) - (pas.getWidth() / 2));
          eventBus.fireEvent(new UIElementEditedEvent((BusinessEntity)pas));
          index++;
        }
      }
    }    
  }

  @Override
  public void onVerticalSpreadButtonClicked() {
    if (widgetSelectionUtil.getSelectedWidgets().size() > 1) {
      PositionableAndSizable topMost = null, bottomMost = null;
      int topBorder = Integer.MAX_VALUE, bottomBorder = 0, totalHeight = 0;
      List<PositionableAndSizable> elementsToProcess = new ArrayList<PositionableAndSizable>();

      // On first iteration, search for top and bottom margin of the area we need to spread over
      for (ComponentContainer cc : widgetSelectionUtil.getSelectedWidgets()) {
        if (cc instanceof AbsoluteLayoutContainer) {
          Absolute absolute = ((AbsoluteLayoutContainer)cc).getAbsolute();
          if (absolute.getTop() < topBorder) {
            topBorder = absolute.getTop();
            topMost = absolute;
          }
          if (absolute.getTop() + absolute.getHeight() > bottomBorder) {
            bottomBorder = absolute.getTop() + absolute.getHeight();
            bottomMost = absolute;
          }
          totalHeight += absolute.getHeight();
          elementsToProcess.add(absolute);
        } else if (cc instanceof GridLayoutContainerHandle) {        
          UIGrid grid = ((GridLayoutContainerHandle)cc).getGridlayoutContainer().getGrid();
          if (grid.getTop() < topBorder) {
            topBorder = grid.getTop();
            topMost = grid;
          }
          if (grid.getTop() + grid.getHeight() > bottomBorder) {
            bottomBorder = grid.getTop() + grid.getHeight();
            bottomMost = grid;
          }
          totalHeight += grid.getHeight();
          elementsToProcess.add(grid);
        }
      }

      // Don't move the border elements
      elementsToProcess.remove(topMost);
      elementsToProcess.remove(bottomMost);
      
      // Sort elements from top to bottom position
      Collections.sort(elementsToProcess, new Comparator<PositionableAndSizable>() {
        @Override
        public int compare(PositionableAndSizable o1, PositionableAndSizable o2) {
          return o1.getTop() - o2.getTop();
        }        
      });
      
      if (totalHeight < bottomBorder - topBorder) {
        // There is enough space to fit all elements, use constant spacing between elements

        float spacing = (float)(bottomBorder - topBorder - totalHeight) / (elementsToProcess.size() + 1);
        float currentTopPosition = topMost.getTop() + topMost.getHeight() + spacing;
        for (PositionableAndSizable pas : elementsToProcess) {
          pas.setTop((int)currentTopPosition);
          currentTopPosition += pas.getHeight() + spacing;
          eventBus.fireEvent(new UIElementEditedEvent((BusinessEntity)pas));
        }
      } else {
        // Fallback to other algorithm, distribute all elements between left and right most one on their center axis 

        // Spacing between the center axis of all elements to be moved
        int centerSpacing = (bottomBorder - topBorder) / (elementsToProcess.size() + 1);
        int index = 1;
        for (PositionableAndSizable pas : elementsToProcess) {
          pas.setTop(topBorder + (index * centerSpacing) - (pas.getHeight() / 2));
          eventBus.fireEvent(new UIElementEditedEvent((BusinessEntity)pas));
          index++;
        }
      }
    }    
  }

  @Override
  public void onHorizontalCenterButtonClicked() {
    if (!widgetSelectionUtil.getSelectedWidgets().isEmpty()) {
      int leftBorder = Integer.MAX_VALUE, rightBorder = 0, totalWidth = 0;
      List<PositionableAndSizable> elementsToProcess = new ArrayList<PositionableAndSizable>();

      // On first iteration, search for left and right margin of the area we need to spread over
      for (ComponentContainer cc : widgetSelectionUtil.getSelectedWidgets()) {
        if (cc instanceof AbsoluteLayoutContainer) {
          Absolute absolute = ((AbsoluteLayoutContainer)cc).getAbsolute();
          if (absolute.getLeft() < leftBorder) {
            leftBorder = absolute.getLeft();
          }
          if (absolute.getLeft() + absolute.getWidth() > rightBorder) {
            rightBorder = absolute.getLeft() + absolute.getWidth();
          }
          totalWidth += absolute.getWidth();
          elementsToProcess.add(absolute);
        } else if (cc instanceof GridLayoutContainerHandle) {        
          UIGrid grid = ((GridLayoutContainerHandle)cc).getGridlayoutContainer().getGrid();
          if (grid.getLeft() < leftBorder) {
            leftBorder = grid.getLeft();
          }
          if (grid.getLeft() + grid.getWidth() > rightBorder) {
            rightBorder = grid.getLeft() + grid.getWidth();
          }
          totalWidth += grid.getWidth();
          elementsToProcess.add(grid);
        }
      }
      int totalMargin = ((ScreenTabItem)this.view.getScreenPanel().getScreenItem().getSelectedItem()).getScreen().getTouchPanelDefinition().getCanvas().getWidth() - (rightBorder - leftBorder);
      int offset = (totalMargin / 2) - leftBorder;
      for (PositionableAndSizable pas : elementsToProcess) {
        pas.setLeft(pas.getLeft() + offset);
        eventBus.fireEvent(new UIElementEditedEvent((BusinessEntity)pas));
      }
    }    
  }

  @Override
  public void onVerticalCenterButtonClicked() {
    if (!widgetSelectionUtil.getSelectedWidgets().isEmpty()) {
      int topBorder = Integer.MAX_VALUE, bottomBorder = 0, totalHeight = 0;
      List<PositionableAndSizable> elementsToProcess = new ArrayList<PositionableAndSizable>();

      // On first iteration, search for left and right margin of the area we need to spread over
      for (ComponentContainer cc : widgetSelectionUtil.getSelectedWidgets()) {
        if (cc instanceof AbsoluteLayoutContainer) {
          Absolute absolute = ((AbsoluteLayoutContainer)cc).getAbsolute();
          if (absolute.getTop() < topBorder) {
            topBorder = absolute.getTop();
          }
          if (absolute.getTop() + absolute.getHeight() > bottomBorder) {
            bottomBorder = absolute.getTop() + absolute.getHeight();
          }
          totalHeight += absolute.getHeight();
          elementsToProcess.add(absolute);
        } else if (cc instanceof GridLayoutContainerHandle) {        
          UIGrid grid = ((GridLayoutContainerHandle)cc).getGridlayoutContainer().getGrid();
          if (grid.getTop() < topBorder) {
            topBorder = grid.getTop();
          }
          if (grid.getTop() + grid.getHeight() > bottomBorder) {
            bottomBorder = grid.getTop() + grid.getHeight();
          }
          totalHeight += grid.getHeight();
          elementsToProcess.add(grid);
        }
      }
      
      Screen screen = ((ScreenTabItem)this.view.getScreenPanel().getScreenItem().getSelectedItem()).getScreen();
      boolean hasTabBar = ((this.view.getScreenPanel().getScreenItem().getScreenPair().getParentGroup().getTabbar() != null)
                          || (this.view.getScreenPanel().getScreenItem().getScreenPair().getParentGroup().getParentPanel().getTabbar() != null));
      int totalMargin = screen.getTouchPanelDefinition().getCanvas().getHeight()
                          - (hasTabBar?screen.getTouchPanelDefinition().getTabbarDefinition().getHeight():0)
                          - (bottomBorder - topBorder);      
      int offset = (totalMargin / 2) - topBorder;
      for (PositionableAndSizable pas : elementsToProcess) {
        pas.setTop(pas.getTop() + offset);
        eventBus.fireEvent(new UIElementEditedEvent((BusinessEntity)pas));
      }
    }    
  }
  
}
