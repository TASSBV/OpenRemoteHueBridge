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

import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.rpc.ConfigurationRPCService;
import org.openremote.modeler.service.UserService;

/**
 * Gets the system configuration from config.properties.
 */
@SuppressWarnings("serial")
public class ConfigurationController extends BaseGWTSpringController implements ConfigurationRPCService {
   
   /** The configuration. */
   private Configuration configuration;
   
   private UserService userService;

   /**
    * Beehive rest root url.
    * 
    * @return beehive rest url.
    * 
    * @see org.openremote.modeler.client.rpc.ConfigurationRPCService#beehiveRESTRootUrl()
    */
   public String beehiveRESTRootUrl() {
      return configuration.getBeehiveRESTRootUrl();
   }
   
   public String getTemplatesListRestUrl(){
      Long accouontOid = userService.getAccount().getOid();
      return configuration.getBeehiveRESTRootUrl()+"account/"+accouontOid+"/templates";
   }
   
   public String getTemplateSaveRestUrl(){
      Long accountOid = userService.getAccount().getOid();
      return configuration.getBeehiveRESTRootUrl()+"account/"+accountOid+"/template/save";
   }
   
   public String getAllPublicTemplateRestUrl(){
      return configuration.getBeehiveRESTRootUrl()+"account/0"+"/templates/";
   }
   /**
    * Sets the configuration.
    * 
    * @param configuration the new configuration
    */
   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   public void setUserService(UserService userService) {
      this.userService = userService;
   }
   
   
}
