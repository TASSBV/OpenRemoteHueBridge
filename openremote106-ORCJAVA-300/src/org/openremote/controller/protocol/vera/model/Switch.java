package org.openremote.controller.protocol.vera.model;

import org.jdom.Element;
import org.openremote.controller.protocol.vera.VeraClient;
import org.openremote.controller.protocol.vera.VeraCmd;


public class Switch extends VeraDevice {

   protected Boolean status;
   
   public Switch(VeraCategory category, int id, String name, VeraClient client) {
      super(category, id, name, client);
   }

   public void turnOn() {
      StringBuffer cmdUrl = new StringBuffer();
      cmdUrl.append("http://");
      cmdUrl.append(client.getAddress());
      cmdUrl.append(":3480/data_request?id=lu_action&output_format=xml&DeviceNum=");
      cmdUrl.append(id);
      cmdUrl.append("&serviceId=urn:upnp-org:serviceId:SwitchPower1&action=SetTarget&newTargetValue=1");
      getClient().sendCommand(cmdUrl.toString());
   }

   public void turnOff() {
      StringBuffer cmdUrl = new StringBuffer();
      cmdUrl.append("http://");
      cmdUrl.append(client.getAddress());
      cmdUrl.append(":3480/data_request?id=lu_action&output_format=xml&DeviceNum=");
      cmdUrl.append(id);
      cmdUrl.append("&serviceId=urn:upnp-org:serviceId:SwitchPower1&action=SetTarget&newTargetValue=0");
      getClient().sendCommand(cmdUrl.toString());
   }

   @Override
   protected void updateDeviceSpecificSensors() {
      if ((attachedSensors.get(VeraCmd.GET_STATUS) != null) && (status != null)) {
         attachedSensors.get(VeraCmd.GET_STATUS).update(status?"on":"off");
      }
   }

   @Override
   protected void updateDeviceSpecificStatus(Element element) {
      if (element.getAttributeValue("status") != null) {
         this.status = (element.getAttributeValue("status").equals("1"))?true:false;
      }
   }

}
