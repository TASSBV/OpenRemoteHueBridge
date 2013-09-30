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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * The subclass of <b>SensorRefItem</b>, which define a relation between a sensor and a slider.
 * A slider can have a SliderSensorRef.
 */
@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("SLIDER_SENSOR_REF")
public class SliderSensorRef extends SensorRefItem {

   private Slider slider;

   public SliderSensorRef() {
   }

   public SliderSensorRef(Slider slider) {
      this.slider = slider;
   }

   @OneToOne
   @JoinColumn(name = "slider_oid")
   public Slider getSlider() {
      return slider;
   }

   public void setSlider(Slider slider) {
      this.slider = slider;
   }

}
