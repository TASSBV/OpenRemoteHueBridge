package org.openremote.modeler.client.event;

import org.openremote.modeler.shared.dto.MacroDTO;

import com.google.gwt.event.shared.GwtEvent;

public class MacroUpdatedEvent extends GwtEvent<MacroUpdatedEventHandler> {

  public static Type<MacroUpdatedEventHandler> TYPE = new Type<MacroUpdatedEventHandler>();

  private final MacroDTO macro;
  
  public MacroUpdatedEvent(MacroDTO macro) {
    super();
    this.macro = macro;
  }

  public MacroDTO getMacro() {
    return macro;
  }

  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<MacroUpdatedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(MacroUpdatedEventHandler handler) {
    handler.onMacroUpdated(this);
  }

}
