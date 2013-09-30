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

package org.openremote.modeler.client.gxtextends;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.TreePanelDragSource;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

/**
 * Inherited from {@link com.extjs.gxt.ui.client.dnd.TreePanelDragSource}.<br/>
 * 
 * Overrides {@link #onDragDrop(DNDEvent)} to do nothing on Drag Drop.
 * 
 */
public class TreePanelDragSourceMacroDragExt extends TreePanelDragSource {

   /**
    * Instantiates a new tree panel drag source macro drag ext.
    * 
    * @param tree
    *           the tree
    */
   public TreePanelDragSourceMacroDragExt(TreePanel<? extends ModelData> tree) {
      super(tree);
   }

   /*
    * (non-Javadoc)
    * 
    * @see com.extjs.gxt.ui.client.dnd.TreePanelDragSource#onDragDrop(com.extjs.gxt.ui.client.event.DNDEvent)
    */
   @Override
   protected void onDragDrop(DNDEvent event) {
      // do nothing after drag drop.
   }
}
