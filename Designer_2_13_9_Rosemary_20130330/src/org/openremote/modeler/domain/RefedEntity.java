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
package org.openremote.modeler.domain;

/**
 * The Class RefedEntity defined refCount, which can be increase or decrease.
 * The sub classes include Group and ScreenPair.
 * 
 */
public class RefedEntity extends BusinessEntity {

   private static final long serialVersionUID = -7357154193810808172L;
   
   private int refCount = 0;
   
   public void ref() {
      refCount++;
   }
   
   public void releaseRef() {
      refCount--;
   }
   
   public int getRefCount() {
      return refCount;
   }
}
