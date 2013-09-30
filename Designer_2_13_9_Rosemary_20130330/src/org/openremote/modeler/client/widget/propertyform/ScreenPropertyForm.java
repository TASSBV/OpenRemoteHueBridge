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
import java.util.List;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.component.ImageSelectAdapterField;
import org.openremote.modeler.client.widget.uidesigner.GestureWindow;
import org.openremote.modeler.client.widget.uidesigner.ImageAssetPicker;
import org.openremote.modeler.client.widget.uidesigner.ImageAssetPicker.ImageAssetPickerListener;
import org.openremote.modeler.client.widget.uidesigner.PropertyPanel;
import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.Background;
import org.openremote.modeler.domain.Background.RelativeType;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.component.Gesture;
import org.openremote.modeler.domain.component.ImageSource;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
/**
 * A property form for editing the screen's property . 
 * @author Javen
 *
 */
public class ScreenPropertyForm extends PropertyForm {
   public static final String SCREEN_NAME = "screenName";

   public static final String SCREEN_BACKGROUND = "background";
   
   public static final String SCREEN_RELATIVE = "relative";
   
   public static final String SCREEN_ABSOLUTE = "absolute";
   
   public static final String FILL_SCREEN = "Fill screen ";
   
   public static final String yesFill = "true";

   public static final String notFill = "false";
   
   private RadioGroup whetherFillScreen; 
   private ScreenCanvas canvas = null;
  
   public ScreenPropertyForm(ScreenCanvas canvas, WidgetSelectionUtil widgetSelectionUtil) {
      super(canvas, widgetSelectionUtil);
      this.canvas = canvas;
      createFields();
   }
   
   @SuppressWarnings("unchecked")
   private void createFields() {
      
      FieldSet positionSet = new FieldSet();
      FormLayout layout = new FormLayout();
      layout.setLabelWidth(80);
      layout.setDefaultWidth(110);
      positionSet.setLayout(layout);
      positionSet.setHeading("Position");

      setLabelWidth(80);
      
      ImageSelectAdapterField background = createBackgroundField();
      
      whetherFillScreen = createScreenFillerField(positionSet);
      
      TextField<String> posLeftField = createLeftSetField();
      TextField<String> posTopField = createTopSetField();
      
      createPositionField(positionSet,posLeftField,posTopField);
      positionSet.add(posLeftField);
      positionSet.add(posTopField);
      if(canvas.getScreen().getBackground().isFillScreen()){
         positionSet.hide();
      }
      this.add(background);
      this.add(whetherFillScreen);
      this.add(positionSet);
      this.add(createGestureField());
   }

   private TextField<String> createTopSetField() {
      final TextField<String> posTopField = new TextField<String>();
      posTopField.setName("posTop");
      posTopField.setFieldLabel("Top");
      posTopField.setAllowBlank(false);
      posTopField.setRegex(Constants.REG_INTEGER);
      posTopField.getMessages().setRegexText("The top must be a integer");
      posTopField.setValue("0"); // temp set top 0
      posTopField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            Background bkGrd = canvas.getScreen().getBackground();
            bkGrd.setTop(Integer.parseInt(posTopField.getValue()));
            canvas.updateGround();
         }
      });
      posTopField.setLabelStyle("text-align:right;");
      posTopField.setValue(canvas.getScreen().getBackground().getTop()+"");
      return posTopField;
   }

   private TextField<String> createLeftSetField() {
      final TextField<String> posLeftField = new TextField<String>();
      posLeftField.setName("posLeft");
      posLeftField.setFieldLabel("Left");
      posLeftField.setAllowBlank(false);
      posLeftField.setRegex(Constants.REG_INTEGER);
      posLeftField.getMessages().setRegexText("The left must be a integer");
      posLeftField.setValue("0"); // temp set left 0
      posLeftField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            Background bkGrd = canvas.getScreen().getBackground();
            bkGrd.setLeft(Integer.parseInt(posLeftField.getValue()));
            canvas.updateGround();
         }
      });
      posLeftField.setLabelStyle("text-align:right;");
      posLeftField.setValue(canvas.getScreen().getBackground().getLeft()+"");
      return posLeftField;
   }

   private void createPositionField(FieldSet positionSet, final TextField<? extends Object>... fields) {
      LayoutContainer selectContainer = new LayoutContainer();
      selectContainer.setLayout(new ColumnLayout());
      
      final RadioGroup radioGroup = new RadioGroup();
      radioGroup.setOrientation(Orientation.VERTICAL);
      Radio relativeRadio = new Radio();
      relativeRadio.setBoxLabel("Relative");
      relativeRadio.setValueAttribute(SCREEN_RELATIVE);
      Radio absoluteRadio = new Radio();
      absoluteRadio.setBoxLabel("Absolute");
      absoluteRadio.setValueAttribute(SCREEN_ABSOLUTE);
      radioGroup.add(relativeRadio);
      radioGroup.add(absoluteRadio);
      selectContainer.add(radioGroup);
      
      final ComboBox<ModelData> relative = new ComboBox<ModelData>();
      relative.setWidth(120);
      ListStore<ModelData> store = new ListStore<ModelData>();
      RelativeType[] relatedTypes  = RelativeType.values();
      for (int i = 0; i < relatedTypes.length; i++) {
         ComboBoxDataModel<RelativeType> relativeItem = new ComboBoxDataModel<RelativeType>(relatedTypes[i].toString(),
               relatedTypes[i]);
         store.add(relativeItem);
      }
      
      relative.setStore(store);
      relative.setName(SCREEN_RELATIVE);
      relative.setAllowBlank(false);
      relative.setEditable(false);
      relative.setTriggerAction(TriggerAction.ALL);
      relative.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
         @SuppressWarnings("unchecked")
         @Override
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            Background bkGrd = canvas.getScreen().getBackground();
            ComboBoxDataModel<RelativeType> relativeItem;
            relativeItem = (ComboBoxDataModel<RelativeType>) se.getSelectedItem();
            bkGrd.setRelatedType(relativeItem.getData());
            canvas.updateGround();
         }
      });
      relative.setValue(new ComboBoxDataModel<RelativeType>(canvas.getScreen().getBackground().getRelatedType().toString(),canvas.getScreen().getBackground().getRelatedType()));
      selectContainer.add(relative);
      
      relative.setEnabled(!canvas.getScreen().getBackground().isAbsolute());
     
      relative.setDisplayField(ComboBoxDataModel.getDisplayProperty());
      relative.setEmptyText("Please select one... ");
      
      relativeRadio.setValue(!canvas.getScreen().getBackground().isAbsolute());
      absoluteRadio.setValue(canvas.getScreen().getBackground().isAbsolute());
      enableTextField(canvas.getScreen().getBackground().isAbsolute(), fields);
      
      radioGroup.addListener(Events.Change, new Listener<FieldEvent>() {
         public void handleEvent(FieldEvent be) {
            String value = radioGroup.getValue().getValueAttribute();
            Background bkGrd = canvas.getScreen().getBackground();
            if (SCREEN_ABSOLUTE.equals(value)) {
               bkGrd.setAbsolute(true);
               relative.setEnabled(false);
               enableTextField(true, fields);
            } else if(SCREEN_RELATIVE.equals(value)) {
               bkGrd.setAbsolute(false);
               relative.setEnabled(true);
               enableTextField(false, fields);
            }
            canvas.updateGround();
         }
      });
      positionSet.add(selectContainer);
   }

   private RadioGroup createScreenFillerField(final FieldSet positionSet) {
      Radio fillScreen = new Radio();
      fillScreen.setName(FILL_SCREEN);
      fillScreen.setBoxLabel("yes");
      fillScreen.setValueAttribute(yesFill);
      fillScreen.setValue(canvas.getScreen().getBackground().isFillScreen());
      
      Radio notFillScreen = new Radio();
      notFillScreen.setName(FILL_SCREEN);
      notFillScreen.setBoxLabel("no");
      notFillScreen.setValueAttribute(notFill);
      
      final RadioGroup whetherFieldGroup = new RadioGroup();
      whetherFieldGroup.setFieldLabel(FILL_SCREEN);
      whetherFieldGroup.add(fillScreen);
      whetherFieldGroup.add(notFillScreen);
      whetherFieldGroup.addListener(Events.Change, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            String value = whetherFieldGroup.getValue().getValueAttribute();
            Background bkGrd = canvas.getScreen().getBackground();
            if (yesFill.equals(value)) {
               positionSet.hide();
               bkGrd.setFillScreen(true);
            } else if (notFill.equals(value)) {
               positionSet.show();
               bkGrd.setFillScreen(false);
            }
            canvas.updateGround();
         }
         
      });
      whetherFieldGroup.hide();
      whetherFieldGroup.setValue(canvas.getScreen().getBackground().isFillScreen()?fillScreen:notFillScreen);
      String backgroundSrc = canvas.getScreen().getBackground().getImageSource().getSrc();
      if (backgroundSrc != null && !backgroundSrc.equals("")) {
         whetherFieldGroup.show();
      }
      return whetherFieldGroup;
   }

   private ImageSelectAdapterField createBackgroundField() {
     final ImageSelectAdapterField backgroundField = new ImageSelectAdapterField("Background");
     backgroundField.setText(canvas.getScreen().getBackground().getImageSource().getImageFileName());
     backgroundField.addSelectionListener(new SelectionListener<ButtonEvent>() {
        @Override
        public void componentSelected(ButtonEvent ce) {
          final ImageSource image = canvas.getScreen().getBackground().getImageSource();
          
          ImageAssetPicker imageAssetPicker = new ImageAssetPicker((image != null)?image.getSrc():null);
          imageAssetPicker.show();
          imageAssetPicker.center();
          imageAssetPicker.setListener(new ImageAssetPickerListener() {
           @Override
           public void imagePicked(String imageURL) {
             setBackground(imageURL);
             backgroundField.setText(canvas.getScreen().getBackground().getImageSource().getImageFileName());
             whetherFillScreen.show();
           }             
          });
        }
     });
     backgroundField.addDeleteListener(new SelectionListener<ButtonEvent>() {
       public void componentSelected(ButtonEvent ce) {
         setBackground("");
         backgroundField.setText("");
         widgetSelectionUtil.setSelectWidget(canvas);
       }
    });
     return backgroundField;
   }

   private void setBackground(String backgroundImgURL) {
      Screen screen = canvas.getScreen();
      screen.setBackground(new Background(new ImageSource(backgroundImgURL)));
      canvas.setStyleAttribute("backgroundImage", "url(" + screen.getCSSBackground() + ")");
      canvas.unmask();

   }
   private void enableTextField(boolean enable, TextField<?>... fields) {
      for (TextField<?> field : fields) {
         field.setEnabled(enable);
      }
   }
   
   private AdapterField createGestureField() {
      Button configGesture = new Button("Config");
      configGesture.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            Group parentGroup = canvas.getScreen().getScreenPair().getParentGroup();
            List<Group> groups = new ArrayList<Group>();
            if (parentGroup != null) {
               groups = parentGroup.getParentPanel().getGroups();
            }
            GestureWindow configGestureWindow = new GestureWindow(canvas.getScreen().getGestures(), groups);
            configGestureWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @SuppressWarnings("unchecked")
               public void afterSubmit(SubmitEvent be) {
                  canvas.getScreen().setGestures((List<Gesture>)be.getData());
               }
            });
         }
      });
      AdapterField adapterConfigGesture = new AdapterField(configGesture);
      adapterConfigGesture.setFieldLabel("Gestures");
      adapterConfigGesture.setAutoWidth(true);
      return adapterConfigGesture;
   }
   
   @Override
   protected void afterRender() {
      super.afterRender();
      ((PropertyPanel)this.getParent()).setHeading("Screen properties");
   }
}
