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
package org.openremote.controller.utils;

import org.jdom.Element;
import org.openremote.controller.command.Command;

/**
 * Command Utility.
 * 
 * @author Dan Cong
 */
public class CommandUtil {
   
   private CommandUtil() {
   }
   
   /**
    * Parse a string with dynamic param taken from slider or color picker etc.<br />
    * (1) /rest/control/27(slider)/128 <br />
    * 
    * with TCP/IP command mapping: <br />
    *
    * (2) 'SEND ${param}'<br /> 
    * 
    * Will be parsed to" <br />
    *
    * (3) 'SEND 128'
    *
    * @param element
    *           DOM element with dynamic value.
    * @param str
    *           string to be parsed
    * @return a string with dynamic value
    */
   public static String parseStringWithParam(Element elementWithDynamicValue, String str) {
      String commandParam = elementWithDynamicValue.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);
      if (str != null && commandParam != null) {
         return str.replaceAll(Command.DYNAMIC_PARAM_PLACEHOLDER_REGEXP, commandParam);
      }
      return str;
   }

}
