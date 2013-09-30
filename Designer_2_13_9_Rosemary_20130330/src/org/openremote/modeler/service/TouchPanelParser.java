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
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.exception.ParseTouchPanelException;
import org.openremote.modeler.touchpanel.TouchPanelCanvasDefinition;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;
import org.openremote.modeler.touchpanel.TouchPanelTabbarDefinition;
import org.xml.sax.SAXException;

/**
 * This class is for parsing the predefined touchPanel xml files from local path.
 */
@SuppressWarnings("unchecked")
public class TouchPanelParser {
   
   /** The Constant PANEL_DIR. */
   private static final String PANEL_DIR = "/touchpanels";
   
   /** The Constant PANEL_XSD_FILE_NAME. */
   private static final String PANEL_XSD_FILE_NAME = "touchpanel_bg.xsd";
   
   /** The Constant PANEL_ELEMENT_NAME. */
   private static final String PANEL_ELEMENT_NAME = "panel";
   
   /** The Constant TYPE_ATTR_NAME. */
   private static final String TYPE_ATTR_NAME = "type";
   
   /** The Constant NAME_ATTR_NAME. */
   private static final String NAME_ATTR_NAME = "name";
   
   /** The Constant BGIMAGE_ATTR_NAME. */
   private static final String BGIMAGE_ATTR_NAME = "bgImage";
   
   /** The Constant WIDTH_ATTR_NAME. */
   private static final String WIDTH_ATTR_NAME = "width";
   
   /** The Constant HEIGHT_ATTR_NAME. */
   private static final String HEIGHT_ATTR_NAME = "height";
   
   /** The Constant PADDINGLEFT_ATTR_NAME. */
   private static final String PADDINGLEFT_ATTR_NAME = "paddingLeft";
   
   /** The Constant PADDINGTOP_ATTR_NAME. */
   private static final String PADDINGTOP_ATTR_NAME = "paddingTop";
   
   private static final String CANVAS_ELEMENT_NAME = "canvas";
   
   private static final String TABBAR_ELEMENT_NAME = "tabbar";
   
   /** The xml path. */
   private String xmlPath;
   
   /**
    * Parses the xmls.
    * 
    * @return the hash map< string, list< panel definition>>
    */
   public HashMap<String, List<TouchPanelDefinition>> parseXmls() {
      HashMap<String, List<TouchPanelDefinition>> panelMap = new HashMap<String, List<TouchPanelDefinition>>();
      File dir = new File(getPath());
      for (File file : dir.listFiles(new FileFilter() {
         public boolean accept(File pathname) {
            if (pathname.getName().lastIndexOf(".xml") > 0) {
               return true;
            }
            return false;
         }
      })) {
         parse(panelMap, file);
      }
      return panelMap;
   }
   
   /**
    * Parses the.
    * 
    * @param panelMap the panel map
    * @param file the file
    */
   private void parse(Map<String, List<TouchPanelDefinition>> panelMap, File file) {
      List<TouchPanelDefinition> panelList = new ArrayList<TouchPanelDefinition>();
      
      Document panelDoc = readXmlFromFile(file);
      
      Element openremoteElement = panelDoc.getRootElement();
      Iterator<Element> panelItr = openremoteElement.elementIterator(PANEL_ELEMENT_NAME);
      while (panelItr.hasNext()) {
         Element panelElement = panelItr.next();
         String type = panelElement.attributeValue(TYPE_ATTR_NAME);
         if (panelMap.containsKey(type)) {
            panelList = panelMap.get(type);
         }
         TouchPanelDefinition panelDefinition = new TouchPanelDefinition();
         panelDefinition.setType(type);
         panelDefinition.setName(panelElement.attributeValue(NAME_ATTR_NAME));
         panelDefinition.setBgImage(panelElement.attributeValue(BGIMAGE_ATTR_NAME));
         panelDefinition.setWidth(Integer.valueOf(panelElement.attributeValue(WIDTH_ATTR_NAME)));
         panelDefinition.setHeight(Integer.valueOf(panelElement.attributeValue(HEIGHT_ATTR_NAME)));
         panelDefinition.setPaddingLeft(Integer.valueOf(panelElement.attributeValue(PADDINGLEFT_ATTR_NAME)));
         panelDefinition.setPaddingTop(Integer.valueOf(panelElement.attributeValue(PADDINGTOP_ATTR_NAME)));
         
         panelDefinition.setCanvas(parseGrid(panelElement));
         panelDefinition.setTabbarDefinition(parseTabbar(panelElement));
         panelList.add(panelDefinition);
         
         panelMap.put(type, panelList);
      }
   }
   
   /**
    * Parses the grid.
    * 
    * @param panelElement the panel element
    * 
    * @return the grid definition
    */
   private TouchPanelCanvasDefinition parseGrid(Element panelElement) {
      Element gridElement = panelElement.element(CANVAS_ELEMENT_NAME);
      TouchPanelCanvasDefinition grid = new TouchPanelCanvasDefinition(Integer.valueOf(gridElement.attributeValue(WIDTH_ATTR_NAME)), 
            Integer.valueOf(gridElement.attributeValue(HEIGHT_ATTR_NAME)));
      return grid;
   }
   
   private TouchPanelTabbarDefinition parseTabbar(Element panelElement) {
      Element tabbarEle = panelElement.element(TABBAR_ELEMENT_NAME);
      TouchPanelTabbarDefinition tchPanelDef = new TouchPanelTabbarDefinition();
      tchPanelDef.setHeight(Integer.valueOf(tabbarEle.attributeValue(HEIGHT_ATTR_NAME)));
      tchPanelDef.setBackground(new ImageSource(tabbarEle.attributeValue(BGIMAGE_ATTR_NAME)));
      return tchPanelDef; 
   }
   /**
    * Read xml from file.
    * 
    * @param file the file
    * 
    * @return the document
    */
   private Document readXmlFromFile(File file) {
      Document panelDoc = null;
      SAXReader reader = new SAXReader();
      XMLErrorHandler errorHandler = new XMLErrorHandler();
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setValidating(true);
      factory.setNamespaceAware(true);
      try {
         panelDoc = reader.read(file);
         SAXParser parser = factory.newSAXParser();
         parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
               "http://www.w3.org/2001/XMLSchema");
         parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", "file:"
               + this.getClass().getResource("/").getPath().toString() + PANEL_XSD_FILE_NAME);
         SAXValidator validator = new SAXValidator(parser.getXMLReader());
         validator.setErrorHandler(errorHandler);

         validator.validate(panelDoc);

         XMLWriter writer = new XMLWriter(OutputFormat.createPrettyPrint());

         if (errorHandler.getErrors().hasContent()) {
            writer.write(errorHandler.getErrors());
            throw new ParseTouchPanelException("validate xml schema on File " + file.getAbsolutePath() + " fail.");
         }
      } catch (ParserConfigurationException e) {
         throw new ParseTouchPanelException("Read xml From File " + file.getAbsolutePath()
               + " occur ParserConfigurationException.", e);
      } catch (SAXException e) {
         throw new ParseTouchPanelException("Read xml From File " + file.getAbsolutePath() + " occur SAXException.", e);
      } catch (UnsupportedEncodingException e) {
         throw new ParseTouchPanelException("Read xml From File " + file.getAbsolutePath()
               + " occur UnsupportedEncodingException.", e);
      } catch (IOException e) {
         throw new ParseTouchPanelException("Read xml From File " + file.getAbsolutePath() + " occur IOException.", e);
      } catch (DocumentException e) {
         throw new ParseTouchPanelException("Read xml From File " + file.getAbsolutePath()
               + " occur DocumentException.", e);
      }
      return panelDoc;
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
         return this.getClass().getResource(PANEL_DIR).getPath();

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
