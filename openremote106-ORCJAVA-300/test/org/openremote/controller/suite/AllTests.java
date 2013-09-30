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
package org.openremote.controller.suite;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.openremote.controller.deployer.Version20ModelBuilderTest;
import org.openremote.controller.net.MulticastAutoDiscoveryTest;
import org.openremote.controller.utils.MacrosIrDelayUtilTest;
import org.openremote.controller.utils.PathUtil;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.model.PanelTest;
import org.openremote.controller.spring.SpringContext;
import org.openremote.controller.Constants;
import org.openremote.controller.ControllerConfiguration;
import org.w3c.dom.Document;
import org.jdom.input.DOMBuilder;

/**
 * Collects *all* unit tests. Also, the implementation contains utility methods in common across
 * various test suites.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
@RunWith(Suite.class) @SuiteClasses(
{
   AllServiceTests.class,
   MacrosIrDelayUtilTest.class,
   AllCommandBuildersTests.class,
   MulticastAutoDiscoveryTest.class,
   RoundRobinTests.class,
   UtilTests.class,
    
   RESTTests.class,
   KNXTests.class,
   VirtualProtocolTests.class,
   PanelTest.class,
   AsyncEventTests.class,
   ExceptionTests.class,
   ComponentTests.class,
   SensorTests.class,
   EventTests.class,
   StatusCacheTests.class,
   ProtocolTests.class,
   BusTests.class,
   DSCIT100Tests.class,
   Version20ModelBuilderTest.class
})


public class AllTests
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * Currently used test port for the embedded container hosting HTTP/REST implementation
   */
  public final static int WEBAPP_PORT = 8090;

  /**
   * Localhost IP address.
   */
  public final static String LOCALHOST = "127.0.0.1";

  /**
   * Path to test fixture directory. This is relevant when at the top level of classes directory.
   */
  public final static String FIXTURE_DIR = "org/openremote/controller/fixture/";

  /**
   * Path to polling related fixture files.
   */
  public final static String POLLING_FIXTURES = FIXTURE_DIR + "polling/";


  // Class Members --------------------------------------------------------------------------------



  private static boolean hasContext = false;

  @BeforeClass public synchronized static void initServiceContext()
  {
    if (hasContext)
      return;

    try
    {
      new SpringContext();

      hasContext = true;
    }

    catch (Throwable t)
    {
      throw new Error("Failed to run tests : " + t.getMessage());
    }
  }


  private final static Logger log = Logger.getLogger("OpenRemote.Controller.UnitTest");


  // Path Helpers ---------------------------------------------------------------------------------

  /**
   * Returns the absolute directory path to the root directory of test fixture files. The location
   * where fixture files are searched from is set by the (Ant) build script with its
   * ${classes.dir} property. The value is passed to the jUnit VM from the build script. <p>
   *
   * Note that the value of the <tt>classes.dir</tt> JVM property should be set to the root
   * of the compiled test classes directory -- this is then resolved to point to a subdirectory
   * defined by {@link #FIXTURE_DIR} constant.
   *
   * @return  absolute file URI to the root directory of test fixture files
   */
  public static URI getAbsoluteFixturePath()
  {
    String relativeFixturePath = System.getProperty("classes.dir");

    Assert.assertNotNull(
        "Could not find ${classes.dir} absolute path which points to compiled test directory " +
        "in system properties. Please ensure the unit test JVM is started with " +
        "'-Dclasses.dir=<absolute path to compiled test directory with fixture files>' " +
        "or '<sysproperty name = 'classes.dir' file = '${classes.dir}/>' is included " +
        "in the Ant build script which executes the <junit> tasks.",
        relativeFixturePath
    );
    
    return new File(relativeFixturePath).toURI().resolve(FIXTURE_DIR);
  }


  // XML Parser Utilities -------------------------------------------------------------------------

  /**
   * Builds a *JDOM* Document from a file.
   *
   * @param f     file name path
   *
   * @return  JDOM document instance
   *
   * @throws Exception    in case there's an error
   */
  public static org.jdom.Document getJDOMDocument(File f) throws Exception
  {
    return new DOMBuilder().build(getDOMDocument(f));
  }

  /**
   * Builds a *DOM* document from a file.
   *
   * @param f   file name path
   *
   * @return  DOM document instance
   *
   * @throws Exception    in case there's an error
   */
  public static Document getDOMDocument(File f) throws Exception
  {
    return getDOMDocument(new FileInputStream(f));
  }

  /**
   * Builds a *DOM* document from a I/O stream
   *
   * @param in    input stream
   *
   * @return  DOM document instance
   *
   * @throws Exception    in case there's an error
   */
  public static Document getDOMDocument(InputStream in) throws Exception
  {
    BufferedInputStream bin = new BufferedInputStream(in);

    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder parser = domFactory.newDocumentBuilder();

    return parser.parse(bin);
  }



  // Container Controller.xml Utilities -----------------------------------------------------------



  /**
   * Replaces the controller.xml of the *test container* with a file from the test fixture
   * directory structure. <p>
   *
   * The location of test containers resource files (where controller.xml is) is resolved
   * by setting a <tt>testcontainer.vm.resource.path</tt> system property for this unit test
   * JVM. This is done by Ant build script which will read the deployed controller web app's
   * config.properties file and resolve the <tt>resource.path</tt> property from within.
   *
   * @param filename
   */
  public static void replaceTestContainerControllerXML(String filename)
  {
    URI fixtureFile = getAbsoluteFixturePath().resolve(filename);

    String testContainerConfigResourcePath = System.getProperty("testcontainer.vm.resource.path");

    Assert.assertFalse("got " + testContainerConfigResourcePath,
                       testContainerConfigResourcePath == null ||
                       testContainerConfigResourcePath.equals("")
    );

    URI testContainerControllerXML = new File(testContainerConfigResourcePath).toURI()
        .resolve(Constants.CONTROLLER_XML);

    log.info(
        "Replacing Test Container ''{0}'' with ''{1}''",
        testContainerConfigResourcePath, fixtureFile
    );

    copyFile(fixtureFile.getPath(), testContainerControllerXML.getPath());
  }




  // Container Panel.xml Utilities ----------------------------------------------------------------

  public static void restorePanelXML()
  {
    String panelXML = AllTests.getFixtureFile(Constants.PANEL_XML);

    if (new File(panelXML + ".bak").exists())
    {
       new File(panelXML + ".bak").renameTo(new File(panelXML));
    }
  }


  public static void replacePanelXML(String filename)
  {
    String fixtureFile = AllTests.getFixtureFile(filename);

    String panelXML = getPanelXML();

    if (new File(panelXML).exists())
    {
       new File(panelXML).renameTo(new File(panelXML + ".bak"));
    }

    copyFile(fixtureFile, panelXML);
  }


  private static String getPanelXML()
  {
    return PathUtil.addSlashSuffix(
        ControllerConfiguration.readXML().getResourcePath()) +
        Constants.PANEL_XML;
  }


  // File Helpers ---------------------------------------------------------------------------------


  /**
   * Returns a path to a fixture (resource) file used by tests. Test fixtures are stored in their
   * own (fixed) location in the test directories. This method resolves the path to a given file
   * name in the directory.
   *
   * @param name    name of the fixture file (without path)
   *
   * @return        full path to the fixture file
   *
   * @throws  AssertionError   if the file is not found
   */
  public static String getFixtureFile(String name)
  {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();

    String resource = FIXTURE_DIR + name;

    Assert.assertNotNull("Got null resource from '" + resource + "'.", cl.getResource(resource));

    return cl.getResource(resource).getFile();
  }


  private static void copyFile(String src, String dest)
  {
    File inputFile = new File(src);
    File outputFile = new File(dest);

    FileReader in;

    try
    {
      in = new FileReader(inputFile);

      if (!outputFile.getParentFile().exists())
      {
        outputFile.getParentFile().mkdirs();
      }

      if (!outputFile.exists())
      {
        outputFile.createNewFile();
      }

      FileWriter out = new FileWriter(outputFile);

      int c;

      while ((c = in.read()) != -1)
      {
        out.write(c);
      }

      in.close();
      out.close();
    }

    catch (IOException e)
    {
      e.printStackTrace();
    }
  }




}
