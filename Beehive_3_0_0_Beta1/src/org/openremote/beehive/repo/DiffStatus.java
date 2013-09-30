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
import java.util.List;

/**
 * @author Tomsky
 * 
 */
public class DiffStatus {
   private List<Element> diffStatus = new ArrayList<Element>();

   public void addElement(Element element) {
      this.diffStatus.add(element);
   }

   public List<Element> getDiffStatus() {
      return diffStatus;
   }

   public class Element {
      private String path;
      private Actions status;

      public Element(String path, Actions status) {
         this.path = path;
         this.status = status;
      }

      public String getPath() {
         return path;
      }

      public Actions getStatus() {
         return status;
      }

   }
}
