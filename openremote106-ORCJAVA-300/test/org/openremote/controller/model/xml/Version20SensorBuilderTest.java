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
package org.openremote.controller.model.xml;


import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.command.CommandFactory;
import org.openremote.controller.component.ComponentBuilder;
import org.openremote.controller.component.ComponentFactory;
import org.openremote.controller.component.LevelSensor;
import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.component.control.button.ButtonBuilder;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.StateSensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.protocol.ReadCommand;
import org.openremote.controller.service.ControlCommandService;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.service.DeployerTest;
import org.openremote.controller.service.impl.ControlCommandServiceImpl;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.suite.AllTests;


/**
 * Unit tests for {@link Version20SensorBuilder} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Version20SensorBuilderTest
{

  // Instance Fields ------------------------------------------------------------------------------

  private Deployer deployer;
  private Version20SensorBuilder sensorBuilder;
  private ControlCommandService commandService;
  private StatusCache cache;


  // Test Lifecycle -------------------------------------------------------------------------------


  /**
   * Setup the service dependencies of deployer and other required services.
   *
   * @throws Exception    if setup fails
   */
  @Before public void setUp() throws Exception
  {
    AllTests.initServiceContext();
    
    URI deploymentURI = AllTests.getAbsoluteFixturePath().resolve("builder/sensor");

    CommandFactory cf = DeployerTest.createCommandFactory();
    sensorBuilder = new Version20SensorBuilder();
    cache = new StatusCache();

    deployer = DeployerTest.createDeployer(deploymentURI, cf, sensorBuilder, cache);
    
    ButtonBuilder bb = new ButtonBuilder();
    bb.setDeployer(deployer);
    bb.setCommandFactory(cf);

    Map<String, ComponentBuilder> cb = new HashMap<String, ComponentBuilder>();
    cb.put("button", bb);

    ComponentFactory cof = new ComponentFactory();
    cof.setComponentBuilders(cb);
    
    commandService = new ControlCommandServiceImpl(deployer, cof);

    deployer.softRestart();
  }


  // Tests ----------------------------------------------------------------------------------------

  /**
   * Parse the following sensor when deployed through a complete controller.xml document.
   *
   * <pre>{@code
   * <sensor id="1008" name="range sensor" type="range">
   *   <include type="command" ref="96" />
   *   <min value="-20" />
   *   <max value="100" />
   * </sensor>
   *
   * }</pre>
   *
   * @throws Exception if the test fails
   */
  @Test public void testRangeSensorBuild() throws Exception
  {
    RangeSensor s = (RangeSensor)buildSensor(SensorType.RANGE);
    Assert.assertEquals(100, s.getMaxValue());
    Assert.assertEquals(-20, s.getMinValue());
    Assert.assertTrue(s.getName().equals("range sensor"));
    Assert.assertTrue(s.getProperties().size() == 2);
    Assert.assertTrue(s.getSensorID() == 1008);
    Assert.assertFalse(s.isRunning());
    Assert.assertTrue(s.isPolling());
    Assert.assertFalse(s.isEventListener());

    Assert.assertFalse(s.equals(null));
    Assert.assertTrue(s.equals(s));
    Assert.assertTrue(s.equals(buildSensor(SensorType.RANGE)));
    Assert.assertFalse(s.equals(buildSensor(SensorType.SWITCH)));
  }


  /**
   * Parse the following sensor when deployed through a complete controller.xml document.
   *
   * <pre>{@code
   * <sensor id="1001" name="lampA power sensor" type="switch">
   *   <include type="command" ref="98" />
   *   <state name="on" value="on" />
   *   <state name="off" value="off" />
   * </sensor>
   *
   * }</pre>
   *
   * @throws Exception if test fails
   */
  @Test public void testSwitchSensorBuild() throws Exception
  {
    Sensor s = buildSensor(SensorType.SWITCH);
    Assert.assertTrue(s instanceof SwitchSensor);
    Assert.assertTrue(s.getName().equals("lampA power sensor"));
    Assert.assertTrue(s.getProperties().size() == 0);

    Assert.assertTrue(s.getSensorID() == 1001);
    Assert.assertFalse(s.isRunning());
    Assert.assertTrue(s.isPolling());
    Assert.assertFalse(s.isEventListener());

    Assert.assertFalse(s.equals(null));
    Assert.assertTrue(s.equals(s));
    Assert.assertTrue(s.equals(buildSensor(SensorType.SWITCH)));
    Assert.assertFalse(s.equals(buildSensor(SensorType.RANGE)));
  }

  /**
   * Parse the following sensor when deployed through a complete controller.xml document.
   *
   * <pre>{@code
   * <sensor id="1011" name="s1011" type="switch">
   *   <include type="command" ref="962" />
   *   <state name="on" value="on" />
   *   <state name="off" value="off" />
   * </sensor>
   *
   * }</pre>
   *
   * @throws Exception if test fails
   */
  @Test public void testSwitchSensorBuildWithListener() throws Exception
  {
    Sensor s = buildSensor(1011);
    Assert.assertTrue(s instanceof SwitchSensor);
    Assert.assertTrue(s.getName().equals("s1011"));
    Assert.assertTrue(s.getProperties().size() == 0);

    Assert.assertTrue(s.getSensorID() == 1011);
    Assert.assertFalse(s.isRunning());
    Assert.assertFalse(s.isPolling());
    Assert.assertTrue(s.isEventListener());

    Assert.assertFalse(s.equals(null));
    Assert.assertTrue(s.equals(s));
    Assert.assertFalse(s.equals(buildSensor(SensorType.SWITCH)));
    Assert.assertFalse(s.equals(buildSensor(SensorType.RANGE)));
    Assert.assertTrue(s.equals(buildSensor(1011)));
  }


  /**
   * Parse the following sensor when deployed through a complete controller.xml document.
   *
   * <pre>{@code
   * <sensor id="1012" name="s1012" type="switch">
   *   <include type="command" ref="98" />
   * </sensor>
   *
   * }</pre>
   *
   * @throws Exception if test fails
   */
  @Test public void testBasicSwitchSensorBuild() throws Exception
  {
    Sensor s = buildSensor(1012);
    Assert.assertTrue(s instanceof SwitchSensor);
    Assert.assertTrue(s.getName().equals("s1012"));
    Assert.assertTrue(s.getProperties().size() == 0);

    Assert.assertTrue(s.getSensorID() == 1012);
    Assert.assertFalse(s.isRunning());
    Assert.assertTrue(s.isPolling());
    Assert.assertFalse(s.isEventListener());

    Assert.assertFalse(s.equals(null));
    Assert.assertTrue(s.equals(s));
    Assert.assertFalse(s.equals(buildSensor(SensorType.SWITCH)));
    Assert.assertFalse(s.equals(buildSensor(SensorType.RANGE)));
    Assert.assertTrue(s.equals(buildSensor(1012)));
  }



  /**
   * Parse the following sensor when deployed through a complete controller.xml document.
   *
   * <pre>{@code
   * <sensor id="1013" name="s1013" type="switch">
   *   <include type="command" ref="962" />
   *   <state name="on" value="open" />
   *   <state name="off" value="close" />
   * </sensor>
   *
   * }</pre>
   *
   * @throws Exception if test fails
   */
  @Test public void testMappedSwitchSensorBuildWithListener() throws Exception
  {
    Sensor s = buildSensor(1013);
    Assert.assertTrue(s instanceof SwitchSensor);
    Assert.assertTrue(s.getName().equals("s1013"));
    Assert.assertTrue(s.getProperties().size() == 0);

    Assert.assertTrue(s.getSensorID() == 1013);
    Assert.assertFalse(s.isRunning());
    Assert.assertFalse(s.isPolling());
    Assert.assertTrue(s.isEventListener());

    Assert.assertFalse(s.equals(null));
    Assert.assertTrue(s.equals(s));
    Assert.assertFalse(s.equals(buildSensor(SensorType.SWITCH)));
    Assert.assertFalse(s.equals(buildSensor(SensorType.RANGE)));
  }




  /**
   * Parse the following sensor when deployed through a complete controller.xml document.
   *
   * <pre>{@code
   * <sensor id="1010" name="level sensor" type="level">
   *   <include type="command" ref="96" />
   * </sensor>
   *
   * }</pre>
   *
   * @throws Exception if test fails
   */
  @Test public void testLevelSensorBuild() throws Exception
  {
    LevelSensor s = (LevelSensor)buildSensor(SensorType.LEVEL);
    Assert.assertEquals(100, s.getMaxValue());
    Assert.assertEquals(0, s.getMinValue());
    Assert.assertTrue(s.getName().equals("level sensor"));
    Assert.assertTrue(s.getProperties().size() == 0);

    Assert.assertTrue(s.getSensorID() == 1010);
    Assert.assertFalse(s.isRunning());
    Assert.assertTrue(s.isPolling());
    Assert.assertFalse(s.isEventListener());

    Assert.assertFalse(s.equals(null));
    Assert.assertTrue(s.equals(s));
    Assert.assertFalse(s.equals(buildSensor(SensorType.SWITCH)));
    Assert.assertTrue(s.equals(buildSensor(SensorType.LEVEL)));
  }



  /**
   * Parse the following sensor when deployed through a complete controller.xml document.
   * The command implementation is an event listener.
   *
   * <pre>{@code
   * <sensor id="1040" name="ls1040" type="level">
   *   <include type="command" ref="963" />
   * </sensor>
   *
   * }</pre>
   *
   * @throws Exception if test fails
   */
  @Test public void testLevelSensorBuildWithListener() throws Exception
  {
    LevelSensor s = (LevelSensor)buildSensor(1040);
    Assert.assertEquals(100, s.getMaxValue());
    Assert.assertEquals(0, s.getMinValue());
    Assert.assertTrue(s.getName().equals("ls1040"));
    Assert.assertTrue(s.getProperties().size() == 0);

    Assert.assertTrue(s.getSensorID() == 1040);
    Assert.assertFalse(s.isRunning());
    Assert.assertFalse(s.isPolling());
    Assert.assertTrue(s.isEventListener());

    Assert.assertFalse(s.equals(null));
    Assert.assertTrue(s.equals(s));
    Assert.assertFalse(s.equals(buildSensor(SensorType.SWITCH)));
    Assert.assertTrue(s.equals(buildSensor(1040)));
  }



  /**
   * Parse the following sensor when deployed through a complete controller.xml document.
   * This one includes incorrect min/max value elements. They should be ignored (or schema
   * validation should flag the document instance incorrect).
   *
   * <pre>{@code
   * <sensor id="1020" name="ls1020" type="level">
   *   <include type="command" ref="96" />
   *
   *   <min value = "0" />
   *   <max value = "100" />
   * </sensor>
   *
   * }</pre>
   *
   * @throws Exception if test fails
   */
  @Test public void testLevelSensorBuildIncorrectSchema() throws Exception
  {
    LevelSensor s = (LevelSensor)buildSensor(1020);
    Assert.assertEquals(100, s.getMaxValue());
    Assert.assertEquals(0, s.getMinValue());
    Assert.assertTrue(s.getName().equals("ls1020"));
    Assert.assertTrue(s.getProperties().size() == 0);

    Assert.assertTrue(s.getSensorID() == 1020);
    Assert.assertFalse(s.isRunning());
    Assert.assertTrue(s.isPolling());
    Assert.assertFalse(s.isEventListener());

    Assert.assertFalse(s.equals(null));
    Assert.assertTrue(s.equals(s));
    Assert.assertFalse(s.equals(buildSensor(SensorType.SWITCH)));
    Assert.assertTrue(s.equals(buildSensor(1020)));
  }

  /**
   * Parse the following sensor when deployed through a complete controller.xml document.
   * This one includes incorrect min/max value elements. They should be ignored (or schema
   * validation should flag the document instance incorrect).
   *
   * <pre>{@code
   * <sensor id="1030" name="ls1030" type="level">
   *   <include type="command" ref="96" />
   *
   *   <min value = "20" />
   *   <max value = "40" />
   * </sensor>
   *
   * }</pre>
   *
   * @throws Exception if test fails
   */
  @Test public void testLevelSensorBuildIncorrectSchema2() throws Exception
  {
    LevelSensor s = (LevelSensor)buildSensor(1030);
    Assert.assertEquals(100, s.getMaxValue());
    Assert.assertEquals(0, s.getMinValue());
    Assert.assertTrue(s.getName().equals("ls1030"));
    Assert.assertTrue(s.getProperties().size() == 0);

    Assert.assertTrue(s.getSensorID() == 1030);
    Assert.assertFalse(s.isRunning());
    Assert.assertTrue(s.isPolling());
    Assert.assertFalse(s.isEventListener());

    Assert.assertFalse(s.equals(null));
    Assert.assertTrue(s.equals(s));
    Assert.assertFalse(s.equals(buildSensor(SensorType.SWITCH)));
    Assert.assertTrue(s.equals(buildSensor(1030)));
  }





  /**
   * Parse the following sensor configuration:
   *
   * <pre>{@code
   *
   * Two-state CUSTOM sensor configuration.
   *
   * Read command return value 'on' is mapped to 'open'
   * Read command return value 'off' is mapped to 'close'
   *
   * <sensor id = "1009" name = "Door power sensor" type = "custom">
   *   <include type = "command" ref = "98" />
   *   <state name = "open" value = "on" />
   *   <state name = "close" value = "off" />
   * </sensor>
   * }</pre>
   *
   * @throws Exception if test fails
   */
  @Test public void testCustomSensorBuild() throws Exception
  {
    Sensor s = buildSensor(SensorType.CUSTOM);
    Assert.assertTrue(s.getName().equals("Door power sensor"));
    Assert.assertTrue(s.getProperties().size() == 2);
    Assert.assertTrue(s.getProperties().keySet().contains("state-1"));
    Assert.assertTrue(s.getProperties().keySet().contains("state-2"));
    Assert.assertTrue(s.getProperties().values().contains("on"));
    Assert.assertTrue(s.getProperties().values().contains("off"));

    Assert.assertTrue(s instanceof StateSensor);

    StateSensor state = (StateSensor)s;

    Assert.assertTrue(state.processEvent("on").getValue().equals("open"));
    Assert.assertTrue(state.processEvent("off").getValue().equals("close"));

    Assert.assertTrue(state.processEvent("foo").getValue().equals("foo"));
  }


  /**
   * Parse the following sensor configuration:
   *
   * <pre>{@code
   *
   *  Four-state CUSTOM sensor configuration. Mixed mappings.
   *
   *  Read command return value '1' is mapped to 'one'
   *  Read command return value '2' is not mapped.
   *  Read command return value '3' is mapped to 'three'
   *  Read command return value '4' is not mapped.
   *
   * <sensor id = "1099" name = "Numbers" type = "custom">
   *   <include type = "command" ref = "98" />
   *
   *   <state name = "one" value = "1" />
   *   <state name = "three" value = "3" />
   * </sensor>
   * }</pre>
   *
   * @throws Exception if test fails
   */
  @Test public void testPartiallyMappedCustomSensorBuild() throws Exception
  {
    Sensor s = buildSensor(1099);
    Assert.assertTrue(s.getName().equals("Numbers"));
    Assert.assertTrue(s.getProperties().size() == 2);
    Assert.assertTrue(s.getProperties().keySet().contains("state-1"));
    Assert.assertTrue(s.getProperties().keySet().contains("state-2"));
    Assert.assertTrue(s.getProperties().values().contains("1"));
    Assert.assertTrue(s.getProperties().values().contains("3"));

    Assert.assertTrue(s instanceof StateSensor);

    StateSensor state = (StateSensor)s;

    Assert.assertTrue(state.processEvent("1").getValue().equals("one"));
    Assert.assertTrue(state.processEvent("2").getValue().equals("2"));
    Assert.assertTrue(state.processEvent("3").getValue().equals("three"));
    Assert.assertTrue(state.processEvent("4").getValue().equals("4"));

    Assert.assertTrue(state.processEvent("foo").getValue().equals("foo"));
  }


  /**
   * Parse the following sensor configuration:
   *
   * <pre>{@code
   *
   * <sensor id = "1098" name = "s1098" type = "custom">
   *   <include type = "command" ref = "98" />
   * </sensor>
   * }</pre>
   *
   * @throws Exception if test fails
   */
  @Test public void testArbitraryCustomSensorBuild() throws Exception
  {
    Sensor s = buildSensor(1098);
    Assert.assertTrue(s.getName().equals("s1098"));
    Assert.assertTrue(s.getProperties().size() == 0);

    Assert.assertTrue(s instanceof StateSensor);

    StateSensor state = (StateSensor)s;

    Assert.assertTrue(state.processEvent("1").getValue().equals("1"));
    Assert.assertTrue(state.processEvent("2").getValue().equals("2"));
    Assert.assertTrue(state.processEvent("3").getValue().equals("3"));
    Assert.assertTrue(state.processEvent("4").getValue().equals("4"));
    Assert.assertTrue(state.processEvent("foo").getValue().equals("foo"));
  }

  



  @Test public void testInvalidConfigs()
  {
    // TODO : ORCJAVA-72
    //
    // need more tests to make sure different configuration variants are initialized correctly
    // and also test some error handling...
    
    Assert.fail("Not Yet Implemented (see ORCJAVA-72 -- http://jira.openremote.org/browse/ORCJAVA-72)");
  }


  /**
   * Test against what could be qualified as a bug that has now become a feature and we need to
   * make sure we don't regress on it unintentionally should we try to fix the bug again. <p>
   *
   * Current tooling generates a style of switch sensors in XML that makes very little sense:
   *
   * <pre>{@code
   *
   * <sensor id = "717" name = "se" type = "switch">
   *   <include type = "command" ref = "96" />
   *   <state name = "on" />
   *   <state name = "off" />
   * </sensor>
   * }</pre>
   *
   * It makes no sense because switch can only ever return on/off as states and no mapping is
   * provided, making the state declarations redundant. But because tooling does generate this,
   * we need to make sure we correctly parse it. <p>
   *
   * See http://jira.openremote.org/browse/ORCJAVA-73
   *
   * @throws Exception if test fails
   */
  @Test public void testSwitchStateMappingWithNoValue() throws Exception
  {
    Sensor s = buildSensorWithID(717);

    Assert.assertTrue(s.getName().equals("se"));
    Assert.assertTrue(s.getSensorID() == 717);

    // switch sensor states should not show up as properties, even if mapped...

    Assert.assertTrue(s.getProperties().size() == 0);

    commandService.trigger("666", "click");

    String offValue = getSensorValueFromCache(717);

    Assert.assertTrue(
        "Expected 'off', got '" + offValue + "'",
        offValue.equals("off")
    );

    commandService.trigger("555", "click");


    String onValue = getSensorValueFromCache(717);

    Assert.assertTrue(
        "Expected 'on', got '" + onValue + "'",
        onValue.equals("on")
    );

    Assert.assertTrue(s instanceof SwitchSensor);

    StateSensor state = (StateSensor)s;

    // check that states are in place despite funky XML model...

    Assert.assertTrue(state.processEvent("on").getValue().equals("on"));
    Assert.assertTrue(state.processEvent("off").getValue().equals("off"));
    Assert.assertTrue(state.processEvent("foo").getValue().equals(Sensor.UNKNOWN_STATUS));


    Assert.assertTrue(s.isPolling());
    Assert.assertFalse(s.isEventListener());
  }


  /**
   * Same as {@link #testSwitchStateMappingWithNoValue} above, just uses
   * an event listener instead of polling sensor command.
   *
   * See http://jira.openremote.org/browse/ORCJAVA-73
   *
   * @throws Exception if test fails
   */
  @Test public void testSwitchStateMappingWithNoValueAndListener() throws Exception
  {
    Sensor s = buildSensorWithID(727);

    s.start();
    
    Assert.assertTrue(s.getName().equals("se2"));
    Assert.assertTrue(s.getSensorID() == 727);

    // switch sensor states should not show up as properties, even if mapped...

    Assert.assertTrue(s.getProperties().size() == 0);


    // should get either one depending what the state of the listener is

    String val = cache.queryStatus(727);
    
    Assert.assertTrue(
        "Expected either 'on' or 'off', got " + val,
        val.equals("off") || val.equals("on")
    );
    

    Assert.assertTrue(s instanceof SwitchSensor);

    StateSensor state = (StateSensor)s;

    // check that states are in place despite funky XML model...

    Assert.assertTrue(state.processEvent("on").getValue().equals("on"));
    Assert.assertTrue(state.processEvent("off").getValue().equals("off"));
    Assert.assertTrue(state.processEvent("foo").getValue().equals(Sensor.UNKNOWN_STATUS));

    String status = cache.queryStatus(727);

    // should have something since its a listener...

    Assert.assertFalse(status.equals(Sensor.UNKNOWN_STATUS));

    Assert.assertTrue(s.isEventListener());
    Assert.assertFalse(s.isPolling());
  }


  // TODO : test SensorBuilder subclassing with new sensor types
  
  

  // Helpers --------------------------------------------------------------------------------------

  private String getSensorValueFromCache(int sensorID) throws Exception
  {
    // sleep here to give the polling mechanism enough time to push the event value to cache...
    
    Thread.sleep(ReadCommand.POLLING_INTERVAL * 2);
    
    return cache.queryStatus(sensorID);
  }

  private Sensor buildSensor(SensorType type) throws Exception
  {

    Element ele = null;

    switch (type)
    {
      case RANGE:
        ele = deployer.queryElementById(1008);
        break;

      case LEVEL:
        ele = deployer.queryElementById(1010);
        break;

      case SWITCH:
        ele = deployer.queryElementById(1001);
        break;

      case CUSTOM:
        ele = deployer.queryElementById(1009);
        break;

      default:
        break;
    }
    
    return sensorBuilder.build(ele);
  }

  private Sensor buildSensor(int id) throws Exception
  {
    return sensorBuilder.build(deployer.queryElementById(id));
  }


  private Sensor buildSensorWithID(int id) throws Exception
  {
    Element el = deployer.queryElementById(id);

    return sensorBuilder.build(el);
  }


  enum SensorType { RANGE, LEVEL, SWITCH, CUSTOM }

}
