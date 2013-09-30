/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2011, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * The Class ButtonCommandServiceTest.
 * 
 * @author Handy.Wang 2009-10-26
 */
public class PollingServiceTest {

   /** The status command service. */
   // private KNXCommandBuilder knxCommandBuilder = (KNXCommandBuilder)
   // SpringContextForTest.getInstance().getBean("knxCommandBuilder");;
//   private StatusCommandService statusCommandService = (StatusCommandService) SpringTestContext.getInstance().getBean(
//         "statusCommandService");

   @Test
   public void testRegex() {
      String urlControl = "http://localhost:8080/controller/rest/control/1001/next";
      String urlStatus = "http://localhost:8080/controller/rest/status/1001,1002,1003";
      String urlPolling = "http://localhost:8080/controller/rest/polling/96e79218965eb72c92a549dd5a330112/1001,1002,1003";

      String regexpControl = "rest\\/control\\/(\\d+)\\/(\\w+)";
      String regexpStatus = "rest\\/status\\/(.*)";
      String regexpPolling = "rest\\/polling\\/(.*?)\\/(.*)";

      Pattern patternControl = Pattern.compile(regexpControl);
      Pattern patternnStatus = Pattern.compile(regexpStatus);
      Pattern patternnPolling = Pattern.compile(regexpPolling);

      String controlID = null;
      String commandTypeStr = null;

      Matcher matcherControl = patternControl.matcher(urlControl);
      Matcher matcherStatus = patternnStatus.matcher(urlStatus);
      Matcher matcherPolling = patternnPolling.matcher(urlPolling);

      if (matcherControl.find()) {
         controlID = matcherControl.group(1);
         commandTypeStr = matcherControl.group(2);
         System.out.println(controlID + ", " + commandTypeStr);
      }

      String buttonIDs = null;
      if (matcherStatus.find()) {
         buttonIDs = matcherStatus.group(1);
         System.out.println(buttonIDs);
      }

      String deviceID = "";
      String pollingControlIDs = "";
      if (matcherPolling.find()) {
         deviceID = matcherPolling.group(1);
         pollingControlIDs = matcherPolling.group(2);
         System.out.println(deviceID);
         System.out.println(pollingControlIDs);
      }

   }

   @Test
   public void testIsNumberCommand() {
      String number = "121";
      String regexpNumber = "^\\d+$";
      Pattern patternNumber = Pattern.compile(regexpNumber);
      Matcher matcherNumber = patternNumber.matcher(number);

      if (matcherNumber.find()) {
         String rst = matcherNumber.group(0);
         System.out.println(rst);
      } else {
         System.out.println("It's not......");
      }
   }

}
