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
package org.openremote.modeler.client.widget.propertyform;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.client.utils.SensorLink;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.ComboBoxExt;
import org.openremote.modeler.client.widget.component.ImageSelectAdapterField;
import org.openremote.modeler.client.widget.component.ScreenImage;
import org.openremote.modeler.client.widget.uidesigner.ImageAssetPicker;
import org.openremote.modeler.client.widget.uidesigner.ImageAssetPicker.ImageAssetPickerListener;
import org.openremote.modeler.client.widget.uidesigner.PropertyPanel;
import org.openremote.modeler.client.widget.uidesigner.SelectSensorWindow;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.State;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.UIImage;
import org.openremote.modeler.domain.component.UILabel;
import org.openremote.modeler.shared.dto.SensorWithInfoDTO;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * A panel for display screen Image properties.
 */
public class ImagePropertyForm extends PropertyForm {
   private ScreenImage screenImage = null;

   private FieldSet statesPanel; 
   
   public ImagePropertyForm(ScreenImage screenImage, WidgetSelectionUtil widgetSelectionUtil) {
      super(screenImage, widgetSelectionUtil);
      this.screenImage = screenImage;
      addFields(screenImage);
      createSensorStates();
      super.addDeleteButton();
   }
   private void addFields(final ScreenImage screenImage) {
      this.setLabelWidth(70);
      final UIImage uiImage = screenImage.getUiImage();
      
      final Button sensorSelectBtn = new Button("Select");
      if(screenImage.getUiImage().getSensorDTO()!=null){
         sensorSelectBtn.setText(screenImage.getUiImage().getSensorDTO().getDisplayName());
      }
      sensorSelectBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            SelectSensorWindow selectSensorWindow = new SelectSensorWindow();
            selectSensorWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  BeanModel dataModel = be.<BeanModel> getData();
                  SensorWithInfoDTO sensorDTO = dataModel.getBean();
                  uiImage.setSensorDTOAndInitSensorLink(sensorDTO);
                  sensorSelectBtn.setText(sensorDTO.getDisplayName());
                  if (sensorDTO.getType() == SensorType.SWITCH || sensorDTO.getType()==SensorType.CUSTOM) {
                     statesPanel.show();
                     createSensorStates();
                  } else {
                     statesPanel.hide();
                  }
                  screenImage.clearSensorStates();
               }
            });
         }
      });
      
     
      ComboBox<ModelData> labelBox = createLabelSelector();
      
      add(createImageUploader());
      AdapterField sensorAdapter = new AdapterField(sensorSelectBtn);
      sensorAdapter.setFieldLabel("Sensor");
      add(sensorAdapter);
      
      add(labelBox);
      
      statesPanel = new FieldSet();
      FormLayout layout = new FormLayout();
      layout.setLabelWidth(65);
      layout.setDefaultWidth(150);
      statesPanel.setLayout(layout);
      statesPanel.setHeading("Sensor State");
      add(statesPanel);
      SensorWithInfoDTO sensor = screenImage.getUiImage().getSensorDTO();
      if (sensor == null) {
         statesPanel.hide();
      } else if (sensor.getType() != SensorType.SWITCH && sensor.getType() != SensorType.CUSTOM) {
         statesPanel.hide();
      }
   }
   @SuppressWarnings("unchecked")
   private ComboBox<ModelData> createLabelSelector() {
      ComboBox<ModelData> labelBox = new ComboBoxExt();
      labelBox.setFieldLabel("FallbackLabel");
      Collection<UILabel> labelsonScreen = (Collection<UILabel>) screenImage.getScreenCanvas().getScreen().getAllUIComponentByType(UILabel.class);
      ListStore<ModelData> labelStore = new ListStore<ModelData>();
      for (UILabel label : labelsonScreen) {
         if (!label.isRemoved()) {
            ComboBoxDataModel<UILabel> labelModel = new ComboBoxDataModel<UILabel>(label.getDisplayName(), label);
            labelStore.add(labelModel);
         }
      }
      //set the label for the image. 
      if(screenImage.getUiImage().getLabel()!=null && !screenImage.getUiImage().getLabel().isRemoved()){
         labelBox.setValue(new ComboBoxDataModel<UILabel>(screenImage.getUiImage().getLabel().getDisplayName(),screenImage.getUiImage().getLabel()));
      }
      labelBox.setStore(labelStore);
      labelBox.addSelectionChangedListener(new SelectionChangedListener<ModelData>(){

         @Override
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            ComboBoxDataModel<ModelData> labelData = (ComboBoxDataModel<ModelData>) se.getSelectedItem();
            UILabel label = (UILabel) labelData.getData();
            screenImage.getUiImage().setLabel(label);
         }
         
      });
      return labelBox;
   }
   
   private ImageSelectAdapterField createImageUploader() {
     final ImageSelectAdapterField imageSrcField = new ImageSelectAdapterField("Image");
     imageSrcField.setText(screenImage.getUiImage().getImageSource().getImageFileName());
     imageSrcField.addSelectionListener(new SelectionListener<ButtonEvent>() {
        @Override
        public void componentSelected(ButtonEvent ce) {
          final ImageSource image = screenImage.getUiImage().getImageSource();
          
          ImageAssetPicker imageAssetPicker = new ImageAssetPicker((image != null)?image.getSrc():null);
          imageAssetPicker.show();
          imageAssetPicker.center();
          imageAssetPicker.setListener(new ImageAssetPickerListener() {
           @Override
           public void imagePicked(String imageURL) {
             screenImage.setImageSource(new ImageSource(imageURL));
             imageSrcField.setText(screenImage.getUiImage().getImageSource().getImageFileName());
           }             
          });
        }
     });
     imageSrcField.addDeleteListener(new SelectionListener<ButtonEvent>() {
       public void componentSelected(ButtonEvent ce) {
          if (!UIImage.DEFAULT_IMAGE_URL.equals(screenImage.getUiImage().getImageSource().getSrc())){
             screenImage.setImageSource(new ImageSource(UIImage.DEFAULT_IMAGE_URL));
             imageSrcField.setText(screenImage.getUiImage().getImageSource().getImageFileName());
          }
       }
    });
     return imageSrcField;
   }
   private void createSensorStates(){
      statesPanel.removeAll();
      final SensorLink sensorLink = screenImage.getUiImage().getSensorLink();
      if(screenImage.getUiImage().getSensorDTO()!=null && screenImage.getUiImage().getSensorDTO().getType()==SensorType.SWITCH){
        final ImageSelectAdapterField onImageUploadField = new ImageSelectAdapterField("on");
        onImageUploadField.addSelectionListener(new SelectionListener<ButtonEvent>() {
           @Override
           public void componentSelected(ButtonEvent ce) {
             ImageAssetPicker imageAssetPicker = new ImageAssetPicker((sensorLink != null)?sensorLink.getStateValueByStateName("on"):null);
             imageAssetPicker.show();
             imageAssetPicker.center();
             imageAssetPicker.setListener(new ImageAssetPickerListener() {
              @Override
              public void imagePicked(String imageURL) {
                Map<String,String> sensorAttrMap = new HashMap<String,String>();
                sensorAttrMap.put("name","on");
                sensorAttrMap.put("value", imageURL.substring(imageURL.lastIndexOf("/")+1));
                SensorLink sensorLink = screenImage.getUiImage().getSensorLink();
                sensorLink.addOrUpdateChildForSensorLinker("state", sensorAttrMap);
                screenImage.clearSensorStates();
                onImageUploadField.setText(sensorLink.getStateValueByStateName("on"));
              }             
             });
           }
        });
        onImageUploadField.addDeleteListener(new SelectionListener<ButtonEvent>() {
          public void componentSelected(ButtonEvent ce) {
            removeSensorImage("on");
          }
       });
        final ImageSelectAdapterField offImageUploadField = new ImageSelectAdapterField("off");
        offImageUploadField.addSelectionListener(new SelectionListener<ButtonEvent>() {
           @Override
           public void componentSelected(ButtonEvent ce) {
             ImageAssetPicker imageAssetPicker = new ImageAssetPicker((sensorLink != null)?sensorLink.getStateValueByStateName("off"):null);
             imageAssetPicker.show();
             imageAssetPicker.center();
             imageAssetPicker.setListener(new ImageAssetPickerListener() {
              @Override
              public void imagePicked(String imageURL) {
                Map<String,String> sensorAttrMap = new HashMap<String,String>();
                sensorAttrMap.put("name","off");
                sensorAttrMap.put("value", imageURL.substring(imageURL.lastIndexOf("/")+1));
                SensorLink sensorLink = screenImage.getUiImage().getSensorLink();
                sensorLink.addOrUpdateChildForSensorLinker("state", sensorAttrMap);
                screenImage.clearSensorStates();
                offImageUploadField.setText(sensorLink.getStateValueByStateName("off"));
              }             
             });
           }
        });
        offImageUploadField.addDeleteListener(new SelectionListener<ButtonEvent>() {
          public void componentSelected(ButtonEvent ce) {
            removeSensorImage("off");
          }
       });
        
        // TODO EBR : refactor to avoid code duplication

        if (sensorLink!=null) {
            onImageUploadField.setText(sensorLink.getStateValueByStateName("on"));
            offImageUploadField.setText(sensorLink.getStateValueByStateName("off"));
         }
        
         statesPanel.add(onImageUploadField);
         statesPanel.add(offImageUploadField);
      }else if(screenImage.getUiImage().getSensorDTO()!=null && screenImage.getUiImage().getSensorDTO().getType() == SensorType.CUSTOM){
        SensorWithInfoDTO customSensor = screenImage.getUiImage().getSensorDTO();
        List<String> stateNames = customSensor.getStateNames();
        for(final String stateName: stateNames){
           final ImageSelectAdapterField imageUploaderField = new ImageSelectAdapterField(stateName);
           imageUploaderField.addSelectionListener(new SelectionListener<ButtonEvent>() {
              @Override
              public void componentSelected(ButtonEvent ce) {
                ImageAssetPicker imageAssetPicker = new ImageAssetPicker((sensorLink != null)?sensorLink.getStateValueByStateName(stateName):null);
                imageAssetPicker.show();
                imageAssetPicker.center();
                imageAssetPicker.setListener(new ImageAssetPickerListener() {
                 @Override
                 public void imagePicked(String imageURL) {
                   Map<String,String> sensorAttrMap = new HashMap<String,String>();
                   sensorAttrMap.put("name", stateName);
                   sensorAttrMap.put("value", imageURL.substring(imageURL.lastIndexOf("/")+1));
                   SensorLink sensorLink = screenImage.getUiImage().getSensorLink();
                   sensorLink.addOrUpdateChildForSensorLinker("state", sensorAttrMap);
                   screenImage.clearSensorStates();
                   imageUploaderField.setText(sensorLink.getStateValueByStateName(stateName));
                 }             
                });
              }
           });
           imageUploaderField.addDeleteListener(new SelectionListener<ButtonEvent>() {
             public void componentSelected(ButtonEvent ce) {
               removeSensorImage(stateName);
             }
          });
           if(sensorLink!=null) {
             imageUploaderField.setText(sensorLink.getStateValueByStateName(stateName));
          }
            statesPanel.add(imageUploaderField);
            
         }
      }
      statesPanel.layout(true);
   }
   
   @Override
   protected void afterRender() {
      super.afterRender();
      ((PropertyPanel)this.getParent()).setHeading("Image properties");
   }
   
   private void removeSensorImage(String stateName) {
      String sensorValue = screenImage.getUiImage().getSensorLink().getStateValueByStateName(stateName);
      if (!"".equals(sensorValue)) {
         screenImage.clearSensorStates();
         Map<String,String> sensorAttrMap = new HashMap<String,String>();
         sensorAttrMap.put("name", stateName);
         sensorAttrMap.put("value", sensorValue);
         screenImage.getUiImage().getSensorLink().removeChildForSensorLinker("state", sensorAttrMap);
         createSensorStates();
      }
   }
}
