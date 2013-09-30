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

import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.widget.component.ScreenPropertyEditable;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPair.OrientationType;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * A panel for editing screen's name. 
 * @author Javen
 */
public class ScreenPropertyEditForm extends PropertyForm {
   private ScreenPropertyEditable editor = null;
   private ScreenPair screenPair = null;
   public ScreenPropertyEditForm(ScreenPropertyEditable editor, ScreenPair screenPair) {
      super(editor);
      setFieldWidth(130);
      this.editor = editor;
      this.screenPair = screenPair;
      addFields();
      show();
   }

   private void addFields() {
      // initial name field.
      final TextField<String> name = new TextField<String>();
      name.setFieldLabel("Name");
      name.setValue(editor.getName());
      name.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            if (name.getValue() != null && name.getValue().trim().length() > 0) {
               editor.setName(name.getValue());
            } else {
               MessageBox.alert("Error", "Empty name is not allowed!", null);
               name.setValue(editor.getName());
            }
         }
      });

      add(name);
      addOrientationField();
   }
   
   private void addOrientationField() {
      Radio vRadio = new Radio();
      vRadio.setValueAttribute(OrientationType.PORTRAIT.toString());
      vRadio.setBoxLabel("Portrait");
      Radio hRadio = new Radio();
      hRadio.setValueAttribute(OrientationType.LANDSCAPE.toString());
      hRadio.setBoxLabel("Landscape");
      Radio vhRadio = new Radio();
      vhRadio.setValueAttribute(OrientationType.BOTH.toString());
      vhRadio.setBoxLabel("Portrait & Landscape");

      RadioGroup radioGroup = new RadioGroup("orientation");
      radioGroup.setOrientation(Orientation.VERTICAL);
      radioGroup.setFieldLabel("Orientation");
      radioGroup.add(vRadio);
      radioGroup.add(hRadio);
      radioGroup.add(vhRadio);
      add(radioGroup);
      if (OrientationType.PORTRAIT.equals(screenPair.getOrientation())) {
         vRadio.setValue(true);
      } else if (OrientationType.LANDSCAPE.equals(screenPair.getOrientation())) {
         hRadio.setValue(true);
      } else if (OrientationType.BOTH.equals(screenPair.getOrientation())) {
         vhRadio.setValue(true);
      }
      radioGroup.addListener(Events.Change, new Listener<FieldEvent>() {
         public void handleEvent(FieldEvent be) {
            Radio radio = ((RadioGroup)be.getField()).getValue();
            if (OrientationType.PORTRAIT.toString().equals(radio.getValueAttribute())) {
               screenPair.setOrientation(OrientationType.PORTRAIT);
               if (screenPair.getPortraitScreen() == null) {
                  Screen screen = new Screen();
                  screen.setOid(IDUtil.nextID());
                  screen.setName(screenPair.getName());
                  screenPair.setPortraitScreen(screen);
               }
               screenPair.getPortraitScreen().setTouchPanelDefinition(screenPair.getTouchPanelDefinition());               
               editor.updateScreen();
            } else if (OrientationType.LANDSCAPE.toString().equals(radio.getValueAttribute())) {
               screenPair.setOrientation(OrientationType.LANDSCAPE);
               if (screenPair.getLandscapeScreen() == null) {
                  Screen screen = new Screen();
                  screen.setLandscape(true);
                  screen.setOid(IDUtil.nextID());
                  screen.setName(screenPair.getName());
                  screenPair.setLandscapeScreen(screen);
               }
               screenPair.getLandscapeScreen().setTouchPanelDefinition(screenPair.getTouchPanelDefinition().getHorizontalDefinition());
               editor.updateScreen();
            } else if (OrientationType.BOTH.toString().equals(radio.getValueAttribute())) {
               screenPair.setOrientation(OrientationType.BOTH);
               if (screenPair.getPortraitScreen() == null) {
                  Screen screen = new Screen();
                  screen.setOid(IDUtil.nextID());
                  screen.setName(screenPair.getName());
                  screenPair.setPortraitScreen(screen);
               }
               if (screenPair.getLandscapeScreen() == null) {
                  Screen screen = new Screen();
                  screen.setLandscape(true);
                  screen.setOid(IDUtil.nextID());
                  screen.setName(screenPair.getName());
                  screenPair.setLandscapeScreen(screen);
               }
               screenPair.getLandscapeScreen().setTouchPanelDefinition(screenPair.getTouchPanelDefinition().getHorizontalDefinition());               
               editor.updateScreen();
            }
            screenPair.clearInverseScreenIds();
         }
         
      });
   }
}
