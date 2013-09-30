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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openremote.beehive.Configuration;
import org.openremote.beehive.exception.LIRCrawlerException;
import org.openremote.beehive.file.EnumCharset;
import org.openremote.beehive.spring.SpringContext;

/**
 * Utility class for File.
 * 
 * @author Dan 2009-2-16
 */
public class FileUtil {

   /** The Constant LOGGER. */
   private static final Logger LOGGER = Logger.getLogger(FileUtil.class.getName());

   /** The configuration. */
   public static Configuration configuration = (Configuration) SpringContext.getInstance().getBean("configuration");

   /**
    * Instantiates a new file util.
    */
   private FileUtil() {
   }

   /**
    * Reads a <code>FileInputStream</code> from a file with a specified file system path.
    * 
    * @param path
    *           a specified file system path,e.g C:\remotes\3m\MP8640
    * 
    * @return <code>FileInputStream</code>
    */
   public static FileInputStream readStream(String path) {
      File file = new File(path);
      if (!file.exists() || file.isDirectory()) {
         return null;
      }
      FileInputStream fis = null;
      try {
         fis = new FileInputStream(path);
      } catch (FileNotFoundException e) {
         LOGGER.error("The file " + path + " is not found.", e);
      }
      return fis;
   }

   /**
    * Gets a list of content text from a file with a specified file system path.
    * 
    * @param path
    *           a specified file system path,e.g C:\remotes\3m\MP8640
    * 
    * @return list of String
    */
   public static List<String> getContentList(String path) {
      File file = new File(path);
      if (!file.exists() || file.isDirectory()) {
         return null;
      }
      BufferedReader br = null;
      List<String> list = new ArrayList<String>();
      try {
         br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
         String line = null;
         while ((line = br.readLine()) != null) {
            list.add(line);
         }
      } catch (FileNotFoundException e) {
         LOGGER.error("The file " + path + " is not found.", e);
      } catch (IOException e) {
         LOGGER.error("Read file " + path + " error.", e);
      } finally {
         try {
            if (br != null) {
               br.close();
            }
         } catch (IOException e) {
            LOGGER.error("Close BufferedReader error.", e);
         }
      }
      return list;
   }

   /**
    * Gets a list of content text from a file with a specified file system path <code>FileInputStream</code>.
    * 
    * @param fis
    *           a specified <code>FileInputStream</code>
    * 
    * @return list of String
    */
   public static List<String> getContentList(FileInputStream fis) {

      BufferedReader br = null;
      List<String> list = new ArrayList<String>();
      try {
         br = new BufferedReader(new InputStreamReader(fis));
         String line = null;
         while ((line = br.readLine()) != null) {
            list.add(line);
         }
      } catch (IOException e) {
         LOGGER.error("Read fileInputStream error.", e);
      } finally {
         try {
            if (br != null) {
               br.close();
            }
            if (fis != null) {
               fis.close();
            }
         } catch (IOException e) {
            LOGGER.error("Close BufferedReader error.", e);
         }
      }
      return list;
   }

   /**
    * Gets a <code>PrintWriter</code> from a file with a specified file system path and charset, the defaut charet is
    * UTF-8.
    * 
    * @param filePath
    *           a specified file system path,e.g C:\remotes\3m\MP8640
    * @param charset
    *           a charset enumeration:<code>EnumCharset</code>
    * 
    * @return a <code>PrintWriter</code>
    */
   public static PrintWriter getPrintWriterFromFile(String filePath, EnumCharset... charset) {
      File file = new File(filePath);
      if (!file.exists()) {
         try {
            file.getParentFile().mkdir();
            file.createNewFile();
         } catch (IOException e1) {
            LOGGER.error("Can not create file " + filePath, e1);
         }
      }
      PrintWriter pw = null;
      try {
         if (charset != null) {
            if (charset.length != 0) {
               pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filePath), charset[0].getValue()));
            } else {
               pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filePath)));
            }
         } else {
            pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filePath)));
         }
      } catch (UnsupportedEncodingException e) {
         LOGGER.error("Get PrintWriter from " + filePath + " occurs UnsupportedEncodingException", e);
      } catch (FileNotFoundException e) {
         LOGGER.error("Get PrintWriter from " + filePath + " occurs FileNotFoundException", e);
      }
      return pw;
   }

   /**
    * Writes a file on a file system with a specified file system path and charset, the defaut charet is UTF-8.
    * 
    * @param filePath
    *           a specified file system path,e.g C:\remotes\3m\MP8640
    * @param content
    *           a content text to write
    * @param charset
    *           a charset enumeration:<code>EnumCharset</code>
    */
   public static void writeFile(String filePath, String content, EnumCharset... charset) {
      File dir = new File(filePath).getParentFile();
      if (dir.exists() == false) {
         dir.mkdirs();
      }
      PrintWriter pw = null;
      if (charset.length != 0) {
         pw = getPrintWriterFromFile(filePath, charset);
      } else {
         pw = getPrintWriterFromFile(filePath);
      }
      pw.println(content);
      pw.close();
   }

   /**
    * Writes a String list to a file with a specified file system path and charset, the defaut charet is UTF-8.
    * 
    * @param filePath
    *           a specified file system path,e.g C:\remotes\3m\MP8640
    * @param contentList
    *           content list
    * @param charset
    *           a charset enumeration:<code>EnumCharset</code>
    */
   public void writeListToFile(String filePath, List<String> contentList, EnumCharset... charset) {
      File dir = new File(filePath).getParentFile();
      if (dir.exists() == false) {
         dir.mkdirs();
      }
      PrintWriter pw = null;
      if (charset.length != 0) {
         pw = getPrintWriterFromFile(filePath, charset);
      } else {
         pw = getPrintWriterFromFile(filePath);
      }
      if (contentList != null) {
         int i = 1;
         for (String line : contentList) {
            i++;
            if (line != null && line.indexOf("\\xEF") < 0) {
               pw.println(line.toString());
            }
         }
      }
      pw.close();
   }

   /**
    * Checks if the file is a image.
    * 
    * @param file
    *           a file to check
    * 
    * @return true if it is a image,false otherwise.
    */
   public static boolean isImage(File file) {
      String path = file.getAbsolutePath();
      String extension = path.substring(path.lastIndexOf('.') + 1);
      return extension.toLowerCase().matches("gif|png|jpg|bmp");
   }

   /**
    * Checks if a file is a HTML.
    * 
    * @param file
    *           a file to check
    * 
    * @return true if it is a HTML,false otherwise.
    */
   public static boolean isHTML(File file) {
      String path = file.getAbsolutePath();
      String extension = path.substring(path.lastIndexOf('.') + 1);
      return extension.toLowerCase().matches("html|htm");
   }

   /**
    * Checks if it is ignored. lircrc,HTML,image,lircmd.conf,Thumbs.db will be ignored.
    * 
    * @param file
    *           a file to check
    * 
    * @return true if it is a ignored,false otherwise.
    */
   public static boolean isIgnored(File file) {
      String path = file.getAbsolutePath().toLowerCase();
      boolean isImage = isImage(file);
      boolean isLircrc = path.endsWith(".lircrc");
      boolean isLircmd = path.indexOf("lircmd.conf") != -1;
      boolean isHtml = isHTML(file);
      boolean isThumbs = path.endsWith("Thumbs.db");
      return isImage || isLircrc || isLircmd || isHtml || isThumbs;
   }

   /**
    * Splits a file path by path separator.
    * 
    * @param file
    *           a file to split
    * 
    * @return array of String including directory name and file name split by path separator
    */
   public static String[] splitPath(File file) {
      String path = file.getAbsolutePath();
      if (path.startsWith("/")) {
         return path.split("/");
      } else {
         return path.split("\\\\");
      }
   }

   /**
    * Copy file.
    * 
    * @param srcFile
    *           the src file
    * @param destFile
    *           the dest file
    */
   public static void copyFile(File srcFile, File destFile) {
      try {
         FileUtils.copyFile(srcFile, destFile);
      } catch (IOException e) {
         LOGGER.error("Copy file from " + srcFile + " to " + destFile + " failed!", e);
      }
   }

   /**
    * Create a file from a InputStream.
    * 
    * @param in
    *           the in
    * @param destFile
    *           the dest file
    */
   public static void createFile(InputStream in, File destFile) {
      if (destFile.exists()) {
         destFile.delete();
      }
      FileOutputStream out = null;
      try {
         out = new FileOutputStream(destFile);

         byte[] buffer = new byte[8 * 1024];
         int count = 0;
         do {
            out.write(buffer, 0, count);
            count = in.read(buffer, 0, buffer.length);
         } while (count != -1);
      } catch (IOException e) {
         LOGGER.error("Create file " + destFile.getName() + " from inputStream occur error.", e);
      } finally {
         try {
            if (out != null) {
               out.close();
            }
            if (in != null) {
               in.close();

            }
         } catch (IOException e) {
            LOGGER.error("Can not close inputStream or fileOutputStream.", e);
         }
      }
   }

   /**
    * Delete directory.
    * 
    * @param directory
    *           the directory
    */
   public static void deleteDirectory(File directory) {
      try {
         FileUtils.deleteDirectory(directory);
      } catch (IOException e) {
         LOGGER.error("Delete directory " + directory.getPath() + " failed!", e);
      }
   }

   /**
    * Read file to string.
    * 
    * @param file
    *           the file
    * 
    * @return the string buffer
    */
   public static StringBuffer readFileToString(File file) {
      StringBuffer strBuffer = new StringBuffer();
      InputStream is = null;
      byte[] buffer = null;
      int count = 0;
      try {
         is = new FileInputStream(file);
         do {
            buffer = new byte[1024];
            count = is.read(buffer, 0, buffer.length);
            strBuffer.append(new String(buffer));
         } while (count != -1);
      } catch (IOException e) {
         LOGGER.error("Read file " + file.getName() + " to StringBuffer occur error.", e);
      } finally {
         try {
            is.close();
         } catch (IOException e) {
            LOGGER.error("Can not close inputStream.", e);
         }
      }
      return strBuffer;
   }

   /**
    * Write line to file.
    * 
    * @param fileName
    *           the file name
    * @param line
    *           the string line
    */
   public static void writeLineToFile(String fileName, String line) {
      File f = new File(fileName);
      if (!f.getParentFile().exists()) {
         f.getParentFile().mkdirs();
      }
      try {
         if (!f.exists()) {
            f.createNewFile();
         }
         FileWriter writer = new FileWriter(fileName, true);
         writer.write(line);
         writer.write(13);
         writer.write(10);
         writer.close();
      } catch (IOException e) {
         LOGGER.error("Write line " + line + " to file " + fileName + " occur error.", e);
      }
   }

   /**
    * Relative workcopy path.
    * 
    * @param file
    *           the file
    * 
    * @return the string
    */
   public static String relativeWorkcopyPath(File file) {
      String wc = new File(configuration.getWorkCopyDir()).getPath();
      return file.getAbsolutePath().replace(wc, "").replaceAll("\\\\", "/");
   }

   /**
    * Ignore the tomcat launch path when committing file. e.g.:
    * "/usr/share/apache-tomcat-5.5.27/bin/home/openremote/work/workCopy/holtek" will be fixed to be
    * "/home/openremote/work/workCopy/holtek".
    * 
    * @param file
    *           the committing file
    * @return fixed file
    */
   public static File fixCommitFilePath(File file) {
      String wc = new File(configuration.getWorkCopyDir()).getPath();
      String absolutePath = file.getAbsolutePath();
      return new File(absolutePath.substring(absolutePath.indexOf(wc)));
   }
   
   /**
    * Relative workcopy path.
    * 
    * @param path
    *           the path
    * 
    * @return the string
    */
   public static String relativeWorkcopyPath(String path) {
      String wc = new File(configuration.getWorkCopyDir()).getPath();
      return path.replace(wc, "").replaceAll("\\\\", "/");
   }

   /**
    * Delete file on exist.
    * 
    * @param file
    *           the file
    */
   public static void deleteFileOnExist(File file) {
      if (file.exists()) {
         file.delete();
      }
   }

   /**
    * Write inputStream to file.
    * 
    * @param is
    *           the is
    * @param dstPath
    *           the dest path
    */
   public static void writeInputStreamToFile(InputStream is, String dstPath) {
      OutputStream out = null;
      try {
         File parent = new File(dstPath).getParentFile();
         if (!parent.exists()) {
            parent.mkdirs();
         }
         out = new FileOutputStream(dstPath);
         byte[] buf = new byte[1024 * 1024];
         int len;
         while ((len = is.read(buf)) > 0) {
            out.write(buf, 0, len);
         }
      } catch (IOException e) {
         LOGGER.error("Write file " + dstPath + " failed!", e);
      } finally {
         try {
            is.close();
            out.close();
         } catch (IOException e) {
            LOGGER.error("Can not close inputStream or fileOutputStream when write inputStream to file " + dstPath, e);
         }
      }
   }

   /**
    * Checks if is image.
    * 
    * @param path
    *           the path
    * 
    * @return true, if is image
    */
   public static boolean isImage(String path) {
      String extension = path.substring(path.lastIndexOf('.') + 1);
      return extension.toLowerCase().matches("gif|png|jpg|bmp");
   }

   /**
    * Write string to file.
    * 
    * @param content
    *           the content
    * @param filePath
    *           the file path
    */
   public static void writeStringToFile(String content, String filePath) {
      File file = new File(filePath);
      if (!file.getParentFile().exists()) {
         file.getParentFile().mkdirs();
      }
      try {
         FileUtils.writeStringToFile(file, content, "UTF-8");
      } catch (IOException e) {
         LOGGER.error("Can't write content to file " + filePath, e);
         LIRCrawlerException ee = new LIRCrawlerException("Can't write content to file " + filePath, e);
         ee.setErrorCode(LIRCrawlerException.CRAWLER_WRITEFILE_ERROR);
         throw ee;
      }
   }
}
