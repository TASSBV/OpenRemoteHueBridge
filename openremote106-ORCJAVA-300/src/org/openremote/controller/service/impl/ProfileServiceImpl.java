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
package org.openremote.controller.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.Constants;
import org.openremote.controller.exception.InvalidPanelXMLException;
import org.openremote.controller.exception.NoSuchPanelException;
import org.openremote.controller.exception.PanelXMLNotFoundException;
import org.openremote.controller.service.ProfileService;
import org.openremote.controller.utils.PathUtil;

/**
 * Used to get panel.xml content according to panel identity.
 * 
 * @author Javen
 *
 */
public class ProfileServiceImpl implements ProfileService
{

  private static final String TABBAR_ELEMENT_NAME = "tabbar";

  private ControllerConfiguration configuration;

  @Override public String getProfileByPanelID(String panelID)
  {
    String xmlPath = PathUtil.addSlashSuffix(configuration.getResourcePath()) + Constants.PANEL_XML;
    return getProfileByPanelID(xmlPath, panelID);
  }

  @Override public String getProfileByPanelID(String panleXMLPath, String panelID)
  {
    Document doc = this.getProfileDocumentByPanelID(panleXMLPath, panelID);
    return output(doc);
  }

  @Override public String getAllPanels()
  {
    String xmlPath = PathUtil.addSlashSuffix(configuration.getResourcePath()) + Constants.PANEL_XML;
    Document doc = getAllPanelsDocument(xmlPath);
    return output(doc);
  }

  @Override public String getAllPanels(String panelXMLPath)
  {
    Document doc = getAllPanelsDocument(panelXMLPath);
    return output(doc);
  }

  @Override public String getPanelsXML(String xmlPath)
  {
    Document doc = getAllPanelsDocument(xmlPath);
    return output(doc);
  }

  @Override public String getProfileByPanelName(String panelName)
  {
    String xmlPath = PathUtil.addSlashSuffix(configuration.getResourcePath()) + Constants.PANEL_XML;
    Document doc = getProfileDocumentByPanelName(xmlPath, panelName);
    return output(doc);
  }

  @Override public String getProfileByName(String panelXMLPath, String panelName)
  {
    Document doc = getProfileDocumentByPanelName(panelXMLPath, panelName);
    return output(doc);
  }



  private Document getAllPanelsDocument(String xmlPath)
  {
    Document doc = buildXML(xmlPath);
    Element root = doc.getRootElement();
    Element newRoot = new Element(root.getName());

    Document targetDocument = new Document(newRoot);
    setNamespace(root, newRoot);
    List<Element> panels = queryElementByElementName(doc, "panel");

    if (panels == null || panels.size() == 0)
    {
      throw new NoSuchPanelException("No panel in panel.xml");
    }

    for (Element tmpPanel : panels)
    {
      Element panel = (Element) tmpPanel.clone();
      panel.removeContent();
      newRoot.addContent(panel);
    }

    return targetDocument;
  }


  @SuppressWarnings("unchecked")
  private Document getProfileDocumentByPanelID(String xmlPath, String panelID)
  {
    Document doc = buildXML(xmlPath);
    Element root = doc.getRootElement();
    Element newRoot = new Element(root.getName());
    newRoot.setNamespace(root.getNamespace());

    Document targetDocument = new Document(newRoot);
    Element screensEle = new Element("screens");
    Element groupsEle = new Element("groups");
    setNamespace(root, newRoot, screensEle, groupsEle);

    newRoot.addContent(screensEle);
    newRoot.addContent(groupsEle);

    Element panel = queryElementFromXMLById(xmlPath, panelID);

    if (panel == null)
    {
      throw new NoSuchPanelException("No such Panel :ID = " + panelID);
    }

    List<Element> refGroups = panel.getChildren("include", root.getNamespace());

    for (Element groupRef : refGroups)
    {
      String groupID = groupRef.getAttributeValue("ref");
      Element group = (Element) queryElementFromXMLById(xmlPath, groupID).clone();
      groupsEle.addContent(group);
    }

    List<Element> groups = queryElementByElementName(targetDocument, "group");

    for (Element group : groups)
    {
      List<Element> refScreens = group.getChildren("include", root.getNamespace());

      for (Element refScreen : refScreens)
      {
        String screenID = refScreen.getAttributeValue("ref");
        Element screen = (Element) queryElementFromXMLById(xmlPath, screenID).clone();
        screensEle.addContent(screen);
      }
    }

    return targetDocument;
  }

  @SuppressWarnings("unchecked")
  private Document getProfileDocumentByPanelName(String xmlPath, String name)
  {
    Document doc = buildXML(xmlPath);
    Element root = doc.getRootElement();
    Element newRoot = new Element(root.getName());
    newRoot.setNamespace(root.getNamespace());

    Document targetDocument = new Document(newRoot);

    Element panel = queryPanelByName(xmlPath, name);

    if (panel == null)
    {
      throw new NoSuchPanelException("No such Panel :NAME = " + name);
    }

    //Begin add tabbar.
    Element globalTabBarEle = panel.getChild(TABBAR_ELEMENT_NAME, root.getNamespace());

    if (globalTabBarEle != null)
    {
      Element cloneGlobalTabBarEle = (Element) globalTabBarEle.clone();
      cloneGlobalTabBarEle.setNamespace(root.getNamespace());
      newRoot.addContent(cloneGlobalTabBarEle);
    }
    //End

    Element screensEle = new Element("screens");
    Element groupsEle = new Element("groups");
    setNamespace(root, newRoot, screensEle, groupsEle);

    newRoot.addContent(screensEle);
    newRoot.addContent(groupsEle);

    List<Element> refGroups = panel.getChildren("include", root.getNamespace());

    for (Element groupRef : refGroups)
    {
      String groupID = groupRef.getAttributeValue("ref");
      Element includeGroup = queryElementFromXMLById(xmlPath, groupID);

      if (null == includeGroup)
      {
        throw new InvalidPanelXMLException(
            "Group reference ID = " + groupID + " not found in panel: " + name
        );
      }

      Element group = (Element) includeGroup.clone();

      //Begin local tabbar
      Element localTabBarEle = includeGroup.getChild(TABBAR_ELEMENT_NAME);

      if (localTabBarEle != null)
      {
        Element newLocalTabBarEle = (Element) localTabBarEle.clone();
        group.addContent(newLocalTabBarEle);
      }
      //End local tabbar

      groupsEle.addContent(group);
    }

    List<Element> groups = queryElementByElementName(targetDocument, "group");

    for (Element group : groups)
    {
      List<Element> refScreens = group.getChildren("include", root.getNamespace());

      for (Element refScreen : refScreens)
      {
        String screenID = refScreen.getAttributeValue("ref");
        Element screen = (Element) queryElementFromXMLById(xmlPath, screenID).clone();
        screensEle.addContent(screen);
      }
    }

    return targetDocument;
  }



  private String output(Document targetDocument)
  {
    Format format = Format.getPrettyFormat();
    format.setLineSeparator("\r\n");
    XMLOutputter out = new XMLOutputter(format);
    StringWriter writer = new StringWriter();

    try
    {
      out.output(targetDocument, writer);
    }

    catch (IOException e)
    {
      e.printStackTrace();
    }

    return writer.toString();
  }


  private void setNamespace(Element root, Element newRoot, Element... elements)
  {
    Namespace ns1 = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    Namespace ns2 = Namespace.getNamespace("schemaLocation", "http://www.openremote.org/panel.xsd");
    newRoot.setNamespace(root.getNamespace());
    newRoot.addNamespaceDeclaration(ns1);
    newRoot.addNamespaceDeclaration(ns2);

    for (Element ele : elements)
    {
      ele.setNamespace(root.getNamespace());
    }
  }


  private Element queryElementFromXMLById(String xmlPath, String id)
  {
    return queryElementFromXML(xmlPath, "//" + Constants.OPENREMOTE_NAMESPACE + ":*[@id='" + id + "']");
  }

  private Element queryPanelByName(String xmlPath, String name)
  {
    return queryElementFromXML(xmlPath, "//" + Constants.OPENREMOTE_NAMESPACE + ":panel[@name=\""
          + escapeQuotes(name) + "\"]");
  }

  private List<Element> queryElementByElementName(Document doc, String eleName)
  {
    return queryElementFromDocument(doc, "//" + Constants.OPENREMOTE_NAMESPACE + ":" + eleName);
  }

  @SuppressWarnings("unchecked")
  private List<Element> queryElementFromDocument(Document doc, String xPath)
  {
    try
    {
      XPath xpath = XPath.newInstance(xPath);
      xpath.addNamespace(Constants.OPENREMOTE_NAMESPACE, Constants.OPENREMOTE_WEBSITE);
      List<Element> elements = xpath.selectNodes(doc);
      return elements;
    }

    catch (JDOMException e)
    {
      throw new InvalidPanelXMLException(
          "check the version of schema or structure of " + Constants.PANEL_XML +
          " with " + Constants.PANEL_XSD_PATH
      );
    }
  }

  private Document buildXML(String xmlPath)
  {
    SAXBuilder sb = new SAXBuilder();

    if (!new File(xmlPath).exists())
    {
      throw new PanelXMLNotFoundException(" Make sure it's in " + xmlPath);
    }

    try
    {
      Document doc = sb.build(new File(xmlPath));
      return doc;
    }

    catch (JDOMException e)
    {
      throw new InvalidPanelXMLException(
           "check the version of schema or structure of panel.xml with its dtd or schema : " +
           e.getMessage(), e
      );
    }

    catch (IOException e)
    {
      String msg = " An I/O error prevents a " + Constants.PANEL_XML + " from being fully parsed";
      throw new PanelXMLNotFoundException(msg, e);
    }
  }


  private Element queryElementFromXML(String xmlPath, String xPath)
  {
    Document doc = buildXML(xmlPath);
    List<Element> results = queryElementFromDocument(doc, xPath);
    return results.size() > 0 ? results.get(0) : null;
  }

  public void setConfiguration(ControllerConfiguration configuration)
  {
    this.configuration = configuration;
  }

  private String escapeQuotes(String xpath)
  {
    return xpath.replaceAll("\"", "");
  }
}
