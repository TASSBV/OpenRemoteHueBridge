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
package org.openremote.controller.exception.roundrobin;

/**
 * This exception class is used to describe udp server start fail in roundrobin.
 * 
 * @author Handy.Wang 2009-12-22
 */
@SuppressWarnings("serial")
public class UDPServerStartFailException extends RoundRobinException {

   public UDPServerStartFailException() {
      super();
      setErrorCode(RoundRobinException.UDP_SERVER_START_FAIL);
   }
   
   public UDPServerStartFailException(String exceptionMsg) {
      super();
      setErrorCode(RoundRobinException.UDP_SERVER_START_FAIL);
   }
   
   public UDPServerStartFailException(Throwable cause) {
      super();
      setErrorCode(RoundRobinException.UDP_SERVER_START_FAIL);
   }
   
   public UDPServerStartFailException(String exceptionMsg, Throwable cause) {
      super();
      setErrorCode(RoundRobinException.UDP_SERVER_START_FAIL);
   } 
}
