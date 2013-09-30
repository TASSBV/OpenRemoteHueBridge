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
package org.openremote.controller.protocol.isy99;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.component.EnumSensorType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * OpenRemote protocol implementation for the Universal Devices ISY-99 HTTP-to-Insteon Bridge
 *
 * See http://www.universal-devices.com/mwiki/index.php?title=ISY-99i_Series_INSTEON:REST_Interface
 * for documentation of the REST API for the ISY-99.
 *
 * @author <a href="mailto:andrew.puch.1@gmail.com">Andrew Puch</a>
 * @author <a href="mailto:aball@osintegrators.com">Andrew D. Ball</a>
 */
public class Isy99Command implements ExecutableCommand, StatusCommand
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * Logging. Use common log category for all related classes.
   */
  private final static Logger log = Logger.getLogger(Isy99CommandBuilder.ISY99_LOG_CATEGORY);

  // Constants ------------------------------------------------------------------------------------

  /**
   * Name of the command for both turning on a switch (if no parameter is given)
   * and adjusting the level of a dimmer in the ISY-99 REST interface.
   */
  public final static String ISY99_ON_COMMAND = "DON";

  /**
   * Name of the command for turning off a switch in the ISY-99 REST interface.
   */
  public final static String ISY99_OFF_COMMAND = "DOF";

  /**
   * The maximum level that the ISY-99 will report for dimmers.
   */
  public final static int ISY99_MAX_DIMMER_VALUE = 255;
  
  // Instance Fields ------------------------------------------------------------------------------

  private String host;
  private String username;
  private String password;
  private String address;
  private String command;
  private String commandParam;

  // Constructors ---------------------------------------------------------------------------------

  public Isy99Command(String host, String username, String password, String address, String command)
  {
    this.host = host;
    this.username = username;
    this.password = password;
    this.address = address;
    this.command = command;
  }

  public Isy99Command(String host, String username, String password, String address, String command, String commandParam)
  {
    this(host, username, password, address, command);
    this.commandParam = commandParam;
  }

  // Implements ExecutableCommand -----------------------------------------------------------------

  private String formWriteCommandUrl()
  {
    StringBuilder url = new StringBuilder();
    String effectiveCommand = command;
    String effectiveCommandParam = commandParam;

    // Compensate for the somewhat odd ISY-99 dimming behavior. Using the DON command
    // with 0 as a parameter does not actually cause the device to be turned off. Using
    // the DOF command will, though.
    if (ISY99_ON_COMMAND.equals(command) && commandParam != null)
    {
      if (commandParam.equals("0"))
      {
        effectiveCommand = ISY99_OFF_COMMAND;
        effectiveCommandParam = null;
      }
      else
      {
        effectiveCommandParam = "" + ((Integer.valueOf(commandParam) * ISY99_MAX_DIMMER_VALUE)/100);
      }
    }

    url.append("http://");
    url.append(host);
    url.append("/rest/nodes/");
    url.append(address.replaceAll(" ", "%20"));
    url.append("/cmd/");
    url.append(effectiveCommand);
    if (effectiveCommandParam != null && !effectiveCommandParam.equals(""))
    {
      url.append("/");
      url.append(effectiveCommandParam);
    }

    return url.toString();
  }

  @Override
  public void send()
  {
    String url = formWriteCommandUrl();
    log.debug("send(): URL is " + url);

    DefaultHttpClient client = new DefaultHttpClient();
    
    if (username != null && !username.equals(""))
    {
      CredentialsProvider cred = new BasicCredentialsProvider();
      
      cred.setCredentials(AuthScope.ANY,
          new UsernamePasswordCredentials(username, password));

      client.setCredentialsProvider(cred);
    }

    try
    {
      HttpGet req = new HttpGet(url);
      HttpResponse response = client.execute(req);

      int responseStatusCode = response.getStatusLine().getStatusCode();
      if (responseStatusCode != 200)
      {
        log.error("send(): response status code was " + responseStatusCode);
      }
    }
    catch (IOException e)
    {
      log.error("send(): IOException: address: " + address + "command: " +
          command, e);
    }
  }

  // Implements StatusCommand ---------------------------------------------------------------------

  private String formReadCommandUrl()
  {
    StringBuilder url = new StringBuilder();

    url.append("http://");
    url.append(host);
    url.append("/rest/nodes/");
    url.append(address.replaceAll(" ", "%20"));

    return url.toString();
  }

  @Override
  public String read(EnumSensorType sensorType, Map<String, String> stateMap)
  {
    String url = formReadCommandUrl();
    log.debug("read(): URL is " + url);

    DefaultHttpClient client = new DefaultHttpClient();

    if (username != null && !username.equals(""))
    {
      CredentialsProvider cred = new BasicCredentialsProvider();

      cred.setCredentials(AuthScope.ANY,
          new UsernamePasswordCredentials(username, password));

      client.setCredentialsProvider(cred);
    }

    HttpGet req = new HttpGet(url);

    String value = null;
    InputStream content = null;
    try
    {
      HttpResponse response = client.execute(req);
      HttpEntity entity = response.getEntity();

      if (response.getStatusLine().getStatusCode() != 200)
      {
        log.debug("status line " + response.getStatusLine());
        if (entity != null)
        {
          log.debug("Response content length: " + entity.getContentLength());
        }
      }

      SAXBuilder builder = new SAXBuilder();

      content = response.getEntity().getContent();

      Document document = builder.build(content);
      Element rootNode = document.getRootElement();

      @SuppressWarnings("unchecked")
      List<Element> list = rootNode.getChildren("node");

      for (Element node : list)
      {
        value = node.getChild("property").getAttributeValue("value");
        log.debug("node->property->value = " + value);
      }
    }
    catch (IOException ioe)
    {
      log.error("IOException while reading data from ISY-99", ioe);
    }
    catch (JDOMException jdomex)
    {
      log.error("error while parsing response from ISY-99", jdomex);
    }
    finally
    {
      try
      {
        content.close();
      }
      catch (Exception e) {}
    }
    
    if (value == null || value.equals(""))
    {
      return "";
    }

    int integerValue = -1;
    try
    {
      integerValue = Integer.parseInt(value);
    }
    catch (NumberFormatException e)
    {
      log.error("invalid sensor reading from ISY-99: expected an integer, got \"" + value + "\"");
      return "";
    }

    switch (sensorType)
    {
      case SWITCH:

        if (integerValue >= 1)
        {
          return "on";
        }
        else
        {
          return "off";
        }

      case LEVEL:
        return "" + ((integerValue * 100)/ISY99_MAX_DIMMER_VALUE);

      case RANGE:

        return "" + integerValue;

      default:

        return "";
    }
  }
}

