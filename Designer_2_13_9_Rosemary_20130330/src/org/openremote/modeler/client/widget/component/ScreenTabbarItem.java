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

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.propertyform.PropertyForm;
import org.openremote.modeler.client.widget.propertyform.TabbarItemPropertyForm;
import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.Navigate;
import org.openremote.modeler.domain.component.UITabbarItem;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.Text;
import com.google.gwt.user.client.Event;

/**
 * The tabbarItem can drag and drop in the tabbar.
 * It includes a text and a image.
 * 
 * @author Javen
 *
 */
public class ScreenTabbarItem extends ScreenComponent {
   private UITabbarItem uiTabbarItem = null;
   protected Text center = new Text("item");
   
   public ScreenTabbarItem(ScreenCanvas screenCanvas, WidgetSelectionUtil widgetSelectionUtil) {
      super(screenCanvas, widgetSelectionUtil);
   }

   public ScreenTabbarItem(ScreenCanvas screenCanvas,UITabbarItem uiTabbarItem, WidgetSelectionUtil widgetSelectionUtil) {
      super(screenCanvas, widgetSelectionUtil);
      this.uiTabbarItem = uiTabbarItem;
      
      addStyleName("tabbaritem-background");
      setStyleAttribute("position", "absolute");
      setStyleAttribute("cursor", "move");
      
      center.setStyleAttribute("color", "white");
      center.setStyleAttribute("bottom", "0");
      center.setStyleAttribute("width","100%");
      center.setStyleAttribute("textAlign", "center");
      center.setStyleAttribute("position", "absolute");
      center.setStyleAttribute("fontSize", "11px");
      center.setStyleAttribute("fontFamily", Constants.DEFAULT_FONT_FAMILY);
      center.setText(uiTabbarItem.getName());
      updateImage();
      add(center);
   }
   @Override
   public String getName() {
      return uiTabbarItem.getName();
   }

   // TODO EBR : must get rid of this one to, review the PropertyForm for this
   public void setName(String name) {
      uiTabbarItem.setName(name);
      center.setText(name);
      layout();
   }
   
   @Override
   protected void afterRender() {
      super.afterRender();
      super.el().updateZIndex(1);
   }

   @Override
   public void onComponentEvent(ComponentEvent ce) {
      if (ce.getEventTypeInt() == Event.ONMOUSEDOWN) {
         widgetSelectionUtil.setSelectWidget(this);
      }
      ce.cancelBubble();
      super.onComponentEvent(ce);
   }
   
   
   public Navigate getNavigate() {
      return uiTabbarItem.getNavigate();
   }

   @Override
   public PropertyForm getPropertiesForm() {
      return new TabbarItemPropertyForm(this, widgetSelectionUtil);
   }
   
   public ImageSource getImageSource() {
      if (this.uiTabbarItem.getImage() == null ) return new ImageSource();
      return this.uiTabbarItem.getImage();
   }
   
   public void setImageSource(ImageSource imageSource) {
      this.uiTabbarItem.setImage(imageSource);
      updateImage();
   }
   
   private void updateImage() {
      if (this.uiTabbarItem.getImage() != null && this.uiTabbarItem.getImage().getSrc() != null) {
         String imageURL = this.uiTabbarItem.getImage().getSrc();
         if (imageURL.trim().length() > 0) {
            setStyleAttribute("overflow", "hidden");
            this.setStyleAttribute("background", "url("+imageURL+") top center no-repeat");
            layout();
         }
      }
   }

   public UITabbarItem getUITabbarIem() {
      return this.uiTabbarItem;
   }
   
   public void removeImage() {
      setImageSource(null);
      this.setStyleAttribute("backgroundImage", "");
      layout();
   }
}
