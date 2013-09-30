package org.openremote.controller.protocol.vera.model;

import org.jdom.Element;
import org.openremote.controller.protocol.vera.VeraClient;


public class GenericDevice extends VeraDevice {

   public GenericDevice(VeraCategory category, int id, String name, VeraClient client) {
      super(category, id, name, client);
   }

   @Override
   protected void updateDeviceSpecificSensors() {
   }

   @Override
   protected void updateDeviceSpecificStatus(Element element) {
   }

}
