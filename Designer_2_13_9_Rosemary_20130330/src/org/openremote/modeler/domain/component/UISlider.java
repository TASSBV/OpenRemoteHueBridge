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
import java.util.Collection;
import java.util.List;

import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.shared.dto.SensorWithInfoDTO;
import org.openremote.modeler.shared.dto.SliderWithInfoDTO;

import flexjson.JSON;

/**
 * There are two types of UISlider(vertical and horizontal). 
 * It contains a slider entity, five predefined images, and the images can be changed.
 */
public class UISlider extends UIControl implements SensorOwner, ImageSourceOwner {
   // Default horizontal images.
   public static final String DEFAULT_HORIZONTAL_MIN_IMAGE = "./resources/images/custom/slider/min.png";
   public static final String DEFAULT_HORIZONTAL_MINTRACK_IMAGE = "./resources/images/custom/slider/minTrack.png";
   public static final String DEFAULT_HORIZONTAL_THUMB_IMAGE = "./resources/images/custom/slider/thumb.png";
   public static final String DEFAULT_HORIZONTAL_MAXTRACK_IMAGE = "./resources/images/custom/slider/maxTrack.png";
   public static final String DEFAULT_HORIZONTAL_MAX_IMAGE = "./resources/images/custom/slider/max.png";
   
   // Default vertical images.
   public static final String DEFAULT_VERTICAL_MIN_IMAGE = "./resources/images/custom/slider/vmin.png";
   public static final String DEFAULT_VERTICAL_MINTRACK_IMAGE = "./resources/images/custom/slider/vminTrack.png";
   public static final String DEFAULT_VERTICAL_THUMB_IMAGE = "./resources/images/custom/slider/vthumb.png";
   public static final String DEFAULT_VERTICAL_MAXTRACK_IMAGE = "./resources/images/custom/slider/vmaxTrack.png";
   public static final String DEFAULT_VERTICAL_MAX_IMAGE = "./resources/images/custom/slider/vmax.png";
   
   private static final long serialVersionUID = 4821886776184406692L;
   
   /** Indicate the slider is vertical or horizontal, default is horizontal. */
   private boolean vertical = false;
   private ImageSource thumbImage = new ImageSource("");;
   private ImageSource minImage = new ImageSource("");
   private ImageSource minTrackImage = new ImageSource("");
   private ImageSource maxImage = new ImageSource("");
   private ImageSource maxTrackImage = new ImageSource("");
   private Slider slider;
   private SliderWithInfoDTO sliderDTO;
   
   public UISlider() {
   }
   
   public UISlider(long id) {
      super(id);
   }
   
   /**
    * Instantiates a new ui slider by copying a ui slider's properties.
    * 
    * @param uiSlider the ui slider
    */
   public UISlider(UISlider uiSlider) {
      this.setOid(uiSlider.getOid());
      this.vertical = uiSlider.isVertical();
      this.thumbImage = uiSlider.getThumbImage();
      this.minImage = uiSlider.getMinImage();
      this.minTrackImage = uiSlider.getMinTrackImage();
      this.maxImage = uiSlider.getMaxImage();
      this.maxTrackImage = uiSlider.getMaxTrackImage();
   }
   public boolean isVertical() {
      return vertical;
   }

   public void setVertical(boolean vertical) {
      this.vertical = vertical;
   }
  
   public ImageSource getThumbImage() {
      return thumbImage;
   }

   public void setThumbImage(ImageSource thumbImage) {
      this.thumbImage = thumbImage;
   }

   public ImageSource getMinImage() {
      return minImage;
   }

   public void setMinImage(ImageSource minImage) {
      this.minImage = minImage;
   }

   public ImageSource getMinTrackImage() {
      return minTrackImage;
   }

   public void setMinTrackImage(ImageSource minTrackImage) {
      this.minTrackImage = minTrackImage;
   }

   public ImageSource getMaxImage() {
      return maxImage;
   }

   public void setMaxImage(ImageSource maxImage) {
      this.maxImage = maxImage;
   }

   public ImageSource getMaxTrackImage() {
      return maxTrackImage;
   }

   public void setMaxTrackImage(ImageSource maxTrackImage) {
      this.maxTrackImage = maxTrackImage;
   }

   public Slider getSlider() {
      return slider;
   }

   public void setSlider(Slider slider) {
      this.slider = slider;
   }

   public SliderWithInfoDTO getSliderDTO() {
    return sliderDTO;
  }

  public void setSliderDTO(SliderWithInfoDTO sliderDTO) {
    this.sliderDTO = sliderDTO;
  }

  @Override
   public List<UICommand> getCommands() {
      List<UICommand> commands = new ArrayList<UICommand>();
      if (slider != null && slider.getSetValueCmd() != null) {
         commands.add(slider.getSetValueCmd());
      }
      return commands;
   }

   @Override
   public String getPanelXml() {
      StringBuffer xmlContent = new StringBuffer();
      xmlContent.append("        <slider id=\"" + getOid() + "\" ");
      if (isThumbUploaded()) {
         xmlContent.append("thumbImage=\"" + thumbImage.getImageFileName() + "\" ");
      }
      if (vertical) {
         xmlContent.append("vertical=\"true\" ");
      }
      if (slider == null || slider.getSetValueCmd() == null) {
         xmlContent.append("passive=\"true\" ");
      }
      xmlContent.append(">\n");
      int min = 0;
      int max = 100;
      if (getSensor() != null) {
         Sensor sensor = getSensor();
         if (sensor.getType() == SensorType.RANGE || sensor.getType() == SensorType.LEVEL) {
            xmlContent.append("<link type=\"sensor\" ref=\"" + sensor.getOid() + "\" />\n");
            if (sensor.getType() == SensorType.RANGE) {
               RangeSensor rangeSensor = (RangeSensor) getSensor();
               min = rangeSensor.getMin();
               max = rangeSensor.getMax();
            }
         }
      }
      xmlContent.append("<min value=\"" + min + "\"");
      if (isMinImageUploaded()) {
         xmlContent.append(" image=\"" + minImage.getImageFileName() + "\"");
      }
      if (isMinTrackImageUploaded()) {
         xmlContent.append(" trackImage=\"" + minTrackImage.getImageFileName() + "\"");
      }
      xmlContent.append("/>\n");

      xmlContent.append("<max value=\"" + max + "\" ");
      if (isMaxImageUploaded()) {
         xmlContent.append("image=\"" + maxImage.getImageFileName() + "\" ");
      }
      if (isMaxTrackImageUploaded()) {
         xmlContent.append("trackImage=\"" + maxTrackImage.getImageFileName() + "\" ");
      }
      xmlContent.append("/>\n");
      xmlContent.append("        </slider>\n");
      return xmlContent.toString();
   }


   @Override
   public String getName() {
      return "Slider";
   }

   /**
    * The vertical predefined width is 44, and the horizontal is 198.
    * 
    * @see org.openremote.modeler.domain.component.UIComponent#getPreferredWidth()
    */
   @Override
   public int getPreferredWidth() {
      return vertical ? 44 : 198;
   }

   /**
    * The vertical predefined height is 198, and the horizontal is 44.
    * 
    * @see org.openremote.modeler.domain.component.UIComponent#getPreferredHeight()
    */
   @Override
   public int getPreferredHeight() {
      return vertical ? 198 : 44;
   }

   @Override
   public Sensor getSensor() {
      if(slider!= null && slider.getSliderSensorRef()!=null){
         return slider.getSliderSensorRef().getSensor();
      }
      return null;
   }

   @Override
   public void setSensor(Sensor sensor) {
      if(slider!= null && slider.getSliderSensorRef()!=null){
         slider.getSliderSensorRef().setSensor(sensor);
      }
   }

   @Override
   public void setSensorDTO(SensorWithInfoDTO sensorDTO) {
     // TODO EBR : Just to comply to interface, but this class should not implement this interface, never has direct access to sensor, only through Slider
   }
   
   public SensorWithInfoDTO getSensorDTO() {
     return null;
     // TODO EBR : As above
   }
   
   @Override
   @JSON(include = false)
   public Collection<ImageSource> getImageSources() {
      Collection<ImageSource> imageSources = new ArrayList<ImageSource>(2);
      if (this.minImage != null && !this.minImage.isEmpty() && isMinImageUploaded()) {
         imageSources.add(minImage);
      }
      
      if (this.minTrackImage != null && ! this.minTrackImage.isEmpty() && isMinTrackImageUploaded()) {
         imageSources.add(minTrackImage);
      }
      
      if (this.thumbImage != null && ! this.thumbImage.isEmpty() && isThumbUploaded()) {
         imageSources.add(thumbImage);
      }
      
      if (this.maxTrackImage != null && ! this.maxTrackImage.isEmpty() && isMaxTrackImageUploaded()) {
         imageSources.add(maxTrackImage);
      }
      
      if (this.maxImage != null && ! this.maxImage.isEmpty() && isMaxImageUploaded()) {
         imageSources.add(maxImage);
      }
      return imageSources;
   }
   
   /**
    * Not upload the default min image.
    * 
    * @return true, if is min image uploaded
    */
   public boolean isMinImageUploaded() {
      if (minImage != null && !minImage.isEmpty()) {
         String imageURL = minImage.getSrc();
         return !(DEFAULT_HORIZONTAL_MIN_IMAGE.equals(imageURL) || DEFAULT_VERTICAL_MIN_IMAGE.equals(imageURL)); 
      }
      return false;
   }
   
   /**
    * Not upload the default min track image.
    * 
    * @return true, if is min track image uploaded
    */
   public boolean isMinTrackImageUploaded() {
         if (minTrackImage != null && !minTrackImage.isEmpty()) {
         String imageURL = minTrackImage.getSrc();
         return !(DEFAULT_HORIZONTAL_MINTRACK_IMAGE.equals(imageURL) || DEFAULT_VERTICAL_MINTRACK_IMAGE.equals(imageURL));
      }
      return false;
   }
   
   /**
    * Not upload the default thumb image.
    * 
    * @return true, if is thumb uploaded
    */
   public boolean isThumbUploaded() {
      if (thumbImage !=null && !thumbImage.isEmpty()) {
         String imageURL = thumbImage.getSrc();
         return !(DEFAULT_HORIZONTAL_THUMB_IMAGE.equals(imageURL) || DEFAULT_VERTICAL_THUMB_IMAGE.equals(imageURL)); 
      } 
      return false;
   }
   
   /**
    * Not upload the default max track image.
    * 
    * @return true, if is max track image uploaded
    */
   public boolean isMaxTrackImageUploaded() {
      if (maxTrackImage != null && !maxTrackImage.isEmpty()) {
         String imageURL = maxTrackImage.getSrc();
         return !(DEFAULT_HORIZONTAL_MAXTRACK_IMAGE.equals(imageURL) || DEFAULT_VERTICAL_MAXTRACK_IMAGE.equals(imageURL)); 
      }
      return false;
   }
   
   /**
    * Not upload the default max image.
    * 
    * @return true, if is max image uploaded
    */
   public boolean isMaxImageUploaded() {
      if (maxImage != null && !maxImage.isEmpty()) {
         String imageURL = maxImage.getSrc();
         return !(DEFAULT_HORIZONTAL_MAX_IMAGE.equals(imageURL) || DEFAULT_VERTICAL_MAX_IMAGE.equals(imageURL)); 
      }
      return false;
   }
}
