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
package org.openremote.modeler.client.utils;

import java.util.Collection;

import org.openremote.modeler.domain.BusinessEntity;
import org.openremote.modeler.domain.Panel;

/**
 * The Class store a panel collection and  a max id when restore the uiDesigner.
 */
public class PanelsAndMaxOid extends BusinessEntity{
 
   private static final long serialVersionUID = 2451009088912750552L;
   
   private Collection<Panel> panels ;
   private long maxOid;
   
   public PanelsAndMaxOid(Collection<Panel> panels,long maxOid ){
      this.panels = panels;
      this.maxOid = maxOid;
   }
   public PanelsAndMaxOid(){}
   public Collection<Panel> getPanels() {
      return panels;
   }

   public long getMaxOid() {
      return maxOid;
   }
}
