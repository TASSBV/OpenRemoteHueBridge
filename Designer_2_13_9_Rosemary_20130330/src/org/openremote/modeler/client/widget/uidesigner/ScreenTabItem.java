/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2012, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.modeler.client.widget.uidesigner;

import java.util.List;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.component.ScreenTabbar;
import org.openremote.modeler.domain.BusinessEntity;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenPairRef;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.user.client.Event;

/**
 * The Class ScreenTabItem contain a screenPanel.
 */
public class ScreenTabItem extends TabItem {

   /** The screen. */
   private Screen screen;

   private ComponentContainer screenContainer;
   
   private ScreenCanvas screenCanvas;
   private LayoutContainer dropTarget;
   
   private WidgetSelectionUtil widgetSelectionUtil;

   /**
    * Instantiates a new screen panel.
    * 
    * @param screen
    *           the s
    */
   public ScreenTabItem(Screen screen, WidgetSelectionUtil widgetSelectionUtil) {
      this.screen = screen;
      this.widgetSelectionUtil = widgetSelectionUtil;
      if (screen.isLandscape()) {
         setText(Constants.LANDSCAPE);
         setItemId(Constants.LANDSCAPE);
      } else {
         setText(Constants.PORTRAIT);
         setItemId(Constants.PORTRAIT);
      }
//      setClosable(true);
      setLayout(new FlowLayout());
      setScrollMode(Scroll.AUTO);
      addScreenContainer();
      layout();
   }

   /**
    * Adds the screen container.
    */
   private void addScreenContainer() {
      screenContainer = new ComponentContainer(widgetSelectionUtil){ 
         @Override
         public void onComponentEvent(ComponentEvent ce) {
            super.onComponentEvent(ce);
//            if (ce.getEventTypeInt() == Event.ONMOUSEDOWN) {
//               WidgetSelectionUtil.setSelectWidget(this);
//            }
         }
      };
      screenContainer.addStyleName("screen-background");
      screenContainer.sinkEvents(Event.ONMOUSEDOWN);
      dropTarget = new LayoutContainer();
      dropTarget.setStyleAttribute("padding", "5px");
      screenCanvas = new ScreenCanvas(screen, dropTarget, widgetSelectionUtil);
      dropTarget.add(screenCanvas);
      updateTouchPanel();
      initTabbarForScreenCanvas();
      updateScreenIndicator();
      screenContainer.add(dropTarget);
//      screenContainer.setStyleAttribute("border", "1px dashed gray");
      add(screenContainer);
   }

   /**
    * @param screenContainer
    * @param touchPanelDefinition
    */
   public void updateTouchPanel() {
      TouchPanelDefinition touchPanelDefinition = screen.getTouchPanelDefinition();
      if (touchPanelDefinition.getWidth() == 0 || touchPanelDefinition.getHeight() == 0) {
         touchPanelDefinition.setWidth(touchPanelDefinition.getCanvas().getWidth() + 20);
         touchPanelDefinition.setHeight(touchPanelDefinition.getCanvas().getHeight() + 20);
      }
      screenContainer.setSize(touchPanelDefinition.getWidth(), touchPanelDefinition.getHeight());
      if (touchPanelDefinition.getBgImage() != null) {
         screenContainer.setStyleAttribute("backgroundImage", "url(" + touchPanelDefinition.getBgImage() + ")");
      }
      screenContainer.setStyleAttribute("paddingLeft", String.valueOf(touchPanelDefinition.getPaddingLeft() - 5) + "px");
      screenContainer.setStyleAttribute("paddingTop", String.valueOf(touchPanelDefinition.getPaddingTop() -5) + "px");
      int width = touchPanelDefinition.getCanvas().getWidth();
      int height = touchPanelDefinition.getCanvas().getHeight();
      if (dropTarget != null) {
         dropTarget.setSize(width + 10, height + 10);
      }
      if (screenCanvas != null) {
         screenCanvas.setSize(width, height);
      }
   }

   /**
    * Gets the screen.
    * 
    * @return the screen
    */
   public Screen getScreen() {
      return screen;
   }

   public ScreenCanvas getScreenCanvas() {
      return screenCanvas;
   }

   public void updateTabbarForScreenCanvas(ScreenPairRef screenRef) {
      Group screenGroup = screenRef.getGroup();
      if (screenGroup != null) {
         Panel groupPanel = screenGroup.getParentPanel();
         ScreenTabbar tabbar = null;
         if (screenGroup.getTabbar() != null) {
            tabbar = new ScreenTabbar(screenCanvas,screenGroup.getTabbar(), widgetSelectionUtil);
         } else if (groupPanel != null && groupPanel.getTabbar()!= null) {
            tabbar = new ScreenTabbar(screenCanvas,groupPanel.getTabbar(), widgetSelectionUtil);
            tabbar.setToPanel();
         }
         
         if (tabbar != null) {
            screenCanvas.addTabbar(tabbar);
         } else {
            screenCanvas.removeTabbar();
         }
      }
   }
   
   private void initTabbarForScreenCanvas() {
      if (screen != null) {
         Group screenGroup = screen.getScreenPair().getParentGroup();
         if (screenGroup != null) {
            Panel groupPanel = screenGroup.getParentPanel();
            ScreenTabbar tabbar = null;
            if (screenGroup.getTabbar() != null) {
               tabbar = new ScreenTabbar(screenCanvas,screenGroup.getTabbar(), widgetSelectionUtil);
            } else if (groupPanel != null && groupPanel.getTabbar()!= null) {
               tabbar = new ScreenTabbar(screenCanvas,groupPanel.getTabbar(), widgetSelectionUtil);
               tabbar.setToPanel();
            }
            
            if (tabbar != null) {
               screenCanvas.addTabbar(tabbar);
            }
         }
      }
   }
   
   public void updateScreenIndicator() {
      Group screenGroup = screen.getScreenPair().getParentGroup();
      if (screenGroup != null) {
         if (screen.isLandscape()) {
            List<Screen> landscapeScreens = screenGroup.getLandscapeScreens();
            screenCanvas.updateScreenIndicator(landscapeScreens.size(), landscapeScreens.indexOf(screen));
         } else {
            List<Screen> portraitScreens = screenGroup.getPortraitScreens();
            screenCanvas.updateScreenIndicator(portraitScreens.size(), portraitScreens.indexOf(screen));
         }
      }
   }
   
   public void onUIElementEdited(BusinessEntity element) {
     screenCanvas.onUIElementEdited(element);
   }

}
