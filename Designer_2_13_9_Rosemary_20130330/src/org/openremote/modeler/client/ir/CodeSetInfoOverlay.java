package org.openremote.modeler.client.ir;

import com.google.gwt.core.client.JavaScriptObject;

public class CodeSetInfoOverlay extends JavaScriptObject {

  // Overlay types always have protected, zero-arg ctors
  protected CodeSetInfoOverlay() { } 
    
  public final native String getDescription() /*-{ return this.description; }-*/;
  
  public final native String getCategory() /*-{ return this.category; }-*/;

  public final native String getIndex() /*-{ return this.index; }-*/;

}
