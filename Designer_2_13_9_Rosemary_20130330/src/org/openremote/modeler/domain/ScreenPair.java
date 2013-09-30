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

import javax.persistence.Transient;

import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.touchpanel.TouchPanelCanvasDefinition;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;

import com.extjs.gxt.ui.client.data.BeanModelTag;

import flexjson.JSON;

/**
 * The ScreenPair includes two screens, portrait screen and landscape screen.
 * It can have one or two of them.
 */
public class ScreenPair extends RefedEntity implements BeanModelTag {

   private static final long serialVersionUID = 2501415029050801917L;

   /** 
    * The orientation type indicate the screenpair have which screen,
    * portrait represent portrait screen, landscape represent landscape screen,
    * both represent portrait and landscape screen.
    *  */
   private OrientationType orientation = OrientationType.PORTRAIT;
   private Screen portraitScreen;
   private Screen landscapeScreen;
   
   /** The touch panel definition is defined as portrait or landscape screen's canvas. */
   private TouchPanelDefinition touchPanelDefinition;   
   private Group parentGroup = null;
   public ScreenPair() {
   }
   
   public Screen getPortraitScreen() {
      return portraitScreen;
   }
   public Screen getLandscapeScreen() {
      return landscapeScreen;
   }
   public void setPortraitScreen(Screen portraitScreen) {
      this.portraitScreen = portraitScreen;
      this.portraitScreen.setScreenPair(this);
   }
   public void setLandscapeScreen(Screen landscapeScreen) {
      this.landscapeScreen = landscapeScreen;
      this.landscapeScreen.setScreenPair(this);
   }

   public TouchPanelDefinition getTouchPanelDefinition() {
      return touchPanelDefinition;
   }

   public void setTouchPanelDefinition(TouchPanelDefinition touchPanelDefinition) {
      this.touchPanelDefinition = touchPanelDefinition;
      if (portraitScreen != null) {
         portraitScreen.setTouchPanelDefinition(touchPanelDefinition);
      }
      if (landscapeScreen != null) {
         landscapeScreen.setTouchPanelDefinition(touchPanelDefinition.getHorizontalDefinition());
      }
   }
   public OrientationType getOrientation() {
      return orientation;
   }
   public void setOrientation(OrientationType orientation) {
      this.orientation = orientation;
   }
   @Transient
   @JSON(include=false)
   public String getPanelName() {
      TouchPanelCanvasDefinition canvas = touchPanelDefinition.getCanvas();
      return getName() + "(" + touchPanelDefinition.getName() + "," + canvas.getWidth() + "X" + canvas.getHeight() + ")";
   }
   
   @Transient
   @JSON(include=false)
   public String getName() {
      if (portraitScreen != null) {
         return portraitScreen.getName();
      }
      return landscapeScreen != null ? landscapeScreen.getName() : null;
   }
   
   public void setName(String name) {
      if (portraitScreen != null) {
         portraitScreen.setName(name);
      }
      if (landscapeScreen != null) {
         landscapeScreen.setName(name);
      }
   }
   
   public static enum OrientationType {
      PORTRAIT, LANDSCAPE, BOTH;
   }
   
   /**
    * If the orientation type is BOTH, set portrait and landscape screen's inverse screen id.
    */
   public void setInverseScreenIds() {
      if (orientation.equals(OrientationType.BOTH)) {
         portraitScreen.setInverseScreenId(landscapeScreen.getOid());
         landscapeScreen.setInverseScreenId(portraitScreen.getOid());
      }
   }
   
   /**
    * Make portrait and landscape screen's inverse id be zero.
    */
   public void clearInverseScreenIds() {
      if (portraitScreen != null) {
         portraitScreen.setInverseScreenId(0);
      }
      if (landscapeScreen != null) {
         landscapeScreen.setInverseScreenId(0);
      }
   }
   
   @Transient
   @JSON(include = false)
   public String getPanelXml() {
      StringBuffer xmlContent = new StringBuffer();
      if(orientation.equals(OrientationType.PORTRAIT)) {
         xmlContent.append("<include type=\"screen\" ref=\"" + portraitScreen.getOid() + "\"/>");
      } else if (orientation.equals(OrientationType.LANDSCAPE)) {
         xmlContent.append("<include type=\"screen\" ref=\"" + landscapeScreen.getOid() + "\"/>");
      } else if (orientation.equals(OrientationType.BOTH)) {
         xmlContent.append("<include type=\"screen\" ref=\"" + portraitScreen.getOid() + "\"/>");
         xmlContent.append("<include type=\"screen\" ref=\"" + landscapeScreen.getOid() + "\"/>");
      }
      return xmlContent.toString();
   }
   
   @Override
   @Transient
   @JSON(include = false)
   public String getDisplayName() {
      return getName();
   }
   
   @JSON(include = false)
   public Group getParentGroup() {
      return parentGroup;
   }

   public void setParentGroup(Group belongTo) {
      this.parentGroup = belongTo;
   }
   
   /**
    * Gets the all image sources from the portrait and landscape screens.
    * 
    * @return the all image sources
    */
   public Collection<ImageSource> getAllImageSources() {
      if (orientation.equals(OrientationType.PORTRAIT)) {
         return this.portraitScreen.getAllImageSources();
      } else if (orientation.equals(OrientationType.LANDSCAPE)) {
         return this.landscapeScreen.getAllImageSources();
      } else if (orientation.equals(OrientationType.BOTH)) {
         Collection<ImageSource> imageSources = new ArrayList<ImageSource>();
         imageSources.addAll(this.portraitScreen.getAllImageSources());
         imageSources.addAll(this.landscapeScreen.getAllImageSources());
         return imageSources;
      }

      return new ArrayList<ImageSource>();
   }

   @Transient
   @JSON(include = false)
   public boolean hasPortraitScreen() {
      if (orientation.equals(OrientationType.BOTH) || orientation.equals(OrientationType.PORTRAIT)) {
         return true;
      }
      return false;
   }
   
   @Transient
   @JSON(include = false)
   public boolean hasLandscapeScreen() {
      if (orientation.equals(OrientationType.BOTH) || orientation.equals(OrientationType.LANDSCAPE)) {
         return true;
      }
      return false;
   }
}
