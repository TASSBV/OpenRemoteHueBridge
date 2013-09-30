package org.openremote.modeler.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface DevicesCreatedEventHandler extends EventHandler {

  void onDevicesCreated(DevicesCreatedEvent event);
  
}
