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
package org.openremote.modeler.ui;

import org.openremote.modeler.selenium.DebugId;
import org.openremote.modeler.selenium.SeleniumTestNGBase;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * The TestNGTest for Device CRUD Selenium Test.
 * 
 * @author Dan 2009-7-31
 */
public class DeviceSeleniumTestNGTest extends SeleniumTestNGBase {
   
   /**
    * Constant pause millisecond.
    */
   private static final int PAUSE_MS = 2000;
   
   /**
    * Creates the device.
    * 
    * @throws Exception
    *            the exception
    */
   @Test
   public void createDevice() throws Exception {
      pause(PAUSE_MS);
      click(DebugId.DEVICE_NEW_BTN);
      pause(PAUSE_MS);
      click(DebugId.NEW_DEVICE_MENU_ITEM);
      // pause(PAUSE_MS);
      String name = "seleniumTest" + Math.random();
      type(DebugId.DEVICE_NAME_FIELD, name);
      type(DebugId.DEVICE_VENDOR_FIELD, "ThoughtWorks");
      type(DebugId.DEVICE_MODEL_FIELD, "Selenium RC");
      click(DebugId.DEVICE_FINISH_BTN);
      pause(PAUSE_MS);
      
      Assert.assertTrue(selenium.isTextPresent(name));
   }

   /**
    * Edits the device.
    * 
    * @throws Exception
    *            the exception
    */
   @Test(dependsOnMethods = { "createDevice" })
   public void editDevice() throws Exception {
      click(DebugId.DEVICE_EDIT_BTN);
      pause(PAUSE_MS);
      double random = Math.random();
      String name = "seleniumTestUpdated" + random;
      
      String vendor = "ThoughtWorks" + random;
      String model = "Selenium RC" + random;
      pause(PAUSE_MS);
      type(DebugId.DEVICE_NAME_FIELD, name);
      type(DebugId.DEVICE_VENDOR_FIELD, vendor);
      type(DebugId.DEVICE_MODEL_FIELD, model);
      click(DebugId.COMMON_SUBMIT_BTN);
      pause(PAUSE_MS);
      click(DebugId.DEVICE_EDIT_BTN);
      pause(PAUSE_MS);
      Assert.assertEquals(getValue(DebugId.DEVICE_NAME_FIELD), name);
      Assert.assertEquals(getValue(DebugId.DEVICE_VENDOR_FIELD), vendor);
      Assert.assertEquals(getValue(DebugId.DEVICE_MODEL_FIELD), model);
      
   }
   
   /**
    * Delete device.
    * 
    * @throws Exception
    *            the exception
    */
   @Test(dependsOnMethods = { "editDevice" })
   public void deleteDevice() throws Exception {
      String lastDeviceName = getValue(DebugId.DEVICE_NAME_FIELD);
      click(DebugId.COMMON_SUBMIT_BTN);
      pause(PAUSE_MS);
      click(DebugId.DELETE_DEVICE_BUTTON);
      pause(PAUSE_MS);
      Assert.assertFalse(selenium.isTextPresent(lastDeviceName));
   }

}
