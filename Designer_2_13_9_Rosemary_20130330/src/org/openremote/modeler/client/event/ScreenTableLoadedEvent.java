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

import com.google.gwt.event.shared.GwtEvent;

/**
 * Indicates when the BeanModel ScreenTable has been loaded.
 * 
 * @author eric@openremote.org
 */
public class ScreenTableLoadedEvent extends GwtEvent<ScreenTableLoadedEventHandler> {

  // TODO EBR : this might be a temporary solution, this whole loading / caching mechanism needs review
  // It is currently required so that ScreenTablePresenter can register for events with the table
  // at the appropriate time
  
  public static Type<ScreenTableLoadedEventHandler> TYPE = new Type<ScreenTableLoadedEventHandler>();
  
  public ScreenTableLoadedEvent() {
    super();
  }
  
  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<ScreenTableLoadedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ScreenTableLoadedEventHandler handler) {
    handler.onScreenTableLoaded(this);
  }

}
