
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
*/package org.openremote.controller.config;

import org.openremote.controller.exception.ControllerException;

/**
 * 
 * @author handy.wang 2010-03-20
 *
 */
@SuppressWarnings("serial")
public class ControllerXMLChangedException extends ControllerException {
   
   private static final int CONTROLLER_XML_CHANGED = 506;
   
   public ControllerXMLChangedException() {
      super();
      setErrorCode(CONTROLLER_XML_CHANGED);
   }

   public ControllerXMLChangedException(String message, Throwable cause) {
      super(message, cause);
      setErrorCode(CONTROLLER_XML_CHANGED);
   }

   public ControllerXMLChangedException(String message) {
      super(message);
      setErrorCode(CONTROLLER_XML_CHANGED);
   }

   public ControllerXMLChangedException(Throwable cause) {
      super(cause);
      setErrorCode(CONTROLLER_XML_CHANGED);
   }
}
