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
package org.openremote.controller.suite;

import java.io.InputStream;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.Assert;
import org.openremote.controller.rest.ControlStatusPollingRESTServletTest;
import org.openremote.controller.rest.FindPanelByIDTest;
import org.openremote.controller.rest.SkipStateTrackTest;
import org.openremote.controller.rest.ListPanelIDsTest;
import org.openremote.controller.rest.SensorStatusTest;
import org.openremote.controller.rest.support.json.JSONTranslatorTest;
import org.openremote.controller.statuscache.StatusAndPollingTest;
import org.openremote.controller.Constants;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.utils.PathUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;


@RunWith(Suite.class)
@SuiteClasses(
{
   ControlStatusPollingRESTServletTest.class,
   SkipStateTrackTest.class,
   StatusAndPollingTest.class,
   JSONTranslatorTest.class,

   FindPanelByIDTest.class,
   ListPanelIDsTest.class,
   SensorStatusTest.class
}
)

public class RESTTests
{


  public final static boolean ASSERT_BODY_CONTENT = true;


  // Class Members --------------------------------------------------------------------------------


  public static URL containerURL = null;


  static
  {
    try
    {
      containerURL = new URL(
          "http://" + AllTests.LOCALHOST + ":" +
          AllTests.WEBAPP_PORT + "/controller"
      );
    }
    catch (Throwable t)
    {
      Assert.fail("Can't initialize tests: " + t.getMessage());
    }
  }


  // HTTP Assertions ------------------------------------------------------------------------------


  public static void assertHttpResponse(HttpURLConnection connection, int returnCode,
                                        boolean hasBodyContent, String contentType,
                                        String charset) throws Exception
  {

    // Assert HTTP response code...

    int code = connection.getResponseCode();

    Assert.assertTrue(
        "Expected return code " + returnCode + ", got " + code,
        code == returnCode
    );


    // Assert HTTP response body content size...

    int contentLen = connection.getContentLength();

    if (hasBodyContent)
    {
      if (contentLen != -1)
      {
        Assert.assertTrue(
          "Expected return type document to have content length but got nothing.",
          contentLen > 0
        );
      }
    }

    else
    {
      Assert.assertTrue(
          "Expected no body content but got " + contentLen + " bytes.",
          contentLen == 0
      );
    }


    // Assert HTTP response content MIME type...

    String contentTypeHeader = connection.getContentType().toLowerCase();

    Assert.assertTrue(
        "Expected " + contentType + ", got '" + contentTypeHeader + "'.",
        contentTypeHeader.contains(contentType.toLowerCase())
    );


    // Assert HTTP charset...

    Assert.assertTrue(
        "Expected charset=" + charset + " in content-type header but got " + contentTypeHeader,
        contentTypeHeader.toLowerCase().contains("charset=" + charset.toLowerCase())
    );
  }


  
  // OpenRemote XML Assertions --------------------------------------------------------------------


  public static void assertOpenRemoteRootElement(Document doc)
  {
    NodeList list = doc.getElementsByTagName("openremote");

    Assert.assertTrue(
        "Expected to find exactly one <openremote> element, got " + list.getLength(),
        list.getLength() == 1
    );

    list = doc.getChildNodes();
    int foundRootElement = 0;

    for (int cindex = 0; cindex < list.getLength(); cindex++)
    {
      if (list.item(cindex).getNodeType() == Node.ELEMENT_NODE)
        ++foundRootElement;
    }


    Assert.assertTrue(
        "Expected exactly one child element on document, got " + foundRootElement,
        foundRootElement == 1
    );
  }


  public static void assertErrorDocument(Document doc, int errorCode)
  {
    RESTTests.assertOpenRemoteRootElement(doc);

    NodeList list = doc.getElementsByTagName("openremote");

    Node openremote = list.item(0);

    List<Node> openremoteChildElements = getChildElements(openremote);

    Assert.assertTrue(
        "Expected exactly one <error> node, got " + openremoteChildElements.size(),
        openremoteChildElements.size() == 1
    );


    Node error = openremoteChildElements.get(0);

    Assert.assertTrue(error.getNodeName().equalsIgnoreCase("error"));


    List<Node> errorChildElements = getChildElements(error);


    Assert.assertTrue(
        "Expected exactly one <code> node and one <message> node, got " + errorChildElements.size(),
        errorChildElements.size() == 2
    );


    Assert.assertTrue(errorChildElements.get(0).getNodeName().equalsIgnoreCase("code"));
    Assert.assertTrue(errorChildElements.get(1).getNodeName().equalsIgnoreCase("message"));


    Node code = errorChildElements.get(0);

    Assert.assertTrue(code.getFirstChild().getNodeValue().equalsIgnoreCase("" + errorCode));

    Node message = errorChildElements.get(1);

    Assert.assertFalse(message.getFirstChild().getNodeValue().equals(""));
  }





  // DOM Utility classes --------------------------------------------------------------------------


  /**
   * Returns an ordered list of {@link Node#ELEMENT_NODE} elements of a DOM tree,
   * ignoring other content or nodes. Returns first level of child elements only,
   * does not recurse deeper into the DOM tree.
   *
   * @param   node    root node whose first-level child elements are returned
   *
   * @return  an ordered list of DOM element nodes below the given root node
   */
  public static List<Node> getChildElements(Node node)
  {
    NodeList list = node.getChildNodes();

    List<Node> elements = new ArrayList<Node>(list.getLength());

    for (int index = 0; index < list.getLength(); ++index)
    {
      Node n = list.item(index);

      if (n.getNodeType() == Node.ELEMENT_NODE)
        elements.add(n);
    }

    return elements;
  }



  public static Document getDOMDocument(InputStream in) throws Exception
  {
    return AllTests.getDOMDocument(in);
  }



  // Configuration File Utilities -----------------------------------------------------------------


  public static void deleteControllerPanelXML()
  {
    String containerPanelXml = getContainerPanelXML();

    deleteFile(containerPanelXml);
  }

  private static String getContainerPanelXML()
  {
    return PathUtil.addSlashSuffix(
        ControllerConfiguration.readXML().getResourcePath()) +
        Constants.PANEL_XML;
  }

  private static void deleteFile(String fileName)
  {

    File f = new File(fileName);

    if (!f.exists())
      return;
    
    boolean success = f.delete();

    if (!success)
    {
       throw new IllegalArgumentException("Delete failed.");
    }
  }



}
