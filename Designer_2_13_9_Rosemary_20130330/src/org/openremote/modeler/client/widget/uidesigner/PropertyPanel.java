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
package org.openremote.modeler.client.widget.uidesigner;

import java.util.List;

import org.openremote.modeler.client.utils.PropertyEditable;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.propertyform.PropertyForm;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * A panel for display and edit different component's properties.
 */
public class PropertyPanel extends ContentPanel {

   private ComponentContainer currentLayoutContainer;
   private PropertyForm currentPropertyForm;
   
   public PropertyPanel(WidgetSelectionUtil widgetSelectionUtil) {
      setBorders(false);
      setFooter(false);
      setBodyStyleName("zero-padding");
      setBodyBorder(false);
      setHeading("Properties");
      setLayout(new FitLayout());
      setFrame(true);      
   }
   
   /**
    * Update the panel's content follow with different component.
    */
   public void update(List<ComponentContainer> components) {
      if (components.isEmpty()) {
         removePropertiesForm();
         setHeading("Properties");
         layout();
         return;
      }
      if (components.size() > 1) {
        removePropertiesForm();
        setHeading("Multiple selection");
        layout();
        return;
      } else {
        ComponentContainer component = components.get(0);
        // TODO EBR : re-check this test, does not seem to work
        if (!component.equals(currentLayoutContainer)) {
           currentLayoutContainer =  component;
           if (component instanceof GridLayoutContainerHandle) {
              addPropertiesForm(component);
              currentLayoutContainer = null;
           /*} else if (component.getParent() instanceof ScreenTabItem) {
              addScreenPairPropertyForm(component);
            */
           }else {
              addPropertiesForm(component);
           } 
           layout();
        }
      }
   }
   
   public void setPropertyForm(PropertyEditable propertyEditable) {
      removePropertiesForm();
      currentPropertyForm = propertyEditable.getPropertiesForm();
      add(currentPropertyForm);
      this.setHeading(propertyEditable.getTitle());
      layout();
   }

   /**
    * @param screenControl
    * @param uiComponent
    */
   private void addPropertiesForm(ComponentContainer screenControl) {
      if (currentPropertyForm != null) {
         currentPropertyForm.removeFromParent();
      }
      currentPropertyForm = screenControl.getPropertiesForm();
      add(currentPropertyForm);
   }
   
   public void removePropertiesForm() {
      if (currentPropertyForm != null) {
         currentPropertyForm.removeFromParent();
         currentLayoutContainer = null;
         currentPropertyForm = null;
      }
   }
   
   /*private void addScreenPairPropertyForm(ComponentContainer component) {
      if (currentPropertyForm != null) {
         currentPropertyForm.removeFromParent();
      }
      currentPropertyForm = new ScreenPairPropertyForm(component);
      add(currentPropertyForm);
   }*/
}
