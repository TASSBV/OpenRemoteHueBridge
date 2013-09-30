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
package org.openremote.controller.protocol.virtual;

import java.net.URL;
import java.net.HttpURLConnection;

import org.junit.Test;
import org.junit.Assert;
import org.openremote.controller.suite.RESTTests;
import org.openremote.controller.suite.AllTests;
import org.openremote.controller.rest.FindPanelByIDTest;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class TempEventTest
{


  @Test public void testTemperatureEvents() throws Exception
  {
    AllTests.replaceTestContainerControllerXML("VirtualTempEventListenerAndRangeSensor-controller.xml");

    URL statusRequest = new URL(RESTTests.containerURL + FindPanelByIDTest.RESTAPI_STATUS_URI + "8880");


    Thread.sleep(7000);

    HttpURLConnection connection = (HttpURLConnection)statusRequest.openConnection();


    Document doc = RESTTests.getDOMDocument(connection.getInputStream());


    NodeList list = doc.getElementsByTagName("status");

    Assert.assertTrue(list.getLength() == 1);

  }
}

