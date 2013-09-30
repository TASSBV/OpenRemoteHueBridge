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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


/**
 * The Utility for Zip.
 * 
 * @author Dan 2009-5-14
 */
public class ZipUtil {
   
   /** The Constant logger. */
   private static final Logger logger = Logger.getLogger(ZipUtil.class);
   
   /**
    * Unzip a zip.
    * 
    * @param inputStream the input stream
    * @param targetDir the target dir
    * 
    * @return true, if success
    */
   public static boolean unzip(InputStream inputStream, String targetDir) {
      return unzip(inputStream, targetDir, null);
   }
   
   public static boolean unzip(InputStream inputStream, String targetDir, String filterdFileName) {
      ZipInputStream zipInputStream = new ZipInputStream(inputStream);
      ZipEntry zipEntry;
      FileOutputStream fileOutputStream = null;
      File zippedFile = null;
      try {
         while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (!zipEntry.isDirectory()) {
               if (filterdFileName != null && !zipEntry.getName().equals(filterdFileName)) {
                  continue;
               }
               targetDir = targetDir.endsWith("/") || targetDir.endsWith("\\") ? targetDir : targetDir + "/";
               zippedFile = new File(targetDir, zipEntry.getName());
               FileUtils.deleteQuietly(zippedFile);
               FileUtils.touch(zippedFile);
               fileOutputStream = new FileOutputStream(zippedFile);
               int b;
               while ((b = zipInputStream.read()) != -1) {
                  fileOutputStream.write(b);
               }
               fileOutputStream.close();
            }
         }
      } catch (IOException e) {
         logger.error("Can't unzip file to " + zippedFile.getPath(), e);
         return false;
      } finally {
         try {
            zipInputStream.closeEntry();
            if (fileOutputStream != null) {
               fileOutputStream.close();
            }
         } catch (IOException e) {
            logger.error("Error while closing stream.", e);
         }

      }
      return true;
   }
   
   public static boolean unzip(File zipFile, String targetDir) {
      return unzip(zipFile, targetDir, null);
   }
   
   public static boolean unzip(File zipFile, String targetDir, String filterdFileName) {
      InputStream inputStream = null;
      try {
         inputStream = new FileInputStream(zipFile);
         return unzip(inputStream, targetDir, filterdFileName);
      } catch (IOException e) {
         throw new RuntimeException("falied to unzip file : " + zipFile.getName(), e);
      } finally {
         try {
            if (inputStream != null) {
               inputStream.close();
            }
         } catch (IOException e) {
            logger.error("Error while closing stream.", e);
         }

      }
   }

}
