package org.openremote.controller.protocol.vera.model;

import org.jdom.Element;
import org.openremote.controller.protocol.vera.VeraClient;
import org.openremote.controller.protocol.vera.VeraCmd;


public class Thermostat extends VeraDevice {

   protected Float heatSetpoint;
   
   public Thermostat(VeraCategory category, int id, String name, VeraClient client) {
      super(category, id, name, client);
   }

   public void setHeatSetpoint(String paramValue) {
      StringBuffer cmdUrl = new StringBuffer();
      cmdUrl.append("http://");
      cmdUrl.append(client.getAddress());
      cmdUrl.append(":3480/data_request?id=lu_action&output_format=xml&DeviceNum=");
      cmdUrl.append(id);
      cmdUrl.append("&serviceId=urn:upnp-org:serviceId:TemperatureSetpoint1_Heat&action=SetCurrentSetpoint&NewCurrentSetpoint="+paramValue);
      getClient().sendCommand(cmdUrl.toString());
   }
   
   @Override
   protected void updateDeviceSpecificSensors() {
      if ((attachedSensors.get(VeraCmd.GET_HEAT_SETPOINT) != null) && (heatSetpoint != null)) {
         attachedSensors.get(VeraCmd.GET_HEAT_SETPOINT).update(heatSetpoint.toString());
      }
   }

   @Override
   protected void updateDeviceSpecificStatus(Element element) {
      if (element.getAttributeValue("heatsp") != null) {
         this.heatSetpoint = Float.parseFloat(element.getAttributeValue("heatsp"));
      }
   }



}
