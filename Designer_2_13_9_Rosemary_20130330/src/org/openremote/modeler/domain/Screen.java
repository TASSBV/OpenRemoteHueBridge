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
import java.util.Collection;
import java.util.List;

import javax.persistence.Transient;

import org.openremote.modeler.domain.component.Gesture;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.ImageSourceOwner;
import org.openremote.modeler.domain.component.UIComponent;
import org.openremote.modeler.domain.component.UIGrid;
import org.openremote.modeler.touchpanel.TouchPanelCanvasDefinition;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;

import com.extjs.gxt.ui.client.data.BeanModelTag;

import flexjson.JSON;

/**
 * The Screen define a isolate page which would be shown in panel(like web console, iPhone, Android, etc.).
 * In the screen, there are components, which in absolute or relative position. The absolutes include components
 * with absolute position, the grid is also in absolute position, but the components in grid is in relative position.  
 */
public class Screen extends BusinessEntity {
   private static final long serialVersionUID = -4133577592315343274L;

   /** The default name index. */
   private static int defaultNameIndex = 1;

   /** The name. */
   private String name;

   /** The absolute components which included in the screen. */
   private List<Absolute> absolutes = new ArrayList<Absolute>();

   /** The grid that include grid cell with component. */
   private List<UIGrid> grids = new ArrayList<UIGrid>();

   /** Support a canvas for the screen to manage components. */
   private TouchPanelDefinition touchPanelDefinition;

   /** The screen's background, include image and image position. */
   private Background background = null;

   private List<Gesture> gestures = new ArrayList<Gesture>();
   
   private boolean hasTabbar;
   
   /** Represent the screen is landscape or portrait, true is landscape and false is portrait. */
   private boolean isLandscape;
   
   /** The screen's id which orientation is opposite to the screen. */
   private long inverseScreenId = 0;
   
   /** The screen pair can have two screens, landscape and portrait, or have one screen. */
   private ScreenPair screenPair;
   
   public Screen() {
      this.background = new Background();
   }

	public boolean isHasTabbar() {
		return hasTabbar;
	}

	public void setHasTabbar(boolean hasTabbar) {
		this.hasTabbar = hasTabbar;
	}

/**
    * Gets the name.
    * 
    * @return the name
    */
   public String getName() {
      return name;
   }
   
   
   public UIGrid getGrid(int index) {
      return grids.size() > 0 ? grids.get(index) : null;
   }

   /**
    * Gets the absolutes.
    * 
    * @return the absolutes
    */
   public List<Absolute> getAbsolutes() {
      return absolutes;
   }

   /**
    * Gets the touch panel definition.
    * 
    * @return the touch panel definition
    */
//   @JSON(include=false)
   public TouchPanelDefinition getTouchPanelDefinition() {
      return touchPanelDefinition;
   }

   /**
    * Gets the CSS background image url, which encode the " " to "%20".
    * 
    * @return the cSS background
    */
   @JSON(include=false)
   public String getCSSBackground() {
      return background.getImageSource().getSrc().replaceAll(" ", "%20");
   }

   /**
    * Sets the name.
    * 
    * @param name
    *           the new name
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * Sets the absolutes.
    * 
    * @param absolutes
    *           the new absolutes
    */
   public void setAbsolutes(List<Absolute> absolutes) {
      this.absolutes = absolutes;
   }

   /**
    * Adds the absolute.
    * 
    * @param absolute
    *           the absolute
    */
   public void addAbsolute(Absolute absolute) {
      this.absolutes.add(absolute);
   }

   /**
    * Sets the touch panel definition.
    * 
    * @param touchPanelDefinition
    *           the new touch panel definition
    */
   public void setTouchPanelDefinition(TouchPanelDefinition touchPanelDefinition) {
      this.touchPanelDefinition = touchPanelDefinition;
   }

   /**
    * Sets the background.
    * 
    * @param background
    *           the new background
    */
   public void setBackground(Background background) {
      this.background = background;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.openremote.modeler.domain.BusinessEntity#getDisplayName()
    */
   @Transient
   @JSON(include=false)
   public String getDisplayName() {
      return getPanelName();
   }

   @Transient
   @JSON(include=false)
   public String getPanelName() {
      TouchPanelCanvasDefinition canvas = touchPanelDefinition.getCanvas();
      return name + "(" + touchPanelDefinition.getName() + "," + canvas.getWidth() + "X" + canvas.getHeight() + ")";
   }

   /**
    * Gets the new default name when you want a new name. such as screen1.
    * 
    * @return the new default name
    */
   @Transient
   public static String getNewDefaultName() {
      return "screen" + defaultNameIndex;
   }

   @Transient
   public static void increaseDefaultNameIndex() {
      defaultNameIndex++;
   }

   public void removeAbsolute(Absolute absolute) {
      if (this.absolutes.size() > 0) {
         this.absolutes.remove(absolute);
         absolute.getUiComponent().setRemoved(true);
      }
   }

   public List<UIGrid> getGrids() {
      return grids;
   }

   public void setGrids(List<UIGrid> grids) {
      this.grids = grids;
   }

   public void addGrid(UIGrid grid) {
      grids.add(grid);
   }

   public void removeGrid(UIGrid grid) {
      if (grids.size() > 0) {
         this.grids.remove(grid);
         Collection<Cell> cells = grid.getCells();
         for (Cell cell : cells) {
            cell.getUiComponent().setRemoved(true);
         }
      }
   }

   public Background getBackground() {
      return background;
   }

   public List<Gesture> getGestures() {
      return gestures;
   }

   public void setGestures(List<Gesture> gestures) {
      this.gestures = gestures;
   }
   public void addGesture(Gesture gesture){
      gestures.add(gesture);
   }
   
   public boolean isLandscape() {
      return isLandscape;
   }

   public void setLandscape(boolean isLandscape) {
      this.isLandscape = isLandscape;
   }

   public long getInverseScreenId() {
      return inverseScreenId;
   }

   public void setInverseScreenId(long inverseScreenId) {
      this.inverseScreenId = inverseScreenId;
   }

   /**
    * get all the UIComponent by the component's class. for example, if you want to get all the UIButton on the screen.
    * you can invoke this method like this: <code>getAllUIComponentByType(UIButton.class)</code>
    * 
    * @param clazz
    * @return
    */
   public Collection<? extends UIComponent> getAllUIComponentByType(Class<? extends UIComponent> clazz) {
      Collection<UIComponent> uiComponents = new ArrayList<UIComponent>();
      for (Absolute absolute : absolutes) {
         if (absolute.getUiComponent().getClass() == clazz) {
            uiComponents.add(absolute.getUiComponent());
         }
      }
      for (UIGrid grid : grids) {
         for (Cell cell : grid.getCells()) {
            if (cell.getUiComponent().getClass() == clazz) {
               uiComponents.add(cell.getUiComponent());
            }
         }
      }
      return uiComponents;
   }

   @JSON(include = false)
   public ScreenPair getScreenPair() {
      return screenPair;
   }

   public void setScreenPair(ScreenPair screenPair) {
      this.screenPair = screenPair;
   }
   
   public String getNameWithOrientation() {
      if (isLandscape) {
         return "(L)" + name;
      }
      return name;
   }
   
   @JSON(include = false)
   public Collection<ImageSource> getAllImageSources() {
      Collection<ImageSource> imageSources = new ArrayList<ImageSource> ();
      for (Absolute absolute : absolutes) {
         if (absolute.getUiComponent() instanceof ImageSourceOwner) {
            ImageSourceOwner imageSourceOwner = (ImageSourceOwner) absolute.getUiComponent();
            imageSources.addAll(imageSourceOwner.getImageSources());
         }
      }
      for (UIGrid grid : grids) {
         for (Cell cell : grid.getCells()) {
            if (cell.getUiComponent() instanceof ImageSourceOwner) {
               ImageSourceOwner imageSourceOwner = (ImageSourceOwner) cell.getUiComponent();
               imageSources.addAll(imageSourceOwner.getImageSources());
            }
         }
      }
      
      //add background image source 
      if (this.background != null && background.getImageSource()!=null && !background.getImageSource().isEmpty()) {
         imageSources.add(this.background.getImageSource());
      }
      return imageSources;
   }
}
