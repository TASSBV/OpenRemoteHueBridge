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
package org.openremote.modeler.service;

import java.util.List;

import org.openremote.modeler.client.utils.ScreenFromTemplate;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.Template;

/**
 * The service for template.
 * @author javen
 *
 */
public interface TemplateService {
   
   String NO_KEYWORDS = "any"; 
   
   /**
    * Save a template to the beehive.
    * @param template The template you want to save. 
    * @return The template after being saved. 
    */
   Template saveTemplate(Template template);
   /*
   String getTemplateContent(Screen screen);*/
   /**
    * Build a screen from a template. 
    * @param template The template you want to use to build a screen. 
    * @return A wrap class consist of screen and its device set.  
    */
   ScreenFromTemplate buildFromTemplate(Template template);
   
   /**
    * Delete a template by template's oid.
    * @param templateOid
    * @return <tt>true</tt> if success ,<tt>false</tt> if not. 
    */
   boolean deleteTemplate(long templateOid);
   
   /**
    * Build a screen from template. The command information is not rebuild to DB. 
    * </br> This method is invoked by the service itself. 
    * @param template
    * @return screen build from template. 
    */
   ScreenPair buildScreen(Template template);
   
   /**
    * ReBuild Device, DeviceCommand, Sensor, Switch, Slider... for a screen. 
    * </br> This method is invoked by the service itself. 
    * @param screen 
    * @return A wrap class consist of screen and its device set.   
    */
   ScreenFromTemplate reBuildCommand(ScreenPair screen);
   
   
   Template updateTemplate(Template template);
   
   String getTemplateContent(ScreenPair screen);
   
   List<Template> getTemplates(boolean isFromPrivate);
   
   List<Template> getTemplatesByKeywordsAndPage(String keywords,int page);
}
