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

import java.util.Map;

import org.openremote.modeler.client.rpc.ProtocolRPCService;
import org.openremote.modeler.client.rpc.ProtocolRPCServiceAsync;
import org.openremote.modeler.protocol.ProtocolDefinition;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The Class Protocols. Used for get all protocol definitions from xml files.
 */
public class Protocols {
   
   /** The m_instance. */
   private static Map<String, ProtocolDefinition> instanceMap;
   
   /** The Constant protocolService. */
   private static final ProtocolRPCServiceAsync protocolService = (ProtocolRPCServiceAsync) GWT.create(ProtocolRPCService.class);
   
   /**
    * Instantiates a new protocols.
    */
   private Protocols() {
   }
   
   /**
    * Gets the single instance of Protocols.
    * 
    * @return single instance of Protocols
    */
   public static synchronized Map<String, ProtocolDefinition> getInstance() {
      if (instanceMap == null) {
         protocolService.getProtocols(new AsyncCallback<Map<String, ProtocolDefinition>>() {
            public void onFailure(Throwable caught) {
               MessageBox.info("Error", "Can't get protocols from xml file!", null);
            }
            public void onSuccess(Map<String, ProtocolDefinition> protocols) {
               instanceMap = protocols;
            }
         });
      }
      return instanceMap;
   }
}
