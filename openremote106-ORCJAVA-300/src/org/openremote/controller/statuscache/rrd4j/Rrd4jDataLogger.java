/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.controller.statuscache.rrd4j;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.statuscache.EventContext;
import org.openremote.controller.statuscache.EventProcessor;
import org.openremote.controller.statuscache.LifeCycleEvent;
import org.rrd4j.ConsolFun;
import org.rrd4j.DsType;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is an EventProcessor which parses the rrd4j-config.xml and created RRD4j databases.
 * Each sensor update creates a new data entry within a rrd4j datasource.
 * The rrd4j-config.xml can also be used to configure graphs which later can be displayed within the console.
 * The graphs are provided from a servlet.
 * @author marcus
 *
 */
public class Rrd4jDataLogger extends EventProcessor {

   private List<RrdDb> rrdDbList;
   private Map<String,String> graphDefMap;
   
   @Override
   public String getName() {
      return "RRD4J Data Logger";
   }

   @Override
   public synchronized void push(EventContext ctx) {
      String sensorName = ctx.getEvent().getSource();
      for (RrdDb rrdDb : rrdDbList) {
         try {
            if (rrdDb.getDatasource(sensorName) != null) {
               try {
                  long newUpdate = System.currentTimeMillis() / 1000;
                  long lastUpdate = rrdDb.getLastUpdateTime();
                  if (lastUpdate<newUpdate){
                     double value = Double.parseDouble("" + ctx.getEvent().getValue());
                     Sample sample = rrdDb.createSample(newUpdate);
                     sample.setValue(sensorName, value);
                     sample.update();
                  }
               } catch (NumberFormatException e) {
               }
            }
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }

   @Override
   public void start(LifeCycleEvent ctx) throws InitializationException {
      URI rrdDirUri = getRrdDirUri();
      URI rrdConfigUri = rrdDirUri.resolve("rrd4j-config.xml");
      if (!hasDirectoryReadAccess(rrdConfigUri)) {
         throw new InitializationException("Directory ''{0}'' does not exist or cannot be read.", rrdConfigUri);
      }
      
      //Parse XML for RRD4J databases and datasources
      List<RrdDef> rrdDefList = parseConfigXML(rrdConfigUri);
      rrdDbList = new ArrayList<RrdDb>();
      for (RrdDef rrdDef : rrdDefList) {
         RrdDb rrdDb = null;
         String dbFileName = rrdDef.getPath();
         try {
            URI rrdFileUri = rrdDirUri.resolve(dbFileName);
            File rrdFile = new File(rrdFileUri);
            if (rrdFile.exists()) {
               rrdDb = new RrdDb(rrdFile.getAbsolutePath());
            } else {
               rrdDef.setPath(rrdFile.getAbsolutePath());
               rrdDb = new RrdDb(rrdDef);
            }
            rrdDbList.add(rrdDb);
         } catch (IOException e) {
            throw new InitializationException("Could not load/create rrd4j db file", e);
         }
      }
      
      //Parse XML for RRD4J graph definitions
      graphDefMap = parseConfigXMLGraphs(rrdConfigUri, rrdDirUri);

      
   }

   public String getGraphDef(String graphName) {
      return graphDefMap.get(graphName);
   }
   
   @Override
   public void stop() {
      for (RrdDb rrdDb : rrdDbList) {
         try {
            rrdDb.close();
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }

   private Map<String,String> parseConfigXMLGraphs(URI configUri, URI rddDirUri) throws InitializationException {
      try {
         File fXmlFile = new File(configUri);
         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         Document doc = dBuilder.parse(fXmlFile);
         doc.getDocumentElement().normalize();
         Map<String,String> graphDefMap = new HashMap<String,String>();

         NodeList nList = doc.getElementsByTagName("rrd");
         for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            String a = nNode.getTextContent();
            nNode.setTextContent(rddDirUri.resolve(a).getPath());
         }
         
         nList = doc.getElementsByTagName("rrd_graph_def");
         for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            NamedNodeMap attributes = nNode.getAttributes();
            String graphName = attributes.getNamedItem("name").getNodeValue();
            String def = nodeToString(nNode);
            graphDefMap.put(graphName, def);
         }
         return graphDefMap;
         
      } catch (Exception e) {
         e.printStackTrace();
         throw new InitializationException("Error parsinf rrd4j-config.xml", e);
      }
   }

   private List<RrdDef> parseConfigXML(URI configUri) throws InitializationException {
      try {
         File fXmlFile = new File(configUri);
         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         Document doc = dBuilder.parse(fXmlFile);
         doc.getDocumentElement().normalize();
         List<RrdDef> rrdDefList = new ArrayList<RrdDef>();

         NodeList nList = doc.getElementsByTagName("rrdDB");
         for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            NamedNodeMap attributes = nNode.getAttributes();
            String dbFileName = attributes.getNamedItem("fileName").getNodeValue();
            String dbStep = attributes.getNamedItem("step").getNodeValue();

            RrdDef rrdDef = new RrdDef(dbFileName, Long.parseLong(dbStep));

            NodeList childs = nNode.getChildNodes();
            for (int i = 0; i < childs.getLength(); i++) {
               Node child = childs.item(i);
               if (child.getNodeType() == Node.ELEMENT_NODE) {
                  if (child.getNodeName().equalsIgnoreCase("datasource")) {
                     attributes = child.getAttributes();
                     String name = attributes.getNamedItem("name").getNodeValue();
                     String type = attributes.getNamedItem("type").getNodeValue();
                     String heartbeat = attributes.getNamedItem("heartbeat").getNodeValue();
                     String minValue = null;
                     String maxValue = null;
                     if (attributes.getNamedItem("minValue") != null) {
                        minValue = attributes.getNamedItem("minValue").getNodeValue();
                     }
                     if (attributes.getNamedItem("maxValue") != null) {
                        maxValue = attributes.getNamedItem("maxValue").getNodeValue();
                     }
                     rrdDef.addDatasource(name, DsType.valueOf(type), Long.parseLong(heartbeat),
                           (minValue == null) ? Double.NaN : Long.parseLong(minValue), (maxValue == null) ? Double.NaN
                                 : Long.parseLong(maxValue));
                  } else if (child.getNodeName().equalsIgnoreCase("archive")) {
                     attributes = child.getAttributes();
                     String function = attributes.getNamedItem("function").getNodeValue();
                     String xff = attributes.getNamedItem("xff").getNodeValue();
                     String steps = attributes.getNamedItem("steps").getNodeValue();
                     String rows = attributes.getNamedItem("rows").getNodeValue();
                     rrdDef.addArchive(ConsolFun.valueOf(function), Double.parseDouble(xff), Integer.parseInt(steps),
                           Integer.parseInt(rows));
                  }
               }
            }
            rrdDefList.add(rrdDef);
         }
         return rrdDefList;
      } catch (Exception e) {
         e.printStackTrace();
         throw new InitializationException("Error parsinf rrd4j-config.xml", e);
      }
   }

   private URI getRrdDirUri() throws InitializationException {
      ControllerConfiguration config = ServiceContext.getControllerConfiguration();
      URI resourceURI;
      try {
         resourceURI = new URI(config.getResourcePath());

         if (!resourceURI.isAbsolute()) {
            resourceURI = new File(config.getResourcePath()).toURI();
         }
      } catch (URISyntaxException e) {
         throw new InitializationException("Property 'resource.path' value ''{0}'' cannot be parsed. "
               + "It must contain a valid URI : {1}", e, config.getResourcePath(), e.getMessage());
      }
      URI rrdURI = resourceURI.resolve("rrd/");
      if (!hasDirectoryReadAccess(rrdURI)) {
         throw new InitializationException("Directory ''{0}'' does not exist or cannot be read.", rrdURI);
      }
      return rrdURI;
   }

   /**
    * Checks that the rrd directory exists and we can access it.
    * 
    * @param uri
    *           file URI pointing to the directory where rrd files are stored
    * 
    * @return true if we can read/write the dir, false otherwise
    * 
    * @throws InitializationException
    *            if URI is null or security manager was installed but read access was not granted to directory pointed
    *            by the given file URI
    */
   private boolean hasDirectoryReadAccess(URI uri) throws InitializationException {

      if (uri == null) {
         throw new InitializationException("rrd resource directory was resolved to 'null'");
      }

      File dir = new File(uri);

      try {
         return dir.exists() && dir.canRead() && dir.canWrite();
      }

      catch (SecurityException e) {
         throw new InitializationException("Security Manager has denied read access to directory ''{0}''. "
               + "In order to write rrd data, file write access must be explicitly "
               + "granted to this directory. ({1})", e, uri, e.getMessage());
      }
   }

   private String nodeToString(Node node) {
      StringWriter sw = new StringWriter();
      try {
      Transformer t = TransformerFactory.newInstance().newTransformer();
      t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      t.transform(new DOMSource(node), new StreamResult(sw));
      } catch (TransformerException te) {
      System.out.println("nodeToString Transformer Exception");
      }
      return sw.toString();
      }
}
