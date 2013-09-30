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
package org.openremote.controller;

import java.util.HashSet;
import java.util.Set;

import org.openremote.controller.service.ServiceContext;


/**
 * TODO : Configuration of RoundRobin.
 * 
 * @author Handy.Wang, Dan Cong
 */
public class RoundRobinConfiguration extends Configuration
{

  // Constants ------------------------------------------------------------------------------------

  public static final String CONTROLLER_GROUPMEMBER_AUTODETECT_ON =
      "controller.groupmember.autodetect.on";

  public static final String CONTROLLER_ROUNDROBIN_MULTICAST_ADDRESS =
      "controller.roundrobin.multicast.address";

  public static final String CONTROLLER_ROUNDROBIN_MULTICAST_PORT =
      "controller.roundrobin.multicast.port";

  public static final String CONTROLLER_GROUPNAME =
      "controller.groupname";

  public static final String CONTROLLER_APPLICATIONNAME =
      "controller.applicationname";

  public static final String CONTROLLER_ROUNDROBIN_TCPSERVER_PORT =
      "controller.roundrobin.tcpserver.port";

  public static final String CONTROLLER_GROUPMEMBER_CANDIDATE_URLS =
      "controller.groupmember.candidate.urls";



  // Class Members --------------------------------------------------------------------------------

  public static RoundRobinConfiguration readXML()
  {
    RoundRobinConfiguration config = ServiceContext.getRoundRobinConfiguration();

    return (RoundRobinConfiguration)Configuration.updateWithControllerXMLConfiguration(config);
  }



  // Instance Fields ------------------------------------------------------------------------------

  
  /** whether groupmember auto-dectect is on */
  private boolean isGroupMemberAutoDetectOn;

  /**  Discovery multicast address of round robin. */
  private String roundRobinMulticastAddress;

  /** Discovery multicast port of round robin. */
  private int roundRobinMulticastPort;

  /** Group name of Controller app. */
  private String controllerGroupName;

  /** ApplicationName of Controller. */
  private String controllerApplicationName;

  /** TCP server socket port of Round Robin */
  public int roundRobinTCPServerSocketPort;

  /** Group members candidate urls */
  public String[] groupMemberCandidateURLs;



  // Public Methods -------------------------------------------------------------------------------

  public boolean getIsGroupMemberAutoDetectOn()
  {
    return preferAttrCustomValue(CONTROLLER_GROUPMEMBER_AUTODETECT_ON, isGroupMemberAutoDetectOn);
  }

  public void setIsGroupMemberAutoDetectOn(boolean isGroupMemberAutoDetectOn)
  {
    this.isGroupMemberAutoDetectOn = isGroupMemberAutoDetectOn;
  }

  public String getRoundRobinMulticastAddress()
  {
    return preferAttrCustomValue(CONTROLLER_ROUNDROBIN_MULTICAST_ADDRESS, roundRobinMulticastAddress);
  }

  public void setRoundRobinMulticastAddress(String roundRobinMulticastAddress)
  {
    this.roundRobinMulticastAddress = roundRobinMulticastAddress;
  }

  public int getRoundRobinMulticastPort()
  {
    return preferAttrCustomValue(CONTROLLER_ROUNDROBIN_MULTICAST_PORT, roundRobinMulticastPort);
  }

  public void setRoundRobinMulticastPort(int roundRobinMulticastPort)
  {
    this.roundRobinMulticastPort = roundRobinMulticastPort;
  }

  public String getControllerGroupName()
  {
    return preferAttrCustomValue(CONTROLLER_GROUPNAME, controllerGroupName);
  }

  public void setControllerGroupName(String controllerGroupName)
  {
    this.controllerGroupName = controllerGroupName;
  }

  public String getControllerApplicationName()
  {
    return preferAttrCustomValue(CONTROLLER_APPLICATIONNAME, controllerApplicationName);
  }

  public void setControllerApplicationName(String controllerApplicationName)
  {
    this.controllerApplicationName = controllerApplicationName;
  }

  public int getRoundRobinTCPServerSocketPort()
  {
    return preferAttrCustomValue(CONTROLLER_ROUNDROBIN_TCPSERVER_PORT, roundRobinTCPServerSocketPort);
  }

  public void setRoundRobinTCPServerSocketPort(int roundRobinTCPServerSocketPort)
  {
    this.roundRobinTCPServerSocketPort = roundRobinTCPServerSocketPort;
  }

  public String[] getGroupMemberCandidateURLs()
  {
    return preferAttrCustomValue(CONTROLLER_GROUPMEMBER_CANDIDATE_URLS, groupMemberCandidateURLs);
  }

  public Set<String> getGroupMemberCandidateURLsSet()
  {
    Set<String> urlsSet = new HashSet<String>();

    for (String groupMembersURL : getGroupMemberCandidateURLs())
    {
       urlsSet.add(groupMembersURL);
    }

    return urlsSet;
  }

  public void setGroupMemberCandidateURLs(String[] groupMemberCandidateURLs)
  {
    this.groupMemberCandidateURLs = groupMemberCandidateURLs;
  }

}

