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
import org.openremote.modeler.client.widget.propertyform.PropertyForm;
import org.openremote.modeler.client.widget.propertyform.SliderPropertyForm;
import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.UISlider;
import org.openremote.modeler.shared.dto.SliderWithInfoDTO;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;

/**
 * The class generates the slider component, it has five images.
 * There are two type of slider, vertical and horizontal.
 * 
 * @author javen
 *
 */
public class ScreenSlider extends ScreenComponent {

   private UISlider uiSlider;

   protected LayoutContainer minImage = new LayoutContainer();
   protected LayoutContainer minTrackImage = new LayoutContainer();
   protected LayoutContainer thumbImage = new LayoutContainer();
   protected LayoutContainer maxTrackImage = new LayoutContainer();
   protected LayoutContainer maxImage = new LayoutContainer();

   public ScreenSlider(ScreenCanvas screenCanvas, WidgetSelectionUtil widgetSelectionUtil) {
      super(screenCanvas, widgetSelectionUtil);
      setLayout(new FlowLayout());
   }

   public ScreenSlider(ScreenCanvas screenCanvas, UISlider uiSlider, WidgetSelectionUtil widgetSelectionUtil) {
      this(screenCanvas, widgetSelectionUtil);
      this.uiSlider = uiSlider;
      
      if(uiSlider.isVertical()) {
         this.addVerticalStyle();
         updateVerticalImages();
      } else {
        this.addHorizontalStyle();
        updateHorizontaoImages();
      }
      add(minImage);
      add(minTrackImage);
      add(thumbImage);
      add(maxTrackImage);
      add(maxImage);
      this.layout();
   }

   public UISlider getUiSlider() {
      return uiSlider;
   }

   public void setUiSlider(UISlider uiSlider) {
      this.uiSlider = uiSlider;
   }

   @Override
   public String getName() {
      return "Slider";
   }

   @Override
   public void setSize(int width, int height) {
      super.setSize(width, height);
   }

   @Override
   public PropertyForm getPropertiesForm() {
      return new SliderPropertyForm(this, widgetSelectionUtil);
   }

   public void setMinImage(String imageURL) {
      uiSlider.setMinImage(new ImageSource(imageURL));
      minImage.setStyleAttribute("backgroundImage", "url(" + imageURL + ")");
   }

   public void setMinTrackImage(String imageURL) {
      uiSlider.setMinTrackImage(new ImageSource(imageURL));
      minTrackImage.setStyleAttribute("backgroundImage", "url(" + imageURL + ")");
   }

   public void setThumbImage(String imageURL) {
      uiSlider.setThumbImage(new ImageSource(imageURL));
      thumbImage.setStyleAttribute("backgroundImage", "url(" + imageURL + ")");
   }

   public void setMaxTrackImage(String imageURL) {
      uiSlider.setMaxTrackImage(new ImageSource(imageURL));
      maxTrackImage.setStyleAttribute("backgroundImage", "url(" + imageURL + ")");
   }

   public void setMaxImage(String imageURL) {
      uiSlider.setMaxImage(new ImageSource(imageURL));
      maxImage.setStyleAttribute("backgroundImage", "url(" + imageURL + ")");
   }

   public void setSliderDTO(SliderWithInfoDTO sliderDTO) {
      uiSlider.setSliderDTO(sliderDTO);
   }

   public SliderWithInfoDTO getSliderDTO() {
      return uiSlider.getSliderDTO();
   }
   
   public void setVertical(boolean isVertical) {
      uiSlider.setVertical(isVertical);
      if(isVertical) {
         toVertical(true);
         updateVerticalImages();
      } else {
        toHorizontal(true);
        updateHorizontaoImages();
      }
      this.getScreenCanvas().setSizeToDefault(uiSlider);
      this.layout();
   }
   
   private void toVertical(boolean initialized) {
      //1, remove the style for horizontal slider. 
      this.removeHorizontalStyle();
      //2, add the style for vertical slider. 
      this.addVerticalStyle();
   }
   
   
   private void toHorizontal(boolean initialized) {
      //1, remove the style for vertical slider.
      this.removeVerticalStyle();
      //2, add the style for horizontal slider.
      this.addHorizontalStyle();
   }
   
   private void removeHorizontalStyle() {
      minImage.removeStyleName("sliderMinImage");
      minTrackImage.removeStyleName("sliderMinTrackImage");
      thumbImage.removeStyleName("sliderThumbImage");
      maxTrackImage.removeStyleName("sliderMaxTrackImage");
      maxImage.removeStyleName("sliderMaxImage");
   }
   
   private void addHorizontalStyle () {
      minImage.addStyleName("sliderMinImage");
      minTrackImage.addStyleName("sliderMinTrackImage");
      thumbImage.addStyleName("sliderThumbImage");
      maxTrackImage.addStyleName("sliderMaxTrackImage");
      maxImage.addStyleName("sliderMaxImage");
   }
   
   private void removeVerticalStyle() {
      minImage.removeStyleName("vsliderMinImage");
      minTrackImage.removeStyleName("vsliderMinTrackImage");
      thumbImage.removeStyleName("vsliderThumbImage");
      maxTrackImage.removeStyleName("vsliderMaxTrackImage");
      maxImage.removeStyleName("vsliderMaxImage");
   }
   
   private void addVerticalStyle() {
      minImage.addStyleName("vsliderMinImage");
      minTrackImage.addStyleName("vsliderMinTrackImage");
      thumbImage.addStyleName("vsliderThumbImage");
      maxTrackImage.addStyleName("vsliderMaxTrackImage");
      maxImage.addStyleName("vsliderMaxImage");
   }
   
   private void updateVerticalImages() {
      if(!isMinImageUploaded()) {
         setMinImage(UISlider.DEFAULT_VERTICAL_MIN_IMAGE);
      } else {
         setMinImage(uiSlider.getMinImage().getSrc());
      }
      
      if(!isMinTrackImageUploaded()) {
         setMinTrackImage(UISlider.DEFAULT_VERTICAL_MINTRACK_IMAGE);
      } else {
         setMinTrackImage(uiSlider.getMinTrackImage().getSrc());
      }
      
      if(!isThumbUploaded()) {
         setThumbImage(UISlider.DEFAULT_VERTICAL_THUMB_IMAGE);
      } else {
         setThumbImage(uiSlider.getThumbImage().getSrc());
      }
      
      if(!isMaxTrackImageUploaded()) {
         setMaxTrackImage(UISlider.DEFAULT_VERTICAL_MAXTRACK_IMAGE);
      } else {
         setMaxTrackImage(uiSlider.getMaxTrackImage().getSrc());
      }
      
      if(!isMaxImageUploaded()) {
         setMaxImage(UISlider.DEFAULT_VERTICAL_MAX_IMAGE);
      } else {
         setMaxImage(uiSlider.getMaxImage().getSrc());
      }
   }
   
   private void updateHorizontaoImages() {
      if(!isMinImageUploaded()) {
         setMinImage(UISlider.DEFAULT_HORIZONTAL_MIN_IMAGE);
      } else {
         setMinImage(uiSlider.getMinImage().getSrc());
      }
      
      if(!isMinTrackImageUploaded()) {
         setMinTrackImage(UISlider.DEFAULT_HORIZONTAL_MINTRACK_IMAGE);
      } else {
         setMinTrackImage(uiSlider.getMinTrackImage().getSrc());
      }
      
      if(!isThumbUploaded()) {
         setThumbImage(UISlider.DEFAULT_HORIZONTAL_THUMB_IMAGE);
      } else {
         setThumbImage(uiSlider.getThumbImage().getSrc());
      }
      
      if(!isMaxTrackImageUploaded()) {
         setMaxTrackImage(UISlider.DEFAULT_HORIZONTAL_MAXTRACK_IMAGE);
      } else {
         setMaxTrackImage(uiSlider.getMaxTrackImage().getSrc());
      }
      
      if(!isMaxImageUploaded()) {
         setMaxImage(UISlider.DEFAULT_HORIZONTAL_MAX_IMAGE);
      } else {
         setMaxImage(uiSlider.getMaxImage().getSrc());
      }
   }
   
   
   public boolean isMinImageUploaded() {
      return uiSlider.isMinImageUploaded();
   }
   
   public boolean isMinTrackImageUploaded() {
      return uiSlider.isMinTrackImageUploaded();
   }
   
   public boolean isThumbUploaded() {
      return uiSlider.isThumbUploaded();
   }
   
   public boolean isMaxTrackImageUploaded() {
      return uiSlider.isMaxTrackImageUploaded();
   }
   
   public boolean isMaxImageUploaded() {
      return uiSlider.isMaxImageUploaded();
   }

   public Boolean isVertical() {
      return uiSlider.isVertical();
   }
   
   public void removeMinImage() {
      if(uiSlider.isVertical()) {
         setMinImage(UISlider.DEFAULT_VERTICAL_MIN_IMAGE);
      } else {
         setMinImage(UISlider.DEFAULT_HORIZONTAL_MIN_IMAGE);
      }
      layout();
   }
   
   public void removeMinTrackImage() {
      if(uiSlider.isVertical()) {
         setMinTrackImage(UISlider.DEFAULT_VERTICAL_MINTRACK_IMAGE);
      } else {
         setMinTrackImage(UISlider.DEFAULT_HORIZONTAL_MINTRACK_IMAGE);
      }
      layout();
   }
   public void removeThumbImage() {
      if(uiSlider.isVertical()) {
         setThumbImage(UISlider.DEFAULT_VERTICAL_THUMB_IMAGE);
      } else {
         setThumbImage(UISlider.DEFAULT_HORIZONTAL_THUMB_IMAGE);
      }
      layout();
   }
   public void removeMaxImage() {
      if(uiSlider.isVertical()) {
         setMaxImage(UISlider.DEFAULT_VERTICAL_MAX_IMAGE);
      } else {
         setMaxImage(UISlider.DEFAULT_HORIZONTAL_MAX_IMAGE);
      }
      layout();
   }
   public void removeMaxTrackImage() {
      if(uiSlider.isVertical()) {
         setMaxTrackImage(UISlider.DEFAULT_VERTICAL_MAXTRACK_IMAGE);
      } else {
         setMaxTrackImage(UISlider.DEFAULT_HORIZONTAL_MAXTRACK_IMAGE);
      }
      layout();
   }
}
