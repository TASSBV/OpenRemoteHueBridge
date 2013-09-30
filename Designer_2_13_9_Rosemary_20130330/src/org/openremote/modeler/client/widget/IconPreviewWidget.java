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
package org.openremote.modeler.client.widget;

import org.openremote.modeler.client.widget.component.FlexStyleBox;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;

/**
 * Initializes a layout container which can be button style to show the text
 * or can be image style to show the icon. Its size is set by the caller.
 * 
 * It is used for the <b>ChangeIconWindow</b> to preview the widget's icon or text.
 */
public class IconPreviewWidget extends LayoutContainer {

   private FlexTable btnTable = new FlexStyleBox();

   private Text center = new Text();

   private Image image = new Image();
   
   /**
    * Instantiates a new icon preview widget.
    * 
    * @param width the width
    * @param height the height
    */
   public IconPreviewWidget(int width, int height) {
      setSize(width, height);
      addStyleName("screen-btn");
      addStyleName("button-border");
      add(btnTable);
   }
   
   /**
    * Sets the icon.
    * 
    * @param icon the new icon
    */
   public void setIcon(String icon) {
      if (icon != null) {
         image.setUrl(icon);
      }
      btnTable.removeStyleName("screen-btn-cont");
      btnTable.setWidget(1, 1, image);
   }
   
   /**
    * Sets the text.
    * 
    * @param text the new text
    */
   public void setText(String text) {
      center.setText(text);
      btnTable.addStyleName("screen-btn-cont");
      btnTable.setWidget(1, 1, center);
   }
}
