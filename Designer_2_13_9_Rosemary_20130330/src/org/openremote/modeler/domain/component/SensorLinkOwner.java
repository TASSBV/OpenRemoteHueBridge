package org.openremote.modeler.domain.component;

import org.openremote.modeler.client.utils.SensorLink;

public interface SensorLinkOwner {

  SensorLink getSensorLink();
  void setSensorLink(SensorLink sensorLinker);

}
