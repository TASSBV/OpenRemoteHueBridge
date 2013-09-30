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
package org.openremote.controller.statuscache;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import org.openremote.controller.protocol.Event;
import org.openremote.controller.protocol.ReadCommand;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.model.event.Switch;
import org.openremote.controller.model.event.Level;
import org.openremote.controller.model.event.Range;
import org.openremote.controller.model.event.CustomState;
import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.component.LevelSensor;

import junit.framework.Assert;

/**
 * Unit tests for {@link StatusCache} class.
 *
 * @author @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class StatusCacheTest
{


  // QueryStatus(ID) Tests ------------------------------------------------------------------------

  
  /**
   * Simple run-through test of queryStatus() method.
   */
  @Test public void testQueryStatus()
  {
    StatusCache cache = new StatusCache();

    Switch switchEvent = new Switch(1, "test", "foo", Switch.State.ON);

    cache.update(switchEvent);

    Assert.assertTrue(cache.queryStatus(1).equals("foo"));
  }

  /**
   * Test response on a unknown sensor id.
   */
  @Test public void testQueryStatusNonExistingStatus()
  {
    StatusCache cache = new StatusCache();

    Assert.assertTrue(Sensor.isUnknownSensorValue(cache.queryStatus(0)));
  }


  
  // RegisterSensor Tests -------------------------------------------------------------------------


  /**
   * Test registration on two different types of sensors -- level and range
   */
  @Test public void testRegisterSensor()
  {
    StatusCache cache = new StatusCache();

    DummyCommand cmd1 = new DummyCommand("0");

    Sensor s1 = new RangeSensor("test1", 1, cache, cmd1, -10, 10);

    cache.registerSensor(s1);

    Assert.assertTrue(cache.getSensor(1).equals(s1));
    Assert.assertTrue(Sensor.isUnknownSensorValue(cache.queryStatus(1)));

    s1.start();

    waitForUpdate();

    Assert.assertTrue(cache.queryStatus(1).equals("0"));


    DummyCommand cmd2 = new DummyCommand("10");

    Sensor s2 = new LevelSensor("test2", 2, cache, cmd2);

    cache.registerSensor(s2);

    Assert.assertTrue(cache.getSensor(2).equals(s2));
    Assert.assertTrue(Sensor.isUnknownSensorValue(cache.queryStatus(2)));


    s2.start();

    waitForUpdate();

    Assert.assertTrue(cache.queryStatus(2).equals("10"));
  }


  @Test public void testDuplicateRegistration()
  {
    Assert.fail("Not Yet Implemented -- see ORCJAVA-204 (http://jira.openremote.org/browse/ORCJAVA-204)");
  }

  @Test public void testNullRegistration()
  {
    Assert.fail("Not Yet Implemented -- see ORCJAVA-204 (http://jira.openremote.org/browse/ORCJAVA-204)");
  }



  /**
   * Basic sensor update test
   */
  @Test public void testUpdate()
  {
    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain epc = new EventProcessorChain();

    List<EventProcessor> processors = new ArrayList<EventProcessor>();
    TestProcessor tp = new TestProcessor();

    processors.add(tp);

    epc.setEventProcessors(processors);

    StatusCache cache = new StatusCache(cst, epc);

    DummyCommand cmd = new DummyCommand("on");

    Sensor s = new SwitchSensor("test-sensor", 2, cache, cmd);

    cache.registerSensor(s);

    Assert.assertTrue(Sensor.isUnknownSensorValue(cache.queryStatus(2)));

    s.start();

    waitForUpdate();

    Assert.assertTrue(cache.queryStatus(2).equals("on"));
  }

  /**
   *  Basic switch event update/query tests. Query by ID.
   */
  @Test public void testSwitchEventUpdate()
  {
    StatusCache cache = new StatusCache();

    Switch sw1 = new Switch(1, "foo", "on", Switch.State.ON);

    cache.update(sw1);

    String currentStatus = cache.queryStatus(1);

    Assert.assertTrue(currentStatus.equals("on"));



    Switch sw2 = new Switch(1, "foo", "off", Switch.State.OFF);

    Assert.assertFalse(sw1.isEqual(sw2));
    Assert.assertFalse(sw2.isEqual(sw1));
    
    cache.update(sw2);

    currentStatus = cache.queryStatus(1);

    Assert.assertTrue(
        "Expected 'off', got '" + currentStatus + "'",
        currentStatus.equals("off")
    );



    cache.update(sw2);

    currentStatus = cache.queryStatus(1);

    Assert.assertTrue(
        "Expected 'off', got '" + currentStatus + "'",
        currentStatus.equals("off")
    );

    Switch sw3 = new Switch(1, "foo", "closed", Switch.State.OFF);

    cache.update(sw3);

    currentStatus = cache.queryStatus(1);

    Assert.assertTrue(
        "Expected 'closed', got '" + currentStatus + "'",
        currentStatus.equals("closed")
    );



    cache.update(sw1);

    currentStatus = cache.queryStatus(1);

    Assert.assertTrue(currentStatus.equals("on"));
  }


  /**
   * Basic level event update/query tests. Query by ID.
   *
   * @throws Exception if test fails
   */
  @Test public void testLevelEventUpdate() throws Exception
  {
    StatusCache cache = new StatusCache();

    Level l1 = new Level(10, "level", 10);

    cache.update(l1);

    String currentStatus = cache.queryStatus(10);

    Assert.assertTrue(currentStatus.equals("10"));





    Level l2 = new Level(10, "level", 11);

    Assert.assertFalse(l1.isEqual(l2));
    Assert.assertFalse(l2.isEqual(l1));

    cache.update(l2);

    currentStatus = cache.queryStatus(10);

    Assert.assertTrue(
        "Expected '11', got '" + currentStatus + "'",
        currentStatus.equals("11")
    );




    cache.update(l2);

    currentStatus = cache.queryStatus(10);

    Assert.assertTrue(
        "Expected '11', got '" + currentStatus + "'",
        currentStatus.equals("11")
    );

    Level l3 = new Level(10, "level", 20);

    cache.update(l3);

    currentStatus = cache.queryStatus(10);

    Assert.assertTrue(
        "Expected '20', got '" + currentStatus + "'",
        currentStatus.equals("20")
    );



    cache.update(l1);

    currentStatus = cache.queryStatus(10);

    Assert.assertTrue(currentStatus.equals("10"));
  }




  /**
   * TODO : query by *NAME*
   *
   * @throws Exception if test fails
   */
  @Test public void testLevelEventQuery() throws Exception
  {
    StatusCache cache = new StatusCache();

    Level l1 = new Level(10, "l1", 10);

    cache.update(l1);

    String currentStatus = cache.queryStatus(10);

    Assert.assertTrue(currentStatus.equals("10"));

    Event currentStatusEvent = cache.queryStatus("l1");

    Assert.assertTrue(currentStatusEvent.isEqual(l1));
    Assert.assertTrue(currentStatusEvent.getValue().equals(10));
    Assert.assertTrue(currentStatusEvent.serialize().equals("10"));


//
//    Switch sw2 = new Switch(1, "foo", "off", Switch.State.OFF);
//
//    Assert.assertFalse(sw1.isEqual(sw2));
//    Assert.assertFalse(sw2.isEqual(sw1));
//
//    cache.update(sw2);
//
//    currentStatus = cache.queryStatus(1);
//
//    Assert.assertTrue(
//        "Expected 'off', got '" + currentStatus + "'",
//        currentStatus.equals("off")
//    );
//
//
//
//    cache.update(sw2);
//
//    currentStatus = cache.queryStatus(1);
//
//    Assert.assertTrue(
//        "Expected 'off', got '" + currentStatus + "'",
//        currentStatus.equals("off")
//    );
//
//    Switch sw3 = new Switch(1, "foo", "closed", Switch.State.OFF);
//
//    cache.update(sw3);
//
//    currentStatus = cache.queryStatus(1);
//
//    Assert.assertTrue(
//        "Expected 'closed', got '" + currentStatus + "'",
//        currentStatus.equals("closed")
//    );
//
//
//
//    cache.update(sw1);
//
//    currentStatus = cache.queryStatus(1);
//
//    Assert.assertTrue(currentStatus.equals("on"));    
  }




  /**
   * Test that sensor events are pushed through event processor chain
   */
  @Test public void testEventProcessorPassThrough()
  {
    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain epc = new EventProcessorChain();

    List<EventProcessor> processors = new ArrayList<EventProcessor>();
    ValueProcessor vp1 = new ValueProcessor("acme");
    ValueProcessor vp2 = new ValueProcessor("acme");

    processors.add(vp1);
    processors.add(vp2);

    epc.setEventProcessors(processors);

    StatusCache cache = new StatusCache(cst, epc);

    Event evt = new Switch(3, "test", "acme", Switch.State.ON);

    cache.update(evt);

    Assert.assertTrue(cache.queryStatus(3).equals("acme"));
  }

  /**
   * Test event replace within event processor chain.
   */
  @Test public void testEventProcessorEventMod()
  {
    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain epc = new EventProcessorChain();

    List<EventProcessor> processors = new ArrayList<EventProcessor>();
    ValueProcessor vp1 = new ValueProcessor("acme");
    OnReplacer rep = new OnReplacer();

    processors.add(vp1);
    processors.add(rep);

    epc.setEventProcessors(processors);

    StatusCache cache = new StatusCache(cst, epc);

    Event evt = new Switch(4, "test4", "acme", Switch.State.ON);

    cache.update(evt);

    Assert.assertTrue(
        "Expected 'on', got '" + cache.queryStatus(4) + "'.",
        cache.queryStatus(4).equals("on")
    );
  }


  /**
   * Test update of an event that does not have an associated sensor bound to the same id.
   *
   * NOTE : These semantics may be not useful as long as we operate on IDs on REST interfaces
   *        rather than logical names/properties. However, codifying this behavior in unit
   *        tests for now for possible future use-cases.
   *                                                                        [JPL]
   */
  @Test public void testUpdateUndefinedSensorID()
  {
    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain epc = new EventProcessorChain();

    List<EventProcessor> processors = new ArrayList<EventProcessor>();
    TestProcessor tp = new TestProcessor();

    processors.add(tp);

    epc.setEventProcessors(processors);

    StatusCache cache = new StatusCache(cst, epc);

    Event evt = new Switch(1, "test", "foo", Switch.State.ON);

    cache.update(evt);

    Assert.assertTrue(cache.queryStatus(1).equals("foo"));
  }


  /**
   * TODO:
   *
   *   This test exists to document the current behavior of StatusCache use of ChangedStatusTable
   *   implementation. The functionality of ChangedStatusTable and ChangedStatusRecord should
   *   be internalized as part of StatusCache implementation, as per ORCJAVA-208 -- this test is
   *   likely in need of modification at that point.
   */
  @Test public void testUpdateStatusChange()
  {
    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain epc = new EventProcessorChain();

    StatusCache cache = new StatusCache(cst, epc);


    // One event update to a single device...

    Event evt = new Switch(1, "test1", "foo", Switch.State.OFF);

    Set<Integer> pollingSensorIDs = new TreeSet<Integer>();
    pollingSensorIDs.add(1);

    cst.insert(new ChangedStatusRecord("My Device-1", pollingSensorIDs));

    cache.update(evt);

    ChangedStatusRecord record = cst.query("My Device-1");

    Assert.assertTrue(record.getRecordKey().equals("My Device-1"));
    Assert.assertTrue(record.getPollingSensorIDs().contains(1));
    Assert.assertTrue(record.getPollingSensorIDs().size() == 1);
    Assert.assertTrue(record.getStatusChangedSensorIDs().contains(1));
    Assert.assertTrue(record.getStatusChangedSensorIDs().size() == 1);




    // One event update on a request of 3 sensor IDs (same device)...

    evt = new Level(30, "test30", 30);

    pollingSensorIDs = new TreeSet<Integer>();
    pollingSensorIDs.add(10);
    pollingSensorIDs.add(20);
    pollingSensorIDs.add(30);
    pollingSensorIDs.add(40);

    cst.insert(new ChangedStatusRecord("Another Device-10,20,30,40", pollingSensorIDs));

    cache.update(evt);

    pollingSensorIDs.remove(new Integer(10));

    record = cst.query("Another Device-10,20,30,40");

    Assert.assertTrue(record.getRecordKey().equals("Another Device-10,20,30,40"));
    Assert.assertTrue(record.getPollingSensorIDs().contains(20));
    Assert.assertTrue(record.getPollingSensorIDs().contains(30));
    Assert.assertTrue(record.getPollingSensorIDs().contains(40));
    Assert.assertTrue(record.getPollingSensorIDs().size() == 3);
    Assert.assertTrue(record.getStatusChangedSensorIDs().contains(30));
    Assert.assertTrue(record.getStatusChangedSensorIDs().size() == 1);




    // Two event updates on a request of 4 sensor IDs (same device)...
    
    Event evt1 = new Range(100, "test100", 100, -1000, 1000);
    Event evt2 = new CustomState(200, "test200", "acme");


    pollingSensorIDs = new TreeSet<Integer>();
    pollingSensorIDs.add(100);
    pollingSensorIDs.add(200);
    pollingSensorIDs.add(300);
    pollingSensorIDs.add(400);

    cst.insert(new ChangedStatusRecord("Third Device-100,200,300,400", pollingSensorIDs));

    cache.update(evt1);
    cache.update(evt2);

    record = cst.query("Third Device-100,200,300,400");

    Assert.assertTrue(record.getRecordKey().equals("Third Device-100,200,300,400"));
    Assert.assertTrue(record.getPollingSensorIDs().contains(100));
    Assert.assertTrue(record.getPollingSensorIDs().contains(200));
    Assert.assertTrue(record.getPollingSensorIDs().contains(300));
    Assert.assertTrue(record.getPollingSensorIDs().contains(400));
    Assert.assertTrue(record.getPollingSensorIDs().size() == 4);
    Assert.assertTrue(record.getStatusChangedSensorIDs().contains(100));
    Assert.assertTrue(record.getStatusChangedSensorIDs().contains(200));
    Assert.assertTrue(record.getStatusChangedSensorIDs().size() == 2);
  }


  
  // Helper Methods -------------------------------------------------------------------------------

  private void waitForUpdate()
  {
    try
    {
      Thread.sleep(ReadCommand.POLLING_INTERVAL * 2);
    }

    catch (InterruptedException e)
    {
      Assert.fail(e.getMessage());
    }
  }


  // Nested Classes -------------------------------------------------------------------------------


  private static class DummyCommand extends ReadCommand
  {
    private String value;

    DummyCommand(String value)
    {
      this.value = value;
    }

    public String read(Sensor s)
    {
      return value;
    }
  }


  private static class TestProcessor extends EventProcessor
  {
    @Override public void push(EventContext ctx) { }

    @Override public String getName()
    {
      return "Test Processor";
    }
  }

  private static class OnReplacer extends EventProcessor
  {
    @Override public void push(EventContext ctx)
    {
      Event evt = ctx.getEvent();

      if (evt instanceof Switch)
      {
        Switch sw = (Switch)evt;
        sw.setValue("on");
      }
    }

    @Override public String getName()
    {
      return "OnReplacer";
    }
  }

  private static class ValueProcessor extends EventProcessor
  {
    private String eventValue;

    ValueProcessor(String val)
    {
      eventValue = val;
    }

    @Override public void push(EventContext ctx)
    {
      Assert.assertTrue(ctx.getEvent().getValue().equals(eventValue));
    }

    @Override public String getName()
    {
      return "ValueProcessor";
    }
  }




///**
// *
// * This class is mainly used to test the <b>SkipStateTrack</b>.<br /><br />
// *
// * There is a <b>ods file</b> named <b>SkipStateTrackTest.ods</b> in current directory.<br />
// * The file <b>SkipStateTrackTest.ods</b> contains several situations of skip-state tracking.<br />
// * So, the following methods depend on these situations in SkipStateTrackTest.ods descriped.<br /><br />
// *
// * <b>NOTE: Start tomcat firstly.</b>
// *
// */
//
//   private Logger logger = Logger.getLogger(this.getClass().getName());
//
//   /**
//    * <b>Situation1:</b><br />
//    *
//    *  Not found time out record in TIME_OUT_TABLE during polling operation,<br />
//    *  not timeout while observing and Getting the changed status at last.
//    */
//   @Test
//   public void testCase1() throws Exception {
//      WebConversation wc = new WebConversation();
//      WebRequest pollingGetMethodRequest = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + AllTests.WEBAPP_PORT + "/controller/rest/polling/96e79218965eb72c92a549dd5a330112/1001");
//      try {
//         WebResponse wr = wc.getResponse(pollingGetMethodRequest);
//         logger.info("The result was : \n" + wr.getText());
//      } catch (HttpException e) {
//         if (e.getResponseCode() == 504) {
//            logger.info("Polling request was timeout.");
//         }
//      }
//   }
//
//   /**
//    * <b>Situation2:</b><br />
//    *
//    * <b>First polling request:</b> Not found time out record in TIME_OUT_TABLE during polling operation,<br />
//    * timeout while observing, this time out request will record into TIME_OUT_TABLE,<br />
//    * client gets 503 error at last.<br /><br />
//    *
//    * <b>Second Polling request:</b> Found previous time out record in TIME_OUT_TABLE,<br />
//    * gets the changed status with <b>the value of column STATUS_CHANGED_IDS in TIME_OUT_TABLE</b> from<br />
//    * CachedStatus table(currently it's simulated).<br /><br />
//    *
//    * <b>NOTE:</b> This situation must work with method <b>simulateSkipStateTrackTestCase2</b> which was called<br />
//    * while <b>InitCachedStatusDBListener</b> starting.
//    */
//   @Test
//   public void testCase2() throws Exception {
//      WebConversation wc = new WebConversation();
//      WebRequest pollingGetMethodRequest = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + AllTests.WEBAPP_PORT + "/controller/rest/polling/96e79218965eb72c92a549dd5a330112/1002");
//      try {
//         WebResponse wr = wc.getResponse(pollingGetMethodRequest);
//         logger.info("The result was : \n" + wr.getText());
//      } catch (HttpException e) {
//         if (e.getResponseCode() == 504) {
//            logger.info("Polling request was timeout.");
//         }
//      }
//   }
//
//   /**
//    * <b>Situation3:</b><br /><br />
//    *
//    * <b>First polling request:</b> Not found time out record in TIME_OUT_TABLE during polling operation,<br />
//    * timeout while observing, this time out request will record into TIME_OUT_TABLE,<br />
//    * client gets 503 error at last.<br /><br />
//    *
//    * <b>Second polling request:</b> Found previous time out record in TIME_OUT_TABLE,<br />
//    * but the statuses which previous polling request care about didn't change.<br />
//    * So, current polling request observes the change of statuses and gets the changed status at last.<br /><br />
//    *
//    * <b>NOTE:</b> This situation must work with method <b>simulateSkipStateTrackTestCase3</b> which was called<br />
//    * while <b>InitCachedStatusDBListener</b> starting.
//    */
//   @Test
//   public void testCase3() throws Exception {
//      WebConversation wc = new WebConversation();
//      WebRequest pollingGetMethodRequest = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + AllTests.WEBAPP_PORT + "/controller/rest/polling/96e79218965eb72c92a549dd5a330112/1003");
//      try {
//         WebResponse wr = wc.getResponse(pollingGetMethodRequest);
//         logger.info("The result was : \n" + wr.getText());
//      } catch (HttpException e) {
//         if (e.getResponseCode() == 504) {
//            logger.info("Polling request was timeout.");
//         }
//      }
//   }
//
//   /**
//    * <b>Situation4:</b><br /><br />
//    *
//    * <b>First polling request:</b> Not found time out record in TIME_OUT_TABLE during polling operation,<br />
//    * timeout while observing, this time out request will record into TIME_OUT_TABLE,<br />
//    * client gets 503 error at last.<br /><br />
//    *
//    * <b>Second polling request:</b> Found previous time out record in TIME_OUT_TABLE,<br />
//    * but the statuses which previous polling request care about didn't change.<br />
//    * So, current polling request observes the change of statuses but timeout,<br />
//    * client gets 503 error at last.<br /><br />
//    */
//   //@Test
//   public void testCase4() throws Exception {
//      WebConversation wc = new WebConversation();
//      WebRequest pollingGetMethodRequest = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + AllTests.WEBAPP_PORT + "/controller/rest/polling/96e79218965eb72c92a549dd5a330112/1004");
//      try {
//         WebResponse wr = wc.getResponse(pollingGetMethodRequest);
//         logger.info("The result was : \n" + wr.getText());
//      } catch (HttpException e) {
//         if (e.getResponseCode() == 504) {
//            logger.info("Polling request was timeout.");
//         }
//      }
//   }
}
