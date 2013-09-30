package org.openremote.controller.protocol.vera.model;

import org.jdom.Element;
import org.openremote.controller.protocol.vera.VeraClient;
import org.openremote.controller.protocol.vera.VeraCmd;


public class HumiditySensor extends VeraDevice {

   protected Float humidity;
   
   public HumiditySensor(VeraCategory category, int id, String name, VeraClient client) {
      super(category, id, name, client);
   }

   @Override
   protected void updateDeviceSpecificSensors() {
      if ((attachedSensors.get(VeraCmd.GET_HUMIDITY) != null) && (humidity != null)) {
         attachedSensors.get(VeraCmd.GET_HUMIDITY).update(humidity.toString());
      }
   }

   @Override
   protected void updateDeviceSpecificStatus(Element element) {
      if (element.getAttributeValue("humidity") != null) {
         this.humidity = Float.parseFloat(element.getAttributeValue("humidity"));
      }
   }

}
