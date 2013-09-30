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
package org.openremote.controller.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.openremote.controller.RoundRobinConfiguration;
import org.openremote.controller.exception.roundrobin.TCPServerStartFailException;
import org.openremote.controller.spring.SpringContext;

/**
 * This class provide discover groupMember function.<br /><br />
 * 
 * Firstly, startup a tcpserver for receive groupmembers' controller url.<br />
 * 
 * Secondly, send a udppackage to detect groupmembers.<br />
 * 
 * And then return the discovered groupmembers' controller url.<br />
 * 
 * @author Handy.Wang 2009-12-22
 */
public class RoundRobinClient {

   private Logger logger = Logger.getLogger(this.getClass().getName());

   private RoundRobinConfiguration roundRobinConfig = RoundRobinConfiguration.readXML();
   
   private String msgKey = UUID.randomUUID().toString();
   
   public static final String SEPARATOR_BETWEEN_MSG_KEY_AND_GROUP_NAME = "\u0001";

   /** Container of group members' url. */
   private List<String> groupMemberURLs;
   
   /** 
    * Group name of controller.<br /><br /> 
    * <b>NOTE:</b> This property can be initialized by <b>CONSTRUCTOR</b> or <b>CONFIGURATION</b>.<br />
    * If this property is null, it will be initialized by <b>CONFIGURATION</b>.
    */
   private String groupName;
   
   public RoundRobinClient(){
      super();
      this.groupMemberURLs = Collections.synchronizedList(new ArrayList<String>());
   }
   
   public RoundRobinClient(String groupName){
      super();
      this.groupName = groupName;
      this.groupMemberURLs = Collections.synchronizedList(new ArrayList<String>());
   }
   
   /**
    * Public interface for providing function of get all the group members which have same group name of current controller application.
    */
   public List<String> getGroupMemberURLsList() {
      discoverGroupMembers();
      return groupMemberURLs;
   }
   
   public Set<String> getGroupMemberURLsSet() {
      discoverGroupMembers();
      Set<String> urls = new HashSet<String>();
      urls.addAll(groupMemberURLs);
      return urls;
   }
   
   /**
    * Discover all the group members which have the same group name of current controller application. 
    */
   @SuppressWarnings("unchecked")
   private void discoverGroupMembers() throws TCPServerStartFailException {
      sendRoundRobinUDPMultiCastRequest();
      waitForGroupMemberURLS();
      
      ConcurrentHashMap<String, List> chm = (ConcurrentHashMap<String, List>) SpringContext.getInstance().getBean("servers");
      if (chm.containsKey(msgKey)) {
         groupMemberURLs.clear();
         Set<String> msgKeys = chm.keySet();
         for (String tempMsgKey : msgKeys) {
            if (tempMsgKey.equals(msgKey)) {
               groupMemberURLs = chm.get(tempMsgKey);
            }
         }
      }
   }

   /**
    * Send RoundRobin UDP Multicast request for detecing whether there is any group member existed. 
    */
   private void sendRoundRobinUDPMultiCastRequest() throws TCPServerStartFailException {
      MulticastSocket socket = null;
      try {
         socket = new MulticastSocket();
         InetAddress groupMulticastAdressForRoundRobin = InetAddress.getByName(roundRobinConfig.getRoundRobinMulticastAddress());
         socket.joinGroup(groupMulticastAdressForRoundRobin);
         String transferData = msgKey + SEPARATOR_BETWEEN_MSG_KEY_AND_GROUP_NAME + roundRobinConfig.getRoundRobinTCPServerSocketPort() + SEPARATOR_BETWEEN_MSG_KEY_AND_GROUP_NAME;
         if (groupName == null || "".equals(groupName)) {
            transferData += roundRobinConfig.getControllerGroupName();
         } else {
            transferData += groupName;
         }
         byte[] data = transferData.getBytes();
         DatagramPacket packet = new DatagramPacket(data, data.length, groupMulticastAdressForRoundRobin, roundRobinConfig.getRoundRobinMulticastPort());
         socket.send(packet);
      } catch (IOException e) {
         logger.error("Created UDP request socket fail.", e);
      }
   }
   
   /**
    * Wait for a moment for giving RoundRobin TCP clients some time to response.
    */
   private void waitForGroupMemberURLS() {
      synchronized(groupMemberURLs) {
         try {
            groupMemberURLs.wait(500);
         } catch (InterruptedException e) {
            logger.error(e.getStackTrace(), e);
         }
         
      }
   }

   public void setGroupName(String groupName) {
      this.groupName = groupName;
   }

}
