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
package org.openremote.controller.component.control;

import java.net.URI;

import junit.framework.Assert;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.command.CommandFactory;
import org.openremote.controller.component.Sensory;
import org.openremote.controller.component.control.switchtoggle.Switch;
import org.openremote.controller.component.control.switchtoggle.SwitchBuilder;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.service.DeployerTest;
import org.openremote.controller.suite.AllTests;

/**
 * TODO
 *
 *   - see related tasks
 *     ORCJAVA-145  (http://jira.openremote.org/browse/ORCJAVA-145)
 *     ORCJAVA-146  (http://jira.openremote.org/browse/ORCJAVA-146)
 *     ORCJAVA-147  (http://jira.openremote.org/browse/ORCJAVA-147)
 *
 */
public class SwitchBuilderTest
{

  // Instance Fields ------------------------------------------------------------------------------

  private SwitchBuilder builder;
  private Deployer deployer;


  // Test Lifecycle -------------------------------------------------------------------------------

  /**
   * Setup the service dependencies of deployer and other required services.
   *
   * @throws Exception    if setup fails
   */
  @Before public void setUp() throws Exception
  {
    URI uri = AllTests.getAbsoluteFixturePath().resolve("builder/switch");

    CommandFactory cf = DeployerTest.createCommandFactory();

    deployer = DeployerTest.createDeployer(uri, cf);

    builder = new SwitchBuilder();
    builder.setDeployer(deployer);
    builder.setCommandFactory(cf);
    
    deployer.softRestart();
  }



  // Tests ----------------------------------------------------------------------------------------

  // TODO : add some broken definitions to test error handling


  
  @Test public void testNoNull() throws Exception
  {
    Assert.assertNotNull(getSwitchByID(3, "on"));
  }

  @Test public void testGetCommand() throws Exception
  {
    Switch sw = getSwitchByID(4, "on");

    Assert.assertEquals(sw.getExecutableCommands().size(), 1);

    sw = getSwitchByID(4, "off");

    Assert.assertEquals(sw.getExecutableCommands().size(), 1);

    sw = getSwitchByID(4, "status");

    Assert.assertTrue(sw instanceof Sensory);
    Assert.assertTrue(sw.fetchSensorID() == 1004);
  }


  // Helpers --------------------------------------------------------------------------------------

  private Switch getSwitchByID(int switchID, String cmdParam)
      throws InitializationException
  {
    Element controlElement = deployer.queryElementById(switchID);

    Assert.assertTrue(
        "Was expecting 'switch', got '" + controlElement.getName() + "'.",
        controlElement.getName().equals("switch")
    );
    
    return (Switch) builder.build(controlElement, cmdParam);
  }

}
