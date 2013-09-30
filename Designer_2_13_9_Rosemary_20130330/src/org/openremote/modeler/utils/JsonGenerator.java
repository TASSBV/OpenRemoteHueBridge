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

import flexjson.JSONSerializer;

/**
 * serializer object to json string.
 * 
 * @author handy.wang
 */
public final class JsonGenerator {

   /**
    * Instantiates a new json generator.
    */
   private JsonGenerator() {
   }
   
   /**
    * Serializer object.
    * 
    * @param object the object
    * 
    * @return the string
    */
   public static String serializerObject(Object object) {
      JSONSerializer serializer = new JSONSerializer();
      return serializer.exclude("*.class").serialize(object);
   }


   /**
    * Serializer object include.
    * 
    * @param object the object
    * @param includedPropertyNames the included property names
    * @param excludedPropertyNames the excluded property names
    * 
    * @return the string
    */
   public static String serializerObjectInclude(Object object, String[] includedPropertyNames, String[] excludedPropertyNames) {
      JSONSerializer serializer = new JSONSerializer();
      return serializer.exclude("*.class").include(includedPropertyNames).exclude(excludedPropertyNames).deepSerialize(object);
   }
   
}
