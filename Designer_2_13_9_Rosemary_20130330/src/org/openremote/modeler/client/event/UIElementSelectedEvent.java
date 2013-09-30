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
package org.openremote.modeler.client.event;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.event.shared.GwtEvent;

public class UIElementSelectedEvent extends GwtEvent<UIElementSelectedEventHandler> {

  public static Type<UIElementSelectedEventHandler> TYPE = new Type<UIElementSelectedEventHandler>();

  private final BeanModel element;
  
  public UIElementSelectedEvent(BeanModel element) {
    super();
    this.element = element;
  }
  
  public BeanModel getElement() {
    return element;
  }

  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<UIElementSelectedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(UIElementSelectedEventHandler handler) {
    handler.onElementSelected(this);
  }

}
