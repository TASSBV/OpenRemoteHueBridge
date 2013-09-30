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

import org.openremote.modeler.domain.ScreenPairRef;

import com.google.gwt.event.shared.GwtEvent;

/**
 * When a screen is selected.
 * This might be a bit too granular and we might want a generic event when any UI object is selected.
 * 
 * @author ebr
 */
public class ScreenSelectedEvent extends GwtEvent<ScreenSelectedEventHandler> {

  public static Type<ScreenSelectedEventHandler> TYPE = new Type<ScreenSelectedEventHandler>();

  private final ScreenPairRef selectedScreenPair;
  
  public ScreenSelectedEvent(ScreenPairRef selectedScreenPair) {
    super();
    this.selectedScreenPair = selectedScreenPair;
  }
  
  public ScreenPairRef getSelectedScreenPair() {
    return selectedScreenPair;
  }

  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<ScreenSelectedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ScreenSelectedEventHandler handler) {
    handler.onScreenSelected(this);
  }

}
