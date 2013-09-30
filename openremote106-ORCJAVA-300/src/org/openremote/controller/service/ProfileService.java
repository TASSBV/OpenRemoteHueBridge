/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.service;
/**
 * This service is used to provide some profile related method. such as: get profile by panel id, 
 * get profile by panel name, get all panels. etc...
 * @author Javen
 *
 */
public interface ProfileService {
   /**
    * This method is used to get a xml string which contains the UI information. 
    * This string contains all the information for a panel whose id is panelId.
    * attention : This method will read panel.xml by your configuration, you should put panel.xml into your ${resource.path}  
    * @param panelId the id of your panel. 
    * @return an xml string which contains all the panel information.
    */
   String getProfileByPanelID(String panelId);
   /**
    * This method is used to get a xml string which contains the UI information. 
    * This string contains all the information for a panel whose id is panelId.
    * This method can get the information from your panel.xml. 
    * @param panelID the id of your panel. 
    * @param xmlPath The path of file panel.xml. 
    * @return an xml string which contains all the panel information.
    */
   String getProfileByPanelID(String xmlPath,String panelID);
   /**
    * This method is used to get a xml string which represents an profile by the name of an panel. 
    * attention : This method will read panel.xml by your configuration, you should put panel.xml into your ${resource.path}  
    * @param panelName The name of panel. 
    * @return a xml string which contains all the information a panel has. 
    */
   String getProfileByPanelName(String panelName);
   /**
    * This method is used to get a xml string which represents an profile by the name of an panel.
    * @param xmlPath The path of file panel.xml.  
    * @param panelName The name of panel. 
    * @return a xml string which contains all the information a panel has. 
    */
   String getProfileByName(String xmlPath,String panelName);
   /**
    * This method is used to get a xml string which include all the panels in panel.xml. 
    * attention : This method will read panel.xml by your configuration, you should put panel.xml into your ${resource.path} 
    * @return A xml string that contains all the panels. 
    */
   String getAllPanels();
   /**
    * This method is used to get a xml string which include all the panels in panel.xml. 
    * @param panleXMLPath The path of file panel.xml
    * @return A xml string that contains all the panels. 
    */
   String getAllPanels(String panleXMLPath);
   /**
    * This method is used to get a xml string which include all the panels in panel.xml. 
    * @param xmlPath The path of file panel.xml. 
    * @return A xml string that contains all the panels. 
    */
   String getPanelsXML(String xmlPath);
   
}
