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
package org.openremote.modeler.client.widget.buildingmodeler;

import java.util.Arrays;

import net.customware.gwt.dispatch.client.DispatchAsync;

import org.openremote.modeler.client.ModelerGinjector;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.widget.CommonForm;
import org.openremote.modeler.selenium.DebugId;
import org.openremote.modeler.shared.russound.CreateRussoundDeviceAction;
import org.openremote.modeler.shared.russound.CreateRussoundDeviceResult;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Wizard form for {@link DeviceInfoForm}, this is a part of {@link DeviceWizardWindow}.
 * 
 * @author Dan 2009-8-21
 */
public class RussoundInfoWizardForm extends CommonForm {

  /** The Constant DEVICE_NAME. */
  public static final String DEVICE_NAME = "name";
  
  /** The Constant DEVICE_VENDOR. */
  public static final String DEVICE_VENDOR = "vendor";
  
  /** The Constant DEVICE_MODEL. */
  public static final String DEVICE_MODEL = "model";
  
  /** The Constant CONTROLLERS. */
  public static final String CONTROLLERS = "controllers";
  
  /** The wrapper. */
  final protected Component wrapper;
  
  TextField<String> nameField;
  SimpleComboBox<String> modelField;
  TextField<String> controllerCountField;
  
   /**
    * Instantiates a new device info wizard form.
    * 
    * @param wrapper
    *           the wrapper
    * @param deviceBeanModel
    *           the device bean model
    */
   public RussoundInfoWizardForm(Component parent) {
     super();
     this.wrapper = parent;
     createFields();
     addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
        public void handleEvent(FormEvent be) {
          ModelerGinjector injector = GWT.create(ModelerGinjector.class);
          DispatchAsync dispatcher = injector.getDispatchAsync();

          CreateRussoundDeviceAction action = new CreateRussoundDeviceAction(nameField.getValue(), modelField.getSimpleValue(), Integer.parseInt(controllerCountField.getValue()));
          
          dispatcher.execute(action, new AsyncCallback<CreateRussoundDeviceResult>() {

            @Override
            public void onFailure(Throwable caught) {
              Info.display("ERROR", caught.getMessage());
              
              // TODO: better error reporting
            }

            @Override
            public void onSuccess(CreateRussoundDeviceResult result) {
              
              // TODO: might have an error message in result, handle it
              
              wrapper.fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(result.getDevices()));
            }            
          });
        }          
     });
   }

  /**
    * Creates the fields.
    */
   private void createFields() {
      nameField = new TextField<String>();
      nameField.setName(DEVICE_NAME);
      nameField.ensureDebugId(DebugId.DEVICE_NAME_FIELD);
      nameField.setFieldLabel("Name");
      nameField.setAllowBlank(false);
    
      modelField = new SimpleComboBox<String>();
      modelField.setFieldLabel("Model");  
      modelField.setName(DEVICE_MODEL);
      modelField.ensureDebugId(DebugId.DEVICE_MODEL_FIELD);
      modelField.setAllowBlank(false);
      modelField.setEditable(false);
      modelField.add(Arrays.asList(new String[]{"CA-S44", "CA-A66", "CA-M66", "CA-V66", "MCA-C3", "MCA-C5"}));
      
      controllerCountField = new TextField<String>();
      controllerCountField.setName(CONTROLLERS);
      controllerCountField.setFieldLabel("No of controller");
      controllerCountField.setAllowBlank(false);
     
      add(nameField);
      add(modelField);
      add(controllerCountField);
   }
   
   

   /* (non-Javadoc)
    * @see org.openremote.modeler.client.widget.CommonForm#isNoButton()
    */
   @Override
   public boolean isNoButton() {
      return true;
   }

   /* (non-Javadoc)
    * @see com.extjs.gxt.ui.client.widget.Component#show()
    */
   @Override
   public void show() {
      super.show();
      ((Window) wrapper).setSize(360, 250);
   }
   
   
   

}
