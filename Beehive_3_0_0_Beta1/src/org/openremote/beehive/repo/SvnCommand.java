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
package org.openremote.beehive.repo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.openremote.beehive.exception.SVNException;
import org.openremote.beehive.file.EnumCharset;
import org.openremote.beehive.repo.DiffStatus.Element;
import org.openremote.beehive.utils.FileUtil;

/**
 * The Class SvnCommand.
 * 
 * @author Tomsky
 */
public class SvnCommand {
   
   /** The Constant LOGGER. */
   private static final Logger LOGGER = Logger.getLogger(SvnCommand.class);

   /**
    * Gets the status.
    * 
    * @param path
    *           the path
    * 
    * @return the status
    */
   public static DiffStatus getStatus(String path) {
      String cmd = "svn status " + path;
      BufferedReader br = null;
      DiffStatus diffStatus = new DiffStatus();
      try {
         Process pro = Runtime.getRuntime().exec(cmd);
         br = new BufferedReader(new InputStreamReader(pro.getInputStream(), EnumCharset.UTF_8.getValue()));
         String line = "";
         while ((line = br.readLine()) != null) {
            String[] arr = line.trim().split("\\s+");
            if (arr.length == 2) {
               Actions action = getTextStatus(arr[0].charAt(0));
               if (action.equals(Actions.UNVERSIONED)) {
                  addFileRecursively(diffStatus, new File(arr[1]));
               } else {
                  diffStatus.addElement(diffStatus.new Element(FileUtil.relativeWorkcopyPath(arr[1]), action));
               }
            }
         }
      } catch (IOException e) {
         LOGGER.error("Get status of " + path + " occur error!", e);
         SVNException ee = new SVNException("Get status of " + path + " occur error!", e);
         ee.setErrorCode(SVNException.SVN_GETSTATUS_ERROR);
         throw ee;
      }
      return diffStatus;
   }

   /**
    * Gets the text status.
    * 
    * @param statusChar
    *           the status char
    * 
    * @return the text status
    */
   private static Actions getTextStatus(char statusChar) {
      switch (statusChar) {
      case 'A':
         return Actions.ADDED;
      case '!': // missing or incomplete
         return Actions.MISSING;
      case 'D':
         return Actions.DELETED;
      case 'M':
         return Actions.MODIFIED;
      case '?':
         return Actions.UNVERSIONED;
      default:
         return Actions.NORMAL;
      }
   }

   /**
    * Adds the file recursively.
    * 
    * @param ds
    *           the ds
    * @param file
    *           the file
    */
   private static void addFileRecursively(DiffStatus ds, File file) {
      Element e1 = ds.new Element(FileUtil.relativeWorkcopyPath(file), Actions.UNVERSIONED);
      ds.addElement(e1);
      if (file.isDirectory()) {
         for (File subFile : file.listFiles()) {
            addFileRecursively(ds, subFile);
         }
      }
   }
}
