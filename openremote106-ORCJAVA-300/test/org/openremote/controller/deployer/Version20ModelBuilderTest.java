/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.controller.deployer;

import java.io.File;

import junit.framework.Assert;
import org.jdom.Document;
import org.junit.Test;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.command.CommandFactory;
import org.openremote.controller.exception.ControllerDefinitionNotFoundException;
import org.openremote.controller.exception.XMLParsingException;
import org.openremote.controller.model.xml.Version20SensorBuilder;
import org.openremote.controller.service.DeployerTest;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.suite.AllTests;

/**
 * Basic unit tests for {@link org.openremote.controller.deployer.Version20ModelBuilder} class.  <p>
 *
 * TODO: ORCJAVA-285 -- complete the unit tests of this class
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Version20ModelBuilderTest
{
  /**
   * TODO : ORCJAVA-283
   *
   * @throws Exception
   */
  @Test public void testGetControllerDefinitionFileEmptyString() throws Exception
  {
    ControllerConfiguration cc = new ControllerConfiguration();
    cc.setResourcePath("");

    File f = Version20ModelBuilder.getControllerDefinitionFile(cc);

    Assert.fail("ORCJAVA-283 : Indicate configuration error with empty resource path configuration");
  }

  /**
   * TODO : ORCJAVA-283
   *
   * @throws Exception
   */
  @Test public void testGetControllerDefinitionFileNull() throws Exception
  {
    ControllerConfiguration cc = new ControllerConfiguration();

    try
    {
      cc.setResourcePath(null);
    }
    catch (Throwable t)
    {
      Assert.fail("ORCJAVA-283: disallow null arg on cc.setResourcePath()");
    }

    File f = Version20ModelBuilder.getControllerDefinitionFile(cc);
  }

  /**
   * Tests spaces in config parameter.
   *
   * @throws Exception if test fails for any reason
   */
  @Test public void testGetControllerDefinitionFileWhiteSpace() throws Exception
  {
    ControllerConfiguration cc = new ControllerConfiguration();
    cc.setResourcePath("/My Path");

    File f = Version20ModelBuilder.getControllerDefinitionFile(cc);

    Assert.assertTrue(
        "Expected '/My Path/" + Version20ModelBuilder.CONTROLLER_XML + "', got : '" + f.toString() + "'",
        f.toString().equals("/My Path/" + Version20ModelBuilder.CONTROLLER_XML)
    );
  }

  /**
   * Codifying the current implementation behavior -- if URI is entered, it is not decoded to
   * file path format. It might be desirable to modify this behavior at some point.
   *
   * @throws Exception  if test fails
   */
  @Test public void testGetControllerDefinitionFileURIEncoding() throws Exception
  {
    ControllerConfiguration cc = new ControllerConfiguration();
    cc.setResourcePath("/Test%20Path");

    File f = Version20ModelBuilder.getControllerDefinitionFile(cc);

    Assert.assertTrue(
        "Expected '/Test%20Path/" + Version20ModelBuilder.CONTROLLER_XML + "', got : '" + f.toString() + "'",
        f.toString().equals("/Test%20Path/" + Version20ModelBuilder.CONTROLLER_XML)
    );
  }

  /**
   * Codifying the current implementation behavior -- if URL is entered, it is not decoded to
   * file path format. It might be desirable to modify this behavior at some point.
   *
   * @throws Exception if test fails
   */
  @Test public void testGetControllerDefinitionFileURLEncoding() throws Exception
  {
    ControllerConfiguration cc = new ControllerConfiguration();
    cc.setResourcePath("/Test+Path");

    File f = Version20ModelBuilder.getControllerDefinitionFile(cc);

    Assert.assertTrue(
        "Expected '/Test+Path/" + Version20ModelBuilder.CONTROLLER_XML + "', got : '" + f.toString() + "'",
        f.toString().equals("/Test+Path/" + Version20ModelBuilder.CONTROLLER_XML)
    );
  }


  /**
   * Basic controller.xml read test.
   *
   * @throws Exception if test fails
   */
  @Test public void testReadControllerXMLDocument() throws Exception
  {
    String resourcePath = AllTests.getAbsoluteFixturePath()
        .resolve("deployment/version20/example1/").toString();

    ControllerConfiguration config = new ControllerConfiguration();
    config.setResourcePath(resourcePath);

    CommandFactory commandFactory = DeployerTest.createCommandFactory();

    Version20ModelBuilder builder = new Version20ModelBuilder(
        new StatusCache(),
        config,
        new Version20SensorBuilder(),
        new Version20CommandBuilder(commandFactory),
        commandFactory
    );

    Document doc = builder.readControllerXMLDocument();

    Assert.assertTrue(doc != null);

    Assert.assertTrue(doc.getRootElement().getName().equals("openremote"));
    Assert.assertTrue(doc.getRootElement().getNamespaceURI().equals("http://www.openremote.org"));

    Assert.assertTrue(doc.getRootElement().getChild("components", doc.getRootElement().getNamespace()) != null);
    Assert.assertTrue(doc.getRootElement().getChild("sensors", doc.getRootElement().getNamespace()) != null);
    Assert.assertTrue(doc.getRootElement().getChild("commands", doc.getRootElement().getNamespace()) != null);
    Assert.assertTrue(doc.getRootElement().getChild("config", doc.getRootElement().getNamespace()) != null);
  }

  /**
   * Attempt to read controller xml from a non-existent location.
   *
   * @throws Exception if test fails
   */
  @Test public void testReadControllerXMLDocumentWhichDoesntExist() throws Exception
  {
    String resourcePath = AllTests.getAbsoluteFixturePath()
        .resolve("deployment/version20/doesnotexist/").toString();

    ControllerConfiguration config = new ControllerConfiguration();
    config.setResourcePath(resourcePath);

    CommandFactory commandFactory = DeployerTest.createCommandFactory();

    Version20ModelBuilder builder = new Version20ModelBuilder(
        new StatusCache(),
        config,
        new Version20SensorBuilder(),
        new Version20CommandBuilder(commandFactory),
        commandFactory
    );

    try
    {
      Document doc = builder.readControllerXMLDocument();

      Assert.fail("Was expecting ControllerDefinitionNotFoundException...");
    }

    catch (ControllerDefinitionNotFoundException e)
    {
      // expected...
    }
  }


  /**
   * Attempt to read controller xml from a path location containing spaces.
   *
   * @throws Exception if test fails
   */
  @Test public void testReadControllerXMLDocumentFromPathWithSpace() throws Exception
  {
    String resourcePath = AllTests.getAbsoluteFixturePath()
        .resolve("deployment/version20/white%20space/").toString();

    ControllerConfiguration config = new ControllerConfiguration();
    config.setResourcePath(resourcePath);

    CommandFactory commandFactory = DeployerTest.createCommandFactory();

    Version20ModelBuilder builder = new Version20ModelBuilder(
        new StatusCache(),
        config,
        new Version20SensorBuilder(),
        new Version20CommandBuilder(commandFactory),
        commandFactory
    );

      Document doc = builder.readControllerXMLDocument();

    Assert.assertTrue(doc != null);

    Assert.assertTrue(doc.getRootElement().getName().equals("openremote"));
    Assert.assertTrue(doc.getRootElement().getNamespaceURI().equals("http://www.openremote.org"));

    Assert.assertTrue(doc.getRootElement().getChild("components", doc.getRootElement().getNamespace()) != null);
    Assert.assertTrue(doc.getRootElement().getChild("sensors", doc.getRootElement().getNamespace()) != null);
    Assert.assertTrue(doc.getRootElement().getChild("commands", doc.getRootElement().getNamespace()) != null);
    Assert.assertTrue(doc.getRootElement().getChild("config", doc.getRootElement().getNamespace()) != null);
  }


  /**
   * Attempt to read controller xml from a path location containing spaces.
   *
   * @throws Exception if test fails
   */
  @Test public void testReadControllerXMLDocumentFromPathWithSpaceNonURI() throws Exception
  {
    File resourcePath = new File(AllTests.getAbsoluteFixturePath());

    // legacy style...

    resourcePath = new File(resourcePath, "deployment/version20/white space/");

    ControllerConfiguration config = new ControllerConfiguration();
    config.setResourcePath(resourcePath.toString());

    CommandFactory commandFactory = DeployerTest.createCommandFactory();

    Version20ModelBuilder builder = new Version20ModelBuilder(
        new StatusCache(),
        config,
        new Version20SensorBuilder(),
        new Version20CommandBuilder(commandFactory),
        commandFactory
    );

    Document doc = builder.readControllerXMLDocument();

    Assert.assertTrue(doc != null);

    Assert.assertTrue(doc.getRootElement().getName().equals("openremote"));
    Assert.assertTrue(doc.getRootElement().getNamespaceURI().equals("http://www.openremote.org"));

    Assert.assertTrue(doc.getRootElement().getChild("components", doc.getRootElement().getNamespace()) != null);
    Assert.assertTrue(doc.getRootElement().getChild("sensors", doc.getRootElement().getNamespace()) != null);
    Assert.assertTrue(doc.getRootElement().getChild("commands", doc.getRootElement().getNamespace()) != null);
    Assert.assertTrue(doc.getRootElement().getChild("config", doc.getRootElement().getNamespace()) != null);
  }

  /**
   * Attempt to read controller xml which used invalid schema
   *
   * @throws Exception if test fails
   */
  @Test public void testReadControllerXMLDocumentWithInvalidSchema() throws Exception
  {
    File resourcePath = new File(AllTests.getAbsoluteFixturePath());

    // legacy style...

    resourcePath = new File(resourcePath, "deployment/version20/invalidschema/");

    ControllerConfiguration config = new ControllerConfiguration();
    config.setResourcePath(resourcePath.toString());

    CommandFactory commandFactory = DeployerTest.createCommandFactory();

    Version20ModelBuilder builder = new Version20ModelBuilder(
        new StatusCache(),
        config,
        new Version20SensorBuilder(),
        new Version20CommandBuilder(commandFactory),
        commandFactory
    );

    try
    {
      Document doc = builder.readControllerXMLDocument();

      Assert.fail("was expecting XMLParsingException...");
    }

    catch (XMLParsingException e)
    {
      // expected...
    }
  }



}



