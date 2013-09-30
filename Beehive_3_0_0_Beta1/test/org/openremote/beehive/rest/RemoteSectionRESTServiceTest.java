package org.openremote.beehive.rest;

import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.openremote.beehive.LIRCTestBase;
import org.openremote.beehive.rest.service.RemoteSectionRESTTestService;
import org.openremote.beehive.utils.RESTTestUtils;

public class RemoteSectionRESTServiceTest extends LIRCTestBase {
   
   public void testGetRemoteSectionsXml() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(RemoteSectionRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/lirc/3m/MP8640");
      mockHttpRequest.accept(MediaType.APPLICATION_XML);

      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      System.out.println(mockHttpResponse.getContentAsString());

   }

   public void testGetRemoteSectionsJson() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(RemoteSectionRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/lirc/3m/MP8640");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);

      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      System.out.println(mockHttpResponse.getContentAsString());

   }

}
