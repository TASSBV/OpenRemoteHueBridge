package org.openremote.modeler.client.event;

import java.util.ArrayList;

import org.openremote.modeler.shared.dto.MacroDTO;

import com.google.gwt.event.shared.GwtEvent;

public class MacrosCreatedEvent extends GwtEvent<MacrosCreatedEventHandler> {

  public static Type<MacrosCreatedEventHandler> TYPE = new Type<MacrosCreatedEventHandler>();

  private final ArrayList<MacroDTO> macros;

  public MacrosCreatedEvent(MacroDTO macro) {
    super();
    this.macros = new ArrayList<MacroDTO>();
    this.macros.add(macro);
  }
  
  public MacrosCreatedEvent(ArrayList<MacroDTO> macros) {
    super();
    this.macros = macros;
  }

  public ArrayList<MacroDTO> getMacros() {
    return macros;
  }

  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<MacrosCreatedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(MacrosCreatedEventHandler handler) {
    handler.onMacrosCreated(this);
  }

}