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
package org.openremote.modeler.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 *  The data structure for the ComboBox. Define label and original model field. And getter method.
 * 
 * @param <T> the generics
 * @author Tomsky
 */
public class ComboBoxDataModel<T> extends BaseModelData implements Serializable {
   
   /** The Constant LABEL. */
   private static final String LABEL = "label";
   
   /** The Constant DATA. */
   private static final String DATA = "data";
   
   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = 4414421641647033397L;
   
   /**
    * Instantiates a new combo box data model.
    * 
    * @param label the label
    * @param t the t
    */
   public ComboBoxDataModel(String label, T t) {
      set(LABEL, label);
      set(DATA, t);
   }
   
   /**
    * Gets the label.
    * 
    * @return the label
    */
   public String getLabel() {
      return (String) get(LABEL);
   }
   
   /**
    * Gets the display property.
    * 
    * @return the display property
    */
   public static String getDisplayProperty() {
      return LABEL;
   }
   
   /**
    * Gets the data property.
    * 
    * @return the data property
    */
   public static String getDataProperty() {
      return DATA;
   }

   /**
    * Get the model's label.
    * @return label.
    */
   public String toString() {
      return getLabel();
   }

   /**
    * Gets the data.
    * 
    * @return the data
    */
   @SuppressWarnings("unchecked")
   public T getData() {
      return (T) get(DATA);
   }
}
