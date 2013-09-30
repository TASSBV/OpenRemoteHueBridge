/*
 * OpenRemote, the Home of the Digital Home.
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

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.component.ImageSelectAdapterField;
import org.openremote.modeler.client.widget.component.ScreenSwitch;
import org.openremote.modeler.client.widget.uidesigner.ImageAssetPicker;
import org.openremote.modeler.client.widget.uidesigner.ImageAssetPicker.ImageAssetPickerListener;
import org.openremote.modeler.client.widget.uidesigner.PropertyPanel;
import org.openremote.modeler.client.widget.uidesigner.SelectSwitchWindow;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.UISwitch;
import org.openremote.modeler.shared.dto.SwitchWithInfoDTO;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;

/**
 * A panel for display screen switch properties. 
 */
public class SwitchPropertyForm extends PropertyForm {

   public SwitchPropertyForm(ScreenSwitch screenSwitch, UISwitch uiSwitch, WidgetSelectionUtil widgetSelectionUtil) {
      super(screenSwitch, widgetSelectionUtil);
      setLabelWidth(90);
      addFields(screenSwitch, uiSwitch);
      super.addDeleteButton();
   }
   
   private void addFields(final ScreenSwitch screenSwitch, final UISwitch uiSwitch) {
      final ImageSelectAdapterField imageONField = new ImageSelectAdapterField("Image(ON)");
      if (uiSwitch.getOnImage() != null) {
         imageONField.setText(uiSwitch.getOnImage().getImageFileName());
      }
      imageONField.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
           final ImageSource image = uiSwitch.getOnImage();
           
           ImageAssetPicker imageAssetPicker = new ImageAssetPicker((image != null)?image.getSrc():null);
           imageAssetPicker.show();
           imageAssetPicker.center();
           imageAssetPicker.setListener(new ImageAssetPickerListener() {
            @Override
            public void imagePicked(String imageURL) {
              if (image != null) {
                 image.setSrc(imageURL);
              } else {
                 uiSwitch.setOnImage(new ImageSource(imageURL));
              }
              imageONField.setText(uiSwitch.getOnImage().getImageFileName());
              screenSwitch.setIcon(imageURL);
            }             
           });
         }
      });
      imageONField.addDeleteListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            if (uiSwitch.getOnImage() != null) {
               imageONField.removeImageText();
               screenSwitch.removeIcon();
               uiSwitch.setOnImage(null);
            }
         }
      });

      final ImageSelectAdapterField imageOFFField = new ImageSelectAdapterField("Image(OFF)");
      if (uiSwitch.getOffImage() != null) {
         imageOFFField.setText(uiSwitch.getOffImage().getImageFileName());
      }
      imageOFFField.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
           final ImageSource image = uiSwitch.getOffImage();
           
           ImageAssetPicker imageAssetPicker = new ImageAssetPicker((image != null)?image.getSrc():null);
           imageAssetPicker.show();
           imageAssetPicker.center();
           imageAssetPicker.setListener(new ImageAssetPickerListener() {
            @Override
            public void imagePicked(String imageURL) {
              if (image != null) {
                 image.setSrc(imageURL);
              } else {
                 uiSwitch.setOffImage(new ImageSource(imageURL));
              }
              imageOFFField.setText(uiSwitch.getOffImage().getImageFileName());
            }             
           });
         }
      });
      imageOFFField.addDeleteListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            if (uiSwitch.getOffImage() != null) {
               imageOFFField.removeImageText();
               uiSwitch.setOffImage(null);
               if (uiSwitch.getOnImage() != null) {
                  screenSwitch.setIcon(uiSwitch.getOnImage().getSrc());
               }
            }
         }
      });
      
      Button switchCommand = new Button("Select");
      if (uiSwitch.getSwitchDTO() != null) {
         switchCommand.setText(uiSwitch.getSwitchDTO().getDisplayName());
      }
      switchCommand.addSelectionListener(createSelectionListener(uiSwitch, switchCommand));
      AdapterField adapterSwitchCommand = new AdapterField(switchCommand);
      adapterSwitchCommand.setFieldLabel("SwitchCommand");
      adapterSwitchCommand.setAutoHeight(true);

      add(imageONField);
      add(imageOFFField);
      add(adapterSwitchCommand);
   }

   /**
    * @param uiSwitch
    * @param command
    * @return
    */
   private SelectionListener<ButtonEvent> createSelectionListener(final UISwitch uiSwitch, final Button command) {
      return new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            SelectSwitchWindow selectSwitchWindow = new SelectSwitchWindow();
            selectSwitchWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  BeanModel dataModel = be.<BeanModel> getData();
                  if (dataModel.getBean() instanceof SwitchWithInfoDTO) {
                    SwitchWithInfoDTO switchDTO = dataModel.getBean();
                     uiSwitch.setSwitchDTO(switchDTO);
                     command.setText(switchDTO.getDisplayName());
                  }
               }
            });
         
         }
      };
   }

   @Override
   protected void afterRender() {
      super.afterRender();
      ((PropertyPanel)this.getParent()).setHeading("Switch properties");
   }
}
