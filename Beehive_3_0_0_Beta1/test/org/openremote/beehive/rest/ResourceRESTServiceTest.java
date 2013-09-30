/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.beehive.rest;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.openremote.beehive.Constant;
import org.openremote.beehive.TemplateTestBase;
import org.openremote.beehive.rest.service.ResourceRESTTestService;
import org.openremote.beehive.utils.FixtureUtil;
import org.openremote.beehive.utils.RESTTestUtils;

public class ResourceRESTServiceTest extends TemplateTestBase {
   
   public void testDownload() throws URISyntaxException {
      
      File dowanloadedZip = FixtureUtil.getFile(Constant.ACCOUNT_RESOURCE_ZIP_NAME);
      dowanloadedZip.deleteOnExit();
      assertEquals(false, dowanloadedZip.exists());
      
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(ResourceRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/user/dan/" + Constant.ACCOUNT_RESOURCE_ZIP_NAME);
      mockHttpRequest.accept("application/zip");
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      
      try {
         FileUtils.writeByteArrayToFile(dowanloadedZip, mockHttpResponse.getOutput());
      } catch (IOException e) {
         fail();
      }
      System.out.println(dowanloadedZip.getAbsolutePath());
      assertEquals(true, dowanloadedZip.exists());
   }
   
   public void testDownloadWithoutAuth() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(ResourceRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/user/dan/" + Constant.ACCOUNT_RESOURCE_ZIP_NAME);
      mockHttpRequest.accept("application/zip");
      
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
//      assertEquals(401, mockHttpResponse.getStatus());
   }
   
   public void testGetAllPanels() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(ResourceRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/user/dan/panels");
      mockHttpRequest.accept(MediaType.APPLICATION_XML);
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      System.out.println(mockHttpResponse.getContentAsString());
   }
   
   public void testGetPanelXMLByName() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(ResourceRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/user/dan/panel/panel1");
      mockHttpRequest.accept(MediaType.APPLICATION_XML);
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      System.out.println(mockHttpResponse.getContentAsString());
   }
   

}
