package org.openremote.controller.protocol.vera.model;

import org.jdom.Element;
import org.openremote.controller.protocol.vera.VeraClient;
import org.openremote.controller.protocol.vera.VeraCmd;


public class PowerMeter extends VeraDevice {

   protected Float watts;
   protected Float kwh;
   
   public PowerMeter(VeraCategory category, int id, String name, VeraClient client) {
      super(category, id, name, client);
   }

   @Override
   protected void updateDeviceSpecificSensors() {
      if ((attachedSensors.get(VeraCmd.GET_WATTS) != null) && (watts != null)) {
         attachedSensors.get(VeraCmd.GET_WATTS).update(watts.toString());
      }
      if ((attachedSensors.get(VeraCmd.GET_CONSUMPTION) != null) && (kwh != null)) {
         attachedSensors.get(VeraCmd.GET_CONSUMPTION).update(kwh.toString());
      }
   }

   @Override
   protected void updateDeviceSpecificStatus(Element element) {
      if (element.getAttributeValue("watts") != null) {
         this.watts = Float.parseFloat(element.getAttributeValue("watts"));
      }
      if (element.getAttributeValue("kwh") != null) {
         this.kwh = Float.parseFloat(element.getAttributeValue("kwh"));
      }
   }

}
