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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DifferenceModel parse the difference between two version files
 * @author Tomsky
 * 
 */
public class DifferenceModel {

   protected List<DifferenceArea> areas = new ArrayList<DifferenceArea>();
   protected int addedItemsCount = 0;
   protected int modifiedItemsCount = 0;
   protected int deletedItemsCount = 0;

   protected class DifferenceArea {
      protected int leftIndex;
      protected int leftSize;
      protected int rightIndex;
      protected int rightSize;
      protected List<String> data = new ArrayList<String>();

      public DifferenceArea(int leftIndex, int leftSize, int rightIndex, int rightSize) {
         this.leftIndex = leftIndex;
         this.leftSize = leftSize;
         this.rightIndex = rightIndex;
         this.rightSize = rightSize;
      }

      public int getLeftIndex() {
         return this.leftIndex;
      }

      public int getLeftSize() {
         return this.leftSize;
      }

      public int getRightIndex() {
         return this.rightIndex;
      }

      public int getRightSize() {
         return this.rightSize;
      }

      public void addElement(String element) {
         this.data.add(element);
      }

      public List<DifferenceLine> getLeftElements() {
         List<DifferenceLine> ret = new ArrayList<DifferenceLine>();
         int number = this.leftIndex;
         String previousOperation = " ";
         for (Iterator<String> i = this.data.iterator(); i.hasNext();) {
            String line = (String) i.next();
            String operation = line.substring(0, 1);
            line = line.substring(1);
            if (" ".equals(operation)) {
               ret.add(new DifferenceLine(number++, DifferenceLine.NOT_CHANGED, line));
               previousOperation = " ";
            } else if ("-".equals(operation)) {
               ret.add(new DifferenceLine(number++, DifferenceLine.DELETED, line));
               previousOperation = "-";
               deletedItemsCount++;
            } else if ("+".equals(operation)) {
               if ("-".equals(previousOperation)) {
                  DifferenceLine l = (DifferenceLine) ret.get(ret.size() - 1);
                  l.setType(DifferenceLine.MODIFIED);
                  modifiedItemsCount++;
                  deletedItemsCount--;
               } else {
                  ret.add(new DifferenceLine(DifferenceLine.EMPTY_NUMBER, DifferenceLine.NOT_CHANGED, ""));
               }
               previousOperation = "+";
            }
         }
         return ret;
      }

      public List<DifferenceLine> getRightElements() {
         List<DifferenceLine> ret = new ArrayList<DifferenceLine>();
         int number = this.rightIndex;
         String previousOperation = " ";
         for (Iterator<String> i = this.data.iterator(); i.hasNext();) {
            String line = (String) i.next();
            String operation = line.substring(0, 1);
            line = line.substring(1);
            if (" ".equals(operation)) {
               ret.add(new DifferenceLine(number++, DifferenceLine.NOT_CHANGED, line));
               previousOperation = " ";
            } else if ("-".equals(operation)) {
               ret.add(new DifferenceLine(DifferenceLine.EMPTY_NUMBER, DifferenceLine.NOT_CHANGED, ""));
               previousOperation = "-";
            } else if ("+".equals(operation)) {
               if ("-".equals(previousOperation)) {
                  DifferenceLine l = (DifferenceLine) ret.get(ret.size() - 1);
                  l.setLine(line);
                  l.setType(DifferenceLine.MODIFIED);
                  l.setNumber(number++);
               } else {
                  ret.add(new DifferenceLine(number++, DifferenceLine.ADDED, line));
                  addedItemsCount++;
               }
               previousOperation = "+";
            }
         }
         return ret;
      }
   }

   public DifferenceModel(String difference) {
      Pattern header = Pattern.compile("@@ -(\\d+)(,\\d+)? \\+(\\d+)(,\\d+)? @@");
      String[] lines = difference.split("\\r\\n|\\r|\\n");
      DifferenceArea area = null;
      for (int i = 0; i < lines.length; i++) {

         Matcher matcher = header.matcher(lines[i]);
         if (matcher.matches()) {
            String leftIndex = this.checkGroup(matcher.group(1));
            String leftSize = this.checkGroup(matcher.group(2));
            String rightIndex = this.checkGroup(matcher.group(3));
            String rightSize = this.checkGroup(matcher.group(4));

            area = new DifferenceArea(Integer.parseInt(leftIndex) - 1, Integer.parseInt(leftSize), Integer
                  .parseInt(rightIndex) - 1, Integer.parseInt(rightSize));
            this.areas.add(area);
         } else {
            if (area != null) {
               area.addElement(lines[i]);
            }
         }
      }
   }

   protected String checkGroup(String num) {
      String res = null;
      if (num == null) {
         res = "1";
      } else {
         int index = num.indexOf(",");
         if (index != -1) {
            res = num.substring(1);
         } else {
            res = num;
         }
      }

      return res;
   }

   public List<DifferenceLine> getLeftLines(String left) {
      List<DifferenceLine> ret = new ArrayList<DifferenceLine>();
      String[] lines = left.split("\\r\\n|\\r|\\n");
      int index = 0;

      /**
       * As contexts from difference areas may overlay, we need to add check not to add the same lines twice, e.g.:
       * 
       * @@ -10,6 +10,7 @@ ILocation loc = (ILocation) iter.next(); resourceCreated(loc); } + return; } if
       *    (!isOurLocation(newLoc)) { // only oldLoc is ours
       * @@ -14,6 +15,7 @@ if (!isOurLocation(newLoc)) { // only oldLoc is ours resourceRemoved(oldLoc); + return; }
       *    Collection filesMoved = getFileSubLocations(newLoc); Iterator iter = filesMoved.iterator();
       * 
       *    Here following lines are encountered in both contexts: if (!isOurLocation(newLoc)) { // only oldLoc is ours
       */
      Set<Integer> diffAreaContextLineNumbers = new HashSet<Integer>();

      for (Iterator<DifferenceArea> i = this.areas.iterator(); i.hasNext();) {
         DifferenceArea area = (DifferenceArea) i.next();

         // add lines and mark them as unchanged, which are encountered before current diff area or
         // between current diff area and previous diff area
         for (int j = index; j < area.getLeftIndex(); j++) {
            ret.add(new DifferenceLine(j, DifferenceLine.NOT_CHANGED, lines[j]));
         }

         // process lines from diff area
         List<DifferenceLine> leftElements = area.getLeftElements();
         Iterator<DifferenceLine> iter = leftElements.iterator();
         while (iter.hasNext()) {
            DifferenceLine diffLine = (DifferenceLine) iter.next();
            if (diffLine.getNumber() == DifferenceLine.EMPTY_NUMBER
                  || (diffLine.getNumber() != DifferenceLine.EMPTY_NUMBER && diffAreaContextLineNumbers
                        .add(new Integer(diffLine.getNumber())))) {
               ret.add(diffLine);
            }
         }

         index = area.getLeftIndex() + area.getLeftSize();
      }

      // add lines and mark them as unchanged, which are encountered after last diff area
      if (index >= 0) {
         for (int i = index; i < lines.length; i++) {
            ret.add(new DifferenceLine(i, DifferenceLine.NOT_CHANGED, lines[i]));
         }
      }
      return ret;
   }

   public List<DifferenceLine> getRightLines(String right) {
      List<DifferenceLine> ret = new ArrayList<DifferenceLine>();
      String[] lines = right.split("\\r\\n|\\r|\\n");
      int index = 0;

      /**
       * As contexts from difference areas may overlay, we need to add check not to add the same lines twice
       */
      Set<Integer> diffAreaContextLineNumbers = new HashSet<Integer>();

      for (Iterator<DifferenceArea> i = this.areas.iterator(); i.hasNext();) {
         DifferenceArea area = (DifferenceArea) i.next();

         // add lines and mark them as unchanged, which are encountered before current diff area or
         // between current diff area and previous diff area
         for (int j = index; j < area.getRightIndex(); j++) {
            ret.add(new DifferenceLine(j, DifferenceLine.NOT_CHANGED, lines[j]));
         }

         // process lines from diff area
         List<DifferenceLine> rightElements = area.getRightElements();
         Iterator<DifferenceLine> iter = rightElements.iterator();
         while (iter.hasNext()) {
            DifferenceLine diffLine = (DifferenceLine) iter.next();
            if (diffLine.getNumber() == DifferenceLine.EMPTY_NUMBER
                  || (diffLine.getNumber() != DifferenceLine.EMPTY_NUMBER && diffAreaContextLineNumbers
                        .add(new Integer(diffLine.getNumber())))) {
               ret.add(diffLine);
            }
         }

         index = area.getRightIndex() + area.getRightSize();
      }

      // add lines and mark them as unchanged, which are encountered after last diff area
      if (index >= 0) {
         for (int i = index; i < lines.length; i++) {
            ret.add(new DifferenceLine(i, DifferenceLine.NOT_CHANGED, lines[i]));
         }
      }
      return ret;
   }

   public static List<DifferenceLine> getUntouchedLines(String content) {
      List<DifferenceLine> ret = new ArrayList<DifferenceLine>();
      String[] lines = content.split("\\r\\n|\\r|\\n");
      for (int i = 0; i < lines.length; i++) {
         ret.add(new DifferenceLine(i, DifferenceLine.NOT_CHANGED, lines[i]));
      }
      return ret;
   }

   public int getAddedItemsCount() {
      return addedItemsCount;
   }

   public int getModifiedItemsCount() {
      return modifiedItemsCount;
   }

   public int getDeletedItemsCount() {
      return deletedItemsCount;
   }
}
