/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.modeler.exception;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * If the parse touch panel XMLs procedure occurr error, application will throw this exception.
 */
public class ParseTouchPanelException  extends RuntimeException implements IsSerializable {

   private static final long serialVersionUID = -510888683143871120L;
   
   /**
    * This constructor is necessary for GWT to serialize to client. 
    */
   public ParseTouchPanelException() {
      super();
   }

   /**
    * Instantiates a new parses the protocol exception.
    * 
    * @param message the message
    * @param cause the cause
    */
   public ParseTouchPanelException(String message, Throwable cause) {
      super(message, cause);
   }

   /**
    * Instantiates a new parses the protocol exception.
    * 
    * @param message the message
    */
   public ParseTouchPanelException(String message) {
      super(message);
   }

   /**
    * Instantiates a new parses the protocol exception.
    * 
    * @param cause the cause
    */
   public ParseTouchPanelException(Throwable cause) {
      super(cause);
   }

}
