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

import java.util.Date;

import junit.framework.TestCase;

/**
 * The Class DateUtilTest.
 * 
 * @author Tomsky
 */
public class DateUtilTest extends TestCase {
   
   public void testGetTimeFormat() {
      String currentTime = DateUtil.getTimeFormat(new Date(), "yyyy-MM-dd.HH-mm");
      System.out.println(currentTime);
      String[] time = currentTime.split("\\.");
      System.out.println(time.length);
      System.out.println(time[0] + "   " + time[1]);
   }
}
