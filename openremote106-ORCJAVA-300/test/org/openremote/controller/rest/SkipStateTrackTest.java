/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.rest;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openremote.controller.suite.AllTests;
import org.openremote.controller.utils.SecurityUtil;

import com.meterware.httpunit.HttpException;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import junit.framework.Assert;

/**
 * This class is mainly used to test the <b>SkipStateTrack</b>.<br /><br />
 * 
 * There is a <b>ods file</b> named <b>SkipStateTrackTest.ods</b> in current directory.<br />
 * The file <b>SkipStateTrackTest.ods</b> contains several situations of skip-state tracking.<br />
 * So, the following methods depend on these situations in SkipStateTrackTest.ods descriped.<br /><br />
 * 
 * <b>NOTE: Start tomcat firstly.</b>
 * 
 * @author Handy.Wang 2009-10-26
 */
public class SkipStateTrackTest {

  // TODO :
  //
  //    - commenting out these tests for now for a few reasons:
  //
  //      1) I don't know what they are trying to accomplish
  //      2) They do not make any assertions, only logging, which makes them
  //         completely unusable for *automated* unit tests
  //      3) They tend to run for long time making it seem like tests are
  //         hanging (and at times they do seem to hang indefinitely)
  //
  //                                                                      [JPL]

  @Test public void testNeedsRewrite()
  {
    Assert.fail("Tests should be rewritten with proper assertions to automate them.");
  }


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
//      WebRequest request = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + AllTests.WEBAPP_PORT + "/controller/rest/polling/96e79218965eb72c92a549dd5a330112/1001");
//      try {
//         WebResponse wr = wc.getResponse(request);
//         System.out.println("The result is : \n" + wr.getText());
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
//      WebRequest request = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + AllTests.WEBAPP_PORT + "/controller/rest/polling/96e79218965eb72c92a549dd5a330112/1002");
//      WebResponse wr;
//      try {
//         wr = wc.getResponse(request);
//         System.out.println("The result of first polling request is : \n" + wr.getText());
//      } catch (HttpException e) {
//         if (e.getResponseCode() == 504) {
//            logger.info("Polling request was timeout.");
//            try {
//               wr = wc.getResponse(request);
//               System.out.println("The result of second polling request is : \n" + wr.getText());
//            } catch (HttpException e2) {
//               if (e2.getResponseCode() == 504) {
//                  logger.info("Polling request was timeout.");
//               }
//            }
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
//      WebRequest request = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + AllTests.WEBAPP_PORT + "/controller/rest/polling/96e79218965eb72c92a549dd5a330112/1003");
//      WebResponse wr;
//      try {
//         wr = wc.getResponse(request);
//         System.out.println("The result of first polling request is : \n" + wr.getText());
//      } catch (HttpException e2) {
//         if (e2.getResponseCode() == 504) {
//            logger.info("Polling request was timeout.");
//            try {
//               wr = wc.getResponse(request);
//               System.out.println("The result of second polling request is : \n" + wr.getText());
//            } catch (HttpException e1) {
//               if (e1.getResponseCode() == 504) {
//                  logger.info("Polling request was timeout.");
//               }
//            }
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
//   @Test
//   public void testCase4() throws Exception {
//      WebConversation wc = new WebConversation();
//      WebRequest request = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + AllTests.WEBAPP_PORT + "/controller/rest/polling/96e79218965eb72c92a549dd5a330112/1004");
//      WebResponse wr;
//      try {
//         wr = wc.getResponse(request);
//         System.out.println("The result of first polling request is : \n" + wr.getText());
//      } catch (HttpException e1) {
//         if (e1.getResponseCode() == 504) {
//            logger.info("Polling request was timeout.");
//            try {
//               wr = wc.getResponse(request);
//               System.out.println("The result of second polling request is : \n" + wr.getText());
//            } catch (HttpException e2) {
//               if (e2.getResponseCode() == 504) {
//                  logger.info("Polling request was timeout.");
//               }
//            }
//         }
//      }
//   }
}
