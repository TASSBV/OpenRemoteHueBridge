package org.openremote.controller.protocol.huebridge;

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
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.protocol.ReadCommand;
import org.openremote.controller.statuscache.ChangedStatusTable;
import org.openremote.controller.statuscache.EventProcessorChain;
import org.openremote.controller.statuscache.StatusCache;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * @author TASS Technology Solutions - www.tass.nl
 */
public class HueBridgeCommandBuilderTest {

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
    private HueBridgeCommandBuilder builder = new HueBridgeCommandBuilder();

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
    @Before
    public void setUp() throws Exception
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
    @After
    public void tearDown() throws Exception
    {
        httpServer.stop();
    }




    // Constructor Tests ----------------------------------------------------------------------------


    /**
     * Tests basic constructor access and property setting is done correctly.
     * @throws MalformedURLException
     */
    @Test public void testBasicBrightnessConstruction() throws MalformedURLException
    {

        String key = "openremote";
        String lightid = "1";
        String putCommand = "brightness";
        String putValue ="200";
        String workload ="{\"bri\":200}";

        String URL = "http://"+LOCALHOST +"/api/"+ key +"/lights/"+lightid+"/state";
        Command cmd = getHueBridgepPutCommand(LOCALHOST,key,lightid,putCommand,putValue);

        Assert.assertTrue(cmd instanceof HueBridgeCommand);

        HueBridgeCommand hueCommand = (HueBridgeCommand)cmd;

        Assert.assertTrue(URL.equals(hueCommand.getUri().toURL().toExternalForm()));

        Assert.assertEquals(workload,hueCommand.createWorkload());
    }

    @Test public void testBasicPowerConstruction() throws MalformedURLException
    {

        String key = "openremote";
        String lightid = "1";
        String putCommand = "power";
        String putValue ="on";
        String workload ="{\"on\":true}";

        String URL = "http://"+ LOCALHOST  +"/api/"+ key +"/lights/"+lightid+"/state";
        Command cmd = getHueBridgepPutCommand(LOCALHOST,key,lightid,putCommand,putValue);

        Assert.assertTrue(cmd instanceof HueBridgeCommand);

        HueBridgeCommand hueCommand = (HueBridgeCommand)cmd;

        Assert.assertTrue(URL.equals(hueCommand.getUri().toURL().toExternalForm()));

        Assert.assertEquals(workload,hueCommand.createWorkload());

    }

    @Test public void testBasicColorConstruction() throws MalformedURLException
    {

        String key = "openremote";
        String lightid = "1";
        String putCommand = "color";
        String putValue ="20000";
        String workload ="{\"hue\":20000}";

        String URL = "http://"+LOCALHOST  +"/api/"+ key +"/lights/"+lightid+"/state";
        Command cmd = getHueBridgepPutCommand(LOCALHOST ,key,lightid,putCommand,putValue);

        Assert.assertTrue(cmd instanceof HueBridgeCommand);

        HueBridgeCommand hueCommand = (HueBridgeCommand)cmd;

        Assert.assertTrue(URL.equals(hueCommand.getUri().toURL().toExternalForm()));

        Assert.assertEquals(workload,hueCommand.createWorkload());
    }

    @Test public void testBasicSaturationConstruction() throws MalformedURLException
    {

        String key = "openremote";
        String lightid = "1";
        String putCommand = "saturation";
        String putValue ="200";
        String workload ="{\"sat\":200}";

        String URL = "http://"+ LOCALHOST  +"/api/"+ key +"/lights/"+lightid+"/state";
        Command cmd = getHueBridgepPutCommand(LOCALHOST ,key,lightid,putCommand,putValue);

        Assert.assertTrue(cmd instanceof HueBridgeCommand);

        HueBridgeCommand hueCommand = (HueBridgeCommand)cmd;

        Assert.assertTrue(URL.equals(hueCommand.getUri().toURL().toExternalForm()));

        Assert.assertEquals(workload,hueCommand.createWorkload());
    }

    @Test public void testBasicAllConstruction() throws MalformedURLException
    {

        String key = "openremote";
        String lightid = "1";

        String workload ="{\"on\":true,\"bri\":200,\"hue\":20000,\"sat\":200}";


        String URL = "http://"+LOCALHOST  +"/api/"+ key +"/lights/"+lightid+"/state";
        Command cmd = getHueBridgeAllCommand(LOCALHOST , key, lightid);

        Assert.assertTrue(cmd instanceof HueBridgeCommand);
        HueBridgeCommand hueCommand = (HueBridgeCommand)cmd;

        Assert.assertTrue(URL.equals(hueCommand.getUri().toURL().toExternalForm()));

        Assert.assertEquals(workload,hueCommand.createWorkload());
    }

    /**
     * Tests basic constructor access and property setting is done correctly.
     * @throws MalformedURLException
     */
    @Test public void testBasicGetConstruction() throws MalformedURLException
    {

        String key = "openremote";
        String lightid = "1";
        String sensorCommand ="brightness";

        String URL = "http://"+LOCALHOST  +"/api/"+ key +"/lights/"+lightid;
        Command cmd = getHueBridgeGetCommand(LOCALHOST ,key,lightid,sensorCommand);

        Assert.assertTrue(cmd instanceof HueBridgeCommand);

        HueBridgeCommand hueCommand = (HueBridgeCommand)cmd;

        Assert.assertTrue(URL.equals(hueCommand.getUri().toURL().toExternalForm()));
    }

    // API Execution Tests --------------------------------------------------------------------------

    /**
     * Test send() implementation against an arbitrary URL.
     */
    @Test public void testSendCommand()
    {
        String key = "openremote";
        String lightid = "1";
        String putCommand = "saturation";
        String putValue ="200";

        ExecutableCommand command = (ExecutableCommand)getHueBridgepPutCommand(LOCALHOST ,key,lightid,putCommand,putValue);

        command.send();
    }

    /**
     * Test send() implementation when an empty URL has been set.
     */
    @Test public void testSendCommandEmptyURL()
    {
        try
        {
            ExecutableCommand command = (ExecutableCommand) getHueBridgeGetCommand("","","","");

            command.send();

            Assert.fail("should not get here, was expecting a NoSuchCommandException");
        }
        catch (NoSuchCommandException e)
        {
            // expected, do nothing...
        }
    }

    /**
     * Test send() implementation behavior when a malfored input has been configured.
     */
    @Test public void testSendCommandMalformedInfo()
    {
        try
        {
            ExecutableCommand command = (ExecutableCommand) getHueBridgeGetCommand("foo://bar","","","");
            command.send();
            Assert.fail("should not get here, was expecting a NoSuchCommandException...");
        }

        catch (NoSuchCommandException e)
        {
            // expected, do nothing...
        }
        try
        {
            ExecutableCommand command = (ExecutableCommand) getHueBridgeGetCommand("","foo://bar","","");
            command.send();
            Assert.fail("should not get here, was expecting a NoSuchCommandException...");
        }

        catch (NoSuchCommandException e)
        {
            // expected, do nothing...
        }
        try
        {
            ExecutableCommand command = (ExecutableCommand) getHueBridgeGetCommand("","","foo://bar","");
            command.send();
            Assert.fail("should not get here, was expecting a NoSuchCommandException...");
        }

        catch (NoSuchCommandException e)
        {
            // expected, do nothing...
        }

        try
        {
            ExecutableCommand command = (ExecutableCommand) getHueBridgeGetCommand("","","","foo://bar");
            command.send();
            Assert.fail("should not get here, was expecting a NoSuchCommandException...");
        }

        catch (NoSuchCommandException e)
        {
            // expected, do nothing...
        }

    }



    // Helpers --------------------------------------------------------------------------------------


//    private Command getHueBridgeCommand(String url, String key, String, int paramValue)
//    {
//        return getHueBridgeCommand(url + "${param}", "" + paramValue, null);
//    }

    private Command getHueBridgeGetCommand(String url, String key, String lightid, String sensorCommand)
    {
        return getHueBridgeCommand(url, key, lightid, null, null, sensorCommand, null, null);
    }

    private Command getHueBridgepPutCommand(String url, String key, String lightid, String putCommand,String putValue)
    {
        return getHueBridgeCommand(url, key, lightid, putCommand, putValue, null, null, null);
    }

    private Command getHueBridgeIntervalCommand(String url, String key, String lightid, String interval)
    {
        return getHueBridgeCommand(url, key, lightid, null, null, null, null, interval);
    }

    private Command getHueBridgeCommand(String bridgeip, String key, String lightid, String putCommand, String putValue, String sensorCommand, String paramValue, String interval)
    {
        Element ele = new Element("command");
        ele.setAttribute("id", "test");
        ele.setAttribute("protocol", "huebridge");

        if (paramValue != null)
        {
            ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, paramValue);
        }

        Element propName = new Element("property");
        propName.setAttribute("name", "name");
        propName.setAttribute("value", "commandname");

        Element propBridgeip = new Element("property");
        propBridgeip.setAttribute("name", "bridgeip");
        propBridgeip.setAttribute("value", bridgeip);

        Element propKey = new Element("property");
        propKey.setAttribute("name", "key");
        propKey.setAttribute("value", key);

        Element proplight = new Element("property");
        proplight.setAttribute("name", "lightid");
        proplight.setAttribute("value", lightid);

        if (putCommand != null) {
            Element propInterval = new Element("property");
            propInterval.setAttribute("name", putCommand);
            propInterval.setAttribute("value",putValue);
            ele.addContent(propInterval);
        }

        if (sensorCommand != null) {
            Element propInterval = new Element("property");
            propInterval.setAttribute("name", "sensor");
            propInterval.setAttribute("value", sensorCommand);
            ele.addContent(propInterval);
        }



        if (interval != null) {
            Element propInterval = new Element("property");
            propInterval.setAttribute("name", "pollingInterval");
            propInterval.setAttribute("value", interval);
            ele.addContent(propInterval);
        }

        ele.addContent(propName);
        ele.addContent(propBridgeip);
        ele.addContent(propKey);
        ele.addContent(proplight);

        return builder.build(ele);
    }

    private Command getHueBridgeAllCommand(String bridgeip, String key, String lightid)
    {
        Element ele = new Element("command");
        ele.setAttribute("id", "test");
        ele.setAttribute("protocol", "huebridge");

        Element propName = new Element("property");
        propName.setAttribute("name", "name");
        propName.setAttribute("value", "commandname");

        Element propBridgeip = new Element("property");
        propBridgeip.setAttribute("name", "bridgeip");
        propBridgeip.setAttribute("value", bridgeip);

        Element propKey = new Element("property");
        propKey.setAttribute("name", "key");
        propKey.setAttribute("value", key);

        Element proplight = new Element("property");
        proplight.setAttribute("name", "lightid");
        proplight.setAttribute("value", lightid);

        Element propInterval = new Element("property");
        propInterval.setAttribute("name", "brightness");
        propInterval.setAttribute("value", "200");
        ele.addContent(propInterval);

        propInterval = new Element("property");
        propInterval.setAttribute("name", "power");
        propInterval.setAttribute("value", "on");
        ele.addContent(propInterval);

        propInterval = new Element("property");
        propInterval.setAttribute("name", "saturation");
        propInterval.setAttribute("value", "200");
        ele.addContent(propInterval);

        propInterval = new Element("property");
        propInterval.setAttribute("name", "color");
        propInterval.setAttribute("value", "20000");
        ele.addContent(propInterval);

        ele.addContent(propBridgeip);
        ele.addContent(propKey);
        ele.addContent(proplight);

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
            String returnJson = "";

            if(target.startsWith("/api/openremote/lights" )){
                response.setContentType("application/json");

                if(target.endsWith("state")){
                    target  = JSON_RETURN_PUTSATTWO;
                }  else{
                    target = JSON_RETURN_GET;
                }
                response.getWriter().print(target);
                response.getWriter().flush();
            }

            if(target.equalsIgnoreCase("/api/openremote" )){
                 response.setContentType("application/json");
                 target = JSON_RETURN_NOSPECIFICATION;
                response.getWriter().print(target);
                response.getWriter().flush();
            }


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
    private final static String JSON_RETURN_PUTSATTWO = "[\n" +
            "\t{\n" +
            "\t\t\"success\": {\n" +
            "\t\t\t\"/lights/2/state/sat\": 2\n" +
            "\t\t}\n" +
            "\t}\n" +
            "]";

    private final static String JSON_RETURN_GET = "{\n" +
            "\t\"state\": {\n" +
            "\t\t\"on\": false,\n" +
            "\t\t\"bri\": 110,\n" +
            "\t\t\"hue\": 15331,\n" +
            "\t\t\"sat\": 121,\n" +
            "\t\t\"xy\": [\n" +
            "\t\t\t0.4448,\n" +
            "\t\t\t0.4066\n" +
            "\t\t],\n" +
            "\t\t\"ct\": 343,\n" +
            "\t\t\"alert\": \"none\",\n" +
            "\t\t\"effect\": \"none\",\n" +
            "\t\t\"colormode\": \"ct\",\n" +
            "\t\t\"reachable\": true\n" +
            "\t},\n" +
            "\t\"type\": \"Extended color light\",\n" +
            "\t\"name\": \"Bol\",\n" +
            "\t\"modelid\": \"LCT001\",\n" +
            "\t\"swversion\": \"65003148\",\n" +
            "\t\"pointsymbol\": {\n" +
            "\t\t\"1\": \"none\",\n" +
            "\t\t\"2\": \"none\",\n" +
            "\t\t\"3\": \"none\",\n" +
            "\t\t\"4\": \"none\",\n" +
            "\t\t\"5\": \"none\",\n" +
            "\t\t\"6\": \"none\",\n" +
            "\t\t\"7\": \"none\",\n" +
            "\t\t\"8\": \"none\"\n" +
            "\t}\n" +
            "}";

    private final static String JSON_RETURN_NOSPECIFICATION = "{\n" +
            "\t\"lights\": {\n" +
            "\t\t\"1\": {\n" +
            "\t\t\t\"state\": {\n" +
            "\t\t\t\t\"on\": false,\n" +
            "\t\t\t\t\"bri\": 110,\n" +
            "\t\t\t\t\"hue\": 15331,\n" +
            "\t\t\t\t\"sat\": 121,\n" +
            "\t\t\t\t\"xy\": [\n" +
            "\t\t\t\t\t0.4448,\n" +
            "\t\t\t\t\t0.4066\n" +
            "\t\t\t\t],\n" +
            "\t\t\t\t\"ct\": 343,\n" +
            "\t\t\t\t\"alert\": \"none\",\n" +
            "\t\t\t\t\"effect\": \"none\",\n" +
            "\t\t\t\t\"colormode\": \"ct\",\n" +
            "\t\t\t\t\"reachable\": true\n" +
            "\t\t\t},\n" +
            "\t\t\t\"type\": \"Extended color light\",\n" +
            "\t\t\t\"name\": \"Rare bol\",\n" +
            "\t\t\t\"modelid\": \"LCT001\",\n" +
            "\t\t\t\"swversion\": \"65003148\",\n" +
            "\t\t\t\"pointsymbol\": {\n" +
            "\t\t\t\t\"1\": \"none\",\n" +
            "\t\t\t\t\"2\": \"none\",\n" +
            "\t\t\t\t\"3\": \"none\",\n" +
            "\t\t\t\t\"4\": \"none\",\n" +
            "\t\t\t\t\"5\": \"none\",\n" +
            "\t\t\t\t\"6\": \"none\",\n" +
            "\t\t\t\t\"7\": \"none\",\n" +
            "\t\t\t\t\"8\": \"none\"\n" +
            "\t\t\t}\n" +
            "\t\t},\n" +
            "\t\t\"2\": {\n" +
            "\t\t\t\"state\": {\n" +
            "\t\t\t\t\"on\": false,\n" +
            "\t\t\t\t\"bri\": 110,\n" +
            "\t\t\t\t\"hue\": 15331,\n" +
            "\t\t\t\t\"sat\": 121,\n" +
            "\t\t\t\t\"xy\": [\n" +
            "\t\t\t\t\t0.4448,\n" +
            "\t\t\t\t\t0.4066\n" +
            "\t\t\t\t],\n" +
            "\t\t\t\t\"ct\": 343,\n" +
            "\t\t\t\t\"alert\": \"none\",\n" +
            "\t\t\t\t\"effect\": \"none\",\n" +
            "\t\t\t\t\"colormode\": \"ct\",\n" +
            "\t\t\t\t\"reachable\": true\n" +
            "\t\t\t},\n" +
            "\t\t\t\"type\": \"Extended color light\",\n" +
            "\t\t\t\"name\": \"Bol\",\n" +
            "\t\t\t\"modelid\": \"LCT001\",\n" +
            "\t\t\t\"swversion\": \"65003148\",\n" +
            "\t\t\t\"pointsymbol\": {\n" +
            "\t\t\t\t\"1\": \"none\",\n" +
            "\t\t\t\t\"2\": \"none\",\n" +
            "\t\t\t\t\"3\": \"none\",\n" +
            "\t\t\t\t\"4\": \"none\",\n" +
            "\t\t\t\t\"5\": \"none\",\n" +
            "\t\t\t\t\"6\": \"none\",\n" +
            "\t\t\t\t\"7\": \"none\",\n" +
            "\t\t\t\t\"8\": \"none\"\n" +
            "\t\t\t}\n" +
            "\t\t},\n" +
            "\t\t\"3\": {\n" +
            "\t\t\t\"state\": {\n" +
            "\t\t\t\t\"on\": false,\n" +
            "\t\t\t\t\"bri\": 254,\n" +
            "\t\t\t\t\"hue\": 14922,\n" +
            "\t\t\t\t\"sat\": 144,\n" +
            "\t\t\t\t\"xy\": [\n" +
            "\t\t\t\t\t0.4595,\n" +
            "\t\t\t\t\t0.4105\n" +
            "\t\t\t\t],\n" +
            "\t\t\t\t\"ct\": 369,\n" +
            "\t\t\t\t\"alert\": \"none\",\n" +
            "\t\t\t\t\"effect\": \"none\",\n" +
            "\t\t\t\t\"colormode\": \"ct\",\n" +
            "\t\t\t\t\"reachable\": true\n" +
            "\t\t\t},\n" +
            "\t\t\t\"type\": \"Extended color light\",\n" +
            "\t\t\t\"name\": \"Raam\",\n" +
            "\t\t\t\"modelid\": \"LCT001\",\n" +
            "\t\t\t\"swversion\": \"65003148\",\n" +
            "\t\t\t\"pointsymbol\": {\n" +
            "\t\t\t\t\"1\": \"none\",\n" +
            "\t\t\t\t\"2\": \"none\",\n" +
            "\t\t\t\t\"3\": \"none\",\n" +
            "\t\t\t\t\"4\": \"none\",\n" +
            "\t\t\t\t\"5\": \"none\",\n" +
            "\t\t\t\t\"6\": \"none\",\n" +
            "\t\t\t\t\"7\": \"none\",\n" +
            "\t\t\t\t\"8\": \"none\"\n" +
            "\t\t\t}\n" +
            "\t\t}\n" +
            "\t},\n" +
            "\t\"groups\": {},\n" +
            "\t\"config\": {\n" +
            "\t\t\"name\": \"Philips hue\",\n" +
            "\t\t\"mac\": \"00:17:88:0a:26:f3\",\n" +
            "\t\t\"dhcp\": true,\n" +
            "\t\t\"ipaddress\": \"10.10.4.168\",\n" +
            "\t\t\"netmask\": \"255.255.0.0\",\n" +
            "\t\t\"gateway\": \"10.10.2.1\",\n" +
            "\t\t\"proxyaddress\": \"none\",\n" +
            "\t\t\"proxyport\": 0,\n" +
            "\t\t\"UTC\": \"2013-09-18T14:06:17\",\n" +
            "\t\t\"whitelist\": {\n" +
            "\t\t\t\"0f607264fc6318a92b9e13c65db7cd3c\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-10T14:41:03\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-03T12:36:55\",\n" +
            "\t\t\t\t\"name\": \"mPhone\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"openremote\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-18T14:06:17\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-03T12:41:36\",\n" +
            "\t\t\t\t\"name\": \"test user\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"000000003c0c04b57d6cd2e97d6cd2e9\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-04T07:42:42\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-04T07:41:51\",\n" +
            "\t\t\t\t\"name\": \"samsung GT-I9300\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"37aadee639b0ade415c781322485ab5f\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:13:36\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-04T13:48:22\",\n" +
            "\t\t\t\t\"name\": \"de.jaetzold.philips.hue.HueBridge\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"3da06a61ebd1fa436d1acf21863561f\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"name\": \"de.jaetzold.philips.hue.HueBridge\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"7b183542d37eb0f31af4d7c295e8c77\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-13T13:45:44\",\n" +
            "\t\t\t\t\"name\": \"iPhone\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"sy5kfn9TGAlFl3WG\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-16T15:09:29\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-13T15:11:58\",\n" +
            "\t\t\t\t\"name\": \"mPhone\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"ffffffff927dcf93ffffffff978612dc\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-13T15:12:10\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-13T15:12:00\",\n" +
            "\t\t\t\t\"name\": \"Sony Ericsson LT26i\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"8ExyDnGOj76wBrcm\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-16T09:42:52\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-16T09:39:24\",\n" +
            "\t\t\t\t\"name\": \"mPad\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"fb93355359d3fc7a9ba4211102e0f3\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-16T09:52:15\",\n" +
            "\t\t\t\t\"name\": \"OpenRemoteController\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"2c58ad982c8a527f26a3074831dda28f\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-16T11:38:41\",\n" +
            "\t\t\t\t\"name\": \"OpenRemoteController\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"30904893e8af5ef2eb4f78b1fe56967\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-16T11:42:50\",\n" +
            "\t\t\t\t\"name\": \"OpenRemoteController\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"130c46b814d85d1f271dd8682d18d52f\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-16T11:46:46\",\n" +
            "\t\t\t\t\"name\": \"OpenRemoteController\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"123f634b4ec3daf2f282c533ef45337\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-16T11:49:46\",\n" +
            "\t\t\t\t\"name\": \"OpenRemoteController\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"35f871e421159b4f33e40e0c3ae964d7\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-16T11:51:52\",\n" +
            "\t\t\t\t\"name\": \"OpenRemoteController\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"1fa3b28c2e05bdcf3595866419d5bd67\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-16T11:54:52\",\n" +
            "\t\t\t\t\"name\": \"OpenRemoteController\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"2f76d0f49a8822f1cbe4f1c33c17797\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-16T11:56:29\",\n" +
            "\t\t\t\t\"name\": \"OpenRemoteController\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"269a612b66b298f2bc2e6332b30bb17\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-16T11:59:22\",\n" +
            "\t\t\t\t\"name\": \"OpenRemoteController\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"1ffa38091ef240f71d842f4d2e8e001b\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-16T12:03:33\",\n" +
            "\t\t\t\t\"name\": \"OpenRemoteController\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"83b2913c51f4b7277254c592526eb\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-16T12:05:09\",\n" +
            "\t\t\t\t\"name\": \"OpenRemoteController\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"32dc3551342b29773acf3852e9077ab\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-16T12:10:46\",\n" +
            "\t\t\t\t\"name\": \"OpenRemoteController\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"1cb16b7939fa9af710a261bd1645b7fb\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-16T12:11:11\",\n" +
            "\t\t\t\t\"name\": \"OpenRemoteController\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"33a8a5fa1bc35b3735aa9f66b52b453\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-16T12:11:19\",\n" +
            "\t\t\t\t\"name\": \"OpenRemoteController\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"32d796be334d6f072de926d21253872b\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-16T12:23:29\",\n" +
            "\t\t\t\t\"name\": \"OpenRemoteController\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"1d6d491e12b83ce71007cf322c77430b\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-16T12:29:36\",\n" +
            "\t\t\t\t\"name\": \"OpenRemoteController\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"2cf7ae6346f96cf3d8ee35b9696e47\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-16T12:31:41\",\n" +
            "\t\t\t\t\"name\": \"OpenRemoteController\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"311edfc339031a2f28568bb378a05a7\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-16T12:55:22\",\n" +
            "\t\t\t\t\"name\": \"OpenRemoteController\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"5e311422fe25d3731d73e5e18c18543\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-16T13:47:28\",\n" +
            "\t\t\t\t\"name\": \"OpenRemoteController\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"39724f4414a80b2f1ef9976c3fa9d0b7\": {\n" +
            "\t\t\t\t\"last use date\": \"2013-09-05T15:17:25\",\n" +
            "\t\t\t\t\"create date\": \"2013-09-16T14:20:15\",\n" +
            "\t\t\t\t\"name\": \"OpenRemoteController\"\n" +
            "\t\t\t}\n" +
            "\t\t},\n" +
            "\t\t\"swversion\": \"01006390\",\n" +
            "\t\t\"swupdate\": {\n" +
            "\t\t\t\"updatestate\": 0,\n" +
            "\t\t\t\"url\": \"\",\n" +
            "\t\t\t\"text\": \"\",\n" +
            "\t\t\t\"notify\": false\n" +
            "\t\t},\n" +
            "\t\t\"linkbutton\": false,\n" +
            "\t\t\"portalservices\": true\n" +
            "\t},\n" +
            "\t\"schedules\": {},\n" +
            "\t\"scenes\": {\n" +
            "\t\t\"2e1dbbb4ac\": {\n" +
            "\t\t\t\"name\": \"2e1dbbb4ac\",\n" +
            "\t\t\t\"lights\": [\n" +
            "\t\t\t\t\"1\",\n" +
            "\t\t\t\t\"2\",\n" +
            "\t\t\t\t\"3\"\n" +
            "\t\t\t],\n" +
            "\t\t\t\"active\": true\n" +
            "\t\t},\n" +
            "\t\t\"caed3bc97a\": {\n" +
            "\t\t\t\"name\": \"caed3bc97a\",\n" +
            "\t\t\t\"lights\": [\n" +
            "\t\t\t\t\"1\",\n" +
            "\t\t\t\t\"2\",\n" +
            "\t\t\t\t\"3\"\n" +
            "\t\t\t],\n" +
            "\t\t\t\"active\": true\n" +
            "\t\t},\n" +
            "\t\t\"84fa88aa5f\": {\n" +
            "\t\t\t\"name\": \"84fa88aa5f\",\n" +
            "\t\t\t\"lights\": [\n" +
            "\t\t\t\t\"1\",\n" +
            "\t\t\t\t\"2\",\n" +
            "\t\t\t\t\"3\"\n" +
            "\t\t\t],\n" +
            "\t\t\t\"active\": true\n" +
            "\t\t},\n" +
            "\t\t\"007ee0187f\": {\n" +
            "\t\t\t\"name\": \"007ee0187f\",\n" +
            "\t\t\t\"lights\": [\n" +
            "\t\t\t\t\"2\",\n" +
            "\t\t\t\t\"3\"\n" +
            "\t\t\t],\n" +
            "\t\t\t\"active\": true\n" +
            "\t\t},\n" +
            "\t\t\"e5a1981a08\": {\n" +
            "\t\t\t\"name\": \"e5a1981a08\",\n" +
            "\t\t\t\"lights\": [\n" +
            "\t\t\t\t\"2\",\n" +
            "\t\t\t\t\"3\"\n" +
            "\t\t\t],\n" +
            "\t\t\t\"active\": true\n" +
            "\t\t},\n" +
            "\t\t\"6ff4c503a7\": {\n" +
            "\t\t\t\"name\": \"6ff4c503a7\",\n" +
            "\t\t\t\"lights\": [\n" +
            "\t\t\t\t\"1\",\n" +
            "\t\t\t\t\"2\",\n" +
            "\t\t\t\t\"3\"\n" +
            "\t\t\t],\n" +
            "\t\t\t\"active\": true\n" +
            "\t\t},\n" +
            "\t\t\"2141cb8bd2\": {\n" +
            "\t\t\t\"name\": \"2141cb8bd2\",\n" +
            "\t\t\t\"lights\": [\n" +
            "\t\t\t\t\"1\",\n" +
            "\t\t\t\t\"2\",\n" +
            "\t\t\t\t\"3\"\n" +
            "\t\t\t],\n" +
            "\t\t\t\"active\": true\n" +
            "\t\t},\n" +
            "\t\t\"e915785b2b\": {\n" +
            "\t\t\t\"name\": \"e915785b2b\",\n" +
            "\t\t\t\"lights\": [\n" +
            "\t\t\t\t\"1\",\n" +
            "\t\t\t\t\"2\",\n" +
            "\t\t\t\t\"3\"\n" +
            "\t\t\t],\n" +
            "\t\t\t\"active\": true\n" +
            "\t\t}\n" +
            "\t}\n" +
            "}" +
            "";

}
