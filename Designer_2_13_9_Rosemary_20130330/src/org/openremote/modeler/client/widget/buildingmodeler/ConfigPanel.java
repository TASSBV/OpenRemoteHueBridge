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
package org.openremote.modeler.client.widget.buildingmodeler;

import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.view.BuildingModelerView;
import org.openremote.modeler.client.widget.TreePanelBuilder;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
/**
 * Contains a tree panel for configuring the Controller info. 
 * @author javen
 *
 */
public class ConfigPanel extends ContentPanel {
   private BuildingModelerView buildingModelerView = null;
   private Icons icon = GWT.create(Icons.class);
   private TreePanel<BeanModel> configCategory;
   public ConfigPanel(BuildingModelerView buildingModelerView){
      this.buildingModelerView = buildingModelerView;
      setHeading("Config for Controller");
      setIcon(icon.configIcon());
      createCategory();
      setLayout(new FitLayout());
      show();
   }
   
   
   /**
    * Creates the config category tree.
    */
   private void createCategory(){
      configCategory = TreePanelBuilder.buildControllerConfigCategoryPanelTree(buildingModelerView.getConfigTabPanel());
      add(configCategory);
   }


   public BuildingModelerView getBuildingModelerView() {
      return buildingModelerView;
   }


   public void setBuildingModelerView(BuildingModelerView buildingModelerView) {
      this.buildingModelerView = buildingModelerView;
   }
   
}
