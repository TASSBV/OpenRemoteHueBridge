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

package org.openremote.modeler.service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXValidator;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.XMLErrorHandler;
import org.openremote.modeler.exception.ParseProtocolException;
import org.openremote.modeler.protocol.ProtocolAttrDefinition;
import org.openremote.modeler.protocol.ProtocolDefinition;
import org.openremote.modeler.protocol.ProtocolValidator;
import org.xml.sax.SAXException;

/**
 * Parse all the protocol describe xml file in classpath/protocols.
 * 
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
@SuppressWarnings("unchecked")
public class ProtocolParser {

   /** The Constant PROTOCOL_ELEMENT_NAME. */
   private static final String PROTOCOL_ELEMENT_NAME = "protocol";

   /** The Constant PROTOCOLS_DIR. */
   private static final String PROTOCOLS_DIR = "/protocols";
   
   /** The Constant PROTOCOL_XSD_FILE_NAME. */
   private static final String PROTOCOL_XSD_FILE_NAME = "protocol.xsd";

   /** The Constant VALIDATIONS_ElEMENT_NAME. */
   private static final String VALIDATIONS_ElEMENT_NAME = "validations";
   
   /** The Constant ATTR_ELEMENT_NAME. */
   private static final String ATTR_ELEMENT_NAME = "attr";

   /** The Constant LABEL_ATTR_NAME. */
   private static final String LABEL_ATTR_NAME = "label";
   
   /** The Constant NAME_ATTR_NAME. */
   private static final String NAME_ATTR_NAME = "name";
   
   private static final String TOOLTIP_MESSAGE_ATTR_NAME = "tooltipMessage";
   
   /** The Constant VALUE_ATTR_NAME for the attr default value. */
   private static final String VALUE_ATTR_NAME = "value";
   
   /** The Constant OPTIONS_ATTR_NAME for the attr predefined values. */
   private static final String OPTIONS_ATTR_NAME = "options";
   
   /** The Constant DISPLAY_NAME_ATTR_NAME. */
   private static final String DISPLAY_NAME_ATTR_NAME = "displayName";
   
   /** The Constant TAG_NAME_ATTR_NAME. */
   private static final String TAG_NAME_ATTR_NAME = "tagName"; 
   
   /** The Constant MESSAGE_ATTR_NAME. */
   private static final String MESSAGE_ATTR_NAME = "message";

   /** The xml path. */
   private String xmlPath = "";

   /**
    * Parses the xmls.
    * 
    * @return the hash map< string, protocol definition>
    */
   public HashMap<String, ProtocolDefinition> parseXmls() {
      HashMap<String, ProtocolDefinition> definitionHashMap = new HashMap<String, ProtocolDefinition>();
      File dir = new File(getPath());
      for (File file : dir.listFiles(new FileFilter() {

         public boolean accept(File pathname) {
            if (pathname.getName().lastIndexOf(".xml") > 0) {
               return true;
            }
            return false;
         }
      })) {
         definitionHashMap.putAll(parse(file));
      }
      return definitionHashMap;
   }

   /**
    * Parses the.
    * 
    * @param file the file
    * 
    * @return the map< string, protocol definition>
    */
   private Map<String, ProtocolDefinition> parse(File file) {
      Map<String, ProtocolDefinition> map = new HashMap<String, ProtocolDefinition>();

      Document protocolDoc = readXmlFromFile(file);

      Element openremoteElement = protocolDoc.getRootElement();
      Iterator<Element> protocolItr = openremoteElement.elementIterator(PROTOCOL_ELEMENT_NAME);
      while (protocolItr.hasNext()) {
         Element protocolElement = protocolItr.next();
         ProtocolDefinition protocolDefinition = new ProtocolDefinition();
         // set protocol displayName
         protocolDefinition.setDisplayName(protocolElement.attributeValue(DISPLAY_NAME_ATTR_NAME));
         // set protocol tagName
         protocolDefinition.setTagName(protocolElement.attributeValue(TAG_NAME_ATTR_NAME));

         // parse attr element start
         protocolDefinition.getAttrs().addAll(parseAttributs(protocolElement));
         map.put(protocolDefinition.getDisplayName(), protocolDefinition);
      }
      return map;
   }

   /**
    * Parses the attributs.
    * 
    * @param protocol the protocol
    * 
    * @return the list< protocol attr definition>
    */
   private List<ProtocolAttrDefinition> parseAttributs(Element protocol) {
      List<ProtocolAttrDefinition> attrs = new ArrayList<ProtocolAttrDefinition>();
      Iterator<Element> attrItr = protocol.elementIterator(ATTR_ELEMENT_NAME);
      while (attrItr.hasNext()) {
         Element attr = attrItr.next();
         ProtocolAttrDefinition attrDefinition = new ProtocolAttrDefinition();
         attrDefinition.setLabel(attr.attributeValue(LABEL_ATTR_NAME));
         attrDefinition.setName(attr.attributeValue(NAME_ATTR_NAME));
         attrDefinition.setTooltipMessage(attr.attributeValue(TOOLTIP_MESSAGE_ATTR_NAME));
         attrDefinition.setValue(attr.attributeValue(VALUE_ATTR_NAME));
         String options = attr.attributeValue(OPTIONS_ATTR_NAME);
         if (options != null && !"".endsWith(options)) {
            String[] optionArray = options.split(",");
            attrDefinition.setOptions(Arrays.asList(optionArray));
         }
         Element validationsElement = attr.element(VALIDATIONS_ElEMENT_NAME);

         // parse validators start
         attrDefinition.getValidators().addAll(parseValidators(validationsElement));

         attrs.add(attrDefinition);

      }

      return attrs;
   }

   /**
    * Parses the validators.
    * 
    * @param validationsElement the validations element
    * 
    * @return the list< protocol validator>
    */
   private List<ProtocolValidator> parseValidators(Element validationsElement) {
      List<ProtocolValidator> validators = new ArrayList<ProtocolValidator>();

      for (Iterator<Element> validationsItr = validationsElement.elementIterator(); validationsItr.hasNext();) {
         Element validatorElement = validationsItr.next();
         if (getValidatorType(validatorElement.getName()) == -1) {
            throw new ParseProtocolException("Can't find validator " + validatorElement.getName());
         }
         ProtocolValidator protocolValidator = new ProtocolValidator(getValidatorType(validatorElement.getName()),
               validatorElement.getTextTrim(), validatorElement.attributeValue(MESSAGE_ATTR_NAME));
         validators.add(protocolValidator);
      }
      return validators;
   }

   /**
    * Gets the validator type.
    * 
    * @param elementName the element name
    * 
    * @return the validator type
    */
   private int getValidatorType(String elementName) {
      if (ProtocolValidator.ALLOW_BLANK.equals(elementName)) {
         return ProtocolValidator.ALLOW_BLANK_TYPE;
      } else if (ProtocolValidator.MAX_LENGTH.equals(elementName)) {
         return ProtocolValidator.MAX_LENGTH_TYPE;
      } else if (ProtocolValidator.MAX_LENGTH.equals(elementName)) {
         return ProtocolValidator.MAX_LENGTH_TYPE;
      } else if (ProtocolValidator.REGEX.equals(elementName)) {
         return ProtocolValidator.REGEX_TYPE;
      } else {
         return -1;
      }
   }

   /**
    * Read xml from file.
    * 
    * @param file the file
    * 
    * @return the document
    */
   private Document readXmlFromFile(File file) {
      Document protocolDoc = null;
      SAXReader reader = new SAXReader();
      XMLErrorHandler errorHandler = new XMLErrorHandler();
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setValidating(true);
      factory.setNamespaceAware(true);
      try {
         protocolDoc = reader.read(file);
         SAXParser parser = factory.newSAXParser();
         parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
               "http://www.w3.org/2001/XMLSchema");
         parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", "file:"
               + this.getClass().getResource("/").getPath().toString() + PROTOCOL_XSD_FILE_NAME);
         SAXValidator validator = new SAXValidator(parser.getXMLReader());
         validator.setErrorHandler(errorHandler);

         validator.validate(protocolDoc);

         XMLWriter writer = new XMLWriter(OutputFormat.createPrettyPrint());

         if (errorHandler.getErrors().hasContent()) {
            writer.write(errorHandler.getErrors());
            throw new ParseProtocolException("validate xml schema on File " + file.getAbsolutePath() + " fail.");
         }
      } catch (ParserConfigurationException e) {
         throw new ParseProtocolException("Read xml From File " + file.getAbsolutePath()
               + " occur ParserConfigurationException.", e);
      } catch (SAXException e) {
         throw new ParseProtocolException("Read xml From File " + file.getAbsolutePath() + " occur SAXException.", e);
      } catch (UnsupportedEncodingException e) {
         throw new ParseProtocolException("Read xml From File " + file.getAbsolutePath()
               + " occur UnsupportedEncodingException.", e);
      } catch (IOException e) {
         throw new ParseProtocolException("Read xml From File " + file.getAbsolutePath() + " occur IOException.", e);
      } catch (DocumentException e) {
         throw new ParseProtocolException("Read xml From File " + file.getAbsolutePath() + " occur DocumentException.",
               e);
      }
      return protocolDoc;
   }

   /**
    * Gets the path.
    * 
    * @return the path
    */
   private String getPath() {
      if (xmlPath != null && xmlPath.length() > 0) {
         return xmlPath;
      } else {
         return this.getClass().getResource(PROTOCOLS_DIR).getPath();

      }

   }

   /**
    * Sets the path.
    * 
    * @param path the new path
    */
   public void setPath(String path) {
      xmlPath = path;
   }
}
