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

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.event.ScreenSelectedEvent;
import org.openremote.modeler.client.event.ScreenSelectedEventHandler;
import org.openremote.modeler.client.event.ScreenTableLoadedEvent;
import org.openremote.modeler.client.event.ScreenTableLoadedEventHandler;
import org.openremote.modeler.client.event.TemplateSelectedEvent;
import org.openremote.modeler.client.event.TemplateSelectedEventHandler;
import org.openremote.modeler.client.event.UIElementEditedEvent;
import org.openremote.modeler.client.event.UIElementEditedEventHandler;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.utils.BeanModelTable;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.uidesigner.AbsoluteLayoutContainer;
import org.openremote.modeler.client.widget.uidesigner.ComponentContainer;
import org.openremote.modeler.client.widget.uidesigner.GridLayoutContainerHandle;
import org.openremote.modeler.client.widget.uidesigner.ScreenPanel;
import org.openremote.modeler.client.widget.uidesigner.ScreenTab;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPairRef;
import org.openremote.modeler.domain.component.UIGrid;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.google.gwt.event.shared.EventBus;

public class ScreenPanelPresenter implements Presenter, ScreenPanel.Presenter {

  private EventBus eventBus;
  private WidgetSelectionUtil widgetSelectionUtil;
  private ScreenPanel view;
  
  public ScreenPanelPresenter(EventBus eventBus, WidgetSelectionUtil widgetSelectionUtil, ScreenPanel view) {
    super();
    this.eventBus = eventBus;
    this.widgetSelectionUtil = widgetSelectionUtil;
    this.view = view;
    view.setPresenter(this);
    
    bind();
  }

  private void bind() {
    eventBus.addHandler(ScreenSelectedEvent.TYPE, new ScreenSelectedEventHandler() {
      @Override
      public void onScreenSelected(ScreenSelectedEvent event) {
        screenSelected(event.getSelectedScreenPair());
      }
    });
    
    eventBus.addHandler(ScreenTableLoadedEvent.TYPE, new ScreenTableLoadedEventHandler() {
      @Override
      public void onScreenTableLoaded(ScreenTableLoadedEvent event) {
        BeanModelDataBase.screenTable.setInsertListener(Constants.SCREEN_TABLE_OID, new ChangeListener() {
          public void modelChanged(ChangeEvent event) {
            if (event.getType() == BeanModelTable.ADD) {
              BeanModel beanModel = (BeanModel) event.getItem();
              if (beanModel.getBean() instanceof ScreenPair) {
                ScreenPanelPresenter.this.view.setScreenItem(new ScreenTab((ScreenPair) beanModel.getBean(), widgetSelectionUtil));
              }
            }
          }
        });
      }
    });
    
    eventBus.addHandler(TemplateSelectedEvent.TYPE, new TemplateSelectedEventHandler() {
      @Override
      public void onTemplateSelected(TemplateSelectedEvent event) {
        if (event.getTemplate() != null) {
          view.setScreenItem(new ScreenTab(event.getTemplate().getScreen(), widgetSelectionUtil));
        } else {
//          templateEditPanel.remove(editTabItem);// TODO EBR : this is not done anymore, but was it really required, below call should be enough
          view.closeCurrentScreenTab();
        }
      }
    });
    
    eventBus.addHandler(UIElementEditedEvent.TYPE, new UIElementEditedEventHandler() {      
      @Override
      public void onElementEdited(UIElementEditedEvent event) {
        view.onUIElementEdited(event.getElement());
      }
    });
  }
  
  private void screenSelected(ScreenPairRef screenPairRef) {
    ScreenPair screen = screenPairRef.getScreen();
    screen.setTouchPanelDefinition(screenPairRef.getTouchPanelDefinition());
    screen.setParentGroup(screenPairRef.getGroup());
    ScreenTab screenTabItem = this.view.getScreenItem();
    if (screenTabItem != null) {
      if (screen == screenTabItem.getScreenPair()) {
        screenTabItem.updateTouchPanel();
        screenTabItem.updateTabbarForScreenCanvas(screenPairRef);
      } else {
        screenTabItem = new ScreenTab(screen, widgetSelectionUtil);
        screenTabItem.updateTabbarForScreenCanvas(screenPairRef);
        this.view.setScreenItem(screenTabItem);
      }
    } else {
      screenTabItem = new ScreenTab(screen, widgetSelectionUtil);
      screenTabItem.updateTabbarForScreenCanvas(screenPairRef);
      this.view.setScreenItem(screenTabItem);
    }
    screenTabItem.updateScreenIndicator();
  }
  
  // Use arrow keys for moving selection
  public void onRightKeyPressed() {
    moveWidgetSelection((view.isShiftKeyDown()?10:1), 0);
  }
  
  public void onLeftKeyPressed() {
    moveWidgetSelection(-(view.isShiftKeyDown()?10:1), 0);
  }
  
  public void onUpKeyPressed() {
    moveWidgetSelection(0, -(view.isShiftKeyDown()?10:1));
  }
  
  public void onDownKeyPressed() {
    moveWidgetSelection(0, (view.isShiftKeyDown()?10:1));
  }         

  private void moveWidgetSelection(int left, int top) {
    for (ComponentContainer cc : widgetSelectionUtil.getSelectedWidgets()) {
       if (cc instanceof AbsoluteLayoutContainer) {
         Absolute absolute = ((AbsoluteLayoutContainer)cc).getAbsolute();
         absolute.setLeft(absolute.getLeft() + left);
         absolute.setTop(absolute.getTop() + top);
         eventBus.fireEvent(new UIElementEditedEvent(absolute));
       } else if (cc instanceof GridLayoutContainerHandle) {        
         UIGrid grid = ((GridLayoutContainerHandle)cc).getGridlayoutContainer().getGrid();
         grid.setLeft(grid.getLeft() + left);
         grid.setTop(grid.getTop() + top);
         eventBus.fireEvent(new UIElementEditedEvent(grid));
       }          
     }
  }
  
}
