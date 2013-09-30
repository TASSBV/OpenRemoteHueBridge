package org.openremote.modeler.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface MacroUpdatedEventHandler extends EventHandler {

  void onMacroUpdated(MacroUpdatedEvent event);
  
}
