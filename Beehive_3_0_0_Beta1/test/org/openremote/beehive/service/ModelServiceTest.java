/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.beehive.service;

import org.openremote.beehive.LIRCTestBase;
import org.openremote.beehive.SpringTestContext;
import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.file.LircConfFileTestScraper;
import org.openremote.beehive.utils.FileUtil;
import org.openremote.beehive.utils.FixtureUtil;

public class ModelServiceTest extends LIRCTestBase {

   private ModelService service = (ModelService) SpringTestContext.getInstance().getBean("modelService");

   public void testAddLircConfFile() {
      service.add(FileUtil.readStream(FixtureUtil.path() + "/remotes/3m/MP8640"), "3m", "MP8640");// single section
      service.add(FileUtil.readStream(FixtureUtil.path() + "/remotes/sigma_designs/lircd.conf.realmagic"), "sigma_designs",
            "lircd.conf.realmagic");// multi section
      service.add(FileUtil.readStream(FixtureUtil.path() + "/remotes/zenith/ZN110"), "zenith", "ZN110");// raw code
   }

   public void testScrap() {
      LircConfFileTestScraper.scrapDir(FixtureUtil.path() + "/remotes");
   }

   public void testExportText() {
      System.out.println(service.exportText(1L));
   }

   public void testUpdate() {
      // String[] paths = {"/3m"};
      // try {
      // service.update(paths, "update directory", "username");
      // } catch (SVNException e) {
      // e.printStackTrace();
      // }
   }

   public void testRollback() {
      // String path = "/3m/MP8640";
      // try {
      // service.rollback(path, 163, "tomsky");
      // } catch (SVNException e) {
      // e.printStackTrace();
      // }
   }

   public void testCount() {
      assertEquals(24, service.count());
   }

   public void testSync() {
      // File file = new File("c:/sony/RM-862");
      // service.syncWith(file);
   }
}
