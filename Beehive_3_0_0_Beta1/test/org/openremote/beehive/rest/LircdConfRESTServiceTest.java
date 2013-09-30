package org.openremote.beehive.rest;

import java.net.URISyntaxException;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.openremote.beehive.LIRCTestBase;
import org.openremote.beehive.rest.service.LircdConfRESTTestService;
import org.openremote.beehive.utils.RESTTestUtils;

public class LircdConfRESTServiceTest extends LIRCTestBase {
   
   public void testGetZeroLircdConf() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(LircdConfRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/lirc.conf");

      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      
      try {
         dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      } catch (Exception e) {
         return;
      }
      fail();
   }

   public void testGetOneLircdConf() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(LircdConfRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/lirc.conf?ids=1");

      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      System.out.println(mockHttpResponse.getContentAsString());
   }

   public void testGetTwoLircdConf() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(LircdConfRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/lirc.conf?ids=1,2");

      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      System.out.println(mockHttpResponse.getContentAsString());
   }

}
