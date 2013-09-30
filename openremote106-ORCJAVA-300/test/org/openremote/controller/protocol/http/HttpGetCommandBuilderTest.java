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
package org.openremote.controller.protocol.http;


import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.jdom.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.component.LevelSensor;
import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.StateSensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.ReadCommand;
import org.openremote.controller.statuscache.ChangedStatusTable;
import org.openremote.controller.statuscache.EventProcessorChain;
import org.openremote.controller.statuscache.StatusCache;


/**
 * Tests the implementation of {@link org.openremote.controller.protocol.http.HttpGetCommand} class.
 *
 * TODO:
 *   http authentication tests
 *
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Javen
 * @author Dan Cong
 *
 */
public class HttpGetCommandBuilderTest
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * Port we are using for the HTTP server during tests.
   */
  private final static int HTTP_SERVER_PORT = 9999;

  /**
   * Localhost IP address.
   */
  private final static String LOCALHOST = "127.0.0.1";

  /**
   * URL to access the local HTTP server for these tests.
   */
  private final static String HTTP_SERVER_URL = "http://" + LOCALHOST + ":" + HTTP_SERVER_PORT;



  // Instance Fields ------------------------------------------------------------------------------


  /**
   * Reference to the builder implementation for HTTP commands.
   */
  private HttpGetCommandBuilder builder = new HttpGetCommandBuilder();

  /**
   * HTTP server that can be used to provide responses to HTTP commands.
   */
  private Server httpServer;

  /* share the same cache across all sensor tests */
  private StatusCache cache = null;

  // Test Setup and Tear Down ---------------------------------------------------------------------


  /**
   * Start the HTTP server for each test.
   *
   * @throws Exception  if HTTP server start failed.
   */
  @Before public void setUp() throws Exception
  {
    httpServer = new Server(HTTP_SERVER_PORT);
    httpServer.setHandler(new HttpServerResponse());
    httpServer.start();
    
    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain echain = new EventProcessorChain();

    cache = new StatusCache(cst, echain);
  }

  /**
   * Stop the HTTP server after each test (regardless of success or failure)
   *
   * @throws Exception    if server stop failed
   */
  @After public void tearDown() throws Exception
  {
    httpServer.stop();
  }




  // Constructor Tests ----------------------------------------------------------------------------


  /**
   * Tests basic constructor access and property setting is done correctly.
 * @throws MalformedURLException 
   */
  @Test public void testBasicConstruction() throws MalformedURLException
  {
    final String URL = "http://www.openremote.org";

    Command cmd = getHttpCommand(URL);

    Assert.assertTrue(cmd instanceof HttpGetCommand);

    HttpGetCommand httpCommand = (HttpGetCommand)cmd;

    Assert.assertTrue(URL.equals(httpCommand.getUri().toURL().toExternalForm()));
  }

  
  /**
   * Tests basic constructor access and property setting is done correctly when URL contains parameter with spaces
 * @throws MalformedURLException 
   */
  @Test public void testBasicConstructionWithParameter() throws MalformedURLException
  {
    final String URL = "http://www.openremote.org?cmd=turn on";

    Command cmd = getHttpCommand(URL);

    Assert.assertTrue(cmd instanceof HttpGetCommand);

    HttpGetCommand httpCommand = (HttpGetCommand)cmd;

    Assert.assertTrue("http://www.openremote.org?cmd=turn%20on".equals(httpCommand.getUri().toURL().toExternalForm()));
  }


  // Variable ${param} Tests ----------------------------------------------------------------------

  /**
   * Tests for parameter replacement -- if ${param} is used somewhere within the configured URL,
   * it should be replaced with a value given in the command's XML element attribute (denoted
   * with {@link Command#DYNAMIC_VALUE_ATTR_NAME} attribute) in the HTTP command's builder
   * implementation.
 * @throws MalformedURLException 
   */
  @Test public void testParameterPlacement() throws MalformedURLException
  {
    final String parameterizedURL = "http://www.openremote.org/command?param=";

    Command cmd = getParameterizedHttpCommand(parameterizedURL, 100);

    Assert.assertTrue(cmd instanceof HttpGetCommand);

    HttpGetCommand httpCommand = (HttpGetCommand)cmd;

    Assert.assertTrue((parameterizedURL + "100").equals(httpCommand.getUri().toURL().toExternalForm()));
  }

  /**
   * Tests for parameter replacement when multiple ${param} variables are present -- if ${param}
   * is used somewhere within the configured URL, it should be replaced with a value given in the
   * command's XML element attribute (denoted with {@link Command#DYNAMIC_VALUE_ATTR_NAME}
   * attribute) in the HTTP command's builder implementation.
 * @throws MalformedURLException 
   */
  @Test public void testParameterPlacementMultiple() throws MalformedURLException
  {
    final String parameterizedURL = "http://www.openremote.org/command?param=${param}&another=${param}";
    final String finalURL = "http://www.openremote.org/command?param=10000&another=10000";

    Command cmd = getHttpCommand(parameterizedURL, "10000", null);

    Assert.assertTrue(cmd instanceof HttpGetCommand);

    HttpGetCommand httpCommand = (HttpGetCommand)cmd;

    Assert.assertTrue(
        "Expected '" + finalURL + "', got '" + httpCommand.getUri().toURL() + "'.",
        "http://www.openremote.org/command?param=10000&another=10000".equals(httpCommand.getUri().toURL().toExternalForm())
    );
  }


  /**
   * Tests parameter replacement is done correctly regardless where it is used in the configured
   * URL -- if ${param} is used somewhere within the configured URL, it should be replaced with a
   * value given in the command's XML element attribute
   * (denoted with {@link Command#DYNAMIC_VALUE_ATTR_NAME} attribute) in the HTTP command's builder
   * implementation.
 * @throws MalformedURLException 
   */
  @Test public void testParameterPlacementMiddle() throws MalformedURLException
  {
    final String parameterizedURL = "http://www.openremote.org/command?param=${param}&another=foo";
    final String finalURL = "http://www.openremote.org/command?param=XXX&another=foo";

    Command cmd = getHttpCommand(parameterizedURL, "XXX", null);

    Assert.assertTrue(cmd instanceof HttpGetCommand);

    HttpGetCommand httpCommand = (HttpGetCommand)cmd;


    Assert.assertTrue(
        "Expected '" + finalURL + "', got '" + httpCommand.getUri().toURL() + "'.",
        "http://www.openremote.org/command?param=XXX&another=foo".equals(httpCommand.getUri().toURL().toExternalForm())
    );
  }



  // API Execution Tests --------------------------------------------------------------------------

  /**
   * Test send() implementation against an arbitrary URL.
   */
  @Test public void testSendCommand()
  {
    ExecutableCommand command = (ExecutableCommand) getHttpCommand(HTTP_SERVER_URL);

    command.send();
  }

  /**
   * Test send() implementation when an empty URL has been set.
   */
  @Test public void testSendCommandEmptyURL()
  {
    try
    {
      ExecutableCommand command = (ExecutableCommand) getHttpCommand("");

      command.send();

      Assert.fail("should not get here, was expecting a NoSuchCommandException");
    }
    catch (NoSuchCommandException e)
    {
      // expected, do nothing...
    }
  }


  /**
   * Test send() implementation behavior when a malfored URL has been configured.
   */
  @Test public void testSendCommandMalformedURL()
  {
    try
    {
      ExecutableCommand command = (ExecutableCommand) getHttpCommand("foo://bar");

      command.send();

      Assert.fail("should not get here, was expecting a NoSuchCommandException...");
    }

    catch (NoSuchCommandException e)
    {
      // expected, do nothing...
    }
  }


  /**
   * Test send() implementation when URL not found (404) is returned.
   */
  @Test public void testSendCommandNonExistentURL()
  {
    ExecutableCommand command = (ExecutableCommand) getHttpCommand(HTTP_SERVER_URL + "/doesnotexist");

    command.send();
  }


  /**
   * Test send() implementation behavior when HTTP server responds with an error code.
   */
  @Test public void testSendCommandToErrorURL()
  {
    ExecutableCommand command = (ExecutableCommand) getHttpCommand(HTTP_SERVER_URL + "/error/500");

    command.send();
  }



  // Read Sensor Related Tests -------------------------------------------------------------

  /**
   * Test EventListener pollingInterval 
   */
  @Test public void testPollingInterval()
  {
     EventListener cmd = (EventListener) getHttpCommand(HTTP_SERVER_URL + "/response/on", "1m");
     Assert.assertEquals(1*60*1000, ((HttpGetCommand)cmd).getPollingInterval().intValue());
  }

  /**
   * Test read() implementation against 'SWITCH' sensor type.
   */
  @Test public void tesSwitchOnStatus() throws Exception
  {
     EventListener cmd = (EventListener) getHttpCommand(HTTP_SERVER_URL + "/response/on", "1m");
     Sensor s1 = new SwitchSensor("switch", 1, cache, cmd);
     s1.start();
     String returnValue = getSensorValueFromCache(1);
     Assert.assertTrue(returnValue.equals("on"));
  }

  /**
   * Test read() implementation against 'SWITCH' sensor type.
   */
  @Test public void testSwitchOffStatus() throws Exception
  {
     EventListener cmd = (EventListener) getHttpCommand(HTTP_SERVER_URL + "/response/off", "1m");
     Sensor s1 = new SwitchSensor("switch", 2, cache, cmd);
     s1.start();
     String returnValue = getSensorValueFromCache(2);
     Assert.assertTrue(returnValue.equals("off"));
  }

  /**
   * Basic read() test on 'RANGE' type of sensor.
   */
  @Test public void testReadRangeStatus() throws Exception
  {
     EventListener cmd = (EventListener) getHttpCommand(HTTP_SERVER_URL + "/response/50", "1m");
     Sensor s2 = new RangeSensor("range1", 3, cache, cmd, 0, 100);
     s2.start();
     String returnValue = getSensorValueFromCache(3);
     Assert.assertTrue(returnValue.equals("50"));
  }

  /**
   * Basic read() test on 'RANGE' type of sensor.
   */
  @Test public void testReadRangeStatusOutOfLowerBounds() throws Exception
  {
     EventListener cmd = (EventListener) getHttpCommand(HTTP_SERVER_URL + "/response/10", "1m");
     Sensor s2 = new RangeSensor("range2", 4, cache, cmd, 50, 100);
     s2.start();
     String returnValue = getSensorValueFromCache(4);
     Assert.assertTrue(returnValue.equals("50"));
  }

  /**
   * Basic read() test on 'RANGE' type of sensor.
   */
  @Test public void testReadRangeStatusOutOfUpperBounds() throws Exception
  {
     EventListener cmd = (EventListener) getHttpCommand(HTTP_SERVER_URL + "/response/200", "1m");
     Sensor s2 = new RangeSensor("range3", 5, cache, cmd, 50, 100);
     s2.start();
     String returnValue = getSensorValueFromCache(5);
     Assert.assertTrue(returnValue.equals("100"));
  }
  
  /**
   * Test basic 'CUSTOM' sensor type where return values are mapped to specific distinct state
   * values.
   */
  @Test public void testDistinctStateMapping() throws Exception
  {
    StateSensor.DistinctStates states = new StateSensor.DistinctStates();
    states.addStateMapping("this_is_on", "111");
    EventListener cmd = (EventListener) getHttpCommand(HTTP_SERVER_URL + "/response/this_is_on", "1m");
    Sensor s2 = new StateSensor("state1", 6, cache, cmd, states);
    s2.start();
    String returnValue = getSensorValueFromCache(6);
    Assert.assertTrue(returnValue.equals("111"));
  }

  /**
   * Test basic 'CUSTOM' sensor type where return values are mapped to specific distinct state
   * values.
   */
  @Test public void testDistinctStateMappingNoMatch() throws Exception
  {
    StateSensor.DistinctStates states = new StateSensor.DistinctStates();
    states.addStateMapping("this_is_on", "111");
    EventListener cmd = (EventListener) getHttpCommand(HTTP_SERVER_URL + "/response/no_match", "1m");
    Sensor s2 = new StateSensor("state2", 7, cache, cmd, states);
    s2.start();
    String returnValue = getSensorValueFromCache(7);
    Assert.assertTrue(returnValue.equals("N/A"));
  }

  /**
   * Basic read() test on 'LEVEL' type of sensor.
   */
  @Test public void testReadLevelStatus() throws Exception
  {
     EventListener cmd = (EventListener) getHttpCommand(HTTP_SERVER_URL + "/response/20", "1m");
     Sensor s2 = new LevelSensor("level1", 8, cache, cmd);
     s2.start();
     String returnValue = getSensorValueFromCache(8);
     Assert.assertTrue(returnValue.equals("20"));
  }
  
  /**
   * Basic read() test on 'LEVEL' type of sensor.
   */
  @Test public void testReadLevelStatusOutOfLowerBounds() throws Exception
  {
     EventListener cmd = (EventListener) getHttpCommand(HTTP_SERVER_URL + "/response/-10", "1m");
     Sensor s2 = new LevelSensor("level2", 9, cache, cmd);
     s2.start();
     String returnValue = getSensorValueFromCache(9);
     Assert.assertTrue(returnValue.equals("0"));
  }
  
  /**
   * Basic read() test on 'LEVEL' type of sensor.
   */
  @Test public void testReadLevelStatusOutOfUpperBounds() throws Exception
  {
     EventListener cmd = (EventListener) getHttpCommand(HTTP_SERVER_URL + "/response/120", "1m");
     Sensor s2 = new LevelSensor("level3", 10, cache, cmd);
     s2.start();
     String returnValue = getSensorValueFromCache(10);
     Assert.assertTrue(returnValue.equals("100"));
  }
  
  // Helpers --------------------------------------------------------------------------------------


  private Command getParameterizedHttpCommand(String url, int paramValue)
  {
    return getHttpCommand(url + "${param}", "" + paramValue, null);
  }

  private Command getHttpCommand(String url)
  {
    return getHttpCommand(url, null, null);
  }
  
  private Command getHttpCommand(String url, String interval)
  {
    return getHttpCommand(url, null, interval);
  }

  private Command getHttpCommand(String url, String paramValue, String interval)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute("protocol", "httpGet");

    if (paramValue != null)
    {
      ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, paramValue);
    }
    
    Element propName = new Element("property");
    propName.setAttribute("name", "name");
    propName.setAttribute("value", "commandname");

    Element propUrl = new Element("property");
    propUrl.setAttribute("name", "url");
    propUrl.setAttribute("value", url);

    if (interval != null) {
      Element propInterval = new Element("property");
      propInterval.setAttribute("name", "pollingInterval");
      propInterval.setAttribute("value", interval);
      ele.addContent(propInterval);
    }
    
    ele.addContent(propName);
    ele.addContent(propUrl);

    return builder.build(ele);
  }

  private String getSensorValueFromCache(int sensorID) throws Exception
  {
    // sleep here to give the polling mechanism enough time to push the event value to cache...

    Thread.sleep(ReadCommand.POLLING_INTERVAL * 2);
                                                                       return cache.queryStatus(sensorID);
  }

  // Nested Classes -------------------------------------------------------------------------------

  private static class HttpServerResponse extends AbstractHandler
  {

    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch)
         throws IOException, ServletException
    {

      if (target.startsWith("/doesnotexist"))
      {
        response.sendError(404);
      }

      else if (target.startsWith("/error/"))
      {
        target = target.substring(7);

        response.sendError(Integer.parseInt(target));
      }
      
      else
      {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        if (target.startsWith("/response/"))
        {
          target = target.substring(10, target.length());
          response.getWriter().print(target);
          response.getWriter().flush();
        }
      }
      
      ((Request) request).setHandled(true);
    }

  }

}


