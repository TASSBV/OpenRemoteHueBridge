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

import java.util.ArrayList;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.IconPreviewWidget;
import org.openremote.modeler.client.widget.NavigateFieldSet;
import org.openremote.modeler.client.widget.component.ImageSelectAdapterField;
import org.openremote.modeler.client.widget.component.ScreenButton;
import org.openremote.modeler.client.widget.uidesigner.ImageAssetPicker;
import org.openremote.modeler.client.widget.uidesigner.ImageAssetPicker.ImageAssetPickerListener;
import org.openremote.modeler.client.widget.uidesigner.PropertyPanel;
import org.openremote.modeler.client.widget.uidesigner.SelectCommandWindow;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.Navigate;
import org.openremote.modeler.domain.component.Navigate.ToLogicalType;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.shared.dto.UICommandDTO;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FieldSetEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * A panel for display screen button properties.
 * It includes name, command, default image, pressed image, repeatable and navigation.
 * 
 */
public class ButtonPropertyForm extends PropertyForm {
   private CheckBox repeat = new CheckBox();
   private NavigateFieldSet navigateSet = null;
   
   public ButtonPropertyForm(ScreenButton screenButton, UIButton uiButton, WidgetSelectionUtil widgetSelectionUtil) {
      super(screenButton, widgetSelectionUtil);
      addFields(screenButton, uiButton);
      super.addDeleteButton();
   }
   private void addFields(final ScreenButton screenButton, final UIButton uiButton) {
      // initial name field.
      final TextField<String> name = new TextField<String>();
      name.setFieldLabel("Name");
      name.setValue(uiButton.getName());
      name.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
           // TODO - EBR : Setting the name on the screen button (displayed widget) so that the setter will modify
           // the UIButton (object model) as a side effect is bad design.
           // Call here should only change model and other visual representations should update because they listen to changes on the bus.
//            screenButton.setName(name.getValue());
            
            String buttonName = name.getValue();
            uiButton.setName((buttonName != null)?buttonName:""); // Do not use null as button name, see MODELER-270
            screenButton.adjustTextLength();
         }
      });
      
      // initial command field.
      final Button command = new Button("Select");
      command.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            SelectCommandWindow selectCommandWindow = new SelectCommandWindow();
            selectCommandWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  BeanModel dataModel = be.<BeanModel> getData();
                  UICommandDTO dto = dataModel.getBean();
                  uiButton.setUiCommandDTO(dto);
                  command.setText(dto.getDisplayName());
               }
            });
         }
      });
      if (uiButton.getUiCommandDTO() != null) {
         command.setText(uiButton.getUiCommandDTO().getDisplayName());
      }
      AdapterField adapterCommand = new AdapterField(command);
      adapterCommand.setFieldLabel("Command");
      
      // initial navigate properties
      final Navigate navigate = uiButton.getNavigate();
      Group parentGroup = screenButton.getScreenCanvas().getScreen().getScreenPair().getParentGroup();
      if (parentGroup != null) {
         navigateSet = new NavigateFieldSet(navigate, parentGroup.getParentPanel().getGroups());
      } else {
         navigateSet = new NavigateFieldSet(navigate, new ArrayList<Group>());
      }
      navigateSet.setCheckboxToggle(true);
      navigateSet.addListener(Events.BeforeExpand, new Listener<FieldSetEvent>() {
         @Override
         public void handleEvent(FieldSetEvent be) {
            uiButton.setRepeate(false);
            repeat.setValue(false);
            if (!navigate.isSet()) {
               navigate.setToLogical(ToLogicalType.login);
            }
            navigateSet.update(navigate);
         }
         
      });
      navigateSet.addListener(Events.BeforeCollapse, new Listener<FieldSetEvent>() {
         @Override
         public void handleEvent(FieldSetEvent be) {
            navigate.clear();
         }
      });
      if (navigate.isSet()) {
         navigateSet.fireEvent(Events.BeforeExpand);
      } else {
         navigateSet.collapse();
      }
      
      final ImageSelectAdapterField defaultImageField = new ImageSelectAdapterField("Image");
      if (uiButton.getImage() != null) {
         defaultImageField.setText(uiButton.getImage().getImageFileName());
      }
      defaultImageField.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
           final ImageSource image = uiButton.getImage();
           
           ImageAssetPicker imageAssetPicker = new ImageAssetPicker((image != null)?image.getSrc():null);
           imageAssetPicker.show();
           imageAssetPicker.center();
           imageAssetPicker.setListener(new ImageAssetPickerListener() {
            @Override
            public void imagePicked(String imageURL) {
              screenButton.setIcon(imageURL);
              if (image != null) {
                 image.setSrc(imageURL);
              } else {
                 uiButton.setImage(new ImageSource(imageURL));
              }
              defaultImageField.setText(uiButton.getImage().getImageFileName());              
            }             
           });
         }
      });
      defaultImageField.addDeleteListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            if (uiButton.getImage() != null) {
               defaultImageField.removeImageText();
               uiButton.setImage(null);
               screenButton.removeIcon();
            }
         }
      });
      
      final ImageSelectAdapterField pressImageField = new ImageSelectAdapterField("PressImage");
      if (uiButton.getPressImage() != null) {
         pressImageField.setText(uiButton.getPressImage().getImageFileName());
      }
      pressImageField.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
           final ImageSource image = uiButton.getPressImage();
           
           ImageAssetPicker imageAssetPicker = new ImageAssetPicker((image != null)?image.getSrc():null);
           imageAssetPicker.show();
           imageAssetPicker.center();
           imageAssetPicker.setListener(new ImageAssetPickerListener() {
            @Override
            public void imagePicked(String imageURL) {
              if (image != null) {
                 image.setSrc(imageURL);
              } else {
                 uiButton.setPressImage(new ImageSource(imageURL));
              }
              pressImageField.setText(uiButton.getPressImage().getImageFileName());              
            }
           });
         }
      });
      pressImageField.addDeleteListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            if (uiButton.getPressImage() != null) {
               pressImageField.removeImageText();
               uiButton.setPressImage(null);
            }
         }
      });
      
      CheckBoxGroup repeatCheckBoxGroup = new CheckBoxGroup();
      repeatCheckBoxGroup.setFieldLabel("Repeat");
      repeat.setValue(uiButton.isRepeate());
      repeat.addListener(Events.Change, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            if (repeat.getValue() == true) {
               if (uiButton.getNavigate() != null) {
                  uiButton.getNavigate().setToGroup(-1L);
                  uiButton.getNavigate().setToLogical(null);
               }
            }
            navigateSet.collapse();
            uiButton.setRepeate(repeat.getValue());
         }
      });
      repeatCheckBoxGroup.add(repeat); 
      add(name);
      add(adapterCommand);
      add(defaultImageField);
      add(pressImageField);
      add(repeatCheckBoxGroup);
      add(navigateSet);
      
   }

   /**
    * @param screenButton
    * @param imageSource
    * @return
    */
   private IconPreviewWidget createIconPreviewWidget(final ScreenButton screenButton, final ImageSource imageSource) {
     // TODO EBR : UIButton should be passed instead of ScreenButton, but UIButton does not have width/height
     // The Absolute it is part of has or it can compute it via Cell/Grid
      IconPreviewWidget previewWidget = new IconPreviewWidget(screenButton.getWidth(), screenButton.getHeight());
      previewWidget.setText(screenButton.getName());
      if (imageSource != null) {
         previewWidget.setIcon(imageSource.getSrc());
      }
      return previewWidget;
   }
   @Override
   protected void afterRender() {
      super.afterRender();
      ((PropertyPanel)this.getParent()).setHeading("Button properties");
   }
   
}
