package org.openremote.modeler.client.event;

import org.openremote.modeler.domain.Template;

import com.google.gwt.event.shared.GwtEvent;

public class TemplateSelectedEvent extends GwtEvent<TemplateSelectedEventHandler> {
  
  public static Type<TemplateSelectedEventHandler> TYPE = new Type<TemplateSelectedEventHandler>();

  private final Template template;
  
  public TemplateSelectedEvent(Template template) {
    super();
    this.template = template;
  }
  
  public Template getTemplate() {
    return template;
  }

  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<TemplateSelectedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(TemplateSelectedEventHandler handler) {
    handler.onTemplateSelected(this);
  }


}
