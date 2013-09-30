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
package org.openremote.beehive.utils;

import java.io.File;
import java.io.FileInputStream;

import org.openremote.beehive.TestConstraint;
/**
 * Test fixture utility.
 * 
 * @author Dan
 *
 */

public class FixtureUtil {
   
   private FixtureUtil() {
   }

   
   public static File getFile(String fileName) {
      return new File(path() + fileName);
      
   }
   public static String path() {
      return FixtureUtil.class.getClassLoader().getResource(TestConstraint.FIXTURE_DIR).getFile();
   }

   public static String getFileContent(String fileName) {
      return FileUtil.readFileToString(new File(FixtureUtil.path() + fileName)).toString().trim();
   }
   
   public static FileInputStream getFileInputstream(String fileName) {
      return FileUtil.readStream(FixtureUtil.path() + fileName);
   }

}
