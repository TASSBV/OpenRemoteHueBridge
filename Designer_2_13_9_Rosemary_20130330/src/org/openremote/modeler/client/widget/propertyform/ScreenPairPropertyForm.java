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

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.uidesigner.ComponentContainer;
import org.openremote.modeler.client.widget.uidesigner.PropertyPanel;
import org.openremote.modeler.client.widget.uidesigner.ScreenTab;
import org.openremote.modeler.client.widget.uidesigner.ScreenTabItem;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPair.OrientationType;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;

/**
 * A form to edit screenPair properties.
 */
public class ScreenPairPropertyForm extends PropertyForm {

   private ScreenTab screenTab;
   private ScreenPair screenPair;
   public ScreenPairPropertyForm(ComponentContainer componentContainer, WidgetSelectionUtil widgetSelectionUtil) {
      super(componentContainer, widgetSelectionUtil);
      setFieldWidth(130);
      this.screenTab = (ScreenTab) componentContainer.getParent().getParent();
      this.screenPair = screenTab.getScreenPair();
      createFields();
   }

   private void createFields() {
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
               if (screenTab.getItemByItemId(Constants.LANDSCAPE) != null) {
                  screenTab.getItemByItemId(Constants.LANDSCAPE).disable();
               }
               if (screenTab.getItemByItemId(Constants.PORTRAIT) == null) {
                  screenTab.insert(new ScreenTabItem(screenPair.getPortraitScreen(), widgetSelectionUtil), 0);
               } else {
                  screenTab.getItemByItemId(Constants.PORTRAIT).enable();
               }
               screenTab.setSelection(screenTab.getItemByItemId(Constants.PORTRAIT));
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
               if (screenTab.getItemByItemId(Constants.PORTRAIT) != null) {
                  screenTab.getItemByItemId(Constants.PORTRAIT).disable();
               }
               if (screenTab.getItemByItemId(Constants.LANDSCAPE) == null) {
                  screenTab.add(new ScreenTabItem(screenPair.getLandscapeScreen(), widgetSelectionUtil));
               } else {
                  screenTab.getItemByItemId(Constants.LANDSCAPE).enable();
               }
               screenTab.setSelection(screenTab.getItemByItemId(Constants.LANDSCAPE));
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
               if (screenTab.getItemByItemId(Constants.PORTRAIT) != null) {
                  screenTab.getItemByItemId(Constants.PORTRAIT).enable();
               } else {
                  screenTab.insert(new ScreenTabItem(screenPair.getPortraitScreen(), widgetSelectionUtil), 0);
               }
               if (screenTab.getItemByItemId(Constants.LANDSCAPE) != null) {
                  screenTab.getItemByItemId(Constants.LANDSCAPE).enable();
               } else {
                  screenTab.add(new ScreenTabItem(screenPair.getLandscapeScreen(), widgetSelectionUtil));
               }
               screenTab.setSelection(screenTab.getItemByItemId(Constants.PORTRAIT));
            }
            screenPair.clearInverseScreenIds();
         }
         
      });
   }
   
   @Override
   protected void afterRender() {
      super.afterRender();
      ((PropertyPanel)this.getParent()).setHeading("Screen pair properties");
   }
}
