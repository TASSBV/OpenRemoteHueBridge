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
package org.openremote.controller.model.sensor;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.protocol.ReadCommand;
import org.openremote.controller.statuscache.ChangedStatusTable;
import org.openremote.controller.statuscache.EventProcessorChain;
import org.openremote.controller.statuscache.StatusCache;

/**
 * Base tests for {@link org.openremote.controller.model.sensor.SwitchSensor}.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class SwitchSensorTest
{

  /* share the same cache across all sensor tests */
  private StatusCache cache = null;


  @Before public void setup()
  {
    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain echain = new EventProcessorChain();

    cache = new StatusCache(cst, echain);
  }


  /**
   * Test 'on' state.
   *
   * @throws Exception if test fails
   */
  @Test public void testSwitchOnState() throws Exception
  {
    final int SENSOR_ID = 1;

    SwitchSensor s1 = new SwitchSensor("switch on", SENSOR_ID, cache, new SwitchReadCommand("switch on", SENSOR_ID, "on"));

    cache.registerSensor(s1);
    s1.start();

    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals("on"));
    Assert.assertTrue(s1.getSensorID() == SENSOR_ID);
    Assert.assertTrue(s1.isPolling());
    Assert.assertFalse(s1.isEventListener());
    Assert.assertTrue(s1.getName().equals("switch on"));
    Assert.assertTrue(s1.getProperties().size() == 0);
  }

  /**
   * Test 'off' state.
   *
   * @throws Exception if test fails
   */
  @Test public void testSwitchOffState() throws Exception
  {
    final int SENSOR_ID = 2;

    SwitchSensor s1 = new SwitchSensor("switch off", SENSOR_ID, cache, new SwitchReadCommand("switch off", SENSOR_ID, "off"));

    cache.registerSensor(s1);
    s1.start();

    
    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals("off"));
    Assert.assertTrue(s1.getSensorID() == SENSOR_ID);
    Assert.assertTrue(s1.isPolling());
    Assert.assertFalse(s1.isEventListener());
    Assert.assertTrue(s1.getName().equals("switch off"));
    Assert.assertTrue(s1.getProperties().size() == 0);
  }


  /**
   * Test switch when event producer gives a non-compliant value.
   *
   * @throws Exception if test fails
   */
  @Test public void testSwitchUnknownState() throws Exception
  {
    final int SENSOR_ID = 3;

    SwitchSensor s1 = new SwitchSensor("unknown", SENSOR_ID, cache, new SwitchReadCommand("unknown", SENSOR_ID, "foo"));

    cache.registerSensor(s1);
    s1.start();

    Assert.assertTrue(
        "Expected '" + Sensor.UNKNOWN_STATUS + "', got " + getSensorValueFromCache(SENSOR_ID),
        getSensorValueFromCache(SENSOR_ID).equals(Sensor.UNKNOWN_STATUS)
    );

    
    Assert.assertTrue(s1.getSensorID() == SENSOR_ID);
    Assert.assertTrue(s1.isPolling());
    Assert.assertFalse(s1.isEventListener());
    Assert.assertTrue(s1.getName().equals("unknown"));
    Assert.assertTrue(s1.getProperties().size() == 0);
  }


  /**
   * Test translations of switch states.
   *
   * @throws Exception if test fails
   */
  @Test public void testSwitchStateMapping() throws Exception
  {
    SwitchSensor.DistinctStates mapping = new SwitchSensor.DistinctStates();
    mapping.addStateMapping("off", "close");
    mapping.addStateMapping("on", "open");

    final int SENSOR_ID = 4;

    SwitchSensor s1 = new SwitchSensor("door sensor", SENSOR_ID, cache, new SwitchReadCommand("door sensor", SENSOR_ID, "on"), mapping);

    cache.registerSensor(s1);
    s1.start();

    Assert.assertTrue(
        "Expected 'open', got " + getSensorValueFromCache(SENSOR_ID),
        getSensorValueFromCache(SENSOR_ID).equals("open")
    );

    Assert.assertTrue(s1.getSensorID() == SENSOR_ID);
    Assert.assertTrue(s1.isPolling());
    Assert.assertFalse(s1.isEventListener());
    Assert.assertTrue(s1.getName().equals("door sensor"));
    Assert.assertTrue(s1.getProperties().size() == 0);


    final int SENSOR_ID2 = 5;

    SwitchSensor s2 = new SwitchSensor("door", SENSOR_ID2, cache, new SwitchReadCommand("door", SENSOR_ID2, "off"), mapping);

    cache.registerSensor(s2);
    s2.start();

    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID2).equals("close"));

    Assert.assertTrue(s2.getSensorID() == SENSOR_ID2);
    Assert.assertFalse(s2.isEventListener());
    Assert.assertTrue(s2.isPolling());
    Assert.assertTrue(s2.getName().equals("door"));
    Assert.assertTrue(s2.getProperties().size() == 0);



    final int SENSOR_ID3 = 6;

    SwitchSensor s3 = new SwitchSensor("unknown", SENSOR_ID3, cache, new SwitchReadCommand("unknown", SENSOR_ID3, "bar"), mapping);

    cache.registerSensor(s3);
    s3.start();

    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID3).equals(StatusCommand.UNKNOWN_STATUS));

    Assert.assertTrue(s3.getSensorID() == SENSOR_ID3);
    Assert.assertTrue(s3.isPolling());
    Assert.assertFalse(s3.isEventListener());
    Assert.assertTrue(s3.getName().equals("unknown"));
    Assert.assertTrue(s3.getProperties().size() == 0);
  }


  /**
   * Test switch behavior against a broken event producer.
   *
   * @throws Exception if test fails
   */
  @Test public void testBrokenCommand() throws Exception
  {
    final int SENSOR_ID = 7;

    SwitchSensor s1 = new SwitchSensor("broken", SENSOR_ID, cache, new BrokenCommand());

    cache.registerSensor(s1);
    s1.start();

    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals(Sensor.UNKNOWN_STATUS));
    Assert.assertTrue(s1.getSensorID() == SENSOR_ID);
    Assert.assertTrue(s1.isPolling());
    Assert.assertFalse(s1.isEventListener());
    Assert.assertTrue(s1.getName().equals("broken"));
    Assert.assertTrue(s1.getProperties().size() == 0);
  }


  @Test public void testMixedMapping()
  {

    // test mapping when only one of the two states is mapped

    Assert.fail("Not Yet Implemented.");
  }


  @Test public void testNullArgs()
  {

    // test null args on constructor etc

    Assert.fail("Not Yet Implemented.");
  }


  // Helpers --------------------------------------------------------------------------------------

  private String getSensorValueFromCache(int sensorID) throws Exception
  {
    // sleep here to give the polling mechanism enough time to push the event value to cache...

    Thread.sleep(ReadCommand.POLLING_INTERVAL * 2);

    return cache.queryStatus(sensorID);
  }


  // Nested Classes -------------------------------------------------------------------------------

  private static class SwitchReadCommand extends ReadCommand
  {

    private String returnValue;
    private String name;
    private int id;

    SwitchReadCommand(String name, int id, String returnValue)
    {
      this.returnValue = returnValue;
      this.name = name;
      this.id = id;
    }

    public String read(Sensor s)
    {
      Assert.assertTrue(s instanceof SwitchSensor);
      Assert.assertTrue(s.getName().equals(name));
      Assert.assertTrue(s.getSensorID() == id);
      Assert.assertTrue(s.getProperties().size() == 0);

      return returnValue;
    }
  }

  private static class BrokenCommand extends ReadCommand
  {

    @Override public String read(Sensor s)
    {
      throw new NullPointerException("shouldve been handled");
    }
  }
}

