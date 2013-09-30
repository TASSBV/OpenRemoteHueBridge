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

public class ScreenSeleniumTestNGTest extends SeleniumTestNGBase {

   private static final int PAUSE_MS = 2000;

   @Test
   public void createScreen() throws Exception {
      pause(PAUSE_MS);
      click(DebugId.SCREEN_NEW_BTN);
      pause(PAUSE_MS);
      String name = "ScreenTest" + Math.random();
      type(DebugId.SCREEN_NAME_FIELD, name);
      click(DebugId.SCREEN_SUBMIT_BTN);
      pause(PAUSE_MS);

      Assert.assertTrue(selenium.isTextPresent(name));
   }

   @Test(dependsOnMethods = { "createScreen" })
   public void editScreen() throws Exception {
      click(DebugId.SCREEN_EDIT_BTN);
      pause(PAUSE_MS);
      double random = Math.random();
      String name = "ScreenTestUpdated" + random;

      pause(PAUSE_MS);
      type(DebugId.SCREEN_NAME_FIELD, name);
      click(DebugId.SCREEN_SUBMIT_BTN);
      pause(PAUSE_MS);
   }

   @Test(dependsOnMethods = { "editScreen" })
   public void deleteScreen() throws Exception {
      click(DebugId.SCREEN_DELETE_BTN);
      pause(PAUSE_MS);
   }
}
