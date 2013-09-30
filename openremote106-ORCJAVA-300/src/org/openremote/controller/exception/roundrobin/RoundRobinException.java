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
 * Exception root of <b>RoundRobin</b>
 * 
 * @author Handy.Wang 2009-12-22
 */
@SuppressWarnings("serial")
public class RoundRobinException extends RuntimeException {
   
   /** Error code of TCP Server start fail. */
   public static final int TCP_SERVER_START_FAIL = 450;
   
   /** Error code of UDP Server start fail. */
   public static final int UDP_SERVER_START_FAIL = 451;
   
   /** Error code of UDP Client establish fail.  */
   public static final int UDP_CLIENT_ESTABLISH_FAIL = 452;
   
   /** Error code of wrong roundrobin url */
   public static final int INVALID_ROUND_ROBIN_URL = 453;
   
   /** Error code stand for different RoundRobin exception type. */
   private int errorCode; 

   public RoundRobinException() {
      super();
   }
   
   public RoundRobinException(String exceptionMsg) {
      super(exceptionMsg);
   }
   
   public RoundRobinException(Throwable cause) {
      super(cause);
   }
   
   public RoundRobinException(String exceptionMsg, Throwable cause) {
      super(exceptionMsg, cause);
   }

   public int getErrorCode() {
      return errorCode;
   }

   public void setErrorCode(int errorCode) {
      this.errorCode = errorCode;
   }
   
}
