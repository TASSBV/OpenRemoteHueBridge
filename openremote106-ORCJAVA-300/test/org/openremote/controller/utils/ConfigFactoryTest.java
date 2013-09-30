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
package org.openremote.controller.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;
import java.net.URI;

import org.junit.Test;
import org.junit.Assert;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.RoundRobinConfiguration;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.service.DeployerTest;
import org.openremote.controller.suite.AllTests;

/**
 * TODO :
 *   - ORCJAVA-170 (http://jira.openremote.org/browse/ORCJAVA-170)
 *   - ORCJAVA-169 (http://jira.openremote.org/browse/ORCJAVA-169)
 * 
 *  These tests need to be restructured, leaving them in their current state for now until
 *  have more time for proper fixes.
 *
 * 
 * @author Dan Cong
 * @author <a href="mailto:juha@openremote.org>Juha Lindfors</a>
 *
 */
public class ConfigFactoryTest
{

  // TODO : test URI/abs file/relative file support in getResource()


  // Tests ----------------------------------------------------------------------------------------


  @Test public void getBasicConfig() throws Exception
  {
    ControllerConfiguration cc1 = new ControllerConfiguration();

    URI deploymentURI = AllTests.getAbsoluteFixturePath().resolve("deployment/configuration");
    cc1.setResourcePath(deploymentURI.getPath());

    Deployer deployer = DeployerTest.createDeployer(cc1);

    deployer.startController();

    //overrideConfigFromControllerXML(deployer, cc1);

    Assert.assertTrue(
        "Expected 'controller1', got '" + cc1.getWebappName() + "'",
        "controller1".equals(cc1.getWebappName())
    );
    
    assertEquals(false, cc1.isCopyLircdconf());

    // TODO : resource path cannot be overridden from online editor, not really sure why it was excluded [JPL]
    // assertEquals("/home/openremote/controller", cc1.getResourcePath());

    assertEquals("/etc/lircd.conf", cc1.getLircdconfPath());
    assertEquals("http://openremote.org/beehvie/rest/", cc1.getBeehiveRESTRootUrl());
    assertEquals("192.168.4.63", cc1.getWebappIp());
    assertEquals(8888, cc1.getWebappPort());
    assertEquals("/usr/local/bin/irsend", cc1.getIrsendPath());
    assertEquals(500, cc1.getMacroIRExecutionDelay());
    assertEquals(3333, cc1.getMulticastPort());
    assertEquals("224.0.1.100", cc1.getMulticastAddress());
  }


  @Test public void getBasicConfig2() throws Exception
  {
    ControllerConfiguration cc2 = new ControllerConfiguration();

    URI deploymentURI = AllTests.getAbsoluteFixturePath().resolve("deployment/configuration2");
    cc2.setResourcePath(deploymentURI.getPath());

    Deployer deployer = DeployerTest.createDeployer(cc2);

    deployer.startController();

    //overrideConfigFromControllerXML(deployer, cc2);

    assertEquals("controller2", cc2.getWebappName());
    assertEquals(true, cc2.isCopyLircdconf());

    // TODO : resource path cannot be overridden from online editor, not really sure why it was excluded [JPL]
    // assertEquals("/home/openremote/controller", cc2.getResourcePath());

    assertEquals("/etc/lircd.conf", cc2.getLircdconfPath());
    assertEquals("http://openremote.org/beehive/rest/", cc2.getBeehiveRESTRootUrl());
    assertEquals("192.168.4.63", cc2.getWebappIp());
    assertEquals(8888, cc2.getWebappPort());
    assertEquals("/usr/local/bin/irsend", cc2.getIrsendPath());
    assertEquals(500, cc2.getMacroIRExecutionDelay());
    assertEquals(3333, cc2.getMulticastPort());
    assertEquals("224.0.1.100", cc2.getMulticastAddress());
  }



  @Test public void getRoundRobinConfig() throws Exception
  {
//    StatusCache sc = new StatusCache();
    ControllerConfiguration cc = new ControllerConfiguration();

    URI deploymentURI = AllTests.getAbsoluteFixturePath().resolve("deployment/roundrobin/configuration");
    cc.setResourcePath(deploymentURI.getPath());

//    Deployer deployer = new Deployer("Deployer for " + deploymentURI, sc, cc);
    Deployer deployer = DeployerTest.createDeployer(cc);

    deployer.startController();

    RoundRobinConfiguration rrc = new RoundRobinConfiguration();

    //overrideConfigFromControllerXML(deployer, rrc);

    assertEquals("controller1", rrc.getControllerApplicationName());
    assertEquals(true, rrc.getIsGroupMemberAutoDetectOn());
    assertEquals("openremote-office", rrc.getControllerGroupName());
    assertEquals("224.0.1.200", rrc.getRoundRobinMulticastAddress());
    assertEquals(20000, rrc.getRoundRobinMulticastPort());
    assertEquals(10000, rrc.getRoundRobinTCPServerSocketPort());

    String[] urls = "http://192.168.1.5:8080/controller/,http://192.168.1.100:8080/controller/,http://192.168.1.105:8080/controller/".split(",");
    assertTrue(Arrays.equals(urls, rrc.getGroupMemberCandidateURLs()));
  }

  @Test public void getRoundRobinConfig2() throws Exception
  {
    ControllerConfiguration cc = new ControllerConfiguration();

    URI deploymentURI = AllTests.getAbsoluteFixturePath().resolve("deployment/roundrobin/configuration2");
    cc.setResourcePath(deploymentURI.getPath());

    Deployer deployer = DeployerTest.createDeployer(cc);

    deployer.startController();

    RoundRobinConfiguration rrc = new RoundRobinConfiguration();

    //overrideConfigFromControllerXML(deployer, rrc);

    assertEquals("controller2", rrc.getControllerApplicationName());
    assertEquals(false, rrc.getIsGroupMemberAutoDetectOn());
    assertEquals("openremote-home", rrc.getControllerGroupName());
    assertEquals("224.0.1.200", rrc.getRoundRobinMulticastAddress());
    assertEquals(20000, rrc.getRoundRobinMulticastPort());
    assertEquals(10000, rrc.getRoundRobinTCPServerSocketPort());

    String[] urls = "http://192.168.1.5:8080/controller/,http://192.168.1.100:8080/controller/,http://192.168.1.105:8080/controller/".split(",");
    assertTrue(Arrays.equals(urls, rrc.getGroupMemberCandidateURLs()));
  }



  // Helpers --------------------------------------------------------------------------------------

  private void overrideConfigFromControllerXML(Deployer d, ControllerConfiguration cc) throws Exception
  {
    Map<String, String> attrMap = d.getConfigurationProperties();
    cc.setConfigurationProperties(attrMap);

  }
  
  private void overrideConfigFromControllerXML(Deployer d, RoundRobinConfiguration rrc) throws Exception
  {
    Map<String, String> attrMap = d.getConfigurationProperties();
    rrc.setConfigurationProperties(attrMap);
  }


}
