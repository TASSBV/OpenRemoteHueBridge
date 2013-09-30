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
import java.io.InputStream;
import java.util.List;

import org.openremote.beehive.api.dto.TemplateDTO;
import org.openremote.beehive.domain.Template;

/**
 * Service for UI Designer templates
 * 
 * @author Dan 2010-1-29
 *
 */
public interface TemplateService {
   String KEYWORDS_SEPERATOR = ",";
   String NO_KEYWORDS = "any"; 
   String TEMPLATE_RESOURCE_ZIP_FILE_NAME = "template.zip";
   int TEMPLATE_SIZE_PER_PAGE = 10;
   
   long save(Template t);
   
   List<TemplateDTO> loadAllPrivateTemplatesByAccountOid(long accountOid);
   
   TemplateDTO loadTemplateByOid(long templateOid);
   
   boolean delete(long templateOid);

   List<TemplateDTO> loadAllPublicTemplatesByAccountOid(long accountOid);
   
   File getTemplateResourceZip(long templateOid);
   
   boolean saveTemplateResourceZip(long templateOid,InputStream input);
   
   List<TemplateDTO> loadPublicTemplatesByKeywordsAndPage(String keywords,int page);
   
   TemplateDTO updateTemplate(Template t);
}
