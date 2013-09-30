/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.modeler.client.view;

import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.uidesigner.ProfilePanel;
import org.openremote.modeler.client.widget.uidesigner.PropertyPanel;
import org.openremote.modeler.client.widget.uidesigner.ScreenPanelImpl;
import org.openremote.modeler.client.widget.uidesigner.TemplatePanelImpl;
import org.openremote.modeler.client.widget.uidesigner.UIDesignerToolbar;
import org.openremote.modeler.client.widget.uidesigner.UIDesignerToolbarImpl;
import org.openremote.modeler.client.widget.uidesigner.WidgetPanel;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;

/**
 * The class is for initializing the ui designer view.
 * 
 * It use border layout, includes profile panel and template panel 
 * in the west, screen panel in the center, widget panel and property
 * panel in the east.
 */
public class UIDesignerView extends TabItem {
  
   /** The screen panel is for DND widget in it. */
   private ScreenPanelImpl screenPanel;

   private ProfilePanel profilePanel = null;
   
   private TemplatePanelImpl templatePanel = null;
   
   private PropertyPanel propertyPanel = null;
   
   private UIDesignerToolbarImpl toolbar;
   
   private WidgetSelectionUtil widgetSelectionUtil;
   
   /**
    * Instantiates a new uI designer view.
    */
   public UIDesignerView(WidgetSelectionUtil widgetSelectionUtil) {
      super();
      this.widgetSelectionUtil = widgetSelectionUtil;
      screenPanel = new ScreenPanelImpl(widgetSelectionUtil);
      setText("UI Designer");
      setLayout(new BorderLayout());
      profilePanel = createWest();
      createCenter();
      createEast();
   }
   
   /**
    * Creates the east part of the view.
    * It includes widget and property panels.
    */
   private void createEast() {
      BorderLayoutData eastLayout = new BorderLayoutData(LayoutRegion.EAST, 300);
      eastLayout.setSplit(true);
      eastLayout.setMargins(new Margins(0, 2, 0, 2));
      add(createWidgetAndPropertyContainer(), eastLayout);
   }

   /**
    * Creates the west part of the view.
    * It includes profilePanel and templatePanel.
    */
   private ProfilePanel createWest() {
      ContentPanel west = new ContentPanel();
      ProfilePanel result = new ProfilePanel();

      templatePanel = new TemplatePanelImpl();
      BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 200);
      westData.setSplit(true);
      west.setLayout(new AccordionLayout());
      west.setBodyBorder(false);
      west.setHeaderVisible(false);
      west.add(result);
      west.add(templatePanel);
      westData.setMargins(new Margins(0, 2, 0, 0));
      add(west, westData);
      return result;
   }

   /**
    * Refresh the profile panel tree.
    */
   public void refreshPanelTree() {
      if (profilePanel != null) {
         profilePanel.layout();
      }
   }

   /**
    * Creates the center part of the view.
    * It includes the sceenPanel.
    */
   private void createCenter() {
      BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
      centerData.setMargins(new Margins(0, 2, 0, 2));
      
      LayoutContainer centerContainer = new LayoutContainer();
      centerContainer.setLayout(new BorderLayout());
      add(centerContainer, centerData);      
      centerContainer.add(screenPanel, centerData);
      
      BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 34);
      northData.setMargins(new Margins(2, 2, 2, 2));
      toolbar = new UIDesignerToolbarImpl();
      centerContainer.add(toolbar, northData);
   }

   /**
    * create widget and property container in the view's east.
    */
   private ContentPanel createWidgetAndPropertyContainer() {
      ContentPanel widgetAndPropertyContainer = new ContentPanel(new BorderLayout());
      widgetAndPropertyContainer.setHeaderVisible(false);
      widgetAndPropertyContainer.setBorders(false);
      widgetAndPropertyContainer.setBodyBorder(false);

      WidgetPanel widgetPanel = new WidgetPanel();
      BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH);
      northData.setSplit(true);
      northData.setMargins(new Margins(2));
      widgetPanel.setSize("100%", "50%");
      widgetAndPropertyContainer.add(widgetPanel, northData);

      propertyPanel = new PropertyPanel(widgetSelectionUtil);
      BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
      centerData.setSplit(true);
      centerData.setMargins(new Margins(2));
      propertyPanel.setSize("100%", "50%");

      widgetAndPropertyContainer.add(propertyPanel, centerData);
      return widgetAndPropertyContainer;
   }

   // TODO EBR : This might be temporary, check what the presenter really needs access to / how to make public
   
  public TemplatePanelImpl getTemplatePanel() {
    return templatePanel;
  } 
  
  public ProfilePanel getProfilePanel() {
    return profilePanel;
  }

  public ScreenPanelImpl getScreenPanel() {
    return screenPanel;
  }
  
  public PropertyPanel getPropertyPanel() {
    return propertyPanel;
  }

  public UIDesignerToolbar getToolbar() {
    return toolbar;
  }
}