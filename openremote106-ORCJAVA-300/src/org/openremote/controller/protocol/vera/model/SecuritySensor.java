package org.openremote.controller.protocol.vera.model;

import org.jdom.Element;
import org.openremote.controller.protocol.vera.VeraClient;
import org.openremote.controller.protocol.vera.VeraCmd;


public class SecuritySensor extends VeraDevice {

   protected Boolean armed;
   protected Boolean tripped;
   
   public SecuritySensor(VeraCategory category, int id, String name, VeraClient client) {
      super(category, id, name, client);
   }

   @Override
   protected void updateDeviceSpecificSensors() {
      if ((attachedSensors.get(VeraCmd.GET_ARMED) != null) && (armed != null)) {
         attachedSensors.get(VeraCmd.GET_ARMED).update(armed?"on":"off");
      }
      if ((attachedSensors.get(VeraCmd.GET_TRIPPED) != null) && (tripped != null)) {
         attachedSensors.get(VeraCmd.GET_TRIPPED).update(tripped?"on":"off");
      }
   }

   @Override
   protected void updateDeviceSpecificStatus(Element element) {
      if (element.getAttributeValue("armed") != null) {
         this.armed = (element.getAttributeValue("armed").equals("1"))?true:false;
      }
      if (element.getAttributeValue("tripped") != null) {
         this.tripped = (element.getAttributeValue("tripped").equals("1"))?true:false;
      }
   }

}
