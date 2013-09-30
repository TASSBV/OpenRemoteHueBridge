/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.service.impl;

import java.util.Set;

import org.openremote.controller.RoundRobinConfiguration;
import org.openremote.controller.net.RoundRobinClient;
import org.openremote.controller.service.RoundRobinService;

/**
 * RoundRobin service for proving: group members apps' url finding, etc.
 * 
 * @author Handy.Wang 2009-12-23
 */
public class RoundRobinServiceImpl implements RoundRobinService {
   
  private RoundRobinConfiguration roundRobinConfig;
   
   private static final String XML_HEADER_ELEMENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
   
   private static final String OPENREMOTE_START_ELEMENT = "<openremote xmlns=\"http://www.openremote.org\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n";
   
   private static final String OPENREMOTE_END_ELEMENT = "</openremote>";
   
   private static final String SERVERS_START_ELEMENT = "<servers>\n";
   
   private static final String SERVERS_END_ELEMENT = "</servers>\n";
   
   private static String SERVER_ELEMENT = "<server url=\"${server_url}\" />\n";

   /**
    * Discover group members' app url.
    * 
    * @return group members' controller application url
    */
   @Override
   public Set<String> discoverGroupMembersAppURL() {
      if (roundRobinConfig.getIsGroupMemberAutoDetectOn()) {
         RoundRobinClient roundRobinClient = new RoundRobinClient();
         return roundRobinClient.getGroupMemberURLsSet();
      } else {
         return roundRobinConfig.getGroupMemberCandidateURLsSet();
      }
   }

   /**
    * Construct XML format data with Servers' URL Set
    * 
    * @param groupMemberControllerAppURLSet
    * @return XML format of servers' URL
    */
   @Override
   public String constructServersXML(Set<String> groupMemberControllerAppURLSet) {
      StringBuffer xml = new StringBuffer();
      xml.append(XML_HEADER_ELEMENT);
      xml.append(OPENREMOTE_START_ELEMENT);
      xml.append(SERVERS_START_ELEMENT);
      for (String serverURL : groupMemberControllerAppURLSet) {
         xml.append(SERVER_ELEMENT.replace("${server_url}", serverURL));
      }
      xml.append(SERVERS_END_ELEMENT);
      xml.append(OPENREMOTE_END_ELEMENT);
      return xml.toString();
   }

   public void setRoundRobinConfig(RoundRobinConfiguration roundRobinConfig) {
      this.roundRobinConfig = roundRobinConfig;
   }

}
