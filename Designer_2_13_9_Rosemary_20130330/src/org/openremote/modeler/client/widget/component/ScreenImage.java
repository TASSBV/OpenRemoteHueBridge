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

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.utils.SensorLink;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.propertyform.ImagePropertyForm;
import org.openremote.modeler.client.widget.propertyform.PropertyForm;
import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.State;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.UIImage;
import org.openremote.modeler.shared.dto.SensorWithInfoDTO;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Image;

/**
 * The Class ScreenButton. It display as a style box, can be adjust size.
 */
public class ScreenImage extends ScreenComponent {


   /** The btnTable center text. */
   protected Image image = new Image();

   private UIImage uiImage = new UIImage();

   private int stateIndex = -1;
   
   private List<String> states = new ArrayList<String>();
   
   public ScreenImage(ScreenCanvas canvas, UIImage uiImage, WidgetSelectionUtil widgetSelectionUtil) {
      super(canvas, widgetSelectionUtil);
      this.uiImage = uiImage;
      initial();
   }

   /**
    * Initial.
    * 
    */
   protected void initial() {
      image.setStyleName("screen-image");
      if (!"".equals(uiImage.getImageSource().getSrc().trim())) {
         image.setUrl(uiImage.getImageSource().getSrc());
      }
      add(image);
      layout();
   }

   public UIImage getUiImage() {
      return uiImage;
   }

   public void setUiImage(UIImage uiImage) {
      this.uiImage = uiImage;
   }

   public void setImageSource(ImageSource imageURL) {
      uiImage.setImageSource(imageURL);
      image.setUrl(imageURL.getSrc());
   }

   @Override
   public String getName() {
      return uiImage.getName();
   }

   @Override
   public PropertyForm getPropertiesForm() {
      return new ImagePropertyForm(this, widgetSelectionUtil);
   }

   public void clearSensorStates() {
      stateIndex = -1;
      states.clear();
      image.setUrl(uiImage.getImageSource().getSrc());
   }
   
   public void onStateChange() {
      SensorWithInfoDTO sensor = uiImage.getSensorDTO();
      if (sensor != null && states.isEmpty()) {
         String resourcePath = Cookies.getCookie(Constants.CURRETN_RESOURCE_PATH);
         if (resourcePath != null) {
            if (sensor.getType() == SensorType.SWITCH) {
               if (!"".equals(uiImage.getSensorLink().getStateValueByStateName("on"))) {
                  states.add(resourcePath + uiImage.getSensorLink().getStateValueByStateName("on"));
               }
               if (!"".equals(uiImage.getSensorLink().getStateValueByStateName("off"))) {
                  states.add(resourcePath + uiImage.getSensorLink().getStateValueByStateName("off"));
               }
            } else if (sensor.getType() == SensorType.CUSTOM) {
               SensorLink sensorLink = uiImage.getSensorLink();
               for (String stateName : sensor.getStateNames()) {
                  if (!"".equals(uiImage.getSensorLink().getStateValueByStateName(stateName))) {
                     states.add(resourcePath + sensorLink.getStateValueByStateName(stateName));
                  }
               }
            }
         }
      }
      
      if (!states.isEmpty()) {
         if (stateIndex < states.size() - 1) {
            stateIndex = stateIndex + 1;
         } else if (stateIndex == states.size() - 1) {
            stateIndex = 0;
         }
         image.setUrl(states.get(stateIndex));
      }
   }
}
