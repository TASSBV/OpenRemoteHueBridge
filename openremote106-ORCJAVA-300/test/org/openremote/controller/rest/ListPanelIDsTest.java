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
package org.openremote.controller.rest;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.openremote.controller.Constants;
import org.openremote.controller.model.JSONMapping;
import org.openremote.controller.model.Panel;
import org.openremote.controller.suite.RESTTests;
import org.openremote.controller.suite.AllTests;
import org.w3c.dom.Document;


/**
 * Tests against {@link org.openremote.controller.rest.ListPanelIDs} servlet (by default
 * corresponding to the '/rest/panels' URI).
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ListPanelIDsTest
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * Controller 2.0 REST/XML URI for listing all available panel ids in the controller
   */
  public final static String RESTAPI_PANELS_URI = "/rest/panels";



  // Test Setup and Teardown ----------------------------------------------------------------------

  @After public void tearDown()
  {

    // Keep things clean between tests -- always delete the panel.xml used and expect each
    // test to deploy its own...

    RESTTests.deleteControllerPanelXML();
  }



  // Tests ----------------------------------------------------------------------------------------

  /**
   * Basic test to retrieve panel elements from a deployed panel.xml configuration.
   *
   * @throws Exception    if an error occurs
   */
  @Test public void testListPanelIds() throws Exception
  {
    // Deploy our panel.xml...

    AllTests.replacePanelXML("panelList1.xml");


    // Retrieve the panels list through REST/XML API...

    URL panelList = new URL(RESTTests.containerURL + RESTAPI_PANELS_URI);

    HttpURLConnection connection = (HttpURLConnection)panelList.openConnection();

    RESTTests.assertHttpResponse(
        connection, HttpURLConnection.HTTP_OK, RESTTests.ASSERT_BODY_CONTENT,
        Constants.MIME_APPLICATION_XML, Constants.CHARACTER_ENCODING_UTF8
    );


    // Parse XML response...

    Document doc = RESTTests.getDOMDocument(connection.getInputStream());


    // Assertions...

    RESTTests.assertOpenRemoteRootElement(doc);

    List<String> panelNames = Panel.getPanelNames(doc);
    List<String> panelIds = Panel.getPanelIds(doc);

    Assert.assertTrue(panelNames.size() == panelIds.size());
    Assert.assertTrue(panelNames.size() == 3);

    Assert.assertNotNull(
        "Expected panel id 'iPhone' but that was not found.",
        panelIds.contains("iPhone")
    );

    Assert.assertNotNull(
        "Expected panel id 'Android' but that was not found.",
        panelIds.contains("Android")
    );

    Assert.assertNotNull(
        "Expected panel id '2fd894042c668b90aadf0698d353e579' but that was not found.",
        panelIds.contains("2fd894042c668b90aadf0698d353e579")
    );


    Assert.assertTrue(
        "Expected panel id=iPhone with name 'easy'.",
        panelNames.contains("easy")
    );

    Assert.assertTrue(
        "Expected panel id=Android with name 'advanced'.",
        panelNames.contains("advanced")
    );

    Assert.assertTrue(
        "Expected panel id=2fd894042c668b90aadf0698d353e579 with name 'me'.",
        panelNames.contains("me")
    );
  }


  @Test public void testListPanelIdsJSON() throws Exception
  {
    // Deploy our panel.xml...

    AllTests.replacePanelXML("panelList1.xml");


    // Retrieve the panels list through REST/XML API...

    URL panelList = new URL(RESTTests.containerURL + RESTAPI_PANELS_URI);

    HttpURLConnection connection = (HttpURLConnection)panelList.openConnection();

    connection.setRequestProperty(Constants.HTTP_ACCEPT_HEADER, Constants.MIME_APPLICATION_JSON);

    RESTTests.assertHttpResponse(
        connection, HttpURLConnection.HTTP_OK, RESTTests.ASSERT_BODY_CONTENT,
        Constants.MIME_APPLICATION_JSON, Constants.CHARACTER_ENCODING_UTF8
    );

    Object o = JSONMapping.getJSONRoot(connection.getInputStream());

    Assert.assertTrue(o instanceof JSONObject);

    JSONObject root = (JSONObject)o;

    Assert.assertTrue(root.has("panel"));

    JSONArray array = root.getJSONArray("panel");

    Assert.assertTrue(array.length() == 3);

    for (int index = 0; index < array.length(); index++)
    {
      JSONObject panel = array.getJSONObject(index);

      Assert.assertTrue(panel.has("id"));
      Assert.assertTrue(panel.has("name"));

      Assert.assertNotNull(panel.getString("id"));
      Assert.assertNotNull(panel.getString("name"));

      Assert.assertFalse(panel.getString("id").equals(""));
      Assert.assertFalse(panel.getString("name").equals(""));
    }
  }


  /**
   * Tests the HTTP response code on request for panel list against controller that has no
   * configuration deployed.
   *
   * @throws Exception  if any error occurs
   */
  @Test public void testNoPanelXMLDeployed() throws Exception
  {
    URL panelList = new URL(RESTTests.containerURL + RESTAPI_PANELS_URI);

    HttpURLConnection connection = (HttpURLConnection)panelList.openConnection();

    RESTTests.assertHttpResponse(
        connection, Constants.HTTP_RESPONSE_PANEL_XML_NOT_DEPLOYED,
        RESTTests.ASSERT_BODY_CONTENT, Constants.MIME_APPLICATION_XML,
        Constants.CHARACTER_ENCODING_UTF8
    );
  }


  /**
   * Validate XML parsing error handling when the XML is not well formatted (incomplete element).
   *
   * @throws Exception  if any error other than the expected ones occur
   */
  @Test public void testBrokenPanelXMLStructureNotWellFormatted() throws Exception
  {
    // Deploy our panel.xml...

    AllTests.replacePanelXML("brokenPanelStructure-NotWellFormatted.xml");


    URL panelList = new URL(RESTTests.containerURL + RESTAPI_PANELS_URI);

    HttpURLConnection connection = (HttpURLConnection)panelList.openConnection();

    RESTTests.assertHttpResponse(
        connection, Constants.HTTP_RESPONSE_INVALID_PANEL_XML,
        RESTTests.ASSERT_BODY_CONTENT, Constants.MIME_APPLICATION_XML,
        Constants.CHARACTER_ENCODING_UTF8
    );
  }



  /**
   * Validate XML parsing error handling when the XML is not well formatted (missing elements).
   *
   * @throws Exception  if any error other than the expected ones occur
   */
  @Test public void testBrokenPanelXMLStructureNotWellFormatted2() throws Exception
  {
    // Deploy our panel.xml...

    AllTests.replacePanelXML("brokenPanelStructure-NotWellFormatted2.xml");


    URL panelList = new URL(RESTTests.containerURL + RESTAPI_PANELS_URI);

    HttpURLConnection connection = (HttpURLConnection)panelList.openConnection();

    RESTTests.assertHttpResponse(
        connection, Constants.HTTP_RESPONSE_INVALID_PANEL_XML,
        RESTTests.ASSERT_BODY_CONTENT, Constants.MIME_APPLICATION_XML,
        Constants.CHARACTER_ENCODING_UTF8
    );
  }

  /**
   * Validate XML parsing error handling when the XML is not well formatted
   * (missing one <panels> tag).
   *
   * @throws Exception  if any error other than the expected ones occur
   */
  @Test public void testBrokenPanelXMLStructureMissingPanelsTag() throws Exception
  {
    // Deploy our panel.xml...

    AllTests.replacePanelXML("brokenPanelStructure-MissingPanelsTag.xml");


    URL panelList = new URL(RESTTests.containerURL + RESTAPI_PANELS_URI);

    HttpURLConnection connection = (HttpURLConnection)panelList.openConnection();

    RESTTests.assertHttpResponse(
        connection, Constants.HTTP_RESPONSE_INVALID_PANEL_XML,
        RESTTests.ASSERT_BODY_CONTENT, Constants.MIME_APPLICATION_XML,
        Constants.CHARACTER_ENCODING_UTF8
    );
  }


  /**
   * Validate XML parsing error handling when the XML is not well formatted
   * (missing ending tag).
   *
   * @throws Exception  if any error other than the expected ones occur
   */
  @Test public void testBrokenPanelXMLStructureMissingEndTag() throws Exception
  {
    // Deploy our panel.xml...

    AllTests.replacePanelXML("brokenPanelStructure-MissingEndTag.xml");


    URL panelList = new URL(RESTTests.containerURL + RESTAPI_PANELS_URI);

    HttpURLConnection connection = (HttpURLConnection)panelList.openConnection();

    RESTTests.assertHttpResponse(
        connection, Constants.HTTP_RESPONSE_INVALID_PANEL_XML,
        RESTTests.ASSERT_BODY_CONTENT, Constants.MIME_APPLICATION_XML,
        Constants.CHARACTER_ENCODING_UTF8
    );
  }


}

