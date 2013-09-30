package org.openremote.modeler.client.ir;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public class BrandsResultOverlay extends JavaScriptObject {

  // Overlay types always have protected, zero-arg ctors
  protected BrandsResultOverlay() { } 
    
  // Typically, methods on overlay types are JSNI
  public final native String getErrorMessage() /*-{ return this.errorMessage; }-*/;
  
  public final native JsArrayString getResult() /*-{ return this.result; }-*/;

  public static native BrandsResultOverlay fromJSONString(String jsonString) /*-{
    return eval('(' + jsonString + ')');
  }-*/;

}
