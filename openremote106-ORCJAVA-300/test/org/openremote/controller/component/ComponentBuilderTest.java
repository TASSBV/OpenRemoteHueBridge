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
package org.openremote.controller.component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.Constants;
import org.openremote.controller.deployer.ModelBuilder;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.model.xml.Version20SensorBuilder;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.service.DeployerTest;
import org.openremote.controller.suite.AllTests;


/**
 * TODO :
 *   - most of these tests belong to their particular component builder tests but are
 *     left here until new builder implementations are ready for all component types
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ComponentBuilderTest
{

  private Version20SensorBuilder sensorBuilder;
  private Deployer deployer;


  // Test Lifecycle -------------------------------------------------------------------------------

  @Before public void setUp() throws Exception
  {
    AllTests.initServiceContext();
    URI deploymentURI = AllTests.getAbsoluteFixturePath().resolve("component");

    sensorBuilder = new Version20SensorBuilder();
    deployer = DeployerTest.createDeployer(deploymentURI, sensorBuilder);

    deployer.softRestart();
  }


  // Tests ----------------------------------------------------------------------------------------


  /**
   * Test constructing a sensor reference through a {@code <switch>} component
   * sensor reference:
   *
   * <pre>{@code
   *
   * <switch id = "1">
   *  <on>
   *   <include type = "command" ref = "501" />
   *  </on>
   *  <off>
   *   <include type = "command" ref = "502" />
   *  </off>
   *
   *  <include type = "sensor" ref = "1001" />
   * </switch>
   *
   * }</pre>
   *
   * @throws Exception  if test fails
   */
  @Test public void testParseSensorOnSwitch1() throws Exception
  {
    Element switch1 = deployer.queryElementById(1);

    Assert.assertNotNull(switch1);
    Assert.assertTrue(switch1.getAttribute("id").getIntValue() == 1);

    Element include = switch1.getChild("include", ModelBuilder.SchemaVersion.OPENREMOTE_NAMESPACE);

    Assert.assertNotNull(include);
    Assert.assertNotNull(include.getAttribute("type"));
    Assert.assertNotNull(include.getAttribute("ref"));

    Assert.assertTrue(include.getAttribute("type").getValue().equals("sensor"));
    Assert.assertTrue(include.getAttribute("ref").getValue().equals("1001"));

    // Test...

    Sensor sensor = deployer.getSensorFromComponentInclude(include);


    Assert.assertTrue(sensor.getSensorID() == 1001);
    Assert.assertTrue(sensor instanceof SwitchSensor);
    Assert.assertTrue(sensor.isRunning());
  }


  /**
   * Redundant regression test of {@link #testParseSensorOnSwitch1()} on a bug
   * that has since been fixed. Leaving this in place for now.
   *
   * @throws Exception  if test fails
   */
  @Test public void testParseSensorBUG() throws Exception
  {
    Element switch1 = deployer.queryElementById(1);
    Element include = switch1.getChild("include", ModelBuilder.SchemaVersion.OPENREMOTE_NAMESPACE);


    // Test...

    Sensor sensor = deployer.getSensorFromComponentInclude(include);


    Assert.assertTrue(sensor.getSensorID() == 1001);
    Assert.assertTrue(sensor instanceof SwitchSensor);
    Assert.assertTrue(sensor.isRunning());
  }


  /**
   * Tests parsing of a switch sensor definition with redundant {@code <state>} elements.
   *
   * <pre>{@code
   *
   * <sensor id = "1001" name = "lampA power sensor" type = "switch">
   *  <include type = "command" ref = "98" />
   *
   *   <state name = "on" value = "on" />
   *   <state name = "off" value = "off" />
   * </sensor>
   *
   * }</pre>
   *
   * @throws Exception    if test fails
   */
  @Test public void testSensorBuilderOnSwitchSensor() throws Exception
  {
    Element sensor1001 = deployer.queryElementById(1001);


    // Test...

    Sensor s = sensorBuilder.build(sensor1001);
    s.start();

    Assert.assertTrue(s.getSensorID() == 1001);
    Assert.assertTrue(s instanceof SwitchSensor);
    Assert.assertTrue(s.isRunning());

    // although there are distinct states in the sensor, they should not show up as
    // properties for the protocol implementors -- the state values are automatically
    // mapped by the controller

    Assert.assertTrue(s.getProperties().size() == 0);
  }


  /**
   * Test constructing a sensor reference through a {@code <slider>} component
   * sensor reference:
   *
   * <pre>{@code
   *
   * <slider id = "8">
   *   <setValue>
   *     <include type = "command" ref = "507" />
   *   </setValue>
   *
   *   <include type = "sensor" ref = "1008" />
   * </slider>
   *
   * }</pre>
   *
   * @throws Exception      if test fails
   */
  @Test public void testParseSensorOnSlider() throws Exception
  {
    Element slider = deployer.queryElementById(8);

    Assert.assertNotNull(slider);
    Assert.assertTrue(slider.getAttribute("id").getIntValue() == 8);

    Element include = slider.getChild("include", ModelBuilder.SchemaVersion.OPENREMOTE_NAMESPACE);

    Assert.assertNotNull(include);
    Assert.assertNotNull(include.getAttribute("type"));
    Assert.assertNotNull(include.getAttribute("ref"));

    Assert.assertTrue(include.getAttribute("type").getValue().equals("sensor"));
    Assert.assertTrue(include.getAttribute("ref").getValue().equals("1008"));


    // Test...

    Sensor sensor = deployer.getSensorFromComponentInclude(include);


    Assert.assertTrue(sensor.getSensorID() == 1008);

    Assert.assertTrue(sensor instanceof RangeSensor);
    Assert.assertTrue(sensor.isRunning());

    RangeSensor range = (RangeSensor)sensor;

    Assert.assertTrue(
        "Expected 100, got " + range.getMaxValue(),
        range.getMaxValue() == 100
    );

    Assert.assertTrue(
        "Expected -20, got " + range.getMinValue(),
        range.getMinValue() == -20
    );

    Assert.assertNotNull(sensor.getProperties());
    Assert.assertTrue(sensor.getProperties().containsKey(Sensor.RANGE_MAX_STATE));
    Assert.assertTrue(sensor.getProperties().containsKey(Sensor.RANGE_MIN_STATE));
    Assert.assertTrue(sensor.getProperties().get(Sensor.RANGE_MAX_STATE).equals("100"));
    Assert.assertTrue(sensor.getProperties().get(Sensor.RANGE_MIN_STATE).equals("-20"));
  }


  /**
   * Redundant regression test of {@link #testParseSensorOnSlider()} on a bug
   * that has since been fixed. Leaving this in place for now.
   *
   * @throws Exception  if test fails
   */
  @Test public void testParseSensorOnSliderBUG() throws Exception
  {
    Element slider = deployer.queryElementById(8);
    Element include = slider.getChild("include",  ModelBuilder.SchemaVersion.OPENREMOTE_NAMESPACE);


    // Test...

    Sensor sensor = deployer.getSensorFromComponentInclude(include);


    Assert.assertNotNull("BUG: got null sensor", sensor);

    Assert.assertNotNull(sensor.getProperties());
    Assert.assertTrue(sensor.getProperties().containsKey(Sensor.RANGE_MAX_STATE));
    Assert.assertTrue(sensor.getProperties().containsKey(Sensor.RANGE_MIN_STATE));
    Assert.assertTrue(sensor.getProperties().get(Sensor.RANGE_MAX_STATE).equals("100"));
    Assert.assertTrue(sensor.getProperties().get(Sensor.RANGE_MIN_STATE).equals("-20"));
  }


  /**
   * Tests parsing of a range sensor definition with redundant min/max elements.
   *
   * <pre>{@code
   *
   *  <sensor id = "1008" name = "range sensor" type = "range">
   *   <include type = "command" ref = "96" />
   *
   *    <min value = "-20" />
   *    <max value = "100" />
   *  </sensor>
   *
   * }</pre>
   *
   * @throws Exception      if test fails
   */
  @Test public void testSensorBuilderOnRangeSensor() throws Exception
  {
    Element sensor1008 = deployer.queryElementById(1008);

    // Test...

    RangeSensor s = (RangeSensor)sensorBuilder.build(sensor1008);


    Assert.assertTrue(s.getSensorID() == 1008);
    Assert.assertTrue(s.getMaxValue() == 100);
    Assert.assertTrue(s.getMinValue() == -20);
    Assert.assertNotNull(s.getProperties());
    Assert.assertTrue(s.getProperties().containsKey(Sensor.RANGE_MAX_STATE));
    Assert.assertTrue(s.getProperties().containsKey(Sensor.RANGE_MIN_STATE));
    Assert.assertTrue(s.getProperties().get(Sensor.RANGE_MAX_STATE).equals("100"));
    Assert.assertTrue(s.getProperties().get(Sensor.RANGE_MIN_STATE).equals("-20"));
  }

  


  // Nested Classes -------------------------------------------------------------------------------

  private final static class TestComponentBuilder extends ComponentBuilder
  {
    @Override public Component build(org.jdom.Element el, String commandParam)
    {
      return new MyComponent();
    }
  }

  private final static class MyComponent extends Component
  {
    public List<String> getAvailableActions()
    {
      return new ArrayList<String>();
    }
  }
}

