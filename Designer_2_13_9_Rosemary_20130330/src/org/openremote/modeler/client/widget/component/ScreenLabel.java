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
import org.openremote.modeler.client.widget.propertyform.LabelPropertyForm;
import org.openremote.modeler.client.widget.propertyform.PropertyForm;
import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.component.UILabel;
import org.openremote.modeler.shared.dto.SensorWithInfoDTO;

import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;

/**
 * Label on Screen.
 * 
 * @author Javen Zhang, Dan Cong
 *
 */
public class ScreenLabel extends ScreenComponent {


   protected Text center = new Text();

   private UILabel uiLabel = new UILabel();

   private int stateIndex = -1;
   
   private List<String> states = new ArrayList<String>();
   
   public ScreenLabel(ScreenCanvas canvas, UILabel uiLabel, WidgetSelectionUtil widgetSelectionUtil) {
      super(canvas, widgetSelectionUtil);
      this.uiLabel = uiLabel;
      center.setText(uiLabel.getText());
      initial();
      adjustTextLength();
   }

   /**
    * Initial.
    * 
    */
   protected void initial() {
      setLayout(new CenterLayout());
      center.setStyleAttribute("textAlign", "center");
      center.setStyleAttribute("color", "#" + uiLabel.getColor());
      center.setStyleAttribute("fontSize", uiLabel.getFontSize() + "px");
      center.setStyleAttribute("fontFamily", Constants.DEFAULT_FONT_FAMILY);
      add(center);
      layout();
   }

   public void setText(String text) {
      uiLabel.setText(text);
      adjustTextLength();
   }

   public UILabel getUiLabel() {
      return uiLabel;
   }

   public void setUiLabel(UILabel uiLabel) {
      this.uiLabel = uiLabel;
   }

   public void setColor(String color) {
      uiLabel.setColor(color);
      center.setStyleAttribute("color", "#" + color);
   }

   public void setFontSize(int size) {
      uiLabel.setFontSize(size);
      center.setStyleAttribute("fontSize", size + "px");
   }

   @Override
   public String getName() {
      return uiLabel.getName();
   }

   @Override
   public PropertyForm getPropertiesForm() {
      return new LabelPropertyForm(this, widgetSelectionUtil);
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
   private void adjustTextLength() {
      if (stateIndex == -1) {
         adjustTextLength(getWidth());
      } else {
         setState(states.get(stateIndex));
      }
   }

   private void adjustTextLength(int width) {
      if (center.isVisible()) {
         int ajustLength = (width - 6) / 6;
         if (ajustLength < uiLabel.getText().length()) {
            center.setText(uiLabel.getText().substring(0, ajustLength) + "..");
         } else {
            center.setText(uiLabel.getText());
         }
      }
   }
   
   public void clearSensorStates() {
      stateIndex = -1;
      states.clear();
      setText(uiLabel.getText());
   }
   public void onStateChange() {
      SensorWithInfoDTO sensor = uiLabel.getSensorDTO();
      if (sensor != null && states.isEmpty()) {
         if (sensor.getType() == SensorType.SWITCH) {
            if (!"".equals(uiLabel.getSensorLink().getStateValueByStateName("on"))) {
               states.add(uiLabel.getSensorLink().getStateValueByStateName("on"));
            }
            if (!"".equals(uiLabel.getSensorLink().getStateValueByStateName("off"))) {
               states.add(uiLabel.getSensorLink().getStateValueByStateName("off"));
            }
         } else if (sensor.getType() == SensorType.CUSTOM) {
            SensorLink sensorLink = uiLabel.getSensorLink();
            for (String stateName : sensor.getStateNames()) {
               if (!"".equals(uiLabel.getSensorLink().getStateValueByStateName(stateName))) {
                  states.add(sensorLink.getStateValueByStateName(stateName));
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
         setState(states.get(stateIndex));
      }
   }
   
   private void setState(String state) {
      if (center.isVisible()) {
         int ajustLength = (getWidth() - 6) / 6;
         if (ajustLength < state.length()) {
            center.setText(state.substring(0, ajustLength) + "..");
         } else {
            center.setText(state);
         }
      }
   }
}
