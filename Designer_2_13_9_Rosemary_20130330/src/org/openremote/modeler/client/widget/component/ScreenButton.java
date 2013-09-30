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

import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.propertyform.ButtonPropertyForm;
import org.openremote.modeler.client.widget.propertyform.PropertyForm;
import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.utils.StringUtils;

import com.google.gwt.user.client.ui.FlexTable;

/**
 * The Class ScreenButton. It display as a style box, can be adjust size, change icon and text.
 */
public class ScreenButton extends ScreenComponent {

   private FlexTable btnTable = new FlexStyleBox();

   private UIButton uiButton = new UIButton();

   /**
    * Instantiates a new screen button.
    */
   public ScreenButton(ScreenCanvas canvas, WidgetSelectionUtil widgetSelectionUtil) {
      super(canvas, widgetSelectionUtil);
      initial();
   }

   public ScreenButton(ScreenCanvas canvas, String text, WidgetSelectionUtil widgetSelectionUtil) {
      super(canvas, widgetSelectionUtil);
      setText(text);
   }

   /**
    * Instantiates a new screen button.
    * 
    * @param width
    *           the width
    * @param height
    *           the height
    */
   public ScreenButton(ScreenCanvas canvas, int width, int height, WidgetSelectionUtil widgetSelectionUtil) {
      this(canvas, widgetSelectionUtil);
      setSize(width, height);
   }

   public ScreenButton(ScreenCanvas canvas, UIButton uiButton, WidgetSelectionUtil widgetSelectionUtil) {
      this(canvas, widgetSelectionUtil);
      this.uiButton = uiButton;
      adjustTextLength();
      if (uiButton.getImage() != null) {
         setIcon(uiButton.getImage().getSrc());
      }
   }

   /**
    * Initial.
    * 
    */
   protected void initial() {
      addStyleName("screen-btn");
      setStyleAttribute("backgroundRepeat", "no-repeat");
      setStyleAttribute("backgroundPosition", "0 0");
      setText("Button");
      add(btnTable);
   }

   @Override
   public String getName() {
      return uiButton.getName();
   }

   /**
    * Sets the center icon url.
    * 
    */
   public void setIcon(String icon) {
      btnTable.removeStyleName("screen-btn-cont");
      setStyleAttribute("backgroundImage", "url(" + icon + ")");
   }

   @Override
   public PropertyForm getPropertiesForm() {
      return new ButtonPropertyForm(this, uiButton, widgetSelectionUtil);
   }

   @Override
   public void setSize(int width, int height) {
      super.setSize(width, height);
      if (getWidth() == 0) {
         adjustTextLength(width);
      } else {
         adjustTextLength();
      }
   }

   /**
    * Adjust text length.
    * 
    * @param length
    *           the length
    */
   public void adjustTextLength() {
      adjustTextLength(getWidth());
   }

   private void adjustTextLength(int width) {
      int ajustLength = (width - 6) / 7;
      if (uiButton.getName() != null && ajustLength < uiButton.getName().length()) {
         setText(uiButton.getName().substring(0, ajustLength) + "..");
      } else {
         if (StringUtils.isEmpty(uiButton.getName())) {
            setText(" ");
         } else {
            setText(uiButton.getName());
         }
      }
   }

   public void setDefaultImage() {
      if (uiButton.getImage() != null) {
         setIcon(uiButton.getImage().getSrc());
      }
   }
   
   public void setPressedImage() {
      if (uiButton.getPressImage() != null && uiButton.getImage() != null) {
         setIcon(uiButton.getPressImage().getSrc());
      }
   }
   
   public void removeIcon() {
      btnTable.addStyleName("screen-btn-cont");
      adjustTextLength();
      setStyleAttribute("backgroundImage", "url()");
   }
   
   private void setText(String text) {
      btnTable.setText(1, 1, text);
   }

   @Override
   protected void afterRender() {
      super.afterRender();
      adjustTextLength();
   }
   
}
