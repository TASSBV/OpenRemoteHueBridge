package org.openremote.modeler.client.event;

import java.util.ArrayList;

import org.openremote.modeler.shared.dto.DeviceDTO;

import com.google.gwt.event.shared.GwtEvent;

public class DevicesCreatedEvent extends GwtEvent<DevicesCreatedEventHandler> {

  public static Type<DevicesCreatedEventHandler> TYPE = new Type<DevicesCreatedEventHandler>();

  private final ArrayList<DeviceDTO> devices;

  public DevicesCreatedEvent(DeviceDTO device) {
    super();
    this.devices = new ArrayList<DeviceDTO>();
    this.devices.add(device);
  }
  
  public DevicesCreatedEvent(ArrayList<DeviceDTO> devices) {
    super();
    this.devices = devices;
  }

  public ArrayList<DeviceDTO> getDevices() {
    return devices;
  }

  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<DevicesCreatedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(DevicesCreatedEventHandler handler) {
    handler.onDevicesCreated(this);
  }
  
}