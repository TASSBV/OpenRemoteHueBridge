/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.modeler.service;

import java.util.List;

import org.openremote.modeler.SpringTestContext;
import org.openremote.modeler.dao.GenericDAO;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.ProtocolAttr;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorCommandRef;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.State;
import org.openremote.modeler.domain.User;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SensorServiceTest {

   private SensorService sensorService =
      (SensorService) SpringTestContext.getInstance().getBean("sensorService");
   
   private DeviceCommandService deviceCommandService =
      (DeviceCommandService) SpringTestContext.getInstance().getBean("deviceCommandService");
   
   private GenericDAO genericDAO =
      (GenericDAO) SpringTestContext.getInstance().getBean("genericDAO");
   
   private UserService userService =
      (UserService) SpringTestContext.getInstance().getBean("userService");
   
   
   @Test
   public void save() {
      DeviceCommand deviceCommand = new DeviceCommand();
      deviceCommand.setName("command1");
      
      Protocol protocol = new Protocol();
      protocol.setType("http");
      protocol.setDeviceCommand(deviceCommand);
      
      ProtocolAttr protocolAttr = new ProtocolAttr();
      protocolAttr.setName("url");
      protocolAttr.setValue("http://www.sina.com");
      protocolAttr.setProtocol(protocol);
      protocol.getAttributes().add(protocolAttr);
      deviceCommand.setProtocol(protocol);
      deviceCommandService.save(deviceCommand);
      
      Sensor sensor = new Sensor();
      String name = "sensor1";
      sensor.setName(name);
      SensorCommandRef sensorCommandRef = new SensorCommandRef();
      sensorCommandRef.setDeviceCommand(deviceCommand);
      sensorCommandRef.setSensor(sensor);
      sensor.setSensorCommandRef(sensorCommandRef);
      sensor.setType(SensorType.SWITCH);
      
      User user = new User();
      Account account = new Account();
      user.setAccount(account);
      user.setUsername("user1");
      user.setPassword("xxxx");
      
      Sensor sensorInDB = sensorService.saveSensor(sensor);
      account.getSensors().add(sensor);
      userService.saveUser(user);
      Assert.assertEquals(sensorInDB.getName(), name);
      
   }
   
   @Test(dependsOnMethods = "delete")
   public void saveRange() {
      DeviceCommand deviceCommand = new DeviceCommand();
      deviceCommand.setName("command");
      
      Protocol protocol = new Protocol();
      protocol.setType("http");
      protocol.setDeviceCommand(deviceCommand);
      
      ProtocolAttr protocolAttr = new ProtocolAttr();
      protocolAttr.setName("url");
      protocolAttr.setValue("http://www.baidu.com");
      protocolAttr.setProtocol(protocol);
      protocol.getAttributes().add(protocolAttr);
      deviceCommand.setProtocol(protocol);
      deviceCommandService.save(deviceCommand);
      
      RangeSensor sensor = new RangeSensor();
      String name = "rangSensor";
      sensor.setName(name);
      SensorCommandRef sensorCommandRef = new SensorCommandRef();
      sensorCommandRef.setDeviceCommand(deviceCommand);
      sensorCommandRef.setSensor(sensor);
      sensor.setSensorCommandRef(sensorCommandRef);
      sensor.setMin(5);
      sensor.setMax(30);
      
      User user = new User();
      Account account = new Account();
      user.setAccount(account);
      user.setUsername("user2");
      user.setPassword("YYYYY");
      
      RangeSensor sensorInDB = (RangeSensor)sensorService.saveSensor(sensor);
      account.getSensors().add(sensor);
      userService.saveUser(user);
      Assert.assertEquals(sensorInDB.getMin(), 5);
      
   }
   
   @Test(dependsOnMethods = "saveRange")
   public void saveCustom() {
      DeviceCommand deviceCommand = new DeviceCommand();
      deviceCommand.setName("commandCo");
      
      Protocol protocol = new Protocol();
      protocol.setType("http");
      protocol.setDeviceCommand(deviceCommand);
      
      ProtocolAttr protocolAttr = new ProtocolAttr();
      protocolAttr.setName("url");
      protocolAttr.setValue("http://www.hao123.com");
      protocolAttr.setProtocol(protocol);
      protocol.getAttributes().add(protocolAttr);
      deviceCommand.setProtocol(protocol);
      deviceCommandService.save(deviceCommand);
      
      CustomSensor sensor = new CustomSensor();
      String name = "customSensor";
      sensor.setName(name);
      SensorCommandRef sensorCommandRef = new SensorCommandRef();
      sensorCommandRef.setDeviceCommand(deviceCommand);
      sensorCommandRef.setSensor(sensor);
      sensor.setSensorCommandRef(sensorCommandRef);
      State state1 = new State();
      state1.setSensor(sensor);
      sensor.addState(state1);
      
      User user = new User();
      Account account = new Account();
      user.setAccount(account);
      user.setUsername("user3");
      user.setPassword("ZZZZZ");
      
      CustomSensor sensorInDB = (CustomSensor)sensorService.saveSensor(sensor);
      account.getSensors().add(sensor);
      userService.saveUser(user);
      Assert.assertEquals(sensorInDB.getStates().size(), 1);
      
   }
   
   @Test(dependsOnMethods = "save")
   public void update() {
      List<Sensor> sensors = genericDAO.loadAll(Sensor.class);
      String name = "sensor2";
      sensors.get(0).setName(name);
      
      Sensor sensorInDB = sensorService.updateSensor(sensors.get(0));
      Assert.assertEquals(sensorInDB.getName(), name);
   }
   
   @Test(dependsOnMethods = "update")
   public void delete() {
      List<Sensor> sensors = genericDAO.loadAll(Sensor.class);
      for (Sensor sensor : sensors) {
         sensorService.deleteSensor(sensor.getOid());
      }
      sensors = genericDAO.loadAll(Sensor.class);
      Assert.assertEquals(sensors.size(), 0);
   }
}
