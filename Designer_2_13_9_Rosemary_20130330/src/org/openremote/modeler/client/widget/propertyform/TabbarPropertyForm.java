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

import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.component.ScreenTabbar;
import org.openremote.modeler.client.widget.uidesigner.PropertyPanel;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * A panel for editing tabbar properties.
 */
public class TabbarPropertyForm extends PropertyForm {
   
   private ScreenTabbar tabbar = null;
   
   public TabbarPropertyForm(ScreenTabbar tabbar, WidgetSelectionUtil widgetSelectionUtil) {
      super(tabbar, widgetSelectionUtil);
      this.tabbar = tabbar;
      setLayout(new FormLayout());
      addFields();
      super.addDeleteButton();
   }
   private void addFields(){
      RadioGroup scopeCheckGroup = new RadioGroup();
      scopeCheckGroup.setFieldLabel("Scope");
      
      final Radio groupScopeRadio = new Radio();
      groupScopeRadio.setBoxLabel("Group");
      groupScopeRadio.setValue(tabbar.isGroupScope());
      
      final Radio panelScopeRadio = new Radio();
      panelScopeRadio.setValue(tabbar.isPanelScope());
      panelScopeRadio.setBoxLabel("Panel");
      
      panelScopeRadio.addListener(Events.Change, new Listener<FieldEvent>() {

         @Override
         public void handleEvent(FieldEvent be) {
            boolean panelScope = panelScopeRadio.getValue();
            if (panelScope) {
               tabbar.setToPanel();
            } else {
               tabbar.setToGroup();
            }
         }
         
      });
      scopeCheckGroup.add(groupScopeRadio);
      scopeCheckGroup.add(panelScopeRadio);
      
      add(scopeCheckGroup);
   }
   
   @Override
   protected void afterRender() {
      super.afterRender();
      ((PropertyPanel)this.getParent()).setHeading("Tab bar properties");
   }
}
