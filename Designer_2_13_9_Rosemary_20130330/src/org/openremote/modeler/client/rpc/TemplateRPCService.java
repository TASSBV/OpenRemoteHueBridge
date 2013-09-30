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

import java.util.List;

import org.openremote.modeler.client.utils.ScreenFromTemplate;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.Template;
import org.openremote.modeler.exception.BeehiveNotAvailableException;
import org.openremote.modeler.exception.ResourceFileLostException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The RPC service for Template.
 * @author javen
 *
 */
@RemoteServiceRelativePath("template.smvc")
public interface TemplateRPCService extends RemoteService {
   
   /**
    * Gets the templates by sharing type.
    * 
    * @param isFromPrivate the is from private
    * If true, get private templates, else get public templates.
    * 
    * @return the templates
    * 
    * @throws BeehiveNotAvailableException the beehive not available exception
    */
   List<Template> getTemplates(boolean isFromPrivate) throws BeehiveNotAvailableException;
   /**
    * save a template to the beehive.
    */
   Template saveTemplate(Template template) throws BeehiveNotAvailableException;
   /**
    * build a screen from template.
    * @param template
    * @return
    */
   ScreenFromTemplate buildScreeFromTemplate(Template template) throws BeehiveNotAvailableException, ResourceFileLostException;
   
   Template updateTemplate(Template template) throws BeehiveNotAvailableException,ResourceFileLostException;
   
   ScreenPair buildScreen(Template template);
   
   Boolean deleteTemplate(long templateId) throws BeehiveNotAvailableException;
   
   List<Template> searchTemplates(String keywords,int page);
}
