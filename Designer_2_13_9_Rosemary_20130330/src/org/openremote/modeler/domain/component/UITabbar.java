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
package org.openremote.modeler.domain.component;

import java.util.ArrayList;
import java.util.List;

/**
 * UITabbar has two scope(group and panel), the default scope is group.
 * It has some tabbar items.
 *
 */
public class UITabbar extends UIComponent {

   private static final long serialVersionUID = 8227607089110291452L;
   public static final int MAX_TABBARITEM_COUNT = 5;
   
   private List<UITabbarItem> tabbarItems = new ArrayList<UITabbarItem>(5);
   private Scope scope = Scope.GROUP;
   
   public UITabbar() {}
   public UITabbar(UITabbar uiComponent) {
//      ui
   }

   @Override
   public String getPanelXml() {
      return "";
   }

   public void addTabbarItem(UITabbarItem uiTabbarItem) {
      tabbarItems.add(uiTabbarItem);
   }
   
   public void removeTabarItem(UITabbarItem uiTabbarItem) {
      tabbarItems.remove(uiTabbarItem);
   }
   
   public void insertTabbarItem(int index,UITabbarItem uiTabbarItem) {
      tabbarItems.add(index, uiTabbarItem);
   }
   
   public Scope getScope() {
      return scope;
   }

   public void setScope(Scope scope) {
      this.scope = scope;
   }

   public List<UITabbarItem> getTabbarItems() {
      return tabbarItems;
   }
   public void setTabbarItems(List<UITabbarItem> tabbarItems) {
      this.tabbarItems = tabbarItems;
   }
   @Override
   public String getName() {
      return "Tab Bar";
   }
   
   public static enum Scope {
      PANEL,GROUP;
   }
   
   public void removeAll() {
      tabbarItems.removeAll(this.tabbarItems);
   }
}
