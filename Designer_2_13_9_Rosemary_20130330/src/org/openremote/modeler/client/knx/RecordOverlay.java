package org.openremote.modeler.client.knx;

import com.google.gwt.core.client.JavaScriptObject;

public class RecordOverlay  extends JavaScriptObject {

  protected RecordOverlay() {}
  
  public final native String getCommand() /*-{ return this.command; }-*/;
  public final native String getDpt() /*-{ return this.dpt; }-*/;
  public final native String getGroupAddress() /*-{ return this.groupAddress; }-*/;
  public final native Boolean getImportGA() /*-{ return this.importGA; }-*/;
  public final native String getName() /*-{ return this.name; }-*/;
}