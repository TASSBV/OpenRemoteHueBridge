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

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.domain.BusinessEntity;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPair.OrientationType;
import org.openremote.modeler.domain.ScreenPairRef;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;

/**
 * The tabPanel stores screenTabItems which display for the screenPair.
 * If the screenPair has two screens, there would be two screenTabItems,
 * else has one.
 */
public class ScreenTab extends TabPanel {
   
   private ScreenPair screenPair;
   
   public ScreenTab(ScreenPair screenPair, final WidgetSelectionUtil widgetSelectionUtil) {
      setTabPosition(TabPosition.BOTTOM);
      this.screenPair = screenPair;
      if (OrientationType.PORTRAIT.equals(screenPair.getOrientation())) {
         add(new ScreenTabItem(screenPair.getPortraitScreen(), widgetSelectionUtil));
      } else if (OrientationType.LANDSCAPE.equals(screenPair.getOrientation())) {
         add(new ScreenTabItem(screenPair.getLandscapeScreen(), widgetSelectionUtil));
      } else if (OrientationType.BOTH.equals(screenPair.getOrientation())) {
         add(new ScreenTabItem(screenPair.getPortraitScreen(), widgetSelectionUtil));
         add(new ScreenTabItem(screenPair.getLandscapeScreen(), widgetSelectionUtil));
      }
      this.addListener(Events.Select, new Listener<TabPanelEvent>(){
         public void handleEvent(TabPanelEvent be) {
            widgetSelectionUtil.resetSelection();
         }
         
      });
   }
   
   public ScreenPair getScreenPair() {
      return screenPair;
   }
   
   public void updateTouchPanel() {
      if (this.getItemByItemId(Constants.PORTRAIT) != null) {
         ((ScreenTabItem)this.getItemByItemId(Constants.PORTRAIT)).updateTouchPanel();
      }
      if (this.getItemByItemId(Constants.LANDSCAPE) != null) {
         ((ScreenTabItem)this.getItemByItemId(Constants.LANDSCAPE)).updateTouchPanel();
      }
   }
   
   public void updateTabbarForScreenCanvas(ScreenPairRef screenRef) {
      if (this.getItemByItemId(Constants.PORTRAIT) != null) {
         ((ScreenTabItem)this.getItemByItemId(Constants.PORTRAIT)).updateTabbarForScreenCanvas(screenRef);
      } if (this.getItemByItemId(Constants.LANDSCAPE) != null) {
         ((ScreenTabItem)this.getItemByItemId(Constants.LANDSCAPE)).updateTabbarForScreenCanvas(screenRef);
      }
   }

   public void updateScreenTabItems() {
      ScreenPairRef spRef = new ScreenPairRef();
      spRef.setScreen(this.screenPair);
      this.screenPair.releaseRef();
      spRef.setGroup(this.screenPair.getParentGroup());
      updateTabbarForScreenCanvas(spRef);
   }

   @Override
   public void setSelection(TabItem item) {
      super.setSelection(item);
      updateScreenTabItems();
   }
   
   public void updateScreenIndicator() {
      if (this.getItemByItemId(Constants.PORTRAIT) != null) {
         ((ScreenTabItem)this.getItemByItemId(Constants.PORTRAIT)).updateScreenIndicator();
      } if (this.getItemByItemId(Constants.LANDSCAPE) != null) {
         ((ScreenTabItem)this.getItemByItemId(Constants.LANDSCAPE)).updateScreenIndicator();
      }
   }
   
   public void onUIElementEdited(BusinessEntity element) {
     ((ScreenTabItem)getSelectedItem()).onUIElementEdited(element);
   }
}
