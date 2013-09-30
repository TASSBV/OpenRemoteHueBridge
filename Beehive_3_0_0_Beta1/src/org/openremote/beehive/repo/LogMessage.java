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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * LogMessage include the specify revision's changes
 * @author Tomsky
 * 
 */
public class LogMessage {
   private String revision;
   private String author;
   private String comment;
   private Date date;
   private List<Character> actions;
   private List<ChangePath> changePaths;

   public LogMessage() {
      this.actions = new ArrayList<Character>();
      this.changePaths = new ArrayList<ChangePath>();
   }

   public class ChangePath {
      private String path;
      private Character action;

      public String getPath() {
         return path;
      }

      public Character getAction() {
         return action;
      }

      public void setPath(String path) {
         this.path = path;
      }

      public void setAction(Character action) {
         this.action = action;
      }

      public ChangePath(String path, Character action) {
         this.path = path;
         this.action = action;
      }
   }

   public String getRevision() {
      return revision;
   }

   public String getAuthor() {
      return author;
   }

   public String getComment() {
      return comment;
   }

   public List<Character> getActions() {
      return actions;
   }

   public List<ChangePath> getChangePaths() {
      return changePaths;
   }

   public void setRevision(String revision) {
      this.revision = revision;
   }

   public void setAuthor(String author) {
      this.author = author;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public void addChangePath(ChangePath changePath) {
      if (!this.actions.contains(changePath.getAction())) {
         this.actions.add(changePath.getAction());
      }
      this.changePaths.add(changePath);
   }

   public Date getDate() {
      return date;
   }

   public void setDate(Date date) {
      this.date = date;
   }
   
   public String getAge() {
      return DateFormatter.format(date);
   }
}
