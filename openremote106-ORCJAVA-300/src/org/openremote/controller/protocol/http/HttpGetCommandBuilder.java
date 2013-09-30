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
package org.openremote.controller.protocol.http;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.utils.CommandUtil;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.utils.Strings;

/**
 * Builds HTTP GET command from XML element. example:
 * 
 * <pre>
 * {@code 
 * <command id="xxx" protocol="httpGet">
 *      <property name="url" value="http://127.0.0.1:8080/xxx/light1_on" />
 * </command>
 * }
 * </pre>
 * 
 * @author Marcus 2009-4-26
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class HttpGetCommandBuilder implements CommandBuilder
{

  // Constants
  // ------------------------------------------------------------------------------------

  /**
   * Common log category name for all HTTP protocol related logging.
   */
  public final static String HTTP_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "http";

  private final static String STR_ATTRIBUTE_NAME_URL = "url";
  private final static String STR_ATTRIBUTE_NAME_METHOD = "method";
  private final static String STR_ATTRIBUTE_NAME_WORKLOAD = "workload";
  private final static String STR_ATTRIBUTE_NAME_CONTENT_TYPE = "contentType";
  private final static String STR_ATTRIBUTE_NAME_USERNAME = "username";
  private final static String STR_ATTRIBUTE_NAME_PASSWORD = "password";
  private final static String STR_ATTRIBUTE_NAME_XPATH = "xpath";
  private final static String STR_ATTRIBUTE_NAME_JSONPATH = "jsonpath";
  private final static String STR_ATTRIBUTE_NAME_REGEX = "regex";
  private final static String STR_ATTRIBUTE_NAME_POLLINGINTERVAL = "pollingInterval";
  // Class Members
  // --------------------------------------------------------------------------------

  /**
   * Logger for this HTTP protocol implementation.
   */
  private final static Logger logger = Logger.getLogger(HTTP_PROTOCOL_LOG_CATEGORY);

  // Implements CommandBuilder
  // --------------------------------------------------------------------

  @SuppressWarnings("unchecked")
  public Command build(Element element)
  {
    logger.debug("Building HttGetCommand");
    List<Element> propertyEles = element.getChildren("property", element.getNamespace());
    int parserCount = 0;
    String urlAsString = null;
    String method = null;
    String workload = null;
    String contentType = null;
    String username = null;
    String password = null;
    String xpath = null;
    String regex = null;
    String jsonpath = null;
    String interval = null;
    Integer intervalInMillis = null;

    // read values from config xml

    for (Element ele : propertyEles)
    {
        System.out.println("create html command");
      String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
      String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

      if (STR_ATTRIBUTE_NAME_URL.equals(elementName))
      {
        urlAsString = CommandUtil.parseStringWithParam(element, elementValue);
        logger.debug("HttpGetCommand: url = " + urlAsString);
      } else if (STR_ATTRIBUTE_NAME_METHOD.equals(elementName))
      {
        method = elementValue;
        logger.debug("HttpGetCommand: method = " + method);
      } else if (STR_ATTRIBUTE_NAME_WORKLOAD.equals(elementName))
      {
        workload = CommandUtil.parseStringWithParam(element, elementValue);
        logger.debug("HttpGetCommand: workload = " + workload);
      } else if (STR_ATTRIBUTE_NAME_CONTENT_TYPE.equals(elementName))
      {
         contentType = CommandUtil.parseStringWithParam(element, elementValue);
        logger.debug("HttpGetCommand: contentTyoe = " + contentType);
      } else if (STR_ATTRIBUTE_NAME_USERNAME.equals(elementName))
      {
        username = elementValue;
        logger.debug("HttpGetCommand: username = " + username);
      } else if (STR_ATTRIBUTE_NAME_PASSWORD.equals(elementName))
      {
        /**************************************************************************************
         * TODO:
         * 
         * THE USE OF PASSWORDS IN CONFIGURATION IS INHERENTLY UNSAFE AND SHOULD BE AVOIDED
         * 
         * We need a better mechanism to handle sensitive configuration data.
         * 
         **************************************************************************************/
        password = elementValue;
        logger.debug("HttpGetCommand: password = " + password);
      } else if (STR_ATTRIBUTE_NAME_POLLINGINTERVAL.equals(elementName))
      {
        interval = elementValue;
        logger.debug("HttpGetCommand: pollingInterval = " + interval);
      } else if (STR_ATTRIBUTE_NAME_REGEX.equals(elementName))
      {
        regex = elementValue;
        parserCount++;
        logger.debug("HttpGetCommand: regex = " + regex);
      } else if (STR_ATTRIBUTE_NAME_XPATH.equals(elementName))
      {
        xpath = elementValue;
        parserCount++;
        logger.debug("HttpGetCommand: xpath = " + xpath);
      } else if (STR_ATTRIBUTE_NAME_JSONPATH.equals(elementName))
      {
        jsonpath = elementValue;
        parserCount++;
        logger.debug("HttpGetCommand: jsonpath = " + jsonpath);
      }
    }

    if (parserCount > 1)
    {
      throw new NoSuchCommandException("Unable to create HttpGet command, only one of either Regex, Xpath or JSONpath allowed!");
    }
    if ((method == null) || (method.trim().length() == 0)) {
       method = "GET";
    }
    try
    {
      if (null != interval) {
        intervalInMillis = Integer.valueOf(Strings.convertPollingIntervalString(interval));
      }
    } catch (Exception e1)
    {
      throw new NoSuchCommandException("Unable to create HttpGet command, pollingInterval could not be converted into milliseconds");
    }

    URL url;
    URI uri;
    try
    {
      url = new URL(urlAsString);
      uri = new URI(url.getProtocol(), null, url.getHost(), url.getPort(), url.getPath(), url.getQuery(), null);
    } catch (MalformedURLException e)
    {
      throw new NoSuchCommandException("Invalid URL: " + e.getMessage(), e);
    } catch (URISyntaxException e) {
       throw new NoSuchCommandException("Invalid URI: " + e.getMessage(), e);
    }

    if (null != username && null != password)
    {
      return new HttpGetCommand(uri, username, password.getBytes(), xpath, regex, intervalInMillis, method, workload, jsonpath, contentType);
    } else
    {
      return new HttpGetCommand(uri, xpath, regex, intervalInMillis, method, workload, jsonpath, contentType);
    }
  }

}
