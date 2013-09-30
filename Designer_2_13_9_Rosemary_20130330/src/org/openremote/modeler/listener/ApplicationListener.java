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
package org.openremote.modeler.listener;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openremote.modeler.SpringContext;
import org.openremote.modeler.configuration.PathConfig;
import org.openremote.modeler.service.UserService;
import org.springframework.context.ApplicationEvent;


/**
 * Init application when web server is started.
 * It make sure the resource folder("modeler_tmp") be created.
 * 
 * @see ApplicationEvent
 * @author Tomsky, Dan
 */
public class ApplicationListener implements ServletContextListener {
   
   private UserService userService = (UserService) SpringContext.getInstance().getBean("userService");

   public void contextDestroyed(ServletContextEvent event) {
      ;//do nothing
   }

   public void contextInitialized(ServletContextEvent event) {
      // set web root, eg: "E:\apache-tomcat-5.5.28\webapps\modeler\".
      PathConfig.WEBROOTPATH = event.getServletContext().getRealPath("/");
      File tempFolder = new File(PathConfig.WEBROOTPATH + File.separator + PathConfig.RESOURCEFOLDER);
      if (!tempFolder.exists()) {
         tempFolder.mkdirs();
      }
      userService.initRoles();
   }

   
}
