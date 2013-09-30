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

import java.text.DecimalFormat;
import java.util.Date;


/**
 * LIRCEntry the entry of specify version file.
 * 
 * @author Tomsky
 */
public class LIRCEntry {
   /** The path. */
   private String path;

   /** The version. */
   private long version;

   /** The file. */
   private boolean file;

   /** The content. */
   private String content;

   /** The author. */
   private String author;

   /** The date. */
   private Date date;

   /** The size. */
   private long size;

   private boolean isHeadversion;

   /**
    * Instantiates a new lIRC entry.
    */
   public LIRCEntry() {
      isHeadversion = false;
   }

   /**
    * Gets the content.
    * 
    * @return the content
    */
   public String getContent() {
      return content;
   }

   /**
    * Sets the content.
    * 
    * @param content
    *           the new content
    */
   public void setContent(String content) {
      this.content = content;
   }

   /**
    * Checks if is file.
    * 
    * @return true, if is file
    */
   public boolean isFile() {
      return file;
   }

   /**
    * Sets the file.
    * 
    * @param file
    *           the new file
    */
   public void setFile(boolean file) {
      this.file = file;
   }

   /**
    * Gets the path.
    * 
    * @return the path
    */
   public String getPath() {
      return path;
   }

   /**
    * Gets the version.
    * 
    * @return the version
    */
   public long getVersion() {
      return version;
   }

   /**
    * Sets the path.
    * 
    * @param path
    *           the new path
    */
   public void setPath(String path) {
      this.path = path;
   }

   /**
    * Sets the version.
    * 
    * @param version
    *           the new version
    */
   public void setVersion(long version) {
      this.version = version;
   }

   /**
    * Gets the author.
    * 
    * @return the author
    */
   public String getAuthor() {
      return author;
   }

   /**
    * Sets the author.
    * 
    * @param author
    *           the new author
    */
   public void setAuthor(String author) {
      this.author = author;
   }

   /**
    * Gets the age.
    * 
    * @return the age
    */
   public String getAge() {
      return DateFormatter.format(date);
   }

   /**
    * Gets the size.
    * 
    * @return the size
    */
   public String getSizeString() {
      return new Double(new DecimalFormat(".0").format(size / 1024.0)).toString() + "K";
   }

   /**
    * Gets the size of long.
    * 
    * @return the size of long
    */
   public long getSize() {
      return size;
   }

   /**
    * Gets the date.
    * 
    * @return the date
    */
   public Date getDate() {
      return date;
   }

   /**
    * Sets the date.
    * 
    * @param date
    *           the new date
    */
   public void setDate(Date date) {
      this.date = date;
   }

   /**
    * Sets the content size.
    * 
    * @param size
    *           the new content size
    */
   public void setSize(long size) {
      this.size = size;
   }

   /**
    * Checks if is headversion.
    * 
    * @return true, if is headversion
    */
   public boolean isHeadversion() {
      return isHeadversion;
   }

   /**
    * Sets the headversion.
    * 
    * @param isHeadversion
    *           the new headversion
    */
   public void setHeadversion(boolean isHeadversion) {
      this.isHeadversion = isHeadversion;
   }

}
