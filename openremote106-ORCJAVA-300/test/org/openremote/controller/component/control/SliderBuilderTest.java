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

import static org.junit.Assert.fail;

import java.net.URI;

import junit.framework.Assert;

import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.service.DeployerTest;
import org.openremote.controller.suite.AllTests;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.CommandFactory;
import org.openremote.controller.component.control.slider.Slider;
import org.openremote.controller.component.control.slider.SliderBuilder;
import org.openremote.controller.exception.XMLParsingException;

/**
 * TODO:
 *    - see ORCJAVA-166 (http://jira.openremote.org/browse/ORCJAVA-166)
 * 
 */
public class SliderBuilderTest {

  private Deployer deployer;
  private SliderBuilder sliderBuilder;


  // Test Lifecycle -------------------------------------------------------------------------------

  @Before public void setUp() throws Exception
  {
    URI deploymentURI = AllTests.getAbsoluteFixturePath().resolve("builder/slider");

    CommandFactory cf = DeployerTest.createCommandFactory();

    deployer = DeployerTest.createDeployer(deploymentURI, cf);
    deployer.startController();

    sliderBuilder = new SliderBuilder();
    sliderBuilder.setDeployer(deployer);
    sliderBuilder.setCommandFactory(cf);
  }


  // Tests ----------------------------------------------------------------------------------------

  
  /** Get invalid slider with control id from controller.xml. */
  @Test public void testGetInvalidSlider() throws Exception
  {
    try
    {
      getSliderByID("555");

      fail("should not get here...");
    }

    catch (XMLParsingException e)
    {
      // expected
    }
  }
   
  /** Get a non-null slider and it's valid. */
  @Test public void testGetSliderNotNull() throws Exception
  {
    Slider slider = getSliderByID("1");

    Assert.assertNotNull(slider);
    Assert.assertTrue(slider.fetchSensorID() == 101);

    slider = getSliderByID("2");

    Assert.assertNotNull(slider);
    Assert.assertTrue(slider.fetchSensorID() == 101);

    slider = getSliderByID("3");

    Assert.assertNotNull(slider);
    Assert.assertTrue(slider.fetchSensorID() == 103);

    slider = getSliderByID("4");

    Assert.assertNotNull(slider);
    Assert.assertTrue(slider.fetchSensorID() == 103);

  }



  /** Get the slider and check whether the executable commands are null. */
  @Test public void testGetExecutableCommandsOfSlider() throws Exception
  {
    Slider slider = getSliderByID("1");
    Assert.assertNotNull(slider.getExecutableCommands());
    Assert.assertTrue(slider.getExecutableCommands().size() > 0);

    for (ExecutableCommand executableCommand : slider.getExecutableCommands())
    {
       executableCommand.send();
    }
  }



  // Helpers --------------------------------------------------------------------------------------


  private Slider getSliderByID(String sliderID) throws Exception
  {
    Element controlElement = deployer.queryElementById(Integer.parseInt(sliderID));

    return (Slider) sliderBuilder.build(controlElement, "20");
  }
}
