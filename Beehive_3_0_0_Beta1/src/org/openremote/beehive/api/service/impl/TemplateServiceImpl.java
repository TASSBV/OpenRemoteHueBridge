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
package org.openremote.beehive.api.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openremote.beehive.Configuration;
import org.openremote.beehive.api.dto.TemplateDTO;
import org.openremote.beehive.api.service.TemplateService;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.Template;
import org.openremote.beehive.utils.FileUtil;


public class TemplateServiceImpl extends BaseAbstractService<Template> implements TemplateService {
   private static final Log logger = LogFactory.getLog(TemplateService.class);

   protected Configuration configuration = null;

   @Override
   public List<TemplateDTO> loadAllPrivateTemplatesByAccountOid(long accountOid) {
      return loadAllTemplatesByAccountOidAndSharedType(accountOid,false);
   }

   @Override
   public List<TemplateDTO> loadAllPublicTemplatesByAccountOid(long accountOid) {
      return loadAllTemplatesByAccountOidAndSharedType(accountOid,true);
   }

   public List<TemplateDTO> loadPublicTemplatesByKeywordsAndPage(String keywords, int page) {
      List<TemplateDTO> templateDTOs = new ArrayList<TemplateDTO>();
      DetachedCriteria critera = DetachedCriteria.forClass(Template.class);
      critera.add(Restrictions.eq("shared", true));
      if (keywords != null && keywords.trim().length() > 0) {
         String[] kwords = keywords.split(KEYWORDS_SEPERATOR);
         for (String keyword : kwords) {
            critera.add(Restrictions.like("keywords", keyword, MatchMode.ANYWHERE));
         }
      }
      List<Template> templates = genericDAO.findPagedDateByDetachedCriteria(critera, TEMPLATE_SIZE_PER_PAGE,
            (TEMPLATE_SIZE_PER_PAGE) * page);
      if (templates != null && templates.size() > 0) {
         for (Template template : templates) {
            templateDTOs.add(template.toDTO());
         }
      }
      return templateDTOs;
   }

   @Override
   public TemplateDTO loadTemplateByOid(long templateOid) {
      Template template = genericDAO.getById(Template.class, templateOid);
      if (template == null) {
         return null;
      }
      return template.toDTO();
   }

   @Override
   public long save(Template t) {
      long templateOid = (Long) genericDAO.save(t);
      createTemplateFolder(templateOid);
      return templateOid;
   }

   @Override
   public boolean delete(long templateOid) {
      Template t = genericDAO.getById(Template.class, templateOid);
      if (t != null) {
         genericDAO.delete(t);
         return true;
      }
      return false;
   }

   @Override
   public File getTemplateResourceZip(long templateOid) {
      File templateFolder = createTemplateFolder(templateOid);
      File[] files = templateFolder.listFiles(new FilenameFilter() {

         @Override
         public boolean accept(File dir, String name) {
            return name.equalsIgnoreCase("template.zip");
         }

      });
      if (files != null && files.length != 0) {
         return files[0];
      }
      return null;
   }

   private List<TemplateDTO> loadAllTemplatesByAccountOidAndSharedType(long accountOid,boolean shared) {
      List<TemplateDTO> templateDTOs = new ArrayList<TemplateDTO>();
      Account account = genericDAO.getById(Account.class, accountOid);
      if (account == null) {
         return null;
      }
      for (Template template : account.getTemplates()) {
         if (template.isShared() == shared) {
            templateDTOs.add(template.toDTO());
         }
      }
      return templateDTOs;
   }
   private File createTemplateFolder(long templateOid) {
      String templateFolder = configuration.getTemplateResourcesDir() + File.separator + templateOid;

      File templateFolderFile = new File(templateFolder);
      templateFolderFile.mkdirs();
      return templateFolderFile;
   }

   public boolean saveTemplateResourceZip(long templateOid, InputStream input) {
      File templateFolder = createTemplateFolder(templateOid);
      File zipFile = new File(templateFolder, TEMPLATE_RESOURCE_ZIP_FILE_NAME);
      FileOutputStream fos = null;

      try {
         FileUtil.deleteFileOnExist(zipFile);
         fos = new FileOutputStream(zipFile);
         byte[] buffer = new byte[1024];
         int length = 0;

         while ((length = input.read(buffer)) != -1) {
            fos.write(buffer, 0, length);
         }

         logger.info("Save resource success!");

         return true;
      } catch (Exception e) {
         logger.error("Failed to save resource from modeler to beehive", e);
      } finally {
         if (fos != null) {
            try {
               fos.close();
            } catch (IOException ioException) {
               logger.warn("Error in closing the file output stream (" + TEMPLATE_RESOURCE_ZIP_FILE_NAME + "): "
                     + ioException.getMessage(), ioException);
            }
         }
      }
      return false;
   }

   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   @Override
   public TemplateDTO updateTemplate(Template t) {
      try {
         Template oldTemplate = genericDAO.loadById(Template.class, t.getOid());
         oldTemplate.setContent(t.getContent());
         oldTemplate.setName(t.getName());
         oldTemplate.setKeywords(t.getKeywords());
         oldTemplate.setShared(t.isShared());

         return oldTemplate.toDTO();
      } catch (ObjectNotFoundException e) {
         return null;
      }
   }

}
