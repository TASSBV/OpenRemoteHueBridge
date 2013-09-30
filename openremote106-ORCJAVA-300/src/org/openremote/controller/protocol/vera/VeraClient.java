/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.controller.protocol.vera;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.openremote.controller.protocol.vera.model.Dimmer;
import org.openremote.controller.protocol.vera.model.HumiditySensor;
import org.openremote.controller.protocol.vera.model.PowerMeter;
import org.openremote.controller.protocol.vera.model.SecuritySensor;
import org.openremote.controller.protocol.vera.model.Switch;
import org.openremote.controller.protocol.vera.model.TemperatureSensor;
import org.openremote.controller.protocol.vera.model.Thermostat;
import org.openremote.controller.protocol.vera.model.GenericDevice;
import org.openremote.controller.protocol.vera.model.VeraCategory;
import org.openremote.controller.protocol.vera.model.VeraDevice;
import org.openremote.controller.utils.Logger;

/**
 * This class is doing the communication with the Vera.
 * Only one instance is created during system start.
 * 
 * @author marcus
 *
 */
public class VeraClient extends Thread {

   // Constants ------------------------------------------------------------------------------------
   public final static int TIMEOUT = 60;
   public final static int MINIMUM_DELAY = 500;
   
   // Class Members --------------------------------------------------------------------------------
   private final static Logger log = Logger.getLogger(VeraCommandBuilder.VERA_PROTOCOL_LOG_CATEGORY);

   // Instance Fields ------------------------------------------------------------------------------
   private boolean running;
   private String lastLoadtime;
   private String lastDataversion;
   private String address;
   private Map<Integer, VeraDevice> devices = new HashMap<Integer, VeraDevice>();
   
   // Constructors ----------------------------------------------------------------------------------
   public VeraClient(String address) {
      super("Vera Client");
      this.address = address;
   }

   // Methods ------------------------------------------------------------------------------
   public Map<Integer, VeraDevice> startVeraClient() {
      String initialStatus = requestStatus(true);
      if (initialStatus == null) {
         log.error("Could not get initial Vera status. Vera protocol is not started");
         return null;
      }
      
      try {
         parseFullStatus(initialStatus);
      } catch (Exception e) {
         log.error("Could not parse Vera status file. Vera protocol is not started", e);
         return null;
      }
      
      this.running = true;
      this.start();
      return this.devices;
   }
   
   @Override
   public void run() {
      while(this.running) {
         String result = requestStatus(false);
         log.debug("Received Vera result:\n"+result);
         try {
            parseChangedStatus(result);
         } catch (Exception e) {
            log.warn("Could not parse Vera status file.", e);
         }
      }
   }
   
   public VeraDevice getDevice(Integer deviceId) {
      return devices.get(deviceId);
   }
   
   /**
    * This method is called once after the first full request is made.
    * It creates a list of devices and updates the initial status of those devices.
    *  
    * @param statusXML
    * @throws Exception
    */
   private void parseFullStatus(String statusXML) throws Exception {
      // parse the XML as a W3C Document
      SAXBuilder builder = new SAXBuilder();
      Document document = null;
      StringReader in = new StringReader(statusXML);
      document = builder.build(in);
      
      extractLoadtimeAndDataversion(document);
      createDevices(document);
      updateDeviceStatus(document);
   }
   
   @SuppressWarnings("unchecked")
   private void createDevices(Document document) throws Exception {
      XPath xpath = XPath.newInstance("//root/devices/device");
      List<Element> xresult = xpath.selectNodes(document);
      for (Element element : xresult) {
         String name = element.getAttributeValue("name");
         int id = Integer.parseInt(element.getAttributeValue("id"));
         VeraCategory category = VeraCategory.fromInt(Integer.parseInt(element.getAttributeValue("category")));
         switch (category) {
            case Switch:
               this.devices.put(id, new Switch(category, id, name, this));
               break;
            case DimmableLight:
               this.devices.put(id, new Dimmer(category, id, name, this));
               break;
            case Thermostat:
               this.devices.put(id, new Thermostat(category, id, name, this));
               break;
            case SecuritySensor:
               this.devices.put(id, new SecuritySensor(category, id, name, this));
               break;
            case TemperatureSensor:
               this.devices.put(id, new TemperatureSensor(category, id, name, this));
               break;
            case HumiditySensor:
               this.devices.put(id, new HumiditySensor(category, id, name, this));
               break;
            case PowerMeter:
               this.devices.put(id, new PowerMeter(category, id, name, this));
               break;
            default:
               this.devices.put(id, new GenericDevice(category, id, name, this));
         }
      }
   }

   /**
    * This method is called after each update request returns.
    * It updates the status of the known devices.
    * 
    * @param statusXML
    * @throws Exception
    */
   private void parseChangedStatus(String statusXML) throws Exception {
      // parse the XML as a W3C Document
      SAXBuilder builder = new SAXBuilder();
      Document document = null;
      StringReader in = new StringReader(statusXML);
      document = builder.build(in);
      
      extractLoadtimeAndDataversion(document);
      updateDeviceStatus(document);
   }
   
   @SuppressWarnings("unchecked")
   private void updateDeviceStatus(Document document) throws Exception {
      XPath xpath = XPath.newInstance("//root/devices/device");
      List<Element> xresult = xpath.selectNodes(document);
      for (Element element : xresult) {
         int id = Integer.parseInt(element.getAttributeValue("id"));
         VeraDevice device = devices.get(id);
         if (device != null) {
            device.updateStatus(element);
         }
      }
   }

   /**
    * This method extracts 'loadtime' and 'dataversion' from the given XML document
    * and stores those in 'lastLoadtime' and 'lastDataversion'
    * 
    * @param document the XML status document
    * @throws Exception
    */
   private void extractLoadtimeAndDataversion(Document document) throws Exception {
      // Extract loadtime
      XPath xpath = XPath.newInstance("//root/@loadtime");
      Attribute xresult = (Attribute)xpath.selectSingleNode(document);
      this.lastLoadtime = xresult.getValue();
      
      // Extract dataversion
      xpath = XPath.newInstance("//root/@dataversion");
      xresult = (Attribute)xpath.selectSingleNode(document);
      this.lastDataversion = xresult.getValue();
   }

   /**
    * This method performs an HTTP call to request Vera's status XML file.
    * If lastLoadtime is available an URL for the partial request is used otherwise
    * a full request is made.
    * 
    * @return a String with the status XML or NULL
    */
   private String requestStatus(boolean fullStatus) {
      DefaultHttpClient client = new DefaultHttpClient();
      HttpUriRequest request = null;
      if (lastLoadtime == null || fullStatus) {
         //Full request
         request = new HttpGet("http://" + this.address + ":3480/data_request?id=lu_sdata&output_format=xml");   
      } else {
         //Partial request with wait time
         StringBuffer sb = new StringBuffer("http://" + this.address + ":3480/data_request?id=lu_sdata&output_format=xml");
         sb.append("&loadtime="+this.lastLoadtime);
         sb.append("&dataversion="+this.lastDataversion);
         sb.append("&timeout=" + TIMEOUT + "&minimumdelay=" + MINIMUM_DELAY);
         request = new HttpGet(sb.toString());
      }
      
      String resp = null;
      ResponseHandler<String> responseHandler = new BasicResponseHandler();
      try {
         resp = client.execute(request, responseHandler);
      } catch (ClientProtocolException e) {
         log.error("ClientProtocolException when executing HTTP method", e);
      } catch (IOException e) {
         log.error("IOException when executing HTTP method", e);
      }
      log.debug("received message: " + resp);
      return resp;
   }
   
   public synchronized void requestFullStatus() {
      String result = requestStatus(true);
      try {
         parseChangedStatus(result);
      } catch (Exception e) {
         log.error("Could not parse full status", e);
      }
   }

   public boolean isRunning() {
      return this.running;
   }

   public void sendCommand(String url) {
      DefaultHttpClient client = new DefaultHttpClient();
      try {
         log.debug("Sending Vera command: " + url);
         ResponseHandler<String> responseHandler = new BasicResponseHandler();
         String result = client.execute(new HttpGet(url), responseHandler);
         log.debug("Received: " + result);
      } catch (Exception e) {
         log.error("Could not send URL", e);
      }
   }

   public String getAddress() {
      return this.address;
   }


}
