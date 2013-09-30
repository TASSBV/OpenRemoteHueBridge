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
import org.openremote.controller.exception.XMLParsingException;
import org.openremote.controller.exception.ConfigurationException;
import org.openremote.controller.utils.SpringTestContext;

/**
 * TODO :
 *
 *   see ORCJAVA-148 (http://jira.openremote.org/browse/ORCJAVA-148)
 *
 * @author Javen
 */
public class LabelBuilderTest
{
  private Document doc = null;
  private LabelBuilder builder = (LabelBuilder) SpringTestContext.getInstance().getBean("labelBuilder");


  // Test Lifecycle -------------------------------------------------------------------------------

  @Before public void setUp() throws Exception
  {
    String controllerXMLPath = this.getClass().getClassLoader().getResource(AllTests.FIXTURE_DIR + "controller.xml").getFile();
    SAXBuilder builder = new SAXBuilder();
    doc = builder.build(controllerXMLPath);
  }


  // Tests ----------------------------------------------------------------------------------------

  @Test public void testGetLabelforRealID() throws Exception
  {
    Label label = getLabelByID("6");
    Assert.assertNotNull(label);
  }

//  @Test public void testGetLabelforInvalidLabel() throws Exception
//  {
//    try
//    {
//       getLabelByID("8");
//       fail();
//    }
//
//    catch (XMLParsingException e)
//    {
//
//    }
//  }
//
//  @Test public void testGetLabelforNoSuchID() throws Exception
//  {
//    try
//    {
//       getLabelByID("200");
//       fail();
//    }
//
//    catch (XMLParsingException e)
//    {
//
//    }
//  }
//

  // Helpers --------------------------------------------------------------------------------------

  private Label getLabelByID(String labelID) throws XMLParsingException, JDOMException, ConfigurationException
  {
    Element controlElement = getElementByID(labelID);

    Assert.assertTrue(
        "Was expecting 'label', got '" + controlElement.getName() + "'",
        "label".equalsIgnoreCase(controlElement.getName())
    );

//    if (!"label".equalsIgnoreCase(controlElement.getName()))
//    {
//       throw new NoSuchComponentException("Invalid Label.");
//    }

    return (Label) builder.build(controlElement, "test");
  }

  private Element getElementByID(String id) throws JDOMException
  {
     String xpath = "//" + Constants.OPENREMOTE_NAMESPACE + ":*[@id='" + id + "']";
     XPath xPath = XPath.newInstance(xpath);
     xPath.addNamespace(Constants.OPENREMOTE_NAMESPACE, Constants.OPENREMOTE_WEBSITE);
     List<Element> elements = xPath.selectNodes(doc);

     if (elements.size() > 1)
     {
        throw new RuntimeException("duplicated id :" + id);
     }

    Assert.assertTrue(
        "Was expecting more than zero elements",
        elements.size() != 0
    );

//     else if (elements.size() == 0)
//     {
//        throw new NoSuchComponentException("No such component id " + id);
//     }

     return elements.get(0);
  }

}
