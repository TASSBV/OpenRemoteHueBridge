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
package org.openremote.beehive.file;

import java.io.File;

import org.openremote.beehive.SpringTestContext;
import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.utils.FileUtil;

public class LircConfFileTestScraper {
   
   protected static ModelService getModelService() {
      return (ModelService) SpringTestContext.getInstance().getBean("modelService");
   }
   
   /**
    * Scraps a directory in file system containing LIRC configuration files.
    */
   public static void scrapDir(String strPath) {
      File dir = new File(strPath);
      for (File vendorDir : dir.listFiles()) {
         if (vendorDir.isDirectory() && !".svn".equals(vendorDir.getName())) {
            for (File modelFile : vendorDir.listFiles()) {
               String[] arr = FileUtil.splitPath(modelFile);
               String vendorName = arr[arr.length - 2];
               String modelName = arr[arr.length - 1];
               if (modelFile.isDirectory() && !".svn".equals(modelFile.getName())) {
                  for (File subModelFile : modelFile.listFiles()) {
                     if (subModelFile.isFile()) {
                        arr = FileUtil.splitPath(subModelFile);
                        modelName = arr[arr.length - 1];
                        importFile(vendorName, modelName, subModelFile);
                     }
                  }
               } else if (modelFile.isFile()) {
                  importFile(vendorName, modelName, modelFile);
               }
            }
         }
      }
   }

   private static void importFile(String vendorName, String modelName, File subModel) {
      if (!FileUtil.isIgnored(subModel)) {
         getModelService().add(FileUtil.readStream(subModel.getAbsolutePath()), vendorName, modelName);
      }
   }

}
