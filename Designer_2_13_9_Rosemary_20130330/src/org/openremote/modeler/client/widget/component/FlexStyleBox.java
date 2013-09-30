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
package org.openremote.modeler.client.widget.component;

import com.google.gwt.user.client.ui.FlexTable;

/**
 * FlexTableBox is a style box which can be change size.
 */
public class FlexStyleBox extends FlexTable {
   
   public FlexStyleBox() {
      addStyleName("screen-btn-cont");
      setCellPadding(0);
      setCellSpacing(0);
      
      setWidget(0, 0, null);
      setWidget(0, 1, null);
      setWidget(0, 2, null);

      setWidget(1, 0, null);
      setWidget(1, 1, null);
      setWidget(1, 2, null);

      setWidget(2, 0, null);
      setWidget(2, 1, null);
      setWidget(2, 2, null);

      getCellFormatter().addStyleName(0, 0, "tl-c");
      getCellFormatter().addStyleName(0, 1, "top");
      getCellFormatter().addStyleName(0, 2, "tr-c");

      getCellFormatter().addStyleName(1, 0, "ml");
      getCellFormatter().addStyleName(1, 1, "middle");
      getCellFormatter().addStyleName(1, 2, "mr");

      getCellFormatter().addStyleName(2, 0, "bl-c");
      getCellFormatter().addStyleName(2, 1, "bottom");
      getCellFormatter().addStyleName(2, 2, "br-c");

      getRowFormatter().addStyleName(0, "screen-top");
      getRowFormatter().addStyleName(2, "screen-bottom");
   }

}
