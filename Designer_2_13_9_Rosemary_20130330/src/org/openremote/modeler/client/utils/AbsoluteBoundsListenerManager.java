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
package org.openremote.modeler.client.utils;

import java.util.HashMap;
import java.util.Map;

import org.openremote.modeler.client.event.AbsoluteBoundsEvent;
import org.openremote.modeler.client.listener.AbsoluteBoundsListener;
import org.openremote.modeler.client.model.ORBounds;
import org.openremote.modeler.client.widget.uidesigner.AbsoluteLayoutContainer;

/**
 * Manage absoluteBoundsListeners.
 */
public class AbsoluteBoundsListenerManager {

   private static AbsoluteBoundsListenerManager instance;
   private static Map<AbsoluteLayoutContainer, AbsoluteBoundsListener> eventListeners = new HashMap<AbsoluteLayoutContainer, AbsoluteBoundsListener>();
   
   /**
    * Adds the absolute bounds listener.
    * 
    */
   public void addAbsoluteBoundsListener(AbsoluteLayoutContainer layoutContainer, AbsoluteBoundsListener listener) {
      eventListeners.put(layoutContainer, listener);
   }
   
   /**
    * Notify absolute bounds listener.
    * 
    */
   public void notifyAbsoluteBoundsListener(AbsoluteLayoutContainer layoutContainer, ORBounds bounds) {
      AbsoluteBoundsListener listener = eventListeners.get(layoutContainer);
      if (listener == null) {
         return;
      } else {
         listener.handleEvent(new AbsoluteBoundsEvent(bounds));
      }
   }
   
   /**
    * Delete absolute bounds listener.
    * 
    */
   public void deleteAbsoluteBoundsListener(AbsoluteLayoutContainer layoutContainer, AbsoluteBoundsListener listener) {
      eventListeners.remove(layoutContainer);
   }
   
   /**
    * Gets the single instance of AbsoluteBoundsListenerManager.
    * 
    */
   public static synchronized AbsoluteBoundsListenerManager getInstance() {
      if ( instance == null) {
         instance = new AbsoluteBoundsListenerManager();
      }
      return instance;
   }
}
