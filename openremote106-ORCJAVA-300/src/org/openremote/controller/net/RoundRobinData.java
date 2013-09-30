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

/**
 * Store data which transfer between RoundRobinServer and RoundRobinClient.
 * 
 * @author Handy.Wang 2009-12-24
 */
public class RoundRobinData {
   
   private String msgKey;
   
   private int tcpServerPort;
   
   private String content;

   public String getMsgKey() {
      return msgKey;
   }

   public void setMsgKey(String msgKey) {
      this.msgKey = msgKey;
   }

   public int getTcpServerPort() {
      return tcpServerPort;
   }

   public void setTcpServerPort(int tcpServerPort) {
      this.tcpServerPort = tcpServerPort;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

}
