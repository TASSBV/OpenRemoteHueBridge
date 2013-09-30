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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.utils.Logger;
import org.w3c.dom.Document;

import com.jayway.jsonpath.JsonPath;

/**
 * 
 * @author Marcus 2009-4-26
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class HttpGetCommand implements ExecutableCommand, EventListener, Runnable
{

  // Class Members --------------------------------------------------------------
  /**
   * Common logging category.
   */
  private static Logger logger = Logger.getLogger(HttpGetCommandBuilder.HTTP_PROTOCOL_LOG_CATEGORY);

  
  // Instance Fields ------------------------------------------------------------
  /** The uri to perform the http get request on */
  private URI uri;
  
  /** The http method (GET,POST,DELETE,PUT) to perform */
  private String method;
  
  /** The workload which is added to POST and PUT methods */
  private String workload;
  
  /** The content type which is set in the HTTP header */
  private String contentType;

  /** The username which is used for basic authentication */
  private String username;

  /** The password which is used for basic authentication */
  private byte[] password;

  /** The xpath which is used to extract sensor data from received result */
  private String xpathExpression;

  /** The jsonpath which is used to extract sensor data from received json data*/
  private String jsonpathExpression;
  
  /** The regex which is used to extract sensor data from received result */
  private String regex;

  /** The polling interval which is used for the sensor update thread */
  private Integer pollingInterval;

  /** The thread that is used to peridically update the sensor */
  private Thread pollingThread;
  
  /** The sensor which is updated */
  private Sensor sensor;
  
  /** Boolean to indicate if polling thread should run */
  boolean doPoll = false;
  
  // Constructors  ----------------------------------------------------------------
  public HttpGetCommand(URI uri, String xpathExpression, String regex, Integer pollingInterval, String method, String workload, String jsonpathExpression, String contentType)
  {
    this.uri = uri;
    this.method = method;
    this.workload = workload;
    this.xpathExpression = xpathExpression;
    this.regex = regex;
    this.pollingInterval = pollingInterval;
    this.jsonpathExpression = jsonpathExpression;
    this.contentType = contentType;
  }

  public HttpGetCommand(URI uri, String username, byte[] pwd, String xpath, String regex, Integer pollingInterval, String method, String workload, String jsonpathExpression, String contentType)
  {
    this(uri, xpath, regex, pollingInterval, method, workload, jsonpathExpression, contentType);
    this.username = username;
    this.password = pwd;
  }

  // Public Instance Methods
  // ----------------------------------------------------------------------

  public URI getUri()
  {
    return uri;
  }

  public String getUsername()
  {
    return username;
  }

  public Integer getPollingInterval() {
     return pollingInterval;
  }

  public void setPollingInterval(Integer pollingInterval) {
     this.pollingInterval = pollingInterval;
  }
  
  // Implements ExecutableCommand
  // -----------------------------------------------------------------

@Override
  public void send()
  {
    requestURL();
  }

  @Override
  public void setSensor(Sensor sensor)
  {
    logger.debug("*** setSensor called as part of EventListener init *** sensor is: " + sensor);
    if (pollingInterval == null) {
      throw new RuntimeException("Could not set sensor because no polling interval was given");
    }
    this.sensor = sensor;
    this.doPoll = true;
    pollingThread = new Thread(this);
    pollingThread.setName("Polling thread for sensor: " + sensor.getName());
    pollingThread.start();
  }

  @Override
  public void stop(Sensor sensor)
  {
    this.doPoll = false;
  }
  
  
  // Private Instance Methods ---------------------------------------------------------------------
  private String requestURL()
  {

    DefaultHttpClient client = new DefaultHttpClient();
    if (getUsername() != null)
    {
      CredentialsProvider cred = new BasicCredentialsProvider();
      cred.setCredentials(new AuthScope(AuthScope.ANY), new UsernamePasswordCredentials(getUsername(), new String(password)));
      client.setCredentialsProvider(cred);
    }
    HttpUriRequest request = null;
    if (method.equalsIgnoreCase("GET")) {
      request = new HttpGet(uri);
    } else if (method.equalsIgnoreCase("POST")) {
      request = new HttpPost(uri);
      if ((workload != null) && (workload.trim().length() != 0)) {
        StringEntity data;
        try {
          data = new StringEntity(workload, "UTF-8");
          ((HttpPost)request).setEntity(data);
        } catch (UnsupportedEncodingException e) {
          logger.error("Could not set HTTP Post method workload", e);
        }
      }
    } else if (method.equalsIgnoreCase("PUT")) {
       request = new HttpPut(uri);
       if ((workload != null) && (workload.trim().length() != 0)) {
         StringEntity data;
         try {
           data = new StringEntity(workload, "UTF-8");
           ((HttpPut)request).setEntity(data);
         } catch (UnsupportedEncodingException e) {
           logger.error("Could not set HTTP Put method workload", e);
         }
       }
    } else if (method.equalsIgnoreCase("DELETE")) {
       request = new HttpDelete(uri);
    }
    String resp = "";
    HttpResponse response = null;
    ResponseHandler<String> responseHandler = new BasicResponseHandler();
    request.addHeader("User-Agent", "OpenRemoteController");
    request.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
    if (contentType != null) {
       request.addHeader("Content-Type", contentType);
    }
    try {
       response = client.execute(request);
       resp = responseHandler.handleResponse(response);
    } catch (ClientProtocolException e) {
       logger.error("ClientProtocolException when executing HTTP method", e);
    } catch (IOException e) {
       logger.error("IOException when executing HTTP method", e);
    }
    finally {
       try {
          if ((response != null) && (response.getEntity() != null)) {
             response.getEntity().consumeContent();    
          }
       } catch (IOException ignored) {}
       client.getConnectionManager().shutdown();
    }
    logger.info("received message: " + resp);
    return resp;
  }
  
  @Override
  public void run() {
     logger.debug("Sensor thread started for sensor: " + sensor);
     while (doPoll) {
        String readValue = this.requestURL();
        if (regex != null) {
          Pattern regexPattern = Pattern.compile(regex);
          Matcher matcher = regexPattern.matcher(readValue);
          if (matcher.find()) {
            String result = matcher.group();
            logger.info("result of regex evaluation: " + result);
            sensor.update(result);
          } else {
            logger.info("regex evaluation did not find a match");
            sensor.update("N/A");
          }
        } else if (xpathExpression != null) {
          DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
          //The following line had "true" but I changed this to "false" since it did not work with
          //documents that actually had a namespace defined. (MR)
          factory.setNamespaceAware(false);
          String result;
          try
          {
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream bin = new ByteArrayInputStream(readValue.getBytes());
            Document doc = builder.parse(bin);

            XPathFactory xfac = XPathFactory.newInstance();
            XPath xpath = xfac.newXPath();
            XPathExpression expr = xpath.compile(xpathExpression);
            result = (String)expr.evaluate(doc, XPathConstants.STRING);
            logger.info("result of xpath evaluation: " + result);
            sensor.update(result);
          } catch (Exception e)
          {
            logger.error("Could not perform xpath evaluation", e);
            sensor.update("N/A");
          }
        } else if (jsonpathExpression !=null) {
          try {
            Object result = JsonPath.read(readValue, jsonpathExpression);
            sensor.update(result.toString());
          } catch (Exception e) 
          {
            sensor.update("N/A");
            logger.error("Could not perform jsonpath evaluation", e);    
          }
        } else {
          sensor.update(readValue);
        }
        try {
           Thread.sleep(pollingInterval); // We wait for the given pollingInterval before requesting URL again
        } catch (InterruptedException e) {
           doPoll = false;
           pollingThread.interrupt();
        }
     }
     logger.debug("*** Out of run method: " + sensor);
  }

}
