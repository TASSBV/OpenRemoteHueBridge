package org.openremote.modeler.client.ir;

import com.google.gwt.core.client.JavaScriptObject;

public class IRCommandInfoOverlay  extends JavaScriptObject {

  // Overlay types always have protected, zero-arg ctors
  protected IRCommandInfoOverlay() { } 
    
  public final native String getComment() /*-{ return this.comment; }-*/;
  
  public final native String getName() /*-{ return this.name; }-*/;

  public final native String getCode() /*-{ return this.code; }-*/;

  public final native String getOriginalCode() /*-{ return this.originalCode; }-*/;

}
