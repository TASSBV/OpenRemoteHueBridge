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
package org.openremote.modeler.client.widget.uidesigner;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.gxtextends.TreePanelDragSourceMacroDragExt;
import org.openremote.modeler.client.widget.TreePanelBuilder;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.Element;

/**
 * A panel for display all components which can be drag into screen canvas.
 */
public class WidgetPanel extends ContentPanel {

   public WidgetPanel() {
      setBodyBorder(false);
      setHeading("Widgets");
      final TreePanel<BeanModel> widgetTree = TreePanelBuilder.buildWidgetTree();
      createDrapSource(widgetTree);
      LayoutContainer treeContainer = new LayoutContainer() {
         @Override
         protected void onRender(Element parent, int index) {
            super.onRender(parent, index);
            add(widgetTree);
         }
      };
      // overflow-auto style is for IE hack.
      treeContainer.addStyleName("overflow-auto");
      treeContainer.setStyleAttribute("backgroundColor", "white");
      add(treeContainer);
   }
   
   private void createDrapSource(TreePanel<BeanModel> widgetTree) {
      TreePanelDragSourceMacroDragExt dragSource = new TreePanelDragSourceMacroDragExt(widgetTree);
      dragSource.setGroup(Constants.CONTROL_DND_GROUP);
   }
}
