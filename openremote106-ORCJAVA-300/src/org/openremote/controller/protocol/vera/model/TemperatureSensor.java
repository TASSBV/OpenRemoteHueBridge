package org.openremote.controller.protocol.vera.model;

import org.jdom.Element;
import org.openremote.controller.protocol.vera.VeraClient;
import org.openremote.controller.protocol.vera.VeraCmd;


public class TemperatureSensor extends VeraDevice {

   protected Float temperature;
   
   public TemperatureSensor(VeraCategory category, int id, String name, VeraClient client) {
      super(category, id, name, client);
   }

   @Override
   protected void updateDeviceSpecificSensors() {
      if ((attachedSensors.get(VeraCmd.GET_TEMPERATURE) != null) && (temperature != null)) {
         attachedSensors.get(VeraCmd.GET_TEMPERATURE).update(temperature.toString());
      }
   }

   @Override
   protected void updateDeviceSpecificStatus(Element element) {
      if (element.getAttributeValue("temperature") != null) {
         this.temperature = Float.parseFloat(element.getAttributeValue("temperature"));
      }
   }

}
