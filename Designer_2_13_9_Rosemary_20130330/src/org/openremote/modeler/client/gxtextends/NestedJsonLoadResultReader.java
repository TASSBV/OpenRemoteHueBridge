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
import java.util.Date;

import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.data.JsonLoadResultReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * This class is used for fix gxt read xml bug (auto convert number to double) and limitation (json root not support
 * nested structure).
 * 
 * @param <D> any JsonLoadResultReader
 * 
 * @author allen.wei
 */
public class NestedJsonLoadResultReader<D> extends JsonLoadResultReader<D> {

   /** The my model type. */
   private ModelType myModelType;

   /**
    * Instantiates a new nested json load result reader.
    * 
    * @param modelType the model type
    */
   public NestedJsonLoadResultReader(ModelType modelType) {
      super(modelType);
      myModelType = modelType;
   }

   /*
    * (non-Javadoc)
    * 
    * @see com.extjs.gxt.ui.client.data.JsonReader#read(java.lang.Object, java.lang.Object)
    */
   @SuppressWarnings("unchecked")
   @Override
   public D read(Object loadConfig, Object data) {
      JSONObject jsonRoot = null;
      if (data instanceof JavaScriptObject) {
         jsonRoot = new JSONObject((JavaScriptObject) data);
      } else {
         jsonRoot = (JSONObject) JSONParser.parse((String) data);
      }
      
      // You can specify root using dot separate. eg, vendors.vendor 
      String[] roots = myModelType.getRoot().split("\\.");
      JSONValue rootValue = null;
      JSONArray root = null;
      for (int i = 0; i < roots.length; i++) {
         rootValue = jsonRoot.get(roots[i]);
         if (i == roots.length - 1) {
            if (rootValue instanceof JSONObject) {
               root = new JSONArray();
               root.set(0, rootValue);
            } else {
               root = (JSONArray) rootValue;
            }
         } else {
            jsonRoot = (JSONObject) jsonRoot.get(roots[i]);
         }
      }

      int size = root.size();
      ArrayList<ModelData> models = new ArrayList<ModelData>();
      for (int i = 0; i < size; i++) {
         JSONObject obj = (JSONObject) root.get(i);
         ModelData model = newModelInstance();
         for (int j = 0; j < myModelType.getFieldCount(); j++) {
            DataField field = myModelType.getField(j);
            String name = field.getName();
            Class type = field.getType();
            String map = field.getMap() != null ? field.getMap() : field.getName();
            JSONValue value = obj.get(map);

            if (value == null) {
               continue;
            }
            if (value.isArray() != null) {
               // nothing
            } else if (value.isBoolean() != null) {
               model.set(name, value.isBoolean().booleanValue());
            } else if (value.isNumber() != null) {
               if (type != null) {
                  Double d = value.isNumber().doubleValue();
                  if (type.equals(Integer.class)) {
                     model.set(name, d.intValue());
                  } else if (type.equals(Long.class)) {
                     model.set(name, d.longValue());
                  } else if (type.equals(Float.class)) {
                     model.set(name, d.floatValue());
                  } else {
                     model.set(name, d);
                  }
               } else {
                  // convert no type number to string.
                  model.set(name, value.isNumber().toString());
               }
            } else if (value.isObject() != null) {
               // nothing
            } else if (value.isString() != null) {
               String s = value.isString().stringValue();
               if (type != null) {
                  if (type.equals(Date.class)) {
                     if (field.getFormat().equals("timestamp")) {
                        Date d = new Date(Long.parseLong(s) * 1000);
                        model.set(name, d);
                     } else {
                        DateTimeFormat format = DateTimeFormat.getFormat(field.getFormat());
                        Date d = format.parse(s);
                        model.set(name, d);
                     }
                  }
               } else {
                  model.set(name, s);
               }
            } else if (value.isNull() != null) {
               model.set(name, null);
            }
         }
         models.add(model);
      }
      int totalCount = models.size();
      if (myModelType.getTotalName() != null) {
         totalCount = getTotalCount(jsonRoot);
      }
      return (D) createReturnData(loadConfig, models, totalCount);
   };

}