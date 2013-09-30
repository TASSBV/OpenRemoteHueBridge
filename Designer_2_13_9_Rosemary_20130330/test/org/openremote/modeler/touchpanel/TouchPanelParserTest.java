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
package org.openremote.modeler.touchpanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.exception.ParseTouchPanelException;
import org.openremote.modeler.service.TouchPanelParser;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * The Class PanelParserTest.
 */
public class TouchPanelParserTest {

   /**
    * Test parse xmls.
    */
   @Test
   public void testParseXmlsSuccess() {
      TouchPanelParser panelParser = new TouchPanelParser();
      panelParser.setPath(getClass().getResource("fixture").getPath());
      
      Map<String, List<TouchPanelDefinition>> panels = new HashMap<String, List<TouchPanelDefinition>>();
      List<TouchPanelDefinition> iphonePanels = new ArrayList<TouchPanelDefinition>();
      TouchPanelDefinition panelDefinition = getCorrectPanelDefinition();
      iphonePanels.add(panelDefinition);
      panels.put(panelDefinition.getType(), iphonePanels);
      
      Assert.assertEquals(panels, panelParser.parseXmls());
      
   }
   
   /**
    * Gets the correct panel definition.
    * 
    * @return the correct panel definition
    */
   private TouchPanelDefinition getCorrectPanelDefinition() {
      TouchPanelDefinition panelDefinition = new TouchPanelDefinition();
      TouchPanelCanvasDefinition grid = new TouchPanelCanvasDefinition(196, 294);
      panelDefinition.setType("iphone");
      panelDefinition.setName("iphone1");
      panelDefinition.setBgImage("iphone_background.jpg");
      panelDefinition.setWidth(269);
      panelDefinition.setHeight(500);
      panelDefinition.setPaddingLeft(35);
      panelDefinition.setPaddingTop(105);
      panelDefinition.setCanvas(grid);
      return panelDefinition;
   }
   
   @Test(expectedExceptions = { ParseTouchPanelException.class })
   public void testSchemaValidateFail() {
      TouchPanelParser panelParser = new TouchPanelParser();
      panelParser.setPath(getClass().getResource("fixture").getPath() + "/fail");
      panelParser.parseXmls();
   }
   
}
