package org.openremote.modeler.client.ir;

import org.openremote.modeler.client.utils.ArrayOverlay;

import com.google.gwt.core.client.JavaScriptObject;

public class CodeSetsResultOverlay extends JavaScriptObject {

  // Overlay types always have protected, zero-arg ctors
  protected CodeSetsResultOverlay() { } 
    
  // Typically, methods on overlay types are JSNI
  public final native String getErrorMessage() /*-{ return this.errorMessage; }-*/;
  
  public final native ArrayOverlay<CodeSetInfoOverlay> getResult() /*-{ return this.result; }-*/;

  public static native CodeSetsResultOverlay fromJSONString(String jsonString) /*-{
    return eval('(' + jsonString + ')');
  }-*/;

}
