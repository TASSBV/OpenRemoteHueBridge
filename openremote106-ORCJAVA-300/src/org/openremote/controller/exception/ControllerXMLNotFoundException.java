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
package org.openremote.controller.exception;

/**
 * The exception class when controller.xml Not Found.
 * 
 * @author Dan 2009-5-22
 */
@SuppressWarnings("serial")
public class ControllerXMLNotFoundException extends ControlCommandException {

   /**
    * Instantiates a new controller xml not found exception.
    */
   public ControllerXMLNotFoundException() {
      super("*controller.xml* not found.");
      setErrorCode(ControlCommandException.CONTROLLER_XML_NOT_FOUND);
   }

   /**
    * Instantiates a new controller xml not found exception.
    * 
    * @param message the message
    * @param cause the cause
    */
   public ControllerXMLNotFoundException(String message, Throwable cause) {
      super("*controller.xml* not found." + message, cause);
      setErrorCode(ControlCommandException.CONTROLLER_XML_NOT_FOUND);
   }

   /**
    * Instantiates a new controller xml not found exception.
    * 
    * @param message the message
    */
   public ControllerXMLNotFoundException(String message) {
      super("*controller.xml* not found." + message);
      setErrorCode(ControlCommandException.CONTROLLER_XML_NOT_FOUND);
   }
   

}
