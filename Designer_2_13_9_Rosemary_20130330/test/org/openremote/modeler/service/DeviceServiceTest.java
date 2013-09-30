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

import junit.framework.Assert;

import org.hibernate.ObjectNotFoundException;
import org.openremote.modeler.SpringTestContext;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.User;
import org.testng.annotations.Test;


/**
 * The TestNG Unit Test for Device Service CRUD.
 * 
 * @author Tomsky, Dan 2009-7-10
 */
public class DeviceServiceTest {
   
   /** The device service. */
   private DeviceService deviceService =
      (DeviceService) SpringTestContext.getInstance().getBean("deviceService");
   
   /** The user service. */
   private UserService userService =
      (UserService) SpringTestContext.getInstance().getBean("userService");

    /**
       * Test save device.
       */
   @Test
   public void save() {
      Device device = new Device();
      device.setName("tv");
      device.setModel("tv");
      device.setVendor("sony");
      deviceService.saveDevice(device);
      Device deviceInDB = deviceService.loadById(1L);
      Assert.assertEquals(deviceInDB.getName(), device.getName());
      
   }
   
   /**
    * Delete.
    */
   @Test(dependsOnMethods = "save", expectedExceptions = { ObjectNotFoundException.class })
   public void delete() {
      Device device = new Device();
      device.setName("xxx");
      device.setModel("MP8640");
      device.setVendor("3m");
      deviceService.saveDevice(device);
      deviceService.deleteDevice(device.getOid());
      Device deviceInDB = deviceService.loadById(device.getOid());
      deviceInDB.getName(); //throws ObjectNotFoundException
   }
   
   /**
    * Load all.
    */
   @Test(dependsOnMethods = "save")
   public void loadAll() {
      
      User user = new User();
      user.setUsername("dan");
      user.setPassword("XXX");
      Account account = new Account();
      user.setAccount(account);
      Device device = new Device();
      device.setName("tv");
      device.setModel("tv");
      device.setVendor("sony");
      account.addDevice(device);
      account.addDevice(device);
      account.addDevice(device);
      account.addDevice(device);
      account.addDevice(device);
      account.addDevice(device);
      userService.saveUser(user);
      Assert.assertEquals(6, deviceService.loadAll(account).size());
      
   }
   
   /**
    * Update.
    */
   @Test(dependsOnMethods = "save")
   public void update() {
      Device device = new Device();
      device.setName("xxx");
      device.setModel("MP8640");
      device.setVendor("3m");
      deviceService.saveDevice(device);
      Device deviceInDB = deviceService.loadById(device.getOid());
      
      deviceInDB.setName("aaa");
      deviceInDB.setVendor("sony");
      deviceInDB.setModel("tv");
      deviceService.updateDevice(deviceInDB);
      
      device = deviceService.loadById(device.getOid());
      Assert.assertEquals(deviceInDB.getName(), device.getName());
      
      deviceService.deleteDevice(device.getOid());
   }
}
