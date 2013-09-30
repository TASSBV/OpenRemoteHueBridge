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
package org.openremote.controller.service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.net.URI;

import org.junit.Test;
import org.junit.Assert;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.deployer.Version20ModelBuilder;
import org.openremote.controller.deployer.ModelBuilder;
import org.openremote.controller.deployer.Version20CommandBuilder;
import org.openremote.controller.exception.ControllerDefinitionNotFoundException;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.command.CommandFactory;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.suite.AllTests;
import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.component.LevelSensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.virtual.VirtualCommandBuilder;
import org.openremote.controller.model.xml.Version20SensorBuilder;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.model.sensor.StateSensor;
import org.jdom.Element;

/**
 * Basic unit tests for {@link org.openremote.controller.service.Deployer Deployer} service.  <p>
 *
 * This test class also provides generic build methods to create deployer instances for other
 * tests -- in this way the use of deployer API is centralized and evolution of the API is easier
 * to manage in single location rather than spread across many test classes.
 *
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class DeployerTest
{

  private final static String deployerName = "Deployer for " + DeployerTest.class.getSimpleName();


  // Deployer Builder Methods ---------------------------------------------------------------------

  /**
   * Creates a deployer where the controller's configuration has been configured to a given
   * resource path URI (where controller artifacts are loaded from). Also uses a given command
   * factory configuration for the built deployer instead of the default one.
   *
   * @param resourcePath
   *            the URI to set for {@link ControllerConfiguration#setResourcePath}
   *
   * @param cf
   *            command factory configuration for the built deployer (command builder implementations)
   *
   * @return    deployer instance
   *
   * @throws InitializationException  if instantiation fails
   */
  public static Deployer createDeployer(URI resourcePath, CommandFactory cf)
      throws InitializationException
  {
    return createDeployer(resourcePath, cf, new Version20SensorBuilder(), new StatusCache());
  }

  /**
   * Creates a deployer with given dependant objects.
   *
   * @param resourcePath
   *            the URI to set for {@link ControllerConfiguration#setResourcePath}
   *
   * @param cf
   *            command factory configuration for the built deployer (command builder implementations)
   *
   * @param sensorBuilder
   *            a sensor builder instance to use with the built deployer
   *
   * @param cache
   *            a device state cache instance to use with the built deployer
   *
   * @return    deployer instance
   *
   * @throws InitializationException    if instantiation fails
   */
  public static Deployer createDeployer(URI resourcePath, CommandFactory cf,
                                        Version20SensorBuilder sensorBuilder,
                                        StatusCache cache)
      throws InitializationException
  {
    ControllerConfiguration config = new ControllerConfiguration();
    config.setResourcePath(resourcePath.getPath());

    return createDeployer("Deployment for " + resourcePath.getPath(), cache, config, cf, sensorBuilder);
  }


  /**
   * Creates a deployer with a given controller configuration. Other dependant objects are
   * using default implementations.
   *
   * @param config
   *            controller configuration
   *
   * @return    deployer instance
   *
   * @throws InitializationException    if initialization fails
   */
  public static Deployer createDeployer(ControllerConfiguration config)
      throws InitializationException
  {
    return createDeployer(
        "Deployer for " + config.getResourcePath(),
        new StatusCache(),
        config,
        createCommandFactory(),
        new Version20SensorBuilder()
    );
  }


  /**
   * Creates a deployer where the controller's configuration has been configured to a given
   * resource path URI (where controller artifacts are loaded from). Also uses a given sensor
   * builder instance for the deployer instead of the default one.
   *
   * @param resourcePath
   *            the URI to set for {@link ControllerConfiguration#setResourcePath}
   *
   * @param sensorBuilder
   *            a sensor builder instance to use with the built deployer instance
   *
   * @return    deployer instance
   *
   * @throws InitializationException    if instantiation fails
   */
  public static Deployer createDeployer(URI resourcePath, Version20SensorBuilder sensorBuilder)
      throws InitializationException
  {
    return createDeployer(resourcePath, createCommandFactory(), sensorBuilder, new StatusCache());
  }

  

  /**
   * Creates a deployer where the controller's configuration has been configured to a given
   * resource path URI (where controller artifacts are loaded from). Otherwise uses a default
   * configuration for cache, XML builders and a command builder configuration that includes
   * only virtual commands.
   *
   * @param resourcePath    the URI to set for {@link ControllerConfiguration#setResourcePath}
   *
   * @return                deployer instance
   *
   * @throws InitializationException    if instantiation fails
   */
  private static Deployer createDeployer(URI resourcePath) throws InitializationException
  {
    return createDeployer(resourcePath, createCommandFactory());
  }


  /**
   * Creates a deployer with a given name and default dependent objects.
   *
   * @param name    deployer name
   *
   * @return    deployer instance
   *
   * @throws InitializationException    if instantiation fails
   */
  private static Deployer createDeployer(String name) throws InitializationException
  {
    return createDeployer(name, new StatusCache());
  }

  /**
   * Creates a deployer with a given name and a given device state cache instance.
   *
   * @param name    deployer name
   * @param cache   device state cache instance to use with the built deployer
   *
   * @return        deployer instance
   *
   * @throws InitializationException    if instantiation fails
   */
  private static Deployer createDeployer(String name, StatusCache cache)
      throws InitializationException
  {
    return createDeployer(
        name, cache, new ControllerConfiguration(),
        createCommandFactory(), new Version20SensorBuilder()
    );
  }


  /**
   * Creates a deployer with given dependent objects.
   *
   * @param deployerName
   *            name of the deployer
   *
   * @param cache
   *            reference to device state cache to be used with the built deployer
   *
   * @param config
   *            controller's configuration
   *
   * @param cf
   *            command factory instance for the built deployer (command builders)
   *
   * @param sensorBuilder
   *            sensor builder instance for the built deployer
   *
   * @return    deployer instance
   *
   * @throws InitializationException    if initialization fails
   */
  private static Deployer createDeployer(String deployerName, StatusCache cache,
                                         ControllerConfiguration config,
                                         CommandFactory cf, Version20SensorBuilder sensorBuilder)
      throws InitializationException
  {
    sensorBuilder.setCommandFactory(cf);


    Version20ModelBuilder builder =
        new Version20ModelBuilder(cache, config, sensorBuilder, new Version20CommandBuilder(cf), cf);

    Map<String, ModelBuilder> modelBuilders = new HashMap<String, ModelBuilder>();
    modelBuilders.put(ModelBuilder.SchemaVersion.VERSION_2_0.toString(), builder);

    return new Deployer(deployerName, cache, config, null, modelBuilders);
  }


  /**
   * Creates a preconfigured command factory instance that includes virtual commands only.
   *
   * @return    command factory instance that has been configured with a builder for virtual
   *            commands
   */
  public static CommandFactory createCommandFactory()
  {
    Map<String, CommandBuilder> builders = new HashMap<String, CommandBuilder>();
    builders.put("virtual", new VirtualCommandBuilder());

    return new CommandFactory(builders);
  }


  // Constructor Tests ----------------------------------------------------------------------------


  /**
   * Test valid constructor execution.
   *
   * @throws Exception if test fails
   */
  @Test public void construction() throws Exception
  {
    StatusCache cache = new StatusCache();
    ControllerConfiguration config = new ControllerConfiguration();

    Map<String, ModelBuilder> map = new HashMap<String, ModelBuilder>();
    map.put(
        ModelBuilder.SchemaVersion.VERSION_2_0.toString(),
        new Version20ModelBuilder(
            cache, config,
            new Version20SensorBuilder(),
            new Version20CommandBuilder(new CommandFactory(null)),
            new CommandFactory(new HashMap<String, CommandBuilder>())
        )
    );

    new Deployer(deployerName, cache, config, null, map);
  }

  /**
   * Test invalid construction execution with null args.
   *
   * @throws Exception if test fails
   */
  @Test public void constructionNullArgs() throws Exception
  {
    try
    {
      new Deployer(null, null, null, null, null);

      Assert.fail("should not get here...");
    }

    catch (IllegalArgumentException e)
    {
      // expected...
    }
  }

  /**
   * Test invalid construction execution with null configuration.
   *
   * @throws Exception if test fails
   */
  @Test public void constructionNullArgs2() throws Exception
  {
    try
    {
      new Deployer(null, new StatusCache(), null, null, null);

      Assert.fail("should not get here...");
    }

    catch (IllegalArgumentException e)
    {
      // Expected...
    }
  }

  /**
   * Test invalid construction execution with null status cache.
   *
   * @throws Exception if test fails
   */
  @Test public void constructionNullArgs3() throws Exception
  {
    try
    {
      new Deployer(null, null, new ControllerConfiguration(), null, null);

      Assert.fail("should not get here...");
    }

    catch (IllegalArgumentException e)
    {
      // Expected...
    }
  }

  /**
   * Test construction with null name
   *
   * @throws Exception if test fails
   */
  public void constructionNullArgs4() throws Exception
  {
      new Deployer(null, new StatusCache(), new ControllerConfiguration(), null, null);
  }




  // GetSensor Tests ------------------------------------------------------------------------------

  /**
   *
   * @throws Exception  if test fails
   */
  @Test public void testGetSensor() throws Exception
  {
    StatusCache cache = new StatusCache();
    Deployer d = createDeployer(deployerName, cache);

    Sensor s1 = new SwitchSensor("Sensor 1", 1, cache, new TestEventListener());
    Sensor s2 = new RangeSensor("Sensor 2", 2, cache, new TestEventListener(), 0, 10);
    Sensor s3 = new LevelSensor("Sensor 3", 3, cache, new TestEventListener());
    Sensor s4 = new StateSensor("Sensor 4", 4, cache, new TestEventListener(),
                                new StateSensor.DistinctStates());

    cache.registerSensor(s1);
    cache.registerSensor(s2);
    cache.registerSensor(s3);
    cache.registerSensor(s4);


    Sensor sensor1 = d.getSensor(1);

    Assert.assertNotNull(sensor1);
    Assert.assertNotNull(sensor1.equals(s1));
    Assert.assertNotNull(s1.equals(sensor1));
    Assert.assertTrue(s1.getName().equals(sensor1.getName()));
    Assert.assertTrue(s1.getSensorID() == sensor1.getSensorID());
    Assert.assertTrue(sensor1.getSensorID() == 1);


    Sensor sensor2 = d.getSensor(2);

    Assert.assertNotNull(sensor2);
    Assert.assertNotNull(sensor2.equals(s2));
    Assert.assertNotNull(s2.equals(sensor2));
    Assert.assertTrue(s2.getName().equals(sensor2.getName()));
    Assert.assertTrue(s2.getSensorID() == sensor2.getSensorID());
    Assert.assertTrue(sensor2.getSensorID() == 2);


    Sensor sensor3 = d.getSensor(3);

    Assert.assertNotNull(sensor3);
    Assert.assertNotNull(sensor3.equals(s3));
    Assert.assertNotNull(s3.equals(sensor3));
    Assert.assertTrue(s3.getName().equals(sensor3.getName()));
    Assert.assertTrue(s3.getSensorID() == sensor3.getSensorID());
    Assert.assertTrue(sensor3.getSensorID() == 3);


    Sensor sensor4 = d.getSensor(4);

    Assert.assertNotNull(sensor4);
    Assert.assertNotNull(sensor4.equals(s4));
    Assert.assertNotNull(s4.equals(sensor4));
    Assert.assertTrue(s4.getName().equals(sensor4.getName()));
    Assert.assertTrue(s4.getSensorID() == sensor4.getSensorID());
    Assert.assertTrue(sensor4.getSensorID() == 4);
  }


  /**
   * Test retrieving sensor ID that is not registered with status cache. This currently
   * returns a null pointer.
   *
   * @throws Exception if test fails
   */
  @Test public void testGetSensorUnknownID() throws Exception
  {
    Deployer d = createDeployer(deployerName);

    Sensor s = d.getSensor(0);

    Assert.assertTrue(s == null);
  }



  // SoftRestart Tests ----------------------------------------------------------------------------


  /**
   * Use softRestart to load new controller definition once.
   *
   * @throws Exception if test fails
   */
  @Test public void testSoftRestart() throws Exception
  {
    URI deploymentURI = AllTests.getAbsoluteFixturePath().resolve("deployment/sensorsonly");

    Deployer d = createDeployer(deploymentURI);

    d.softRestart();

    Sensor sensor1 = d.getSensor(1);

    Assert.assertNotNull("got null sensor", sensor1);
    Assert.assertTrue(sensor1.getName().equals("Sensor 1"));
    Assert.assertTrue(sensor1.getSensorID() == 1);
    Assert.assertTrue(sensor1 instanceof SwitchSensor);
    Assert.assertTrue(sensor1.isRunning());


    Sensor sensor2 = d.getSensor(2);

    Assert.assertNotNull(sensor2);
    Assert.assertTrue(sensor2.getName().equals("Sensor 2"));
    Assert.assertTrue(sensor2.getSensorID() == 2);
    Assert.assertTrue(sensor2 instanceof LevelSensor);
    Assert.assertTrue(sensor2.isRunning());


    Sensor sensor3 = d.getSensor(3);

    Assert.assertNotNull(sensor3);
    Assert.assertTrue(sensor3.getName().equals("Sensor 3"));
    Assert.assertTrue(sensor3.getSensorID() == 3);
    Assert.assertTrue(sensor3 instanceof RangeSensor);
    Assert.assertTrue(sensor3.isRunning());


    Sensor sensor4 = d.getSensor(4);

    Assert.assertNotNull(sensor4);
    Assert.assertTrue(sensor4.getName().equals("Sensor 4"));
    Assert.assertTrue(sensor4.getSensorID() == 4);
    Assert.assertTrue(sensor4 instanceof StateSensor);
    Assert.assertTrue(sensor4.isRunning());

  }


  /**
   * Test soft restart to load one controller definition and then override it with
   * a new one.
   *
   * @throws Exception if test fails
   */
  @Test public void testSoftRestart2() throws Exception
  {
    ControllerConfiguration cc = new ControllerConfiguration();
    URI deploymentURI = AllTests.getAbsoluteFixturePath().resolve("deployment/sensorsonly");
    cc.setResourcePath(deploymentURI.getPath());

    Deployer d = createDeployer(cc);

    d.softRestart();

    Sensor sensor1 = d.getSensor(1);

    Assert.assertNotNull("got null sensor", sensor1);
    Assert.assertTrue(sensor1.getName().equals("Sensor 1"));
    Assert.assertTrue(sensor1.getSensorID() == 1);
    Assert.assertTrue(sensor1 instanceof SwitchSensor);
    Assert.assertTrue(sensor1.isRunning());


    Sensor sensor2 = d.getSensor(2);

    Assert.assertNotNull(sensor2);
    Assert.assertTrue(sensor2.getName().equals("Sensor 2"));
    Assert.assertTrue(sensor2.getSensorID() == 2);
    Assert.assertTrue(sensor2 instanceof LevelSensor);
    Assert.assertTrue(sensor2.isRunning());


    Sensor sensor3 = d.getSensor(3);

    Assert.assertNotNull(sensor3);
    Assert.assertTrue(sensor3.getName().equals("Sensor 3"));
    Assert.assertTrue(sensor3.getSensorID() == 3);
    Assert.assertTrue(sensor3 instanceof RangeSensor);
    Assert.assertTrue(sensor3.isRunning());


    Sensor sensor4 = d.getSensor(4);

    Assert.assertNotNull(sensor4);
    Assert.assertTrue(sensor4.getName().equals("Sensor 4"));
    Assert.assertTrue(sensor4.getSensorID() == 4);
    Assert.assertTrue(sensor4 instanceof StateSensor);
    Assert.assertTrue(sensor4.isRunning());


    // do the change...

    deploymentURI = AllTests.getAbsoluteFixturePath().resolve("deployment/sensorsonly2");
    cc.setResourcePath(deploymentURI.getPath());

    d.softRestart();

    Assert.assertFalse(sensor4.isRunning());
    Assert.assertFalse(sensor3.isRunning());
    Assert.assertFalse(sensor2.isRunning());
    Assert.assertFalse(sensor1.isRunning());

    Assert.assertTrue(d.getSensor(1) == null);
    Assert.assertTrue(d.getSensor(2) == null);
    Assert.assertTrue(d.getSensor(3) == null);
    Assert.assertTrue(d.getSensor(4) == null);


    Sensor sensor5 = d.getSensor(5);

    Assert.assertNotNull("got null sensor", sensor5);
    Assert.assertTrue(sensor5.getName().equals("Sensor 5"));
    Assert.assertTrue(sensor5.getSensorID() == 5);
    Assert.assertTrue(sensor5 instanceof StateSensor);
    Assert.assertTrue(sensor5.isRunning());


    Sensor sensor6 = d.getSensor(6);

    Assert.assertNotNull(sensor6);
    Assert.assertTrue(sensor6.getName().equals("Sensor 6"));
    Assert.assertTrue(sensor6.getSensorID() == 6);
    Assert.assertTrue(sensor6 instanceof RangeSensor);
    Assert.assertTrue(sensor6.isRunning());


    Sensor sensor7 = d.getSensor(7);

    Assert.assertNotNull(sensor7);
    Assert.assertTrue(sensor7.getName().equals("Sensor 7"));
    Assert.assertTrue(sensor7.getSensorID() == 7);
    Assert.assertTrue(sensor7 instanceof LevelSensor);
    Assert.assertTrue(sensor7.isRunning());


    Sensor sensor8 = d.getSensor(8);

    Assert.assertNotNull(sensor8);
    Assert.assertTrue(sensor8.getName().equals("Sensor 8"));
    Assert.assertTrue(sensor8.getSensorID() == 8);
    Assert.assertTrue(sensor8 instanceof SwitchSensor);
    Assert.assertTrue(sensor8.isRunning());


    Sensor sensor9 = d.getSensor(9);

    Assert.assertNotNull(sensor9);
    Assert.assertTrue(sensor9.getName().equals("Sensor 9"));
    Assert.assertTrue(sensor9.getSensorID() == 9);
    Assert.assertTrue(sensor9 instanceof StateSensor);
    Assert.assertTrue(sensor9.isRunning());


    // finish by deploying empty...

    deploymentURI = AllTests.getAbsoluteFixturePath().resolve("deployment/doesnotexist");
    cc.setResourcePath(deploymentURI.getPath());

    try
    {
      d.softRestart();

      Assert.fail("Expected an exception here.");
    }

    catch (ControllerDefinitionNotFoundException e)
    {
      // expected...
    }

    Assert.assertFalse(sensor5.isRunning());
    Assert.assertFalse(sensor6.isRunning());
    Assert.assertFalse(sensor7.isRunning());
    Assert.assertFalse(sensor8.isRunning());
    Assert.assertFalse(sensor9.isRunning());

    Assert.assertTrue("expected sensor 5 to be cleared", d.getSensor(5) == null);
    Assert.assertTrue("expected sensor 6 to be cleared", d.getSensor(6) == null);
    Assert.assertTrue("expected sensor 7 to be cleared", d.getSensor(7) == null);
    Assert.assertTrue("expected sensor 8 to be cleared", d.getSensor(8) == null);
    Assert.assertTrue("expected sensor 9 to be cleared", d.getSensor(9) == null);

  }


  /**
   * Test restart with redeploying the same controller definition over itself.
   *
   * @throws Exception if test fails
   */
  @Test public void redeployItself() throws Exception
  {
    URI deploymentURI = AllTests.getAbsoluteFixturePath().resolve("deployment/sensorsonly");

    Deployer d = createDeployer(deploymentURI);

    d.softRestart();

    Sensor sensor1 = d.getSensor(1);

    Assert.assertNotNull("got null sensor", sensor1);
    Assert.assertTrue(sensor1.getName().equals("Sensor 1"));
    Assert.assertTrue(sensor1.getSensorID() == 1);
    Assert.assertTrue(sensor1 instanceof SwitchSensor);
    Assert.assertTrue(sensor1.isRunning());


    Sensor sensor2 = d.getSensor(2);

    Assert.assertNotNull(sensor2);
    Assert.assertTrue(sensor2.getName().equals("Sensor 2"));
    Assert.assertTrue(sensor2.getSensorID() == 2);
    Assert.assertTrue(sensor2 instanceof LevelSensor);
    Assert.assertTrue(sensor2.isRunning());


    Sensor sensor3 = d.getSensor(3);

    Assert.assertNotNull(sensor3);
    Assert.assertTrue(sensor3.getName().equals("Sensor 3"));
    Assert.assertTrue(sensor3.getSensorID() == 3);
    Assert.assertTrue(sensor3 instanceof RangeSensor);
    Assert.assertTrue(sensor3.isRunning());


    Sensor sensor4 = d.getSensor(4);

    Assert.assertNotNull(sensor4);
    Assert.assertTrue(sensor4.getName().equals("Sensor 4"));
    Assert.assertTrue(sensor4.getSensorID() == 4);
    Assert.assertTrue(sensor4 instanceof StateSensor);
    Assert.assertTrue(sensor4.isRunning());


    // redeploy

    d.softRestart();

    Assert.assertFalse(sensor4.isRunning());
    Assert.assertFalse(sensor3.isRunning());
    Assert.assertFalse(sensor2.isRunning());
    Assert.assertFalse(sensor1.isRunning());


    sensor1 = d.getSensor(1);

    Assert.assertNotNull("got null sensor", sensor1);
    Assert.assertTrue(sensor1.getName().equals("Sensor 1"));
    Assert.assertTrue(sensor1.getSensorID() == 1);
    Assert.assertTrue(sensor1 instanceof SwitchSensor);
    Assert.assertTrue(sensor1.isRunning());


    sensor2 = d.getSensor(2);

    Assert.assertNotNull(sensor2);
    Assert.assertTrue(sensor2.getName().equals("Sensor 2"));
    Assert.assertTrue(sensor2.getSensorID() == 2);
    Assert.assertTrue(sensor2 instanceof LevelSensor);
    Assert.assertTrue(sensor2.isRunning());


    sensor3 = d.getSensor(3);

    Assert.assertNotNull(sensor3);
    Assert.assertTrue(sensor3.getName().equals("Sensor 3"));
    Assert.assertTrue(sensor3.getSensorID() == 3);
    Assert.assertTrue(sensor3 instanceof RangeSensor);
    Assert.assertTrue(sensor3.isRunning());


    sensor4 = d.getSensor(4);

    Assert.assertNotNull(sensor4);
    Assert.assertTrue(sensor4.getName().equals("Sensor 4"));
    Assert.assertTrue(sensor4.getSensorID() == 4);
    Assert.assertTrue(sensor4 instanceof StateSensor);
    Assert.assertTrue(sensor4.isRunning());

  }


  /**
   * Test restart when configured to non-existent controller.xml path. <p>
   *
   * At the moment, no exception is propagated, the error is logged but the runtime
   * stays operational although not executing any functions.
   *
   * @throws Exception if test fails
   */
  @Test public void testSoftRestartNoXMLDoc() throws Exception
  {
    URI deploymentURI = AllTests.getAbsoluteFixturePath().resolve("deployment/doesntexist");

    Deployer d = createDeployer(deploymentURI);

    try
    {
      d.softRestart();

      Assert.fail("Expected an exception here..");
    }

    catch (ControllerDefinitionNotFoundException e)
    {
      // expected...
    }
  }



  /**
   * TODO :
   *   at the moment there are no assertions that can be made to test
   *   the startController behavior
   *
   * @throws Exception if test fails
   */
  @Test public void testStartControllerConsequtiveCalls() throws Exception
  {
    Deployer d = createDeployer("Test start controller");

    d.startController();

    // TODO : second call should return immediately, log an error or throw an exception
    
    d.startController();
  }
  

  @Test public void testIsPaused()
  {
    Assert.fail("Not Yet Implemented. See ORCJAVA-161");  
  }

  @Test public void testAutoAndRedeployment()
  {
    // These should be integration tests with external test container

    Assert.fail("Not Yet Implemented. See ORCJAVA-162");  
  }

  @Test public void testSlowUnresponsiveSensorOnStop()
  {
    // test deployer behavior when stopping and sensor won't respond to request to stop

    Assert.fail("Not Yet Implemented. See ORCJAVA-163");
  }

  @Test public void testRestartOnIncorrectXML()
  {
    // test restart behavior when the xml is structurally correct but
    // semantically broken (such as linking to non-existent elements, etc).

    Assert.fail("Not Yet Implemented. See ORCJAVA-164");   
  }


  /**
   * TODO :
   *   The method may yet move away from deployer API, so this is a temp test moved here
   *   from elsewhere. It documents a contract change from return a null to raising a checked
   *   exception. Since it's a checked exception, should be a relatively easy contract change
   *   to track due to compiler checks.
   *
   * @throws Exception if test fails
   */
  @Test public void testQueryElementFromXMLByIdNotFound() throws Exception
  {
    URI deploymentURI = AllTests.getAbsoluteFixturePath().resolve("deployment/sensorsonly");

    Deployer d = createDeployer(deploymentURI);

    d.startController();

    try
    {
      Element s = d.queryElementById(11111);

      Assert.fail("should not get here...");
    }

    catch (InitializationException e)
    {
      // expected
    }
  }


  /**
   * TODO :
   *   test is temporarily moved here, the method being tested on the Deployer API
   *   may still be moved elsewhere or made non-public
   *
   * @throws Exception      if test fails
   */  
  @Test public void testQueryElementFromXMLById() throws Exception
  {
    URI deploymentURI = AllTests.getAbsoluteFixturePath().resolve("deployment/sensorsonly");

    Deployer d = createDeployer(deploymentURI);

    d.startController();



    Element sensor = d.queryElementById(1);

    Assert.assertTrue("sensor".equals(sensor.getName()));
    Assert.assertTrue(sensor.getAttributeValue("name").equals("Sensor 1"));
    Assert.assertTrue(sensor.getAttributeValue("type").equals("switch"));
    
    List<Element> sensorChildren = sensor.getChildren();

    Assert.assertTrue(sensorChildren.size() == 1);

    Element include = sensorChildren.get(0);

    Assert.assertTrue(include.getName().equals("include"));
    Assert.assertTrue(include.getAttributeValue("type").equals("command"));
    Assert.assertTrue(include.getAttributeValue("ref").equals("10"));
  }




  // Nested Classes -------------------------------------------------------------------------------


  private static class TestEventListener implements EventListener
  {
    @Override public void setSensor(Sensor s)
    {

    }

    @Override public void stop(Sensor s)
    {

    }
  }

}

