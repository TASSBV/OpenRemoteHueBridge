/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.service;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.suite.AllTests;
import org.openremote.controller.utils.SpringTestContext;

// TODO : the tests that don't make assertions are useless


public class ProfileServiceTest {

   private ProfileService service;
   private String xmlPath = null;
   private String panelNameXmlPath = null;

   @Before
   public void setUp() throws Exception {
      service = (ProfileService) SpringTestContext.getInstance().getBean("profileService");
      xmlPath = this.getClass().getClassLoader().getResource(AllTests.FIXTURE_DIR + "panel.xml").getFile();
      panelNameXmlPath = this.getClass().getClassLoader().getResource(AllTests.FIXTURE_DIR + "panelName.xml").getFile();
   }

   public String generateXMLByPanelID(String panelID) {
      return service.getProfileByPanelID(xmlPath, panelID);
   }

   public String generateXMLByPanelName(String panelName) {
      return service.getProfileByName(xmlPath, panelName);
   }
   public String generateXMLByPanelName2(String panelName) {
      return service.getProfileByName(panelNameXmlPath, panelName);
   }

   @Test
   public void testGenerateXMLToShowAllPanels() {
      System.out.println(service.getAllPanels(xmlPath));
   }

   /* =========================ID:=================== */
   @Test
   public void testMyIphone() {
      String result = generateXMLByPanelID("MyIphone");
      System.out.println(result);
   }

   @Test
   public void testMyAndroid() {
      String result = generateXMLByPanelID("MyAndroid");
      System.out.println(result);
   }

   @Test
   public void testID1() {
      String result = generateXMLByPanelID("2fd894042c668b90aadf0698d353e579");
      System.out.println(result);
   }

   /* =========================ID:=================== */

   /* =************************Name:******************= */
   @Test
   public void testIDVsName1() {
      String nameResult = generateXMLByPanelName("father");
      String idResult = generateXMLByPanelID("MyIphone");

      Assert.assertEquals(nameResult, idResult);
      // System.out.println(result);
   }

   @Test
   public void testIDVsName2() {
      String nameResult = generateXMLByPanelName("mother");
      String idResult = generateXMLByPanelID("MyAndroid");
      Assert.assertEquals(nameResult, idResult);
      // System.out.println(result);
   }

   @Test
   public void testIDVsName3() {
      String nameResult = generateXMLByPanelName("me");
      String idResult = generateXMLByPanelID("2fd894042c668b90aadf0698d353e579");

      Assert.assertEquals(nameResult, idResult);
   }

   /* =************************Name:******************= */
   @Test
   public void testNoPanel() {
      String result = null;
      try {
         result = generateXMLByPanelName("meHaha");
      } catch (Exception e) {
         Assert.assertNotNull(e);
      }

      Assert.assertNull(result);
   }
   //'my panel'
   @Test 
   public void getGetPanelByName() {
      String result = generateXMLByPanelName2("\'my panel\'");
      System.out.println(result);
   }
   //"test"
   @Test 
   public void getGetPanelByName2() {
      String result = generateXMLByPanelName2("\"test\"");
      System.out.println(result);
   }
   //'his panel'
   @Test 
   public void getGetPanelByName3() {
      String result = generateXMLByPanelName2("'his panel'");
      System.out.println(result);
   }
   //'test
   @Test 
   public void getGetPanelByName4() {
      String result = generateXMLByPanelName2("'test");
      System.out.println(result);
   }
   //"'test test"
//   @Test 
   public void getGetPanelByName5() {
      String result = generateXMLByPanelName2("\"\'test test\"");
      System.out.println(result);
   }
}
