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
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.openremote.controller.Constants;
import org.openremote.controller.model.JSONMapping;
import org.openremote.controller.suite.RESTTests;
import org.openremote.controller.suite.AllTests;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Tomsky
 */
public class FindPanelByIDTest
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * Controller 2.0 REST/XML URI for retrieving panel by id from the controller.
   */
  public final static String RESTAPI_PANEL_DEFINITION_URI = "/rest/panel/";

  /**
   * Controller 2.0 REST/XML URI for write commands to the controller.
   */
  public final static String RESTAPI_CONTROL_URI = "/rest/control/";

  /**
   * Controller 2.0 REST/XML URI for read commands.
   */
  public final static String RESTAPI_STATUS_URI = "/rest/status/";




  // Test Lifecycle -------------------------------------------------------------------------------


  @Before public void setup()
  {
    AllTests.initServiceContext();
  }
  
  @After public void tearDown()
  {
    RESTTests.deleteControllerPanelXML();
  }


  // Tests ----------------------------------------------------------------------------------------

  @Test public void requestFatherPanelProfile() throws Exception
  {

    AllTests.replacePanelXML(Constants.PANEL_XML);


    URL panelList = new URL(RESTTests.containerURL + RESTAPI_PANEL_DEFINITION_URI + "father");

    HttpURLConnection connection = (HttpURLConnection)panelList.openConnection();

    RESTTests.assertHttpResponse(
        connection, HttpURLConnection.HTTP_OK, RESTTests.ASSERT_BODY_CONTENT,
        Constants.MIME_APPLICATION_XML, Constants.CHARACTER_ENCODING_UTF8
    );

    Document doc = RESTTests.getDOMDocument(connection.getInputStream());

    RESTTests.assertOpenRemoteRootElement(doc);

    NodeList list = doc.getElementsByTagName("screens");

    Assert.assertTrue("Expected one <screens> element in panel definition.", list.getLength() == 1);

    list = doc.getElementsByTagName("groups");

    Assert.assertTrue("Expected one <groups> element in panel definition.", list.getLength() == 1);

    list = doc.getElementsByTagName("group");

    Assert.assertTrue("Expected at least two groups in the panel definition.", list.getLength() >= 2);

    Set<String> screenReferences = new HashSet<String>(10);

    for (int groupIndex = 0; groupIndex < list.getLength(); ++groupIndex)
    {
      Node group = list.item(groupIndex);

      if (group.getNodeType() != Node.ELEMENT_NODE)
        continue;

      NodeList includes = group.getChildNodes();

      for (int includeIndex = 0; includeIndex < includes.getLength(); ++includeIndex)
      {
        Node include = includes.item(includeIndex);

        if (include.getNodeType() != Node.ELEMENT_NODE)
          continue;

        if (!include.getNodeName().equalsIgnoreCase("include"))
          continue;

        NamedNodeMap attrs = include.getAttributes();

        Assert.assertNotNull("Expected to find attributes in include", attrs);
        Assert.assertNotNull("Expected to find 'ref' attribute in <include>, got null", attrs.getNamedItem("ref"));

        Node ref = attrs.getNamedItem("ref");

        String screenRef = ref.getNodeValue();

        screenReferences.add(screenRef);
      }
    }

    list = doc.getElementsByTagName("screen");

    Set<String> screenIDs = new HashSet<String>(10);

    Assert.assertTrue("Expected four <screen> elements in panel definition.", list.getLength() >= 4);

    for (int screenIndex = 0; screenIndex < list.getLength(); ++screenIndex)
    {
      Node screen = list.item(screenIndex);

      NamedNodeMap attrs = screen.getAttributes();

      Assert.assertNotNull("Expected to find 'id' attribute in screen, not null.", attrs.getNamedItem("id"));

      Node id = attrs.getNamedItem("id");

      screenIDs.add(id.getNodeValue());
    }

    Assert.assertTrue(screenIDs.contains("5"));
    Assert.assertTrue(screenIDs.contains("6"));
    Assert.assertTrue(screenIDs.contains("7"));
    Assert.assertTrue(screenIDs.contains("8"));



    for (String ref : screenReferences)
    {
      Assert.assertTrue("Expected to find screen with id: " + ref, screenIDs.contains(ref));
    }
  }

  @Test public void requestFatherPanelProfileJSON() throws Exception
  {
     AllTests.replacePanelXML(Constants.PANEL_XML);


     URL panelList = new URL(RESTTests.containerURL + RESTAPI_PANEL_DEFINITION_URI + "father");

     HttpURLConnection connection = (HttpURLConnection)panelList.openConnection();
     
     connection.setRequestProperty(Constants.HTTP_ACCEPT_HEADER, Constants.MIME_APPLICATION_JSON);
     
     RESTTests.assertHttpResponse(
         connection, HttpURLConnection.HTTP_OK, RESTTests.ASSERT_BODY_CONTENT,
         Constants.MIME_APPLICATION_JSON, Constants.CHARACTER_ENCODING_UTF8
     );
     
     Object o = JSONMapping.getJSONRoot(connection.getInputStream());

     Assert.assertTrue(o instanceof JSONObject);

     JSONObject root = (JSONObject)o;
     
     // parse screens
     Assert.assertTrue(root.has("screens"));
     JSONObject screensObj = root.getJSONObject("screens");
     
     Assert.assertTrue(screensObj.has("screen"));
     JSONArray screenArray = screensObj.getJSONArray("screen");
     Assert.assertTrue(screenArray.length() == 4);
     
     Set<String> screenIDs = new HashSet<String>(5);
     for (int index = 0; index < screenArray.length(); index++)
     {
        JSONObject screenObj = screenArray.getJSONObject(index);
        Assert.assertTrue(screenObj.has("id"));
        Assert.assertTrue(screenObj.has("name"));
        
        screenIDs.add(screenObj.get("id").toString());
     }
     
     // parse groups
     Assert.assertTrue(root.has("groups"));
     JSONObject groupsObj = root.getJSONObject("groups");
     
     Assert.assertTrue(groupsObj.has("group"));
     JSONArray groupArray = groupsObj.getJSONArray("group");
     Assert.assertTrue(groupArray.length() == 2);
     
     Set<String> screenReferences = new HashSet<String>(5);
     
     for (int index = 0; index < groupArray.length(); index++)
     {
        JSONObject groupObj = groupArray.getJSONObject(index);
        Assert.assertTrue(groupObj.has("id"));
        Assert.assertTrue(groupObj.has("name"));
        
        if (groupObj.has("include")) 
        {
           JSONArray screenRefArray = groupObj.getJSONArray("include");
           for (int includeIndex = 0; includeIndex < screenRefArray.length(); includeIndex++)
           {
              JSONObject screenRefObj = screenRefArray.getJSONObject(includeIndex);
              Assert.assertTrue(screenRefObj.has("ref"));
              Assert.assertTrue(screenRefObj.has("type"));
              screenReferences.add(screenRefObj.get("ref").toString());
           }
        }
     }
     
     Assert.assertTrue(screenIDs.contains("5"));
     Assert.assertTrue(screenIDs.contains("6"));
     Assert.assertTrue(screenIDs.contains("7"));
     Assert.assertTrue(screenIDs.contains("8"));
     
     for (String ref : screenReferences)
     {
        Assert.assertTrue("Expected to find screen with id: " + ref, screenIDs.contains(ref));
     }
  }

  @Test public void testGetNonExistentPanelProfile() throws Exception
  {
    AllTests.replacePanelXML(Constants.PANEL_XML);

    URL doesNotExist = new URL(RESTTests.containerURL + RESTAPI_PANEL_DEFINITION_URI + "doesNotExist");

    HttpURLConnection connection = (HttpURLConnection)doesNotExist.openConnection();

    RESTTests.assertHttpResponse(
        connection, Constants.HTTP_RESPONSE_PANEL_ID_NOT_FOUND, RESTTests.ASSERT_BODY_CONTENT,
        Constants.MIME_APPLICATION_XML, Constants.CHARACTER_ENCODING_UTF8
    );

    Document doc = RESTTests.getDOMDocument(connection.getErrorStream());

    RESTTests.assertErrorDocument(doc, Constants.HTTP_RESPONSE_PANEL_ID_NOT_FOUND);
  }

  @Test public void testGetNonExistentPanelProfileJSON() throws Exception
  {
     AllTests.replacePanelXML(Constants.PANEL_XML);

     URL doesNotExist = new URL(RESTTests.containerURL + RESTAPI_PANEL_DEFINITION_URI + "doesNotExist");

     HttpURLConnection connection = (HttpURLConnection)doesNotExist.openConnection();

     connection.setRequestProperty(Constants.HTTP_ACCEPT_HEADER, Constants.MIME_APPLICATION_JSON);
     
     RESTTests.assertHttpResponse(
         connection, HttpURLConnection.HTTP_OK, RESTTests.ASSERT_BODY_CONTENT,
         Constants.MIME_APPLICATION_JSON, Constants.CHARACTER_ENCODING_UTF8
     );
     
     Object o = JSONMapping.getJSONRoot(connection.getInputStream());

     Assert.assertTrue(o instanceof JSONObject);

     JSONObject root = (JSONObject)o;
     
     Assert.assertTrue(root.has("error"));
     
     JSONObject error = root.getJSONObject("error");
     
     Assert.assertEquals(Constants.HTTP_RESPONSE_PANEL_ID_NOT_FOUND, error.getInt("code"));
     Assert.assertEquals("No such Panel :NAME = doesNotExist", error.getString("message"));
  }

}
