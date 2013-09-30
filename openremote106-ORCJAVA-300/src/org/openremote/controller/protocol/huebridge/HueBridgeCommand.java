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
package org.openremote.controller.protocol.huebridge;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;
import org.json.JSONObject;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.utils.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/*
 * @author TASS Technology Solutions - www.tass.nl
 */
public class HueBridgeCommand implements ExecutableCommand, EventListener, Runnable
{

  // Class Members --------------------------------------------------------------
  /**
   * Common logging category.
   */
  private static Logger logger = Logger.getLogger(HueBridgeCommandBuilder.HUEBRIDGE_PROTOCOL_LOG_CATEGORY);


  // Instance Fields ------------------------------------------------------------
  /** The url as string of the bridge to perform the http request on */
  private String bridgeip;



  /** The key that needs to be used to authenticate with the bridge */
  private String key;

  /** The method of the request */
  private String sensorCommand;

  /** The id of the device that needs to be changed */
  private String lightid;

  /** The lightstatus to which the light will be set, ON or OFF */
  private Boolean lightstatus;

  /** The color to which the light will be set */
  private Integer color;

  /** The brightness to which the light will be set */
  private Integer brightness;

  /** The Saturation to which the lamp will be set */
  private Integer saturation;

  /** The polling interval which is used for the sensor update thread */
  private Integer pollingInterval;

  /** The thread that is used to peridically update the sensor */
  private Thread pollingThread;

  /** The sensor which is updated */
  private Sensor sensor;

  /** Boolean to indicate if polling thread should run */
  boolean doPoll = false;

    // Constructor  ----------------------------------------------------------------


    public HueBridgeCommand(String bridgeip, String key, String sensorCommand, String lightid, Boolean lightstatus, Integer color, Integer brightness, Integer saturation, Integer intervalInMillis) {
        this.bridgeip = bridgeip;
        this.key = key;
        this.sensorCommand = sensorCommand;
        this.lightid = lightid;
        this.lightstatus = lightstatus;
        this.color = color;
        this.brightness = brightness;
        this.saturation = saturation;
        this.pollingInterval = intervalInMillis;
    }


    // Public Instance Methods
  // ----------------------------------------------------------------------

//  public URI getUri()
//  {
//    return uri;
//  }

    public String getSensorCommand(){
        return sensorCommand;
    }


  public Integer getPollingInterval() {
     return pollingInterval;
  }

  public void setPollingInterval(Integer pollingInterval) {
     this.pollingInterval = pollingInterval;
  }

  // Implements ExecutableCommand
  // -----------------------------------------------------------------


    /**
     * Creates the  {@link URI} based on the bridgeip, key and lightid.
     * Depending if the {@link #sensorCommand} is set it will create a different {@link URI}
     * @return a {@link URI} for controlling a light Example: http://10.10.4.168/api/2e3199608f4f58425b0126ffd3/lights/2/state
     */
    public URI getUri(){

        String combined = "http://"+bridgeip +"/api/"+ key +"/lights/"+lightid;
        if(sensorCommand == null){
            combined = combined +"/state";
        }

        URL url;
        URI uri;
        try {

            url = new URL(combined);
            uri = new URI(url.getProtocol(), null, url.getHost(), url.getPort(), url.getPath(), url.getQuery(), null);
        } catch (MalformedURLException e) {
            throw new NoSuchCommandException("Invalid URL: " + e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new NoSuchCommandException("Invalid URI: " + e.getMessage(), e);
        }
            return uri;
    }

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


    /**
     * Get a response from the address create with {@link #getUri()}
     * It uses {@link DefaultHttpClient}. This method uses a {@link HttpGet} when {@link #sensorCommand} is set
     *  otherwise it will use a {@link HttpPut} with a workload created by {@link #createWorkload()}.
     * @return {@link String} from
     */
  private String requestURL()
  {
    DefaultHttpClient client = new DefaultHttpClient();

    URI uri  = getUri();
    String contentType = "application/json";

    HttpUriRequest request = null;
    if (sensorCommand != null) {

      request = new HttpGet(uri);

    } else if (sensorCommand == null) {

        String workload  = createWorkload();
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

    public String createWorkload() {
        StringBuilder builder = new StringBuilder("{");
        boolean addComma = false;

        if(lightstatus != null){
           builder.append("\"on\":" + lightstatus.toString());
            addComma = true;
        }
        if(brightness != null){
            if(addComma) builder.append(",");
            builder.append("\"bri\":"+brightness.toString());
            addComma = true;
         }
        if (color !=null){
            if(addComma) builder.append(",");
            builder.append("\"hue\":"+color.toString());
            addComma = true;
        }
        if(saturation != null)  {
            if(addComma) builder.append(",");
            builder.append("\"sat\":"+saturation.toString());
        }
        builder.append("}");
        String workload = builder.toString();
        return workload;
    }

    @Override
  public void run() {
     logger.debug("Sensor thread started for sensor: " + sensor);
     while (doPoll) {
        String readValue = this.requestURL();
        String sensorCommand = getSensorCommand();
         if(getSensorCommand()!= null){
             try {
                 JSONObject total = new JSONObject(readValue);
                 JSONObject state = total.getJSONObject("state");
                 String result ="";
                 if(sensorCommand.equalsIgnoreCase("power")){
                     result = state.getString("on");
                 }else if (sensorCommand.equalsIgnoreCase("color")) {
                     result = state.getString("hue");
                 }else if (sensorCommand.equalsIgnoreCase("brightness")) {
                     result = state.getString("bri");
                 }else if (sensorCommand.equalsIgnoreCase("saturation")) {
                     result = state.getString("sat");
                 }
                 sensor.update(result);

             } catch (JSONException e) {
                 sensor.update("N/A");
                 logger.error("Could not perform jsonpath evaluation", e);
             }
         }   else{
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
