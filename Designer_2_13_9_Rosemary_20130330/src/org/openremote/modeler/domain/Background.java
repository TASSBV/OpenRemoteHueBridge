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

import java.util.HashMap;
import java.util.Map;

import org.openremote.modeler.domain.component.ImageSource;

/**
 * This class is used to store the background image information for a screen. 
 * @author Javen
 *
 */
public class Background extends BusinessEntity {
   
   private static final long serialVersionUID = 6464353679677279776L;
   
   private ImageSource imageSource = null;
   private boolean fillScreen = true;
   private  boolean absolute = false;
   private int left = 0;
   private int top = 0;
   private int width = 0;
   private int height = 0;
   private RelativeType relatedType = RelativeType.TOP_LEFT;
   private static Map<RelativeType, String> relativeMap;
   public Background() {
      this.imageSource = new ImageSource("");
   }
   public Background(ImageSource src) {
      this.imageSource = src;
   }

   public ImageSource getImageSource() {
      return imageSource;
   }
   public void setImageSource(ImageSource imageSource) {
      this.imageSource = imageSource;
   }
   public boolean isFillScreen() {
      return fillScreen;
   }
   public void setFillScreen(boolean fillScreen) {
      this.fillScreen = fillScreen;
   }
   
   
   public boolean isAbsolute() {
      return absolute;
   }
   public void setAbsolute(boolean absolute) {
      this.absolute = absolute;
   }
   public int getLeft() {
      return left;
   }
   public void setLeft(int left) {
      this.left = left;
   }
   public int getTop() {
      return top;
   }
   public void setTop(int top) {
      this.top = top;
   }
   public int getWidth() {
      return width;
   }
   public void setWidth(int width) {
      this.width = width;
   }
   public int getHeight() {
      return height;
   }
   public void setHeight(int height) {
      this.height = height;
   }

   
   public RelativeType getRelatedType() {
      return relatedType;
   }

   public void setRelatedType(RelativeType relatedType) {
      this.relatedType = relatedType;
   }

   /**
    * Some type for relative Type. 
    * @author Javen
    *
    */
   public static enum RelativeType {
      LEFT, RIGHT, TOP, BOTTOM, TOP_LEFT, BOTTOM_LEFT, TOP_RIGHT, BOTTOM_RIGHT, CENTER;
   }
   
   public static Map<RelativeType, String> getRelativeMap() {
      if (relativeMap == null) {
         relativeMap = new HashMap<RelativeType, String>();
         relativeMap.put(RelativeType.LEFT, "center left");
         relativeMap.put(RelativeType.RIGHT, "center right");
         relativeMap.put(RelativeType.TOP, "top center");
         relativeMap.put(RelativeType.BOTTOM, "bottom center");
         relativeMap.put(RelativeType.TOP_LEFT, "top left");
         relativeMap.put(RelativeType.BOTTOM_LEFT, "bottom left");
         relativeMap.put(RelativeType.TOP_RIGHT, "top right");
         relativeMap.put(RelativeType.BOTTOM_RIGHT, "bottom right");
         relativeMap.put(RelativeType.CENTER, "center center");
      }
      
      return relativeMap;
   }
}
