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
package org.openremote.modeler.client.widget.component;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.event.WidgetDeleteEvent;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.propertyform.PropertyForm;
import org.openremote.modeler.client.widget.propertyform.TabbarPropertyForm;
import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.UITabbar;
import org.openremote.modeler.domain.component.UITabbarItem;
import org.openremote.modeler.domain.component.UITabbar.Scope;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DragEvent;
import com.extjs.gxt.ui.client.event.DragListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.fx.Draggable;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteLayout;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * This class is to manage screen tabbar items.
 * The tabbar includes two types: panel and group.
 * 
 * @author javen
 *
 */
public class ScreenTabbar extends ScreenComponent {
  
   public static final int PADDING = 5;
   
   private int defaultHeight = 44;
   
   private List<ScreenTabbarItem> screenTabbarItems = new ArrayList<ScreenTabbarItem>();
   
   private FlexTable tabItemContainer = new FlexTable();
   
   private UITabbar uiTabbar = null;
   
   private LayoutContainer moveBackGround = null;
 
   public ScreenTabbar(ScreenCanvas screenCanvas, WidgetSelectionUtil widgetSelectionUtil) {
      super(screenCanvas, widgetSelectionUtil);
   }

   /**
    * Instantiates a new screen tabbar by the uiTabbar and screenCanvas.
    * Use the background which defined in the tabbarDefinition.
    * 
    * @param screenCanvas the screen canvas
    * @param uiTabbar the ui tabbar
    */
   public ScreenTabbar(ScreenCanvas screenCanvas, UITabbar uiTabbar, WidgetSelectionUtil widgetSelectionUtil){
      super(screenCanvas, widgetSelectionUtil);
      this.widgetSelectionUtil = widgetSelectionUtil;
      this.uiTabbar = uiTabbar;
      
      setToGroup();
      Screen screen = this.getScreenCanvas().getScreen();
      defaultHeight = screen.getTouchPanelDefinition().getTabbarDefinition().getHeight();
      
      setHeight(defaultHeight);
      ImageSource bgImage = screen.getTouchPanelDefinition().getTabbarDefinition().getBackground();
      setWidth(screen.getTouchPanelDefinition().getCanvas().getWidth());
      if (bgImage != null && !bgImage.isEmpty()) {
         this.setStyleAttribute("backgroundImage", "url("+screen.getTouchPanelDefinition().getTabbarDefinition().getBackground().getSrc()+");repeat-x");
      } else {
         addStyleName("tabbar-background");
      }
      
      setPosition(0, screen.getTouchPanelDefinition().getCanvas().getHeight() - (defaultHeight));
      tabItemContainer.setSize(screen.getTouchPanelDefinition().getCanvas().getWidth()+"", defaultHeight+"");
      add(tabItemContainer);
      setStyleAttribute("position", "absolute");
      initTabbar();
      addDeleteListener();
   }

   /**
    * The drag line is a narrow yellow frame when the tababar item is dragging.
    */
   private void addDragLine() {
      this.moveBackGround = new LayoutContainer(){
         @Override
         protected void afterRender() {
            super.afterRender();
            super.el().updateZIndex(1);
         }
      };
      moveBackGround.addStyleName("move-background");
      moveBackGround.setSize("1px", defaultHeight+"px");
      moveBackGround.setStyleAttribute("position", "absolute");
      moveBackGround.setPosition(0, 0);
      moveBackGround.hide();
      add(moveBackGround);
   }
   @Override
   public String getName() {
      return "Tabbar";
   }

   @Override
   public PropertyForm getPropertiesForm() {
      return new TabbarPropertyForm(this, widgetSelectionUtil);
   }
   
   @Override
   public void onComponentEvent(ComponentEvent ce) {
      if (ce.getEventTypeInt() == Event.ONMOUSEDOWN) {
         widgetSelectionUtil.setSelectWidget(this);
      }
      ce.cancelBubble();
      super.onComponentEvent(ce);
   }
   @Override
   protected void afterRender() {
      super.afterRender();
      super.el().updateZIndex(1);
   }
   public List<ScreenTabbarItem> getScreenTabbarItems() {
      return screenTabbarItems;
   }

   public void setScreenTabbarItems(List<ScreenTabbarItem> screenTabbarItems) {
      this.screenTabbarItems = screenTabbarItems;
   }

   public void setToGroup() {
      uiTabbar.setScope(Scope.GROUP);
      
      //1, remove tabbar from panel:
      Panel panel = getScreenCanvas().getScreen().getScreenPair().getParentGroup().getParentPanel();
      if (panel.getTabbar()==this.uiTabbar){
         panel.setTabbar(null);
      }
      
      //2, add tabbar to group
      Group group = getScreenCanvas().getScreen().getScreenPair().getParentGroup();
      group.setTabbar(this.uiTabbar);
   }
   
   public void setToPanel() {
      uiTabbar.setScope(Scope.PANEL);
      //1, remove tabbar from group
      Group group = getScreenCanvas().getScreen().getScreenPair().getParentGroup();
      if (group.getTabbar() == this.uiTabbar){
         group.setTabbar(null);
      }
      //2, add tabbar from panel:
      Panel panel = getScreenCanvas().getScreen().getScreenPair().getParentGroup().getParentPanel();
      panel.setTabbar(this.uiTabbar);
   }
   
   public boolean isPanelScope() {
      return uiTabbar.getScope() == Scope.PANEL;
   }
   
   public boolean isGroupScope() {
      return uiTabbar.getScope() == Scope.GROUP;
   }
   
   public int getTabbarItemCount() {
      return uiTabbar.getTabbarItems().size();
   }
   
   public void addTabbarItem(final UITabbarItem uiTabbarItem) {
      this.uiTabbar.addTabbarItem(uiTabbarItem);
      
      final ScreenTabbarItem screenTabbarItem = new ScreenTabbarItem(this.getScreenCanvas(),uiTabbarItem, widgetSelectionUtil);
      this.getScreenTabbarItems().add(screenTabbarItem);
      this.initTabbar();
      widgetSelectionUtil.setSelectWidget(screenTabbarItem);
   }
   
   private void initTabbar() {
      int tabbarItemNumber = getTabbarItemCount();
      int index = 0;
      if (tabbarItemNumber >0 ) {
         int width = (getScreenCanvas().getScreen().getTouchPanelDefinition().getCanvas().getWidth()-2*PADDING)/tabbarItemNumber;
         this.screenTabbarItems.removeAll(screenTabbarItems);
         for (UITabbarItem uiTabbarItem : this.uiTabbar.getTabbarItems()) {
            ScreenTabbarItem screenTabbarItem = new ScreenTabbarItem(this.getScreenCanvas(),uiTabbarItem, widgetSelectionUtil);
            this.getScreenTabbarItems().add(screenTabbarItem);
            this.addDeleteListenerToTabItem(screenTabbarItem);
            makeTabItemDragable(screenTabbarItem);
         }
         this.removeAll();
         this.setLayout(new AbsoluteLayout());
         for(ScreenTabbarItem item : getScreenTabbarItems()) {
            item.setWidth(width);
            item.setHeight(defaultHeight);
            item.setPosition(index*width+PADDING, 0);
            if ("Tab Bar Item".equals(item.getName())) {
               item.setName("Item");
            }
            add(item);
            index++;
         }
         this.addDragLine();
      }
      this.getScreenCanvas().layout();
   }
   
   private void makeTabItemDragable(final ScreenTabbarItem tabItem) {
      
      Draggable draggable = new Draggable(tabItem);
      draggable.setConstrainVertical(true);
      draggable.setContainer(this);
      draggable.setUseProxy(false);
      draggable.addDragListener(new DragListener() {

         @Override
         public void dragEnd(DragEvent de) {
            super.dragEnd(de);
            int index = getOrder(de.getClientX() - getScreenCanvas().getAbsoluteLeft()-PADDING);
            ScreenTabbarItem screenTabarItem = (ScreenTabbarItem) de.getComponent();
            screenTabarItem.removeFromParent();
            ScreenTabbar.this.uiTabbar.removeTabarItem(screenTabarItem.getUITabbarIem());
            ScreenTabbar.this.uiTabbar.insertTabbarItem(index, screenTabarItem.getUITabbarIem());
            ScreenTabbar.this.initTabbar();
            moveBackGround.hide();
         }

         @Override
         public void dragStart(DragEvent de) {
            super.dragStart(de);
            tabItem.hide();
            moveBackGround.setPosition(0, 0);
            moveBackGround.show();
         }

         @Override
         public void dragMove(DragEvent de) {
            super.dragMove(de);
            moveBackGround.setPosition(de.getClientX() - getScreenCanvas().getAbsoluteLeft()-PADDING, 0);
         }
         
         
         
      });
   }
   
   private void addDeleteListener() {
      addListener(WidgetDeleteEvent.WIDGETDELETE, new Listener<WidgetDeleteEvent>() {
         public void handleEvent(WidgetDeleteEvent be) {
            removeItself();
            getScreenCanvas().removeTabbar();
            getScreenCanvas().initTabbarContainer();
         }
      });
   }
   
   private void removeItself () {
      Group group = this.getScreenCanvas().getScreen().getScreenPair().getParentGroup();
      Panel panel = group.getParentPanel();
      if (UITabbar.Scope.GROUP == uiTabbar.getScope()) {
         group.setTabbar(null);
      } else {
         panel.setTabbar(null);
      }
      for (ScreenTabbarItem item : this.getScreenTabbarItems()) {
         item.removeFromParent();
      }
      uiTabbar.removeAll();
//      this.removeFromParent();
   }
   
   private int getOrder(int xPosition) {
      int result = 0;
      int tabbarItemCouont = getTabbarItemCount();
      if (tabbarItemCouont > 0) {
         int tabbarWidth = getScreenCanvas().getScreen().getTouchPanelDefinition().getCanvas().getWidth() - (2 * PADDING);
         int tabbarItemWidth = tabbarWidth / tabbarItemCouont;
         if (xPosition >= 0 && xPosition <= tabbarWidth){
            result = xPosition / tabbarItemWidth;
         }
      }
      return result;
   }
   
   private void addDeleteListenerToTabItem(final ScreenTabbarItem screenTabbarItem) {
      screenTabbarItem.addListener(WidgetDeleteEvent.WIDGETDELETE, new Listener<WidgetDeleteEvent>() {
         public void handleEvent(WidgetDeleteEvent be) {
            uiTabbar.removeTabarItem(screenTabbarItem.getUITabbarIem());
            screenTabbarItem.removeFromParent();
            initTabbar();
         }
         
      });
   }
}
