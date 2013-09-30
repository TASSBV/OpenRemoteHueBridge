/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.beehive.api.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.openremote.beehive.Configuration;
import org.openremote.beehive.Constant;
import org.openremote.beehive.api.service.AccountService;
import org.openremote.beehive.api.service.ResourceService;
import org.openremote.beehive.domain.User;
import org.openremote.beehive.exception.InvalidPanelXMLException;
import org.openremote.beehive.exception.NoSuchAccountException;
import org.openremote.beehive.exception.NoSuchPanelException;
import org.openremote.beehive.exception.PanelXMLNotFoundException;
import org.openremote.beehive.utils.PathUtil;
import org.openremote.beehive.utils.ZipUtil;

/**
 * 
 * Account resources service, such as openremote.zip etc.
 * 
 * @author javen, Dan
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 *
 */
public class ResourceServiceImpl implements ResourceService {
   
   private static final Logger logger = Logger.getLogger(ResourceService.class);
   
   private AccountService accountService;
   
   protected Configuration configuration;
   
   private static final String TABBAR_ELEMENT_NAME = "tabbar";

   @Override
   public boolean saveResource(long accountOid, InputStream input) {
      logger.debug("save resource from modeler to beehive");

      File dir = makeSureDir(accountOid);
      File zipFile = new File(dir, Constant.ACCOUNT_RESOURCE_ZIP_NAME);
      FileOutputStream fos = null;

      try {
         FileUtils.cleanDirectory(dir);
         fos = new FileOutputStream(zipFile);
         byte[] buffer = new byte[1024];
         int length = 0;

         while ((length = input.read(buffer)) != -1) {
            fos.write(buffer, 0, length);
         }
         fos.flush();
         logger.info("save resource success!");

         return true;
      } catch (IOException e) {
         logger.error("failed to save resource from modeler to beehive", e);
      } finally {
         ZipUtil.unzip(zipFile, dir.getAbsolutePath());
         if (fos != null) {
            try {
               fos.close();
            } catch (IOException ioException) {
               logger.warn("Error in closing file output stream to '" + Constant.ACCOUNT_RESOURCE_ZIP_NAME + "': "
                     + ioException.getMessage(), ioException);
            }
         }
      }
      return false;
   }
   
   public File getResourceZip(String username) {
      
      User user = accountService.loadByUsername(username);
      if (user == null) {
         return null;
      }
      File[] files = getDirByAccountOid(user.getAccount().getOid()).listFiles(new FilenameFilter() {

         @Override
         public boolean accept(File dir, String name) {
            return name.equalsIgnoreCase(Constant.ACCOUNT_RESOURCE_ZIP_NAME);
         }

      });
      if (files != null && files.length != 0) {
         return files[0];
      }
      return null;
   }

   
   private File getDirByAccountOid(long accountOid) {
      return new File(configuration.getModelerResourcesDir() + File.separator + accountOid);
   }
   
   private File makeSureDir(long accountOid) {
      File dir = getDirByAccountOid(accountOid);
      if (!dir.exists()) {
         dir.mkdirs();
      }
      return dir;
   }
   
   private boolean makeSurePanelXMLExist(long accountOid) {
      File dir = makeSureDir(accountOid);
      if (!new File(dir, Constant.PANEL_XML).exists()) {
         File zipFile = new File(dir, Constant.ACCOUNT_RESOURCE_ZIP_NAME);
         if (zipFile.exists()) {
            ZipUtil.unzip(zipFile, dir.getAbsolutePath(), Constant.PANEL_XML);
            return true;
         } else {
            return false;
         }
      }
      return true;
   }


   @Override
   public String getPanelXMLByPanelNameFromAccount(String username, String panelName) {
      long accountId = accountService.queryAccountIdByUsername(username);
      if (accountId == 0L) {
         throw new NoSuchAccountException();
      }
      String xmlPath = PathUtil.addSlashSuffix(makeSureDir(accountId).getAbsolutePath()) + Constant.PANEL_XML;
      if (!makeSurePanelXMLExist(accountId)) {
         throw new PanelXMLNotFoundException();
      }
      String decodedName = null;
      try {
         decodedName = URLDecoder.decode(panelName, "UTF-8");
      } catch (UnsupportedEncodingException e) {
         logger.error("can't decode panel name from percent escape : " + panelName);
      }
      Document doc = getProfileDocumentByPanelName(xmlPath, decodedName);
      return output(doc);
   }

   @Override
   public String getAllPanelsXMLFromAccount(String username) {
      long accountId = accountService.queryAccountIdByUsername(username);
      if (accountId == 0L) {
         throw new NoSuchAccountException();
      }
      String xmlPath = PathUtil.addSlashSuffix(makeSureDir(accountId).getAbsolutePath()) + Constant.PANEL_XML;
      if (!makeSurePanelXMLExist(accountId)) {
         throw new PanelXMLNotFoundException();
      }
      Document doc = getAllPanelsDocument(xmlPath);
      return output(doc);
   }
   

   public String getProfileByName(String panelXMLPath, String panelName) {
      Document doc = getProfileDocumentByPanelName(panelXMLPath, panelName);
      return output(doc);
   }

   private Document getAllPanelsDocument(String xmlPath) {
      Document doc = buildXML(xmlPath);
      Element root = (Element) doc.getRootElement();
      Element newRoot = new Element(root.getName());

      Document targetDocument = new Document(newRoot);
      setNamespace(root, newRoot);
      List<Element> panels = queryElementByElementName(doc, "panel");
      if (panels == null || panels.size() == 0) {
         throw new NoSuchPanelException("No panel in panel.xml");
      }
      for (Element tmpPanel : panels) {
         Element panel = (Element) tmpPanel.clone();
         panel.removeContent();
         newRoot.addContent(panel);
      }

      return targetDocument;
   }

   @SuppressWarnings("unchecked")
   private Document getProfileDocumentByPanelName(String xmlPath, String name) {
  
      
      Document doc = buildXML(xmlPath);
      Element root = (Element) doc.getRootElement();
      Element newRoot = new Element(root.getName());
      newRoot.setNamespace(root.getNamespace());

      Document targetDocument = new Document(newRoot);

      Element panel = queryPanelByName(xmlPath, name);
      if (panel == null) {
         throw new NoSuchPanelException("No such Panel :NAME = " + name);
      }
      
      //Begin add tabbar.
      Element globalTabBarEle = panel.getChild(TABBAR_ELEMENT_NAME, root.getNamespace());
      if (globalTabBarEle != null) {
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
      for (Element groupRef : refGroups) {
         String groupID = groupRef.getAttributeValue("ref");
         Element includeGroup = queryElementFromXMLById(xmlPath, groupID);
         if (null == includeGroup) {
            throw new InvalidPanelXMLException("Group reference ID = " + groupID + 
                  " not found in panel: " + name);
         }
         Element group = (Element) includeGroup.clone();
         //Begin local tabbar
         Element localTabBarEle = includeGroup.getChild(TABBAR_ELEMENT_NAME);
         if (localTabBarEle != null) {
            Element newLocalTabBarEle = (Element) localTabBarEle.clone();
            group.addContent(newLocalTabBarEle);
         }
         //End local tabbar
         groupsEle.addContent(group);
      }

      List<Element> groups = queryElementByElementName(targetDocument, "group");
      for (Element group : groups) {
         List<Element> refScreens = group.getChildren("include", root.getNamespace());
         for (Element refScreen : refScreens) {
            String screenID = refScreen.getAttributeValue("ref");
            Element screen = (Element) queryElementFromXMLById(xmlPath, screenID).clone();
            screensEle.addContent(screen);
         }
      }

      return targetDocument;
   }

   private String output(Document targetDocument) {
      Format format = Format.getPrettyFormat();
      format.setLineSeparator("\r\n");
      XMLOutputter out = new XMLOutputter(format);
      StringWriter writer = new StringWriter();
      try {
         out.output(targetDocument, writer);
      } catch (IOException e) {
         e.printStackTrace();
      }
      return writer.toString();
   }

   private void setNamespace(Element root, Element newRoot, Element... elements) {
      Namespace ns1 = Namespace.getNamespace("xsi", "htt//www.w3.org/2001/XMLSchema-instance");
      Namespace ns2 = Namespace.getNamespace("schemaLocation", "htt//www.openremote.org panel.xsd");
      newRoot.setNamespace(root.getNamespace());
      newRoot.addNamespaceDeclaration(ns1);
      newRoot.addNamespaceDeclaration(ns2);
      for (Element ele : elements) {
         ele.setNamespace(root.getNamespace());
      }
   }

   private Element queryElementFromXMLById(String xmlPath, String id) {
      return queryElementFromXML(xmlPath, "//" + Constant.OPENREMOTE_NAMESPACE + ":*[@id='" + id + "']");
   }

   private Element queryPanelByName(String xmlPath, String name) {
      return queryElementFromXML(xmlPath, "//" + Constant.OPENREMOTE_NAMESPACE + ":panel[@name=\"" + escapeQuotes(name)
            + "\"]");
   }

   private List<Element> queryElementByElementName(Document doc, String eleName) {
      return queryElementFromDocument(doc, "//" + Constant.OPENREMOTE_NAMESPACE + ":" + eleName);
   }

   @SuppressWarnings("unchecked")
   private List<Element> queryElementFromDocument(Document doc, String xPath) {

      try {
         XPath xpath = XPath.newInstance(xPath);
         xpath.addNamespace(Constant.OPENREMOTE_NAMESPACE, Constant.OPENREMOTE_WEBSITE);
         List<Element> elements = xpath.selectNodes(doc);
         return elements;
      } catch (JDOMException e) {
         throw new InvalidPanelXMLException("check the version of schema or structure of " + Constant.PANEL_XML
               + " with " + Constant.PANEL_XSD_PATH);
      }
   }

   private Document buildXML(String xmlPath) {
      SAXBuilder sb = new SAXBuilder();

      if (!new File(xmlPath).exists()) {
         throw new PanelXMLNotFoundException(" Make sure it's in " + xmlPath);
      }
      try {
         Document doc = sb.build(new File(xmlPath));
         return doc;
      } catch (JDOMException e) {
         throw new InvalidPanelXMLException(e.getMessage() +
               "check the version of schema or structure of panel.xml with its dtd or schema : ");
      } catch (IOException e) {
         String msg = " An I/O error prevents a " + Constant.PANEL_XML + " from being fully parsed";
         throw new PanelXMLNotFoundException(msg, e);
      }
   }

   private Element queryElementFromXML(String xmlPath, String xPath) {
      Document doc = buildXML(xmlPath);
      List<Element> results = queryElementFromDocument(doc, xPath);
      return results.size() > 0 ? results.get(0) : null;
   }
   
   private String escapeQuotes(String xpath) {
      return xpath.replaceAll("\"", "");
   }
   
   @Override
   public File getResource(String username, String fileName) throws FileNotFoundException {
      String filePath = PathUtil.addSlashSuffix(makeSureDir(accountService.queryAccountIdByUsername(username))
            .getAbsolutePath()) + fileName;
      File file = new File(filePath);
      if (file.exists()) {
         return file;
      } else {
         throw new FileNotFoundException(fileName + " not exist");
      }
   }

   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   public void setAccountService(AccountService accountService) {
      this.accountService = accountService;
   }

}
