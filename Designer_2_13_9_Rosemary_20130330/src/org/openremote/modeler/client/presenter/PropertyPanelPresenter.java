/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.modeler.client.presenter;

import org.openremote.modeler.client.event.UIElementEditedEvent;
import org.openremote.modeler.client.event.UIElementEditedEventHandler;
import org.openremote.modeler.client.event.UIElementSelectedEvent;
import org.openremote.modeler.client.event.UIElementSelectedEventHandler;
import org.openremote.modeler.client.event.WidgetSelectedEvent;
import org.openremote.modeler.client.event.WidgetSelectedEventHandler;
import org.openremote.modeler.client.utils.PropertyEditableFactory;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.uidesigner.PropertyPanel;

import com.google.gwt.event.shared.EventBus;

public class PropertyPanelPresenter implements Presenter {

  private EventBus eventBus;
  private WidgetSelectionUtil widgetSelectionUtil;
  private PropertyPanel view;
  
  public PropertyPanelPresenter(EventBus eventBus, WidgetSelectionUtil widgetSelectionUtil, PropertyPanel view) {
    super();
    this.eventBus = eventBus;
    this.widgetSelectionUtil = widgetSelectionUtil;
    this.view = view;
    bind();
  }
  
  private void bind() {
    eventBus.addHandler(UIElementSelectedEvent.TYPE, new UIElementSelectedEventHandler() {
      @Override
      public void onElementSelected(UIElementSelectedEvent event) {
        PropertyPanelPresenter.this.view.setPropertyForm(PropertyEditableFactory.getPropertyEditable(event.getElement(), eventBus));
      }
    });
    
    eventBus.addHandler(UIElementEditedEvent.TYPE, new UIElementEditedEventHandler() {      
      @Override
      public void onElementEdited(UIElementEditedEvent event) {
        // TODO EBR - this is just a quick fix, need to review
        view.update(widgetSelectionUtil.getSelectedWidgets());
      }
    });
    
    eventBus.addHandler(WidgetSelectedEvent.TYPE, new WidgetSelectedEventHandler() {
      @Override
      public void onSelectionChanged(WidgetSelectedEvent event) {
        view.update(event.getSelectedWidgets());
      }
    });
  }
  
  
}
