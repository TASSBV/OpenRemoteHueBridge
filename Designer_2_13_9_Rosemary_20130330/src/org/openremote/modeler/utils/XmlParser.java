/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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

package org.openremote.modeler.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.openremote.modeler.SpringContext;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.domain.ConfigCategory;
import org.openremote.modeler.domain.ControllerConfig;
import org.openremote.modeler.exception.XmlParserException;
import org.openremote.modeler.service.ControllerConfigService;
import org.xml.sax.InputSource;

/**
 * Util class for parsering xml file.
 * 
 * @author Tomsky, Handy
 */
public class XmlParser {
   
   /** The Constant LOGGER. */
   private static final Logger LOGGER = Logger.getLogger(XmlParser.class);
   
   /** The Constant SCHEMA_LANGUAGE. */
   public static final String SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
   
   /** The Constant XML_SCHEMA. */
   public static final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
   
   /** The Constant SCHEMA_SOURCE. */
   public static final String SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
   
   /**
    * Instantiates a new xml parser.
    */
   private XmlParser() {      
   }
   
   /**
    * Modify xmlString and download icons from beehive.
    * 
    * @param xmlString the xml string
    * @param folder the folder
    * @param xsdfile the xsdfile
    * 
    * @return modified panelXML
    */
   public static String validateAndOutputXML(File xsdfile, String xmlString, File folder) {
      SAXBuilder sb = new SAXBuilder(true);
      sb.setValidation(true);

      sb.setProperty(SCHEMA_LANGUAGE, XML_SCHEMA);
      sb.setProperty(SCHEMA_SOURCE, xsdfile);
      String result = "";
      try {         
          Document doc = sb.build(new InputSource(new StringReader(xmlString)));
//          xpathParseImage(folder, doc, "//or:*[@src]", "src");
//          xpathParseImage(folder, doc, "//or:state[@value]", "value");
          //xpathParseImage(folder, doc, "//or:background[@src]", "src");
         Format format = Format.getPrettyFormat();
         format.setIndent("  ");
         format.setEncoding("UTF-8");
         XMLOutputter outp = new XMLOutputter(format);
         result = outp.outputString(doc);
      } catch (JDOMException e) {
          throw new XmlParserException("Parser XML occur JDOMException", e);
      } catch (IOException e) {
         throw new XmlParserException("Parser XML occur IOException", e);
      }
      return result;
   }
   
   public static String validateAndOutputXML(File xsdfile, String xmlString) {
      SAXBuilder sb = new SAXBuilder(true);
      sb.setValidation(true);

      sb.setProperty(SCHEMA_LANGUAGE, XML_SCHEMA);
      sb.setProperty(SCHEMA_SOURCE, xsdfile);
      String result = "";
      try {         
          Document doc = sb.build(new InputSource(new StringReader(xmlString)));
         Format format = Format.getPrettyFormat();
         format.setIndent("  ");
         format.setEncoding("UTF-8");
         XMLOutputter outp = new XMLOutputter(format);
         result = outp.outputString(doc);
      } catch (JDOMException e) {
          throw new XmlParserException("Parser XML occur JDOMException", e);
      } catch (IOException e) {
         throw new XmlParserException("Parser XML occur IOException", e);
      }
      return result;
   }
   @SuppressWarnings({ "unchecked", "unused" })
   private static void xpathParseImage(File folder, Document doc, String xpathExpression, String attrName) throws JDOMException, IOException {
      XPath xpath = XPath.newInstance(xpathExpression);
       xpath.addNamespace("or", "http://www.openremote.org");
       List<Element> elements = xpath.selectNodes(doc);
       for (Element element : elements) {
          String iconVal = element.getAttributeValue(attrName);
          String iconName = iconVal.substring(iconVal.lastIndexOf("/") + 1);
          element.setAttribute(attrName, iconName);
          File iphoneIconFile = new File(folder, iconName);
          if (iconVal.startsWith("http")) {
             downloadFile(iconVal, iphoneIconFile);
          }
      }
   }
   
   /**
    * Download file.
    * 
    * @param srcUrl the src url
    * @param destFile the dest file
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    */
   public static void downloadFile(String srcUrl, File destFile) throws IOException {
      HttpClient client = new HttpClient();
      GetMethod get = new GetMethod(srcUrl);
      client.executeMethod(get);
      FileOutputStream output = new FileOutputStream(destFile);
      if (HttpServletResponse.SC_OK == get.getStatusCode()) {
         output.write(get.getResponseBody());
         output.close();
      } else {
         throw new IOException("Can not download file from :"+ srcUrl);
      }
   }
   
   /**
    * Check xml schema.
    * 
    * @param xsdPath the xsd path
    * @param xmlString the xml string
    * 
    * @return true, if successful
    */
   public static boolean checkXmlSchema(String xsdPath, String xmlString) {
      SAXBuilder sb = new SAXBuilder(true);
      sb.setValidation(true);
      
      File xsdfile = new File(xsdPath);

      sb.setProperty(SCHEMA_LANGUAGE, XML_SCHEMA);
      sb.setProperty(SCHEMA_SOURCE, xsdfile);
      try {
         sb.build(new InputSource(new StringReader(xmlString)));
      } catch (JDOMException e) {
         LOGGER.error("Check the schema " + xsdfile.getName() + " occur JDOMException", e);
         return false;
      } catch (IOException e) {
         LOGGER.error("Check the schema " + xsdfile.getName() + " occur IOException", e);
         return false;
      }
      return true;
   }
   
   /**
    * Gets the document from xml file.
    * 
    * @param xmlPath the xml path
    * @param xsdPath the xsd path
    * 
    * @return the document
    */
   public static Document getDocument(String xmlPath,String xsdPath) {
      SAXBuilder builder = new SAXBuilder();
      builder.setValidation(true);
      
      File xsdfile = new File(xsdPath);

      builder.setProperty(SCHEMA_LANGUAGE, XML_SCHEMA);
      builder.setProperty(SCHEMA_SOURCE, xsdfile);
      Document doc = null;
      try {
         doc = builder.build(XmlParser.class.getClassLoader().getResourceAsStream(ControllerConfigService.CONTROLLER_CONFIG_XML_FILE));
      } catch (Exception e) {
         throw new XmlParserException("Failed to parse "+ControllerConfigService.CONTROLLER_CONFIG_XML_FILE, e);
      }

      return doc;
   }
   
   /**
    * Gets the elements by element name from the xml document.
    * 
    * @param doc the doc
    * @param eleName the ele name
    * 
    * @return the elements by element name
    */
   @SuppressWarnings("unchecked")
   public static List<Element> getElementsByElementName(Document doc,String eleName){
      String xpath = "//or" + eleName;
      try {
         XPath xPath = XPath.newInstance(xpath);
         xPath.addNamespace(Constants.OPENREMOTE_NAMESPACE, Constants.OPENREMOTE_WEBSITE);
         List<Element> elements = xPath.selectNodes(doc);
         return elements;
      } catch (JDOMException e) {
         throw new XmlParserException(e);
      }
   }
   
   /**
    * Builds the controllerConfig from xml.
    * 
    * @param ele the element
    * @param categoryName the category name
    * 
    * @return the controller config
    */
   @SuppressWarnings("unchecked")
   public static ControllerConfig buildFromXml(Element ele,String categoryName){
      ControllerConfig cfg = new ControllerConfig();
      List<Attribute> attributes = ele.getAttributes();
      for(Attribute attr : attributes){
         if(ControllerConfig.NAME_XML_ATTRIBUTE_NAME.equals(attr.getName())){
            cfg.setName(attr.getValue());
         } else if(ControllerConfig.VALUE_XML_ATTRIBUTE_NAME.equals(attr.getName())){
            cfg.setValue(attr.getValue());
         } else if(ControllerConfig.VALIDATION_XML_ATTRIBUTE_NAME.equals(attr.getName())){
            cfg.setValidation(attr.getValue());
         } else if(ControllerConfig.OPTION_XML_ATTRIBUTE_NAME.equals(attr.getName())){
            cfg.setOptions(attr.getValue());
         }
      }
      Element hintEle = ele.getChild(ControllerConfig.HINT_XML_NODE_NAME,ele.getNamespace());
      String hint = hintEle == null ?"":hintEle.getTextNormalize();
      cfg.setHint(hint);
      cfg.setCategory(categoryName==null?"":categoryName);
      return cfg;
   }
   
   /**
    * Inits the default controller config from predefined controller-config-2.0-M7.xml.
    * 
    * @param categories the categories
    * @param defaultConfigs the default configs
    */
   @SuppressWarnings("unchecked")
   public static void initControllerConfig(Set<ConfigCategory> categories,Collection<ControllerConfig> defaultConfigs){
      Configuration configuration = (Configuration) SpringContext.getInstance().getBean("configuration");
      String xsdPath = XmlParser.class.getResource(configuration.getControllerConfigXsdPath()).getPath();
      Document doc = XmlParser.getDocument(ControllerConfigService.CONTROLLER_CONFIG_XML_FILE,xsdPath);
      Element root = doc.getRootElement();
      List<Element> categorys = root.getChildren(ConfigCategory.XML_NODE_NAME,root.getNamespace());
      for(Element categoryEle : categorys){
         /*
          * initialize category 
          */
         String categoryName = categoryEle.getAttributeValue(ConfigCategory.NAME_XML_ATRIBUTE_NAME);
         Element descriptionEle = categoryEle.getChild(ConfigCategory.DESCRIBTION_NODE_NAME, categoryEle.getNamespace());
         String description = descriptionEle == null? "":descriptionEle.getTextNormalize();
         ConfigCategory category = new ConfigCategory(categoryName, description);
         categories.add(category);
         
         /*
          * initialize configuration
          */
         List<Element> configEles = categoryEle.getChildren(ControllerConfig.XML_NODE_NAME, categoryEle.getNamespace());
         for(Element configEle : configEles){
            ControllerConfig config = XmlParser.buildFromXml(configEle,categoryName);
            defaultConfigs.add(config);
         }
      }
   }
}
