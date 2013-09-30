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
package org.openremote.modeler.utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


/**
 * A utility class for rotating an image. 
 * @author javen
 *
 */
public class ImageRotateUtil {
   /**
    * The image type supported by this class. 
    */
   public static final String SUPPORT_IMAGE_TYPE = ".png " + ".jpeg " + ".gif " + ".jpg ";

   public static File rotate(File sourceImage, String targetImageName, double degree) {

      String extentionName = getExtentionName(sourceImage.getName());

      if (extentionName != null && isASupportedImageType(extentionName)) {
         try {
            return doRotate(sourceImage, targetImageName, Math.toRadians(degree));
         } catch (IOException e) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static BufferedImage doRotate(File sourceImageFile, double angle) {
      try {
         BufferedImage image = ImageIO.read(sourceImageFile);
         double sin = Math.abs(Math.sin(angle)), cos = Math.abs(Math.cos(angle));
         int w = image.getWidth(), h = image.getHeight();
         int neww = (int) Math.floor(w * cos + h * sin), newh = (int) Math.floor(h * cos + w * sin);
         int transparency = image.getColorModel().getTransparency();
         BufferedImage result = new BufferedImage(image.getHeight(null),image.getWidth(null),transparency);
         Graphics2D g = result.createGraphics();
         g.translate((neww - w) / 2, (newh - h) / 2);
         g.rotate(angle, w / 2, h / 2);
         g.drawRenderedImage(image, null);
         return result;
      } catch (IOException e) {
         return null;
      }
   }

   private static File doRotate(File sourceImageFile, String targetName, double degree) throws IOException {
      
      BufferedImage outImage = doRotate(sourceImageFile, degree);

      File targetFile = new File(targetName);
      ImageIO.write(outImage, getExtentionName(sourceImageFile.getName()), targetFile);
      return targetFile;
   }


   private static String getExtentionName(String fileName) {
      int pointIndex = fileName.lastIndexOf(".");
      if (-1 != pointIndex && pointIndex != fileName.length() - 1) {
         String extentionName = fileName.substring(pointIndex + 1);
         return extentionName;
      }
      return null;
   }

   private static boolean isASupportedImageType(String extentionName) {
      return SUPPORT_IMAGE_TYPE.contains(extentionName);
   }
}