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
package org.openremote.modeler.client.gxtextends;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionProvider;

/**
 * Provides a selection service where <code>SelectionProviders</code> can
 * register, allowing any listeners to be notified of selection events.
 */
public class SelectionServiceExt<M extends ModelData> {

   /** The listener. */
   private SelectionChangedListener<M> listener;
   
   /** The providers. */
   private List<SelectionProvider<M>> providers;
   
   /** The listeners. */
   private List<SelectionChangedListener<M>> listeners;

   /**
    * Instantiates a new selection service ext.
    */
   public SelectionServiceExt() {
      listener = new SelectionChangedListener<M>() {
         public void selectionChanged(SelectionChangedEvent<M> event) {
            onSelectionChanged(event);
         }
      };
      listeners = new ArrayList<SelectionChangedListener<M>>();
      providers = new ArrayList<SelectionProvider<M>>();
   }

   /**
    * Adds a listener to be notified of selection events from any registered selection providers.
    * 
    * @param listener
    *           the listener to add
    */
   @SuppressWarnings("unchecked")
   public void addListener(SelectionChangedListener<? extends ModelData> listener) {
      listeners.add((SelectionChangedListener) listener);
   }

   /**
    * Returns a list of all current listeners.
    * 
    * @return the listeners
    */
   public List<SelectionChangedListener<M>> getListeners() {
      return new ArrayList<SelectionChangedListener<M>>(listeners);
   }

   /**
    * Returns the list of current providers.
    * 
    * @return the providers
    */
   public List<SelectionProvider<M>> getProviders() {
      return new ArrayList<SelectionProvider<M>>(providers);
   }

   /**
    * Registers a selection provider.
    * 
    * @param provider
    *           the provider to add
    */
   public void register(SelectionProvider<M> provider) {
      provider.addSelectionChangedListener(listener);
      providers.add(provider);
   }

   /**
    * Removes a previously added listener.
    * 
    * @param listener
    *           the listener to remove
    */
   public void removeListener(SelectionChangedListener<? extends ModelData> listener) {
      listeners.remove(listener);
   }

   /**
    * Unregisters a selection provider.
    * 
    * @param provider
    *           the provider to unregister
    */
   public void unregister(SelectionProvider<M> provider) {
      provider.removeSelectionListener(listener);
      providers.remove(provider);
   }

   /**
    * Called when any selection changed event is received from any registered providers.
    * 
    * @param event
    *           the selection changed event
    */
   protected void onSelectionChanged(SelectionChangedEvent<M> event) {
      for (SelectionChangedListener<M> l : listeners) {
         l.selectionChanged(event);
      }
   }
}
