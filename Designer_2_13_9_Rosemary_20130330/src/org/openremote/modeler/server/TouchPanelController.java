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
package org.openremote.modeler.server;

import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.rpc.TouchPanelRPCService;
import org.openremote.modeler.service.TouchPanelParser;
import org.openremote.modeler.touchpanel.TouchPanelContainer;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;

/**
 * The Class TouchPanelController for get defined touch panels from panel xml file.
 */
public class TouchPanelController extends BaseGWTSpringController implements TouchPanelRPCService {

   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = -1064152509394362895L;

   /**
    * {@inheritDoc}
    */
   public Map<String, List<TouchPanelDefinition>> getPanels() {
      if (TouchPanelContainer.getInstance().getPanels().size() == 0) {
         TouchPanelParser parser = new TouchPanelParser();
         TouchPanelContainer.getInstance().setPanels(parser.parseXmls());
      }
      return TouchPanelContainer.getInstance().getPanels();
   }

}
