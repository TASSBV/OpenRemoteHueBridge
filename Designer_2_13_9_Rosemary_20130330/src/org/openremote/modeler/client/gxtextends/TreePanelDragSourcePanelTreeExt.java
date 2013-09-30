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

import org.openremote.modeler.client.Constants;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.dnd.TreePanelDragSource;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

/**
 * The Class defines a dragSource for the <b>ProfilePanel</b> can drag its nodes.
 */
public class TreePanelDragSourcePanelTreeExt extends TreePanelDragSource {

   private TreePanel<BeanModel> treePanel;
   public TreePanelDragSourcePanelTreeExt(TreePanel<BeanModel> treePanel) {
      super(treePanel);
      this.treePanel = treePanel;
   }

   @Override
   protected void onDragStart(DNDEvent e) {
      if (treePanel.getSelectionModel().getSelectedItems().size() > 1) {
         e.setCancelled(true);
         return;
      }
      super.onDragStart(e);
   }

   @Override
   protected void onDragDrop(DNDEvent event) {
      return;
   }

   @Override
   public void setGroup(String group) {
      super.setGroup(Constants.REORDER_PANEL_GROUP);
   }

}
