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
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.command.CommandFactory;
import org.openremote.controller.component.control.button.Button;
import org.openremote.controller.component.control.button.ButtonBuilder;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.service.DeployerTest;
import org.openremote.controller.suite.AllTests;

/**
 * TODO
 *
 *  - see related tasks
 *     ORCJAVA-155  (http://jira.openremote.org/browse/ORCJAVA-155)
 *     ORCJAVA-156  (http://jira.openremote.org/browse/ORCJAVA-156)
 *     ORCJAVA-157  (http://jira.openremote.org/browse/ORCJAVA-157)
 *
 */
public class ButtonBuilderTest
{

  // Instance Fields ------------------------------------------------------------------------------


  private ButtonBuilder builder;
  private Deployer deployer;


  
  // Test Lifecycle -------------------------------------------------------------------------------

  @Before public void setUp() throws Exception
  {
    URI deploymentURI = AllTests.getAbsoluteFixturePath().resolve("builder/button");

    CommandFactory cf = DeployerTest.createCommandFactory();
    deployer = DeployerTest.createDeployer(deploymentURI, cf);

    builder = new ButtonBuilder();
    builder.setCommandFactory(cf);
    builder.setDeployer(deployer);


    deployer.softRestart();
  }


  // Tests ----------------------------------------------------------------------------------------

  @Test public void testNoSuchButton() throws Exception
  {
    getButtonByID(10, "click");

    fail("should not get here..");
  }

  @Test public void testBasicBuild() throws Exception
  {
     Button btn = getButtonByID(9, "click");
     Assert.assertNotNull(btn);
  }

  @Test public void testGetCommand() throws Exception
  {
     Button btn = getButtonByID(9, "click");
     Assert.assertEquals(btn.getExecutableCommands().size(), 2);

     btn = getButtonByID(9, "click");
     Assert.assertEquals(btn.getExecutableCommands().size(), 2);

     btn = getButtonByID(9, "status");
     Assert.assertEquals(btn.getExecutableCommands().size(), 0);
  }


  // Helper ---------------------------------------------------------------------------------------


  private Button getButtonByID(int buttonID, String cmdParam) throws InitializationException
  {
    Element controlElement = deployer.queryElementById(buttonID);

    return (Button) builder.build(controlElement, cmdParam);
  }


}
