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

import java.util.Map;

import org.openremote.modeler.client.rpc.ProtocolRPCService;
import org.openremote.modeler.protocol.ProtocolContainer;
import org.openremote.modeler.protocol.ProtocolDefinition;
import org.openremote.modeler.service.ProtocolParser;

/**
 * The server side implementation of the RPC service <code>ProtocolRPCService</code>.
 */
public class ProtocolController extends BaseGWTSpringController implements ProtocolRPCService {

   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = 8057648010410493998L;

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.client.rpc.ProtocolRPCService#getProtocolContainer()
    */
   public Map<String, ProtocolDefinition> getProtocols() {
      if (ProtocolContainer.getInstance().getProtocols().size() == 0) {
         ProtocolParser parser = new ProtocolParser();
         ProtocolContainer.getInstance().setProtocols(parser.parseXmls());
      }
      return ProtocolContainer.getInstance().getProtocols();
   }

}
