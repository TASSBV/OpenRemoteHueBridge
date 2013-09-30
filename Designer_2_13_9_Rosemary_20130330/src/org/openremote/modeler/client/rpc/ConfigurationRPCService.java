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

package org.openremote.modeler.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


/**
 * The Interface is used for getting system configuration from config.properties.
 */
@RemoteServiceRelativePath("config.smvc")
public interface ConfigurationRPCService extends RemoteService {
   
  /**
   * Gets beehive rest url.
   * 
   * @return the string
   */
  String beehiveRESTRootUrl();
  
  /**
   * Gets the rest url that to list templates.
   * 
   * @return the templates list rest url
   */
  String getTemplatesListRestUrl();
  
  /**
   * Gets the rest url that to save template.
   * 
   * @return the template save rest url
   */
  String getTemplateSaveRestUrl();
  
  /**
   * Gets the template rest url that have all public .
   * 
   * @return the all public template rest url
   */
  String getAllPublicTemplateRestUrl();

}
