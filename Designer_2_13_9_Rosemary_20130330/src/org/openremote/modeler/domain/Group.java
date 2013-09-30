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
package org.openremote.modeler.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import org.openremote.modeler.domain.component.UITabbar;
import org.openremote.modeler.domain.component.UITabbarItem;

import com.extjs.gxt.ui.client.data.BeanModelTag;

import flexjson.JSON;

/**
 * A Group includes name, screenRefs and local tabbarItems.
 * The parentPanel property is for passing on to screenRefs.
 */
public class Group extends RefedEntity implements BeanModelTag {

   private static final long serialVersionUID = -9043041127351437532L;

   /** The default name index. */
   private static int defaultNameIndex = 1;
   
   /** The name. */
   private String name;
   
   /** The screen refs. */
   private List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();

   private List<UITabbarItem> tabbarItems = new ArrayList<UITabbarItem>();
   
   private UITabbar tabbar = null;
   
   private Panel parentPanel = null;
   
   public Group() {}
   /**
    * Gets the name.
    * 
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the name.
    * 
    * @param name the new name
    */
   public void setName(String name) {
      this.name = name;
   }

   
   /**
    * Gets the screen refs.
    * 
    * @return the screen refs
    */
   public List<ScreenPairRef> getScreenRefs() {
      return screenRefs;
   }

   /**
    * Sets the screen refs.
    * 
    * @param screenRefs the new screen refs
    */
   public void setScreenRefs(List<ScreenPairRef> screenRefs) {
      this.screenRefs = screenRefs;
   }

   /**
    * Adds the screen ref.
    * 
    * @param screen the screen
    */
   public void addScreenRef(ScreenPairRef screenRef) {
      screenRefs.add(screenRef);
   }
   
   public void removeScreenRef(ScreenPairRef screenRef) {
      screenRefs.remove(screenRef);
   }
   
   public void insertScreenRef(ScreenPairRef before, ScreenPairRef target) {
      int index = screenRefs.indexOf(before);
      target.setGroup(this);
      target.getScreen().setParentGroup(this);
      screenRefs.add(index, target);
   }
   public List<UITabbarItem> getTabbarItems() {
      return tabbarItems;
   }

   public void setTabbarItems(List<UITabbarItem> tabbarItems) {
      this.tabbarItems = tabbarItems;
   }

   /* (non-Javadoc)
    * @see org.openremote.modeler.domain.BusinessEntity#getDisplayName()
    */
   @Transient
   public String getDisplayName() {
      return name;
   }
   
   /**
    * Gets the new default name.
    * 
    * @return the new default name
    */
   @Transient
   public static String getNewDefaultName() {
      return "group" + defaultNameIndex;
   }
   
   public void clearScreenRefs() {
      screenRefs.clear();
   }
   
   @Transient
   public static void increaseDefaultNameIndex() {
      defaultNameIndex++;
   }

   @JSON(include = false)
   public Panel getParentPanel() {
      return parentPanel;
   }

   public void setParentPanel(Panel belongsTo) {
      this.parentPanel = belongsTo;
   }
   public UITabbar getTabbar() {
      return tabbar;
   }
   public void setTabbar(UITabbar tabbar) {
      this.tabbar = tabbar;
   }
   
   @Transient
   @JSON(include = false)
   public List<Screen> getPortraitScreens() {
      List<Screen> portraitScreens = new ArrayList<Screen>();
      for (ScreenPairRef screenPairRef : screenRefs) {
         if (screenPairRef.getScreen().hasPortraitScreen()) {
            portraitScreens.add(screenPairRef.getScreen().getPortraitScreen());
         }
      }
      return portraitScreens;
   }
   
   @Transient
   @JSON(include = false)
   public List<Screen> getLandscapeScreens() {
      List<Screen> landscapeScreens = new ArrayList<Screen>();
      for (ScreenPairRef screenPairRef : screenRefs) {
         if (screenPairRef.getScreen().hasLandscapeScreen()) {
            landscapeScreens.add(screenPairRef.getScreen().getLandscapeScreen());
         }
      }
      return landscapeScreens;
   }
   
}
