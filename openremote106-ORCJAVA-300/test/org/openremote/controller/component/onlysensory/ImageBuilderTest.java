/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.component.onlysensory;

import static org.junit.Assert.fail;

import java.util.List;

import junit.framework.Assert;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.Constants;
import org.openremote.controller.suite.AllTests;
import org.openremote.controller.component.onlysensory.Image;
import org.openremote.controller.component.onlysensory.ImageBuilder;
import org.openremote.controller.exception.NoSuchComponentException;
import org.openremote.controller.exception.XMLParsingException;
import org.openremote.controller.exception.ConfigurationException;
import org.openremote.controller.utils.SpringTestContext;
/**
 * 
 * @author Handy
 *
 */
public class ImageBuilderTest {
   private String controllerXMLPath = null;
   private Document doc = null;
   private ImageBuilder builder = (ImageBuilder) SpringTestContext.getInstance().getBean("imageBuilder");
   @Before
   public void setUp() throws Exception {
      controllerXMLPath = this.getClass().getClassLoader().getResource(AllTests.FIXTURE_DIR + "controller.xml").getFile();
      SAXBuilder builder = new SAXBuilder();
      doc = builder.build(controllerXMLPath);
   }

   @SuppressWarnings("unchecked")
   protected Element getElementByID(String id) throws JDOMException {
      String xpath = "//" + Constants.OPENREMOTE_NAMESPACE + ":*[@id='" + id + "']";
      XPath xPath = XPath.newInstance(xpath);
      xPath.addNamespace(Constants.OPENREMOTE_NAMESPACE, Constants.OPENREMOTE_WEBSITE);
      List<Element> elements = xPath.selectNodes(doc);
      if (elements.size() > 1) {
         throw new RuntimeException("duplicated id :" + id);
      } else if (elements.size() == 0) {
         throw new NoSuchComponentException("No such component id " + id);
      }
      return elements.get(0);
   }
   @Test
   public void testGetImageforRealID() throws Exception{
      Image image = getImageByID("990");
      Assert.assertNotNull(image);
   }
   @Test
   public void testGetImageforInvalidImage() throws Exception{
      try{
         getImageByID("8");
         fail();
      } catch (XMLParsingException e){
      }
   }
   @Test
   public void testGetImageforNoSuchID() throws Exception {
      try {
         getImageByID("200");
         fail();
      } catch (XMLParsingException e) {
      }
   }
   
   private Image getImageByID(String imageID) throws Exception {
      Element controlElement = getElementByID(imageID);
      if (!"image".equalsIgnoreCase(controlElement.getName())) {
         throw new NoSuchComponentException("Invalid Image.");
      }
      return (Image) builder.build(controlElement, "test");
   }
   
}
