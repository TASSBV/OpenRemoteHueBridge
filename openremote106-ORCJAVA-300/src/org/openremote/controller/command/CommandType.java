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
package org.openremote.controller.command;

/**
 * The Enum CommandType.
 * 
 * @author Dan 2009-5-21
 */
public enum CommandType {
   
   /** send command once. */
   SEND_ONCE("SEND_ONCE"), 
   
   /** send command start. */
   SEND_START("SEND_START"), 
   
   /** send command stop. */
   SEND_STOP("SEND_STOP"); 

   /** The type. */
   private String type;

   /**
    * Instantiates a new command type.
    * 
    * @param type the type
    */
   private CommandType(String type) {
      this.type = type;
   }

   /* (non-Javadoc)
    * @see java.lang.Enum#toString()
    */
   @Override
   public String toString() {
      return type;
   }

}
