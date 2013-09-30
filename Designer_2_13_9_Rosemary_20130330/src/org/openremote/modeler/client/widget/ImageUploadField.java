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

import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.google.gwt.core.client.GWT;

/**
 * This class extends <b>FileUpLoadField</b>, it support upload image file by sending a post request.
 * @author Tomsky, Javen
 *
 */
public class ImageUploadField extends FileUploadField {
   public static final String BASE_ACTION_URL = GWT.getModuleBaseURL() + "fileUploadController.htm?method=uploadImage&uploadFieldName=";
   public final static String IMAGEUPLOADFIELD = "imageUploadField";
   
   private String fieldName = "imageUploadField";
   
   /**
    * create a new ImageUploadField 
    * If the fieldName is null, The default fieldName is : <code>ImageUploadField.IMAGEUPLOADFIELD</code>
    * @param fieldName The name for the field in your form. 
    */
   public ImageUploadField(String fieldName) {
      setFieldLabel("Image");
      if (fieldName != null && fieldName.trim().length() != 0) {
         this.fieldName = fieldName;
         setName(fieldName);
      } else {
         setName(IMAGEUPLOADFIELD);
      }
      setAllowBlank(false);
      setRegex(".+?\\.(png|gif|jpg|jpeg|PNG|GIF|JPG|GPEG)");
      getMessages().setRegexText("Please select a gif, jpg or png type image.");
      setStyleAttribute("overflow", "hidden");
   }
   /**
    * A form just has one action, suppose that there are several ImageUploadFields in a form and you want to upload an image when an image has be selected, 
    * The only thing you can do is to change the form's action to the url that required by this ImageUploadField.
    * <br />
    * This method is used to change the action for a form by binding the form's action to itself. 
    * @param form
    */
   public void setActionToForm(FormPanel form) {
      String action = getActionURL();
      form.setAction(action);
      form.setEncoding(Encoding.MULTIPART);
      form.setMethod(Method.POST);
   }

   public String getUploadFieldName() {
      return fieldName;
   }

   public String getActionURL() {
      return BASE_ACTION_URL + (fieldName != null ? fieldName : IMAGEUPLOADFIELD);
   }
}
