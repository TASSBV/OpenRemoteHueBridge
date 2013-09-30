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

package org.openremote.modeler.selenium;

/**
 * Class for Selenium browser test of incompatibility.
 * Defines all the browser type string for Selenium.
 * 
 * @author Dan 2009-7-31
 */
public class Browser {
   
   /**
    * Not be instantiated.
    */
   private Browser() {
   }
   /** The FIREFOX. */
   public static final String FIREFOX = "*chrome";
   
   /** The OPERA. */
   public static final String OPERA = "*opera";
   
   /** The IE. */
   public static final String IE = "*iexplore";
   
   /** The SAFARI. */
   public static final String SAFARI = "*safari";

}
