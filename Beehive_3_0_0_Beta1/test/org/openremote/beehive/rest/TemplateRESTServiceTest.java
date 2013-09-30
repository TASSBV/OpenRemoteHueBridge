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

import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.openremote.beehive.TemplateTestBase;
import org.openremote.beehive.rest.service.TemplateRESTTestService;
import org.openremote.beehive.utils.FixtureUtil;
import org.openremote.beehive.utils.RESTTestUtils;


public class TemplateRESTServiceTest  extends TemplateTestBase {
   

   public void testGetTemplatesByAccountInXML() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/account/1/templates/private");
      mockHttpRequest.accept(MediaType.APPLICATION_XML);
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      System.out.println(mockHttpResponse.getContentAsString());
   }
   public void testGetAllPublicTemplateInXML() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/account/1/templates/public");
      mockHttpRequest.accept(MediaType.APPLICATION_XML);
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      System.out.println(mockHttpResponse.getContentAsString());
   }
   public void testGetTemplatesByAccountInXMLWithInvalidAccountId() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/account/1a/templates/public");
      mockHttpRequest.accept(MediaType.APPLICATION_XML);
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      assertEquals(400, mockHttpResponse.getStatus());
   }
   
   public void testGetTemplatesByAccountInXMLWithOtherAccountId() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/account/2/templates/public");
      mockHttpRequest.accept(MediaType.APPLICATION_XML);
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      assertEquals(401, mockHttpResponse.getStatus());
   }

   public void testGetTemplatesByAccountInJSON() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/account/1/templates/private");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      System.out.println(mockHttpResponse.getContentAsString());
   }
   public void testGetTemplatesByAccountInJSONWithInvalidAccountId() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/account/1a/templates/public");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      
      assertEquals(400, mockHttpResponse.getStatus());
   }

   public void testGetTemplateByIdInXML() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/account/1/template/2");
      mockHttpRequest.accept(MediaType.APPLICATION_XML);
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      System.out.println(mockHttpResponse.getContentAsString());
   }

   public void testGetTemplateByIdInJSON() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/account/1/template/2");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      System.out.println(mockHttpResponse.getContentAsString());
   }

   public void testSaveTemplateIntoAccountWithoutFormData() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/account/1/template");
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      assertEquals(400, mockHttpResponse.getStatus());
   }
   
   public void testSaveTemplateIntoAccountInXML() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/account/1/template");
      mockHttpRequest.accept(MediaType.APPLICATION_XML);
      mockHttpRequest.contentType(MediaType.APPLICATION_FORM_URLENCODED);
      addCredential(mockHttpRequest);
      String postData = "name=dan&content=" + FixtureUtil.getFileContent("template.json");
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      System.out.println(mockHttpResponse.getContentAsString());
   }

   public void testSaveTemplateIntoAccountInJSON() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/account/1/template");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_FORM_URLENCODED);
      addCredential(mockHttpRequest);
      String postData = "name=dan&content=" + FixtureUtil.getFileContent("template.json");
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      System.out.println(mockHttpResponse.getContentAsString());
   }
   
   public void testDeleteTemplateInAccount() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.delete("/account/1/template/1");
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      System.out.println(mockHttpResponse.getContentAsString());
   }

   
   public void testDeleteTemplateInAccountWithoutAuth() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.delete("/account/1/template/1");
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      assertEquals(401, mockHttpResponse.getStatus());
   }
   
//   public void testSaveTemplateResource() throws URISyntaxException {
//      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
//      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/account/1/template/1/resource");
//      addCredential(mockHttpRequest);
//      mockHttpRequest.contentType(MediaType.MULTIPART_FORM_DATA);
//      mockHttpRequest.content(FixtureUtil.getFileInputstream("template.json"));
//      MockHttpResponse mockHttpResponse = new MockHttpResponse();
//      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
//      System.out.println(mockHttpResponse.getContentAsString());
//   }


}
