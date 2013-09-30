/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.beehive.api.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
/**
 * Service for manage resources for modeler.
 * @author javen, Dan
 *
 */
public interface ResourceService {
   /**
    * save resource for an account whose oid is accouontOid
    * @param accountOid The oid for an account
    * @param input
    */
   boolean saveResource(long accountOid,InputStream input);
   
   /**
    * get openremote.zip
    * 
    * @param accountOid account id
    * @return openremote.zip
    */
   File getResourceZip(String username);
   
   
   String getAllPanelsXMLFromAccount(String username);
   
   String getPanelXMLByPanelNameFromAccount(String username, String panelName);
   
   File getResource(String username, String fileName) throws FileNotFoundException;
}
