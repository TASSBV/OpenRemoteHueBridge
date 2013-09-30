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

import java.util.List;

import org.openremote.modeler.client.rpc.TemplateRPCService;
import org.openremote.modeler.client.utils.ScreenFromTemplate;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.Template;
import org.openremote.modeler.exception.BeehiveNotAvailableException;
import org.openremote.modeler.service.TemplateService;

/**
 * The server side implementation of the RPC service <code>TemplateRPCService</code>. 
 * @author javen
 *
 */
@SuppressWarnings("serial")
public class TemplateController extends BaseGWTSpringController implements TemplateRPCService {

   private static final String TEMPLATE_IN_EDITING = "templateInEditing";
   
   private TemplateService templateService = null;
   
   @Override
   public ScreenFromTemplate buildScreeFromTemplate(Template template) {
      return templateService.buildFromTemplate(template);
   }

   @Override
   public List<Template> getTemplates(boolean isFromPrivate) {
      return templateService.getTemplates(isFromPrivate);
   }

   @Override
   public Template saveTemplate(Template template) {
      return templateService.saveTemplate(template);
   }
   @Override
   public Boolean deleteTemplate(long templateId){
      return templateService.deleteTemplate(templateId);
   }
   public void setTemplateService(TemplateService templateService) {
      this.templateService = templateService;
   }

   @Override
   public List<Template> searchTemplates(String keywords, int page) {
      return templateService.getTemplatesByKeywordsAndPage(keywords, page);
   }

   @Override
   public ScreenPair buildScreen(Template template) {
      return templateService.buildScreen(template);
   }

   @Override
   public Template updateTemplate(Template template) throws BeehiveNotAvailableException {
      Template result = template;
      synchronized (getThreadLocalRequest().getSession()) {
         Object obj = getThreadLocalRequest().getSession().getAttribute(TEMPLATE_IN_EDITING);
         if (obj != null && obj instanceof Template) {
            Template oldTemplate = (Template) obj;
            String newContent = templateService.getTemplateContent(template.getScreen());
            String oldContent = templateService.getTemplateContent(oldTemplate.getScreen());
            oldTemplate.setContent(oldContent);
            template.setContent(newContent);
            if (!template.equals(oldTemplate)) {
               result = templateService.updateTemplate(template);
               getThreadLocalRequest().getSession().setAttribute(TEMPLATE_IN_EDITING, template);
            }
         } else {
            result = templateService.updateTemplate(template);
            getThreadLocalRequest().getSession().setAttribute(TEMPLATE_IN_EDITING, template);
         }
      }
      return result;
   }

}
