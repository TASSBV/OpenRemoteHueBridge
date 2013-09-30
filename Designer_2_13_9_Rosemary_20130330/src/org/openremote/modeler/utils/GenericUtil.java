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

package org.openremote.modeler.utils;

import java.lang.reflect.ParameterizedType;

/**
 * Generic utility class User: allenwei Date: 2009-2-13 Time: 10:57:22.
 */
public class GenericUtil {

   /**
    * Instantiates a new generic util.
    */
   private GenericUtil() {
   }

   /**
    * Method for finding out of what type a parameterized generic class is.
    * 
    * @param clazz The class to get the name of
    * 
    * @return classname
    */
   @SuppressWarnings("unchecked")
   public static Class getClassForGenericType(Class<?> clazz) {
      ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
      return (Class<?>) (parameterizedType.getActualTypeArguments()[0]);
   }
}