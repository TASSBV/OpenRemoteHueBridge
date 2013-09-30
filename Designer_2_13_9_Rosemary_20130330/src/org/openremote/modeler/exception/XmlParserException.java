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
 * Custom Exception when parser xml faild.
 * 
 * @author Tomsky
 */
@SuppressWarnings("serial")
public class XmlParserException extends RuntimeException implements IsSerializable{
   
   /**
    * Instantiates a new xml parser exception.
    * 
    * @param s the s
    */
   public XmlParserException(String s) {
      super(s);
   }

   /**
    * Instantiates a new xml parser exception.
    * 
    * @param s the s
    * @param throwable the throwable
    */
   public XmlParserException(String s, Throwable throwable) {
      super(s, throwable);
   }
   
   public XmlParserException(Throwable throwable) {
      super(throwable);
   }
   
   /**
    * This constructor is necessary for GWT to serialize to client. 
    */
   public XmlParserException() {
      
   }
}
