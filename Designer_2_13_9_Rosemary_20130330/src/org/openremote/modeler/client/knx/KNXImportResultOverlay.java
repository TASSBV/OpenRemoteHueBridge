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
package org.openremote.modeler.client.knx;

import org.openremote.modeler.client.utils.ArrayOverlay;

import com.google.gwt.core.client.JavaScriptObject;

public class KNXImportResultOverlay  extends JavaScriptObject {

  // Overlay types always have protected, zero-arg ctors
  protected KNXImportResultOverlay() { } 
    
  // Typically, methods on overlay types are JSNI
  public final native String getException() /*-{ return this.exception; }-*/;

  public final native ArrayOverlay<RecordOverlay> getRecords() /*-{ return this.records; }-*/;

  public static native KNXImportResultOverlay fromJSONString(String jsonString) /*-{
    return eval('(' + jsonString + ')');
  }-*/;
  
}
