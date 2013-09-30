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
import org.openremote.controller.component.LevelSensor;
import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.EventProducer;
import org.openremote.controller.protocol.ReadCommand;
import org.openremote.controller.statuscache.ChangedStatusTable;
import org.openremote.controller.statuscache.EventProcessorChain;
import org.openremote.controller.statuscache.StatusCache;

/**
 * Basic tests on the {@link Sensor} superclass. Specific sensor implementations have their
 * own tests in their respective test classes.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class SensorTest
{

  /* share the same cache across all sensor tests */
  private StatusCache cache = null;


  @Before
  public void setup()
  {
    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain echain = new EventProcessorChain();

    cache = new StatusCache(cst, echain);
  }


  /**
   * Test sensor equality based on sensor ID.
   */
  @Test public void testEquals()
  {
    EventProducer ep1 = new TestEventProducer1();
    EventProducer ep2 = new TestEventProducer2();

    Sensor s1 = new RangeSensor("range", 1, cache, ep1, -50, 50);
    Sensor s2 = new LevelSensor("level", 1, cache, ep2);
    Sensor s3 = new RangeSensor("range", 1, cache, ep1, -20, 20);

    // there should be only one sensor with the same id which is the only one that matters in
    // equals comparison...

    Assert.assertTrue(s1.equals(s1));
    Assert.assertTrue(s1.equals(s2));
    Assert.assertTrue(s2.equals(s1));
    Assert.assertTrue(s1.equals(s3));
    Assert.assertTrue(s3.equals(s1));
    Assert.assertTrue(s3.equals(s2));
    Assert.assertTrue(s2.equals(s3));
    Assert.assertTrue(s2.equals(s2));
    Assert.assertTrue(s3.equals(s3));

    // And they all need to return same hash...

    Assert.assertTrue(s1.hashCode() == s2.hashCode());
    Assert.assertTrue(s2.hashCode() == s3.hashCode());

    // And different ID should not be equals...

    Sensor s4 = new RangeSensor("range", 2, cache, ep1, -50, 50);

    Assert.assertFalse(s1.equals(s4));
    Assert.assertFalse(s4.equals(s1));
    Assert.assertFalse(s2.equals(s4));
    Assert.assertFalse(s4.equals(s2));
    Assert.assertFalse(s3.equals(s4));
    Assert.assertFalse(s4.equals(s3));
  }


  /**
   * Check defensive argument processing with nulls.
   */
  @Test public void testNullProducerInConstructor()
  {
    try
    {
      Sensor s1 = new SwitchSensor("switch", 4, cache, null);

      Assert.fail("should not reach here");
    }
    catch (IllegalArgumentException e)
    {
      // expected..
    }

    try
    {
      Sensor s1 = new RangeSensor("range", 54, cache, null, 0, 0);

      Assert.fail("should not reach here");
    }
    catch (IllegalArgumentException e)
    {
      // expected..
    }

    try
    {
      Sensor s1 = new StateSensor("state", 455, cache, null, null);

      Assert.fail("should not reach here");
    }
    catch (IllegalArgumentException e)
    {
      // expected..
    }
  }

  /**
   * Ensure sensor state gets correctly initialized.
   */
  @Test public void testSensorInitialization()
  {
    Sensor s1 = new SwitchSensor("switch", 9, cache, new EventProducer() {});

    Assert.assertTrue(s1.getSensorID() == 9);
    Assert.assertTrue(s1.getName().equals("switch"));
    Assert.assertTrue(s1.getProperties().size() == 0);


    RangeSensor s2 = new RangeSensor("range", 99, cache, new EventProducer() {}, 0, 0);

    Assert.assertTrue(s2.getSensorID() == 99);
    Assert.assertTrue(s2.getMaxValue() == 0);
    Assert.assertTrue(s2.getMinValue() == 0);
    Assert.assertTrue(s2.getName().equals("range"));
    Assert.assertTrue(s2.getProperties().size() == 2);


    StateSensor.DistinctStates states = new StateSensor.DistinctStates();
    states.addState("one");
    states.addState("two");

    Sensor s3 = new StateSensor("state", 444, cache, new EventProducer() {}, states);

    Assert.assertTrue(s3.getSensorID() == 444);
    Assert.assertTrue(s3.getName().equals("state"));
    Assert.assertTrue(s3.getProperties().size() == 2);
    Assert.assertTrue(s3.getProperties().keySet().contains("state-1"));
    Assert.assertTrue(s3.getProperties().keySet().contains("state-2"));
    Assert.assertTrue(s3.getProperties().values().contains("one"));
    Assert.assertTrue(s3.getProperties().values().contains("two"));

    
    LevelSensor s4 = new LevelSensor("level", 993, cache, new EventProducer() {});

    Assert.assertTrue(s4.getSensorID() == 993);
    Assert.assertTrue(s4.getName().equals("level"));
    Assert.assertTrue(s4.getProperties().size() == 0);
    Assert.assertTrue(s4.getMaxValue() == 100);
    Assert.assertTrue(s4.getMinValue() == 0);
  }


  /**
   * Test that the binding of event producer to sensor works correctly.
   *
   * @throws Exception if test fails
   */
  @Test public void testSensorRead() throws Exception
  {
    Sensor s1 = new SwitchSensor("switch", 84, cache, new SwitchRead("switch", 84));

    cache.registerSensor(s1);
    s1.start();

    String returnValue = getSensorValueFromCache(84);

    Assert.assertTrue(returnValue.equals("on"));



    Sensor s2 = new RangeSensor("range", 33, cache, new RangeRead("range", 33, 0, 1), 0, 1);

    cache.registerSensor(s2);
    s2.start();

    returnValue = getSensorValueFromCache(33);

    Assert.assertTrue(returnValue.equals("0"));
  }


  /**
   * Test the distinction between polling read commands and listeners as event producers.
   */
  @Test public void testPollingVsListener()
  {
    Sensor s1 = new SwitchSensor("switch", 4555, cache, new Listener(4555));

    Assert.assertTrue(s1.isEventListener());
    Assert.assertFalse(s1.isPolling());

    Sensor s2 = new SwitchSensor("switch", 9933, cache, new SwitchRead("switch", 9933));

    Assert.assertTrue(s2.isPolling());
    Assert.assertFalse(s2.isEventListener());
    
  }




  // TODO : test contract on modifying sensor properties -- getProperties() method



  // Helpers --------------------------------------------------------------------------------------


  private String getSensorValueFromCache(int sensorID) throws Exception
  {
    // sleep here to give the polling mechanism enough time to push the event value to cache...

    Thread.sleep(ReadCommand.POLLING_INTERVAL * 2);

    return cache.queryStatus(sensorID);
  }

  
  // Nested Classes -------------------------------------------------------------------------------


  private static class SwitchRead extends ReadCommand
  {

    private String expectedName;
    private int expectedid;

    SwitchRead(String expectedName, int expectedid)
    {
      this.expectedName = expectedName;
      this.expectedid = expectedid;
    }

    public String read(Sensor s)
    {
      Assert.assertTrue(s instanceof SwitchSensor);
      Assert.assertTrue(s.getProperties().size() == 0);
      Assert.assertTrue(s.getSensorID() == expectedid);
      Assert.assertTrue(s.getName().equals(expectedName));

      return "on";
    }
  }


  private static class RangeRead extends ReadCommand
  {
    private String expectedName;
    private int expectedid;
    private int max;
    private int min;

    RangeRead(String expectedName, int expectedid, int min, int max)
    {
      this.expectedName = expectedName;
      this.expectedid = expectedid;
      this.min = min;
      this.max = max;
    }

    public String read(Sensor s)
    {
      Assert.assertTrue(s instanceof RangeSensor);
      Assert.assertTrue(s.getSensorID() == expectedid);
      Assert.assertTrue(s.getName().equals(expectedName));

      Assert.assertTrue(s.getProperties().size() == 2);
      Assert.assertTrue(s.getProperties().keySet().contains(Sensor.RANGE_MAX_STATE));
      Assert.assertTrue(s.getProperties().keySet().contains(Sensor.RANGE_MIN_STATE));
      Assert.assertTrue(s.getProperties().values().contains(Integer.toString(min)));
      Assert.assertTrue(s.getProperties().values().contains(Integer.toString(max)));

      RangeSensor r = (RangeSensor)s;
      Assert.assertTrue(r.getMaxValue() == 1);
      Assert.assertTrue(r.getMinValue() == 0);
      return "0";
    }
  }


  private static class Listener implements EventListener
  {
    private int id;

    Listener(int expectedID)
    {
      this.id = expectedID;
    }

    @Override public void setSensor(Sensor sensor)
    {
      Assert.assertTrue(sensor.getSensorID() == id);
    }

    @Override public void stop(Sensor sensor)
    {
      // no-op...
    }
  }





  private static class TestEventProducer1 implements EventProducer
  {

  }

  private static class TestEventProducer2 implements EventProducer
  {

  }
}

