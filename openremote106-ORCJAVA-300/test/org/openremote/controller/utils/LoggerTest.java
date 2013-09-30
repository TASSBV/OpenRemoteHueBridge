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
package org.openremote.controller.utils;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Level;

import org.junit.Test;
import org.junit.Assert;
import org.openremote.controller.Constants;

/**
 * Basic tests to check nothing gets messed up in the logging facade.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class LoggerTest
{

  /**
   * Just simply run through the method invocations to make sure nothing is badly broken.
   */
  @Test public void testErrorMethod()
  {
    Logger log = Logger.getLogger(Constants.CONTROLLER_ROOT_LOG_CATEGORY + ".test");

    log.error("Test message");

    log.error("Test {0} message", "this");

    log.error("test message", new RuntimeException("log error testing"));

    log.error("test {0} message with {1}", new RuntimeException("log error testing"), "error", this.getClass());
  }


  /**
   * Just simply run through the method invocations to make sure nothing is badly broken.
   */
  @Test public void testWarnMethod()
  {
    Logger log = Logger.getLogger(Constants.CONTROLLER_ROOT_LOG_CATEGORY + ".test");

    log.warn("Test message");

    log.warn("Test {0} message", "this");

    log.warn("test message", new RuntimeException("log warn testing"));

    log.warn("test {0} message with {1}", new RuntimeException("log warn testing"), "warn", this.getClass());
  }




  /**
   * Just simply run through the method invocations to make sure nothing is badly broken.
   */
  @Test public void testInfoMethod()
  {
    Logger log = Logger.getLogger(Constants.CONTROLLER_ROOT_LOG_CATEGORY + ".test");

    log.info("Test message");

    log.info("Test {0} message", "this");

    log.info("test message", new RuntimeException("log info testing"));

    log.info("test {0} message with {1}", new RuntimeException("log info testing"), "info", this.getClass());
  }


  /**
   * Just simply run through the method invocations to make sure nothing is badly broken.
   */
  @Test public void testDebugMethod()
  {
    Logger log = Logger.getLogger(Constants.CONTROLLER_ROOT_LOG_CATEGORY + ".test");

    log.debug("Test message");

    log.debug("Test {0} message", "this");

    log.debug("test message", new RuntimeException("log debug testing"));

    log.debug("test {0} message with {1}", new RuntimeException("log debug testing"), "debug", this.getClass());
  }


  /**
   * TODO : trace not yet implemented
   */
  @Test public void testTraceMethod()
  {
    throw new Error("Not Yet Implemented");

  }
  

  /**
   * Test behavior with null args on logger error facade.
   */
  @Test public void testNullArgsError()
  {
    Logger log = Logger.getLogger(Constants.CONTROLLER_ROOT_LOG_CATEGORY + ".test");

    log.error(null);

    log.error(null, new Object[] {null, null});

    log.error("Test error {0} and {1}", new Object[] {null, null});

    log.error(null, new RuntimeException("test error null args"));

    log.error(null, null, "arg");

    log.error(null, new RuntimeException("test err null args"), (Object[])null);

    log.error("error msg", (Exception)null);

    log.error(null, "error", "arg");
  }

  /**
   * Test behavior with null args on logger warn facade.
   */
  @Test public void testNullArgsWarn()
  {
    Logger log = Logger.getLogger(Constants.CONTROLLER_ROOT_LOG_CATEGORY + ".test");

    log.warn(null);

    log.warn(null, new Object[] {null, null});

    log.warn("Test warn {0} and {1}", new Object[] {null, null});

    log.warn(null, new RuntimeException("test warn null args"));

    log.warn(null, null, "arg");

    log.warn(null, new RuntimeException("test warning null args"), (Object[])null);

    log.warn("warn msg", (Exception)null);

    log.warn(null, "warn", "arg");
  }

  /**
   * Test behavior with null args on logger info facade.
   */
  @Test public void testNullArgsInfo()
  {
    Logger log = Logger.getLogger(Constants.CONTROLLER_ROOT_LOG_CATEGORY + ".test");

    log.info(null);

    log.info(null, new Object[] {null, null});

    log.info("Test info {0} and {1}", new Object[] {null, null});

    log.info(null, new RuntimeException("test info null args"));

    log.info(null, null, "arg");

    log.info(null, new RuntimeException("test information null args"), (Object[])null);

    log.info("info msg", (Exception)null);

    log.info(null, "info", "arg");
  }


  /**
   * Test behavior with null args on logger debug facade.
   */
  @Test public void testNullArgsDebug()
  {
    Logger log = Logger.getLogger(Constants.CONTROLLER_ROOT_LOG_CATEGORY + ".test");

    log.debug(null);

    log.debug(null, new Object[] {null, null});

    log.debug("Test debug {0} and {1}", new Object[] {null, null});

    log.debug(null, new RuntimeException("test debug null args"));

    log.debug(null, null, "arg");

    log.debug(null, new RuntimeException("test debugging null args"), (Object[])null);

    log.debug("debug msg", (Exception)null);

    log.debug(null, "debug", "arg");
  }


  /**
   * Test behavior with null args on logger debug facade.
   */
  @Test public void testFunkyMsgFormatting()
  {
    Logger log = Logger.getLogger(Constants.CONTROLLER_ROOT_LOG_CATEGORY + ".test");

    log.error("Test error {0, date} and {1, integer, currency}", "foo", "bar");
    log.warn("Test warn {0, foo} and {1, bar}", null, 1);
    log.info("Test debug {0, number} and {1, number, percentage}", "foo", "bar");
    log.debug("Test debug {0}", "foo", "bar");
  }


  /**
   * Tests mapping of error logging to JUL levels.
   */
  @Test public void testErrorMapping()
  {
    Logger log = Logger.getLogger(Constants.CONTROLLER_ROOT_LOG_CATEGORY + ".test.errorhandler");
    TestLogHandler handler = new TestLogHandler();

    log.addHandler(handler);

    String msg = "Test error {0}, {1}";

    log.error(msg, "foo", "bar");

    handler.assertLastLog(Level.SEVERE, msg);
  }

  /**
   * Tests mapping of warn logging to JUL levels.
   */
  @Test public void testWarnMapping()
  {
    Logger log = Logger.getLogger(Constants.CONTROLLER_ROOT_LOG_CATEGORY + ".test.warnhandler");
    TestLogHandler handler = new TestLogHandler();

    log.addHandler(handler);

    String msg = "Test warn {0}, {1}";

    log.warn(msg, "foo", "bar");

    handler.assertLastLog(Level.WARNING, msg);
  }


  /**
   * Tests mapping of info logging to JUL levels.
   */
  @Test public void testInfoMapping()
  {
    Logger log = Logger.getLogger(Constants.CONTROLLER_ROOT_LOG_CATEGORY + ".test.infohandler");
    TestLogHandler handler = new TestLogHandler();

    log.addHandler(handler);

    String msg = "Test info {0}, {1}";

    log.info(msg, "foo", "bar");

    handler.assertLastLog(Level.INFO, msg);
  }


  /**
   * Tests mapping of debug logging to JUL levels.
   */
  @Test public void testDebugMapping()
  {
    Logger log = Logger.getLogger(Constants.CONTROLLER_ROOT_LOG_CATEGORY + ".test.debughandler");
    TestLogHandler handler = new TestLogHandler();

    log.addHandler(handler);
    log.setLevel(Level.ALL);
    
    String msg = "Test debug {0}, {1}";

    log.debug(msg, "foo", "bar");

    handler.assertLastLog(Level.FINE, msg);
  }




  // Nested Classes -------------------------------------------------------------------------------


  private static class TestLogHandler extends Handler
  {
    private Level lastLevel;
    private String lastMessage;

    @Override public void publish(LogRecord record)
    {
      lastLevel = record.getLevel();
      lastMessage = record.getMessage();
    }

    @Override public void flush()
    {

    }

    @Override public void close()
    {

    }

    void assertLastLog(Level level, String msg)
    {

      Assert.assertTrue(
          "Expected log message '" + msg + "', got '" + lastMessage + "'.",
          msg.equals(lastMessage)
      );

      Assert.assertTrue(
          "Expected level " + level + ", got " + lastLevel,
          level.equals(lastLevel)
      );

    }
  }
  
}

