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
package org.openremote.controller.statuscache;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openremote.controller.suite.AllTests;
import org.openremote.controller.utils.SecurityUtil;

import com.meterware.httpunit.HttpException;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * 
 * 
 * @author Handy.Wang 2009-10-28
 */
public class StatusAndPollingTest {

   private Logger logger = Logger.getLogger(this.getClass().getName());
   @Test
   public void testCase1() throws Exception {
      WebConversation wc = new WebConversation();
      WebRequest pollingGetMethodRequest = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + AllTests.WEBAPP_PORT + "/controller/rest/status/1001");
      WebResponse wr;
      try {
         wr = wc.getResponse(pollingGetMethodRequest);
         logger.info("The status result was : \n" + wr.getText());
      } catch (HttpException e1) {
         if (e1.getResponseCode() == 504) {
            logger.info("Polling request was timeout.");
            try {
               pollingGetMethodRequest = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + AllTests.WEBAPP_PORT + "/controller/rest/status/1001");
               wr = wc.getResponse(pollingGetMethodRequest);
               logger.info("The result was : \n" + wr.getText());
            } catch (HttpException e2) {
               if (e2.getResponseCode() == 504) {
                  logger.info("Polling request was timeout.");
               }
            }
         }
      }
   }
   @Test
   public void testCase2() throws Exception {
      WebConversation wc = new WebConversation();
      WebRequest pollingGetMethodRequest = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + AllTests.WEBAPP_PORT + "/controller/rest/status/1002");
      try {
         WebResponse wr = wc.getResponse(pollingGetMethodRequest);
         logger.info("The result was : \n" + wr.getText());
      } catch (HttpException e) {
         if (e.getResponseCode() == 504) {
            logger.info("Polling request was timeout.");
         }
      }
   }
   @Test
   public void testCase3() throws Exception {
      WebConversation wc = new WebConversation();
      WebRequest pollingGetMethodRequest = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + AllTests.WEBAPP_PORT + "/controller/rest/polling/96e79218965eb72c92a549dd5a330112/1003");
      try {
         WebResponse wr = wc.getResponse(pollingGetMethodRequest);
         logger.info("The result was : \n" + wr.getText());
      } catch (HttpException e) {
         if (e.getResponseCode() == 504) {
            logger.info("Polling request was timeout.");
         }
      }
   }
   //@Test
   public void testCase4() throws Exception {
      WebConversation wc = new WebConversation();
      WebRequest pollingGetMethodRequest = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + AllTests.WEBAPP_PORT + "/controller/rest/polling/96e79218965eb72c92a549dd5a330112/1004");
      try {
         WebResponse wr = wc.getResponse(pollingGetMethodRequest);
         logger.info("The result was : \n" + wr.getText());
      } catch (HttpException e) {
         if (e.getResponseCode() == 504) {
            logger.info("Polling request was timeout.");
         }
      }
   }
}
