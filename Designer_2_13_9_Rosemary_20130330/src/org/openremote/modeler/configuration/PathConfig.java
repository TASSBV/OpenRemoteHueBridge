/*
 * OpenRemote, the Home of the Digital Home.
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

package org.openremote.modeler.configuration;

import java.io.File;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.domain.Account;

/**
 * The utility class support methods to get some local folder or file path.
 * e.g.: folder "modeler_tmp", file "panel.xml".
 * 
 * @author allen.wei, handy.wang
 */
public class PathConfig {
   
   private static final Logger LOGGER = Logger.getLogger(PathConfig.class);

   private static final PathConfig myInstance = new PathConfig();
   
   public static final String RESOURCEFOLDER = "modeler_tmp";
   
   private Configuration configuration;
   
   public static String WEBROOTPATH = "";
   
   private static String ROOTPATH = "";

   /**
    * Instantiates a new path config.
    */
   private PathConfig() {
   }

   /**
    * Gets the single instance of PathConfig.
    * 
    * @return single instance of PathConfig
    */
   public static PathConfig getInstance(Configuration configuration) {
      myInstance.configuration = configuration;
      return myInstance;
   }

   /**
    * Gets temp folder.
    * 
    * @return folder absolute path
    */
   public String tempFolder() {
      if ("".equals(WEBROOTPATH)) {
         LOGGER.fatal("Can't find modeler.root in system property, please check web.xml.");
         throw new IllegalStateException("Can't find modeler.root in system property, please check web.xml.");
      }
      if ("".equals(ROOTPATH)) {
         ROOTPATH = WEBROOTPATH + File.separator;
      }
      return  ROOTPATH + RESOURCEFOLDER + File.separator;
   }

  
//   /**
//    * Gets panel xml path.
//    *
//    * @param sessionId the session id
//    *
//    * @return file absolute path
//    */
//   @Deprecated public String panelXmlFilePath(String sessionId) {
//      return userFolder(sessionId) + "panel.xml";
//   }

  /**
   * @deprecated This should be internalized to cache implementation. No need to expose outside
   *             of cache. Will go away once ResourceServiceImpl.initResources has been factored
   *             into cache impl.
   */
   @Deprecated public String panelXmlFilePath(Account account) {
      return userFolder(account) + "panel.xml";
   }

//   /**
//    * Gets controller xml file path.
//    *
//    * @param sessionId the session id
//    *
//    * @return file absolute path
//    */
//   @Deprecated public String controllerXmlFilePath(String sessionId) {
//      return userFolder(sessionId) + "controller.xml";
//   }

  /**
   * @deprecated This should be internalized to cache implementation. No need to expose outside
   *             of cache. Will go away once ResourceServiceImpl.initResources has been factored
   *             into cache impl.
   */
   @Deprecated public String controllerXmlFilePath(Account account) {
      return userFolder(account) + "controller.xml";
   }

//   /**
//    * Gets panel description file path.
//    *
//    * @param sessionId the session id
//    *
//    * @return file absolute path
//    */
//   public String panelDescFilePath(String sessionId) {
//      return userFolder(sessionId) + "." + Constants.PANEL_DESC_FILE;
//   }
//
//   /**
//    * Lirc file path.
//    *
//    * @param sessionId the session id
//    *
//    * @return the string
//    */
//   @Deprecated public String lircFilePath(String sessionId) {
//      return userFolder(sessionId) + "lircd.conf";
//   }
//

  /**
   * @deprecated This should be internalized to cache implementation. No need to expose outside
   *             of cache. Will go away once ResourceServiceImpl.initResources has been factored
   *             into cache impl.
   */
   @Deprecated public String lircFilePath(Account account) {
      return userFolder(account) + "lircd.conf";
   }

//   /**
//    * Gets compressed file path.
//    *
//    * @param sessionId the session id
//    *
//    * @return file absolute path
//    */
//   public String openremoteZipFilePath(String sessionId) {
//      return userFolder(sessionId) + "openremote." + UUID.randomUUID() + ".zip";
//   }
   
  /**
   * @deprecated This should be internalized to cache implementation. No need to expose outside
   *             of cache. Will go away once templates have been factored
   *             into cache impl.
   */
   @Deprecated public String openremoteZipFilePath(Account account) {
//      return userFolder(account) + "openremote." + UUID.randomUUID() + ".zip";
      return userFolder(account) + "openremote.zip";
   }
   
//   /**
//    * Dot import file path.
//    *
//    * @param sessionId the session id
//    *
//    * @return the string
//    */
//   public String dotImportFilePath(String sessionId) {
//      return userFolder(sessionId) + ".import";
//   }
//

   /**
    * User folder.
    * 
    * @param sessionId the session id
    * 
    * @return the string
    */
   public String userFolder(String sessionId) {
      return tempFolder() + sessionId + File.separator;
   }
   
   public String userFolder(Account account) {
      return tempFolder()+account.getOid()+File.separator;
   }

//   /**
//    * Gets the zip url.
//    *
//    * @param sessionId the session id
//    *
//    * @return the zip url
//    */
//   public String getZipUrl(String sessionId) {
//      return configuration.getWebappServerRoot() + "/" + RESOURCEFOLDER + "/" + sessionId + "/";
//   }

  /**
   * @deprecated This should not be exposed outside cache. Will go away once the export
   *             functionality has been corrected to fetch user archive from Beehive, not from
   *             cache.
   */
   @Deprecated public String getZipUrl(Account account) {
      return configuration.getWebappServerRoot() + "/" + RESOURCEFOLDER + "/" + account.getOid() + "/";
   }

   /**
    * Gets the relative resource path.
    * 
    * @param sessionId the session id
    * @param fileName the file name
    * 
    * @return the relative resource path
    */
   public String getRelativeResourcePath(String fileName, String sessionId) {
      return "../" + RESOURCEFOLDER + "/" + sessionId + "/" + fileName;
   }
   
   public String getRelativeResourcePath(String fileName, Account account) {
      return RESOURCEFOLDER + "/" + account.getOid() + "/" + fileName;
   }
   
   /**
    * Gets the configuration.
    * 
    * @return the configuration
    */
   public Configuration getConfiguration() {
      return configuration;
   }

   /**
    * Sets the configuration.
    * 
    * @param configuration the new configuration
    */
   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }
   
//   public String getRelativeSessionFolderPath(String sessionId) {
//      return "../" + RESOURCEFOLDER + "/" + sessionId + "/";
//   }
   
   @Deprecated public String getWebRootFolder() {
      return  WEBROOTPATH;
   }
   
   @Deprecated public String getSerializedPanelsFile(Account account){
      return userFolder(account)+"panels.obj";
   }
   
//   public String getControllerConfigPath(){
//      return configuration.getControllerConfigPath();
//   }
}
