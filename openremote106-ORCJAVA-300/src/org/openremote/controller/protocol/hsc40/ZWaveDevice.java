package org.openremote.controller.protocol.hsc40;

import java.util.ArrayList;
import java.util.List;

import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.StateSensor;
import org.openremote.controller.model.sensor.SwitchSensor;

public class ZWaveDevice {

   public String deviceId;
   public String deviceType;
   public String portId;
   public String data;
   public List<Sensor> sensors = new ArrayList<Sensor>();
   
   public ZWaveDevice(String deviceId, String portId, String deviceType) {
      this.deviceId = deviceId;
      this.portId = portId;
      this.deviceType = deviceType;
   }
   
   public ZWaveDevice(String deviceId, String portId, String deviceType, Sensor sensor) {
      this.deviceId = deviceId;
      this.portId = portId;
      this.sensors.add(sensor);
      this.deviceType = deviceType;
   }


   public String getDeviceId() {
      return deviceId;
   }


   public void setDeviceId(String deviceId) {
      this.deviceId = deviceId;
   }


   public String getPortId() {
      return portId;
   }


   public void setPortId(String portId) {
      this.portId = portId;
   }


   public String getData() {
      return data;
   }


   public void setData(String data) {
      this.data = data;
      for (Sensor sensor : this.sensors) {
         if (deviceType.equalsIgnoreCase("dimmer") && (sensor instanceof RangeSensor)) {
            sensor.update(""+Integer.valueOf(data,16));
         } else if ((sensor instanceof SwitchSensor) || (sensor instanceof StateSensor)) {
            if (data.equals("00")) {
               sensor.update("off");
            } else {
               sensor.update("on");
            }
         }
      }
   }

   public List<Sensor> getSensors() {
      return sensors;
   }

   public void addSensor(Sensor sensor) {
      this.sensors.add(sensor);
   }

   public String getDeviceType() {
      return deviceType;
   }

   public void setDeviceType(String deviceType) {
      this.deviceType = deviceType;
   }
   

   
}
