/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2012, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.modeler.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openremote.modeler.domain.component.UIComponent;

/**
 * This is a utility class for store the <class>UIComponent</class> by their class name.
 * 
 * @author Javen
 * 
 */
public class UIComponentBox {
   private Map<Class<UIComponent>, Set<UIComponent>> uiComponentsMap = new HashMap<Class<UIComponent>, Set<UIComponent>>();

   /**
    * add the UIComponent to the box. Different UIComponent will be stored in different set. The same kind of
    * UIComponent will be stored in the same set.
    * 
    * @param component
    *           The UIComponent you want to store.
    */
   @SuppressWarnings("unchecked")
   public void add(UIComponent component) {
      if (null == uiComponentsMap.get(component.getClass())) {
         Set<UIComponent> components = new HashSet<UIComponent>();
         uiComponentsMap.put((Class<UIComponent>) component.getClass(), components);
      }
      Set<UIComponent> components = uiComponentsMap.get(component.getClass());
      /*
       * save or update
       */
      UIComponent oldComponent = null;
      for (UIComponent uiComponent : components) {
         if (uiComponent.getOid() == component.getOid()) {
            oldComponent = uiComponent;
            break;
         }
      }
      if (oldComponent != null) {
         components.remove(oldComponent);
      }
      components.add(component);
   }

   /**
    * get all the UIComponents who have the same type. for example: if you want to get all the UISwitch, you can invoke
    * this function like this: <code>getUIComponent(UISwitch.class)</code>
    * 
    * @param type
    *           the type for the UIComponent.
    * @return A set with the same kind of UIComponent.
    */
   @SuppressWarnings("unchecked")
   public Set<? extends UIComponent> getUIComponentsByType(Class type) {
      Set<UIComponent> uiComponents = uiComponentsMap.get(type);
      if (uiComponents == null) {
         uiComponents = new HashSet<UIComponent>();
      }
      return uiComponents;
   }
   
   public synchronized void clear(){
      uiComponentsMap.clear();
   }
}
