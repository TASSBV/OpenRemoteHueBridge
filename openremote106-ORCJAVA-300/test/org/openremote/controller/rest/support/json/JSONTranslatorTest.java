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
package org.openremote.controller.rest.support.json;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.Constants;
import org.openremote.controller.suite.AllTests;
import org.openremote.controller.suite.RESTTests;
import org.openremote.controller.utils.SecurityUtil;

import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * This is responsible for testing if the requested data is valid JSONP-format data.
 * 
 * @author handy.wang 2010-06-29
 * @author Tomsky
 */
public class JSONTranslatorTest extends TestCase
{

  // Constants ------------------------------------------------------------------------------------
  /**
   * Path to JSON related fixture files.
   */
  private static final String JSON_FIXTURES = "rest/support/json/";


  // Test Lifecycle -------------------------------------------------------------------------------
  
  @Before public void setUp()
  {
    AllTests.replacePanelXML(JSON_FIXTURES + Constants.PANEL_XML);
  }

  private String getJSONFixtureFile(String name)
  {
    return AllTests.getFixtureFile(JSON_FIXTURES + name);
  }

  @After public void tearDown()
  {
    RESTTests.deleteControllerPanelXML();
  }
//
//  @Test public void testTranslate() throws IOException
//  {
//
//     String expectedXMLFilePath = getJSONFixtureFile("expected.xml");
//
//     File expectedXMLFile = new File(expectedXMLFilePath);
//     String expectedXML = FileUtils.readFileToString(expectedXMLFile);
//     String expectedJSONStr = JSONTranslator.translateXMLToJSON(Constants.MIME_APPLICATION_JSON, null, expectedXML);
//
//     String actualXMLFilePath = getJSONFixtureFile("actual.xml");
//
//     File actualXMLFile = new File(actualXMLFilePath);
//     String actualXML = FileUtils.readFileToString(actualXMLFile);
//     String actualJSONStr = JSONTranslator.translateXMLToJSON(Constants.MIME_APPLICATION_JSON, null, actualXML);
//
//     Assert.assertEquals(expectedJSONStr, actualJSONStr);
//  }
//
//  @Test public void testGetPanelsJSONData() throws Exception
//  {
//    doTest("/controller/rest/panels?" + Constants.CALLBACK_PARAM_NAME + "=fun", "/controller/rest/panels");
//  }
//
//
//  @Test public void testGetProfileJSONData() throws Exception
//  {
//    doTest("/controller/rest/panel/father?" + Constants.CALLBACK_PARAM_NAME + "=fun", "/controller/rest/panel/father");
//  }


  // Helpers --------------------------------------------------------------------------------------


  private void doTest(String actualJSONDataURL, String expectedXMLDataURL) throws Exception
  {
    
    WebConversation wc = new WebConversation();

    WebRequest jsonDataRequest = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + AllTests.WEBAPP_PORT
         + actualJSONDataURL);
    WebResponse jsonDataResponse = wc.getResponse(jsonDataRequest);
    String actual = jsonDataResponse.getText();

    WebRequest xmlDataRequest = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + AllTests.WEBAPP_PORT
         + expectedXMLDataURL);
    WebResponse xmlDataResponse = wc.getResponse(xmlDataRequest);

    String xml = xmlDataResponse.getText();
    String expected = JSONTranslator.translateXMLToJSON("application/json", null, xml);
    expected = "fun" + " && " + "fun" + "(" + expected + ")";

    Assert.assertEquals(expected, actual);
  }

}
