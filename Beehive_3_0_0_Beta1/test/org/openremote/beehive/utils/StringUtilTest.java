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

import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
public class StringUtilTest extends TestCase {

   public void testParseStringIds() {
      String idsStr1 = "1,2,3,4";
      ArrayList<Long> result1 = new ArrayList<Long>();
      result1.add(1L);
      result1.add(2L);
      result1.add(3L);
      result1.add(4L);
      assertTrue(StringUtil.parseStringIds(idsStr1,",").equals(result1));

      String idsStr2 = "1,2,3,d";
      ArrayList<Long> result2 = new ArrayList<Long>();
      result2.add(1L);
      result2.add(2L);
      result2.add(3L);
      assertTrue(StringUtil.parseStringIds(idsStr2,",").equals(result2));

   }

   
}
