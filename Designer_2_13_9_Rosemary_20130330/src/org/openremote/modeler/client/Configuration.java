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

package org.openremote.modeler.client;

/**
 * The Class defines beehiveRESTRootUrl and some local file path, which provided in file config.properties.
 */
public class Configuration {

   /** The beehive rest root url.
    * e.g.: "http://openremote.finalist.hk/beehive/rest/".
    */
   private String beehiveRESTRootUrl;
   
   private String irServiceRESTRootUrl;

   /** The relative path of panel xsd. 
    *  e.g.: "/panel-2.0-M7.xsd" 
    */
   private String panelXsdPath;

   /** The relative path of controller xsd. 
    *  e.g.: "/controller-2.0-M7.xsd"
    */
   private String controllerXsdPath;

   /** The webapp server root url. 
    *  e.g.: "http://127.0.0.1:8080/modeler" 
    */
   private String webappServerRoot;

   /** The relative path of controller config xml. 
    *  e.g.: "/controller-config-2.0-M7.xml"
    */
   private String controllerConfigPath;
   
   /** The relative path of controller config xsd. 
    *  e.g.: "/controllerConfig-2.0-M7.xsd" 
    */
   private String controllerConfigXsdPath;

   public String getPanelXsdPath() {
      return panelXsdPath;
   }

   public String getControllerXsdPath() {
      return controllerXsdPath;
   }

   public void setPanelXsdPath(String panelXsdPath) {
      this.panelXsdPath = panelXsdPath;
   }

   public void setControllerXsdPath(String controllerXsdPath) {
      this.controllerXsdPath = controllerXsdPath;
   }

   public String getWebappServerRoot() {
      return webappServerRoot;
   }

   public void setWebappServerRoot(String webappServerRoot) {
      this.webappServerRoot = webappServerRoot;
   }

   public String getBeehiveRESTRootUrl() {
      return beehiveRESTRootUrl;
   }

   public void setBeehiveRESTRootUrl(String beehiveRESTRootUrl) {
      this.beehiveRESTRootUrl = beehiveRESTRootUrl;
   }

   public String getIrServiceRESTRootUrl() {
    return irServiceRESTRootUrl;
  }

  public void setIrServiceRESTRootUrl(String irServiceRESTRootUrl) {
    this.irServiceRESTRootUrl = irServiceRESTRootUrl;
  }

  public String getBeehiveLircdConfRESTUrl() {
      return beehiveRESTRootUrl.endsWith("/")?  beehiveRESTRootUrl + "lirc.conf" : beehiveRESTRootUrl + "/lirc.conf";
   }

   public String getBeehiveRestIconUrl() {
      return beehiveRESTRootUrl.endsWith("/")?  beehiveRESTRootUrl + "icons/" : beehiveRESTRootUrl + "/icons/";
   }

   public String getControllerConfigPath() {
      return controllerConfigPath;
   }

   public void setControllerConfigPath(String controllerConfigPath) {
      this.controllerConfigPath = controllerConfigPath;
   }

   public String getControllerConfigXsdPath() {
      return controllerConfigXsdPath;
   }

   public void setControllerConfigXsdPath(String controllerConfigXsdPath) {
      this.controllerConfigXsdPath = controllerConfigXsdPath;
   }
   
}
